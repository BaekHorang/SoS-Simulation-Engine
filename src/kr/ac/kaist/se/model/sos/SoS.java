package kr.ac.kaist.se.model.sos;

import kr.ac.kaist.se.model.abst.obj._SimContainerObject_;
import kr.ac.kaist.se.model.abst.obj._SimObject_;
import kr.ac.kaist.se.model.sos.geo.SimMap;
import kr.ac.kaist.se.simdata.output.intermediate.RunResult;
import kr.ac.kaist.se.simdata.output.intermediate.UpdateResult;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Abstract class to represent a System-of-Systems (SoS)
 * (0-tier _SimContainerObject_,
 * i.e., an SoS object represents a whole simulation model)
 * <p>
 * According to the Meta-Model for Systems-of-Systems (M2SoS),
 * an SoS is constructed by developing _SimContainerObjects_,
 * which consists of organization(s), infrastructure(s), environment(s),
 * and each _SimContainerObject_ consists of member objects.
 * <p>
 * Interfaces: Simulatable
 *
 * @author ymbaek, ehcho, yjshin
 */
public abstract class SoS extends _SimContainerObject_ {


    /* Map */
    public SimMap sosMap;
    /**
     * Member containers
     */
    //List of organizations
    protected ArrayList<Organization> orgList;
    //List of infrastructures
    protected ArrayList<Infrastructure> infraList;
    //List of environments
    protected ArrayList<Environment> envList;
    /**
     * Member constituents
     */
    //List of all constituent systems
    protected ArrayList<Constituent> csList;
    /**
     * Infrastructure entities
     */
    //List of infra system entities
    protected ArrayList<SystemEntity> systemEntityList;
    //List of infra service entities
    protected ArrayList<ServiceEntity> serviceEntityList;



    /* Environment entities */
    //List of infra resource entities
    protected ArrayList<ResourceEntity> resourceEntityList;


    public SoS(String sosId, String sosName) {
        this.id = sosId;
        this.name = sosName;

        //If the default state need to be different, modify codes below.
        this.isStatic = true;
        this.isActivated = true;
        this.isAvailable = true;

        printObjInfo();

        initLists();
        initSoSModel();
    }


    /**
     * Initialization of member lists
     */
    private void initLists() {
        timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("[" + timestamp + "] (SoS(" + this.id + "):initLists) Lists are initialized.");

        orgList = new ArrayList<>();
        infraList = new ArrayList<>();
        envList = new ArrayList<>();

        csList = new ArrayList<>();

        systemEntityList = new ArrayList<>();
    }


    /**
     * Abstract method for the initialization of simulation models of an SoS.
     * (e.g., Org, Infra, Env, CSs, and so on.)
     * This method should be implemented to build a complete SoS model.
     */
    protected abstract void initSoSModel();

    /**
     * Abstract method for the initialization of a simulation map of an SoS
     */
    protected abstract void initMap();


    /**
     * A method to actually run a simulation model (SoS)
     * This method executes included models
     *
     * @return
     */
    public RunResult run() {
//        timestamp = new Timestamp(System.currentTimeMillis());
//        System.out.println("[" + timestamp + "] (" + this.getClass().getSimpleName() + "(" + id + "):run) -> ");

        //Since an SoS is not an actionable object, selectedActionList is an empty list
        //SubRunResults of orgs, envs, infras will be added into the list
        RunResult runResult = new RunResult(this, new ArrayList<>(0));

        //TODO: An option (of a SimConfiguration) to specify a running sequence

        /* Running order: env -> infra -> org*/
        for (Environment env : this.envList) {
            runResult.addSubRunResult(env.run());
        }

        for (Infrastructure infra : this.infraList) {
            runResult.addSubRunResult(infra.run());
        }

        for (Organization org : this.orgList) {
            //Only if the organization is the top-level organization
            if (org.parentOrg == null) {
                runResult.addSubRunResult(org.run());
            }
        }

        return runResult;
    }

    @Override
    public UpdateResult update(RunResult runResult, int tick) {
//        timestamp = new Timestamp(System.currentTimeMillis());
//        System.out.println("[" + timestamp + "] (" + this.getClass().getSimpleName() + "(" + id + "):update)");

        UpdateResult updateResult = new UpdateResult();

        for (RunResult subRunResult : runResult.getSubRunResults()) {
            //Organization-type subject
            if (subRunResult.getRunSubject() instanceof Organization) {
                Organization target = (Organization) subRunResult.getRunSubject();
                UpdateResult subUpdateResult = target.update(subRunResult, tick);
                updateResult.addAllLogEventToList(subUpdateResult.getLogEventList());
            }
            //TODO: Code for Infrastructure and Environment
            //Organization-type subject
            if (subRunResult.getRunSubject() instanceof Infrastructure) {
                Infrastructure target = (Infrastructure) subRunResult.getRunSubject();
                UpdateResult subUpdateResult = target.update(subRunResult, tick);
                updateResult.addAllLogEventToList(subUpdateResult.getLogEventList());
            }

            //Organization-type subject
            if (subRunResult.getRunSubject() instanceof Environment) {
                Environment target = (Environment) subRunResult.getRunSubject();
                UpdateResult subUpdateResult = target.update(subRunResult, tick);
                updateResult.addAllLogEventToList(subUpdateResult.getLogEventList());
            }
        }


        return updateResult;
    }

    /**
     * A method to print SoS model information.
     * This method prints out all objects included in this SoS.
     */
    protected void printModelInfo() {
        timestamp = new Timestamp(System.currentTimeMillis());

        int orgCount = 0;
        int infraCount = 0;
        int envCount = 0;
        int csCount = 0;

        System.out.println("[" + timestamp + "] ┌─ Simulation Model ───────────────────────────────────────────────┐");
        System.out.println("[" + timestamp + "]  - SoS(" + this.id + ")");
        System.out.print("[" + timestamp + "]    > [numOfOrgs: " + orgList.size() + "] ");
        for (Organization org : orgList) {
            System.out.print(org.getId() + ", ");
        }
        System.out.println();
        System.out.print("[" + timestamp + "]    > [numOfCSs: " + csList.size() + "] ");
        for (Constituent cs : csList) {
            System.out.print(cs.getId() + ", ");
        }
        System.out.println();
        System.out.println("[" + timestamp + "]    > [numOfInfras: " + infraList.size() + "] ");
        System.out.println("[" + timestamp + "]    > [numOfEnvs: " + envList.size() + "] ");
        System.out.println("[" + timestamp + "]  ===================================================================");
        System.out.println("[" + timestamp + "]  - " + orgList.size() + " Organization(s)");

        for (Organization org : orgList) {
            if (org.parentOrg == null) printOrgsRecursively(org, 0);
        }

        System.out.println("[" + timestamp + "]  ===================================================================");
        System.out.println("[" + timestamp + "]  - " + csList.size() + " Constituent(s)");
        for (Constituent cs : csList) {
            System.out.print("[" + timestamp + "]    > [" + csCount + ":" + cs.getId());
            if (cs.getMyOrg() != null) {
                System.out.print(" | belongs to:" + cs.getMyOrg().getId());
            } else {
                System.out.print(" | UNAFFILIATED");
            }
            System.out.println(
                    " | aff:" + cs.getMySoS().getId() +
                            " | (" + cs.isStatic() + "," + cs.isActivated() + "," + cs.isAvailable() + ")" +
                            "] ");
            csCount++;
        }
        System.out.println("[" + timestamp + "]  ===================================================================");
        System.out.println("[" + timestamp + "]  - " + infraList.size() + " Infrastructure(s) ");
        for (Infrastructure infra : infraList) {
            System.out.println("[" + timestamp + "]    > [" + infraCount + ":" + infra.getId() + "] ");
            infraCount++;
        }
        System.out.println("[" + timestamp + "]  ===================================================================");
        System.out.println("[" + timestamp + "]  - " + envList.size() + " Environment(s) ");
        for (Environment env : envList) {
            System.out.println("[" + timestamp + "]    > [" + envCount + ":" + env.getId() + "] ");
            envCount++;
        }
//        System.out.println("[" + timestamp + "]  - " + .size() + " Map ");
        System.out.println("[" + timestamp + "] └───────────────────────────────────────────────────────────────────┘");
    }


    /**
     * A method to print member organizations recursively.
     *
     * @param org   An organization to be printed
     * @param depth Depths of organizations (for recursive execution)
     */
    private void printOrgsRecursively(Organization org, int depth) {

        //System.out.println("parent: " + org.getParentOrg());

        System.out.print("[" + timestamp + "]    ");
        for (int i = 0; i < depth; i++) {
            System.out.print("  ");
        }
        System.out.print("------------------------------------\n");

        System.out.print("[" + timestamp + "]    ");
        for (int i = 0; i < depth; i++) {
            System.out.print("  ");
        }

        org.setOrgDepth(depth);

        System.out.print("> [" + org.getId() +
                " | depth(" + org.getOrgDepth() + ") " +
                " | suborg size:" + org.subOrgList.size() +
                " | all-member size:" + org.getAllMemberCSList().size() +
                " | direct-member size:" + org.getDirectCSList().size() + "] \n");


        /* All Member CSs */
        System.out.print("[" + timestamp + "]    ");
        for (int i = 0; i < depth; i++) {
            System.out.print("  ");
        }
        System.out.print("└> All Members: ");
        for (Constituent memberCS : org.allMemberCSList) {
            System.out.print("[" + memberCS.getId() + "] ");
        }
        System.out.print("\n");

        /* Direct CSs */
        System.out.print("[" + timestamp + "]    ");
        for (int i = 0; i < depth; i++) {
            System.out.print("  ");
        }
        System.out.print("└> Direct Members: ");
        for (Constituent memberCS : org.directCSList) {
            System.out.print("[" + memberCS.getId() + "] ");
        }
        System.out.print("\n");


        if (org.subOrgList.size() != 0) {
            for (Organization aOrg : org.subOrgList) {
                printOrgsRecursively(aOrg, depth + 1);
            }
        }


    }


    /**
     * Method to obtain an object of a member of this SimModel (SoS)
     *
     * @param objId unique id of object
     * @return object that has the given id
     */
    public _SimObject_ getMemberSimObjectById(String objId) {

        //SoS
        if (this.id.equals(objId)) {
            return this;
        }

        for (Organization org : orgList) {
            if (org.getId().equals(objId)) {
                return org;
            }
        }
        for (Infrastructure infra : infraList) {
            if (infra.getId().equals(objId)) {
                return infra;
            }
        }
        for (Environment env : envList) {
            if (env.getId().equals(objId)) {
                return env;
            }
        }
        for (Constituent cs : csList) {
            if (cs.getId().equals(objId)) {
                return cs;
            }
        }
        for (SystemEntity systemEntity : systemEntityList) {
            if (systemEntity.getId().equals(objId)) {
                return systemEntity;
            }
        }

        //If any object is not found, null is returned.
        return null;
    }


    /**
     * A method to get all SimObjects of this SoS
     *
     * @return All SimObjects of this SoS
     */
    public ArrayList<_SimObject_> getAllSimObjects() {
        ArrayList<_SimObject_> allSimObjects = new ArrayList<>();

        for (Organization aOrg : this.orgList) {
            allSimObjects.add(aOrg);
            for (Constituent aCS : aOrg.getDirectCSList()) {
                allSimObjects.add(aCS);
            }
        }

        for (Infrastructure aInfra : this.infraList) {
            allSimObjects.add(aInfra);
        }

        for (Environment aEnv : this.envList) {
            allSimObjects.add(aEnv);
        }


        return allSimObjects;
    }


    /**
     * Check duplicate Ids of _SimObjects_ of an SoS
     *
     * @param objId an object's id to be checked
     * @return true if the given id is duplicate (i.e., it must be false if you want to add an object into a list)
     */
    public boolean isDuplicateId(String objId) {
        //There is no duplicate id
        return getMemberSimObjectById(objId) != null;    //There is a duplicate id
    }


    /*
    Add/Remove objects into the lists of an SoS
    (Org, Infra, Env, CS, etc.)
     */

    public void addOrg(Organization aOrg) {
        timestamp = new Timestamp(System.currentTimeMillis());

        if (!isDuplicateId(aOrg.getId())) {
            System.out.println("[" + timestamp + "] (SoS(" + this.id + "):addOrg) An Organization object is successfully added (id: " + aOrg.getId() + ").");

            orgList.add(aOrg);

            //Add member CSs into csList of an SoS automatically
            for (Constituent cs : aOrg.getDirectCSList()) {
                addCS(cs);
            }

            //Add suborgniazation of aOrg into orgList of this SoS
            if (aOrg.getSubOrgList().size() != 0) {
                for (Organization subOrg : aOrg.getSubOrgList()) {
                    addOrg(subOrg);
                }
            }
        } else {
            System.err.println("[" + timestamp + "] ADD_ORG_TO_SOS_FAILED: (SoS(" + this.id + "):addOrg) An Organization object is failed to be added.");
            System.err.println("[" + timestamp + "] ADD_ORG_TO_SOS_FAILED: (SoS(" + this.id + "):addOrg) aOrg(id): " + aOrg + "(" + aOrg.getId() + "), isDuplicateId: " + isDuplicateId(aOrg.getId()));
        }
    }


    public void removeOrg(Organization aOrg) {
        timestamp = new Timestamp(System.currentTimeMillis());
        if (orgList.remove(aOrg) == true) {
            System.out.println("[" + timestamp + "] (SoS(" + this.id + "):removeOrg) An Organization object is successfully removed (id: " + aOrg.getId() + ").");
        } else {
            System.err.println("[" + timestamp + "] REMOVE_ORG_FROM_SOS_FAILED: (SoS(" + this.id + "):removeOrg) An Organization object is failed to be removed.");

        }
    }

    public void addInfra(Infrastructure aInfra) {
        timestamp = new Timestamp(System.currentTimeMillis());
        if (!isDuplicateId(aInfra.getId()) && infraList.add(aInfra) == true) {
            System.out.println("[" + timestamp + "] (SoS(" + this.id + "):addInfra) An Infrastructure object is successfully added (id: " + aInfra.getId() + ").");
        } else {
            System.err.println("[" + timestamp + "] ADD_INFRA_TO_SOS_FAILED: (SoS(" + this.id + "):addInfra) An Infrastructure object is failed to be added.");
            System.err.println("[" + timestamp + "] ADD_INFRA_TO_SOS_FAILED: (SoS(" + this.id + "):addInfra) aInfra(id): " + aInfra + "(" + aInfra.getId() + "), isDuplicateId: " + isDuplicateId(aInfra.getId()));
        }
    }

    public void removeInfra(Infrastructure aInfra) {
        timestamp = new Timestamp(System.currentTimeMillis());
        if (infraList.remove(aInfra) == true) {
            System.out.println("[" + timestamp + "] (SoS(" + this.id + "):removeInfra) An Infrastructure object is successfully removed (id: " + aInfra.getId() + ").");
        } else {
            System.err.println("[" + timestamp + "] REMOVE_INFRA_FROM_SOS_FAILED: (SoS(" + this.id + "):removeInfra) An Infrastructure object is failed to be removed.");

        }
    }

    public void addEnv(Environment aEnv) {
        timestamp = new Timestamp(System.currentTimeMillis());
        if (!isDuplicateId(aEnv.getId()) && envList.add(aEnv) == true) {
            System.out.println("[" + timestamp + "] (SoS(" + this.id + "):addEnv) An Environment object is successfully added (id: " + aEnv.getId() + ").");
        } else {
            System.err.println("[" + timestamp + "] ADD_ENV_TO_SOS_FAILED: (SoS(" + this.id + "):addEnv) An Environment object is failed to be added.");
            System.err.println("[" + timestamp + "] ADD_ENV_TO_SOS_FAILED: (SoS(" + this.id + "):addEnv) aEnv(id): " + aEnv + "(" + aEnv.getId() + "), isDuplicateId: " + isDuplicateId(aEnv.getId()));
        }
    }

    public void removeEnv(Environment aEnv) {
        timestamp = new Timestamp(System.currentTimeMillis());
        if (envList.remove(aEnv) == true) {
            System.out.println("[" + timestamp + "] (SoS(" + this.id + "):removeEnv) An Environment object is successfully removed (id: " + aEnv.getId() + ").");
        } else {
            System.err.println("[" + timestamp + "] REMOVE_ENV_FROM_SOS_FAILED: (SoS(" + this.id + "):removeEnv) An Environment object is failed to be removed.");

        }
    }

    public void addCS(Constituent aCS) {
        timestamp = new Timestamp(System.currentTimeMillis());
        if (!isDuplicateId(aCS.getId())) {
            csList.add(aCS);
            aCS.setMySoS(this);

            System.out.println("[" + timestamp + "] (SoS(" + this.id + "):addCS) An Constituent object is successfully added (id: " + aCS.getId() + ").");
        } else {
            System.err.println("[" + timestamp + "] ADD_CS_TO_SOS_FAILED: (SoS(" + this.id + "):addCS) An Constituent object is failed to be added.");
            System.err.println("[" + timestamp + "] ADD_CS_TO_SOS_FAILED: (SoS(" + this.id + "):addCS) aCS(id): " + aCS + "(" + aCS.getId() + "), isDuplicateId: " + isDuplicateId(aCS.getId()));
        }
    }

    public void removeCS(Constituent aCS) {
        timestamp = new Timestamp(System.currentTimeMillis());
        if (csList.remove(aCS) == true) {
            System.out.println("[" + timestamp + "] (SoS(" + this.id + "):removeCS) An Constituent object is successfully removed (id: " + aCS.getId() + ").");
        } else {
            System.err.println("[" + timestamp + "] REMOVE_CS_FROM_SOS_FAILED: (SoS(" + this.id + "):removeCS) An Constituent object is failed to be removed.");
        }
    }

    public void addSystemEntity(SystemEntity aSystemEntity) {
        timestamp = new Timestamp(System.currentTimeMillis());
        if (!isDuplicateId(aSystemEntity.getId()) && systemEntityList.add(aSystemEntity) == true) {
            System.out.println("[" + timestamp + "] (SoS(" + this.id + "):addCS) An SystemEntity object is successfully added (id: " + aSystemEntity.getId() + ").");
        } else {
            System.err.println("[" + timestamp + "] ADD_SYS_TO_SOS_FAILED: (SoS(" + this.id + "):addCS) An SystemEntity object is failed to be added.");
            System.err.println("[" + timestamp + "] ADD_SYS_TO_SOS_FAILED: (SoS(" + this.id + "):addCS) aSystemEntity(id): " + aSystemEntity + "(" + aSystemEntity.getId() + "), isDuplicateId: " + isDuplicateId(aSystemEntity.getId()));
        }
    }

    public void removeSystemEntity(SystemEntity aSystemEntity) {
        timestamp = new Timestamp(System.currentTimeMillis());
        if (systemEntityList.remove(aSystemEntity) == true) {
            System.out.println("[" + timestamp + "] (SoS(" + this.id + "):removeCS) An SystemEntity object is successfully removed (id: " + aSystemEntity.getId() + ").");
        } else {
            System.err.println("[" + timestamp + "] REMOVE_SYS_FROM_SOS_FAILED: (SoS(" + this.id + "):removeCS) An SystemEntity object is failed to be removed.");
        }
    }


    /* Getters & Setters */

    public ArrayList<Organization> getOrgList() {
        return orgList;
    }

    public void setOrgList(ArrayList<Organization> orgList) {
        this.orgList = orgList;
    }

    public ArrayList<Infrastructure> getInfraList() {
        return infraList;
    }

    public void setInfraList(ArrayList<Infrastructure> infraList) {
        this.infraList = infraList;
    }

    public ArrayList<Environment> getEnvList() {
        return envList;
    }

    public void setEnvList(ArrayList<Environment> envList) {
        this.envList = envList;
    }

    public ArrayList<Constituent> getCsList() {
        return csList;
    }

    public void setCsList(ArrayList<Constituent> csList) {
        this.csList = csList;
    }

    public ArrayList<SystemEntity> getSystemEntityList() {
        return systemEntityList;
    }

    public void setSystemEntityList(ArrayList<SystemEntity> systemEntityList) {
        this.systemEntityList = systemEntityList;
    }

    public SimMap getSosMap() {
        return sosMap;
    }

    public void setSosMap(SimMap sosMap) {
        this.sosMap = sosMap;
    }
}

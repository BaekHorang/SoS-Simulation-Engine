package kr.ac.kaist.se.model.sos;

import kr.ac.kaist.se.model.abst.obj._SimContainerObject_;

import java.util.ArrayList;

/**
 * Abstract class to represent an Infrastructure (Infra)
 * (1-tier _SimContainerObject_)
 * <p>
 * According to the Meta-Model for Systems-of-Systems (M2SoS),
 * an infrastructure consists of one or more SystemEntities, ServiceEntities, and ResourceEntities.
 * Main purpose of building an infrastructure is to support goal achievement.
 * <p>
 * Interfaces: Simulatable
 * (An infra is not movable)
 *
 * @author ymbaek, ehcho, yjshin
 */
public abstract class Infrastructure extends _SimContainerObject_ {

    //SoS that this infrastructure belongs to
    protected SoS mySoS;

    //Type of an infrastructure
    protected EnumInfraType infraType;

    /**
     * Member lists
     */
    //Member system entities
    protected ArrayList<SystemEntity> systemEntityList;
    //Member service entities
    protected ArrayList<ServiceEntity> serviceEntityList;
    //Member resource entities
    protected ArrayList<ResourceEntity> resourceEntityList;

    public Infrastructure(SoS simModel, String infraId, String infraName) {
        this.mySoS = simModel;

        this.id = infraId;
        this.name = infraName;

        //If the default state need to be different, modify codes below.
        this.isStatic = true;
        this.isActivated = true;
        this.isAvailable = true;

        printObjInfo();
    }

    public Infrastructure(SoS simModel, String infraId, String infraName, boolean isStatic, boolean isActivated, boolean isAvailable) {
        this.mySoS = simModel;

        this.id = infraId;
        this.name = infraName;

        //If the default state need to be different, modify codes below.
        this.isStatic = isStatic;
        this.isActivated = isActivated;
        this.isAvailable = isAvailable;

        printObjInfo();
    }
}

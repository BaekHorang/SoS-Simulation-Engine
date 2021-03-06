package kr.ac.kaist.se.model.sos;

import kr.ac.kaist.se.model.abst.cap._SimAction_;
import kr.ac.kaist.se.model.abst.comm._SimMessage_;
import kr.ac.kaist.se.model.abst.obj._SimActionableObject_;
import kr.ac.kaist.se.model.intf.Communicatable;
import kr.ac.kaist.se.model.intf.Movable;
import kr.ac.kaist.se.model.intf.Stateful;
import kr.ac.kaist.se.model.sos.cap.CommAction;
import kr.ac.kaist.se.model.sos.cap.FuncAction;
import kr.ac.kaist.se.model.sos.cap.MoveAction;
import kr.ac.kaist.se.simdata.output.intermediate.RunResult;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Abstract class to represent a system (or system-based entity).
 * <p>
 * According to the M2SoS, a SystemEntity is a base class to represent an infrastructure system.
 * A SystemEntity can perform its own actions (i.e., Actionable),
 * and it can do communication actions (i.e., Communicatable).
 * <p>
 * Interfaces: Simulatable, Actionable, Stateful, Movable, Communicatable
 *
 * @author ymbaek, ehcho, yjshin
 */
public abstract class SystemEntity extends _SimActionableObject_
        implements Stateful, Communicatable, Movable {

    //SoS that this object belongs to
    protected SoS mySoS;
    //Infrastructure that this object belongs to
    protected Infrastructure myInfra;

    public SystemEntity(SoS simModel, Infrastructure myInfra, String systemId, String systemName) {
        this.mySoS = simModel;
        this.myInfra = myInfra;

        this.id = systemId;
        this.name = systemName;

        //If the default state need to be different, modify codes below.
        this.isStatic = true;
        this.isActivated = true;
        this.isAvailable = true;

        //A system entity is a stateful object
        this.isStateful = true;

        msgQueue = new LinkedList<_SimMessage_>();

        printObjInfo();
    }

    public SystemEntity(SoS simModel, Infrastructure myInfra, String systemId, String systemName, boolean isStatic, boolean isActivated, boolean isAvailable) {
        this.mySoS = simModel;
        this.myInfra = myInfra;

        this.id = systemId;
        this.name = systemName;

        //If the default state need to be different, modify codes below.
        this.isStatic = isStatic;
        this.isActivated = isActivated;
        this.isAvailable = isAvailable;

        //A system entity is a stateful object
        this.isStateful = true;

        msgQueue = new LinkedList<_SimMessage_>();

        printObjInfo();
    }

    @Override
    public RunResult run() {
        //TODO: Duplicate code (<-> Constituent) (it can differ depending on its implementation)
        timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("[" + timestamp + "] (" + this.getClass().getSimpleName() + "(" + id + " of " + myInfra.getId() + "):run) size of capableActions:" +
                capableActionList.size() + " = " + capableActionList);

        // [Communicatable] Before selecting actions, read a message from its message queue
        readIncomingMsgs();

        //Clear existing selectedActionList
        clearSelectedActionList();

        //Select actions to execute
        selectActions();

        RunResult runResult = new RunResult(this, this.selectedActionList);
//        System.out.println("[" + timestamp + "]  ----------------------------");

        return runResult;
    }


    @Override
    protected void selectActions() {

        //TODO: Duplicate code (<-> Constituent) (it can differ depending on its implementation)
        ArrayList<_SimAction_> possibleMoveActions = new ArrayList<>();

        for (_SimAction_ aAction : capableActionList) {
            //If aAction is not a MoveAction
            if (aAction instanceof FuncAction) {
                if (aAction.checkPrecondition()) {
                    selectedActionList.add(aAction);
                }
            }
            //If aAction is MoveAction
            else if (aAction instanceof MoveAction) {
                if (aAction.checkPrecondition()) {
                    possibleMoveActions.add(aAction);

                }
            } else if (aAction instanceof CommAction) {
                //Dynamically set a message based on makeMsgForCommAction() method.
                //The makeMsgForCommAction() method should be implemented in a concrete Constituent class.
                ((CommAction) aAction).setMessage(makeMsgForCommAction((CommAction) aAction));

                //TODO: Selection mechanism for CommAction (now: select all communication actions)
                if (aAction.checkPrecondition()) {
                    selectedActionList.add(aAction);
                }
            }
        }

        /* Selection of move action (current: random) */
        if (selectMoveActions(possibleMoveActions) != null) {
            selectedActionList.addAll(selectMoveActions(possibleMoveActions));
        }

        timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("[" + timestamp + "] (SystemEntity:" + this.getClass().getSimpleName() + ") capableActionList(" +
                capableActionList.size() + "), selectedActionList(" + selectedActionList.size() + ") = " + selectedActionList);

    }

    /* Getters & Setters */


    public SoS getMySoS() {
        return mySoS;
    }

    public void setMySoS(SoS mySoS) {
        this.mySoS = mySoS;
    }

    public Infrastructure getMyInfra() {
        return myInfra;
    }

    public void setMyInfra(Infrastructure myInfra) {
        this.myInfra = myInfra;
    }
}

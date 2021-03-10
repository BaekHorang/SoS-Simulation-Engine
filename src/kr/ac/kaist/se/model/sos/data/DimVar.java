package kr.ac.kaist.se.model.sos.data;

import kr.ac.kaist.se.model.abst.data._SimDataVariable_;

import java.sql.Timestamp;

/**
 * Abstract class to define a dimension of a geolocation
 * <p>
 * To enable a MoveAction,
 * increaseValueOfDim(..) and decreaseValueOfDim(..) should be implemented
 * to specify how an object can move according to the definition
 *
 * @author ymbaek
 */
public abstract class DimVar extends _SimDataVariable_ {

    //Domain of a dimension variable
    protected DimVarDomain varDomain;

//    public DimVar() {
//        super();
//    }

    public DimVar(String dataId, String dataName, String dataType, String dataDefaultValue) {
        super(dataId, dataName, dataType, dataDefaultValue);
        printDimensionVarCreation();
    }

//    public abstract void increaseValueOfDim(int diff);
//    public abstract void decreaseValueOfDim(int diff);

    public DimVar(String dataId, String dataName, String dataType, String dataDefaultValue, DimVarDomain dataDomain) {
        super(dataId, dataName, dataType, dataDefaultValue);

        this.varDomain = dataDomain;
        this.isDomainConstrained = true;

        printDimensionVarCreation();
    }

    public DimVar(String dataId, String dataName, String dataType, String dataDefaultValue, String dataCurValue, DimVarDomain dataDomain) {
        super(dataId, dataName, dataType, dataDefaultValue, dataCurValue);

        this.varDomain = dataDomain;
        this.isDomainConstrained = true;

        printDimensionVarCreation();
    }

//    public DimVar(String dataId, String dataName, String dataType, String dataDefaultValue, String dataCurValue, DimVarDomain dataDomain) {
//        super(dataId, dataName, dataType, dataDefaultValue, dataCurValue);
//
//        this.varDomain = dataDomain;
//        this.isDomainConstrained = true;
//
//        printDimensionVarCreation();
//    }

    public abstract boolean checkUpdateValid(int diff);

    public abstract boolean updateValueOfDim(int diff);

    private void printDimensionVarCreation() {
        timestamp = new Timestamp(System.currentTimeMillis());

        if (varDomain != null) {
            System.out.println("[" + timestamp + "] (DimVar) A DimVar is initialized: " +
                    varId + " | " +
                    varName + " | " +
                    varType + " | varDomain(" +
                    varDomain.getDomainType() + ", " +
                    varDomain.getDomainMinVal() + ", " +
                    varDomain.getDomainMaxVal() + ", " +
                    varDomain.getDomainEnumVal() + ")");
        } else {
            System.out.println("[" + timestamp + "] (DimVar) A DimVar is initialized: " +
                    varId + " | " +
                    varName + " | " +
                    varType + ")");
        }
    }

//    public


    public DimVarDomain getVarDomain() {
        return varDomain;
    }

    public void setVarDomain(DimVarDomain varDomain) {
        this.varDomain = varDomain;
    }
}
package pers.ccy.filetransportserver.server;

public class FileTransferProtocol {
    private String transHeader = "161830218ccy";
    private Integer transType;
    private Object transObj;

    public Integer getTransType() {
        return transType;
    }

    public void setTransType(Integer transType) {
        this.transType = transType;
    }

    public Object getTransObj() {
        return transObj;
    }

    public void setTransObj(Object transObj) {
        this.transObj = transObj;
    }

    public String getTransHeader() {
        return transHeader;
    }

    public void setTransHeader(String transHeader) {
        this.transHeader = transHeader;
    }
}

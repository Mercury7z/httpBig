package util;

public class StringWrapper {
    private String msg;

    private StringWrapper(String msg) {
        this.msg = msg;
    }

    public static StringWrapper getWrapper(String msg) {
        return new StringWrapper(msg);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
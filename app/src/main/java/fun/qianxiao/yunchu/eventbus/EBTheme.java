package fun.qianxiao.yunchu.eventbus;

public class EBTheme {

    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public EBTheme(boolean success) {
        super();
        this.success = success;
    }
}

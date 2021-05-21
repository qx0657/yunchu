package fun.qianxiao.yunchu.eventbus;

public class EBRequestLogin {
    private boolean success;

    public EBRequestLogin(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}

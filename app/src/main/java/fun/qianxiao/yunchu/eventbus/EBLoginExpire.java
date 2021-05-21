package fun.qianxiao.yunchu.eventbus;

public class EBLoginExpire {

    private boolean success;

    public EBLoginExpire(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}

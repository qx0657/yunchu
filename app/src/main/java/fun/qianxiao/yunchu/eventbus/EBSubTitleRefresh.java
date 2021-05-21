package fun.qianxiao.yunchu.eventbus;

public class EBSubTitleRefresh {
    private boolean success;
    private String text;

    public EBSubTitleRefresh(boolean success, String text) {
        this.success = success;
        this.text = text;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getText() {
        return text;
    }
}

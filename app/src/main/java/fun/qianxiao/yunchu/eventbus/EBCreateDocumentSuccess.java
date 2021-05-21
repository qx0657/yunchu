package fun.qianxiao.yunchu.eventbus;

import org.w3c.dom.Document;

import fun.qianxiao.yunchu.bean.DocumentBean;

public class EBCreateDocumentSuccess {
    private boolean success;
    private DocumentBean documentBean;

    public EBCreateDocumentSuccess(boolean success, DocumentBean documentBean) {
        this.success = success;
        this.documentBean = documentBean;
    }

    public boolean isSuccess() {
        return success;
    }

    public DocumentBean getDocumentBean() {
        return documentBean;
    }
}

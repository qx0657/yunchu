package fun.qianxiao.yunchu.ui.userinfo.utils;


import fun.qianxiao.yunchu.base.BaseAlertDialog;

/**
 * Create by QianXiao
 * On 2020/6/22
 */
public interface OnVerifyResult {
    void verifySuccess(BaseAlertDialog dialog);
    void verifyFail(BaseAlertDialog dialog,String e);
    void verifyCancle(BaseAlertDialog dialog);
}

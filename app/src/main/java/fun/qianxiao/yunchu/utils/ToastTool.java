package fun.qianxiao.yunchu.utils;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

import es.dmoral.toasty.Toasty;

public class ToastTool {
    public static void show(String s){
        //ToastUtils.showShort(s);
        Toasty.normal(Utils.getApp(), s).show();
    }
    public static void success(String s){
        //ToastUtils.showShort(s);
        Toasty.success(Utils.getApp(), s).show();
    }
    public static void error(String s){
        //ToastUtils.showShort(s);
        Toasty.error(Utils.getApp(), s).show();
    }
    public static void info(String s){
        //ToastUtils.showShort(s);
        Toasty.info(Utils.getApp(), s).show();
    }
    public static void warning(String s){
        //ToastUtils.showShort(s);
        Toasty.warning(Utils.getApp(), s).show();
    }
}

package fun.qianxiao.yunchu;

import android.app.Activity;
import android.app.Application;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.blankj.utilcode.util.LogUtils;
import com.hjq.http.EasyConfig;
import com.hjq.http.config.IRequestHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import java.lang.reflect.Type;

import es.dmoral.toasty.Toasty;
import fun.qianxiao.yunchu.bean.User;
import fun.qianxiao.yunchu.config.AppConfig;
import fun.qianxiao.yunchu.lunzige.manager.ActivityManager;
import fun.qianxiao.yunchu.net.BasicParamsInterceptor;
import fun.qianxiao.yunchu.utils.ToastTool;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class MyApplication extends Application {
    private static MyApplication mInstance;
    public static User user;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        //开启Log日志
        LogUtils.getConfig().setLogSwitch(BuildConfig.DEBUG);
        LogUtils.getConfig().setGlobalTag(AppConfig.APP_TAG);

        //友盟统计初始化
        UMConfigure.setLogEnabled(true);
        UMConfigure.init(this, AppConfig.UMENG_APP_ID, AppConfig.UMENG_CHANNEL, UMConfigure.DEVICE_TYPE_PHONE, "qianxiao");
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        EasyConfig.with(new OkHttpClient.Builder()
                    .addInterceptor(new BasicParamsInterceptor.Builder()
                            .addHeaderLine("App: true")  // 示例： 添加公共消息头
                            .addHeaderLine("User-Agent: okhttp/4.9.0 YC/APP-QX")  // 示例： 添加公共消息头
                            .build())
                    .build())
                // 是否打印日志
                .setLogEnabled(BuildConfig.DEBUG)
                // 设置服务器配置
                .setServer(() -> AppConfig.APP_HOST)
                // 设置请求处理策略
                .setHandler(new IRequestHandler() {
                    @Override
                    public Object requestSucceed(LifecycleOwner lifecycle, Response response, Type type) throws Exception {
                        return response.body().string();
                    }

                    @Override
                    public Exception requestFail(LifecycleOwner lifecycle, Exception e) {
                        return e;
                    }
                })
                // 设置请求重试次数
                .setRetryCount(3)
                .into();

        //初始化Toasty弹窗
        Toasty.Config.getInstance()
                .tintIcon(true) // optional (apply textColor also to the icon)
                //.setToastTypeface(@NonNull Typeface typeface) // optional
                .setTextSize(16) // optional
                .allowQueue(false) // optional (prevents several Toastys from queuing)
                .apply(); // required
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

}

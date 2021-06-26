package fun.qianxiao.yunchu.net;

import com.blankj.utilcode.util.AppUtils;

import java.util.concurrent.TimeUnit;

import fun.qianxiao.yunchu.config.AppConfig;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceManager {
    private static final int DEFAULT_CONNECT_TIME = 5;
    private static final int DEFAULT_WRITE_TIME = 30;
    private static final int DEFAULT_READ_TIME = 30;
    private final Retrofit retrofit;
    private final OkHttpClient okHttpClient;

    public ApiServiceManager() {
        // 初始化OkHttpClient对象，并配置相关的属性
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIME, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIME, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIME, TimeUnit.SECONDS)
                .addInterceptor(new BasicParamsInterceptor.Builder()
                        .addHeaderLine("App: true")  // 示例： 添加公共消息头
                        .addHeaderLine("User-Agent: okhttp/4.9.0 YC/APP-QX")  // 示例： 添加公共消息头
                        .build())
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.APP_HOST)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create()) // 支持Gson自动解析JSON
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())  // 支持RxJava
                .build();
    }

    private static class SingletonHolder{
        private static final ApiServiceManager INSTANCE = new ApiServiceManager();
    }

    public static ApiServiceManager getInstance() {
        // 返回一个单例对象
        return SingletonHolder.INSTANCE;
    }

    public <T> T create(Class<T> service) {
        // 返回Retrofit创建的接口代理类
        return retrofit.create(service);
    }
}

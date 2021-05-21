package fun.qianxiao.yunchu.net;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkBaseInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response originalResponse = chain.proceed(request);
        String serverCache = originalResponse.header("Cache-Control");
        String cacheControl = request.cacheControl().toString();
        Response res = originalResponse.newBuilder()
                .addHeader("Cache-Control", cacheControl)
                .removeHeader("Pragma")
                .build();
        return res;
    }
}

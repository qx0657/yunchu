package fun.qianxiao.yunchu.checkupdate;

import fun.qianxiao.yunchu.config.AppConfig;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;

public interface CheckUpdateApi {

    @GET(AppConfig.CHECKUPDATE_URL)
    Observable<ResponseBody> getUpdateConfig();
}

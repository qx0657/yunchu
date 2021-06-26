package fun.qianxiao.yunchu.net;


import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ScanCodeApi {
    @POST
    Call<ResponseBody> first(@Url String url);

    @FormUrlEncoded
    @POST("api/v1/user/qrcode/qrstate.php")
    Observable<ResponseBody> qrState(
            @Query("username") String username,
            @Query("token") String token,
            @Field("data") String state //state
    );

    @FormUrlEncoded
    @POST("api/v1/user/qrcode/qrdata.php")
    Observable<ResponseBody> qrData(
            @Query("username") String username,
            @Query("token") String token,
            @Field("data") String data //data
    );
}

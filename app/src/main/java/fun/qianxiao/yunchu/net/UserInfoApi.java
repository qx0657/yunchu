package fun.qianxiao.yunchu.net;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserInfoApi {
    /**
     * 获取用户个人信息
     * @param username
     * @param token
     * @return
     */
    @POST("api/v1/user/userlist/")
    Observable<ResponseBody> getUserInfo(
            @Query("username") String username,
            @Query("token") String token
    );

    /**
     * 重置用户AppKey
     * @param username
     * @param token
     * @param key
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/user/updatekey/")
    Observable<ResponseBody> updateUserKey(
            @Query("username") String username,
            @Query("token") String token,
            @Field("key") String key //true
    );

    /**
     * 修改密码
     * @param username
     * @param token
     * @param password1
     * @param password2
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/user/updatepassword/")
    Observable<ResponseBody> resetPassword(
            @Query("username") String username,
            @Query("token") String token,
            @Field("password1") String password1,
            @Field("password2") String password2
    );

    /**
     * 注销用户
     * @param username
     * @param token
     * @param qr
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/user/logoutuser/")
    Observable<ResponseBody> destory(
            @Query("username") String username,
            @Query("token") String token,
            @Field("qr") String qr //true
    );
}

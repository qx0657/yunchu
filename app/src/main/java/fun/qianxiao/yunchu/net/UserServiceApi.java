package fun.qianxiao.yunchu.net;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserServiceApi {

    /**
     * 云储登录接口
     * @param uid
     * @param pwd
     * @param expire
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/user/login/")
    Observable<ResponseBody> login(
            @Field("username") String uid,
            @Field("password") String pwd,
            @Field("date") String expire
    );

    /**
     * 云储登录接口2(邮箱登录)
     * @param email
     * @param pwd
     * @param expire
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/user/emaillogin/")
    Observable<ResponseBody> loginEmail(
            @Field("email") String email,
            @Field("password") String pwd,
            @Field("date") String expire
    );

    /**
     * 退出登录
     * @param username
     * @param token
     * @return
     */
    @POST("api/v1/user/logout/")
    Observable<ResponseBody> logout(
            @Query("username") String username,
            @Query("token") String token
    );

    /**
     * 注册
     * @param token
     * @param username
     * @param pwd
     * @param inviteCode
     * @param email
     * @param code
     * @param mobile
     * @param qq
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/user/register/")
    Observable<ResponseBody> register(
            @Query("token") String token,
            @Field("username") String username,
            @Field("password") String pwd,
            @Field("invitationcode") String inviteCode,
            @Field("email") String email,
            @Field("code") String code,
            @Field("mobile") String mobile,
            @Field("qq") String qq
    );

    /**
     * 重置密码
     * @param token
     * @param username
     * @param email
     * @param code
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/user/resetpassword/")
    Observable<ResponseBody> resetPassword(
            @Query("token") String token,
            @Field("username") String username,
            @Field("email") String email,
            @Field("code") String code
    );

}

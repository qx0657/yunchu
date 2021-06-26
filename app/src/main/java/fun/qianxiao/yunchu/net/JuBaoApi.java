package fun.qianxiao.yunchu.net;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface JuBaoApi {
    /**
     * 举报文档
     * @param id 要举报的文档ID
     * @param category 1政治敏感 2暴力血腥 3色情赌博 4虚假诈骗
     * @param content 其他违规内容，填写时category设为0
     * @param password 空
     * @param email 邮箱 用于通知举报结果
     * @param token 空
     * @param type 1
     * @param from official
     * @param types text
     * @return
     */
    @FormUrlEncoded
    @POST("api/v1/report/report.php")
    Observable<ResponseBody> jubao(
            @Field("id") long id,
            @Field("category") int category,
            @Field("content") String content,
            @Field("password") String password,
            @Field("email") String email,
            @Field("token") String token,
            @Field("type") int type,
            @Field("form") String form,
            @Field("types") String types
    );

}

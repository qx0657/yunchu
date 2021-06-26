package fun.qianxiao.yunchu.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import fun.qianxiao.yunchu.net.ApiServiceManager;
import fun.qianxiao.yunchu.net.JuBaoApi;
import fun.qianxiao.yunchu.utils.ToastTool;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class JuBaoModel {
    public enum JuBaoType{
        ZHENGZHIMINGAN(1, "存在政治敏感内容"),
        BAOLIXUEXING(2, "存在暴力血腥内容"),
        SEQINGDUBO(3, "存在色情赌博内容"),
        XUJIAZHAPIAN(4, "存在虚假诈骗内容"),
        OTHER(0, "存在其他违规内容"),
        UNSELECT(-1, "暂未选择");
        private int index;
        private String text;

        JuBaoType(int index, String text) {
            this.index = index;
            this.text = text;
        }
    }

    public interface JuBaoCallBack{
        void juBaoFinish();
        void juBaoError();
    }

    /*
    举报文档
     */
    public void juBao(long id, JuBaoType category, String content, String email, JuBaoCallBack callBack){
        ApiServiceManager.getInstance().create(JuBaoApi.class)
                .jubao(id, category.index, content, "", email, "", 1, "official", "text")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        try{
                            JSONObject jsonObject = new JSONObject(responseBody.string());
                            if(jsonObject.getInt("state")==200){
                                callBack.juBaoFinish();
                                ToastTool.success("已提交举报，请等待审核");
                            }else{
                                callBack.juBaoError();
                                ToastTool.warning(jsonObject.getString("msg"));
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            onError(e);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callBack.juBaoError();
                        ToastTool.error(e.getMessage()==null?e.toString():e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}

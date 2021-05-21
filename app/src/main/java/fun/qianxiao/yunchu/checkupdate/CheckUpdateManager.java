package fun.qianxiao.yunchu.checkupdate;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.OnDownloadListener;
import com.hjq.http.model.HttpMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import fun.qianxiao.yunchu.net.ApiServiceManager;
import fun.qianxiao.yunchu.utils.ToastTool;
import fun.qianxiao.yunchu.view.loading.ILoadingView;
import fun.qianxiao.yunchu.view.loading.MyLoadingDialog;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;


/**
 * Create by QianXiao
 * On 2021/4/6
 */
public final class CheckUpdateManager implements ILoadingView {
    private Context context;
    private MyLoadingDialog loadingDialog;

    private boolean isBrowserDownload = false;
    private boolean isUpdating = false;
    private boolean isApkFullyDownloaded = false;

    public CheckUpdateManager(Context context) {
        this.context = context;
    }

    /**
     * 检查更新
     * @param issilent 是否静默
     */
    public void check(boolean issilent){
        if(!issilent){
            openLoadingDialog("正在检查更新");
        }
        ApiServiceManager.getInstance()
                .create(CheckUpdateApi.class)
                .getUpdateConfig()
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())//子线程访问网络
                .observeOn(AndroidSchedulers.mainThread())//回调到主线程
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ResponseBody responseBody) {
                        try {
                            String data = responseBody.string();
                            JSONObject jsonObject = new JSONObject(data);
                            if(jsonObject.getInt("newversioncode")>context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionCode){
                                //发现新版本
                                String newversionname  = jsonObject.getString("newversionname");
                                String newapkmd5 = jsonObject.getString("newapkmd5");
                                String downloadurl = jsonObject.getString("downloadurl");
                                boolean isforceupdate = jsonObject.getInt("isforceupdate")==1;

                                File mApkFile = new File(PathUtils.getExternalAppDownloadPath(), AppUtils.getAppName() + ".v." + newversionname + ".apk");
                                if(FileUtils.isFileExists(mApkFile) && FileUtils.getFileMD5ToString(mApkFile).equalsIgnoreCase(newapkmd5)){
                                    isApkFullyDownloaded = true;
                                }
                                if(isBrowserDownload){
                                    isApkFullyDownloaded = false;
                                }
                                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                        .setTitle("发现新版本(V." + newversionname + ")")
                                        .setMessage(jsonObject.getString("updatacontent"))
                                        .setCancelable(false)
                                        //点击事件在下面设置 防止点击后dialog消失
                                        .setPositiveButton(isApkFullyDownloaded?"立即安装":"立即更新", null);
                                if (!isforceupdate) {
                                    builder = builder.setNegativeButton(isApkFullyDownloaded?"暂不安装":"暂不更新", null);
                                }
                                if(isBrowserDownload){
                                    builder = builder.setNeutralButton("复制下载链接", null);
                                }
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
                                    ClipboardUtils.copyText(downloadurl);
                                    ToastTool.success("下载链接已复制至剪贴板");
                                });
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(isBrowserDownload){
                                            //从其他浏览器打开
                                            Intent intent = new Intent();
                                            intent.setAction("android.intent.action.VIEW");
                                            intent.addCategory("android.intent.category.BROWSABLE");
                                            intent.addCategory("android.intent.category.DEFAULT");
                                            Uri content_url = Uri.parse(downloadurl);
                                            intent.setData(content_url);
                                            context.startActivity(intent);
                                        }else{
                                            if(isApkFullyDownloaded){
                                                //立即安装
                                                AppUtils.installApp(mApkFile);
                                                return;
                                            }
                                            if(!PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                                                PermissionUtils.permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                        .callback(new PermissionUtils.SimpleCallback() {
                                                            @Override
                                                            public void onGranted() {
                                                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                                                            }

                                                            @Override
                                                            public void onDenied() {
                                                                onClick(v);
                                                            }
                                                        })
                                                        .request();
                                                return;
                                            }
                                            if(!isUpdating){
                                                isUpdating = true;
                                            }else{
                                                return;
                                            }
                                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
                                            EasyHttp.download((AppCompatActivity)context)
                                                    .method(HttpMethod.GET)
                                                    .file(mApkFile)
                                                    .url(downloadurl)
                                                    .md5(newapkmd5)
                                                    .listener(new OnDownloadListener() {
                                                        @Override
                                                        public void onStart(File file) {

                                                        }

                                                        @Override
                                                        public void onProgress(File file, int progress) {
                                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setText(progress+"%");
                                                        }

                                                        @Override
                                                        public void onComplete(File file) {
                                                            if(!isforceupdate){
                                                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
                                                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setText("暂不安装");
                                                            }
                                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setText("立即安装");
                                                            isUpdating = false;
                                                            isApkFullyDownloaded = true;
                                                            //AppUtils.installApp(finalFile);
                                                            installApp(file);
                                                        }

                                                        @Override
                                                        public void onError(File file, Exception e) {
                                                            ToastTool.error(e.toString());
                                                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
                                                            isUpdating = false;
                                                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setText("立即更新");
                                                        }

                                                        @Override
                                                        public void onEnd(File file) {

                                                        }
                                                    })
                                                    .start();
                                        }
                                    }
                                });
                            }else{
                                if(!issilent){
                                    ToastTool.success("当前已是最新版本");
                                }
                            }
                        } catch (IOException | JSONException | PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                            onError(e);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if(!issilent){
                            closeLoadingDialog();
                            ToastTool.error("检查更新出错:"+e.toString());
                        }
                    }

                    @Override
                    public void onComplete() {
                        if(!issilent){
                            closeLoadingDialog();
                        }
                    }
                });
    }

    private void installApp(File mApkFile){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, AppUtils.getAppPackageName()+ ".utilcode.provider", mApkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(mApkFile);
        }

        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void openLoadingDialog(String msg) {
        if(loadingDialog == null){
            loadingDialog = new MyLoadingDialog(context);
        }
        if(!loadingDialog.isShowing()){
            loadingDialog.setMessage(msg);
            loadingDialog.show();
        }
    }

    @Override
    public void closeLoadingDialog() {
        if(loadingDialog!=null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }
}

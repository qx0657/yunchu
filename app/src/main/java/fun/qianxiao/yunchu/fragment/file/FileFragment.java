package fun.qianxiao.yunchu.fragment.file;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.blankj.utilcode.constant.MemoryConstants;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.UriUtils;
import com.blankj.utilcode.util.UtilsTransActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.hjq.http.EasyHttp;
import com.hjq.http.listener.HttpCallback;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.base.BaseFragment;
import fun.qianxiao.yunchu.databinding.FragmentFileBinding;
import fun.qianxiao.yunchu.net.easyhttp.FileUploadApi;
import fun.qianxiao.yunchu.ui.main.MainActivity;
import fun.qianxiao.yunchu.utils.ToastTool;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.Call;

public class FileFragment extends BaseFragment<FragmentFileBinding> {
    private File file;

    @Override
    protected FragmentFileBinding getBinding() {
        return FragmentFileBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initListener() {
        binding.ivSelectFileF.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    ToastTool.warning("??????????????????????????????");
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    startActivityForResult(intent, 1024);
                    return;
                }
            }if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(!PermissionUtils.isGranted(Manifest.permission.READ_EXTERNAL_STORAGE)){
                    PermissionUtils.permission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            .rationale(new PermissionUtils.OnRationaleListener() {
                                @Override
                                public void rationale(@NonNull @NotNull UtilsTransActivity activity, @NonNull @NotNull ShouldRequest shouldRequest) {
                                    shouldRequest.again(true);
                                }
                            })
                            .callback(new PermissionUtils.FullCallback() {
                                @Override
                                public void onGranted(@NonNull @NotNull List<String> granted) {
                                    binding.ivSelectFileF.performClick();
                                }

                                @Override
                                public void onDenied(@NonNull @NotNull List<String> deniedForever, @NonNull @NotNull List<String> denied) {
                                    if(deniedForever.size()!=0){
                                        new AlertDialog.Builder(context)
                                                .setTitle("????????????")
                                                .setMessage("??????????????????????????????????????????????????????????????????????????????????????????????????????????????????")
                                                .setPositiveButton("????????????", (dialog, which) -> {
                                                    PermissionUtils.launchAppDetailsSettings();
                                                })
                                                .setNegativeButton("??????", null)
                                                .show();
                                    }else{
                                        binding.ivSelectFileF.performClick();
                                    }
                                }
                            })
                            .request();
                    return;
                }
            }
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");//???????????????
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, 1025);
        });
        binding.btnUploadCommit.setOnClickListener(v -> {
            if(file==null){
                binding.btnUploadCommit.showError(2000);
                ToastTool.warning("????????????????????????");
                binding.ivSelectFileF.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
            }else if(FileUtils.getLength(file)>50* MemoryConstants.MB){
                binding.btnUploadCommit.showError(2000);
                ToastTool.warning("???????????????????????????50M");
            }else{
                EasyHttp.post(this)
                        .server("http://shared.wd.cn.ecsxs.com")
                        .api(new FileUploadApi()
                                .setImage(file)
                                .setCollection_id(Objects.requireNonNull(binding.tieCollectionIdFileF.getText()).toString())
                                .setCollection_des(Objects.requireNonNull(binding.tieCollectionDesFileF.getText()).toString())
                                .setFile_des(Objects.requireNonNull(binding.tieFileDesFileF.getText()).toString()))
                        .request(new HttpCallback<String>(null) {

                            @Override
                            public void onSucceed(String data) {
                                binding.btnUploadCommit.showSucceed();
                                ThreadUtils.runOnUiThreadDelayed(()->
                                        binding.btnUploadCommit.reset(),1000);
                                try{
                                    LogUtils.i(data);
                                    Pattern p = Pattern.compile("???????????????http://(.*)</center>");
                                    Pattern p2 = Pattern.compile("???????????????http://(.*)</center>");
                                    Matcher ma = p.matcher(data);
                                    Matcher ma2 = p2.matcher(data);
                                    String file_url = "", collection_url="";
                                    if(ma.find()){
                                        file_url = ma.group();
                                        file_url = file_url.substring(5,file_url.length()-9);
                                    }
                                    if(ma2.find()){
                                        collection_url = ma2.group();
                                        collection_url = collection_url.substring(5,collection_url.length()-9);
                                    }
                                    String text = "???????????????"+file_url;
                                    if(!TextUtils.isEmpty(collection_url)){
                                        text += "\n";
                                        text += "???????????????" + collection_url;
                                    }
                                    String finalFile_url = file_url;
                                    String finalCollection_url = collection_url;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                            .setTitle("????????????")
                                            .setMessage(text)
                                            .setPositiveButton("??????????????????", null);
                                    if(!TextUtils.isEmpty(collection_url)){
                                        builder = builder.setNeutralButton("??????????????????", null);
                                    }
                                    AlertDialog alertDialog = builder.setNegativeButton("??????", null)
                                            .setCancelable(false)
                                            .show();
                                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
                                        ClipboardUtils.copyText(finalFile_url);
                                        ToastTool.success("?????????????????????????????????");
                                    });
                                    if(!TextUtils.isEmpty(collection_url)){
                                        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v1 -> {
                                            ClipboardUtils.copyText(finalCollection_url);
                                            ToastTool.success("?????????????????????????????????");
                                        });
                                    }
                                    //alertDialog.setOnDismissListener(dialog -> binding.btnUploadCommit.reset());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    ToastTool.error("?????????????????? "+e.getMessage());
                                    //binding.btnUploadCommit.reset();
                                }
                            }

                            @Override
                            public void onStart(Call call) {
                                ((MainActivity) Objects.requireNonNull(getActivity())).openLoadingDialog("???????????????");
                                super.onStart(call);
                            }

                            @Override
                            public void onFail(Exception e) {
                                binding.btnUploadCommit.showError(2000);
                                super.onFail(e);
                                ((MainActivity) Objects.requireNonNull(getActivity())).closeLoadingDialog();
                                ToastTool.error(e.getMessage());
                            }

                            @Override
                            public void onEnd(Call call) {
                                super.onEnd(call);
                                ((MainActivity) Objects.requireNonNull(getActivity())).closeLoadingDialog();
                            }
                        });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1024:
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    binding.ivSelectFileF.performClick();
                }else{
                    ToastTool.warning("????????????????????????");
                }
                break;
            case 1025:
                if (resultCode == Activity.RESULT_OK) {
                    assert data != null;
                    Uri uri = data.getData();
                    file =  UriUtils.uri2File(uri);
                    LogUtils.i(file.toString());
                    binding.tvSelectFilePathFileF.setText(file.toString());
                    binding.tvSelectFilePathFileF.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }



    @Override
    protected void initData() {
    }
}

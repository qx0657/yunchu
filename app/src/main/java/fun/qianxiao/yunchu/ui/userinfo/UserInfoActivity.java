package fun.qianxiao.yunchu.ui.userinfo;

import android.annotation.SuppressLint;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.base.BaseActivity;
import fun.qianxiao.yunchu.base.BaseAlertDialog;
import fun.qianxiao.yunchu.bean.UserInfo;
import fun.qianxiao.yunchu.databinding.ActivityUserInfoBinding;
import fun.qianxiao.yunchu.eventbus.EBLoginExpire;
import fun.qianxiao.yunchu.model.OperateListener;
import fun.qianxiao.yunchu.model.UserInfoModel;
import fun.qianxiao.yunchu.ui.userinfo.utils.OnVerifyResult;
import fun.qianxiao.yunchu.ui.userinfo.utils.fingerprint.FingerprintManagerUtil;
import fun.qianxiao.yunchu.ui.userinfo.view.ResetPwdDialogFragment;
import fun.qianxiao.yunchu.ui.userinfo.view.VerifyFingerprintAlterDialog;
import fun.qianxiao.yunchu.utils.ToastTool;
import fun.qianxiao.yunchu.view.loading.ILoadingView;
import fun.qianxiao.yunchu.view.loading.MyLoadingDialog;

public class UserInfoActivity extends BaseActivity<ActivityUserInfoBinding> implements ILoadingView {
    private MyLoadingDialog loadingDialog;
    private VerifyFingerprintAlterDialog verifyFingerprintAlterDialog;

    @Override
    protected ActivityUserInfoBinding getBinding() {
        return ActivityUserInfoBinding.inflate(getLayoutInflater());
    }

    public interface OnVerifySuccessCallback{
        void verifySuccess();
    }
    private void invokeFingerPrint(OnVerifySuccessCallback callback){
        if(verifyFingerprintAlterDialog == null){
            verifyFingerprintAlterDialog = new VerifyFingerprintAlterDialog(context);
        }
        verifyFingerprintAlterDialog.setOnVerifyResult(new OnVerifyResult() {
            @Override
            public void verifySuccess(BaseAlertDialog dialog) {
                ThreadUtils.runOnUiThreadDelayed(()->{
                    dialog.dismiss();
                    callback.verifySuccess();
                },200);
            }

            @Override
            public void verifyFail(BaseAlertDialog dialog, String e) {
                dialog.dismiss();
                ToastTool.error(e);
            }

            @Override
            public void verifyCancle(BaseAlertDialog dialog) {

            }
        });
        verifyFingerprintAlterDialog.setCancelable(true);
        verifyFingerprintAlterDialog.show();
    }

    @Override
    protected void initListener() {
        binding.cvInviteCodeUserinfo.setOnClickListener(v -> {
            ClipboardUtils.copyText(binding.tvAppidUia.getText().toString());
            ToastTool.success("??????????????????????????????");
        });
        binding.tvResetKeyUserinfo.setOnClickListener(v -> {
            if(FingerprintManagerUtil.isSupportFingerPaint(context)){
                invokeFingerPrint(() -> {
                    openLoadingDialog("????????????");
                    new UserInfoModel().resetUserAppKey(new OperateListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void operateSuccess(String appKey) {
                            closeLoadingDialog();
                            ToastTool.success("AppKey????????????");
                            binding.tvAppkeyUia.setTag(appKey);
                            binding.tvAppkeyUia.setText(appKey.substring(0,12)+"********"+appKey.substring(20,32));
                        }

                        @Override
                        public void operateError(String e) {
                            closeLoadingDialog();
                            ToastTool.error(e);
                        }
                    });
                });
            }else{

            }
        });
        binding.tvCopyKeyUserinfo.setOnClickListener(v -> {
            ClipboardUtils.copyText((String) binding.tvAppkeyUia.getTag());
            ToastTool.success("AppKey?????????????????????");
        });
        binding.sbResPwdUia.setOnClickListener(v -> {
            if(FingerprintManagerUtil.isSupportFingerPaint(context)){
                invokeFingerPrint(() -> new ResetPwdDialogFragment().show(getSupportFragmentManager(),"ResetPwdDialogFragment"));
            }else{
                new ResetPwdDialogFragment().show(getSupportFragmentManager(),"ResetPwdDialogFragment");
            }

        });
    }

    @SuppressLint("SetTextI18n")
    private void showUserInfo(UserInfo userInfo){
        setTitle("?????????"+userInfo.getId()+"?????????");
        binding.tvGradeUserinfo.setText(userInfo.getA_grade() +" ???");
        Glide.with(context)
                .load(String.format("https://q1.qlogo.cn/g?b=qq&nk=%s&s=640", userInfo.getQq()))
                //??????????????? ImageView
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(binding.ivAvatarUserinfo);
        binding.tvUsernameUserinfo.setText(userInfo.getUsername());
        binding.sbQqUia.setRightText(userInfo.getQq());
        binding.sbMobileUia.setRightText(userInfo.getMobile());
        binding.tvEmailUserinfo.setText(userInfo.getEmail());
        binding.tvAppidUia.setText(userInfo.getAppid());
        if(userInfo.getAppkey()!=null&&userInfo.getAppkey().length()==32){
            binding.tvAppkeyUia.setTag(userInfo.getAppkey());
            binding.tvAppkeyUia.setText(userInfo.getAppkey().substring(0,12)+"********"+userInfo.getAppkey().substring(20,32));
        }
    }

    @Override
    protected void initData() {
        openLoadingDialog("?????????");
        new UserInfoModel().getUserInfo(new UserInfoModel.GetUserInfoListener() {
            @Override
            public void getUserInfoSuccess(UserInfo userInfo) {
                closeLoadingDialog();
                showUserInfo(userInfo);
            }

            @Override
            public void getUserInfoError(String e) {
                closeLoadingDialog();
                ToastTool.error(e);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
        return true;
    }

    private void destoryUID(){
        new AlertDialog.Builder(context)
                .setTitle("????????????")
                .setMessage("??????????????????????????????")
                .setPositiveButton("??????", (dialog1, which1) -> {
                    new AlertDialog.Builder(context)
                            .setTitle("????????????")
                            .setMessage("??????????????????????????????????????????????????????")
                            .setPositiveButton("??????", (dialog2, which2) -> {
                                new AlertDialog.Builder(context)
                                        .setTitle("????????????")
                                        .setMessage("???????????????????????????????????????????????????????????????????????????????????????")
                                        .setPositiveButton("??????", (dialog3, which3) -> {
                                            openLoadingDialog("????????????");
                                            new UserInfoModel().destory(new OperateListener() {
                                                @Override
                                                public void operateSuccess(String var1) {
                                                    closeLoadingDialog();
                                                    ToastTool.success("????????????");
                                                    finish();
                                                    ThreadUtils.runOnUiThreadDelayed(()->EventBus.getDefault().post(new EBLoginExpire(true)),500);
                                                }

                                                @Override
                                                public void operateError(String e) {
                                                    closeLoadingDialog();
                                                    ToastTool.error(e);
                                                }
                                            });
                                        })
                                        .setNegativeButton("??????", null)
                                        .show().getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.common_cancel_text_color));
                            })
                            .setNegativeButton("??????", null)
                            .show().getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.common_cancel_text_color));
                })
                .setNegativeButton("??????", null)
                .show().getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.common_cancel_text_color));

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_destory:
                if(FingerprintManagerUtil.isSupportFingerPaint(context)){
                    invokeFingerPrint(this::destoryUID);
                }else{
                    destoryUID();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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

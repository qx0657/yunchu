package fun.qianxiao.yunchu.ui.userinfo;

import android.annotation.SuppressLint;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

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

    @Override
    protected void initListener() {
        binding.cvInviteCodeUserinfo.setOnClickListener(v -> {
            ClipboardUtils.copyText(binding.tvAppidUia.getText().toString());
            ToastTool.success("邀请码已复制至剪贴板");
        });
        binding.tvResetKeyUserinfo.setOnClickListener(v -> {

            if(verifyFingerprintAlterDialog == null){
                verifyFingerprintAlterDialog = new VerifyFingerprintAlterDialog(context);
            }
            verifyFingerprintAlterDialog.setOnVerifyResult(new OnVerifyResult() {
                @Override
                public void verifySuccess(BaseAlertDialog dialog) {
                    ThreadUtils.runOnUiThreadDelayed(()->{
                        dialog.dismiss();
                        openLoadingDialog("正在重置");
                        new UserInfoModel().resetUserAppKey(new OperateListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void operateSuccess(String appKey) {
                                closeLoadingDialog();
                                ToastTool.success("AppKey重置成功");
                                binding.tvAppkeyUia.setTag(appKey);
                                binding.tvAppkeyUia.setText(appKey.substring(0,12)+"********"+appKey.substring(20,32));
                            }

                            @Override
                            public void operateError(String e) {
                                closeLoadingDialog();
                                ToastTool.error(e);
                            }
                        });
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

        });
        binding.tvCopyKeyUserinfo.setOnClickListener(v -> {
            ClipboardUtils.copyText((String) binding.tvAppkeyUia.getTag());
            ToastTool.success("AppKey已复制至剪贴板");
        });
        binding.sbResPwdUia.setOnClickListener(v -> {
            if(verifyFingerprintAlterDialog == null){
                verifyFingerprintAlterDialog = new VerifyFingerprintAlterDialog(context);
            }
            verifyFingerprintAlterDialog.setOnVerifyResult(new OnVerifyResult() {
                @Override
                public void verifySuccess(BaseAlertDialog dialog) {
                    ThreadUtils.runOnUiThreadDelayed(()->{
                        dialog.dismiss();
                        new ResetPwdDialogFragment().show(getSupportFragmentManager(),"ResetPwdDialogFragment");
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
        });
    }

    @SuppressLint("SetTextI18n")
    private void showUserInfo(UserInfo userInfo){
        setTitle("云储第"+userInfo.getId()+"位用户");
        binding.tvGradeUserinfo.setText(userInfo.getA_grade() +" 级");
        Glide.with(context)
                .load(String.format("https://q1.qlogo.cn/g?b=qq&nk=%s&s=640", userInfo.getQq()))
                //显示圆形的 ImageView
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
        openLoadingDialog("加载中");
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

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_destory:

                if(verifyFingerprintAlterDialog == null){
                    verifyFingerprintAlterDialog = new VerifyFingerprintAlterDialog(context);
                }
                verifyFingerprintAlterDialog.setOnVerifyResult(new OnVerifyResult() {
                    @Override
                    public void verifySuccess(BaseAlertDialog dialog) {
                        ThreadUtils.runOnUiThreadDelayed(()->{
                            dialog.dismiss();
                            new AlertDialog.Builder(context)
                                    .setTitle("注销账号")
                                    .setMessage("您确认要注销账号吗？")
                                    .setPositiveButton("确认", (dialog1, which1) -> {
                                        new AlertDialog.Builder(context)
                                                .setTitle("注销账号")
                                                .setMessage("将会申请注销你的云储账号。确认继续？")
                                                .setPositiveButton("确认", (dialog2, which2) -> {
                                                    new AlertDialog.Builder(context)
                                                            .setTitle("注销账号")
                                                            .setMessage("注销成功后，你将会无法在使用云储服务。您确认要注销账号吗？")
                                                            .setPositiveButton("确认", (dialog3, which3) -> {
                                                                openLoadingDialog("正在注销");
                                                                new UserInfoModel().destory(new OperateListener() {
                                                                    @Override
                                                                    public void operateSuccess(String var1) {
                                                                        closeLoadingDialog();
                                                                        ToastTool.success("注销成功");
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
                                                            .setNegativeButton("取消", null)
                                                            .show();
                                                })
                                                .setNegativeButton("取消", null)
                                                .show();
                                    })
                                    .setNegativeButton("取消", null)
                                    .show();
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

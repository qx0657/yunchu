package fun.qianxiao.yunchu.fragment.doc.view;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ThreadUtils;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.base.BaseDialogFragment;
import fun.qianxiao.yunchu.databinding.DialogfragmentForgetpwdBinding;
import fun.qianxiao.yunchu.model.OperateListener;
import fun.qianxiao.yunchu.model.UserManagerModel;
import fun.qianxiao.yunchu.model.VerifyCodeModel;
import fun.qianxiao.yunchu.utils.ToastTool;
import fun.qianxiao.yunchu.view.loading.ILoadingView;
import fun.qianxiao.yunchu.view.loading.MyLoadingDialog;

public class ForgetPwdDialogFragment extends BaseDialogFragment<DialogfragmentForgetpwdBinding> implements ILoadingView {
    private MyLoadingDialog loadingDialog;

    @Override
    protected DialogfragmentForgetpwdBinding getBinging() {
        return DialogfragmentForgetpwdBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        binding.cvCountdownForgetusername.setOnClickListener(v -> {
            String email = Objects.requireNonNull(binding.tieEmailForgetpwdDf.getText()).toString();
            if(TextUtils.isEmpty(email)){
                binding.tieEmailForgetpwdDf.requestFocus();
                ToastTool.warning("请输入邮箱");
                binding.tilEmailForgetpwdDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                return;
            }
            if(!RegexUtils.isEmail(email)){
                binding.tieEmailForgetpwdDf.requestFocus();
                ToastTool.warning("请输入正确的邮箱");
                binding.tilEmailForgetpwdDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                return;
            }
            new AlertDialog.Builder(context)
                    .setTitle("忘记用户名")
                    .setMessage("确认向"+email+"发送邮箱对应用户名？")
                    .setPositiveButton("确定",(dialog, which) -> {
                        openLoadingDialog("正在发送");
                        new VerifyCodeModel().sendUsername2mail(email, new OperateListener() {
                            @Override
                            public void operateSuccess(String var1) {
                                closeLoadingDialog();
                                ToastTool.success("用户名已发送至邮箱"+email);
                                binding.cvCountdownForgetusername.start();
                            }

                            @Override
                            public void operateError(String e) {
                                closeLoadingDialog();
                                ToastTool.error(e);
                            }
                        });
                    })
                    .setNegativeButton("取消", null)
                    .setCancelable(false)
                    .show();
        });
        binding.cvCountdownSendcode.setOnClickListener(v->{
            String email = Objects.requireNonNull(binding.tieEmailForgetpwdDf.getText()).toString();
            if(TextUtils.isEmpty(email)){
                binding.tieEmailForgetpwdDf.requestFocus();
                ToastTool.warning("请输入邮箱");
                binding.tilEmailForgetpwdDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                return;
            }
            if(!RegexUtils.isEmail(email)){
                binding.tieEmailForgetpwdDf.requestFocus();
                ToastTool.warning("请输入正确的邮箱");
                binding.tilEmailForgetpwdDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                return;
            }
            String username = Objects.requireNonNull(binding.tieUidForgetpwdDf.getText()).toString();
            if(TextUtils.isEmpty(username)){
                binding.tieUidForgetpwdDf.requestFocus();
                ToastTool.warning("请输入用户名");
                binding.tilUidForgetpwdDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                return;
            }
            openLoadingDialog("正在发送");
            new VerifyCodeModel().sendForgetPwdMailCode(username, email, new OperateListener() {
                @Override
                public void operateSuccess(String token) {
                    closeLoadingDialog();
                    binding.cvCountdownSendcode.setTag(token);
                    ToastTool.success("验证码发送成功");
                    binding.cvCountdownSendcode.start();
                }

                @Override
                public void operateError(String e) {
                    closeLoadingDialog();
                    ToastTool.error(e);
                }
            });
        });
        binding.btnForgetpwdCommit.setOnClickListener(v -> {
            String email = Objects.requireNonNull(binding.tieEmailForgetpwdDf.getText()).toString();
            if(TextUtils.isEmpty(email)){
                binding.tieEmailForgetpwdDf.requestFocus();
                ToastTool.warning("请输入邮箱");
                binding.tilEmailForgetpwdDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnForgetpwdCommit.showError(2000);
                return;
            }
            if(!RegexUtils.isEmail(email)){
                binding.tieEmailForgetpwdDf.requestFocus();
                ToastTool.warning("请输入正确的邮箱");
                binding.tilEmailForgetpwdDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnForgetpwdCommit.showError(2000);
                return;
            }
            String username = Objects.requireNonNull(binding.tieUidForgetpwdDf.getText()).toString();
            if(TextUtils.isEmpty(username)){
                binding.tieUidForgetpwdDf.requestFocus();
                ToastTool.warning("请输入用户名");
                binding.tilUidForgetpwdDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnForgetpwdCommit.showError(2000);
                return;
            }
            if(StringUtils.isEmpty((String) binding.cvCountdownSendcode.getTag())){
                binding.tieCodeForgetpwdDf.requestFocus();
                ToastTool.warning("请发送验证码");
                binding.cvCountdownSendcode.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnForgetpwdCommit.showError(2000);
                return;
            }
            String code = Objects.requireNonNull(binding.tieCodeForgetpwdDf.getText()).toString().trim();
            if(TextUtils.isEmpty(code)){
                binding.tieCodeForgetpwdDf.setText("");
                binding.tieCodeForgetpwdDf.requestFocus();
                ToastTool.warning("请输入邮箱验证码");
                binding.tilCodeForgetpwdDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnForgetpwdCommit.showError(2000);
                return;
            }
            if(code.length()!= getResources().getInteger(R.integer.verify_code_length)){
                binding.tieCodeForgetpwdDf.requestFocus();
                ToastTool.warning("请输入正确的验证码");
                binding.tilCodeForgetpwdDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnForgetpwdCommit.showError(2000);
                return;
            }
            KeyboardUtils.hideSoftInput(Objects.requireNonNull(getActivity()));
            new UserManagerModel().resetPwd((String) binding.cvCountdownSendcode.getTag(), username, email, code,
                    new OperateListener() {
                        @Override
                        public void operateSuccess(String var1) {
                            ToastTool.success("密码已发送到您的邮箱");
                            binding.btnForgetpwdCommit.showSucceed();
                            ThreadUtils.runOnUiThreadDelayed(()->{
                                dismiss();
                            },1000);
                        }

                        @Override
                        public void operateError(String e) {
                            ToastTool.error(e);
                            binding.btnForgetpwdCommit.showError(2000);
                        }
                    });
        });
    }

    @Override
    protected void initData() {
        binding.tieEmailForgetpwdDf.requestFocus();
        //延迟打开软键盘
        new Timer().schedule(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }

        }, 200);//n秒后弹出
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

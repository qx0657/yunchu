package fun.qianxiao.yunchu.fragment.doc.view;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.base.BaseDialogFragment;
import fun.qianxiao.yunchu.databinding.DialogfragmentRegisterBinding;
import fun.qianxiao.yunchu.model.OperateListener;
import fun.qianxiao.yunchu.model.UserManagerModel;
import fun.qianxiao.yunchu.model.VerifyCodeModel;
import fun.qianxiao.yunchu.utils.MySpUtils;
import fun.qianxiao.yunchu.utils.ToastTool;
import fun.qianxiao.yunchu.view.loading.ILoadingView;
import fun.qianxiao.yunchu.view.loading.MyLoadingDialog;

/**
 * Create by QianXiao
 * On 2020/6/20
 */
public class RegisterDialogFragment
        extends BaseDialogFragment<DialogfragmentRegisterBinding> implements ILoadingView {
    private TextInputEditText tie_uid_register_df,tie_pwd_register_df,tie_mobile_register_df,tie_qq_register_df,tie_email_register_df,tie_code_register_df;
    private TextInputLayout til_code_register_df;
    private MyLoadingDialog loadingDialog;
    private UserManagerModel.OnLoginListener onLoginClickListener;

    @Override
    protected DialogfragmentRegisterBinding getBinging() {
        return DialogfragmentRegisterBinding.inflate(getLayoutInflater());
    }

    public RegisterDialogFragment setOnLoginClickListener(UserManagerModel.OnLoginListener onLoginClickListener) {
        this.onLoginClickListener = onLoginClickListener;
        return this;
    }

    @Override
    protected void initView() {
        tie_uid_register_df = f(R.id.tie_uid_register_df);
        tie_pwd_register_df = f(R.id.tie_pwd_register_df);
        tie_mobile_register_df = f(R.id.tie_mobile_register_df);
        tie_qq_register_df = f(R.id.tie_qq_register_df);
        tie_email_register_df = f(R.id.tie_email_register_df);
        tie_code_register_df = f(R.id.tie_code_register_df);
        til_code_register_df = f(R.id.til_code_register_df);
    }

    @Override
    protected void initListener() {
        binding.cvRegisterCountdown.setOnClickListener(v -> {
            String email = Objects.requireNonNull(tie_email_register_df.getText()).toString().trim();
            if(TextUtils.isEmpty(email)){
                tie_email_register_df.requestFocus();
                tie_email_register_df.setText("");
                ToastTool.warning("请输入邮箱");
                binding.tilEmailRegisterDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnRegisterCommit.showError(2000);
                return;
            }
            if(!RegexUtils.isEmail(email)){
                tie_email_register_df.requestFocus();
                ToastTool.warning("请输入正确的邮箱");
                binding.tilEmailRegisterDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnRegisterCommit.showError(2000);
                return;
            }
            openLoadingDialog("正在发送");
            new VerifyCodeModel().sendRegisterMailCode(email, new OperateListener() {
                @Override
                public void operateSuccess(String token) {
                    closeLoadingDialog();
                    ToastTool.success("验证码发送成功");
                    binding.cvRegisterCountdown.setTag(token);
                    binding.cvRegisterCountdown.start();
                }

                @Override
                public void operateError(String e) {
                    closeLoadingDialog();
                    ToastTool.error(e);
                }
            });

        });
        binding.btnRegisterCommit.setOnClickListener(v -> {
            String uid = Objects.requireNonNull(tie_uid_register_df.getText()).toString().trim();
            if(TextUtils.isEmpty(uid)){
                tie_uid_register_df.requestFocus();
                tie_uid_register_df.setText("");
                ToastTool.warning("请输入用户名");
                binding.tilUidRegisterDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnRegisterCommit.showError(2000);
                return;
            }
            String pwd = Objects.requireNonNull(tie_pwd_register_df.getText()).toString();
            if(TextUtils.isEmpty(pwd)){
                tie_pwd_register_df.requestFocus();
                ToastTool.warning("请输入密码");
                binding.tilPwdRegisterDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnRegisterCommit.showError(2000);
                return;
            }
            String phone = Objects.requireNonNull(tie_mobile_register_df.getText()).toString().trim();
            if(TextUtils.isEmpty(phone)){
                tie_mobile_register_df.requestFocus();
                tie_mobile_register_df.setText("");
                ToastTool.warning("请输入手机号");
                binding.tilMobileRegisterDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnRegisterCommit.showError(2000);
                return;
            }
            if(!RegexUtils.isMobileExact(phone)){
                tie_mobile_register_df.requestFocus();
                ToastTool.warning("请输入正确的手机号");
                binding.tilMobileRegisterDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnRegisterCommit.showError(2000);
                return;
            }
            String qq = Objects.requireNonNull(tie_qq_register_df.getText()).toString().trim();
            if(TextUtils.isEmpty(qq)){
                tie_qq_register_df.requestFocus();
                tie_qq_register_df.setText("");
                ToastTool.warning("请输入QQ号");
                binding.tilQqRegisterDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnRegisterCommit.showError(2000);
                return;
            }
            String email = Objects.requireNonNull(tie_email_register_df.getText()).toString().trim();
            if(TextUtils.isEmpty(email)){
                tie_email_register_df.requestFocus();
                tie_email_register_df.setText("");
                ToastTool.warning("请输入邮箱");
                binding.tilEmailRegisterDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnRegisterCommit.showError(2000);
                return;
            }
            if(StringUtils.isEmpty((String) binding.cvRegisterCountdown.getTag())){
                tie_code_register_df.requestFocus();
                ToastTool.warning("请发送验证码");
                binding.cvRegisterCountdown.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnRegisterCommit.showError(2000);
                return;
            }
            if(!RegexUtils.isEmail(email)){
                tie_email_register_df.requestFocus();
                ToastTool.warning("请输入正确的邮箱");
                binding.tilEmailRegisterDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnRegisterCommit.showError(2000);
                return;
            }
            String code = Objects.requireNonNull(tie_code_register_df.getText()).toString().trim();
            if(TextUtils.isEmpty(code)){
                tie_code_register_df.setText("");
                tie_code_register_df.requestFocus();
                ToastTool.warning("请输入邮箱验证码");
                binding.tilCodeRegisterDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnRegisterCommit.showError(2000);
                return;
            }
            if(code.length()!= getResources().getInteger(R.integer.verify_code_length)){
                tie_code_register_df.requestFocus();
                ToastTool.warning("请输入正确的验证码");
                binding.tilCodeRegisterDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnRegisterCommit.showError(2000);
                return;
            }
            tie_code_register_df.requestFocus();
            KeyboardUtils.hideSoftInput(tie_code_register_df);
            new UserManagerModel().register((String) binding.cvRegisterCountdown.getTag(), uid, pwd, email, code, phone, qq, new UserManagerModel.OnRegisterListener() {
                        @Override
                        public void registerSuccess(String uid, String pwd) {
                            ToastTool.success("注册成功");
                            MySpUtils.save("login_uid",uid);
                            MySpUtils.save("login_pwd",pwd);
                            binding.btnRegisterCommit.showSucceed();
                            ThreadUtils.runOnUiThreadDelayed(()->{
                                assert getFragmentManager() != null;
                                new LoginDialogFragment(true).setOnLoginClickListener(onLoginClickListener).show(getFragmentManager(),"LoginDialogFragment");
                                dismiss();
                            },1000);
                        }

                        @Override
                        public void registerError(String e) {
                            ToastTool.error(e);
                            binding.btnRegisterCommit.showError(2000);
                        }
                    });
        });
    }

    @Override
    protected void initData() {
        tie_uid_register_df.requestFocus();
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

    int howManySecondsToTryAgain = 60;

    @Override
    public void dismiss() {
        KeyboardUtils.hideSoftInput(Objects.requireNonNull(getActivity()));
        super.dismiss();
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

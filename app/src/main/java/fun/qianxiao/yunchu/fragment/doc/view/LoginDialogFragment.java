package fun.qianxiao.yunchu.fragment.doc.view;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.hjq.widget.view.SubmitButton;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.base.BaseDialogFragment;
import fun.qianxiao.yunchu.bean.User;
import fun.qianxiao.yunchu.databinding.DialogfragmentLoginBinding;
import fun.qianxiao.yunchu.model.UserManagerModel;
import fun.qianxiao.yunchu.utils.MySpUtils;
import fun.qianxiao.yunchu.utils.ToastTool;


/**
 * Create by QianXiao
 * On 2021/5/17
 */
public class LoginDialogFragment extends BaseDialogFragment<DialogfragmentLoginBinding> {
    private boolean autoLogin;
    private TextInputEditText tie_uid_login_df,tie_pwd_login_df;
    private SubmitButton btn_login_commit;
    private UserManagerModel.OnLoginListener onLoginClickListener;

    public LoginDialogFragment(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }

    public LoginDialogFragment() {
    }

    public LoginDialogFragment setOnLoginClickListener(UserManagerModel.OnLoginListener onLoginClickListener) {
        this.onLoginClickListener = onLoginClickListener;
        return this;
    }

    @Override
    protected DialogfragmentLoginBinding getBinging() {
        return DialogfragmentLoginBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        tie_uid_login_df = binding.tieUidLoginDf;
        tie_pwd_login_df = binding.tiePwdLoginDf;
        btn_login_commit = binding.btnLoginCommit;
    }

    @Override
    protected void initListener() {
        binding.tvForgetpwd.setOnLongClickListener(v -> {
            String pwd = ClipboardUtils.getText().toString();
            if(!TextUtils.isEmpty(pwd)){
                tie_pwd_login_df.setText(pwd);
            }
            return true;
        });
        binding.btnLoginCommit.setOnClickListener(v -> {
            String uid = Objects.requireNonNull(tie_uid_login_df.getText()).toString();
            if(TextUtils.isEmpty(uid)){
                ToastTool.warning("请输入用户名或邮箱");
                binding.tilUidLoginDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnLoginCommit.showError(2000);
                tie_uid_login_df.requestFocus();
                return;
            }
            String pwd = Objects.requireNonNull(tie_pwd_login_df.getText()).toString();
            if(TextUtils.isEmpty(pwd)){
                ToastTool.warning("请输入密码");
                binding.tilPwdLoginDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnLoginCommit.showError(2000);
                tie_pwd_login_df.requestFocus();
                return;
            }
            tie_pwd_login_df.requestFocus();
            KeyboardUtils.hideSoftInput(tie_pwd_login_df);

            binding.btnLoginCommit.showProgress();

            new UserManagerModel().login(uid, pwd, new UserManagerModel.OnLoginListener() {
                @Override
                public void loginSuccess(User user) {
                    ToastTool.success("登录成功");
                    MySpUtils.save("login_uid", uid);
                    MySpUtils.save("login_pwd", pwd);
                    binding.btnLoginCommit.showSucceed();
                    ThreadUtils.runOnUiThreadDelayed(()->{
                        onLoginClickListener.loginSuccess(user);
                        dismiss();
                    },autoLogin?0:1000);
                }

                @Override
                public void loginError(String e) {
                    binding.btnLoginCommit.showError(1000);
                    ToastTool.error(e);
                }
            });
        });
        binding.tvForgetpwd.setOnClickListener(v -> {
            assert getFragmentManager() != null;
            new ForgetPwdDialogFragment().show(getFragmentManager(),"ForgetPwdDialogFragment");
            dismiss();
        });
        binding.tvRegister.setOnClickListener(v -> {
            assert getFragmentManager() != null;
            new RegisterDialogFragment().setOnLoginClickListener(onLoginClickListener).show(getFragmentManager(),"RegisterDialogFragment");
            dismiss();
        });
    }

    @Override
    protected void initData() {
        tie_uid_login_df.setText(MySpUtils.getString("login_uid"));
        tie_pwd_login_df.setText(MySpUtils.getString("login_pwd"));
        //setCancelable(false);
        tie_uid_login_df.requestFocus();
        //KeyboardUtils.hideSoftInput(tie_uid_login_df);
        if(autoLogin){
            binding.btnLoginCommit.performClick();
        }else {
            // 延迟打开软键盘
            new Timer().schedule(new TimerTask() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }

            }, 200);//n秒后弹出
        }
    }
}

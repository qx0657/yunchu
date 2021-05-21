package fun.qianxiao.yunchu.ui.userinfo.view;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ThreadUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.base.BaseDialogFragment;
import fun.qianxiao.yunchu.databinding.DialogFragmentResetpwdBinding;
import fun.qianxiao.yunchu.eventbus.EBLoginExpire;
import fun.qianxiao.yunchu.model.OperateListener;
import fun.qianxiao.yunchu.model.UserInfoModel;
import fun.qianxiao.yunchu.utils.MySpUtils;
import fun.qianxiao.yunchu.utils.ToastTool;

public class ResetPwdDialogFragment extends BaseDialogFragment<DialogFragmentResetpwdBinding> {

    @Override
    protected DialogFragmentResetpwdBinding getBinging() {
        return DialogFragmentResetpwdBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initListener() {
        binding.btnResetPwdCommit.setOnClickListener(v -> {
            /*String oldPwd = Objects.requireNonNull(binding.tieOldPwdResetPwdDf.getText()).toString();
            if(TextUtils.isEmpty(oldPwd)){
                binding.tieOldPwdResetPwdDf.requestFocus();
                ToastTool.warning("请输入原密码");
                binding.tilOldPwdResetPwdDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnResetPwdCommit.showError(2000);
                return;
            }*/
            String newPwd = Objects.requireNonNull(binding.tieNewPwdResetPwdDf.getText()).toString();
            if(TextUtils.isEmpty(newPwd)){
                binding.tieNewPwdResetPwdDf.requestFocus();
                ToastTool.warning("请输入新密码");
                binding.tilNewPwdResetPwdDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnResetPwdCommit.showError(2000);
                return;
            }
            /*if(!oldPwd.equals(MySpUtils.getString("login_pwd"))){
                binding.tieOldPwdResetPwdDf.requestFocus();
                ToastTool.warning("原密码不正确");
                binding.tilOldPwdResetPwdDf.startAnimation(AnimationUtils.loadAnimation(context, R.anim.shake_anim));
                binding.btnResetPwdCommit.showError(2000);
                return;
            }*/
            KeyboardUtils.hideSoftInput(Objects.requireNonNull(getActivity()));
            new UserInfoModel().resetUserPwd(newPwd, new OperateListener() {
                @Override
                public void operateSuccess(String var1) {
                    MySpUtils.save("login_pwd",newPwd);
                    ToastTool.success("修改成功");
                    ThreadUtils.runOnUiThreadDelayed(()->{
                        binding.btnResetPwdCommit.showSucceed();
                        ThreadUtils.runOnUiThreadDelayed(()->{
                            dismiss();
                            ((Activity)context).finish();
                            ThreadUtils.runOnUiThreadDelayed(()->EventBus.getDefault().post(new EBLoginExpire(true)),500);
                        },1000);

                    },1000);
                }

                @Override
                public void operateError(String e) {
                    ToastTool.error(e);
                    binding.btnResetPwdCommit.showError(2000);
                }
            });
        });
    }

    @Override
    protected void initData() {
        binding.tieNewPwdResetPwdDf.requestFocus();
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
}

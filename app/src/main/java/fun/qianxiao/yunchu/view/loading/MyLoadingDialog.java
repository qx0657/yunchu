package fun.qianxiao.yunchu.view.loading;

import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ScreenUtils;

import java.util.Objects;

import fun.qianxiao.yunchu.base.BaseAlertDialog;
import fun.qianxiao.yunchu.databinding.DialogLoadingBinding;


public class MyLoadingDialog extends BaseAlertDialog<DialogLoadingBinding> {
    private String message = "加载中";

    public MyLoadingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected DialogLoadingBinding getBinging() {
        return DialogLoadingBinding.inflate(getLayoutInflater());
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void updateMessage(String message) {
        this.message = message;
        if(binding==null){
            return;
        }
        if(binding.tvMessageDialogloading==null){
            return;
        }
        binding.tvMessageDialogloading.setText(message);
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData() {
        Objects.requireNonNull(this.getWindow()).setGravity(Gravity.CENTER);
        this.setCancelable(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Window window = getWindow();
        assert window != null;
        WindowManager.LayoutParams params = window.getAttributes();
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;//显示dialog的时候,就显示软键盘
        params.width = (int) (ScreenUtils.getScreenWidth() * 0.35);
        params.height = params.width;
        window.setAttributes(params);
    }

    @Override
    public void show() {
        super.show();
        binding.tvMessageDialogloading.setText(message);
    }
}

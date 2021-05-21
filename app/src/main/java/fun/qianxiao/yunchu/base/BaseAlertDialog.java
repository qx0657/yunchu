package fun.qianxiao.yunchu.base;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.viewbinding.ViewBinding;

import java.util.Objects;

/**
 * Create by QianXiao
 * On 2021/4/6
 */
public abstract class BaseAlertDialog<T extends ViewBinding> extends AlertDialog {
    protected Context context;
    protected View view;
    protected T binding;

    public BaseAlertDialog(@NonNull Context context) {
        super(context);
        this.context = context;
        Objects.requireNonNull(getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        getWindow().setBackgroundDrawable(dw);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getBinging();
        view = binding.getRoot();
        setContentView(view);
        initListener();
        initData();
    }

    protected abstract T getBinging();

    protected abstract void initListener();

    protected abstract void initData();
}

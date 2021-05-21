package fun.qianxiao.yunchu.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

public abstract class BaseFragment<T extends ViewBinding> extends Fragment {
    protected Context context;
    protected T binding;
    protected View mRootView;

    private boolean mIsInitData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            binding = getBinding();
            mRootView = binding.getRoot();
        }
        return mRootView;
    }

    protected abstract T getBinding();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        if (!isLazyLoad()) {
            fetchData();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchData();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    private void fetchData() {
        if (mIsInitData)
            return;
        initListener();
        initData();
        mIsInitData = true;
    }

    protected abstract void initListener();

    protected void initData() {
    }

    /**
     * 是否懒加载
     */
    protected boolean isLazyLoad() {
        return false;
    }

}

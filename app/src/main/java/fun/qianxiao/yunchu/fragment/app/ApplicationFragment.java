package fun.qianxiao.yunchu.fragment.app;

import fun.qianxiao.yunchu.base.BaseFragment;
import fun.qianxiao.yunchu.databinding.FragmentAppBinding;
import fun.qianxiao.yunchu.databinding.FragmentFileBinding;

public class ApplicationFragment extends BaseFragment<FragmentAppBinding> {

    @Override
    protected FragmentAppBinding getBinding() {
        return FragmentAppBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initListener() {
    }

    @Override
    protected void initData() {
    }
}

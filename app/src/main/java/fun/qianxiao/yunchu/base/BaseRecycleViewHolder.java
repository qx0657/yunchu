package fun.qianxiao.yunchu.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

/**
 * Create by QianXiao
 * On 2021/4/6
 */
public abstract class BaseRecycleViewHolder<T extends ViewBinding> extends RecyclerView.ViewHolder {
    protected View view;
    protected T binding;

    public BaseRecycleViewHolder(@NonNull View itemView) {
        super(itemView);
        this.view = itemView;
        binding = getBinding();
    }

    protected abstract T getBinding();
}

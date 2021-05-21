package fun.qianxiao.yunchu.ui.main.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import fun.qianxiao.yunchu.base.AppAdapter;

public class TestAdapter extends AppAdapter<String> {
    public TestAdapter(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder();
    }

    class ViewHolder extends SimpleHolder{

        public ViewHolder() {
            super(0);
        }

        @Override
        public void onBindView(int position) {

        }
    }
}

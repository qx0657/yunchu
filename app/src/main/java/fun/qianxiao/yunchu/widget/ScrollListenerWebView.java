package fun.qianxiao.yunchu.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import fun.qianxiao.yunchu.lunzige.widget.BrowserView;

public class ScrollListenerWebView extends BrowserView {
    public ScrollInterface mScrollInterface;

    public ScrollListenerWebView(@NonNull Context context) {
        super(context);
    }

    public ScrollListenerWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollListenerWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ScrollListenerWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //实时滑动监控
    //参数l代表滑动后当前位置，old代表原来原值
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        mScrollInterface.onSChanged(l, t, oldl, oldt);
    }
    //供外部调用，监控滑动
    public void setOnCustomScroolChangeListener(ScrollInterface scrollInterface) {
        this.mScrollInterface = scrollInterface;
    }
    public interface ScrollInterface {
        public void onSChanged(int l, int t, int oldl, int oldt);
    }
}

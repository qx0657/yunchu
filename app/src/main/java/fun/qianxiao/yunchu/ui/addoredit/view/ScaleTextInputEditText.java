package fun.qianxiao.yunchu.ui.addoredit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

public class ScaleTextInputEditText extends TextInputEditText {
    //双指缩放 实现
    private int textSize = 0;
    private float oldDist = 0;
    private int mode = 0;

    public ScaleTextInputEditText(@NonNull @NotNull Context context) {
        this(context, null);
    }

    public ScaleTextInputEditText(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleTextInputEditText(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()&MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = 1;
                break;

            case MotionEvent.ACTION_UP:
                mode = 0;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mode += 1;
                oldDist = spacing(event);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mode -= 1;
                break;

            case MotionEvent.ACTION_MOVE:
                getParent().requestDisallowInterceptTouchEvent(true);
                if (mode == 2) {
                    float newDist = spacing(event);

                    if (newDist > oldDist+1) {  //放大
                        zoomOut();
                        oldDist = newDist;
                    }
                    if (newDist < oldDist-1) {      //缩小
                        zoomIn();
                        oldDist = newDist;
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 计算两个点的距离
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) Math.sqrt(x * x + y * y);
        } else {
            return 0;
        }
    }


    float textSize0 = 16;
    float MIN_TEXT_SIZE = 8;
    float MAX_TEXT_SIZE = 50;

    /**
     * 放大
     */
    protected void zoomOut()
    {
        textSize0 += 1;
        if (textSize0 > MAX_TEXT_SIZE)
        {
            textSize0 = MAX_TEXT_SIZE;
        }
        setTextSize(textSize0);
    }


    /**
     * 缩小
     */
    protected void zoomIn()
    {
        textSize0 -= 1;
        if (textSize0 < MIN_TEXT_SIZE)
        {
            textSize0 = MIN_TEXT_SIZE;
        }
        setTextSize(textSize0);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}

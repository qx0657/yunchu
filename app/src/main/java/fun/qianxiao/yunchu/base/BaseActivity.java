package fun.qianxiao.yunchu.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;

import com.hjq.base.action.HandlerAction;

import fun.qianxiao.yunchu.R;
import fun.qianxiao.yunchu.utils.MySpUtils;


public abstract class BaseActivity<T extends ViewBinding> extends AppCompatActivity implements HandlerAction {
    protected Context context;
    protected T binding;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化主题
        themeInit();
        int id = getLayoutId();
        if(id!=0){
            setContentView(id);
        }else {
            binding = getBinding();
            if(binding != null){
                setContentView(binding.getRoot());
            }else {
                throw new IllegalStateException("请设置布局id或传入binding泛型(重写getLayoutId或getBinding方法)");
            }
        }
        context = this;
        Toolbar toolbar = findViewById(R.id.toolBar);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        initListener();
        initData();
    }

    /**
     * 以下两个方法必须重写其中一个
     * @return
     */
    protected T getBinding(){
        return null;
    }

    protected int getLayoutId() {
        return 0;
    }

    protected abstract void initListener();

    protected abstract void initData();

    /**
     * 动态获取主题的ColorPrimary
     * @return
     */
    public int getColorPrimaryId(){
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.resourceId;
    }

    private void themeInit() {
        int themeindex = MySpUtils.getInt("theme");
        int themeid = R.style.AppTheme1;
        switch (themeindex){
            case 1:
                themeid = R.style.AppTheme2;
                break;
            case 2:
                themeid = R.style.AppTheme3;
                break;
            case 3:
                themeid = R.style.AppTheme4;
                break;
            case 4:
                themeid = R.style.AppTheme5;
                break;
            case 5:
                themeid = R.style.AppTheme6;
                break;
            case 6:
                themeid = R.style.AppTheme7;
                break;
            default:
                break;
        }
        setTheme(themeid);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

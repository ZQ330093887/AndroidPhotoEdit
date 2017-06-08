package injection.sw.com.mycocularlater.imagepicker.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import injection.sw.com.mycocularlater.R;
import injection.sw.com.mycocularlater.imagepicker.view.SwipeBackLayout;


/**
 * Created by zhouqiong on 2017/6/5.
 */
public class TranslucentActivity extends FragmentActivity {
    private SwipeBackLayout blSwipeBackLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setBackgroundDrawableResource(R.color.transparent);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initSlideBack();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initSlideBack();
    }

    protected void onResume() {
        super.onResume();
        blSwipeBackLayout.dealWithNightMode();
    }

    /**
     * set sliding mode
     *
     * @param mode
     */
    public void setSlidingMode(SwipeBackLayout.Sliding mode) {
        if (blSwipeBackLayout == null) {
            throw new NullPointerException("ATSwipeBackLayout is null,Please call after the setContentView");
        }
        blSwipeBackLayout.setSlidingMode(mode);
    }

    @SuppressLint("InflateParams")
    private void initSlideBack() {
        blSwipeBackLayout = (SwipeBackLayout) LayoutInflater.from(this).inflate(R.layout.swipeback_layout, null);
        blSwipeBackLayout.attachToActivity(this);
    }

    public void setEnableGesture(boolean enableGesture) {
        Log.d("TAG", "================> thie enable gesture is ===" + enableGesture);
        blSwipeBackLayout.setEnableGesture(enableGesture);
    }
}

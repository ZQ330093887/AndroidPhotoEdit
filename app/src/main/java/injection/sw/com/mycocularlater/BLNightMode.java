package injection.sw.com.mycocularlater;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhouqiong on 2017/6/5.
 */
public class BLNightMode {
    public static final String TAG_NIGH = "ATNightMode";

    private boolean mIsMode = false;
    private int mValue = 150;

    private static BLNightMode mBLNightMode;

    private BLNightMode() {
    }

    public static BLNightMode getInstance() {
        if (mBLNightMode == null) {
            mBLNightMode = new BLNightMode();
        }
        return mBLNightMode;
    }

    public void init(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        mIsMode = sp.getBoolean("useNightMode", false);
        mValue = sp.getInt("nightModeBrightness", 150);
    }

    public void setInNightMode(boolean isNightMode) {
        this.mIsMode = isNightMode;
    }

    public boolean getInNightMode() {
        return mIsMode;
    }

    public void setNightValue(int value) {
        this.mValue = value;
    }

    public void update(Activity activity) {
        Activity parentActivity = activity.getParent();
        if (parentActivity == null) {
            if (mIsMode) {
                int color = mValue << 24;
                ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
                View nightView = decorView.findViewWithTag(TAG_NIGH);
                if (nightView == null) {
                    nightView = new View(activity);
                    nightView.setTag(TAG_NIGH);
                    nightView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
                    decorView.addView(nightView);
                }
                nightView.setBackgroundColor(color);
            } else {
                stop(activity);
            }
        }
    }

    public void update(Dialog dialog) {
        if (mIsMode) {
            int color = mValue << 24;
            ViewGroup decorView = (ViewGroup) dialog.getWindow().getDecorView();
            View nightView = decorView.findViewWithTag(TAG_NIGH);
            if (nightView == null) {
                nightView = new View(dialog.getContext());
                nightView.setTag(TAG_NIGH);
                nightView.setBackgroundColor(color);
                nightView.setLayoutParams(new ViewGroup.LayoutParams(decorView.getWidth(), decorView.getHeight()));
                decorView.addView(nightView);
            } else {
                nightView.setBackgroundColor(color);
            }
        } else {
            stop(dialog);
        }
    }

    public void stop(Activity activity) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        View nightView = decorView.findViewWithTag(TAG_NIGH);
        if (nightView != null) {
            decorView.removeView(nightView);
        }
    }

    public void stop(Dialog dialog) {
        ViewGroup decorView = (ViewGroup) dialog.getWindow().getDecorView();
        View nightView = decorView.findViewWithTag(TAG_NIGH);
        if (nightView != null) {
            decorView.removeView(nightView);
        }
    }

}

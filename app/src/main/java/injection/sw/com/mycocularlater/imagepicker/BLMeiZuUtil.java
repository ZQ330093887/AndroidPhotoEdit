package injection.sw.com.mycocularlater.imagepicker;

import android.os.Build;

/**
 * Created by zhouqiong on 2017/6/5.
 */
public class BLMeiZuUtil {
    public static boolean isMeizuMx2OrHigher() {
        if (Build.VERSION.SDK_INT < 14)
            return false;

        String model = Build.MODEL;
        return model.equals("M040") || model.equals("M045") || model.startsWith("M35") || model.startsWith("M46")
                || model.equalsIgnoreCase("MX4") || model.equals("MX4 Pro")
                || model.equals("m1 note") || model.equals("m1");
    }
}

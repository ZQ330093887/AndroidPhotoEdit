package injection.sw.com.mycocularlater.imagepicker.loader;

import android.app.Activity;
import android.widget.ImageView;

import java.io.Serializable;
/**
 * Created by zhouqiong on 2017/6/5.
 */
public interface ImageLoader extends Serializable {

    void displayImage(Activity activity, String path, ImageView imageView, boolean isScale);

    void clearMemoryCache();
}

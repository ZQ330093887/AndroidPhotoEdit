package injection.sw.com.mycocularlater.imagepicker.loader;

import android.app.Activity;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import injection.sw.com.mycocularlater.R;
/**
 * Created by zhouqiong on 2017/6/5.
 */
public class GlideImageLoader implements ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, boolean isScale) {
        if (isScale){
            Glide.with(activity)                             //配置上下文
                    .load(Uri.fromFile(new File(path)))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .centerCrop()
                    .placeholder(R.mipmap.default_image)
                    .into(imageView);
        }else {
            Glide.with(activity)                             //配置上下文
                    .load(Uri.fromFile(new File(path)))      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                    .placeholder(R.mipmap.default_image)
                    .into(imageView);
        }
    }

    @Override
    public void clearMemoryCache() {
    }
}

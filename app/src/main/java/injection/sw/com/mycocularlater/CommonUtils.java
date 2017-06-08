package injection.sw.com.mycocularlater;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
/**
 * Created by zhouqiong on 2017/6/5.
 */

public class CommonUtils{
        public static final int REQUEST_CODE_ALBUN = 101;

        /* 边框 */
        public static final int PHOTO_FRAME_WITH_DATA = 3024;

        /* 马赛克 */
        public static final int PHOTO_MOSAIC_WITH_DATA = 3025;

        /* 涂鸦 */
        public static final int PHOTO_DRAW_WITH_DATA = 3026;

        /* 剪切 */
        public static final int PHOTO_CROP_WITH_DATA = 3027;

        /* 滤镜 */
        public static final int PHOTO_FILTER_WITH_DATA = 3028;

        /* 增强 */
        public static final int PHOTO_ENHANCE_WITH_DATA = 3029;

        /* 旋转 */
        public static final int PHOTO_REVOLVE_WITH_DATA = 3030;

        /* 图像变形 */
        public static final int PHOTO_WARP_WITH_DATA = 3031;

        /* 添加水印图片 */
        public static final int PHOTO_ADD_WATERMARK_DATA = 3032;
        /* 添加文字 */
        public static final int PHOTO_ADD_TEXT_DATA = 3033;

        /**
         * Must be serialized object must be serialized object
         *
         * @param src
         * @param <T>
         * @return
         * @throws IOException
         * @throws ClassNotFoundException
         */
        public static <T> T deepCopy(T src) throws IOException, ClassNotFoundException {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(byteOut);
                out.writeObject(src);

                ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
                ObjectInputStream in = new ObjectInputStream(byteIn);
                T copyArray = (T) in.readObject();
                return copyArray;
        }
}

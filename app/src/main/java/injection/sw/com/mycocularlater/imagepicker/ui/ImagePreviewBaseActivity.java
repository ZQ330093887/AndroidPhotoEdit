package injection.sw.com.mycocularlater.imagepicker.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import injection.sw.com.mycocularlater.CommonUtils;
import injection.sw.com.mycocularlater.R;
import injection.sw.com.mycocularlater.imagepicker.ImagePicker;
import injection.sw.com.mycocularlater.imagepicker.adapter.ImagePageAdapter;
import injection.sw.com.mycocularlater.imagepicker.bean.ImageItem;
import injection.sw.com.mycocularlater.imagepicker.view.ViewPagerFixed;
/**
 * Created by zhouqiong on 2017/6/5.
 */
public abstract class ImagePreviewBaseActivity extends TranslucentActivity {

    protected ImagePicker imagePicker;
    protected ArrayList<ImageItem> mImageItems;      //跳转进ImagePreviewFragment的图片文件夹
    protected int mCurrentPosition = 0;              //跳转进ImagePreviewFragment时的序号，第几个图片
    protected TextView mTitleCount;                  //显示当前图片的位置  例如  5/31
    protected ArrayList<ImageItem> selectedImages;   //所有已经选中的图片
    protected View content;
    protected View topBar;
    protected ViewPagerFixed mViewPager;
    protected ImagePageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        mCurrentPosition = getIntent().getIntExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
        imagePicker = ImagePicker.getInstance();
        try {
            mImageItems = CommonUtils.deepCopy(getIntent().getBooleanExtra(ImagePicker.IS_PREVIEW, false) ? imagePicker.getSelectedImages() : imagePicker.getCurrentImageFolderItems());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            mImageItems = new ArrayList<>();
        }
        selectedImages = imagePicker.getSelectedImages();

        //初始化控件
        content = findViewById(R.id.content);

        //因为状态栏透明后，布局整体会上移，所以给头部加上状态栏的margin值，保证头部不会被覆盖
        topBar = findViewById(R.id.top_bar);
        topBar.findViewById(R.id.btn_ok).setVisibility(View.GONE);
        topBar.findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleCount = (TextView) findViewById(R.id.tv_des);
        mViewPager = (ViewPagerFixed) findViewById(R.id.viewpager);
        mAdapter = new ImagePageAdapter(this, mImageItems);
        mAdapter.setPhotoViewClickListener(new ImagePageAdapter.PhotoViewClickListener() {
            @Override
            public void OnPhotoTapListener(View view, float v, float v1) {
                onImageSingleTap();
            }
        });
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentPosition, false);

        //初始化当前页面的状态
        mTitleCount.setText(getString(R.string.preview_image_count, String.valueOf(mCurrentPosition + 1), String.valueOf(mImageItems.size())));
    }

    /**
     * 单击时，隐藏头和尾
     */
    public abstract void onImageSingleTap();
}
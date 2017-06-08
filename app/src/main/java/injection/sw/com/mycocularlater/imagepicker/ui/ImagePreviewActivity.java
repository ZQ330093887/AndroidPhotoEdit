package injection.sw.com.mycocularlater.imagepicker.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import injection.sw.com.mycocularlater.R;
import injection.sw.com.mycocularlater.imagepicker.ImagePicker;
import injection.sw.com.mycocularlater.imagepicker.bean.ImageItem;
import injection.sw.com.mycocularlater.imagepicker.view.SuperCheckBox;

/**
 * Created by zhouqiong on 2017/6/5.
 */
public class ImagePreviewActivity extends ImagePreviewBaseActivity implements ImagePicker.OnImageSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public static final String DATA_OF_IMAGE_PATH = "imgpath";
    public static final String ISORIGIN = "isOrigin";
    public static final int IMAGE_EDIT_CODE = 0x01;
    private boolean isOrigin;                                     //是否选中原图
    private SuperCheckBox mCbCheck;               //是否选中当前图片的CheckBox
    private SuperCheckBox mCbOrigin;                //原图
    private Button mBtnOk;                                      //确认图片的选择
    private ImageView mBtnBack;
    private View bottomBar;
    private LinearLayout photoContainer, rootBottomView;
    private TextView editTextView;
    private LinearLayout.LayoutParams params;
    private LinkedHashMap<Integer, ImageItem> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setUpView();
        initPagerConfig();
        initListener();
        initBottomPhotoView();
    }

    /**
     * 图片添加成功后，修改当前图片的选中数量
     * 当调用 addSelectedImageItem 或 deleteSelectedImageItem 都会触发当前回调
     */
    @Override
    public void onImageSelected(int position, ImageItem item, boolean isAdd) {
        boolean hasSelectImage = imagePicker.getSelectImageCount() > 0;
        mBtnOk.setText(hasSelectImage ? getString(R.string.select_complete, String.valueOf(imagePicker.getSelectImageCount()), String.valueOf(imagePicker.getSelectLimit())) : getString(R.string.complete));
        if (mCbOrigin.isChecked()) {//if the origin is selected show image size
            long size = 0;
            for (ImageItem imageItem : selectedImages)
                size += imageItem.size;
            String fileSize = Formatter.formatFileSize(this, size);
            mCbOrigin.setText(getString(R.string.origin_size, fileSize));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            if (!(imagePicker.getSelectImageCount() > 0)) {
                mCbCheck.setChecked(true);
                imagePicker.addSelectedImageItem(mCurrentPosition, imagePicker.getCurrentImageFolderItems().get(mCurrentPosition), mCbCheck.isChecked());
            }
            Intent intent = new Intent();
            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
            setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
            //发送之前通知系统相册更新
            //sendBoareCast(imagePicker.getSelectedImages());
            finish();
        } else if (id == R.id.btn_back) {
            Intent intent = new Intent();
            intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
            setResult(ImagePicker.RESULT_CODE_BACK, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
        setResult(ImagePicker.RESULT_CODE_BACK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.cb_origin) {
            if (isChecked) {
                long size = 0;
                for (ImageItem item : selectedImages) size += item.size;
                String fileSize = Formatter.formatFileSize(this, size);
                mCbOrigin.setText(getString(R.string.origin_size, fileSize));
            } else {
                mCbOrigin.setText(getString(R.string.origin));
            }
            isOrigin = isChecked;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (IMAGE_EDIT_CODE == requestCode && Activity.RESULT_OK == resultCode) {
            try {
                String imgPath = data.getStringExtra(DATA_OF_IMAGE_PATH);
                if (TextUtils.isEmpty(imgPath)) return;
                resetImagePath(imgPath, imagePicker.getCurrentImageFolderItems());
                resetImagePath(imgPath, imagePicker.getSelectedImages());
                mImageItems.get(mCurrentPosition).path = imgPath;
                mAdapter.notifyDataSetChanged();
                initBottomPhotoView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * reset the path of imageItem
     *
     * @param imgPath new image path
     * @param list    list of ImageItem
     */
    private void resetImagePath(String imgPath, ArrayList<ImageItem> list) {
        for (ImageItem it : list) {
            if (it.path.equals(mImageItems.get(mCurrentPosition).path)) {
                it.path = imgPath;
            }
        }
    }

    @Override
    protected void onDestroy() {
        imagePicker.removeOnImageSelectedListener(this);
        super.onDestroy();
    }

    /**
     * 单击时，隐藏头和尾
     */
    @Override
    public void onImageSingleTap() {
        if (topBar.getVisibility() == View.VISIBLE) {
            showToolBar(AnimationUtils.loadAnimation(this, R.anim.top_out), AnimationUtils.loadAnimation(this, R.anim.fade_out), View.GONE);
        } else {
            showToolBar(AnimationUtils.loadAnimation(this, R.anim.top_in), AnimationUtils.loadAnimation(this, R.anim.fade_in), View.VISIBLE);
        }
    }

    private void showToolBar(Animation animation, Animation animation2, int visible) {
        topBar.setAnimation(animation);
        bottomBar.setAnimation(animation2);
        topBar.setVisibility(visible);
        bottomBar.setVisibility(visible);
        if (hashMap != null && hashMap.size() > 0) {
            photoContainer.setVisibility(visible);
            rootBottomView.setVisibility(visible);
        }
    }

    private void initListener() {
        //滑动ViewPager的时候，根据外界的数据改变当前的选中状态和当前的图片的位置描述文本
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                onPageSelectedEvent(position);
            }
        });
        //当点击当前选中按钮的时候，需要根据当前的选中状态添加和移除图片
        mCbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrRemovePhoto();
            }
        });
        editTextView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                if (hashMap != null) {
                    startCompile(hashMap.get(mCurrentPosition) == null ? "" : hashMap.get(mCurrentPosition).path);
                }
            }
        });

    }

    /**
     * add or remove photo in selectedImages
     */
    private void addOrRemovePhoto() {
        int selectLimit = imagePicker.getSelectLimit();
        if (mCbCheck.isChecked() && selectedImages.size() >= selectLimit) {
            Toast.makeText(ImagePreviewActivity.this, ImagePreviewActivity.this.getString(R.string.select_limit, String.valueOf(selectLimit)), Toast.LENGTH_SHORT).show();
            mCbCheck.setChecked(false);
        } else {
            imagePicker.addSelectedImageItem(mCurrentPosition, getIntent().getBooleanExtra(ImagePicker.IS_PREVIEW, false) ? mImageItems.get(mCurrentPosition) : imagePicker.getCurrentImageFolderItems().get(mCurrentPosition), mCbCheck.isChecked());
            initBottomPhotoView();
        }
    }

    /**
     * on pager change event
     *
     * @param position pager position
     */
    private void onPageSelectedEvent(int position) {
        mCurrentPosition = position;
        ImageItem item = mImageItems.get(mCurrentPosition);
        boolean isSelected = imagePicker.isSelect(item);
        mCbCheck.setChecked(isSelected);
        mTitleCount.setText(getString(R.string.preview_image_count, String.valueOf(mCurrentPosition + 1), String.valueOf(mImageItems.size())));
        resetImageSelectedStatus(mCurrentPosition);
        editTextView.setVisibility(imagePicker.isSelect(item) ? View.VISIBLE : View.GONE);
    }

    /**
     * init current pager status
     */
    private void initPagerConfig() {
        //初始化当前页面的状态
        onImageSelected(0, null, false);
        ImageItem item = mImageItems.get(mCurrentPosition);
        boolean isSelected = imagePicker.isSelect(item);
        mTitleCount.setText(getString(R.string.preview_image_count, String.valueOf(mCurrentPosition + 1), String.valueOf(mImageItems.size())));
        mCbCheck.setChecked(isSelected);
    }

    private void initData() {
        hashMap = new LinkedHashMap<>();
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = 15;
        setEnableGesture(false);
        isOrigin = getIntent().getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
    }

    private void setUpView() {
        imagePicker.addOnImageSelectedListener(this);
        mBtnOk = (Button) topBar.findViewById(R.id.btn_ok);
        mBtnBack = (ImageView) topBar.findViewById(R.id.btn_back);
        mBtnOk.setVisibility(View.VISIBLE);
        mBtnOk.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setVisibility(View.VISIBLE);
        photoContainer = (LinearLayout) findViewById(R.id.photo_container);
        rootBottomView = (LinearLayout) findViewById(R.id.root_bottom_view);
        editTextView = (TextView) findViewById(R.id.edit);
        mCbCheck = (SuperCheckBox) findViewById(R.id.cb_check);
        mCbOrigin = (SuperCheckBox) findViewById(R.id.cb_origin);
        mCbOrigin.setText(getString(R.string.origin));
        mCbOrigin.setOnCheckedChangeListener(this);
        mCbOrigin.setChecked(isOrigin);
    }

    /**
     * to ComplieBitmapFragment and edit photo
     *
     * @param file image file path
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void startCompile(String file) {
        if (TextUtils.isEmpty(file)) return;

        Intent intent = new Intent(ImagePreviewActivity.this, CompileBitmapActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putString("bitmap", file);
        intent.putExtras(mBundle);
        startActivityForResult(intent, IMAGE_EDIT_CODE);
    }

    /**
     * init bottom photo view and load image into imageView
     */
    private void initBottomPhotoView() {
        hashMap = new LinkedHashMap<>();
        for (int i = 0; i < mImageItems.size(); i++) {
            if (selectedImages.contains(mImageItems.get(i))) {
                hashMap.put(i, mImageItems.get(i));
            }
        }

        if (photoContainer == null) return;
        photoContainer.setVisibility(hashMap.size() == 0 ? View.GONE : View.VISIBLE);
        rootBottomView.setVisibility(hashMap.size() == 0 ? View.GONE : View.VISIBLE);
        photoContainer.removeAllViews();
        int size = mImageItems.size();
        for (int i = 0; i < size; i++) {
            final View view = LayoutInflater.from(this).inflate(R.layout.preview_bottom_image, null, false);
            ImageView image = (ImageView) view.findViewById(R.id.image);
            View space = new View(this);
            space.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
            if (hashMap.containsValue(mImageItems.get(i))) {// is image in this map
                for (Map.Entry<Integer, ImageItem> integerImageItemEntry : hashMap.entrySet()) {//set listener into image view (because it need right position)
                    final int key = integerImageItemEntry.getKey();
                    if (integerImageItemEntry.getValue().path.equals(mImageItems.get(i).path)) {//if is the same photo set listener into view
                        image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                bottomViewClickEvent(view, key);
                            }
                        });
                        addViewIntoBottomView(image, integerImageItemEntry.getValue().path, view);//add view into bottom view
                    }
                }
            }
            if (!hashMap.containsValue(mImageItems.get(i))) {
                addViewIntoBottomView(null, null, space);// add space into bottonm view
            }
        }
        resetImageSelectedStatus(mCurrentPosition);//reset the bottom view status
    }

    /**
     * Bottom view click event
     *
     * @param view   view root
     * @param finalI position
     */
    private void bottomViewClickEvent(View view, int finalI) {
        mViewPager.setCurrentItem(finalI, true);
    }

    /**
     * Add Image into bottom view
     *
     * @param image target ImageView
     * @param path  imageView path
     * @param view  view root /container
     */
    private void addViewIntoBottomView(ImageView image, String path, View view) {
        if (image != null && !TextUtils.isEmpty(path)) {
            Glide.with(this)
                    .load(Uri.fromFile(new File(path)))
                    .centerCrop()
                    .placeholder(R.mipmap.default_image)
                    .into(image);
            view.setLayoutParams(params);
        }
        photoContainer.addView(view);
    }


    /**
     * reset  imageview status
     *
     * @param position current  photo position
     */
    private void resetImageSelectedStatus(int position) {
        editTextView.setVisibility((hashMap == null || hashMap.size() == 0) ? View.GONE : View.VISIBLE);
        if (photoContainer == null) return;
        for (int i = 0; i < photoContainer.getChildCount(); i++) {
            photoContainer.getChildAt(i).setSelected(position == i);
        }
    }
}

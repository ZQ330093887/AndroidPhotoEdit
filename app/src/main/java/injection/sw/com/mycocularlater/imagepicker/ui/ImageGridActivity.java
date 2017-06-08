package injection.sw.com.mycocularlater.imagepicker.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.List;

import injection.sw.com.mycocularlater.R;
import injection.sw.com.mycocularlater.imagepicker.ImageDataSource;
import injection.sw.com.mycocularlater.imagepicker.ImagePicker;
import injection.sw.com.mycocularlater.imagepicker.Utils;
import injection.sw.com.mycocularlater.imagepicker.adapter.ImageFolderAdapter;
import injection.sw.com.mycocularlater.imagepicker.adapter.ImageGridAdapter;
import injection.sw.com.mycocularlater.imagepicker.bean.ImageFolder;
import injection.sw.com.mycocularlater.imagepicker.bean.ImageItem;
import injection.sw.com.mycocularlater.imagepicker.view.SystemBarTintManager;

/**
 * Created by zhouqiong on 2017/6/5.
 */
public class ImageGridActivity extends TranslucentActivity implements ImageDataSource.OnImagesLoadedListener, ImageGridAdapter.OnImageItemClickListener, ImagePicker.OnImageSelectedListener, View.OnClickListener {

    public final int PERMISSIONS_REQUEST_CAMERA = 123;
    public String[] permissions = new String[]{Manifest.permission.CAMERA};

    private ImagePicker imagePicker;
    private boolean isOrigin = false;  //是否选中原图
    private int screenWidth;     //屏幕的宽
    private int screenHeight;    //屏幕的高
    private GridView mGridView;  //图片展示控件
    private View mTopBar;        //顶部栏
    private View mFooterBar;     //底部栏
    private Button mBtnOk;       //确定按钮
    private Button mBtnDir;      //文件夹切换按钮
    private Button mBtnPre;      //预览按钮
    private ImageFolderAdapter mImageFolderAdapter;    //图片文件夹的适配器
    private ListPopupWindow mFolderPopupWindow;  //ImageSet的PopupWindow
    private List<ImageFolder> mImageFolders;   //所有的图片文件夹
    private ImageGridAdapter mImageGridAdapter;  //图片九宫格展示的适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setEnableGesture(false);
        setContentView(R.layout.activity_image_grid);
        imagePicker = ImagePicker.getInstance();
        int size = getIntent().getIntExtra(ImagePicker.MAX_PHOTO_NUMBER, 0) > 4 ? 4 : getIntent().getIntExtra(ImagePicker.MAX_PHOTO_NUMBER, 0);

        imagePicker.setSelectLimit(size >= 0 ? size : 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintEnabled(true);
            mTintManager.setTintDrawable(getResources().getDrawable(R.color.colorPrimaryDark));
        }

        imagePicker.clear();
        imagePicker.addOnImageSelectedListener(this);
        DisplayMetrics dm = Utils.getScreenPix(this);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        findViewById(R.id.btn_back).setOnClickListener(this);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        mBtnDir = (Button) findViewById(R.id.btn_dir);
        mBtnDir.setOnClickListener(this);
        mBtnPre = (Button) findViewById(R.id.btn_preview);
        mBtnPre.setOnClickListener(this);
        mGridView = (GridView) findViewById(R.id.gridview);
        mTopBar = findViewById(R.id.top_bar);
        mFooterBar = findViewById(R.id.footer_bar);
        if (imagePicker.isMultiMode()) {
            mBtnOk.setVisibility(View.VISIBLE);
            mBtnPre.setVisibility(View.VISIBLE);
        } else {
            mBtnOk.setVisibility(View.GONE);
            mBtnPre.setVisibility(View.GONE);
        }
        mImageGridAdapter = new ImageGridAdapter(this, null);
        mImageFolderAdapter = new ImageFolderAdapter(this, null);
        onImageSelected(0, null, false);
        new ImageDataSource(this, null, this);
    }

    @Override
    protected void onDestroy() {
        imagePicker.removeOnImageSelectedListener(this);
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            Intent intent = new Intent();
            //所有选中的图片路径都存在imagePicker.getSelectedImages()中（返回一个ArrayList<ImageItem>）
            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
            setResult(ImagePicker.RESULT_CODE_ITEMS, intent);  //多选不允许裁剪裁剪，返回数据
            //遍历循环通知系统相册更新（比较耗电，谨慎使用）
            //sendBoareCast(imagePicker.getSelectedImages());
            finish();
        } else if (id == R.id.btn_dir) {
            if (mImageFolders == null) {
                Log.i("ImageGridActivity", "There is no photo in you phone ");
                return;
            }
            //点击文件夹按钮
            if (mFolderPopupWindow == null) createPopupFolderList(screenWidth, screenHeight);
            backgroundAlpha(0.8f);   //改变View的背景透明度
            mImageFolderAdapter.refreshData(mImageFolders);  //刷新数据
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            } else {
                mFolderPopupWindow.show();
                //默认选择当前选择的上一个，当目录很多时，直接定位到已选中的条目
                int index = mImageFolderAdapter.getSelectIndex();
                index = index == 0 ? index : index - 1;
                ListView listView = mFolderPopupWindow.getListView();
                if (listView != null) listView.setSelection(index);
            }
        } else if (id == R.id.btn_preview) {
            Intent intent = new Intent(ImageGridActivity.this, ImagePreviewActivity.class);
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
            intent.putExtra(ImagePicker.IS_PREVIEW, true);
            intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
            startActivityForResult(intent, ImagePicker.REQUEST_CODE_PREVIEW);
        } else if (id == R.id.btn_back) {
            //点击返回按钮
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageGridAdapter.notifyDataSetChanged();
    }

    /**
     * 创建弹出的ListView
     */
    private void createPopupFolderList(int width, int height) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mFolderPopupWindow = new ListPopupWindow(this);
            mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            mFolderPopupWindow.setAdapter(mImageFolderAdapter);
            mFolderPopupWindow.setContentWidth(width);
            mFolderPopupWindow.setWidth(width);  //如果不设置，就是 AnchorView 的宽度
            int maxHeight = height * 5 / 8;
            int realHeight = mImageFolderAdapter.getItemViewHeight() * mImageFolderAdapter.getCount();
            int popHeight = realHeight > maxHeight ? maxHeight : realHeight;
            mFolderPopupWindow.setHeight(popHeight);
            mFolderPopupWindow.setAnchorView(mFooterBar);  //ListPopupWindow总会相对于这个View
            mFolderPopupWindow.setModal(true);  //是否为模态，影响返回键的处理
            mFolderPopupWindow.setAnimationStyle(R.style.popupwindow_anim_style);
            mFolderPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    backgroundAlpha(1.0f);
                }
            });
            mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    mImageFolderAdapter.setSelectIndex(position);
                    imagePicker.setCurrentImageFolderPosition(position);
                    mFolderPopupWindow.dismiss();
                    ImageFolder imageFolder = (ImageFolder) adapterView.getAdapter().getItem(position);
                    if (null != imageFolder) {
                        mImageGridAdapter.refreshData(imageFolder.images);
                        mBtnDir.setText(imageFolder.name);
                    }
                    mGridView.smoothScrollToPosition(0);//滑动到顶部
                }
            });
        }
    }

    /**
     * 设置屏幕透明度  0.0透明  1.0不透明
     */
    public void backgroundAlpha(float alpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) return;
        mGridView.setAlpha(alpha);
        mTopBar.setAlpha(alpha);
        mFooterBar.setAlpha(1.0f);
    }

    @Override
    public void onImagesLoaded(List<ImageFolder> imageFolders) {
        this.mImageFolders = imageFolders;
        imagePicker.setImageFolders(imageFolders);
        if (imageFolders.size() == 0) mImageGridAdapter.refreshData(null);
        else mImageGridAdapter.refreshData(imageFolders.get(0).images);
        mImageGridAdapter.setOnImageItemClickListener(this);
        mGridView.setAdapter(mImageGridAdapter);
        mImageFolderAdapter.refreshData(imageFolders);
    }

    @Override
    public void onImageItemClick(View view, ImageItem imageItem, int position) {
        //根据是否有相机按钮确定位置
        position = imagePicker.isShowCamera() ? position - 1 : position;
        if (imagePicker.isMultiMode()) {
            Intent intent = new Intent(ImageGridActivity.this, ImagePreviewActivity.class);
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
            intent.putExtra(ImagePicker.IS_PREVIEW, false);
            intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
            startActivityForResult(intent, ImagePicker.REQUEST_CODE_PREVIEW);  //如果是多选，点击图片进入预览界面
        } else {
            imagePicker.clearSelectedImages();
            imagePicker.addSelectedImageItem(position, imagePicker.getCurrentImageFolderItems().get(position), true);
            if (imagePicker.isCrop()) {
                Intent intent = new Intent(ImageGridActivity.this, ImageCropActivity.class);
                startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);  //单选需要裁剪，进入裁剪界面
            } else {
                Intent intent = new Intent();
                intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
                setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
                finish();
            }
        }
    }

    @Override
    public void onImageSelected(int position, ImageItem item, boolean isAdd) {
        if (imagePicker.getSelectImageCount() > 0) {
            mBtnOk.setText(getString(R.string.select_complete, String.valueOf(imagePicker.getSelectImageCount()), String.valueOf(imagePicker.getSelectLimit())));
            mBtnOk.setEnabled(true);
            mBtnPre.setEnabled(true);
        } else {
            mBtnOk.setText(getString(R.string.complete));
            mBtnOk.setEnabled(false);
            mBtnPre.setEnabled(false);
        }
        mBtnPre.setText(getResources().getString(R.string.preview_count, String.valueOf(imagePicker.getSelectImageCount())));
        mImageGridAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == ImagePicker.RESULT_CODE_BACK) {
                isOrigin = data.getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
            } else {
                //从拍照界面返回
                //点击 X , 没有选择照片
                if (data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS) == null) {
                    //什么都不做
                } else {
                    //说明是从裁剪页面过来的数据，直接返回就可以
                    setResult(ImagePicker.RESULT_CODE_ITEMS, data);
                    finish();
                }
            }
        } else {
            //如果是裁剪，因为裁剪指定了存储的Uri，所以返回的data一定为null
            if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_TAKE) {
                //发送广播通知图片增加了
                ImagePicker.galleryAddPic(this, imagePicker.getTakeImageFile());
                ImageItem imageItem = new ImageItem();
                imageItem.path = imagePicker.getTakeImageFile().getAbsolutePath();
                imagePicker.clearSelectedImages();
                imagePicker.addSelectedImageItem(0, imageItem, true);
                if (imagePicker.isCrop()) {
                    Intent intent = new Intent(ImageGridActivity.this, ImageCropActivity.class);
                    startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);  //单选需要裁剪，进入裁剪界面
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
                    setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
                    finish();
                }
            }
            mImageGridAdapter.notifyDataSetChanged();
        }
    }

    public void startCamera() {
        if (!hasCameraPermission(this)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e("qy", "请求权限Camera权限");
                requestPermissions(permissions, PERMISSIONS_REQUEST_CAMERA);
            }
        } else {
            //用户已经授权或者版本在6.0以下
            ImagePicker.getInstance().takePicture(this, ImagePicker.REQUEST_CODE_TAKE);
        }
    }

    /**
     * 6.0获取权限
     *
     * @return
     */
    public boolean hasCameraPermission(Context mContext) {
        boolean hasPermission = true;
        if (Build.VERSION.SDK_INT >= 23) {
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(mContext, permissions[i]) != PackageManager.PERMISSION_GRANTED) {//判断用户是否对添加的权限授权
                    //用户没有授权
                    hasPermission = false;
                    break;
                }
            }
        } else {
            hasPermission = true;
        }
        return hasPermission;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean isOpen = true;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        //判断添加的权限用户是否授权
                        isOpen = false;
                        break;
                    }
                }
                if (isOpen) {
                    // Android 6.0 用户已经授权 执行授权之后的操作
                    ImagePicker.getInstance().takePicture(this, ImagePicker.REQUEST_CODE_TAKE);
                } else {
                    // 执行用户没有授权的操作
                    showToast("请开启相机权限后重试");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    protected void showToast(String msg) {
        if (null != msg && !TextUtils.isEmpty(msg)) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }
}

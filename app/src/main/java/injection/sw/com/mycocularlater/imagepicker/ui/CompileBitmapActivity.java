package injection.sw.com.mycocularlater.imagepicker.ui;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

import cn.jarlen.photoedit.activity.AddTextActivity;
import cn.jarlen.photoedit.activity.AddWatermarkActivity;
import cn.jarlen.photoedit.activity.DrawBaseActivity;
import cn.jarlen.photoedit.activity.EnhanceActivity;
import cn.jarlen.photoedit.activity.ImageFilterActivity;
import cn.jarlen.photoedit.activity.ImagePasteActivity;
import cn.jarlen.photoedit.activity.MosaicActivity;
import cn.jarlen.photoedit.activity.PhotoFrameActivity;
import cn.jarlen.photoedit.activity.RevolveActivity;
import cn.jarlen.photoedit.activity.WarpActivity;
import injection.sw.com.mycocularlater.CommonUtils;
import injection.sw.com.mycocularlater.R;
import injection.sw.com.mycocularlater.imagepicker.Utils;
import injection.sw.com.mycocularlater.imagepicker.adapter.CompileBitmapAdapter;

import static injection.sw.com.mycocularlater.imagepicker.ui.ImagePreviewActivity.DATA_OF_IMAGE_PATH;


/**
 * Created by zhouqiong on 2017/6/6.
 */

public class CompileBitmapActivity extends TranslucentActivity implements View.OnClickListener {

    private RecyclerView recyclerView;
    private CompileBitmapAdapter adapter;
    private Class<?> intentClass;
    private int intentType = 0;
    private String cameraPath = null;
    private ImageView pictureShow, backImageView;
    private TextView saveTextView;
    private String[] str;
    private FrameLayout bannerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compile_bitmap);
        initView();
        initData();
        setOnClickListener();
    }

    private void initView() {
        setArgument();
        recyclerView = (RecyclerView) findViewById(R.id.bottom_gallery);
        pictureShow = (ImageView) findViewById(R.id.pictureShow);
        backImageView = (ImageView) findViewById(R.id.back_btn);
        saveTextView = (TextView) findViewById(R.id.save_btn);
        bannerFragment = (FrameLayout) findViewById(R.id.banner);
        Glide.with(this).load(cameraPath).into(pictureShow);
    }

    private void initData() {
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        str = new String[]{"滤镜", "图像变形", "剪切", "涂鸦", "边框", "添加文字", "添加水印", "马赛克", "增强", "旋转"};
        recyclerView.setLayoutManager(linearLayoutManager);
        //设置适配器
        adapter = new CompileBitmapAdapter(this, str);
        recyclerView.setAdapter(adapter);
    }

    private void setOnClickListener() {
        backImageView.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        pictureShow.setOnClickListener(this);
        adapter.setOnItemClickListener(new CompileBitmapAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        intentClass = ImageFilterActivity.class;
                        intentType = CommonUtils.PHOTO_FILTER_WITH_DATA;
                        break;
                    case 1:
                        intentClass = WarpActivity.class;
                        intentType = CommonUtils.PHOTO_WARP_WITH_DATA;
                        break;
                    case 2:
                        intentClass = ImagePasteActivity.class;
                        intentType = CommonUtils.PHOTO_CROP_WITH_DATA;
                        break;
                    case 3:
                        intentClass = DrawBaseActivity.class;
                        intentType = CommonUtils.PHOTO_DRAW_WITH_DATA;
                        break;
                    case 4:
                        intentClass = PhotoFrameActivity.class;
                        intentType = CommonUtils.PHOTO_FRAME_WITH_DATA;
                        break;
                    case 5:
                        intentClass = AddTextActivity.class;
                        intentType = CommonUtils.PHOTO_ADD_TEXT_DATA;
                        break;
                    case 6:
                        intentClass = AddWatermarkActivity.class;
                        intentType = CommonUtils.PHOTO_ADD_WATERMARK_DATA;
                        break;
                    case 7:
                        intentClass = MosaicActivity.class;
                        intentType = CommonUtils.PHOTO_MOSAIC_WITH_DATA;
                        break;
                    case 8:
                        intentClass = EnhanceActivity.class;
                        intentType = CommonUtils.PHOTO_ENHANCE_WITH_DATA;
                        break;
                    case 9:
                        intentClass = RevolveActivity.class;
                        intentType = CommonUtils.PHOTO_REVOLVE_WITH_DATA;
                        break;
                    default:
                        intentClass = null;
                        intentType = 0;
                        break;
                }

                if (cameraPath == null) {
                    Toast.makeText(CompileBitmapActivity.this, "请选择图片",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (intentClass == null) {
                    Toast.makeText(CompileBitmapActivity.this, "请图片操作类型",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // 将图片路径photoPath传到所要调试的模块
                Intent photoFrameIntent = new Intent(CompileBitmapActivity.this, intentClass);
                photoFrameIntent.putExtra("camera_path", cameraPath);
                CompileBitmapActivity.this.startActivityForResult(photoFrameIntent, intentType);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case CommonUtils.PHOTO_FRAME_WITH_DATA:
            case CommonUtils.PHOTO_MOSAIC_WITH_DATA:
            case CommonUtils.PHOTO_DRAW_WITH_DATA:
            case CommonUtils.PHOTO_CROP_WITH_DATA:
            case CommonUtils.PHOTO_FILTER_WITH_DATA:
            case CommonUtils.PHOTO_ENHANCE_WITH_DATA:
            case CommonUtils.PHOTO_REVOLVE_WITH_DATA:
            case CommonUtils.PHOTO_WARP_WITH_DATA:
            case CommonUtils.PHOTO_ADD_WATERMARK_DATA:
            case CommonUtils.PHOTO_ADD_TEXT_DATA:
                cameraPath = data.getStringExtra("camera_path");
                Glide.with(this)
                        .load(Uri.fromFile(new File(cameraPath)))
                        .centerCrop()
                        .placeholder(R.mipmap.default_image)
                        .into(pictureShow);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back_btn) {
            finish();
        } else if (id == R.id.save_btn) {
            File file = Utils.saveBitmap(this, BitmapFactory.decodeFile(cameraPath), "" + SystemClock.currentThreadTimeMillis() + ".jpg");
            Intent mIntent = new Intent();
            mIntent.putExtra(DATA_OF_IMAGE_PATH, file.getAbsolutePath());
            this.setResult(RESULT_OK, mIntent);
            finish();
        } else if (id == R.id.pictureShow) {
            //点击图片，其他view隐藏
            onImageSingleTap();
        }
    }

    public void onImageSingleTap() {
        if (bannerFragment.getVisibility() == View.VISIBLE) {
            showToolBar(AnimationUtils.loadAnimation(this, R.anim.top_out), AnimationUtils.loadAnimation(this, R.anim.fade_out), View.GONE);
        } else {
            showToolBar(AnimationUtils.loadAnimation(this, R.anim.top_in), AnimationUtils.loadAnimation(this, R.anim.fade_in), View.VISIBLE);
        }
    }

    private void showToolBar(Animation animation, Animation animation2, int visible) {
        bannerFragment.setAnimation(animation);
        bannerFragment.setVisibility(visible);
        bannerFragment.setAnimation(animation);
        recyclerView.setAnimation(animation2);
        bannerFragment.setVisibility(visible);
        recyclerView.setVisibility(visible);
    }

    private void setArgument() {
        Bundle bundle = getIntent().getExtras();
        cameraPath = bundle.getString("bitmap");
    }
}

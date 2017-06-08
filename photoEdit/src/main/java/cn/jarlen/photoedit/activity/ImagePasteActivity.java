/*
 *          Copyright (C) 2016 jarlen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package cn.jarlen.photoedit.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import cn.jarlen.photoedit.R;
import cn.jarlen.photoedit.crop.CropImageType;
import cn.jarlen.photoedit.crop.CropImageView;
import cn.jarlen.photoedit.utils.FileUtils;

/**
 * 剪切
 *
 * Created by zhouqiong on 2017/6/5.
 */
public class ImagePasteActivity extends Activity implements Toolbar.OnMenuItemClickListener, View.OnClickListener {

    private Toolbar mToolbar;

    private CropImageView cropImage;

    private String mPath = null;

    private ImageButton cancleBtn, okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crop_image);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.inflateMenu(R.menu.menu_crop);
        mToolbar.setOnMenuItemClickListener(this);

        Intent intent = getIntent();
        mPath = intent.getStringExtra("camera_path");
        Bitmap bit = BitmapFactory.decodeFile(mPath);

        cropImage = (CropImageView) findViewById(R.id.cropmageView);

        cancleBtn = (ImageButton) findViewById(R.id.btn_cancel);
        cancleBtn.setOnClickListener(this);
        okBtn = (ImageButton) findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);

        Bitmap hh = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.crop_button);

        cropImage.setCropOverlayCornerBitmap(hh);
        cropImage.setImageBitmap(bit);

        // Bitmap bit =
        // BitmapFactory.decodeResource(this.getResources(),R.drawable.hi0);

        cropImage.setGuidelines(CropImageType.CROPIMAGE_GRID_ON_TOUCH);// 触摸时显示网格

        cropImage.setFixedAspectRatio(false);// 自由剪切

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        int i = item.getItemId();
        if (i == R.id.action_freedom) {
            cropImage.setFixedAspectRatio(false);

        } else if (i == R.id.action_1_1) {
            cropImage.setFixedAspectRatio(true);
            cropImage.setAspectRatio(10, 10);

        } else if (i == R.id.action_3_2) {
            cropImage.setFixedAspectRatio(true);
            cropImage.setAspectRatio(30, 20);

        } else if (i == R.id.action_4_3) {
            cropImage.setFixedAspectRatio(true);
            cropImage.setAspectRatio(40, 30);

        } else if (i == R.id.action_16_9) {
            cropImage.setFixedAspectRatio(true);
            cropImage.setAspectRatio(160, 90);

        } else if (i == R.id.action_rotate) {
            cropImage.rotateImage(90);

        } else if (i == R.id.action_up_down) {
            cropImage.reverseImage(CropImageType.REVERSE_TYPE.UP_DOWN);

        } else if (i == R.id.action_left_right) {
            cropImage.reverseImage(CropImageType.REVERSE_TYPE.LEFT_RIGHT);

        } else if (i == R.id.action_crop) {
            Bitmap cropImageBitmap = cropImage.getCroppedImage();
            Toast.makeText(
                    this,
                    "已保存到相册；剪切大小为 " + cropImageBitmap.getWidth() + " x "
                            + cropImageBitmap.getHeight(),
                    Toast.LENGTH_SHORT).show();
            FileUtils.saveBitmapToCamera(this, cropImageBitmap, "crop.jpg");

        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cancel) {
            Intent cancelData = new Intent();
            setResult(RESULT_CANCELED, cancelData);
            this.finish();

        } else if (i == R.id.btn_ok) {
            Bitmap bit = cropImage.getCroppedImage();
            FileUtils.writeImage(bit, mPath, 100);

            Intent okData = new Intent();
            okData.putExtra("camera_path", mPath);
            setResult(RESULT_OK, okData);
            this.finish();

        } else {
        }
    }
}

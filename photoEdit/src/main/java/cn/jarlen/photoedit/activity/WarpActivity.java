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
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import cn.jarlen.photoedit.R;
import cn.jarlen.photoedit.utils.FileUtils;
import cn.jarlen.photoedit.warp.Picwarp;
import cn.jarlen.photoedit.warp.WarpView;

/**
 * 图像变形
 * <p>
 * Created by zhouqiong on 2017/6/5.
 */
public class WarpActivity extends Activity implements View.OnClickListener {

    private String TAG = WarpActivity.class.getSimpleName();
    private boolean debug = true;

    String pathName = Environment.getExternalStorageDirectory()
            + "/DCIM/Camera/test.jpg";

    private WarpView image;
    boolean mSaving; // Whether the "save" button is already clicked.
    private Picwarp warp = new Picwarp();

    private ImageButton cancelBtn, okBtn;

    private String warpPicturePath;
    Bitmap pictureBitmap, newBitmap;
    private static final int scale = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_warp);
        initView();
        initDate();
    }

    private void initView() {
        Intent warpIntent = getIntent();
        warpPicturePath = warpIntent.getStringExtra("camera_path");

        image = (WarpView) findViewById(R.id.warp_image);

        cancelBtn = (ImageButton) findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);

        okBtn = (ImageButton) findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);

    }

    private void initDate() {
        pictureBitmap = BitmapFactory
                .decodeFile(warpPicturePath);

        newBitmap = pictureBitmap;
        warp.initArray();

        image.setWarpBitmap(newBitmap);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (debug)
            Log.d(TAG, "onDestroy");

        if (newBitmap != null) {
            newBitmap.recycle();
            newBitmap = null;
            System.gc();
        }
    }

    @Override
    public void onClick(View view) {

        int i = view.getId();
        if (i == R.id.btn_cancel) {
            Intent cancelData = new Intent();
            setResult(RESULT_CANCELED, cancelData);

            recycle();
            this.finish();


        } else if (i == R.id.btn_ok) {
            Bitmap bit = image.getWrapBitmap();
            FileUtils.writeImage(bit, warpPicturePath, 100);

            Intent okData = new Intent();
            okData.putExtra("camera_path", warpPicturePath);
            setResult(RESULT_OK, okData);

            recycle();
            this.finish();

        } else {
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0, 1, 1, "重置");
//		menu.add(0, 2, 2, "保存");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 1:

                image.setWarpBitmap(pictureBitmap);
                image.invalidate();

                break;
//			case 2:
//
//				Bitmap bit = image.getWrapBitmap();
//				FileUtils.writeImage(bit, warpPicturePath, 100);
//
//				Intent okData = new Intent();
//				okData.putExtra("camera_path", warpPicturePath);
//				setResult(RESULT_OK, okData);
//
//				recycle();
//				this.finish();

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void recycle() {
        if (newBitmap != null) {
            newBitmap.recycle();
            newBitmap = null;
        }

        if (pictureBitmap != null) {
            pictureBitmap.recycle();
            pictureBitmap = null;
        }
    }
}

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import cn.jarlen.photoedit.R;
import cn.jarlen.photoedit.utils.FileUtils;
import cn.jarlen.photoedit.utils.PhotoUtils;

/**
 * 翻转
 *
 * Created by zhouqiong on 2017/6/5.
 */
public class RevolveActivity extends Activity implements View.OnClickListener {

    private ImageView pictureShow;
    private Button revoleTest, unTest, fanTestUpDown, fanTestLeftRight;

    private String camera_path;
    private Bitmap srcBitmap, bit;

    private ImageButton cancelBtn, okBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_revolve);
        initView();

        Intent intent = getIntent();
        camera_path = intent.getStringExtra("camera_path");

        srcBitmap = BitmapFactory.decodeFile(camera_path);
        bit = srcBitmap;

        pictureShow.setImageBitmap(srcBitmap);
    }

    private void initView() {
        cancelBtn = (ImageButton) findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);

        okBtn = (ImageButton) findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);

        pictureShow = (ImageView) findViewById(R.id.picture);

        revoleTest = (Button) findViewById(R.id.revoleTest);
        revoleTest.setOnClickListener(this);

        unTest = (Button) findViewById(R.id.unTest);
        unTest.setOnClickListener(this);

        fanTestUpDown = (Button) findViewById(R.id.fanTestUpDown);
        fanTestUpDown.setOnClickListener(this);

        fanTestLeftRight = (Button) findViewById(R.id.fanTestLeftRight);
        fanTestLeftRight.setOnClickListener(this);
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
            FileUtils.writeImage(bit, camera_path, 100);

            Intent intent = new Intent();
            intent.putExtra("camera_path", camera_path);
            setResult(Activity.RESULT_OK, intent);
            recycle();
            this.finish();

        } else if (i == R.id.revoleTest) {
            bit = PhotoUtils.rotateImage(bit, 90);
            pictureShow.setImageBitmap(bit);

        } else if (i == R.id.fanTestLeftRight) {
            bit = PhotoUtils.reverseImage(bit, -1, 1);
            pictureShow.setImageBitmap(bit);

        } else if (i == R.id.fanTestUpDown) {
            bit = PhotoUtils.reverseImage(bit, 1, -1);
            pictureShow.setImageBitmap(bit);


        } else if (i == R.id.unTest) {
            bit = srcBitmap;
            pictureShow.setImageBitmap(bit);

        } else {
        }

    }

    private void recycle() {
        if (srcBitmap != null) {
            srcBitmap.recycle();
            srcBitmap = null;
        }
        if (bit != null) {
            bit.recycle();
            bit = null;
        }
    }
}

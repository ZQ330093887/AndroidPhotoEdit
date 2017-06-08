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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.jarlen.photoedit.R;
import cn.jarlen.photoedit.holocolorpicker.SelectColorPopup;
import cn.jarlen.photoedit.operate.OperateConstants;
import cn.jarlen.photoedit.operate.OperateUtils;
import cn.jarlen.photoedit.operate.OperateView;
import cn.jarlen.photoedit.operate.TextObject;

/**
 * 添加文字
 * Created by zhouqiong on 2017/6/5.
 */
public class AddTextActivity extends Activity implements View.OnClickListener {

    private LinearLayout content_layout;
    private OperateView operateView;
    private String camera_path;
    private String mPath = null;
    OperateUtils operateUtils;
    private ImageButton btn_ok, btn_cancel;
    private Button add, color, family, moren, faceby, facebygf;
    private LinearLayout face_linear;
    private SelectColorPopup menuWindow;
    private String typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtext);
        Intent intent = getIntent();
        camera_path = intent.getStringExtra("camera_path");
        operateUtils = new OperateUtils(this);
        initView();
        // 延迟每次延迟10 毫秒 隔1秒执行一次
        timer.schedule(task, 10, 1000);
    }

    final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (content_layout.getWidth() != 0) {
                    Log.i("LinearLayoutW", content_layout.getWidth() + "");
                    Log.i("LinearLayoutH", content_layout.getHeight() + "");
                    // 取消定时器
                    timer.cancel();
                    fillContent();
                }
            }
        }
    };

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            Message message = new Message();
            message.what = 1;
            myHandler.sendMessage(message);
        }
    };

    private void initView() {
        content_layout = (LinearLayout) findViewById(R.id.mainLayout);
        face_linear = (LinearLayout) findViewById(R.id.face_linear);
        btn_ok = (ImageButton) findViewById(R.id.btn_ok);
        btn_cancel = (ImageButton) findViewById(R.id.btn_cancel);
        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        add = (Button) findViewById(R.id.addtext);
        color = (Button) findViewById(R.id.color);
        family = (Button) findViewById(R.id.family);
        add.setOnClickListener(this);
        color.setOnClickListener(this);
        family.setOnClickListener(this);

        moren = (Button) findViewById(R.id.moren);
        moren.setTypeface(Typeface.DEFAULT);
        faceby = (Button) findViewById(R.id.faceby);
        faceby.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/"
                + OperateConstants.FACE_BY + ".ttf"));
        facebygf = (Button) findViewById(R.id.facebygf);
        facebygf.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/"
                + OperateConstants.FACE_BYGF + ".ttf"));
        moren.setOnClickListener(this);
        faceby.setOnClickListener(this);
        facebygf.setOnClickListener(this);

    }

    private void fillContent() {
        Bitmap resizeBmp = BitmapFactory.decodeFile(camera_path);
        operateView = new OperateView(AddTextActivity.this, resizeBmp);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                resizeBmp.getWidth(), resizeBmp.getHeight());
        operateView.setLayoutParams(layoutParams);
        content_layout.addView(operateView);
        operateView.setMultiAdd(true); //设置此参数，可以添加多个文字
    }

    private void btnSave() {
        operateView.save();
        Bitmap bmp = getBitmapByView(operateView);
        if (bmp != null) {
            mPath = saveBitmap(bmp, "saveTemp");
            Intent okData = new Intent();
            okData.putExtra("camera_path", mPath);
            setResult(RESULT_OK, okData);
            this.finish();
        }
    }

    // 将模板View的图片转化为Bitmap
    public Bitmap getBitmapByView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    // 将生成的图片保存到内存中
    public String saveBitmap(Bitmap bitmap, String name) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File dir = new File(Constants.filePath);
            if (!dir.exists())
                dir.mkdir();
            File file = new File(Constants.filePath + name + ".jpg");
            FileOutputStream out;

            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                    out.flush();
                    out.close();
                }
                return file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private void addfont() {
        final EditText editText = new EditText(AddTextActivity.this);
        new AlertDialog.Builder(AddTextActivity.this).setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @SuppressLint("NewApi")
                    public void onClick(DialogInterface dialog, int which) {
                        String string = editText.getText().toString();
                        // TextObject textObj =
                        // operateUtils.getTextObject(string);
                        // if (textObj != null) {
                        // textObj.setTypeface(OperateConstants.FACE_BY);
                        // textObj.commit();
                        // }

                        TextObject textObj = operateUtils.getTextObject(string,
                                operateView, 5, 150, 100);
                        if (textObj != null) {
                            if (menuWindow != null) {
                                textObj.setColor(menuWindow.getColor());
                            }
                            textObj.setTypeface(typeface);
                            textObj.commit();
                            operateView.addItem(textObj);
                            operateView.setOnListener(new OperateView.MyListener() {
                                public void onClick(TextObject tObject) {
                                    alert(tObject);
                                }
                            });
                        }
                    }
                }).show();
    }

    private void alert(final TextObject tObject) {

        final EditText editText = new EditText(AddTextActivity.this);
        new AlertDialog.Builder(AddTextActivity.this).setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @SuppressLint("NewApi")
                    public void onClick(DialogInterface dialog, int which) {
                        String string = editText.getText().toString();
                        tObject.setText(string);
                        tObject.commit();
                    }
                }).show();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.color) {
            menuWindow = new SelectColorPopup(AddTextActivity.this,
                    AddTextActivity.this);
            // 显示窗口
            menuWindow.showAtLocation(
                    AddTextActivity.this.findViewById(R.id.main),
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        } else if (i == R.id.submit) {
            menuWindow.dismiss();

        } else if (i == R.id.family) {
            if (face_linear.getVisibility() == View.GONE) {
                face_linear.setVisibility(View.VISIBLE);
            } else {
                face_linear.setVisibility(View.GONE);
            }

        } else if (i == R.id.addtext) {
            addfont();

        } else if (i == R.id.btn_ok) {
            btnSave();

        } else if (i == R.id.btn_cancel) {
            finish();

        } else if (i == R.id.moren) {
            typeface = null;
            face_linear.setVisibility(View.GONE);

        } else if (i == R.id.faceby) {
            typeface = OperateConstants.FACE_BY;
            face_linear.setVisibility(View.GONE);

        } else if (i == R.id.facebygf) {
            typeface = OperateConstants.FACE_BYGF;
            face_linear.setVisibility(View.GONE);

        } else {
        }

    }
}

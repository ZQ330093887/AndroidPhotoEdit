package injection.sw.com.mycocularlater;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import injection.sw.com.mycocularlater.imagepicker.ImagePicker;
import injection.sw.com.mycocularlater.imagepicker.bean.ImageItem;
import injection.sw.com.mycocularlater.imagepicker.ui.ImageGridActivity;
/**
 * Created by zhouqiong on 2017/6/5.
 */
public class MainActivity extends AppCompatActivity {
    private ImageAdapter imageAdapter;
    private ArrayList<String> mThumbIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });
        imageAdapter = new ImageAdapter(this);
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(imageAdapter);
    }

    public void selectPhoto() {
        Intent photoIntent = new Intent(this, ImageGridActivity.class);
        photoIntent.putExtra(ImagePicker.MAX_PHOTO_NUMBER, 4);
        startActivityForResult(photoIntent, CommonUtils.REQUEST_CODE_ALBUN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CommonUtils.REQUEST_CODE_ALBUN) {
            if (data != null) {
                try {
                    final ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    int size = images.size();
                    for (int i = 0; i < size; i++) {
                        mThumbIds.add(images.get(i).path);
                    }
                    imageAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(this, "请您打开读取存储文件权限", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mThumbIds.size();
        }

        @Override
        public Object getItem(int position) {
            return mThumbIds.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }
            Glide.with(imageView.getContext()).load(mThumbIds.get(position)).into(imageView);
            return imageView;
        }
    }
}

package injection.sw.com.mycocularlater.imagepicker.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import injection.sw.com.mycocularlater.R;
import injection.sw.com.mycocularlater.imagepicker.ImagePicker;
import injection.sw.com.mycocularlater.imagepicker.Utils;
import injection.sw.com.mycocularlater.imagepicker.bean.ImageItem;
import injection.sw.com.mycocularlater.imagepicker.loader.GlideImageLoader;
import injection.sw.com.mycocularlater.imagepicker.ui.ImageGridActivity;
import injection.sw.com.mycocularlater.imagepicker.view.SuperCheckBox;
/**
 * Created by zhouqiong on 2017/6/5.
 */
public class ImageGridAdapter extends BaseAdapter {

    private static final int ITEM_TYPE_CAMERA = 0;  //第一个条目是相机
    private static final int ITEM_TYPE_NORMAL = 1;  //第一个条目不是相机

    private ImagePicker imagePicker;
    //private Activity mActivity;
    private ImageGridActivity mActivity;
    private ArrayList<ImageItem> images;       //当前需要显示的所有的图片数据
    private ArrayList<ImageItem> mSelectedImages; //全局保存的已经选中的图片数据
    private boolean isShowCamera;         //是否显示拍照按钮
    private int mImageSize;               //每个条目的大小
    private OnImageItemClickListener listener;   //图片被点击的监听

    public ImageGridAdapter(Activity activity, ArrayList<ImageItem> images) {
        this.mActivity = (ImageGridActivity) activity;
        if (images == null || images.size() == 0) this.images = new ArrayList<>();
        else this.images = images;
        mImageSize = Utils.getImageItemWidth(mActivity);
        imagePicker = ImagePicker.getInstance();
        isShowCamera = imagePicker.isShowCamera();
        mSelectedImages = imagePicker.getSelectedImages();
    }

    public void refreshData(ArrayList<ImageItem> images) {
        if (images == null || images.size() == 0) this.images = new ArrayList<>();
        else this.images = images;
        notifyDataSetChanged();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowCamera) return position == 0 ? ITEM_TYPE_CAMERA : ITEM_TYPE_NORMAL;
        return ITEM_TYPE_NORMAL;
    }

    @Override
    public int getCount() {
        return isShowCamera ? images.size() + 1 : images.size();
    }

    @Override
    public ImageItem getItem(int position) {
        if (isShowCamera) {
            if (position == 0) return null;
            return images.get(position - 1);
        } else {
            return images.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == ITEM_TYPE_CAMERA) {
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.adapter_camera_item, parent, false);
            convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize)); //让图片是个正方形
            convertView.setTag(null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.startCamera();
                    //imagePicker.takePicture(mActivity, ImagePicker.REQUEST_CODE_TAKE);
                }
            });
        } else {
            final ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.adapter_image_list_item, parent, false);
                convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mImageSize)); //让图片是个正方形
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final ImageItem imageItem = getItem(position);

            holder.ivThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onImageItemClick(holder.rootView, imageItem, position);
                }
            });
            holder.cbCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int selectLimit = imagePicker.getSelectLimit();
                    if (holder.cbCheck.isChecked() && mSelectedImages.size() >= selectLimit) {
                        holder.cbCheck.setChecked(false);
                        holder.mask.setVisibility(View.GONE);
                    } else {
                        imagePicker.addSelectedImageItem(position, imageItem, holder.cbCheck.isChecked());
                        holder.mask.setVisibility(View.VISIBLE);
                    }
                }
            });
            //根据是否多选，显示或隐藏checkbox
            if (imagePicker.isMultiMode()) {
                holder.cbCheck.setVisibility(View.VISIBLE);
                boolean checked = mSelectedImages.contains(imageItem);
                if (checked) {
                    holder.mask.setVisibility(View.VISIBLE);
                    holder.cbCheck.setChecked(true);
                } else {
                    holder.mask.setVisibility(View.GONE);
                    holder.cbCheck.setChecked(false);
                    if (mSelectedImages.size() >= imagePicker.getSelectLimit()) {
                        holder.cbCheck.setVisibility(View.GONE);
                    }
                }
            } else {
                holder.cbCheck.setVisibility(View.GONE);
            }
            if (imagePicker.getImageLoader() == null)
                imagePicker.setImageLoader(new GlideImageLoader());
            imagePicker.getImageLoader().displayImage(mActivity, imageItem.path, holder.ivThumb, true); //显示图片
        }
        return convertView;
    }

    private class ViewHolder {
        View rootView;
        ImageView ivThumb;
        View mask;
        SuperCheckBox cbCheck;

        public ViewHolder(View view) {
            rootView = view;
            ivThumb = (ImageView) view.findViewById(R.id.iv_thumb);
            mask = view.findViewById(R.id.mask);
            cbCheck = (SuperCheckBox) view.findViewById(R.id.cb_check);
        }
    }

    public void setOnImageItemClickListener(OnImageItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnImageItemClickListener {
        void onImageItemClick(View view, ImageItem imageItem, int position);
    }


}
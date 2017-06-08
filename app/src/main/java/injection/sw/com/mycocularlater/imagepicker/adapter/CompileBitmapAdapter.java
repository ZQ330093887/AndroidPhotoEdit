package injection.sw.com.mycocularlater.imagepicker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import injection.sw.com.mycocularlater.R;

/**
 * Created by zhouqiong on 2017/6/5.
 */

public class CompileBitmapAdapter extends RecyclerView.Adapter<CompileBitmapAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private String[] mDatas;

    public CompileBitmapAdapter(Context context, String[] datats) {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    @Override
    public int getItemCount() {
        return mDatas.length;
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View view = mInflater.inflate(R.layout.activity_index_gallery_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.mTxt = (Button) view.findViewById(R.id.id_index_gallery_item_text);
        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
        viewHolder.mTxt.setText(mDatas[i]);
        viewHolder.mTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, i);
                }
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View rootView) {
            super(rootView);
        }

        Button mTxt;
    }
}
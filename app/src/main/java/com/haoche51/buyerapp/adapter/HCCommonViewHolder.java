package com.haoche51.buyerapp.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.haoche51.buyerapp.helper.ImageLoaderHelper;

public class HCCommonViewHolder {
  private SparseArray<View> mViews;
  private int mPosition;
  private View mConvertView;

  public HCCommonViewHolder(Context context, ViewGroup parent, int layoutId, int mPosition) {
    this.mPosition = mPosition;
    this.mViews = new SparseArray<>();
    mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
    mConvertView.setTag(this);
  }

  public static HCCommonViewHolder getInstance(Context context, View convertView, ViewGroup parent,
      int layoutId, int position) {
    HCCommonViewHolder holder;
    if (convertView == null) {
      holder = new HCCommonViewHolder(context, parent, layoutId, position);
    } else {
      holder = (HCCommonViewHolder) convertView.getTag();
    }
    //复用的是convertView,postition是不复用的
    holder.mPosition = position;
    return holder;
  }

  public View getConvertView() {
    return mConvertView;
  }

  public <T extends View> T findTheView(int viewId) {
    View view = mViews.get(viewId);
    if (view == null) {
      view = mConvertView.findViewById(viewId);
      mViews.put(viewId, view);
    }
    return (T) view;
  }

  public int getPosition() {
    return mPosition;
  }

  public void setTextViewText(int viewId, CharSequence text) {
    TextView tv = findTheView(viewId);
    tv.setText(text);
  }

  public void loadHttpImage(int viewId, String url) {
    ImageView iv = findTheView(viewId);
    ImageLoaderHelper.displayNormalImage(url, iv);
  }

  public void loadHttpImage(ImageView iv, String url) {
    ImageLoaderHelper.displayNormalImage(url, iv);
  }
}

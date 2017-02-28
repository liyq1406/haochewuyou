package com.haoche51.buyerapp.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.data.Brand;
import com.haoche51.buyerapp.entity.HCBrandEntity;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.HCViewUtils;
import java.util.List;

public class BrandAdapter extends BaseAdapter implements SectionIndexer {
  private List<Brand> brands;
  private String host;

  public BrandAdapter(List<Brand> brands, View.OnClickListener listener, String host) {
    this.brands = brands;
    this.mClickListener = listener;
    this.host = host;
  }

  private View.OnClickListener mClickListener;

  @Override public int getCount() {
    return brands.size();
  }

  @Override public Object getItem(int position) {
    return brands.get(position);
  }

  @Override public long getItemId(int position) {
    return position;
  }

  @Override public View getView(final int position, View convertView, ViewGroup parent) {
    ViewHolder mHolder;
    if (convertView == null) {
      mHolder = new ViewHolder();
      int resLayout = R.layout.brand_list_item;
      convertView = LayoutInflater.from(GlobalData.mContext).inflate(resLayout, parent, false);
      mHolder.brandName = (TextView) convertView.findViewById(R.id.brand_name);
      mHolder.IndexLetter = (TextView) convertView.findViewById(R.id.brand_letter);
      mHolder.line = convertView.findViewById(R.id.brand_line);
      RelativeLayout.LayoutParams params =
          (RelativeLayout.LayoutParams) mHolder.line.getLayoutParams();
      params.leftMargin = 60 + HCUtils.getDimenPixels(R.dimen.px_30dp);
      convertView.setTag(mHolder);
      mHolder.IndexLetter.getPaint().setFakeBoldText(true);
    } else {
      mHolder = (ViewHolder) convertView.getTag();
    }

    Brand mBrand = brands.get(position);
    int mTextColor = (R.color.home_hot_text);
    if (FilterUtils.getFilterTerm(host).getBrand_id() == mBrand.getBrandId()) {
      mTextColor = R.color.reminder_red;
    }
    int color = HCUtils.getResColor(mTextColor);
    mHolder.brandName.setTextColor(color);
    mHolder.brandName.setText(mBrand.getBrandName());
    setUpBrand(mHolder, mBrand);
    setIndexer(mHolder, position, mBrand);
    if (position < getCount() - 1) {
      setLine(mHolder, position + 1);
    }
    convertView.setTag(R.id.for_brand_pos, position);
    convertView.setTag(R.id.brand_convert_tag, convert2BrandEntity(mBrand));
    convertView.setOnClickListener(mClickListener);

    return convertView;
  }

  private HCBrandEntity convert2BrandEntity(Brand brand) {
    HCBrandEntity brandEntity = new HCBrandEntity();
    brandEntity.setBrand_id(brand.getBrandId());
    brandEntity.setBrand_name(brand.getBrandName());
    brandEntity.setSeries(brand.getSeries_ids());
    return brandEntity;
  }

  private class ViewHolder {
    TextView brandName;
    TextView IndexLetter;
    View line;
  }

  private void setLine(ViewHolder holder, int position) {
    int section = getSectionForPosition(position);
    if (position == getPositionForSection(section)) {
      holder.line.setVisibility(View.GONE);
    } else {
      holder.line.setVisibility(View.VISIBLE);
    }
  }

  private void setIndexer(ViewHolder holder, int position, Brand brand) {
    int section = getSectionForPosition(position);
    if (position == getPositionForSection(section)) {
      holder.IndexLetter.setVisibility(View.VISIBLE);
      holder.IndexLetter.setText(brand.getSortLetter());
    } else {
      holder.IndexLetter.setVisibility(View.GONE);
    }
  }

  private void setUpBrand(ViewHolder holder, Brand brand) {
    int resID = HCViewUtils.getBrandResID(brand.getBrandId(), R.drawable.empty_brand);
    HCViewUtils.setTextViewDrawable(holder.brandName, resID, HCConsts.DRAWABLE_LEFT, 2);
  }

  @Override public Object[] getSections() {
    return null;
  }

  @SuppressLint("DefaultLocale") @Override public int getPositionForSection(int sectionIndex) {

    for (int i = 0; i < getCount(); i++) {
      char sortChar = brands.get(i).getSortLetter().toUpperCase().charAt(0);
      if (sortChar == sectionIndex) {
        return i;
      }
    }
    return -1;
  }

  /**
   * 根据ListView的当前位置获取分类的首字母的Char ascii值
   */
  @Override public int getSectionForPosition(int position) {
    return brands.get(position).getSortLetter().charAt(0);
  }
}

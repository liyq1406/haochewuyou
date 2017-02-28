package com.haoche51.buyerapp.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.data.FilterTerm;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCUtils;

/**
 * 筛选组件
 */
public class CommonFilterFragment extends HCBaseFragment {

  @InjectView(R.id.tv_filterbar_sort) TextView mSortTv;
  @InjectView(R.id.tv_filterbar_brand) TextView mBrandTv;
  @InjectView(R.id.tv_filterbar_price) TextView mPriceTv;
  @InjectView(R.id.tv_filterbar_detail) TextView mMoreTv;

  public static final String KEY_FOR_HOST = "keyForHost";
  private String host;

  @Override boolean isNeedBindEventBus() {
    this.setPriority(HCEvent.PRIORITY_HIGH);
    return true;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_common_filter;
  }

  @Override void doInitViewOrData() {
    initTags();
    setFilterBarColor();
  }

  @Override public void onEvent(HCCommunicateEntity entity) {
    String action = entity.getAction();

    if (HCEvent.ACTION_RESET_FILTERBAR_COLOR.equals(action)) {
      setFilterBarColor();
    }

    if (HCEvent.ACTION_SWAPTO_CORE_INNER.equals(action)) {
      setFilterBarColor();
    }
  }

  private void initTags() {
    Bundle bundle = getArguments();
    String host = bundle.getString(KEY_FOR_HOST);
    if (TextUtils.isEmpty(host)) {
      throw new RuntimeException("host have not set");
    }
    this.host = host;
  }

  @OnClick({
      R.id.rel_filterbar_sort, R.id.rel_filterbar_brand, R.id.rel_filterbar_price,
      R.id.rel_filterbar_detail
  }) @SuppressWarnings("unused") public void OnFilterBarClick(View v) {
    handleFilterClick(v);
  }

  private void handleFilterClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.rel_filterbar_brand:// 筛选栏_品牌
        HCEvent.postEvent(HCEvent.ACTION_SHOW_BRAND_FRAGMENT, host);
        break;
      case R.id.rel_filterbar_sort:// 筛选栏_排序
        HCEvent.postEvent(HCEvent.ACTION_SHOW_SORT_FRAGMENT, host);
        break;
      case R.id.rel_filterbar_price:// 筛选栏_价格
        HCEvent.postEvent(HCEvent.ACTION_SHOW_PRICE_FRAGMENT, host);
        break;
      case R.id.rel_filterbar_detail:// 筛选栏_细节
        HCEvent.postEvent(HCEvent.ACTION_SHOW_MORE_FRAGMENT, host);
        break;
    }
  }

  public void setFilterBarColor() {
    if (mBrandTv == null) return;
    FilterTerm term = FilterUtils.getFilterTerm(host);

    int redColor = HCUtils.getResColor(R.color.reminder_red);
    int normalColor = HCUtils.getResColor(R.color.hc_tab_text_color);
    int brandColor = normalColor;

    if (term.getBrand_id() > 0) {
      brandColor = redColor;
    }
    mBrandTv.setTextColor(brandColor);

    int sortColor = "综合排序".equals(term.getDescriptionSort()) ? normalColor : redColor;
    mSortTv.setTextColor(sortColor);
    mSortTv.setText(term.getDescriptionSort());

    int priceColor =
        (term.getLowPrice() == 0F && term.getHighPrice() == 0F) ? normalColor : redColor;
    mPriceTv.setTextColor(priceColor);

    int moreColor = FilterUtils.isMoreDefault(term) ? normalColor : redColor;
    mMoreTv.setTextColor(moreColor);
  }
}

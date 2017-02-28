package com.haoche51.buyerapp.fragment;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.BrandAdapter;
import com.haoche51.buyerapp.adapter.FilterBrandGridViewAdapter;
import com.haoche51.buyerapp.dao.BrandDAO;
import com.haoche51.buyerapp.data.Brand;
import com.haoche51.buyerapp.data.FilterTerm;
import com.haoche51.buyerapp.entity.HCBrandEntity;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCThreadUtils;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.PinyinComparator;
import com.haoche51.custom.HCGridView;
import com.haoche51.custom.SideBar;
import com.haoche51.custom.SideBar.OnTouchingLetterChangedListener;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 筛选栏 品牌 Fragment
 */
public class BrandFragment extends HCBaseFragment implements View.OnClickListener {

  public static BrandFragment newInstance() {
    return new BrandFragment();
  }

  @InjectView(R.id.lv_brand_main) ListView mBrandLv;
  @InjectView(R.id.tv_toast) TextView mTvToast;
  @InjectView(R.id.sidrbar) SideBar mSideBar;
  @InjectView(R.id.tv_brand_for_click) TextView mForClickTv;
  @InjectView(R.id.frame_brand_parent) View mBrandParent;
  @InjectView(R.id.tv_brand_name) TextView mBrandTv;

  private TextView mAllBrandTv;

  FilterBrandGridViewAdapter gvAdapter;
  BrandAdapter mBrandAdapter;
  List<Brand> mBrandsData = new ArrayList<>();

  Map<String, Integer> mapBrandName2Position = new HashMap<>();
  List<String> mHotBrandNames = new ArrayList<>();

  private String host;

  public void setHost(String host) {
    this.host = host;
  }

  @Override boolean isNeedBindEventBus() {
    return true;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.filter_brand;
  }

  public void onEvent(HCCommunicateEntity entity) {
    if (entity != null) {
      if (entity.getAction().equals(host + HCEvent.ACTION_BRAND_CHOOSED_CHANGE)) {
        abordEvent(entity);
        if (gvAdapter != null && mBrandAdapter != null) {
          mBrandAdapter.notifyDataSetChanged();
          gvAdapter.notifyDataSetChanged();
          setAllBrandColor();
        }
      }
    }
  }

  @Override void doInitViewOrData() {
    if (mSideBar == null) return;

    int width = (int) (HCUtils.getScreenWidthInPixels() * 110F / 750F);
    mForClickTv.getLayoutParams().width = width;
    mForClickTv.setOnClickListener(this);
    mBrandParent.getLayoutParams().width = HCUtils.getScreenWidthInPixels() - width;
    // 不写这个点击空白处 brandFragment会响应点击事件
    mBrandParent.setOnClickListener(null);
    mBrandTv.setOnClickListener(null);
    mSideBar.setTextView(mTvToast);
    List<HCBrandEntity> HCBrandEntityList = BrandDAO.getInstance().getAllBrands();
    if (HCUtils.isListEmpty(HCBrandEntityList)) return;
    List<HCBrandEntity> hotBrandEntitiesTemp = HCSpUtils.getHotsBrands();
    if (HCUtils.isListEmpty(hotBrandEntitiesTemp) || hotBrandEntitiesTemp.size() < 8) return;
    List<HCBrandEntity> hotBrandEntities = hotBrandEntitiesTemp.subList(0, 8);

    List<Brand> bList = new ArrayList<>();
    Brand brand;
    for (HCBrandEntity HCBrandEntity : HCBrandEntityList) {
      brand = new Brand();
      int brand_id = HCBrandEntity.getBrand_id();
      brand.setBrandId(brand_id);
      brand.setBrandName(HCBrandEntity.getBrand_name());
      brand.setSeries_ids(HCBrandEntity.getSeries());
      brand.setSortLetter(HCBrandEntity.getFirst_char());
      bList.add(brand);
    }

    for (HCBrandEntity br : hotBrandEntities) {
      mHotBrandNames.add(br.getBrand_name());
    }

    // 设置headerView必须在setAdapter之前
    if (!hotBrandEntities.isEmpty()) {
      setHeaderView(hotBrandEntities);
    }

    mBrandsData = sortBrand(bList);
    fillMapData(mBrandsData);
    mBrandAdapter = new BrandAdapter(mBrandsData, this, host);
    mBrandLv.setAdapter(mBrandAdapter);

    // 设置右侧触摸监听
    mSideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
      @Override public void onTouchingLetterChanged(String s) {
        if ("热".equals(s)) {
          mBrandLv.setSelection(0);
        } else if ("#".equals(s)) {
          int offset = HCUtils.getDimenPixels(R.dimen.px_70dp);
          mBrandLv.smoothScrollToPositionFromTop(1, offset);
        }
        // 该字母首次出现的位置
        int position = mBrandAdapter.getPositionForSection(s.charAt(0));
        if (position != -1) {
          //因为有headView,所以实际的位置要加1.
          mBrandLv.setSelection(position + 1);
        }
      }
    });
  }

  private void setHeaderView(List<HCBrandEntity> entities) {
    View headView =
        LayoutInflater.from(getActivity()).inflate(R.layout.filter_brand_headerview, null);
    mAllBrandTv = (TextView) headView.findViewById(R.id.tv_allbrand);
    mAllBrandTv.setOnClickListener(this);
    setAllBrandColor();
    HCGridView gv = (HCGridView) headView.findViewById(R.id.gv_filter_brand_header);
    int resId = R.layout.gridview_item_filter_brand;
    gvAdapter = new FilterBrandGridViewAdapter(getActivity(), entities, resId, host);
    gvAdapter.setmListener(this);
    gv.setAdapter(gvAdapter);
    mBrandLv.addHeaderView(headView);
  }

  private void setAllBrandColor() {
    FilterTerm term = FilterUtils.getFilterTerm(host);
    int mTextColor = (R.color.home_hot_text);
    if (term.getBrand_id() == 0 && term.getClass_id() == 0) {
      mTextColor = R.color.reminder_red;
    }
    int color = HCUtils.getResColor(mTextColor);
    mAllBrandTv.setTextColor(color);
  }

  @Override public void onClick(View v) {
    int vid = v.getId();
    switch (vid) {
      case R.id.tv_brand_for_click:
        HCEvent.postEvent(HCEvent.ACTION_HIDE_BRAND_FRAGMENT);
        break;
      case R.id.tv_allbrand:// 全部品牌
        HCEvent.postEvent(host + HCEvent.ACTION_BRAND_CHOOSED, null);
        HCEvent.postEvent(host + HCEvent.ACTION_BRAND_CHOOSED_CHANGE);
        break;

      case R.id.linear_filter_brand_item:// 热门品牌
        HCBrandEntity HCBrandEntity = (HCBrandEntity) v.getTag();
        HCEvent.postEvent(host + HCEvent.ACTION_BRAND_CHOOSED, HCBrandEntity);
        break;

      case R.id.rel_brand_parent:
        if (mBrandLv == null) return;
        HCBrandEntity mEntity = (HCBrandEntity) v.getTag(R.id.brand_convert_tag);
        HCEvent.postEvent(host + HCEvent.ACTION_BRAND_CHOOSED, mEntity);
        break;
    }
  }

  private void fillMapData(final List<Brand> brands) {
    Runnable command = new Runnable() {
      @Override public void run() {
        int size = brands.size();
        for (int i = 0; i < size; i++) {
          Brand b = brands.get(i);
          String name = b.getBrandName();
          mapBrandName2Position.put(name, i);
        }
      }
    };
    HCThreadUtils.execute(command);
  }

  private List<Brand> sortBrand(List<Brand> brands) {
    Collections.sort(brands, new PinyinComparator());
    return brands;
  }

  public void hideDimView() {
    if (mForClickTv != null) {
      mForClickTv.setVisibility(View.GONE);
    }
  }

  public void showDimView() {
    if (mForClickTv == null) return;
    mForClickTv.postDelayed(new Runnable() {
      @Override public void run() {
        if (mForClickTv == null) return;
        mForClickTv.setVisibility(View.VISIBLE);
        int toColor = 0x50000000;
        int fromColor = Color.TRANSPARENT;
        ObjectAnimator.ofObject(mForClickTv, "backgroundColor", new ArgbEvaluator(), fromColor,
            toColor).setDuration(300).start();
      }
    }, 150);
  }
}

package com.haoche51.buyerapp.fragment;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.SortAdapter;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.entity.KeyValueEntity;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCUtils;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import java.util.ArrayList;
import java.util.List;

public class SortFilterFragment extends HCBaseFragment {

  @InjectView(R.id.lv_sort_main) ListView mPriceLv;
  @InjectView(R.id.tv_sort_for_click) TextView mForClickTv;
  @InjectView(R.id.linear_sort_parent) LinearLayout mSortParent;

  private List<KeyValueEntity> mData = new ArrayList<>();
  private SortAdapter adapter;

  private String host;

  public void setHost(String host) {
    this.host = host;
  }

  public SortFilterFragment() {
  }

  public static SortFilterFragment newInstance() {
    return new SortFilterFragment();
  }

  @Override boolean isNeedBindEventBus() {
    return true;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.filter_sort;
  }

  @Override void doInitViewOrData() {
    HCLog.d("miao", "doInitViewOrData ..." + host);
    int width = (int) (HCUtils.getScreenWidthInPixels() * 110F / 750F);
    mForClickTv.getLayoutParams().width = width;
    mForClickTv.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        HCEvent.postEvent(HCEvent.ACTION_HIDE_SORT_FRAGMENT);
      }
    });
    mSortParent.getLayoutParams().width = HCUtils.getScreenWidthInPixels() - width;
    mSortParent.setOnClickListener(null);

    List<KeyValueEntity> data = FilterUtils.getDefaultSortData(HCConsts.FILTER_SORT);
    mData.clear();
    mData.addAll(data);
    int layoutId = R.layout.lvitem_sort;
    adapter = new SortAdapter(getActivity(), mData, layoutId, host);
    mPriceLv.setAdapter(adapter);
  }

  @Override public void onEvent(HCCommunicateEntity entity) {
    String action = host + HCEvent.ACTION_SORT_CHOOSED_CHANGE;
    if (action.equals(entity.getAction())) {
      abordEvent(entity);
      if (adapter != null) {
        adapter.notifyDataSetChanged();
      }
    }
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

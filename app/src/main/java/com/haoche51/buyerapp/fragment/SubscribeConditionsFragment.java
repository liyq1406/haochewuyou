package com.haoche51.buyerapp.fragment;

import android.text.TextUtils;
import android.widget.ListView;
import butterknife.InjectView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.ManageMySubAdapter;
import com.haoche51.buyerapp.entity.HCBSubcribeEntity;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.entity.SubConditionDataEntity;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.DialogUtils;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.swipe.SwipeLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 我的订阅  管理
 */
public class SubscribeConditionsFragment extends HCBaseFragment {

  @InjectView(R.id.lv_mysub) ListView mSubLv;

  private ManageMySubAdapter swipeAdapter;
  private List<SubConditionDataEntity> mSubDatas = new ArrayList<>();

  @Override boolean isNeedBindEventBus() {
    return false;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_mysubscribe_manager;
  }

  @Override void doInitViewOrData() {
    mSubDatas = HCSpUtils.getAllSubscribe();
    swipeAdapter = new ManageMySubAdapter(getActivity(), mSubDatas, R.layout.lvitem_my_subscribe);
    swipeAdapter.isSwipeLayout(true);
    mSubLv.setAdapter(swipeAdapter);
    swipeAdapter.setItemCallBack(subAdapterCallBack);
  }

  @Override public void onEvent(HCCommunicateEntity entity) {

  }

  private ManageMySubAdapter.SubCallBack subAdapterCallBack = new ManageMySubAdapter.SubCallBack() {
    @Override public void onItemClickcallBack(SubConditionDataEntity entity) {
    }

    @Override public void onItemDelete(SubConditionDataEntity entity, SwipeLayout layout) {
      //可以滑动时,删除回调
      layout.close(true);
      if (!HCUtils.isNetAvailable()) {
        HCUtils.showToast(R.string.hc_net_unreachable);
        return;
      }
      if (swipeAdapter != null) {
        requestDeleteSub(entity);
      }
    }
  };

  private void requestDeleteSub(final SubConditionDataEntity mCondition) {
    final String sub_id = mCondition.getId();
    Map<String, Object> params = HCParamsUtil.unSubscribeVehicle(sub_id);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleDeleteSub(responseJsonString, mCondition);
      }
    }));

    DialogUtils.showProgress(getActivity());
  }

  private void handleDeleteSub(String responseJsonString, SubConditionDataEntity mCondition) {

    if (mSubLv == null) return;

    if (!TextUtils.isEmpty(responseJsonString)) {
      HCBSubcribeEntity bEnty = HCJSONParser.parseDeleteSubcribe(responseJsonString);
      if (bEnty.getErrno() == 0) {
        HCSpUtils.removeSubscribe(mCondition);
        mSubDatas.remove(mCondition);
        swipeAdapter.notifyDataSetChanged();
        HCEvent.postEvent(HCEvent.ACTION_SUBSCRIBE_CHANGED);
        HCEvent.postEvent(HCEvent.ACTION_ALL_GOOD_FILTER_CHANGED);
      }
    }
    //before onFinish
    DialogUtils.dismissProgress();
    if (HCSpUtils.getAllSubscribe().isEmpty()) {
      //删除完了退回之前的Activity
      getActivity().finish();
    }
  }

  @Override public void onResume() {
    super.onResume();
    ThirdPartInjector.onPageStart(this.getClass().getSimpleName());
  }

  @Override public void onPause() {
    super.onPause();
    ThirdPartInjector.onPageEnd(this.getClass().getSimpleName());
  }
}

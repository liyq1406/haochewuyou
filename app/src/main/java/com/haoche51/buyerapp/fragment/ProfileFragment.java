package com.haoche51.buyerapp.fragment;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.InjectView;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.CouponListActivity;
import com.haoche51.buyerapp.activity.LoginActivity;
import com.haoche51.buyerapp.activity.MyBookingVehicleActivity;
import com.haoche51.buyerapp.activity.MyCollectionActivity;
import com.haoche51.buyerapp.activity.MySubscribeVehiclesActivity;
import com.haoche51.buyerapp.activity.RecommendVehicleActivity;
import com.haoche51.buyerapp.activity.ScanHistoryActivity;
import com.haoche51.buyerapp.activity.SettingActivity;
import com.haoche51.buyerapp.activity.VehicleDetailActivity;
import com.haoche51.buyerapp.adapter.SinglePicVehicleAdapter;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.entity.HCProfileCountsEntity;
import com.haoche51.buyerapp.entity.HCVehicleItemEntity;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.DialogUtils;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCSensorsUtil;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCStatistic;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCProfileListView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/***
 * profile 我的 fragment
 */
public class ProfileFragment extends HCBaseFragment implements View.OnClickListener {

  public static final String MAX_NEWS = "99+";

  @Override boolean isNeedBindEventBus() {
    return true;
  }

  @Override int getFragmentContentViewResourceId() {
    return R.layout.fragment_profile;
  }

  private final static int RECOMMEND_COUNTS = 5;

  private List<HCVehicleItemEntity> mVehicleDatas = new ArrayList<>();
  private SinglePicVehicleAdapter mVehicleAdapter;

  @InjectView(R.id.lv_profile) HCProfileListView mListView;

  private ImageView mAvatarIv;
  private TextView mUserNameTv;

  //预定
  private ImageView mBookIconIv;
  private TextView mBookNumTv;
  private TextView mBookReminderTv;

  //收藏
  private ImageView mCollectIconIv;
  private TextView mCollectNumTv;
  private TextView mCollectReminderTv;
  //订阅
  private ImageView mSubIconIv;
  private TextView mSubNumTv;
  private TextView mSubReminderTv;

  private TextView mCouponReminderTv;

  private RelativeLayout mRecommandRel;

  @Override void doInitViewOrData() {

    int layoutRes = R.layout.fragment_profile_headerview;
    View headerView = LayoutInflater.from(getActivity()).inflate(layoutRes, null);

    initListView(headerView);

    initClicks(headerView);

    initRequest();
  }

  private void initListView(View headerView) {

    //addHeaderView must before setAdapter()
    if (mListView == null) return;

    mListView.addHeaderView(headerView);
    int itemRes = R.layout.lvitem_singlepic_vehicle;
    mVehicleAdapter = new SinglePicVehicleAdapter(getActivity(), mVehicleDatas, itemRes);
    mListView.setAdapter(mVehicleAdapter);

    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) return;
        int realPos = position - 1;
        HCVehicleItemEntity entity = mVehicleDatas.get(realPos);
        if (entity != null) {
          String curId = entity.getId();
          VehicleDetailActivity.idToThis(getActivity(), curId, "我的");
          HCSensorsUtil.myPageClick("推荐列表中的车");
          //统计推荐车源的点击数
          HCStatistic.recommendClick();
        }
      }
    });
  }

  private void initClicks(View headerView) {
    headerView.findViewById(R.id.tv_profile_advisory).setOnClickListener(this);
    headerView.findViewById(R.id.rel_profile_coupon).setOnClickListener(this);
    headerView.findViewById(R.id.rel_profile_history).setOnClickListener(this);
    headerView.findViewById(R.id.rel_profile_setting).setOnClickListener(this);
    mRecommandRel = (RelativeLayout) headerView.findViewById(R.id.rel_profile_recommand);
    mRecommandRel.setOnClickListener(this);

    headerView.findViewById(R.id.linear_profile_book).setOnClickListener(this);
    headerView.findViewById(R.id.linear_profile_sub).setOnClickListener(this);
    headerView.findViewById(R.id.frame_profile_collect).setOnClickListener(this);

    mAvatarIv = (ImageView) headerView.findViewById(R.id.iv_profile_avatar);
    mUserNameTv = (TextView) headerView.findViewById(R.id.tv_profile_username);

    mBookReminderTv = (TextView) headerView.findViewById(R.id.tv_profile_book_reminder);
    mBookIconIv = (ImageView) headerView.findViewById(R.id.iv_profile_book_icon);
    mBookNumTv = (TextView) headerView.findViewById(R.id.tv_profile_book_num);

    mCollectReminderTv = (TextView) headerView.findViewById(R.id.tv_profile_collect_reminder);
    mCollectIconIv = (ImageView) headerView.findViewById(R.id.iv_profile_collect_icon);
    mCollectNumTv = (TextView) headerView.findViewById(R.id.tv_profile_collect_num);

    mSubNumTv = (TextView) headerView.findViewById(R.id.tv_profile_sub_num);
    mSubReminderTv = (TextView) headerView.findViewById(R.id.tv_profile_subscribe_reminder);
    mSubIconIv = (ImageView) headerView.findViewById(R.id.iv_profile_sub_icon);

    mCouponReminderTv = (TextView) headerView.findViewById(R.id.tv_profile_coupon_reminder);
    mUserNameTv.setOnClickListener(this);
    mAvatarIv.setOnClickListener(this);

    seeIfNeedShowLogin();
  }

  private void initRequest() {
    requestRecommandVehicle();
  }

  private void requestRecommandVehicle() {
    Map<String, Object> params = HCParamsUtil.getRecommendVehicles();
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleRecommandVehicle(responseJsonString);
      }
    }));
  }

  private void handleRecommandVehicle(String responseJsonString) {
    if (!TextUtils.isEmpty(responseJsonString)) {
      List<HCVehicleItemEntity> vehicles = HCJSONParser.parseRecommendList(responseJsonString);
      if (!HCUtils.isListEmpty(vehicles)) {
        int count = vehicles.size();
        vehicles = count >= RECOMMEND_COUNTS ? vehicles.subList(0, RECOMMEND_COUNTS) : vehicles;
        mVehicleDatas.clear();
        mVehicleDatas.addAll(vehicles);
        mVehicleAdapter.notifyDataSetChanged();
        //显示推荐条目
        mRecommandRel.setVisibility(View.VISIBLE);
      }
    }
  }

  private void requestProfileCounts() {
    Map<String, Object> params = HCParamsUtil.getProfileCounts();
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String resp) {
        handleProfileCounts(resp);
      }
    }));
  }

  private void handleProfileCounts(String resp) {
    if (!TextUtils.isEmpty(resp)) {
      HCProfileCountsEntity countsEntity = HCJSONParser.parseProfileCounts(resp);

      //我的预约数目
      int buyer_order_count = countsEntity.getBuyer_order_count();
      mBookNumTv.setText(String.valueOf(buyer_order_count));

      //收藏数目
      int collection_count = countsEntity.getCollection_count();
      mCollectNumTv.setText(String.valueOf(collection_count));

      //订阅
      int subCounts = countsEntity.getSubscribe_count();
      mSubNumTv.setText(String.valueOf(subCounts));

      int newSubs = countsEntity.getSubscribe_new_count();
      if (newSubs > 0) {
        if (newSubs >= 99) {
          mSubReminderTv.setText(MAX_NEWS);
        } else {
          String reminderCounts = String.valueOf(newSubs);
          mSubReminderTv.setText(reminderCounts);
        }

        mSubReminderTv.setVisibility(View.VISIBLE);
        //更新sp中的订阅提醒数目
        HCSpUtils.setProfileSubscribeReminder(newSubs);
      }
    } else {
      HCUtils.toastNetError();
    }
  }

  private void seeIfNeedShowLogin() {

    if (mListView == null) return;

    if (HCUtils.isUserLogined()) {

      mAvatarIv.setImageResource(R.drawable.icon_has_login_avatar);

      mUserNameTv.setText(HCSpUtils.getUserHintPhone());

      mBookIconIv.setVisibility(View.GONE);
      mBookNumTv.setVisibility(View.VISIBLE);

      mCollectIconIv.setVisibility(View.GONE);
      mCollectNumTv.setVisibility(View.VISIBLE);

      mSubIconIv.setVisibility(View.GONE);
      mSubNumTv.setVisibility(View.VISIBLE);

      if (HCSpUtils.getProfileBookingReminder() > 0) {
        mBookReminderTv.setVisibility(View.VISIBLE);
      } else {
        mBookReminderTv.setVisibility(View.GONE);
      }

      if (HCSpUtils.getProfileCollectionReminder() > 0) {
        mCollectReminderTv.setVisibility(View.VISIBLE);
      } else {
        mCollectReminderTv.setVisibility(View.GONE);
      }

      //订阅提醒
      int subReminder = HCSpUtils.getProfileSubscribeReminder();
      if (subReminder > 0) {
        mSubReminderTv.setText(String.valueOf(subReminder));
      } else {
        mSubReminderTv.setVisibility(View.GONE);
      }

      //优惠券提醒
      int couponReminder = HCSpUtils.getProfileCouponReminder();
      if (couponReminder > 0) {
        mCouponReminderTv.setText(String.valueOf(couponReminder));
        mCouponReminderTv.setVisibility(View.VISIBLE);
      } else {
        mCouponReminderTv.setVisibility(View.GONE);
      }
    } else {
      mAvatarIv.setImageResource(R.drawable.icon_not_login_avatar);
      mUserNameTv.setText(R.string.hc_login_regist);

      mBookReminderTv.setVisibility(View.GONE);
      mBookNumTv.setVisibility(View.GONE);
      mBookIconIv.setVisibility(View.VISIBLE);

      mCollectReminderTv.setVisibility(View.GONE);
      mCollectNumTv.setVisibility(View.GONE);
      mCollectIconIv.setVisibility(View.VISIBLE);

      mSubNumTv.setVisibility(View.GONE);
      mSubReminderTv.setVisibility(View.GONE);
      mSubIconIv.setVisibility(View.VISIBLE);
    }
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.tv_profile_advisory:
        try {
          Intent intent =
              new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + HCConsts.ADVISORY_PHONE));
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);
        } catch (Exception e) {
          HCLog.d("Intent", "profile intent tel is crash ...");
        }
        break;

      case R.id.rel_profile_coupon:
        if (HCUtils.isUserLogined()) {
          HCSpUtils.setProfileCouponReminder(0);
          mCouponReminderTv.setVisibility(View.INVISIBLE);
          notifyTabDoCheck();
        }
        checkLoginDest(CouponListActivity.class);
        HCSensorsUtil.myPageClick("优惠券");
        break;

      case R.id.rel_profile_history:
        startDestAct(ScanHistoryActivity.class);
        HCSensorsUtil.myPageClick("浏览记录");
        break;

      case R.id.rel_profile_setting:
        startDestAct(SettingActivity.class);
        HCSensorsUtil.myPageClick("设置");
        break;

      case R.id.rel_profile_recommand://我的推荐
        startDestAct(RecommendVehicleActivity.class);
        HCSensorsUtil.myPageClick("推荐");
        break;

      case R.id.tv_profile_username:
      case R.id.iv_profile_avatar:
        if (!HCUtils.isUserLogined()) {
          startDestAct(LoginActivity.class);
          HCSensorsUtil.myPageClick("登录");
        }
        break;

      case R.id.linear_profile_book://我的预约
        HCSensorsUtil.myPageClick("预约");
        if (HCUtils.isUserLogined()) {
          if (mBookReminderTv.getVisibility() == View.VISIBLE) {
            mBookReminderTv.setVisibility(View.GONE);
          }
          HCSpUtils.setProfileBookingReminder(0);
        }

        checkLoginDest(MyBookingVehicleActivity.class);
        break;

      case R.id.frame_profile_collect://收藏
        HCSensorsUtil.myPageClick("收藏");

        if (HCUtils.isUserLogined()) {
          if (mCollectReminderTv.getVisibility() == View.VISIBLE) {
            mCollectReminderTv.setVisibility(View.GONE);
          }
          HCSpUtils.setProfileCollectionReminder(0);
          notifyTabDoCheck();
          startDestAct(MyCollectionActivity.class);
        } else {
          Intent mIntent = new Intent(getActivity(), LoginActivity.class);
          String hint = HCUtils.getResString(R.string.hc_action_from_detail_collect);
          mIntent.putExtra(HCConsts.INTENT_KEY_LOGIN_HINT, hint);
          mIntent.putExtra(HCConsts.INTENT_KEY_LOGIN_DEST, MyCollectionActivity.class);
          startActivity(mIntent);
        }
        break;

      case R.id.linear_profile_sub://我的订阅
        HCSensorsUtil.myPageClick("订阅");
        if (HCUtils.isUserLogined()) {
          HCSpUtils.setProfileSubscribeReminder(0);
          mSubReminderTv.setVisibility(View.GONE);
          notifyTabDoCheck();
          startDestAct(MySubscribeVehiclesActivity.class);
        } else {
          Intent mIntent = new Intent(getActivity(), LoginActivity.class);
          String hint = HCUtils.getResString(R.string.hc_action_from_detail_subscribe);
          mIntent.putExtra(HCConsts.INTENT_KEY_LOGIN_HINT, hint);
          mIntent.putExtra(HCConsts.INTENT_KEY_LOGIN_DEST, MySubscribeVehiclesActivity.class);
          startActivity(mIntent);
        }
        break;
    }
  }

  @Override public void onEvent(HCCommunicateEntity entity) {
    if (mListView == null || entity == null) return;

    String action = entity.getAction();
    switch (action) {
      case HCEvent.ACTION_LOGINSTATUS_CHANGED://登陆状态变化
        seeIfNeedShowLogin();
        if (HCUtils.isUserLogined()) {
          requestProfileCounts();
        }
        break;

      case HCEvent.ACTION_COUPON_REMINDER://轮询提醒有新的优惠券提醒
        int couponCounts = HCSpUtils.getProfileCouponReminder();
        if (couponCounts > 0) {
          mCouponReminderTv.setText(String.valueOf(couponCounts));
          mCouponReminderTv.setVisibility(View.VISIBLE);

          //如果此时当前界面显示的话,弹出优惠券数量弹层
          if (isCurentActivityValid()) {
            if (this.isVisible()) {
              DialogUtils.showCouponCountDialog(getActivity());
            }
          }
        }
        break;

      case HCEvent.ACTION_BOOKING_REMINDER://预定红点
        mBookReminderTv.setVisibility(View.VISIBLE);
        break;

      case HCEvent.ACTION_COLLECTION_REMINDER://
        mCollectReminderTv.setVisibility(View.VISIBLE);
        break;
      case HCEvent.ACTION_CHANGED_TO_PROFILE:
        if (HCUtils.isUserLogined()) {
          requestProfileCounts();
        }
        abordEvent(entity);
        break;
    }
  }

  @Override public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
    if (!hidden) {
      seeIfNeedShowLogin();
    }
  }

  @Override public void onResume() {
    super.onResume();

    ThirdPartInjector.onPageStart(this.getClass().getSimpleName());
    if (isVisible()) {
      seeIfNeedShowLogin();
      if (HCUtils.isUserLogined()) {
        requestProfileCounts();
      }
    }
  }

  @Override public void onPause() {
    super.onPause();
    ThirdPartInjector.onPageEnd(this.getClass().getSimpleName());
  }

  private void notifyTabDoCheck() {
    HCEvent.postEvent(HCEvent.ACTION_PROFILE_REMINDER_CHANGED);
  }

  private void checkLoginDest(Class destCls) {
    Intent intent = new Intent();
    if (HCUtils.isUserLogined()) {
      intent.setClass(getActivity(), destCls);
    } else {
      intent.setClass(getActivity(), LoginActivity.class);
      intent.putExtra(HCConsts.INTENT_KEY_LOGIN_DEST, destCls);
    }
    startActivity(intent);
  }
}

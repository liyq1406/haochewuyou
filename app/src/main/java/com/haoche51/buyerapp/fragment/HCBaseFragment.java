package com.haoche51.buyerapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.WebBrowserActivity;
import com.haoche51.buyerapp.entity.HCCommunicateEntity;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCUtils;
import java.lang.reflect.Field;

public abstract class HCBaseFragment extends Fragment {

  protected Activity mCurrentAct;
  protected final String TAG = this.getClass().getSimpleName();
  protected View mFragmentContentView;
  protected LayoutInflater mLayoutInflater;
  private int priority = HCEvent.PRIORITY_NONAL;

  private final static String BTAG = "hcBaseFragmentTag";

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (isNeedBindEventBus()) {
      changePriority(priority);
      HCEvent.register(this, priority);
    }
  }

  protected void changePriority(int priority) {
    this.priority = priority;
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    mLayoutInflater = inflater;
    if (getFragmentContentViewResourceId() > 0) {
      mFragmentContentView = inflater.inflate(getFragmentContentViewResourceId(), container, false);
    } else {
      mFragmentContentView = super.onCreateView(inflater, container, savedInstanceState);
    }
    return mFragmentContentView;
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    mCurrentAct = activity;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    ButterKnife.inject(this, view);

    long costTime = System.currentTimeMillis();
    doInitViewOrData();
    costTime = System.currentTimeMillis() - costTime;

    HCLog.d(BTAG, TAG + " doInitViewOrData cost " + costTime + " \n");
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    ButterKnife.reset(this);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    if (isNeedBindEventBus()) {
      HCEvent.unRegister(this);
    }
  }

  @Override public void onDetach() {
    super.onDetach();
    // for bug ---> java.lang.IllegalStateException: Activity has been destroyed
    try {
      Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
      childFragmentManager.setAccessible(true);
      childFragmentManager.set(this, null);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public void startDestAct(Class cls) {
    startActivity(new Intent(GlobalData.mContext, cls));
  }

  protected void abordEvent(HCCommunicateEntity entity) {
    HCEvent.cancelDelivery(entity);
  }

  @SuppressWarnings("unused") public View getFragmentContentView() {
    return mFragmentContentView;
  }

  abstract boolean isNeedBindEventBus();

  abstract int getFragmentContentViewResourceId();

  abstract void doInitViewOrData();

  public abstract void onEvent(HCCommunicateEntity entity);

  @SuppressWarnings("unused") public int getPriority() {
    return priority;
  }

  @SuppressWarnings("unused") public void setPriority(int priority) {
    this.priority = priority;
  }

  public void showToast(int resString) {
    HCUtils.showToast(resString);
  }

  public void showToast(String msg) {
    HCUtils.showToast(msg);
  }

  public int getResColor(int color) {
    return HCUtils.getResColor(color);
  }

  public Drawable getResDrawable(int drawableId) {
    return HCUtils.getResDrawable(drawableId);
  }

  protected boolean isCurentActivityValid() {
    return getActivity() != null && !getActivity().isFinishing();
  }

  /** 前往 帮买页面 */
  public void goToHelpBuyPage() {
    Intent mIntent = new Intent(GlobalData.mContext, WebBrowserActivity.class);
    mIntent.putExtra(HCConsts.INTENT_KEY_TITLE, getString(R.string.hc_helpbuy_wap_title));
    mIntent.putExtra(HCConsts.INTENT_KEY_URL, HCUtils.getHelpBuyURL());
    startActivity(mIntent);
  }
}

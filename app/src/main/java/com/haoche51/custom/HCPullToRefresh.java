package com.haoche51.custom;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;
import java.util.List;

public class HCPullToRefresh extends LinearLayout
    implements AbsListView.OnScrollListener, View.OnTouchListener {

  private final static String TAG = "HCPullToRefresh";

  public HCPullToRefresh(Context context) {
    this(context, null, 0);
  }

  public HCPullToRefresh(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public HCPullToRefresh(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    doInitViews();
  }

  private PtrClassicFrameLayout mPtrFrame;
  private ListView mInnerLv;
  private OnRefreshCallback mOnRefreshCallback;

  private View footerView;
  private TextView mFooterTv;
  private ProgressBar mFooterPb;
  /**
   * 标识是否正在加载更多
   */
  private boolean isLoadingMore = false;

  /**
   * 标识是否  没有更多数据了
   */
  private boolean isNoMoreData = false;

  /**
   * 控制是否可以下拉 默认可以
   */
  private boolean canPull = true;

  private View emptyView;

  /***
   * 控制当可见条目数等于总条目数,不能下拉
   */
  private boolean isVisibleLessTotal = false;

  /**
   * 控制当有筛选栏隐藏时,不能下拉
   */
  private boolean isFilterBarVisible = true;

  private TextView mTitleTv;

  private HCScrollListener mHCscrollListener;

  private void doInitViews() {
    int res = R.layout.hc_pull_to_refresh;
    View rootView = LayoutInflater.from(getContext()).inflate(res, this);
    mPtrFrame = (PtrClassicFrameLayout) rootView.findViewById(R.id.ptr_frame_base);

    PtrClassicDefaultHeader classicHeader = new PtrClassicDefaultHeader(getContext()) {
      @Override public void onUIRefreshComplete(PtrFrameLayout frame) {
        super.onUIRefreshComplete(frame);
        if (mInnerLv.getEmptyView() == null && emptyView != null) {
          mInnerLv.setEmptyView(emptyView);
        }

        if (mUIChangeCallback != null) {
          mUIChangeCallback.onUIRefreshComplete(frame);
        }
      }

      @Override
      public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status,
          PtrIndicator ptrIndicator) {
        super.onUIPositionChange(frame, isUnderTouch, status, ptrIndicator);
        if (mUIChangeCallback != null) {
          mUIChangeCallback.onUIPositionChange(frame, isUnderTouch, status, ptrIndicator);
        }
      }

      @Override public void onUIRefreshBegin(PtrFrameLayout frame) {
        super.onUIRefreshBegin(frame);
        if (mUIChangeCallback != null) {
          mUIChangeCallback.onUIRefreshBegin(frame);
        }

        if (mTitleTv != null) {
          mTitleTv.setText(R.string.hc_now_updating);
        }
      }

      @Override public void onUIRefreshPrepare(PtrFrameLayout frame) {
        super.onUIRefreshPrepare(frame);
        if (mUIChangeCallback != null) {
          mUIChangeCallback.onUIRefreshPrepare(frame);
        }
      }

      @Override public void onUIReset(PtrFrameLayout frame) {
        super.onUIReset(frame);
        if (mUIChangeCallback != null) {
          mUIChangeCallback.onUIReset(frame);
        }
      }
    };
    mPtrFrame.setHeaderView(classicHeader);
    mPtrFrame.addPtrUIHandler(classicHeader);

    int resId = in.srain.cube.views.ptr.R.id.ptr_classic_header_rotate_view_header_title;
    if (classicHeader.findViewById(resId) != null) {
      mTitleTv = (TextView) classicHeader.findViewById(resId);
    }

    mPtrFrame.setPtrHandler(new PtrHandler() {

      @Override//这里只针对是listview的情况
      public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view1) {

        if (!isFilterBarVisible) {
          return false;
        }

        if (isListViewOnTop()) {
          return canPull;
        } else {
          return false;
        }
      }

      @Override public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
        if (mOnRefreshCallback != null) {
          mOnRefreshCallback.onPullDownRefresh();
        }
      }
    });

    mInnerLv = (ListView) rootView.findViewById(R.id.ptr_lv_base);
    mInnerLv.setFooterDividersEnabled(false);
    mInnerLv.setOnScrollListener(this);
    initFooterView();
  }

  private void initFooterView() {
    footerView = LayoutInflater.from(getContext()).inflate(R.layout.pulldown_footer, null);
    footerView.setVisibility(View.GONE);
    mFooterTv = (TextView) footerView.findViewById(R.id.pulldown_footer_text);
    mFooterPb = (ProgressBar) footerView.findViewById(R.id.pulldown_footer_loading);

    mInnerLv.addFooterView(footerView);

    footerView.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        //底部加载更多点击
        seeIfNeedLoadMore();
      }
    });
  }

  public void setFirstAutoRefresh() {
    autoRefresh();
  }

  public void setCanPull(boolean canPull) {
    this.canPull = canPull;
  }

  public ListView getListView() {
    return mInnerLv;
  }

  @SuppressWarnings("unused") public PtrClassicFrameLayout getPtrClassicFrameLayout() {
    return mPtrFrame;
  }

  public void autoRefresh() {
    this.postDelayed(new Runnable() {
      @Override public void run() {
        mPtrFrame.autoRefresh();
      }
    }, 50);
  }

  public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
    if (mInnerLv != null) mInnerLv.setOnItemClickListener(listener);
  }

  public void setAdapter(ListAdapter adapter) {
    if (mInnerLv != null) mInnerLv.setAdapter(adapter);
  }

  public void setNoDefaultDivider() {
    if (mInnerLv != null) {
      int color = HCUtils.getResColor(R.color.hc_transcolor);
      ColorDrawable trans = new ColorDrawable(color);
      mInnerLv.setDivider(trans);
    }
  }

  public void setFooterStatus(boolean isNoMoreData) {
    this.isNoMoreData = isNoMoreData;
    if (footerView.getVisibility() != View.VISIBLE) {
      footerView.setVisibility(View.VISIBLE);
    }
    mFooterPb.setVisibility(View.GONE);
    int textRes = isNoMoreData ? R.string.hc_nomore_data : R.string.hc_loadmore;
    mFooterTv.setText(textRes);
    isLoadingMore = false;
  }

  public void setFooterStatus(boolean isNoMoreData, int stringId) {
    this.isNoMoreData = isNoMoreData;
    if (footerView.getVisibility() != View.VISIBLE) {
      footerView.setVisibility(View.VISIBLE);
    }
    mFooterPb.setVisibility(View.GONE);
    int textRes = isNoMoreData ? stringId : R.string.hc_loadmore;
    mFooterTv.setText(textRes);
    isLoadingMore = false;
  }

  public void setFooterStatus(List<?> list) {
    if (list != null) {
      boolean isNoMoreData = list.size() < HCConsts.PAGESIZE;
      setFooterStatus(isNoMoreData);
    }
  }

  public void hideFooter() {
    try {
      if (footerView != null) {
        footerView.setVisibility(View.GONE);
      }
    } catch (Exception e) {
    }
  }

  public void removeFooter() {
    if (mInnerLv != null && mInnerLv.getFooterViewsCount() > 0 && footerView != null) {
      mInnerLv.removeFooterView(footerView);
    }
  }

  public void finishRefresh() {
    if (mPtrFrame != null) {
      mPtrFrame.refreshComplete();
    }
  }

  public void setEmptyView(View view) {
    this.emptyView = view;
  }

  @Override public void onScrollStateChanged(AbsListView view, int scrollState) {
    switch (scrollState) {
      case SCROLL_STATE_FLING:
        ImageLoader.getInstance().pause();
        break;

      case SCROLL_STATE_IDLE:
        ImageLoader.getInstance().resume();
        //在idle时候检查是否到了最底部.
        seeIfNeedLoadMore();
        break;

      case SCROLL_STATE_TOUCH_SCROLL:
        break;
    }

    if (mHCscrollListener != null && mInnerLv != null) {
      mHCscrollListener.onScrollStateChanged(view, scrollState);
    }
  }

  @Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
      int totalItemCount) {
    isVisibleLessTotal = visibleItemCount == totalItemCount;

    if (mHCscrollListener != null) {
      mHCscrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }
  }

  private boolean isListViewOnTop() {
    if (mInnerLv == null) return true;

    if (mInnerLv.getChildCount() == 0) return true;
    return mInnerLv.getChildAt(0).getTop() == 0;
  }

  public void tryToScrollUp() {
    if (!isListViewOnTop()) {
      mInnerLv.setSelection(0);
    }
  }

  public void tryToSmoothScrollUp() {
    if (!isListViewOnTop()) {
      mInnerLv.smoothScrollToPosition(0);
    }
  }

  private void seeIfNeedLoadMore() {

    if (mInnerLv == null || mInnerLv.getAdapter() == null || isLoadingMore || isNoMoreData) return;

    if (mInnerLv.getLastVisiblePosition() == mInnerLv.getAdapter().getCount() - 1
        && mInnerLv.getChildAt(mInnerLv.getChildCount() - 1).getBottom() <= mInnerLv.getHeight()) {

      if (mOnRefreshCallback != null) {
        mOnRefreshCallback.onLoadMoreRefresh();
        mFooterPb.setVisibility(View.VISIBLE);
        mFooterTv.setText(R.string.hc_now_updating);
        isLoadingMore = true;
      }
    }
  }

  public void setFilterBarVisible(boolean isFilterBarVisible) {
    this.isFilterBarVisible = isFilterBarVisible;
  }

  public void setOnRefreshCallback(OnRefreshCallback callback) {
    this.mOnRefreshCallback = callback;
  }

  public interface OnRefreshCallback {
    void onPullDownRefresh();

    void onLoadMoreRefresh();
  }

  private OnUIChangeCallback mUIChangeCallback;

  public void setOnUIChangeCallback(OnUIChangeCallback mUIChangeCallback) {
    this.mUIChangeCallback = mUIChangeCallback;
  }

  public interface OnUIChangeCallback {
    /**
     * When the content view has reached top and refresh has been completed, view will be reset.
     */
    void onUIReset(PtrFrameLayout frame);

    /**
     * prepare for loading
     */
    void onUIRefreshPrepare(PtrFrameLayout frame);

    /**
     * perform refreshing UI
     */
    void onUIRefreshBegin(PtrFrameLayout frame);

    /**
     * perform UI after refresh
     */
    void onUIRefreshComplete(PtrFrameLayout frame);

    void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status,
        PtrIndicator ptrIndicator);
  }

  public void setHCscrollListener(HCScrollListener mHCscrollListener) {
    this.mHCscrollListener = mHCscrollListener;
  }

  //-----------------------touch listener ---------------------------------//

  public interface OnPullUpDetector {
    void isPullUp(boolean isMovingUp);
  }

  private OnPullUpDetector mPullDetector;

  public void setPullUpDetector(OnPullUpDetector detector) {
    mPullDetector = detector;
    mTouchSlop = ViewConfiguration.get(GlobalData.mContext).getScaledTouchSlop();
    this.mInnerLv.setOnTouchListener(this);
  }

  private float mLastY;
  private float mTouchSlop;

  private boolean isFinishDetect;

  @Override public boolean onTouch(View v, MotionEvent event) {

    switch (event.getAction()) {

      case MotionEvent.ACTION_MOVE:
        if (mPullDetector != null) {

          if (isFinishDetect) {
            return false;
          }

          if (isVisibleLessTotal) {
            return false;
          }

          float current = event.getRawY();
          float diff = current - mLastY;

          if (Math.abs(diff) > mTouchSlop && mLastY > 0) {
            if (diff > 0) { //下拉
              mPullDetector.isPullUp(false);
              mLastY = 0;
              isFinishDetect = true;
              return true;
            } else {    //上拉
              mPullDetector.isPullUp(true);
              mLastY = 0;
              isFinishDetect = true;
              return true;
            }
          }

          if (mLastY == 0F) {
            mLastY = current;
          }
        }
        break;

      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        isFinishDetect = false;
        break;
    }

    return false;
  }

  //-----------------------touch listener ---------------------------------//

  public boolean isNeedDetectXY = false;

  public boolean isNeedDetectXY() {
    return isNeedDetectXY;
  }

  public void setNeedDetectXY(boolean needDetectXY) {
    isNeedDetectXY = needDetectXY;
  }

  // 滑动距离及坐标
  private float xDistance, yDistance, xLast, yLast;

  @Override public boolean dispatchTouchEvent(MotionEvent ev) {
    if (isNeedDetectXY()) {

      canPull = false;

      switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
          xDistance = yDistance = 0f;
          xLast = ev.getX();
          yLast = ev.getY();
          break;

        case MotionEvent.ACTION_MOVE:
          final float curX = ev.getX();
          final float curY = ev.getY();

          xDistance += Math.abs(curX - xLast);
          yDistance += Math.abs(curY - yLast);
          xLast = curX;
          yLast = curY;

          if (xDistance == yDistance) {
            canPull = false;
          }

          if (xDistance > yDistance) {
            canPull = false;
          } else {
            if (yDistance > (xDistance + 2 * mTouchSlop)) {
              canPull = true;
            }
          }
          break;
      }
    }

    return super.dispatchTouchEvent(ev);
  }

  public interface HCScrollListener {

    public static int SCROLL_STATE_IDLE = 0;

    /**
     * The user is scrolling using touch, and their finger is still on the screen
     */
    public static int SCROLL_STATE_TOUCH_SCROLL = 1;

    /**
     * The user had previously been scrolling using touch and had performed a fling. The
     * animation is now coasting to a stop
     */
    public static int SCROLL_STATE_FLING = 2;

    public void onScrollStateChanged(AbsListView view, int scrollState);

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
        int totalItemCount);
  }
}
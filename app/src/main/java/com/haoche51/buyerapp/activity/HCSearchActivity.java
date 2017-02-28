package com.haoche51.buyerapp.activity;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import com.haoche51.buyerapp.util.HCSensorsUtil;
import com.haoche51.buyerapp.util.ThirdPartInjector;
import com.haoche51.custom.HCViewClickListener;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.adapter.AutoCompleteAdapter;
import com.haoche51.buyerapp.adapter.HCSimpleItemAdapter;
import com.haoche51.buyerapp.entity.HCArrayEntity;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.custom.github.FlowLayout;
import com.haoche51.custom.github.TagAdapter;
import com.haoche51.custom.github.TagFlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 搜索界面
 */
public class HCSearchActivity extends HCCommonTitleActivity {
  @InjectView(R.id.view_loading) View mPreLoadingView;
  @InjectView(R.id.linear_net_refresh) LinearLayout mNetErrLinear;

  @InjectView(R.id.actv_input) AutoCompleteTextView mAutoTv;
  @InjectView(R.id.iv_for_clear) ImageView mClearIv;
  @InjectView(R.id.lv_search_history) ListView mHistoryLv;

  TagFlowLayout mHotsLayout;

  /** 大家都在搜(包括底部的历史记录)父布局 */
  View mHeaderView;

  LinearLayout mHotsParent;

  private AutoCompleteAdapter mAutoAdapter;
  private List<String> mAutoDatas = new ArrayList<>();

  private List<String> mHistoryDatas = HCSpUtils.getSearchHistory();

  private List<String> mHotSearchDatas = new ArrayList<>();
  private boolean isHaveResult = false;
  private boolean isRecommend = false;
  private boolean isHistory = false;
  private String fromPlace = "";

  @Override void initViews() {
    getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    hideTitleBar();
    initAdapters();
    initClearClick();
    initClickListener();
    initTags();
    requestHotSearchData();
    String cachehotData = HCSpUtils.getHotKeyFromCache();
    handleHotSearchResp(cachehotData);
    fromPlace = getIntent().getStringExtra("FromPlace");
  }

  private void initClickListener() {
    findViewById(R.id.iv_for_clear).setOnClickListener(mClickListener);
    findViewById(R.id.tv_search_cancel).setOnClickListener(mClickListener);
    findViewById(R.id.linear_net_refresh).setOnClickListener(mClickListener);
  }

  private HCViewClickListener mClickListener = new HCViewClickListener() {
    @Override public void performViewClick(View v) {
      switch (v.getId()) {
        case R.id.iv_for_clear:
          mAutoTv.setText("");
          break;
        case R.id.tv_search_cancel:
          doFinish();
          break;
        case R.id.linear_net_refresh:
          requestHotSearchData();
          break;
      }
    }
  };

  private void initAdapters() {
    int itemRes = R.layout.lvitem_search_history;
    HCSimpleItemAdapter adapter = new HCSimpleItemAdapter(this, mHistoryDatas, itemRes);
    int paddingLeft = HCUtils.getDimenPixels(R.dimen.coupon_leftright);
    adapter.setPaddingLeft(paddingLeft);

    int resLayout = R.layout.activity_hcsearch_sub;
    mHeaderView = LayoutInflater.from(GlobalData.mContext).inflate(resLayout, null);
    mHotsLayout = (TagFlowLayout) mHeaderView.findViewById(R.id.flowlayout_content);
    mHotsParent = (LinearLayout) mHeaderView.findViewById(R.id.linear_hotsearch_parent);

    mHistoryLv.addHeaderView(mHeaderView);
    //!!! addHeaderView must before setAdapter
    mHistoryLv.setAdapter(adapter);

    mHistoryLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //注意:因为listview添加了headerView,所以position不是历史记录的item
        if (position == 0) return;

        int realPos = position - 1;
        String data = mHistoryDatas.get(realPos);
        isHistory = true;
        doResult(data);
      }
    });

    mAutoAdapter = new AutoCompleteAdapter(this, mAutoDatas, R.layout.lvitem_search_history);
    mAutoTv.setAdapter(mAutoAdapter);
    mAutoTv.setDropDownBackgroundResource(R.drawable.bg_rect_white);
    mAutoTv.setDropDownVerticalOffset(HCUtils.getDimenPixels(R.dimen.px_5dp));
    mAutoTv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < mAutoDatas.size()) {
          String data = mAutoDatas.get(position);
          isHaveResult = true;
          doResult(data);
        }
      }
    });

    mAutoTv.postDelayed(new Runnable() {
      @Override public void run() {
        InputMethodManager imm =
            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
      }
    }, 200);
  }

  private void initClearClick() {

    mAutoTv.addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      }

      @Override public void afterTextChanged(Editable s) {
        if (s != null && mClearIv != null) {
          String input = s.toString();
          input = input.trim();
          int len = input.length();
          if (len > 0) {
            mClearIv.setVisibility(View.VISIBLE);
            requestSuggestion(input);
          } else {
            mClearIv.setVisibility(View.GONE);
          }
        }
      }
    });
  }

  private void initTags() {
    mHotsLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
      @Override public boolean onTagClick(View view, int position, FlowLayout parent) {
        isRecommend = true;
        doResult(mHotSearchDatas.get(position));
        return true;
      }
    });
  }

  private void requestSuggestion(String keyword) {
    if (TextUtils.isEmpty(keyword)) return;

    Map<String, Object> params = HCParamsUtil.getSugesstion(keyword);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        if (!TextUtils.isEmpty(responseJsonString)) {
          handleSuggestion(responseJsonString);
        }
      }
    }));
  }

  private void handleSuggestion(String responseJsonString) {
    HCArrayEntity suggestion = HCJSONParser.parseSugesstion(responseJsonString);
    if (mHistoryLv == null) return;
    mAutoDatas.clear();
    List<String> data = suggestion.getData();
    if (!HCUtils.isListEmpty(data)) {
      mAutoDatas.addAll(data);
      mAutoAdapter.notifyDataSetChanged();
    }
  }

  private void requestHotSearchData() {

    if (!HCUtils.isNetAvailable()) {
      HCUtils.hideViewIfNeed(mPreLoadingView);
      mNetErrLinear.setVisibility(View.VISIBLE);
      HCUtils.toastNetError();
      return;
    }

    HCUtils.hideViewIfNeed(mNetErrLinear);
    mPreLoadingView.setVisibility(View.VISIBLE);

    Map<String, Object> params = HCParamsUtil.getHotSearch();
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleHotSearchResp(responseJsonString);
      }
    }));
  }

  private void handleHotSearchResp(String resp) {
    if (TextUtils.isEmpty(resp)) return;
    if (mHistoryLv == null) return;

    HCArrayEntity en = HCJSONParser.parseSugesstion(resp);
    List<String> data = en.getData();
    if (data != null && !data.isEmpty()) {
      mHotSearchDatas.clear();
      mHotSearchDatas.addAll(data);
      HCUtils.hideViewIfNeed(mPreLoadingView);
      mHeaderView.setVisibility(View.VISIBLE);
      mHotsParent.setVisibility(View.VISIBLE);
      mHotsLayout.setAdapter(new TagAdapter<String>(data) {
        @Override public View getView(FlowLayout parent, int position, String s) {
          int itemRes = R.layout.lvitem_search_suggestion_tag;
          LayoutInflater mInflate = LayoutInflater.from(GlobalData.mContext);
          TextView tv = (TextView) mInflate.inflate(itemRes, mHotsLayout, false);
          tv.setTextColor(HCUtils.getResColor(R.color.popular_search));
          tv.setText(s);
          return tv;
        }
      });
      HCSpUtils.setHotKeySearch(resp);
    }
  }

  private void doResult(String result) {
    HCSpUtils.saveHistory(result);
    Intent intent = new Intent();
    intent.putExtra(HCConsts.KEY_FOR_SEARCH_KEY, result);
    setResult(HCConsts.REQUESTCODE_FOR_SEARCH, intent);
    HCSensorsUtil.search(result, isHaveResult, isRecommend, isHistory, fromPlace);
    doFinish();
  }

  @Override protected void onPause() {
    super.onPause();
    doThirdPartyPause();
    HCUtils.hideKeyboard(mAutoTv);
  }

  private void doFinish() {
    FilterUtils.resetNormalToDefaultExceptCity();
    finish();
  }

  @Override public boolean dispatchKeyEvent(KeyEvent event) {
    if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
      //点击搜索按钮
      String input = mAutoTv.getText().toString();
      if (!TextUtils.isEmpty(input)) {
        doResult(input);
      }
      return true;
    }

    return super.dispatchKeyEvent(event);
  }

  @Override void initTitleBar(TextView backTv, TextView titleTv, TextView rightTv) {
  }

  @Override int getContentViewResouceId() {
    return R.layout.activity_hcsearch;
  }

  @Override protected void onResume() {
    super.onResume();

    doThirdPartyResume();
  }

  private void doThirdPartyResume() {
    String name = this.getClass().getSimpleName();
    ThirdPartInjector.onPageStart(name);
    ThirdPartInjector.onResume(this);
  }

  private void doThirdPartyPause() {
    String name = this.getClass().getSimpleName();
    ThirdPartInjector.onPageEnd(name);
    ThirdPartInjector.onPause(this);
  }
}

package com.haoche51.buyerapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.HCPollService;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.activity.MainActivity;
import com.haoche51.buyerapp.adapter.CityGridViewAdapter;
import com.haoche51.buyerapp.entity.HCCityEntity;
import com.haoche51.buyerapp.entity.HCLocationEntity;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCDbUtil;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCUtils;
import com.haoche51.custom.HCGridView;
import com.haoche51.custom.HCViewClickListener;
import java.util.List;
import java.util.Map;

public class CityDialog {

  private Activity mAct;
  private List<HCCityEntity> mCityData;
  private int lastCityID = HCDbUtil.getSavedCityId();
  private TextView mLocCityTv;
  private Dialog dialog;
  /**
   * 定位后需要显示的城市
   */
  private HCCityEntity mLocCityEntity = null;

  public CityDialog(Activity context, List<HCCityEntity> mCityData) {
    this.mAct = context;
    this.mCityData = mCityData;
  }

  public void showCityDialog() {
    int layoutId = R.layout.dialog_city_choose;
    View contentView = LayoutInflater.from(mAct).inflate(layoutId, null);

    int cityNameTvId = R.id.tv_for_hot_city_name;
    mLocCityTv = (TextView) contentView.findViewById(cityNameTvId);

    //设置推荐城市距离左边屏幕的距离
    int recommendCityId = R.id.tv_for_hot_city;
    TextView recommendCity = (TextView) contentView.findViewById(recommendCityId);
    RelativeLayout.LayoutParams layoutParams =
        (RelativeLayout.LayoutParams) recommendCity.getLayoutParams();
    int width = HCUtils.getScreenWidthInPixels();
    float leftScale = 80 / 640F;
    layoutParams.leftMargin = (int) (width * leftScale);
    recommendCity.setLayoutParams(layoutParams);

    MainActivity mMainAct = (MainActivity) mAct;
    HCPollService.HCServiceBinder binder = mMainAct.getServiceBinder();
    if (binder != null) {
      HCLocationEntity location = binder.getBaiduLocation();
      if (location != null) {
        String city_name = location.getCity_name();
        if (!TextUtils.isEmpty(city_name)) {
          mLocCityEntity = HCDbUtil.queryByCityName(city_name);
        } else {
          mLocCityEntity = FilterUtils.defaultCityEntity;
        }
        if (mLocCityEntity != null) {
          setLocationClick(mLocCityEntity);
        } else {
          // 定位到的城市不在开通范围,发送请求该显示哪个城市
          requestServerCity(location);
        }
      } else {
        setLocationClick(FilterUtils.defaultCityEntity);
      }
    }

    dialog = new Dialog(mAct, R.style.sub_condition_dialog);
    dialog.setContentView(contentView);

    HCGridView gridView = (HCGridView) contentView.findViewById(R.id.gv_city_list);
    int itemId = R.layout.gridview_item_city_list;
    String cityName = HCDbUtil.getCityNameById(String.valueOf(lastCityID));
    CityGridViewAdapter adapter = new CityGridViewAdapter(mAct, mCityData, itemId, cityName);
    gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
    gridView.setAdapter(adapter);
    adapter.setOnClickListener(mClickListener);

    WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
    lp.gravity = Gravity.BOTTOM;
    dialog.show();
  }

  private void setLocationClick(HCCityEntity enty) {
    mLocCityTv.setText(enty.getCity_name());
    mLocCityTv.setOnClickListener(mClickListener);
    mLocCityTv.setTag(R.id.view_tag_city, enty);
  }

  private HCViewClickListener mClickListener = new HCViewClickListener() {
    @Override public void performViewClick(View v) {

      if (v.getTag(R.id.view_tag_city) != null) {
        HCCityEntity cityEntity = (HCCityEntity) v.getTag(R.id.view_tag_city);
        GlobalData.userDataHelper.setCity(cityEntity);
        if (lastCityID != cityEntity.getCity_id()) {
          HCEvent.postEvent(HCEvent.ACTION_CITYCHANGED);
          lastCityID = cityEntity.getCity_id();
          dialog.dismiss();
        }
      }
    }
  };

  private void requestServerCity(HCLocationEntity loc) {
    double latitude = loc.getLatitude();
    double longitude = loc.getlongitude();

    Map<String, Object> params = HCParamsUtil.getNearestCity(latitude, longitude);
    API.post(new HCRequest(params, new HCSimpleCallBack() {
      @Override public void onHttpFinish(String responseJsonString) {
        handleServerCity(responseJsonString);
      }
    }));
  }

  private void handleServerCity(String responseJsonString) {
    if (!TextUtils.isEmpty(responseJsonString)) {
      mLocCityEntity = HCJSONParser.parseNearestCity(responseJsonString);
    }
    //before onFinish
    if (mLocCityEntity == null) {
      mLocCityEntity = FilterUtils.defaultCityEntity;
    }
    setLocationClick(mLocCityEntity);
  }
}

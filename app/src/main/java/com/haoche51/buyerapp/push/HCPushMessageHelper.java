package com.haoche51.buyerapp.push;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.activity.MainActivity;
import com.haoche51.buyerapp.entity.HCAddUserEntity;
import com.haoche51.buyerapp.entity.push.PushMsgDataEntity;
import com.haoche51.buyerapp.entity.push.PushMsgEntity;
import com.haoche51.buyerapp.net.API;
import com.haoche51.buyerapp.net.HCJSONParser;
import com.haoche51.buyerapp.net.HCParamsUtil;
import com.haoche51.buyerapp.net.HCRequest;
import com.haoche51.buyerapp.net.HCSimpleCallBack;
import com.haoche51.buyerapp.util.HCConsts;
import com.haoche51.buyerapp.util.HCEvent;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class HCPushMessageHelper {

  public final static String TAG = "HCPushMessageHelper";

  public static final String KEY_TYPE = "type";

  public static final String KEY_DATA = "data";

  public static final String KEY_VEHICLE_SOURCE_ID = "vehicle_source_id";

  public static final String KEY_PARAMS = "params";

  public static final String KEY_REDIRECT = "redirect";

  public static final String KEY_CITY = "city";
  public static final String KEY_BRAND_ID = "brand_id";
  public static final String KEY_CLASS_ID = "class_id";
  public static final String KEY_PRICE = "price";

  /** 指定URL */
  public static final int TYPE_URL = 3;

  /*** 推荐车源 */
  public static final int TYPE_RECOMMEND = 4;

  /*** 我的订阅 */
  public static final int TYPE_SUBSCRIBE = 5;

  /*** 已预约推送信息 */
  public static final int TYPE_RESERVE = 6;

  /*** 已预定推送信息 */
  public static final int TYPE_ORDAIN = 7;

  /** 推送收藏的车源 */
  public static final int TYPE_COLLECTION_SOLD = 8;

  /** 收藏车源下线 */
  public static final int TYPE_COLLECTION_OFFLINE = 9;

  /** 收藏资源降价 */
  public static final int TYPE_COLLECTION_REDUCTION = 10;

  /** 帮买推送全部好车 */
  public static final int TYPE_BANG_MAI_ALL_GOOD = 11;

  /** 收藏资源被预约 */
  public static final int TYPE_COLLECTION_RESERVATION = 13;

  public static String removeUnUseChars(String str) {
    return str.replace("\\", "").replace("\"{", "{").replace("}\"", "}");
  }

  /** cation: 由于返回数据结构可能变化,不能直接使用gson解析 */
  @SuppressWarnings("finally") public static PushMsgEntity parsePushJson(String content) {
    PushMsgEntity entity = new PushMsgEntity();
    if (!TextUtils.isEmpty(content)) {
      try {
        JSONObject json = new JSONObject(content);
        int type = json.optInt(KEY_TYPE);

        entity.setType(type);

        Object object = json.opt(KEY_DATA);
        if (object != null) {
          PushMsgDataEntity pde = new PushMsgDataEntity();
          Class<?> cls = object.getClass();
          if (cls == JSONObject.class) {
            JSONObject jObject = (JSONObject) object;
            int vehicle_source_id = jObject.optInt(KEY_VEHICLE_SOURCE_ID);
            String redirect = jObject.optString(KEY_REDIRECT);
            String param = jObject.optString(KEY_PARAMS);

            if (vehicle_source_id > 0) {
              pde.setVehicle_source_id(vehicle_source_id);
            }

            if (!TextUtils.isEmpty(redirect)) {
              pde.setRedirect(redirect);
            }

            if (!TextUtils.isEmpty(param)) {
              pde.setParams(param);
            }

            int city = jObject.optInt(KEY_CITY);
            int brand_id = jObject.optInt(KEY_BRAND_ID);
            int class_id = jObject.optInt(KEY_CLASS_ID);
            JSONArray queryArray = jObject.optJSONArray(KEY_PRICE);
            if (queryArray != null) {
              List<Double> list = new ArrayList<>();
              for (int i = 0; i < queryArray.length(); i++) {
                list.add(queryArray.optDouble(i, 0));
              }
              if (!HCUtils.isListEmpty(list)) {
                pde.setPrice(list);
              }
            }

            if (city > 0) {
              pde.setCity(city);
            }
            if (brand_id > 0) {
              pde.setBrand_id(brand_id);
            }
            if (class_id > 0) {
              pde.setClass_id(class_id);
            }
          }

          entity.setData(pde);
        }
      } catch (Exception e) {

      } finally {
        return entity;
      }
    }

    return entity;
  }

  public static void launchToDest(PushMsgEntity entity) {
    Intent mIntent = new Intent();
    mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    mIntent.putExtra(HCConsts.INKENT_KEY_PUSH_TYPE, entity.getType());
    mIntent.setClass(GlobalData.mContext, MainActivity.class);
    if (entity.getData() != null) {
      Bundle bundle = new Bundle();
      bundle.putSerializable(HCConsts.INTEKTN_KEY_PUSH_DATA, entity.getData());
      mIntent.putExtras(bundle);
    }
    GlobalData.mContext.startActivity(mIntent);
  }

  public static void setCollectionAndBookingReminder(int type) {
    if (type == TYPE_COLLECTION_SOLD || type == TYPE_COLLECTION_OFFLINE
        || type == TYPE_COLLECTION_REDUCTION || type == TYPE_COLLECTION_RESERVATION) {
      //收藏
      HCSpUtils.setProfileCollectionReminder(1);
      HCEvent.postEvent(HCEvent.ACTION_COLLECTION_REMINDER);
    } else if (TYPE_RESERVE == type || TYPE_ORDAIN == type) {
      HCSpUtils.setProfileBookingReminder(1);
      HCEvent.postEvent(HCEvent.ACTION_BOOKING_REMINDER);
    }
  }

  public static void requestBindXMServer(String mi_client_id) {

    boolean hasBind = GlobalData.userDataHelper.isBindToXMServer();
    HCLog.d(TAG, "has bind to .. xiaomi .. server " + hasBind + "\n");

    if (hasBind || mi_client_id == null) return;
    HCLog.d(TAG, "requestBindBDServer .. xiaomi .. start  ");

    Map<String, Object> params = HCParamsUtil.newXiaoMiBind(mi_client_id);
    API.post(new HCRequest(params, new HCSimpleCallBack() {

      @Override public void onHttpFinish(String responseJsonString) {

        HCLog.d(TAG, "bind .. xiaomi .. server return str " + responseJsonString + "\n\n");

        if (!TextUtils.isEmpty(responseJsonString)) {
          HCAddUserEntity entity = HCJSONParser.parseAddUser(responseJsonString);
          String _udid = entity.getUser_id();
          String userid = entity.getUserid();
          int realUDID = 0;
          if (!TextUtils.isEmpty(_udid) && TextUtils.isDigitsOnly(_udid)) {
            realUDID = HCUtils.str2Int(_udid);
          }

          if (!TextUtils.isEmpty(userid) && TextUtils.isDigitsOnly(userid)) {
            realUDID = HCUtils.str2Int(userid);
          }

          if (0 != realUDID) {
            HCLog.d(TAG, "requestBindBDServer .. xiaomi .. server returned id =   " + realUDID);
            GlobalData.userDataHelper.setBindToXMServer();
            GlobalData.userDataHelper.setUserDeviceId(realUDID);
          }
        }
      }
    }));
  }

  public static void requestBindBDServer(final String bd_user_id, final String bd_channel_id) {

    boolean hasBind = GlobalData.userDataHelper.isBindToBDServer();

    HCLog.d(TAG, "has bind to .. baidu .. server " + hasBind + "\n");

    if (hasBind || bd_user_id == null || bd_channel_id == null) {
      return;
    }

    HCLog.d(TAG, "requestBindBDServer .. baidu .. start  ");

    Map<String, Object> params = HCParamsUtil.newBaiduBind(bd_user_id, bd_channel_id);

    API.post(new HCRequest(params, new HCSimpleCallBack() {

      @Override public void onHttpFinish(String responseJsonString) {

        HCLog.d(TAG, "bind .. baidu ..server return str " + responseJsonString + "\n\n");

        if (!TextUtils.isEmpty(responseJsonString)) {
          HCAddUserEntity entity = HCJSONParser.parseAddUser(responseJsonString);
          String _udid = entity.getUser_id();
          String userid = entity.getUserid();
          int realUDID = 0;
          if (!TextUtils.isEmpty(_udid) && TextUtils.isDigitsOnly(_udid)) {
            realUDID = HCUtils.str2Int(_udid);
          }

          if (!TextUtils.isEmpty(userid) && TextUtils.isDigitsOnly(userid)) {
            realUDID = HCUtils.str2Int(userid);
          }

          if (0 != realUDID) {
            HCLog.d(TAG, "requestBindBDServer .. baidu .. server returned id =   " + realUDID);
            GlobalData.userDataHelper.setBindToBDServer();
            GlobalData.userDataHelper.setUserDeviceId(realUDID);
          }
        }
      }
    }));
  }
}

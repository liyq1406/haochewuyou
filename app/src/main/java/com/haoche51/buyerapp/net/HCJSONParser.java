package com.haoche51.buyerapp.net;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.haoche51.buyerapp.entity.BHCAddUserEntity;
import com.haoche51.buyerapp.entity.BHCBookOrderEntity;
import com.haoche51.buyerapp.entity.BHCBrandEntity;
import com.haoche51.buyerapp.entity.BHCCityEntity;
import com.haoche51.buyerapp.entity.BHCCompareVehicleEntity;
import com.haoche51.buyerapp.entity.BHCCouponEntity;
import com.haoche51.buyerapp.entity.BHCDetailEntity;
import com.haoche51.buyerapp.entity.BHCHomeCityDataEntity;
import com.haoche51.buyerapp.entity.BHCHomeDialogDataEntity;
import com.haoche51.buyerapp.entity.BHCLoginEntity;
import com.haoche51.buyerapp.entity.BHCNearestCityEntity;
import com.haoche51.buyerapp.entity.BHCProfileCountsEntity;
import com.haoche51.buyerapp.entity.BHCRecommandEntity;
import com.haoche51.buyerapp.entity.BHCScanHistoryEntity;
import com.haoche51.buyerapp.entity.BHCSoldVehicleEntity;
import com.haoche51.buyerapp.entity.BHCSplashEntity;
import com.haoche51.buyerapp.entity.BHCSubListEntity;
import com.haoche51.buyerapp.entity.BHCTodayNewArrivalEntity;
import com.haoche51.buyerapp.entity.BHCVehicleItemEntity;
import com.haoche51.buyerapp.entity.BHCVehicleListEntity;
import com.haoche51.buyerapp.entity.HCAddUserEntity;
import com.haoche51.buyerapp.entity.HCArrayEntity;
import com.haoche51.buyerapp.entity.HCBSubcribeEntity;
import com.haoche51.buyerapp.entity.HCBSubscribeIdEntity;
import com.haoche51.buyerapp.entity.HCBangMaiEntity;
import com.haoche51.buyerapp.entity.HCBookOrderEntity;
import com.haoche51.buyerapp.entity.HCBrandEntity;
import com.haoche51.buyerapp.entity.HCCityEntity;
import com.haoche51.buyerapp.entity.HCCompareVehicleEntity;
import com.haoche51.buyerapp.entity.HCCouponEntity;
import com.haoche51.buyerapp.entity.HCDataIntEntity;
import com.haoche51.buyerapp.entity.HCDetailEntity;
import com.haoche51.buyerapp.entity.HCHomeCityDataEntity;
import com.haoche51.buyerapp.entity.HCHomeDialogDataEntity;
import com.haoche51.buyerapp.entity.HCMoreFilterEntity;
import com.haoche51.buyerapp.entity.HCMoreFilterItemEntity;
import com.haoche51.buyerapp.entity.HCProfileCountsEntity;
import com.haoche51.buyerapp.entity.HCRecommandEntity;
import com.haoche51.buyerapp.entity.HCSearchEntity;
import com.haoche51.buyerapp.entity.HCSoldVehicleEntity;
import com.haoche51.buyerapp.entity.HCSplashEntity;
import com.haoche51.buyerapp.entity.HCSubListEntity;
import com.haoche51.buyerapp.entity.HCSyncSubContionEntity;
import com.haoche51.buyerapp.entity.HCTodayNewArrivalEntity;
import com.haoche51.buyerapp.entity.HCVehicleItemEntity;
import com.haoche51.buyerapp.entity.HCVehicleListDataEntity;
import com.haoche51.buyerapp.entity.SaleServiceEntity;
import com.haoche51.buyerapp.entity.SaleSubmitResEntity;
import com.haoche51.buyerapp.entity.ScanHistoryEntity;
import com.haoche51.buyerapp.entity.SoldVehicleInfoEntity;
import com.haoche51.buyerapp.entity.response.RHCCommonEntity;
import com.haoche51.buyerapp.entity.response.RHCSaleSubmitEntity;
import com.haoche51.buyerapp.entity.response.RHCSellVehicleEntity;
import com.haoche51.buyerapp.util.FilterUtils;
import com.haoche51.buyerapp.util.HCLog;
import com.haoche51.buyerapp.util.HCSpUtils;
import com.haoche51.buyerapp.util.HCUtils;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class HCJSONParser {

  public static final String TAG = "HCJSONParser";

  private static Gson mGson = new Gson();

  private final static String ERRNO = "errno";
  private final static String ERRMSG = "errmsg";

  /**
   * 解析最近城市
   */
  public static HCCityEntity parseNearestCity(String resp) {
    BHCNearestCityEntity be = null;
    try {
      be = mGson.fromJson(resp, BHCNearestCityEntity.class);
    } catch (Exception e) {

    } finally {
      if (be != null) {
        return be.getData();
      } else {
        return FilterUtils.defaultCityEntity;
      }
    }
  }

  /**
   * 解析登陆实体
   */
  public static BHCLoginEntity parseLogin(String resp) {
    BHCLoginEntity entity = null;
    try {
      entity = mGson.fromJson(resp, BHCLoginEntity.class);
    } catch (Exception e) {
      removeLoginStatus();
    } finally {
      return entity == null ? new BHCLoginEntity() : entity;
    }
  }

  /**
   * 解析发送验证码实体
   */
  public static HCDataIntEntity parseSendVerify(String resp) {
    return parseCouponReminderCount(resp);
  }

  /**
   * 解析我的优惠券实体
   */
  public static HCDataIntEntity parseCouponReminderCount(String resp) {
    HCDataIntEntity entity = null;
    try {
      entity = mGson.fromJson(resp, HCDataIntEntity.class);
    } catch (Exception e) {

    } finally {
      return entity == null ? new HCDataIntEntity() : entity;
    }
  }

  /**
   * 解析优惠券列表返回
   */
  public static List<HCCouponEntity> parseCouponList(String resp) {
    BHCCouponEntity entity;
    ArrayList<HCCouponEntity> result = new ArrayList<>();
    try {
      entity = mGson.fromJson(resp, BHCCouponEntity.class);
      if (entity != null) {
        List<HCCouponEntity> datas = entity.getData();
        if (datas != null) {
          result.addAll(datas);
        }
      }
    } catch (Exception e) {

    } finally {
      return result;
    }
  }

  /**
   * 解析支持城市
   */
  public static List<HCCityEntity> parseSupportCity(String resp) {
    List<HCCityEntity> results = new ArrayList<>();
    try {
      BHCCityEntity bhcCityEntity = mGson.fromJson(resp, BHCCityEntity.class);
      if (bhcCityEntity != null) {
        List<HCCityEntity> datas = bhcCityEntity.getData();
        if (datas != null) {
          results.addAll(datas);
        }
      }
    } catch (Exception e) {

    }
    return results;
  }

  private static void removeLoginStatus() {
    HCUtils.logout();
  }

  /**
   * 解析添加用户接口
   */
  public static HCAddUserEntity parseAddUser(String resp) {
    HCAddUserEntity result = null;
    try {
      BHCAddUserEntity bhcAddUserEntity = mGson.fromJson(resp, BHCAddUserEntity.class);
      if (bhcAddUserEntity != null) {
        result = bhcAddUserEntity.getData();
      }
    } catch (Exception e) {
      removeLoginStatus();
    } finally {
      return result == null ? new HCAddUserEntity() : result;
    }
  }

  /**
   * 解析预约看车接口
   */
  public static BHCAddUserEntity parseReserveVehicle(String resp) {
    BHCAddUserEntity result = null;
    try {
      result = mGson.fromJson(resp, BHCAddUserEntity.class);
    } catch (Exception e) {
      removeLoginStatus();
    } finally {
      return result == null ? new BHCAddUserEntity() : result;
    }
  }

  /**
   * 解析比较车源
   */
  public static List<HCCompareVehicleEntity> parseCompareVehicles(String resp) {
    List<HCCompareVehicleEntity> result = null;
    try {
      BHCCompareVehicleEntity bhc = mGson.fromJson(resp, BHCCompareVehicleEntity.class);
      if (bhc != null) {
        result = bhc.getData();
      }
    } catch (Exception e) {

    } finally {
      return result == null ? new ArrayList<HCCompareVehicleEntity>() : result;
    }
  }

  /**
   * 解析获得的品牌
   */
  public static List<HCBrandEntity> parseBrand(String result) {

    if (TextUtils.isEmpty(result)) return new ArrayList<>();

    List<HCBrandEntity> mBrands = null;

    try {
      BHCBrandEntity bhcBrandEntity = mGson.fromJson(result, BHCBrandEntity.class);
      if (bhcBrandEntity != null) {
        mBrands = bhcBrandEntity.getData();
        if (mBrands != null) {
          int size = mBrands.size();
          size = size >= 10 ? 10 : size;
          List<HCBrandEntity> sub = mBrands.subList(0, size);
          HCSpUtils.setHotsBrands(sub);
        }
      }
    } catch (Exception e) {

    } finally {
      return mBrands == null ? new ArrayList<HCBrandEntity>() : mBrands;
    }
  }

  /**
   * 解析浏览记录接口
   */
  public static List<ScanHistoryEntity> parseScanHistory(String resp) {

    List<ScanHistoryEntity> results = null;
    try {
      BHCScanHistoryEntity ben = mGson.fromJson(resp, BHCScanHistoryEntity.class);
      if (ben != null) {
        results = ben.getData().getVehicles();
      }
    } catch (Exception e) {
      HCLog.d(TAG, "parseScanHistory excep  e " + e.getMessage() + ", " + e.getCause());
    } finally {
      return results == null ? new ArrayList<ScanHistoryEntity>() : results;
    }
  }

  /**
   * 解析订阅的车源列表
   */
  public static HCSubListEntity parseSubscribeVehicleData(String resp) {
    HCSubListEntity entity = null;
    try {
      BHCSubListEntity bre = mGson.fromJson(resp, BHCSubListEntity.class);
      if (bre != null) {
        entity = bre.getData();
      }
    } catch (Exception e) {

    } finally {
      return entity == null ? new HCSubListEntity() : entity;
    }
  }

  /**
   * 解析订阅 返回订阅id
   */
  public static HCBSubcribeEntity parseDeleteSubcribe(String resp) {
    HCBSubcribeEntity entity = null;
    try {
      entity = mGson.fromJson(resp, HCBSubcribeEntity.class);
    } catch (Exception e) {
      removeLoginStatus();
    } finally {
      return entity == null ? new HCBSubcribeEntity() : entity;
    }
  }

  public static HCBSubscribeIdEntity paseSubcribeId(String resp) {
    HCBSubscribeIdEntity entity = null;
    try {
      JSONObject json = new JSONObject(resp);
      int errno = json.optInt(ERRNO);
      String errmsg = json.optString(ERRMSG);
      if (errno != 0) {
        //表示有异常情况,返回数据结果要特么变了 注意不能通过Gson解析了
        entity = new HCBSubscribeIdEntity();
        entity.setErrno(errno);
        if (!TextUtils.isEmpty(errmsg)) {
          entity.setErrmsg(errmsg);
        }
      } else {
        entity = mGson.fromJson(resp, HCBSubscribeIdEntity.class);
      }
    } catch (Exception e) {
      removeLoginStatus();
    } finally {
      if (entity == null) {
        entity = new HCBSubscribeIdEntity();
        entity.setErrno(-1);
      }
      return entity;
    }
  }

  /**
   * 同步服务器端订阅条件
   */
  public static HCSyncSubContionEntity parseSyncSubCondition(String resp) {
    HCSyncSubContionEntity entity = null;
    try {
      entity = mGson.fromJson(resp, HCSyncSubContionEntity.class);
    } catch (Exception e) {
      removeLoginStatus();
    } finally {
      return entity == null ? new HCSyncSubContionEntity() : entity;
    }
  }

  /**
   * 兑换优惠券
   */
  public static HCBSubcribeEntity parseExchangeCoupon(String resp) {
    HCBSubcribeEntity entity = null;
    try {
      entity = mGson.fromJson(resp, HCBSubcribeEntity.class);
    } catch (Exception e) {
      removeLoginStatus();
    } finally {
      return entity == null ? new HCBSubcribeEntity() : entity;
    }
  }

  /**
   * 用户订单
   */
  public static List<HCBookOrderEntity> parseBuyerOrder(String resp) {
    BHCBookOrderEntity entity = null;
    try {
      entity = mGson.fromJson(resp, BHCBookOrderEntity.class);
    } catch (Exception e) {
      removeLoginStatus();
    } finally {
      return entity == null ? new ArrayList<HCBookOrderEntity>()
          : (entity.getData() == null ? new ArrayList<HCBookOrderEntity>() : entity.getData());
    }
  }

  /** 解析搜索建议 */
  public static HCArrayEntity parseSugesstion(String resp) {
    HCArrayEntity result = null;
    try {
      result = mGson.fromJson(resp, HCArrayEntity.class);
    } catch (Exception e) {

    } finally {
      return result == null ? new HCArrayEntity() : result;
    }
  }

  /** 解析搜索车源接口 */
  public static HCSearchEntity parseSearchResult(String resp) {
    HCSearchEntity searchEntity = null;
    try {
      searchEntity = mGson.fromJson(resp, HCSearchEntity.class);
    } catch (Exception e) {

    } finally {
      return searchEntity == null ? new HCSearchEntity() : searchEntity;
    }
  }

  //解析 车源   解析搜索车源接口
  public static BHCVehicleItemEntity parseGetVehicleSourceList(String resp) {
    BHCVehicleItemEntity bhcv = null;
    HCSearchEntity entity;
    try {
      entity = mGson.fromJson(resp, HCSearchEntity.class);
      if (entity != null) {
        bhcv = entity.getData();
      }
    } catch (Exception e) {

    } finally {
      return bhcv == null ? new BHCVehicleItemEntity() : bhcv;
    }
  }//解析 车源   解析搜索车源接口

  public static HCTodayNewArrivalEntity parseTodayNewArrivalList(String resp) {
    BHCTodayNewArrivalEntity bhcv = null;
    HCTodayNewArrivalEntity entity = null;
    try {
      bhcv = mGson.fromJson(resp, BHCTodayNewArrivalEntity.class);
      if (bhcv != null) {
        entity = bhcv.getData();
      }
    } catch (Exception e) {

    } finally {
      return entity == null ? new HCTodayNewArrivalEntity() : entity;
    }
  }

  /**
   * 解析出售爱车页面返回信息
   */
  public static SaleServiceEntity parseSaleService(String response) {
    SaleServiceEntity entity = null;
    try {
      entity = mGson.fromJson(response, RHCSellVehicleEntity.class).getData();
    } catch (Exception e) {
      e.printStackTrace();
      entity = null;
    } finally {
      return entity;
    }
  }

  /***
   * 解析提交线索返回结果
   */
  public static SaleSubmitResEntity parseSaleSubimtRes(String response) {
    SaleSubmitResEntity entity = null;
    try {
      entity = mGson.fromJson(response, RHCSaleSubmitEntity.class).getData();
    } catch (Exception e) {
      e.printStackTrace();
      entity = null;
    } finally {
      return entity;
    }
  }

  /**
   * 通用解析。 解析请求语音返回接口？
   */
  public static RHCCommonEntity parseCommon(String response) {
    RHCCommonEntity entity = null;
    try {
      entity = mGson.fromJson(response, RHCCommonEntity.class);
    } catch (Exception e) {
      e.printStackTrace();
      entity = null;
    } finally {
      return entity;
    }
  }

  /***
   * 解析我出售的车
   * action: get_my_vehicle
   */
  public static List<SoldVehicleInfoEntity> parseMySoldVehicles(String resp) {
    List<SoldVehicleInfoEntity> lists = null;
    try {
      BHCSoldVehicleEntity be = mGson.fromJson(resp, BHCSoldVehicleEntity.class);
      if (be != null) {
        HCSoldVehicleEntity soldVehicleEntity = be.getData();
        if (soldVehicleEntity != null) {
          lists = soldVehicleEntity.getVehicle_info();
        }
      }
    } catch (Exception e) {

    } finally {
      return lists == null ? new ArrayList<SoldVehicleInfoEntity>() : lists;
    }
  }

  /** 解析action: get_vehicle_source_by_id */
  public static HCDetailEntity parseVehicleDetail(String resp) {
    HCDetailEntity detail = null;
    try {
      BHCDetailEntity bd = mGson.fromJson(resp, BHCDetailEntity.class);
      if (bd != null) {
        detail = bd.getData();
      }
    } catch (Exception e) {

    } finally {
      return detail == null ? new HCDetailEntity() : detail;
    }
  }

  /***
   * 解析收藏结果
   * action: collection_cancel ,
   */
  public static boolean parseCollectResult(String resp) {
    boolean isSucc = false;
    try {
      BHCDetailEntity bh = mGson.fromJson(resp, BHCDetailEntity.class);
      if (bh != null) {
        isSucc = bh.getErrno() == 0;
      }
    } catch (Exception e) {

    } finally {
      return isSucc;
    }
  }

  /***
   * 解析收藏列表或者今日新上
   * action: collection_list，home_today_vehicle
   */
  public static HCVehicleListDataEntity parseCollectOrTodayList(String resp) {
    HCVehicleListDataEntity entity = null;
    try {
      BHCVehicleListEntity bEnty = mGson.fromJson(resp, BHCVehicleListEntity.class);
      if (bEnty != null) {
        entity = bEnty.getData();
      }
    } catch (Exception e) {

    } finally {
      return entity == null ? new HCVehicleListDataEntity() : entity;
    }
  }

  /**
   * 解析用户推荐车源
   * action: my_recommend_list
   */
  public static List<HCVehicleItemEntity> parseRecommendList(String resp) {

    List<HCVehicleItemEntity> result = null;
    try {
      BHCRecommandEntity bre = mGson.fromJson(resp, BHCRecommandEntity.class);
      if (bre != null) {
        HCRecommandEntity entity = bre.getData();
        if (entity != null) {
          result = entity.getVehicles();
        }
      }
    } catch (Exception e) {

    } finally {
      return result == null ? new ArrayList<HCVehicleItemEntity>() : result;
    }
  }

  /**
   * 解析我的数字 requestProfileCounts
   * action: my_data
   */
  public static HCProfileCountsEntity parseProfileCounts(String resp) {
    HCProfileCountsEntity result = null;
    try {
      BHCProfileCountsEntity ben = mGson.fromJson(resp, BHCProfileCountsEntity.class);
      if (ben != null) {
        result = ben.getData();
      }
    } catch (Exception e) {

    } finally {
      return result == null ? new HCProfileCountsEntity() : result;
    }
  }

  /**
   * 解析闪屏图片信息
   * action:promote_start
   */
  public static HCSplashEntity parseSplashData(String resp) {
    HCSplashEntity entity = null;
    try {
      BHCSplashEntity bsplash = mGson.fromJson(resp, BHCSplashEntity.class);
      if (bsplash != null) {
        entity = bsplash.getData();
      }
    } catch (Exception e) {

    } finally {
      return entity == null ? new HCSplashEntity() : entity;
    }
  }

  /**
   * 新的获取首页数据接口
   * action:  home_city_data_v6
   */
  public static HCHomeCityDataEntity parseHomeCityData(String resp) {
    HCHomeCityDataEntity entity = null;
    try {
      BHCHomeCityDataEntity bEntity = mGson.fromJson(resp, BHCHomeCityDataEntity.class);
      if (bEntity != null) {
        entity = bEntity.getData();
      }
    } catch (Exception e) {

    } finally {
      return entity == null ? new HCHomeCityDataEntity() : entity;
    }
  }

  /***
   * 解析action: promote_home
   */
  public static HCHomeDialogDataEntity parseHomeDialogData(String resp) {
    HCHomeDialogDataEntity entity = null;
    try {
      BHCHomeDialogDataEntity bentity = mGson.fromJson(resp, BHCHomeDialogDataEntity.class);
      if (bentity != null) {
        entity = bentity.getData();
      }
    } catch (Exception e) {

    } finally {
      return entity == null ? new HCHomeDialogDataEntity() : entity;
    }
  }

  /** 解析更多筛选的返回数量 */
  public static HCMoreFilterItemEntity parseMoreFilterCount(String resp) {
    HCMoreFilterItemEntity count = null;
    HCMoreFilterEntity entity;
    try {
      entity = mGson.fromJson(resp, HCMoreFilterEntity.class);
      if (entity != null) {
        count = entity.getData();
      }
    } catch (Exception e) {

    } finally {
      return count == null ? new HCMoreFilterItemEntity() : count;
    }
  }

  /** 解析帮买返回数据 */
  public static HCBangMaiEntity parseBangMai(String resp) {
    HCBangMaiEntity bangMaiEntity = null;
    try {
      bangMaiEntity = mGson.fromJson(resp, HCBangMaiEntity.class);
    } catch (Exception e) {

    } finally {
      return bangMaiEntity == null ? new HCBangMaiEntity() : bangMaiEntity;
    }
  }
}

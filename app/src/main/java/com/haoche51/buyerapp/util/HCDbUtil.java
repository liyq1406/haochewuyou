package com.haoche51.buyerapp.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.dao.BrandDAO;
import com.haoche51.buyerapp.dao.SeriesDAO;
import com.haoche51.buyerapp.entity.BaseEntity;
import com.haoche51.buyerapp.entity.HCBrandEntity;
import com.haoche51.buyerapp.entity.HCCityEntity;
import com.haoche51.buyerapp.entity.SeriesEntity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class HCDbUtil {

  private static final String TAG = "HCDbUtil";

  public static HCCityEntity queryByCityName(String city_name) {
    if (!TextUtils.isEmpty(city_name)) {
      //如果名称以市结尾去掉市
      if (city_name.endsWith("市")) {
        city_name = city_name.substring(0, city_name.indexOf("市"));
      }
      List<HCCityEntity> lists = HCSpUtils.getSupportCities();
      for (HCCityEntity en : lists) {
        String cname = en.getCity_name();
        if (cname.equals(city_name)) {
          return en;
        }
      }
    }

    return null;
  }

  public static void updateBrand(List<HCBrandEntity> brands) {
    // 更新brand
    if (brands != null && brands.size() != 0) {
      try {
        //Caused by: android.database.sqlite.SQLiteFullException: database or disk is full
        BrandDAO.getInstance().truncate();
        BrandDAO.getInstance().insert(brands);
      } catch (Exception e) {
        //
      }
    }
  }

  private final static String PREFIX =
      "insert into series(id,name,short_name,pinyin,brand_id,brand_name) values ";

  public static void insertSeriesData() {

    Runnable command = new Runnable() {
      public void run() {
        SeriesDAO.getInstance().truncate();
        Context context = GlobalData.mContext;
        BufferedReader br = null;
        boolean hasTransaction = false;
        try {
          SQLiteDatabase mDB = GlobalData.mDbHelper.getWritableDatabase();
          InputStream is = context.getResources().openRawResource(R.raw.auto_series);
          br = new BufferedReader(new InputStreamReader(is));
          String line;
          int insertCount = 0;
          while (!TextUtils.isEmpty((line = br.readLine()))) {
            if (insertCount == 0) {
              mDB.beginTransaction();
              hasTransaction = false;
            }
            String sql = PREFIX + line;
            mDB.execSQL(sql);
            insertCount++;
            if (insertCount == 100) {
              mDB.setTransactionSuccessful();
              mDB.endTransaction();
              insertCount = 0;
              hasTransaction = true;
            }
          }
          if (!hasTransaction) {
            mDB.setTransactionSuccessful();
            mDB.endTransaction();
          }
        } catch (Exception e) {
          HCLog.d(TAG, "insertSeriesData is crash ...");
        } finally {
          if (br != null) {
            try {
              br.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          // 到这里就是插入完毕了
          HCSpUtils.setImportedcariesTable();
        }
      }
    };

    HCThreadUtils.execute(command);
  }

  /** 更新车系表 */
  public static void updateSeries(final SeriesEntity entity) {
    HCThreadUtils.execute(new Runnable() {
      @Override public void run() {
        try {
          if (entity != null) {
            int series_id = entity.getId();
            BaseEntity base = SeriesDAO.getInstance().get(series_id);
            if (base == null) {
              SeriesDAO.getInstance().insert(entity);
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  public static String getBrandNameById(int brand_id) {
    if (0 != brand_id && BrandDAO.getInstance().get(brand_id) != null) {
      HCBrandEntity entity = (HCBrandEntity) BrandDAO.getInstance().get(brand_id);
      return entity.getBrand_name();
    }

    return "品牌不限";
  }

  public static String getCarSeriesNameById(int series_id) {
    if (series_id != 0) {
      if (SeriesDAO.getInstance().get(series_id) != null) {
        SeriesEntity entity = (SeriesEntity) SeriesDAO.getInstance().get(series_id);
        return entity.getName();
      }
    }
    return "";
  }

  public static int getSavedCityId() {
    HCCityEntity city = GlobalData.userDataHelper.getCity();
    if (city != null) return city.getCity_id();
    return 12;
  }

  public static String getSavedCityName() {
    HCCityEntity city = GlobalData.userDataHelper.getCity();
    if (city != null) return city.getCity_name();
    return "北京";
  }

  public static String getCityNameById(String city_id) {
    String result = "";
    List<HCCityEntity> allCityEntities = HCSpUtils.getSupportCities();
    if (allCityEntities != null && !allCityEntities.isEmpty()) {
      for (HCCityEntity enty : allCityEntities) {
        int enid = enty.getCity_id();
        if (city_id.equals(String.valueOf(enid))) {
          result = enty.getCity_name();
        }
      }
    }

    return result;
  }
}

package com.haoche51.buyerapp.util;

import android.text.TextUtils;
import com.haoche51.buyerapp.GlobalData;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.dao.BrandDAO;
import com.haoche51.buyerapp.dao.SeriesDAO;
import com.haoche51.buyerapp.data.FilterTerm;
import com.haoche51.buyerapp.entity.HCBrandEntity;
import com.haoche51.buyerapp.entity.HCCityEntity;
import com.haoche51.buyerapp.entity.HCQueryEntity;
import com.haoche51.buyerapp.entity.KeyValueEntity;
import com.haoche51.buyerapp.entity.SeriesEntity;
import com.haoche51.buyerapp.entity.SubConditionDataEntity;
import com.haoche51.buyerapp.entity.push.PushMsgDataEntity;
import com.haoche51.buyerapp.fragment.AllGoodVehiclesFragment;
import com.haoche51.buyerapp.fragment.DirectFragment;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/***
 */
public class FilterUtils {

  private final static String TAG = "FilterUtils";

  public static final String allHost = AllGoodVehiclesFragment.class.getSimpleName();
  public static final String directHost = DirectFragment.class.getSimpleName();

  public static final String SORT_TYPE_REGISTER_TIME = "register_time";
  public static final String SORT_TYPE_PRICE = "price";
  public static final String SORT_TYPE_MILES = "miles";
  public static final String SORT_TYPE_TIME = "time";

  public final static String[] SORT_STR = HCUtils.getResArray(R.array.hc_filter_sort);

  /** 全部好车 */
  public static final int TYPE_NORMAL = 0;
  /** 直营店 */
  public static final int TYPE_DIRECT = 1;
  /** 今日新上 */
  public static final int TYPE_TODAY = 2;
  /** 0 升序 */
  public static final int ORDER_ASC = 0;
  /** 1 降序 */
  public static final int ORDER_DESC = 1;

  public static final String CHAR_SPLIT = "-";
  public static final String STR_YEAR = "年";
  public static final String STR_LITER = "L";
  public static final String STR_WAN = "万";
  public static final String STR_MILES = "公里";
  public static final String STR_AND_BOTTOM = "及以下";
  private static final String STR_ABOVE = "以上";
  private static final String STR_WITHIN = "以内";
  private static final String STR_UNLIMITED = "不限";

  private static FilterTerm defaultFilterTerm = new FilterTerm();

  public static HCCityEntity defaultCityEntity = new HCCityEntity(12, "北京");

  /** 全部好车页面是否为默认筛选条件 */
  public static boolean isCurrentDefaultCondition() {
    return defaultFilterTerm.equals(getNormalFilterterm());
  }

  private static int diffNow(long otherTime) {
    Calendar calendar = Calendar.getInstance();
    long cur = System.currentTimeMillis();
    calendar.setTimeInMillis(cur);
    int nowYear = calendar.get(Calendar.YEAR);

    otherTime = otherTime < HCFormatUtil.MAX10 ? otherTime * 1000 : otherTime;
    otherTime = otherTime > cur ? cur : otherTime;
    calendar.setTimeInMillis(otherTime);

    int otherYear = calendar.get(Calendar.YEAR);

    return nowYear - otherYear;
  }

  public static void saveBrandFilterTerm(String host, int brand_id, int class_id) {
    FilterTerm mTerm = getFilterTerm(host);
    mTerm.setBrand_id(brand_id);
    mTerm.setClass_id(class_id);
    setFilterTerm(host, mTerm);
  }

  public static void saveTermPrice(String host, float lowPrice, float highPrice) {
    FilterTerm filterTerm = getFilterTerm(host);
    filterTerm.setLowPrice(lowPrice);
    filterTerm.setHighPrice(highPrice);
    setFilterTerm(host, filterTerm);
  }

  public static void priceKey2FilterTerm(String host, String key) {
    int[][] values = {
        { 0, 2 }, { 2, 3 }, { 3, 5 }, { 5, 7 }, { 7, 9 }, { 9, 12 }, { 12, 15 }, { 15, 20 },
        { 20, 30 }, { 30, 0 },
    };

    String[] prices = HCUtils.getResArray(R.array.hc_filter_price);

    int index = -1;
    for (int i = 0; i < prices.length; i++) {
      if (prices[i].equals(key)) {
        index = i;
        break;
      }
    }
    if (index >= 0) {
      int[] dest = values[index];
      saveTermPrice(host, dest[0], dest[1]);
    }
  }

  public static void resetNormalToDefaultExceptCity() {
    setNormalFilterterm(defaultFilterTerm);
  }

  public static void resetTodayToDefaultExceptCity() {
    setTodayFilterterm(defaultFilterTerm);
  }

  public static void resetDirectToDefaultExceptCity() {
    setDirectFilterTerm(defaultFilterTerm);
  }

  /** 获取指定类型的排序 */
  public static int getPointSort(int type) {
    if (TYPE_NORMAL == type) {
      return getNormalFilterterm().getSort();
    } else if (TYPE_TODAY == type) {
      return getTodayFilterTerm().getSort();
    } else {
      return getDirectFilterTerm().getSort();
    }
  }

  /**
   * 获取当前排序类型
   * 价格,车龄,里程
   */
  public static String getPointOrder(int type) {
    if (TYPE_NORMAL == type) {
      return getNormalFilterterm().getOrder();
    } else if (TYPE_TODAY == type) {
      return getTodayFilterTerm().getOrder();
    } else {
      return getDirectFilterTerm().getOrder();
    }
  }

  /***
   * 全部好车还是超值低价还是今日新上
   * 城市
   * 品牌
   * 车系
   * 价格
   * 车龄
   * 里程
   * 变速箱
   * 排量
   * 排放标准
   * 车身结构
   * 国别
   * 车身颜色
   *
   * 组装新的字段
   */
  public static Map<String, Object> generQuery(int type) {

    FilterTerm term;
    if (TYPE_NORMAL == type) {
      term = getNormalFilterterm();
    } else if (TYPE_TODAY == type) {
      term = getTodayFilterTerm();
    } else {
      term = getDirectFilterTerm();
    }

    Map<String, Object> map = new HashMap<>();
    map.put("suitable", "0");
    map.put("city", HCDbUtil.getSavedCityId());

    //品牌
    if (term.getBrand_id() > 0) {
      map.put("brand_id", term.getBrand_id());
    }

    //车系
    if (term.getClass_id() > 0) {
      map.put("class_id", term.getClass_id());
    }

    //价格区间
    float lPrice = term.getLowPrice();
    float hPrice = term.getHighPrice();

    if (!(lPrice == 0F && hPrice == 0F)) {
      Number[] price = toArray(lPrice, hPrice);
      map.put("price", price);
    }

    //车龄
    int lYear = term.getFrom_year();
    int hYear = term.getTo_year();

    if (!(lYear == 0 && hYear == 0)) {
      long time[] = HCUtils.getYearInterval(lYear, hYear);
      Number[] year = toArray(time[0], time[1]);
      map.put("register_time", year);
    }

    //里程
    float lMile = term.getFrom_miles();
    float hMile = term.getTo_miles();

    if (!(lMile == 0F && hMile == 0F)) {
      Number[] mile = toArray(lMile, hMile);
      map.put("miles", mile);
    }

    int lgear = 0;
    int hgear = 0;
    switch (term.getGearboxType()) {
      case 0://不限
        lgear = 0;
        hgear = 0;
        break;
      case 1: //手动
        lgear = 1;
        hgear = 1;
        break;
      case 2://自动
        lgear = 2;
        hgear = 5;
        break;
    }

    if (!(lgear == 0 && hgear == 0)) {
      //变速箱
      Number[] gear = toArray(lgear, hgear);
      map.put("gear", gear);
    }

    //排量
    float lEmission = term.getFrom_emission();
    float hEmission = term.getTo_emission();

    if (!(lEmission == 0F && hEmission == 0F)) {
      Number[] emission = toArray(lEmission, hEmission);
      map.put("emission", emission);
    }

    //排放标准
    int lstan = 0;
    int hstan = 0;
    switch (term.getStandard()) {
      case 0:  //不限
        lstan = 0;
        hstan = 0;
        break;
      case 1: //国三
        lstan = 1;
        hstan = 3;
        break;
      case 2: //国四
        lstan = 2;
        hstan = 3;
        break;
      case 3:  //国五
        lstan = 3;
        hstan = 3;
        break;
    }
    if (!(lstan == 0 && hstan == 0)) {
      Number[] standard = toArray(lstan, hstan);
      map.put("es", standard);
    }

    //车身结构
    String structure = term.getStructure() + "";
    if (HCUtils.str2Int(structure) > 0) {
      map.put("structure", structure);
    }

    //国别
    int country = term.getCounty();
    if (country > 0) {
      String[] countries = HCUtils.getResArray(R.array.hc_filter_country_temp);
      map.put("country", countries[country]);
    }

    //颜色
    int color = term.getColor();
    if (color > 0) {
      String[] colors = HCUtils.getResArray(R.array.hc_filter_color_temp);
      map.put("color", colors[color]);
    }

    return map;
  }

  private static Number[] toArray(Number... objs) {
    Number[] numbers = new Number[2];
    if (objs != null && objs.length == 2) {
      if (objs[1].intValue() == 0) {
        objs[1] = 1000;
      }
      numbers[0] = objs[0];
      numbers[1] = objs[1];
    }
    return numbers;
  }

  public static void saveQueryToFilterTerm(HCQueryEntity query) {
    if (query == null) return;
    FilterTerm filterTerm = getNormalFilterterm();

    int brand_id = HCUtils.str2Int(query.getBrand_id());
    int class_id = HCUtils.str2Int(query.getClass_id());

    if (brand_id > 0) {

      filterTerm.setBrand_id(brand_id);
      if (class_id > 0) {
        filterTerm.setClass_id(class_id);
      }
    }

    //价格
    List<String> ps = query.getPrice();
    if (ps != null && ps.size() == 2) {
      int lprice = HCUtils.str2Int(ps.get(0));
      int hprice = HCUtils.str2Int(ps.get(1));
      filterTerm.setLowPrice(lprice);
      filterTerm.setHighPrice(hprice);
    }

    //排放标准
    List<Integer> es = query.getEs();
    if (es != null && es.size() == 2) {
      //lstan, hstan
      int lstan = es.get(0);
      int hstan = es.get(1);

      filterTerm.setFrom_emission(0);
      filterTerm.setTo_emission(0);

      if (lstan == 1 && hstan == 3) {
        //国三及以上
        filterTerm.setFrom_emission(1);
        filterTerm.setTo_emission(3);
      } else if (lstan == 2 && hstan == 3) {
        //国四及以上
        filterTerm.setFrom_emission(2);
        filterTerm.setTo_emission(3);
      } else if (lstan == 3 && hstan == 3) {
        //国五及以上
        filterTerm.setFrom_emission(3);
        filterTerm.setTo_emission(3);
      }
    }

    //排量 不可以筛选

    //变速箱 变速箱类型 0:不限 1:手动 2:自动
    filterTerm.setGearboxType(0);
    List<Integer> gears = query.getGear();
    if (gears != null && gears.size() == 2) {
      int lgear = gears.get(0);
      int hgear = gears.get(1);
      if (lgear == 2 && hgear == 5) {
        //自动
        filterTerm.setGearboxType(2);
      } else if (lgear == 1 && hgear == 1) {
        //手动
        filterTerm.setGearboxType(1);
      }
    }

    //车龄
    List<Integer> years = query.getRegister_time();
    if (years != null && years.size() == 2) {
      long lyear = years.get(0);
      long hyear = years.get(1);
      int diffHigh = diffNow(lyear);
      int diffLow = diffNow(hyear);
      //这么写是为了防止server返回违法数据,只有这些条件才会设置值
      filterTerm.setFrom_year(diffLow);
      filterTerm.setTo_year(diffHigh);
    }

    //车身结构
    if (!TextUtils.isEmpty(query.getStructure())) {
      int structure = HCUtils.str2Int(query.getStructure());
      String[] structureArr = HCUtils.getResArray(R.array.hc_filter_car_type);
      //默认设置不限,返回正常才进入设置值
      filterTerm.setStructure(0);
      if (structure > 0 && structure < structureArr.length) {
        filterTerm.setStructure(structure);
      }
    }

    //颜色
    if (!TextUtils.isEmpty(query.getColor())) {
      String[] colorArr = HCUtils.getResArray(R.array.hc_filter_color);
      filterTerm.setColor(0);
      int color = getStringPosition(colorArr, query.getColor());
      if (color > 0 && color < colorArr.length) {
        filterTerm.setColor(color);
      }
    }

    setNormalFilterterm(filterTerm);
  }

  public static TreeMap<Integer, String> getConditions() {
    TreeMap<Integer, String> map = new TreeMap<>();
    FilterTerm term = getNormalFilterterm();

    //品牌
    String brandName = null;
    String className = null;

    if (term.getClass_id() > 0) {
      SeriesEntity se = SeriesDAO.getInstance().findSerisById(term.getClass_id());
      if (se != null) {
        className = se.getName();
        brandName = se.getBrand_name();
      }
    }

    if (TextUtils.isEmpty(brandName)) {
      int brand_id = term.getBrand_id();
      if (brand_id > 0) {
        if (BrandDAO.getInstance().get(brand_id) != null) {
          HCBrandEntity entity = (HCBrandEntity) BrandDAO.getInstance().get(brand_id);
          brandName = entity.getBrand_name();
        }
      }
    }

    if (!TextUtils.isEmpty(brandName)) {
      map.put(HCConsts.FILTER_BRAND, brandName);
    }

    if (!TextUtils.isEmpty(className)) {
      map.put(HCConsts.FILTER_SERIES, className);
    }

    //价格
    float lprice = term.getLowPrice();
    float hprice = term.getHighPrice();
    if (lprice > 0 && hprice > 0) {
      map.put(HCConsts.FILTER_PRICE, (int) lprice + CHAR_SPLIT + (int) hprice + STR_WAN);
    } else if (lprice > 0 && hprice == 0) {
      map.put(HCConsts.FILTER_PRICE, (int) lprice + STR_WAN + STR_ABOVE);
    } else if (lprice == 0 && hprice > 0) {
      map.put(HCConsts.FILTER_PRICE, (int) hprice + STR_WAN + STR_WITHIN);
    }

    //更多
    //车龄
    int fromYear = term.getFrom_year();
    int toYear = term.getTo_year();
    if (fromYear > 0 && toYear > 0) {
      map.put(HCConsts.FILTER_CAR_AGE, fromYear + CHAR_SPLIT + toYear + STR_YEAR);
    } else if (fromYear > 0 && toYear == 0) {
      map.put(HCConsts.FILTER_CAR_AGE, fromYear + STR_YEAR + STR_ABOVE);
    } else if (fromYear == 0 && toYear > 0) {
      map.put(HCConsts.FILTER_CAR_AGE, toYear + STR_YEAR + STR_WITHIN);
    }

    //里程
    int fromMiles = term.getFrom_miles();
    int toMiles = term.getTo_miles();
    if (fromMiles > 0 && toMiles > 0) {
      map.put(HCConsts.FILTER_DISTANCE, fromMiles + CHAR_SPLIT + toMiles + STR_WAN + STR_MILES);
    } else if (fromMiles > 0 && toMiles == 0) {
      map.put(HCConsts.FILTER_DISTANCE, fromMiles + STR_WAN + STR_MILES + STR_ABOVE);
    } else if (fromMiles == 0 && toMiles > 0) {
      map.put(HCConsts.FILTER_DISTANCE, toMiles + STR_WAN + STR_MILES + STR_WITHIN);
    }

    //变速箱
    int speed = term.getGearboxType();
    String[] speedboxArr = HCUtils.getResArray(R.array.hc_filter_speed_box);
    if (speed > 0 && speed < speedboxArr.length) {
      map.put(HCConsts.FILTER_SPEED_BOX, speedboxArr[speed]);
    }

    //排放标准
    int standard = term.getStandard();
    String[] standardArr = HCUtils.getResArray(R.array.hc_filter_standard);
    if (standard > 0 && standard < standardArr.length) {
      map.put(HCConsts.FILTER_STANDARD, standardArr[standard]);
    }

    //车身结构
    int struc = term.getStructure();
    String[] structureArr = HCUtils.getResArray(R.array.hc_filter_car_type);
    if (struc > 0 && struc < structureArr.length) {
      map.put(HCConsts.FILTER_CAR_TYPE, structureArr[struc]);
    }

    //排量
    float fromEmission = term.getFrom_emission();
    float toEmission = term.getTo_emission();
    if (fromEmission > 0 && toEmission > 0) {
      map.put(HCConsts.FILTER_EMISSION,
          fromEmission + STR_LITER + CHAR_SPLIT + toEmission + STR_LITER);
    } else if (fromEmission > 0 && toEmission == 0) {
      map.put(HCConsts.FILTER_EMISSION, fromEmission + STR_LITER + STR_ABOVE);
    } else if (fromEmission == 0 && toEmission > 0) {
      map.put(HCConsts.FILTER_EMISSION, toEmission + STR_LITER + STR_AND_BOTTOM);
    }

    //国别
    int country = term.getCounty();
    String[] countriesArr = HCUtils.getResArray(R.array.hc_filter_country);
    if (country > 0 && country < countriesArr.length) {
      map.put(HCConsts.FILTER_COUNTRY, countriesArr[country]);
    }

    //车身颜色
    int color = term.getColor();
    String[] colorArr = HCUtils.getResArray(R.array.hc_filter_color);
    if (color > 0 && color < colorArr.length) {
      map.put(HCConsts.FILTER_COLOR, colorArr[color]);
    }
    return map;
  }

  public static String getBrandName(FilterTerm term) {
    int brand_id = term.getBrand_id();
    if (brand_id > 0) {
      if (BrandDAO.getInstance().get(brand_id) != null) {
        HCBrandEntity entity = (HCBrandEntity) BrandDAO.getInstance().get(brand_id);
        return entity.getBrand_name();
      }
    }
    return "未知";
  }

  public static String getPrice(FilterTerm term) {
    float lprice = term.getLowPrice();
    float hprice = term.getHighPrice();
    if (lprice > 0 && hprice > 0) {
      return (int) lprice + CHAR_SPLIT + (int) hprice + STR_WAN;
    } else if (lprice > 0 && hprice == 0) {
      return (int) lprice + STR_WAN + STR_ABOVE;
    } else if (lprice == 0 && hprice > 0) {
      return (int) hprice + STR_WAN + STR_WITHIN;
    } else {
      return STR_UNLIMITED;
    }
  }

  public static String getPrice(int lprice, int hprice) {
    if (lprice > 0 && hprice > 0) {
      return lprice + CHAR_SPLIT + hprice + STR_WAN;
    } else if (lprice > 0 && hprice == 0) {
      return lprice + STR_WAN + STR_ABOVE;
    } else if (lprice == 0 && hprice > 0) {
      return hprice + STR_WAN + STR_WITHIN;
    } else {
      return STR_UNLIMITED;
    }
  }

  public static String getAge(FilterTerm term) {
    int fromYear = term.getFrom_year();
    int toYear = term.getTo_year();
    if (fromYear > 0 && toYear > 0) {
      return fromYear + CHAR_SPLIT + toYear + STR_YEAR;
    } else if (fromYear > 0 && toYear == 0) {
      return fromYear + STR_YEAR + STR_ABOVE;
    } else if (fromYear == 0 && toYear > 0) {
      return toYear + STR_YEAR + STR_WITHIN;
    } else {
      return STR_UNLIMITED;
    }
  }

  public static String getMiles(FilterTerm term) {
    int fromMiles = term.getFrom_miles();
    int toMiles = term.getTo_miles();
    if (fromMiles > 0 && toMiles > 0) {
      return fromMiles + CHAR_SPLIT + toMiles + STR_WAN + STR_MILES;
    } else if (fromMiles > 0 && toMiles == 0) {
      return fromMiles + STR_WAN + STR_MILES + STR_ABOVE;
    } else if (fromMiles == 0 && toMiles > 0) {
      return toMiles + STR_WAN + STR_MILES + STR_WITHIN;
    } else {
      return STR_UNLIMITED;
    }
  }

  public static String getGearBox(FilterTerm term) {
    int speed = term.getGearboxType();
    String[] speedboxArr = HCUtils.getResArray(R.array.hc_filter_speed_box);
    if (speed > 0 && speed < speedboxArr.length) {
      return speedboxArr[speed];
    } else {
      return STR_UNLIMITED;
    }
  }

  public static String getGearBox(int speed) {
    String[] speedboxArr = HCUtils.getResArray(R.array.hc_filter_speed_box);
    if (speed > 0 && speed < speedboxArr.length) {
      return speedboxArr[speed];
    } else {
      return STR_UNLIMITED;
    }
  }

  public static String getStandard(FilterTerm term) {
    int standard = term.getStandard();
    String[] standardArr = HCUtils.getResArray(R.array.hc_filter_standard);
    if (standard > 0 && standard < standardArr.length) {
      return standardArr[standard];
    } else {
      return STR_UNLIMITED;
    }
  }

  public static String getStructure(FilterTerm term) {
    int struc = term.getStructure();
    String[] structureArr = HCUtils.getResArray(R.array.hc_filter_car_type);
    if (struc > 0 && struc < structureArr.length) {
      return structureArr[struc];
    } else {
      return STR_UNLIMITED;
    }
  }

  public static String getEmission(FilterTerm term) {
    float fromEmission = term.getFrom_emission();
    float toEmission = term.getTo_emission();
    if (fromEmission > 0 && toEmission > 0) {
      return fromEmission + STR_LITER + CHAR_SPLIT + toEmission + STR_LITER;
    } else if (fromEmission > 0 && toEmission == 0) {
      return fromEmission + STR_LITER + STR_ABOVE;
    } else if (fromEmission == 0 && toEmission > 0) {
      return toEmission + STR_LITER + STR_AND_BOTTOM;
    } else {
      return STR_UNLIMITED;
    }
  }

  public static String getCountry(FilterTerm term) {
    int country = term.getCounty();
    String[] countryArr = HCUtils.getResArray(R.array.hc_filter_country);
    if (country > 0 && country < countryArr.length) {
      return countryArr[country];
    } else {
      return STR_UNLIMITED;
    }
  }

  public static String getColor(FilterTerm term) {
    int color = term.getColor();
    String[] colorArr = HCUtils.getResArray(R.array.hc_filter_color);
    if (color > 0 && color < colorArr.length) {
      return colorArr[color];
    } else {
      return STR_UNLIMITED;
    }
  }

  /** 当前Filterterm对应的订阅条件是否订阅过 */
  public static boolean isCurrentSubscribed() {
    SubConditionDataEntity sde = getSubscribeByFilterTerm();

    HCLog.d(TAG, "term to sub " + sde.toString());

    List<SubConditionDataEntity> allSub = HCSpUtils.getAllSubscribe();

    return allSub.contains(sde);
  }

  public static SubConditionDataEntity getSubscribeByFilterTerm() {

    FilterTerm term = getNormalFilterterm();

    SubConditionDataEntity sub = new SubConditionDataEntity();

    //品牌  车系
    if (term.getBrand_id() > 0) {
      sub.setBrand_id(String.valueOf(term.getBrand_id()));
    }

    if (term.getClass_id() > 0) {
      sub.setClass_id(String.valueOf(term.getClass_id()));
    }

    if (TextUtils.isEmpty(sub.getBrand_id())) {
      sub.setBrand_id("0");
    }

    if (TextUtils.isEmpty(sub.getClass_id())) {
      sub.setClass_id("0");
    }

    sub.setPrice_low(convert(term.getLowPrice()));
    sub.setPrice_high(convert(term.getHighPrice()));

    sub.setGeerbox(convert(term.getGearboxType()));

    sub.setYear_low(convert(term.getFrom_year()));
    sub.setYear_high(convert(term.getTo_year()));

    sub.setMiles_low(convert(term.getFrom_miles()));
    sub.setMiles_high(convert(term.getTo_miles()));

    sub.setEmission_low(convert(term.getFrom_emission()));
    sub.setEmission_high(convert(term.getTo_emission()));

    //* 排放标准 排放标准:0国二 1国三 2国四 3国五
    int es = term.getStandard();
    if (es == 0) {
      sub.setEs_standard("-1");
    } else {
      sub.setEs_standard(convert(term.getStandard()));
    }

    sub.setStructure(convert(term.getStructure()));

    //国别
    sub.setCountry("");
    int country = term.getCounty();
    if (country > 0) {
      String[] countries = HCUtils.getResArray(R.array.hc_filter_country_temp);
      sub.setCountry(countries[country]);
    }

    //颜色
    sub.setColor("");
    int color = term.getColor();
    if (color > 0) {
      String[] colors = HCUtils.getResArray(R.array.hc_filter_color_temp);
      sub.setColor(colors[color]);
    }

    return sub;
  }

  private static String convert(Number input) {
    float f = input.floatValue();
    int i = (int) f;
    if (f > i) {
      return String.valueOf(f);
    } else {
      return String.valueOf(i);
    }
  }

  //---------------------------------------------------------------------------------//

  public static FilterTerm getNormalFilterterm() {
    return GlobalData.userDataHelper.getNormalFilterterm();
  }

  public static FilterTerm getDirectFilterTerm() {
    return GlobalData.userDataHelper.getDirectFilterTerm();
  }

  public static FilterTerm getTodayFilterTerm() {
    return GlobalData.userDataHelper.getTodayFilterterm();
  }

  public static FilterTerm getFilterTerm(String host) {
    if (allHost.equals(host)) {
      return getNormalFilterterm();
    } else if (directHost.equals(host)) {
      return getDirectFilterTerm();
    } else {
      return getTodayFilterTerm();
    }
  }

  private static void setNormalFilterterm(FilterTerm term) {
    GlobalData.userDataHelper.setNormalFilterterm(term);
  }

  private static void setDirectFilterTerm(FilterTerm term) {
    GlobalData.userDataHelper.setDirectFilterTerm(term);
  }

  private static void setTodayFilterterm(FilterTerm term) {
    GlobalData.userDataHelper.setTodayFilterterm(term);
  }

  public static void setFilterTerm(String host, FilterTerm term) {
    if (allHost.equals(host)) {
      setNormalFilterterm(term);
    } else if (directHost.equals(host)) {
      setDirectFilterTerm(term);
    } else {
      setTodayFilterterm(term);
    }
  }

  /** 获取默认排序 数组内容 */
  public static List<KeyValueEntity> getDefaultSortData(int sortType) {
    int res = -1;
    switch (sortType) {
      case HCConsts.FILTER_SORT:
        res = R.array.hc_filter_sort;
        break;
      case HCConsts.FILTER_PRICE:
        res = R.array.hc_filter_price;
        break;
      case HCConsts.FILTER_CAR_TYPE:
        res = R.array.hc_filter_more_car_type;
        break;
      case HCConsts.FILTER_CAR_AGE:
        res = R.array.hc_filter_more_car_age;
        break;
      case HCConsts.FILTER_DISTANCE:
        res = R.array.hc_filter_more_distance;
        break;
      case HCConsts.FILTER_SPEED_BOX:
        res = R.array.hc_filter_more_speed_box;
        break;
      case HCConsts.FILTER_STANDARD:
        res = R.array.hc_filter_more_standard;
        break;
      case HCConsts.FILTER_EMISSION:
        res = R.array.hc_filter_more_emissions;
        break;
      case HCConsts.FILTER_COUNTRY:
        res = R.array.hc_filter_more_country;
        break;
      case HCConsts.FILTER_COLOR:
        res = R.array.hc_filter_more_color;
        break;
    }

    List<KeyValueEntity> mList = new ArrayList<>();
    if (res > 0) {
      String[] arr = HCUtils.getResArray(res);
      for (int i = 0; i < arr.length; i++) {
        mList.add(new KeyValueEntity(arr[i]));
      }
    }
    return mList;
  }

  public static String getPointOrder(String sortType) {
    if (SORT_STR[0].equals(sortType)) {
      //综合排序
      return SORT_TYPE_TIME;
    } else if (SORT_STR[1].equals(sortType)) {
      //价格低到高
      return SORT_TYPE_PRICE;
    } else if (SORT_STR[2].equals(sortType)) {
      //价格高到低
      return SORT_TYPE_PRICE;
    } else if (SORT_STR[3].equals(sortType)) {
      //车龄新到旧
      return SORT_TYPE_REGISTER_TIME;
    } else if (SORT_STR[4].equals(sortType)) {
      //里程短到长
      return SORT_TYPE_MILES;
    } else {
      return SORT_TYPE_TIME;
    }
  }

  public static int getPointSort(String sortType) {
    if (SORT_STR[0].equals(sortType)) {
      //综合排序
      return ORDER_DESC;
    } else if (SORT_STR[1].equals(sortType)) {
      //价格低到高
      return ORDER_ASC;
    } else if (SORT_STR[2].equals(sortType)) {
      //价格高到低
      return ORDER_DESC;
    } else if (SORT_STR[3].equals(sortType)) {
      //车龄新到旧
      return ORDER_DESC;
    } else if (SORT_STR[4].equals(sortType)) {
      //里程短到长
      return ORDER_ASC;
    } else {
      return ORDER_DESC;
    }
  }

  public static void saveSort(String host, String value) {
    String order = FilterUtils.getPointOrder(value);
    int sort = FilterUtils.getPointSort(value);
    FilterTerm term = FilterUtils.getFilterTerm(host);
    term.setSort(sort);
    term.setOrder(order);
    term.setDescriptionSort(value);
    setFilterTerm(host, term);
  }

  public static void changeFilterTerm(FilterTerm term, int type, String data) {
    int[][] carAgeValues = { { 0, 1 }, { 0, 3 }, { 0, 5 }, { 0, 8 }, { 8, 0 } };
    int[][] distanceValues = { { 0, 3 }, { 0, 5 }, { 0, 8 }, { 0, 10 }, { 10, 0 } };
    float[][] emissionsValues = {
        { 0F, 1.2F }, { 1.3F, 1.6F }, { 1.7F, 2.0F }, { 2.1F, 3.0F }, { 3.1F, 4.0F }, { 4.1F, 0F }
    };

    String[] carAges = HCUtils.getResArray(R.array.hc_filter_more_car_age);
    String[] distances = HCUtils.getResArray(R.array.hc_filter_more_distance);
    String[] speedBoxes = HCUtils.getResArray(R.array.hc_filter_speed_box);
    String[] standards = HCUtils.getResArray(R.array.hc_filter_standard);
    String[] carTypes = HCUtils.getResArray(R.array.hc_filter_car_type);
    String[] emissions = HCUtils.getResArray(R.array.hc_filter_more_emissions);
    String[] countries = HCUtils.getResArray(R.array.hc_filter_country);
    String[] colors = HCUtils.getResArray(R.array.hc_filter_color);

    int position;
    switch (type) {
      case HCConsts.FILTER_CAR_AGE:
        position = getStringPosition(carAges, data);
        int[] age = carAgeValues[position];
        term.setFrom_year(age[0]);
        term.setTo_year(age[1]);
        break;
      case HCConsts.FILTER_DISTANCE:
        position = getStringPosition(distances, data);
        int[] distance = distanceValues[position];
        term.setFrom_miles(distance[0]);
        term.setTo_miles(distance[1]);
        break;
      case HCConsts.FILTER_SPEED_BOX:
        position = getStringPosition(speedBoxes, data);
        term.setGearboxType(position);
        break;
      case HCConsts.FILTER_STANDARD:
        position = getStringPosition(standards, data);
        term.setStandard(position);
        break;
      case HCConsts.FILTER_CAR_TYPE:
        position = getStringPosition(carTypes, data);
        term.setStructure(position);
        break;
      case HCConsts.FILTER_EMISSION:
        position = getStringPosition(emissions, data);
        float[] emission = emissionsValues[position];
        term.setFrom_emission(emission[0]);
        term.setTo_emission(emission[1]);
        break;
      case HCConsts.FILTER_COUNTRY:
        position = getStringPosition(countries, data);
        term.setCounty(position);
        break;
      case HCConsts.FILTER_COLOR:
        position = getStringPosition(colors, data);
        term.setColor(position);
        break;
    }
  }

  private static int getStringPosition(String[] arr, String str) {
    int position = 0;
    for (int i = 0; i < arr.length; i++) {
      if (str.equals(arr[i])) {
        position = i;
        break;
      }
    }
    return position;
  }

  public static String getFilterTermString(FilterTerm term, int type) {
    int[][] carAgeValues = { { 0, 1 }, { 0, 3 }, { 0, 5 }, { 0, 8 }, { 8, 0 } };
    int[][] distanceValues = { { 0, 3 }, { 0, 5 }, { 0, 8 }, { 0, 10 }, { 10, 0 } };
    float[][] emissionsValues = {
        { 0F, 1.2F }, { 1.3F, 1.6F }, { 1.7F, 2.0F }, { 2.1F, 3.0F }, { 3.1F, 4.0F }, { 4.1F, 0F }
    };

    String[] carAges = HCUtils.getResArray(R.array.hc_filter_more_car_age);
    String[] distances = HCUtils.getResArray(R.array.hc_filter_more_distance);
    String[] speedBoxes = HCUtils.getResArray(R.array.hc_filter_speed_box);
    String[] standards = HCUtils.getResArray(R.array.hc_filter_standard);
    String[] carTypes = HCUtils.getResArray(R.array.hc_filter_car_type);
    String[] emissions = HCUtils.getResArray(R.array.hc_filter_more_emissions);
    String[] countries = HCUtils.getResArray(R.array.hc_filter_country);
    String[] colors = HCUtils.getResArray(R.array.hc_filter_color);
    String str = "";
    switch (type) {
      case HCConsts.FILTER_PRICE:
        int priceLow = (int) term.getLowPrice();
        int priceHigh = (int) term.getHighPrice();
        if (priceLow == 0 && priceHigh == 0) {
          str = STR_UNLIMITED;
        } else if (priceLow != 0 && priceHigh == 0) {
          str = priceLow + STR_WAN + STR_ABOVE;
        } else if (priceLow == 0 && priceHigh != 0) {
          str = priceHigh + STR_WAN + STR_WITHIN;
        } else {
          str = priceLow + CHAR_SPLIT + priceHigh + STR_WAN;
        }
        break;
      case HCConsts.FILTER_CAR_AGE:
        int carAgeFrom = term.getFrom_year();
        int carAgeTo = term.getTo_year();
        for (int i = 0; i < carAgeValues.length; i++) {
          if (carAgeFrom == carAgeValues[i][0] && carAgeTo == carAgeValues[i][1]) {
            str = carAges[i];
            break;
          }
        }
        break;
      case HCConsts.FILTER_DISTANCE:
        int distanceFrom = term.getFrom_miles();
        int distanceTo = term.getTo_miles();
        for (int i = 0; i < distanceValues.length; i++) {
          if (distanceFrom == distanceValues[i][0] && distanceTo == distanceValues[i][1]) {
            str = distances[i];
            break;
          }
        }
        break;
      case HCConsts.FILTER_SPEED_BOX:
        int gearboxType = term.getGearboxType();
        str = speedBoxes[gearboxType];
        break;
      case HCConsts.FILTER_STANDARD:
        int standard = term.getStandard();
        str = standards[standard];
        break;
      case HCConsts.FILTER_CAR_TYPE:
        int structure = term.getStructure();
        str = carTypes[structure];
        break;
      case HCConsts.FILTER_EMISSION:
        float emissionFrom = term.getFrom_emission();
        float emissionTo = term.getTo_emission();
        for (int i = 0; i < emissionsValues.length; i++) {
          if (emissionFrom == emissionsValues[i][0] && emissionTo == emissionsValues[i][1]) {
            str = emissions[i];
            break;
          }
        }
        break;
      case HCConsts.FILTER_COUNTRY:
        int county = term.getCounty();
        str = countries[county];
        break;
      case HCConsts.FILTER_COLOR:
        int color = term.getColor();
        str = colors[color];
        break;
    }
    return str;
  }

  public static boolean isMoreDefault(FilterTerm term) {
    boolean result = true;
    if (term.getFrom_year() != 0 || term.getTo_year() != 0) {
      result = false;
    } else if (term.getFrom_miles() != 0 || term.getTo_miles() != 0) {
      result = false;
    } else if (term.getFrom_emission() != 0 || term.getTo_emission() != 0) {
      result = false;
    } else if (term.getGearboxType() != 0) {
      result = false;
    } else if (term.getStandard() != 0) {
      result = false;
    } else if (term.getStructure() != 0) {
      result = false;
    } else if (term.getCounty() != 0) {
      result = false;
    } else if (term.getColor() != 0) {
      result = false;
    }
    return result;
  }

  public static FilterTerm bangMaiToFilterTerm(PushMsgDataEntity entity) {
    FilterTerm term = new FilterTerm();

    if (entity.getBrand_id() != 0) {
      if (entity.getClass_id() != 0) {
        term.setClass_id(entity.getClass_id());
      }
      term.setBrand_id(entity.getBrand_id());
    }
    List<Double> price = entity.getPrice();
    if (!HCUtils.isListEmpty(price) && price.size() == 2) {
      Double low = price.get(0);
      Double high = price.get(1);
      term.setLowPrice(HCUtils.str2Float(low + ""));
      term.setHighPrice(HCUtils.str2Float(high + ""));
    }
    return term;
  }
}
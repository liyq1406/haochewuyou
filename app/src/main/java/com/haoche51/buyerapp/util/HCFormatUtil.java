package com.haoche51.buyerapp.util;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import com.haoche51.buyerapp.R;
import com.haoche51.buyerapp.dao.BrandDAO;
import com.haoche51.buyerapp.dao.SeriesDAO;
import com.haoche51.buyerapp.data.FilterTerm;
import com.haoche51.buyerapp.entity.HCBookOrderEntity;
import com.haoche51.buyerapp.entity.HCBrandEntity;
import com.haoche51.buyerapp.entity.SeriesEntity;
import com.haoche51.buyerapp.entity.SubConditionDataEntity;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressLint({ "DefaultLocale", "SimpleDateFormat" }) public class HCFormatUtil {

  public static final long MAX10 = 9999999999L;

  private static final String VEHICLE_INFO_FORMAT_COMMON = "%s上牌 · %s万公里 · %s";

  public static String getVehicleFormat(String time, String miles, String gearbox) {
    return String.format(VEHICLE_INFO_FORMAT_COMMON, time, miles, gearbox);
  }

  /** 车辆详情展示格式 */
  private static final String VEHICLE_INFO_FORMAT = "%d.%d上牌 · %s万公里 · %s";

  public static String getVehicleFormat(int registYear, int registMonth, float miles, int gearbox) {
    return String.format(VEHICLE_INFO_FORMAT, registYear, registMonth, String.valueOf(miles),
        getGearboxString(gearbox));
  }

  /** 获取变速箱类型 */
  private static String getGearboxString(int gearbox) {
    String[] gearArr = HCUtils.getResArray(R.array.gearbox_array);
    if (gearbox >= 1 && gearbox <= 5) return gearArr[gearbox];

    return gearArr[0];
  }

  private static final String SIMPLE_VEHICLE_DETAIL = "%s上牌 · %s万公里";

  public static String getSimpleVehicleDetail(String time, String miles) {
    return String.format(SIMPLE_VEHICLE_DETAIL, time, miles);
  }

  /** 更多筛选结果展示格式 */
  public static final String MORE_RESULT = "为您找到<font color=#ff2626>%s</font>辆车";

  public static String getMoreResultFormat(String count) {
    return String.format(MORE_RESULT, count);
  }

  /** 汽车价格展示格式 */
  private static final String VEHICLE_RPICE_FORMAT = "<font color=%s>%s万</font>";

  public static String getSoldPriceFormat(double price) {
    double dp = HCArithUtil.round(price, 2);

    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    String str = decimalFormat.format(dp);

    return String.format(VEHICLE_RPICE_FORMAT, "#ff2626", String.valueOf(str));
  }

  private static final String VEHICLE_BIG_RPICE_FORMAT =
      "<font color=%s><small>￥</small><b><big>%s</big></b><small>万</small></font>";

  public static String getSoldPriceFormat(double price, int remainCounts) {
    double dp = HCArithUtil.round(price, remainCounts);
    return String.format(VEHICLE_BIG_RPICE_FORMAT, "#ff2626", String.valueOf(dp));
  }

  /** 汽车首付展示格式 */
  private static final String VEHICLE_RPICE_PAY_FORMAT = "<font color=%s>首付%s万</font>";

  public static String getPayPriceFormat(double price) {
    double dp = HCArithUtil.round(price, 2);

    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    String str = decimalFormat.format(dp);

    return String.format(VEHICLE_RPICE_PAY_FORMAT, "#9f9f9f", String.valueOf(str));
  }

  /** 获取车源名，解决排版混乱问题 */
  public static String getVehicleName(String input) {

    if (TextUtils.isEmpty(input)) return "";

    char[] c = input.toCharArray();
    for (int i = 0; i < c.length; i++) {
      if (c[i] == 12288) {
        c[i] = (char) 32;
        continue;
      }
      if (c[i] > 65280 && c[i] < 65375) c[i] = (char) (c[i] - 65248);
    }
    return new String(c);
  }

  private static final String WAN_PRICE = "￥%s万";

  public static String formatWanPrice(double price) {
    double dp = HCArithUtil.round(price, 2);
    return String.format(WAN_PRICE, dp);
  }

  private static final String MILES = "%s万公里";

  public static String formatMiles(double miles) {
    double dp = HCArithUtil.round(miles, 1);
    return String.format(MILES, dp);
  }

  private static SimpleDateFormat myFormat = new SimpleDateFormat("yyyy年MM月");

  public static String formatMonthYear(long time) {
    if (time < MAX10) {
      time = time * 1000;
    }
    return myFormat.format(new Date(time)) + "上牌";
  }

  // 第三个空格改成换行
  public static String formatVehicleName(String name) {
    StringBuilder sb = new StringBuilder();
    if (TextUtils.isEmpty(name)) return name;
    name = name.trim();
    if (name.contains(HCConsts.HC_BLANK)) {
      String[] ss = name.split(HCConsts.HC_BLANK);
      int length = ss.length;
      if (length >= 2) {
        for (int i = 0; i < length; i++) {
          sb.append(ss[i]);
          sb.append(i == 1 ? HCConsts.HC_ENTER : HCConsts.HC_BLANK);
        }
        name = sb.toString();
      }
    }
    return name;
  }

  private static SimpleDateFormat couponFormat = new SimpleDateFormat("yyyy.MM.dd");

  public static String formatCouponTime(long from_time, long to_time) {
    if (from_time < MAX10) {
      from_time = from_time * 1000;
    }
    if (to_time < MAX10) {
      to_time = to_time * 1000;
    }
    String fromStr = couponFormat.format(new Date(from_time));
    String toStr = couponFormat.format(new Date(to_time));
    return "有效期: " + fromStr + "-" + toStr;
  }

  private static final String SUB_PRICE = "%s~%s万";

  /** 10~15万元 */
  public static String formatSubPrice(float lowPrice, float highPrice) {
    String result = "";
    if (lowPrice > 0F && highPrice > 0F && highPrice <= 30F) {
      result = String.format(SUB_PRICE, (int) lowPrice, (int) highPrice);
    } else if (lowPrice > 0F && highPrice == 0F) {
      result = (int) (lowPrice) + "万以上";
    } else if (lowPrice == 0F && highPrice > 0F) {
      result = (int) (highPrice) + "万以内";
    } else if (lowPrice == 0F && highPrice == 2F) {
      result = "2万以内";
    } else if (lowPrice == 0F && highPrice == 0F) {
      result = "";
    } else if (lowPrice >= 30F) {
      result = "30万以上";
    }
    return result;
  }

  /**
   * 北京 · 10~12万 · 1~7年 · 3~8万公里 · 自动 · 国四 · SUV · 1.3L~1.6L · 国产 · 红色
   */
  public static String formatSubDetail(SubConditionDataEntity entity) {

    String result;

    /** 车龄 */
    int from_year = HCUtils.str2Int(entity.getYear_low());
    int to_year = HCUtils.str2Int(entity.getYear_high());

    String year = "";
    if (from_year != 0 && to_year == 0) {
      year = from_year + "年以上";
    } else if (from_year == 0 && to_year != 0) {
      year = to_year + "年以内";
    } else if (from_year >= 1 && to_year <= 10) {
      year = from_year + "~" + to_year + "年";
    }
    year = TextUtils.isEmpty(year) ? year : year + DOT;

    /** 里程 */
    int from_miles = HCUtils.str2Int(entity.getMiles_low());
    int to_miles = HCUtils.str2Int(entity.getMiles_high());

    String miles = "";
    if (from_miles == 0 && to_miles > 0) {
      miles = to_miles + "万公里以内";
    } else if (from_miles > 0 && to_miles == 0) {
      miles = from_miles + "万公里以上";
    } else if (from_miles >= 1 && to_miles <= 8) {
      miles = from_miles + "~" + to_miles + "万公里";
    }
    if (!TextUtils.isEmpty(miles)) {
      miles = miles + DOT;
    }

    /** 变速箱 */
    int gearboxType = HCUtils.str2Int(entity.getGeerbox());

    String gearbox = gearboxType == 0 ? "" : (gearboxType == 1 ? "手动" : "自动");
    if (!TextUtils.isEmpty(gearbox)) {
      gearbox = gearbox + DOT;
    }

    /** 排放标准 */
    int indexStand = HCUtils.str2Int(entity.getEs_standard());

    String standard = "";
    String[] standArr = HCUtils.getResArray(R.array.hc_filter_standard);
    if (indexStand > 0 && indexStand < standArr.length) {
      standard = standArr[indexStand];
    }
    standard = TextUtils.isEmpty(standard) ? standard : standard + DOT;

    /** 车身结构 */
    int indexStructure = HCUtils.str2Int(entity.getStructure());

    String struc = "";
    String[] arr = HCUtils.getResArray(R.array.hc_filter_car_type);
    if (indexStructure > 0 && indexStructure < arr.length) {
      struc = arr[indexStructure];
    }
    struc = TextUtils.isEmpty(struc) ? struc : struc + DOT;

    /** 排量 */
    float from_emission = HCUtils.str2Float(entity.getEmission_low());
    float to_emission = HCUtils.str2Float(entity.getEmission_high());

    String emission = "";
    if (from_emission > 0F && to_emission == 0F) {
      emission = from_emission + "L以上";
    } else if (from_emission == 0F && to_emission > 0F) {
      emission = to_emission + "L及以下";
    } else if (from_emission > 0F && to_emission > 0F) {
      emission = from_emission + "~" + to_emission + "L";
    }
    emission = TextUtils.isEmpty(emission) ? emission : emission + DOT;

    /** 国别 */
    String country = entity.getCountry();
    String countryEnd = "";
    String[] countryArrTemp = HCUtils.getResArray(R.array.hc_filter_country_temp);
    String[] countryArr = HCUtils.getResArray(R.array.hc_filter_country);
    int countryIndex = getStringPosition(countryArrTemp, country);
    if (countryIndex > 0 && countryIndex < countryArr.length) {
      countryEnd = countryArr[countryIndex];
    }
    countryEnd = TextUtils.isEmpty(countryEnd) ? countryEnd : countryEnd + DOT;

    /** 颜色 */
    String color = entity.getColor();
    String colorEnd = "";
    String[] colorArrTemp = HCUtils.getResArray(R.array.hc_filter_color_temp);
    String[] colorArr = HCUtils.getResArray(R.array.hc_filter_color);
    int colorIndex = getStringPosition(colorArrTemp, color);
    if (colorIndex > 0 && colorIndex < colorArr.length) {
      colorEnd = colorArr[colorIndex];
    }
    colorEnd = TextUtils.isEmpty(colorEnd) ? colorEnd : colorEnd + DOT;

    /** 拼接字符串  */
    result = year + miles + gearbox + standard + struc + emission + countryEnd + colorEnd;

    float lowPrice = HCUtils.str2Float(entity.getPrice_low());
    float highPrice = HCUtils.str2Float(entity.getPrice_high());
    String priceStr = HCFormatUtil.formatSubPrice(lowPrice, highPrice);

    if (TextUtils.isEmpty(priceStr) && TextUtils.isEmpty(result)) {
      priceStr = "价格不限" + DOT + "里程不限" + DOT + "变速箱不限";
    }

    String cityStr = HCDbUtil.getCityNameById(entity.getCity_id());
    cityStr = TextUtils.isEmpty(cityStr) ? cityStr : cityStr + DOT;

    priceStr = TextUtils.isEmpty(priceStr) ? priceStr : priceStr + DOT;

    priceStr = cityStr + priceStr;

    result = priceStr + result;

    if (result.endsWith(DOT)) {
      int index = result.lastIndexOf(DOT);
      result = result.substring(0, index);
    }

    return result;
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

  public static final String DOT = " · ";

  private static final String COMMA = " ,";

  /**
   * 奥迪 ,奥迪A4L ,10~12万 ,1~7年 ,3~8万公里 ,自动 ,国四 ,SUV ,1.3L~1.6L ,国产 ,白色
   */
  public static String formatFilterTerm(FilterTerm entity) {

    String result;
    /**  品牌车系*/
    String brandName = "";
    String className = "";

    if (entity.getClass_id() > 0) {
      SeriesEntity se = SeriesDAO.getInstance().findSerisById(entity.getClass_id());
      if (se != null) {
        className = se.getName();
        brandName = se.getBrand_name();
      }
    }

    if (TextUtils.isEmpty(brandName)) {
      int brand_id = entity.getBrand_id();
      if (brand_id > 0) {
        if (BrandDAO.getInstance().get(brand_id) != null) {
          HCBrandEntity en = (HCBrandEntity) BrandDAO.getInstance().get(brand_id);
          brandName = en.getBrand_name();
        }
      }
    }

    brandName = TextUtils.isEmpty(brandName) ? brandName : brandName + COMMA;
    className = TextUtils.isEmpty(className) ? className : className + COMMA;

    /** 车龄 */
    int from_year = entity.getFrom_year();
    int to_year = entity.getTo_year();

    String year = "";
    if (from_year != 0 && to_year == 0) {
      year = from_year + "年以上";
    } else if (from_year == 0 && to_year != 0) {
      year = to_year + "年以内";
    } else if (from_year >= 1 && to_year <= 10) {
      year = from_year + "~" + to_year + "年";
    }
    year = TextUtils.isEmpty(year) ? year : year + COMMA;

    /** 里程 */
    int from_miles = entity.getFrom_miles();
    int to_miles = entity.getTo_miles();

    String miles = "";
    if (from_miles == 0 && to_miles > 0) {
      miles = to_miles + "万公里以内";
    } else if (from_miles > 0 && to_miles == 0) {
      miles = from_miles + "万公里以上";
    } else if (from_miles >= 1 && to_miles <= 8) {
      miles = from_miles + "~" + to_miles + "万公里";
    }
    if (!TextUtils.isEmpty(miles)) {
      miles = miles + COMMA;
    }

    /** 变速箱 */
    int gearboxType = entity.getGearboxType();

    String gearbox = gearboxType == 0 ? "" : (gearboxType == 1 ? "手动" : "自动");
    if (!TextUtils.isEmpty(gearbox)) {
      gearbox = gearbox + COMMA;
    }

    /** 排放标准 */
    int indexStand = entity.getStandard();

    String standard = "";
    String[] standArr = HCUtils.getResArray(R.array.hc_filter_standard);
    if (indexStand > 0 && indexStand < standArr.length) {
      standard = standArr[indexStand];
    }
    standard = TextUtils.isEmpty(standard) ? standard : standard + COMMA;

    /** 车身结构 */
    int indexStructure = entity.getStructure();

    String struc = "";
    String[] arr = HCUtils.getResArray(R.array.hc_filter_car_type);
    if (indexStructure > 0 && indexStructure < arr.length) {
      struc = arr[indexStructure];
    }
    struc = TextUtils.isEmpty(struc) ? struc : struc + COMMA;

    /** 排量 */
    float from_emission = entity.getFrom_emission();
    float to_emission = entity.getTo_emission();

    String emission = "";
    if (from_emission > 0F && to_emission == 0F) {
      emission = from_emission + "L以上";
    } else if (from_emission == 0F && to_emission > 0F) {
      emission = to_emission + "L及以下";
    } else if (from_emission > 0F && to_emission > 0F) {
      emission = from_emission + "~" + to_emission + "L";
    }
    emission = TextUtils.isEmpty(emission) ? emission : emission + COMMA;

    /** 国别 */
    int indexCountry = entity.getCounty();
    String country = "";
    String[] countryArr = HCUtils.getResArray(R.array.hc_filter_country);
    if (indexCountry > 0 && indexCountry < countryArr.length) {
      country = countryArr[indexCountry];
    }
    country = TextUtils.isEmpty(country) ? country : country + COMMA;

    /** 颜色 */
    int indexColor = entity.getColor();
    String color = "";
    String[] colorArr = HCUtils.getResArray(R.array.hc_filter_color);
    if (indexColor > 0 && indexColor < colorArr.length) {
      color = colorArr[indexColor];
    }
    color = TextUtils.isEmpty(color) ? color : color + COMMA;

    /** 拼接字符串  */
    result = year + miles + gearbox + standard + struc + emission + country + color;

    float lowPrice = entity.getLowPrice();
    float highPrice = entity.getHighPrice();
    String priceStr = HCFormatUtil.formatSubPrice(lowPrice, highPrice);

    priceStr = TextUtils.isEmpty(priceStr) ? priceStr : priceStr + COMMA;

    result = brandName + className + priceStr + result;

    if (result.endsWith(COMMA)) {
      int index = result.lastIndexOf(COMMA);
      result = result.substring(0, index);
    }

    return result;
  }

  public static String getShowName(int brand_id, int class_id) {
    String bstr = HCDbUtil.getBrandNameById(brand_id);
    String sstr = HCDbUtil.getCarSeriesNameById(class_id);
    return !TextUtils.isEmpty(sstr) ? sstr : bstr;
  }

  public static String getOrderDetail(HCBookOrderEntity book) {
    String result;
    long register = book.getRegister_time();
    if (register < MAX10) register = register * 1000;
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(register);
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH) + 1;
    int geerbox = book.getGeerbox();
    float miles = book.getMile();
    result = getVehicleFormat(year, month, miles, geerbox);
    return result;
  }

  private static SimpleDateFormat orderFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

  public static String formatOrderTime(long time) {
    String result = "";
    if (time == 0) return result;
    if (time < MAX10) {
      time = time * 1000;
    }
    result = orderFormat.format(new Date(time));
    return result;
  }

  public static Html.ImageGetter getImageGetterInstance() {
    return new Html.ImageGetter() {
      @Override public Drawable getDrawable(String source) {
        int fontH = (int) (HCUtils.getResources().getDimension(R.dimen.ip5_28px));
        int id = Integer.parseInt(source);
        Drawable d = null;
        try {
          d = HCUtils.getResDrawable(id);
        } catch (Exception e) {
          HCLog.d("ImageGetter", "ImageGetter getImageGetterInstance is crash ... ");
        }
        int width = (int) (70F / 28F * fontH);
        int height = (int) (fontH * 60F / 42F);
        if (d != null) {
          d.setBounds(0, 0, width, height);
        }
        return d;
      }
    };
  }
}

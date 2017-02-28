package com.haoche51.buyerapp.util;

public class HCConsts {

  public final static String ADVISORY_PHONE = "4006968390";

  public final static String ADVISORY_FORMAT_PHONE = "400-696-8390";

  public final static String FORUM_SHARE_URL =
      "http://bbs.haoche51.com/forum.php?mod=forumdisplay&fid=82&filter=typeid&typeid=11";

  /**
   * 登陆完以后需要到的目的Class
   */
  public static final String INTENT_KEY_LOGIN_DEST = "hcLoginAfterDest";

  public static final String INKENT_KEY_PUSH_TYPE = "hcPushType";

  public static final String INTEKTN_KEY_PUSH_DATA = "hcPushData";

  public static final String INTENT_KEY_LOGIN_HINT = "hcLoginHint";

  public static final String INTENT_LOGIN_FOR_COLL_OR_SUB = "hcLoginForCollOrSub";

  /**
   * 订阅条目限制
   */
  public static final int SUBSCRIBE_LIMIT = 5;

  /**
   * 短信验证码间隔(秒)
   */
  public static final int VERIFY_TIME = 60;

  public static final String HC_BLANK = " ";
  public static final String HC_ENTER = "\n";

  public static final int PAGESIZE = 10;

  public static final String UNLIMITED = "不限";

  public static final String INTENT_KEY_VEHICLEID = "vehicleid";

  public static final String INTENT_KEY_MINE = "myvehicleid";

  public static final String INTENT_KEY_OTHER = "othervehicleid";

  public static final String INTENT_KEY_LOGIN_TITLE = "keyForModifyLoginTitle";

  public static final String INTENT_KEY_IS_FOR_LOGIN = "keyIsForLogin";

  /**
   * intent key title
   */
  public static final String INTENT_KEY_TITLE = "title";
  /**
   * intent key url
   */
  public static final String INTENT_KEY_URL = "url";

  public static final String INTENT_KEY_BANNER = "hcbanner";

  public static final String INTENT_KEY_FORUM = "hcforum";

  public static final String INTENT_KEY_ORDERID = "hcorderid";

  /**
   * intent key 传递ScanHistoryEntity
   */
  public static final String INTENT_KEY_SCANENTITY = "ScanHistoryEntity";

  /**
   * 已售出 固定值
   */
  public static final int STATUS_SOLD = 5;
  /**
   * 车主售出
   */
  public static final int STATUS_OWNER_SOLD = 7;

  /**
   * TextView Drawable 的位置
   */
  public static final int DRAWABLE_LEFT = 0x101;
  public static final int DRAWABLE_TOP = 0x102;
  public static final int DRAWABLE_RIGHT = 0x103;
  public static final int DRAWABLE_BOTTOM = 0x104;

  /** 筛选排序 */
  public static final int FILTER_SORT = 0x301;
  /** 筛选品牌 */
  public static final int FILTER_BRAND = 0x302;
  /** 筛选车系 */
  public static final int FILTER_SERIES = 0x303;
  /** 筛选价格 */
  public static final int FILTER_PRICE = 0x304;
  /** 筛选车身结构 */
  public static final int FILTER_CAR_TYPE = 0x305;
  /** 筛选车龄 */
  public static final int FILTER_CAR_AGE = 0x306;
  /** 筛选里程 */
  public static final int FILTER_DISTANCE = 0x307;
  /** 筛选变速箱 */
  public static final int FILTER_SPEED_BOX = 0x308;
  /** 筛选排放标准 */
  public static final int FILTER_STANDARD = 0x309;
  /** 筛选排量 */
  public static final int FILTER_EMISSION = 0x310;
  /** 筛选排量 */
  public static final int FILTER_COUNTRY = 0x311;
  /** 筛选排量 */
  public static final int FILTER_COLOR = 0x312;

  /**
   * 清单文件中友盟渠道号key
   */
  public static final String UMENG_CHANNEL = "UMENG_CHANNEL";

  /**
   * 已预约约约约约约
   */
  public static final int ORDER_HAS_RESERVE = 1;

  /**
   * 已预定定定定定
   */
  public static final int ORDER_HAS_ORDAIN = 4;

  /** 搜索页面 */
  public static final int REQUESTCODE_FOR_SEARCH = 0x1021;
  public static final String KEY_FOR_SEARCH_KEY = "keyforSearchKey";
}

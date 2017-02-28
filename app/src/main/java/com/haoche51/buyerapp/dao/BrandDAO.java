package com.haoche51.buyerapp.dao;

import android.content.ContentValues;
import android.database.Cursor;
import com.haoche51.buyerapp.entity.BaseEntity;
import com.haoche51.buyerapp.entity.HCBrandEntity;
import java.util.ArrayList;
import java.util.List;

public class BrandDAO extends BaseDAO {
  private static BrandDAO dao = new BrandDAO();

  public static BrandDAO getInstance() {
    return dao;
  }

  public static final String TABLE_NAME = "brand";
  private static final String DEFAULT_ORDER_BY = "`brand_id` DESC";
  public static final String CREATE_TABLE =
      "create table " + TABLE_NAME + " (" + "brand_id             integer primary key ,"
          + "brand_name			text not null , " + "first_letter text not null ,"
          + "series_ids text not null " + ");";

  @Override protected String getTableName() {
    return TABLE_NAME;
  }

  private static final String[] COLUMNS =
      { "brand_id", "brand_name", "first_letter", "series_ids", };

  @Override protected ContentValues getContentValues(BaseEntity entity) {
    HCBrandEntity brand = (HCBrandEntity) entity;
    ContentValues mValues = new ContentValues();
    mValues.put(COLUMNS[0], brand.getBrand_id());
    mValues.put(COLUMNS[1], brand.getBrand_name());
    mValues.put(COLUMNS[2], brand.getFirst_char());
    String series_ids = brand.getSeries().toString();
    series_ids.replace("[", "(").replace("]", ")");
    mValues.put(COLUMNS[3], series_ids);
    return mValues;
  }

  @Override protected BaseEntity getEntityFromCursor(Cursor mCursor) {
    HCBrandEntity brand = new HCBrandEntity();
    brand.setBrand_id(mCursor.getInt(0));
    brand.setBrand_name(mCursor.getString(1));
    brand.setFirst_char(mCursor.getString(2));
    String list2Str = mCursor.getString(3);
    List<String> series_ids = string2List(list2Str);
    brand.setSeries(series_ids);
    return brand;
  }

  /**
   * 获取数据库中所有品牌
   */
  @SuppressWarnings("unchecked") public List<HCBrandEntity> getAllBrands() {
    return (List<HCBrandEntity>) BrandDAO.getInstance().get(" 1=1 ");
  }

  @Override public BaseEntity get(int id) {
    return get("brand_id=" + id, null, null, null);
  }

  @Override protected String[] getColumns() {
    return COLUMNS;
  }

  @Override protected String getDefaultOrderby() {
    return DEFAULT_ORDER_BY;
  }

  public List<String> string2List(String list2Str) {
    List<String> list = new ArrayList<>();
    if (list2Str != null && !"".equals(list2Str.trim())) {
      int len = list2Str.length();
      if (len > 2) {
        list2Str = list2Str.substring(1, len - 1);
        String[] ss = list2Str.split(",");
        for (String s : ss) {
          list.add(s.trim());
        }
      }
    }
    return list;
  }
}

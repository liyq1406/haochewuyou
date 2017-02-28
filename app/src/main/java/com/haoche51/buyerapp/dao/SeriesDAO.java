package com.haoche51.buyerapp.dao;

import android.content.ContentValues;
import android.database.Cursor;
import com.haoche51.buyerapp.entity.BaseEntity;
import com.haoche51.buyerapp.entity.SeriesEntity;
import java.util.List;

public class SeriesDAO extends BaseDAO {

  private static SeriesDAO dao = new SeriesDAO();

  public static SeriesDAO getInstance() {
    return dao;
  }

  public static final String TABLE_NAME = "series";
  private static final String DEFAULT_ORDER_BY = " id DESC";
  public static final String CREATE_TABLE =
      "create table " + TABLE_NAME + " (" + "id integer primary key ," + " name text not null,"
          + " short_name text not null," + " pinyin text not null ," + " brand_id integer not null,"
          + " brand_name text not null" + " );";

  @Override protected String getTableName() {
    return TABLE_NAME;
  }

  private static final String[] COLUMNS =
      { "id", "name", "short_name", "pinyin", "brand_id", "brand_name" };

  @Override protected ContentValues getContentValues(BaseEntity bentity) {
    SeriesEntity entity = (SeriesEntity) bentity;
    ContentValues mValues = new ContentValues();
    mValues.put(COLUMNS[0], entity.getId());
    mValues.put(COLUMNS[1], entity.getName());
    mValues.put(COLUMNS[2], entity.getShort_name());
    mValues.put(COLUMNS[3], entity.getPinyin());
    mValues.put(COLUMNS[4], entity.getBrand_id());
    mValues.put(COLUMNS[5], entity.getBrand_name());
    return mValues;
  }

  @Override protected BaseEntity getEntityFromCursor(Cursor mCursor) {
    SeriesEntity entity = new SeriesEntity();
    entity.setId(mCursor.getInt(0));
    entity.setName(mCursor.getString(1));
    entity.setShort_name(mCursor.getString(2));
    entity.setPinyin(mCursor.getString(3));
    entity.setBrand_id(mCursor.getInt(4));
    entity.setBrand_name(mCursor.getString(5));
    return entity;
  }

  @SuppressWarnings("unchecked") public List<SeriesEntity> findSeriesById(List<String> seriesIDs) {
    String where = seriesIDs.toString().replace("[", "(").replace("]", ")");
    where = " id in " + where;
    return (List<SeriesEntity>) SeriesDAO.getInstance().get(where);
  }

  public SeriesEntity findSerisById(int id) {
    if (getInstance().get(id) != null) {
      SeriesEntity se = (SeriesEntity) getInstance().get(id);
      return se;
    }
    return null;
  }

  @Override protected String[] getColumns() {
    return COLUMNS;
  }

  @Override protected String getDefaultOrderby() {
    return DEFAULT_ORDER_BY;
  }
}

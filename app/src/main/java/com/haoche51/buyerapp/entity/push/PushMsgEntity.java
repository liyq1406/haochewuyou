package com.haoche51.buyerapp.entity.push;

/**
 * Created by xuhaibo on 15/11/7
 */
public class PushMsgEntity {
  /**
   * type : 8
   * data : {"vehicle_source_id":5342}
   *
   * type: 3
   * data : {"redirect":"VehicleDetailActivity","param":"http://www.so.com"}
   */

  private int type;
  /**
   * vehicle_source_id : 5342
   */

  private PushMsgDataEntity data;

  public void setType(int type) {
    this.type = type;
  }

  public void setData(PushMsgDataEntity data) {
    this.data = data;
  }

  public int getType() {
    return type;
  }

  public PushMsgDataEntity getData() {
    return data;
  }
}

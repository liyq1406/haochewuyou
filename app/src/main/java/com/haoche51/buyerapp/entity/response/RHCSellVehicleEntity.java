package com.haoche51.buyerapp.entity.response;

import com.haoche51.buyerapp.entity.SaleServiceEntity;

/**
 * Created by xuhaibo on 15/11/12
 */
public class RHCSellVehicleEntity {
	/**
	 * errno : 0
	 * errmsg : ok
	 * data : {"cover_img":"http://image3.haoche51.com/o_1a3m2odv71ic1gvi15bb1joo1uuoi.png?imageView2/1/w/220/h/87","seller_num":4154,"sell_phone":"400-801-9151"}
	 */

	private int errno;
	private String errmsg;
	private SaleServiceEntity data;

	public void setErrno(int errno) {
		this.errno = errno;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public void setData(SaleServiceEntity data) {
		this.data = data;
	}

	public int getErrno() {
		return errno;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public SaleServiceEntity getData() {
		return data;
	}
}

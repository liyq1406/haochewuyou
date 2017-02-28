package com.haoche51.buyerapp.entity.response;

import com.haoche51.buyerapp.entity.SaleSubmitResEntity;

/**
 * Created by xuhaibo on 15/11/12
 */
public class RHCSaleSubmitEntity {


	/**
	 * errno : 0
	 * data : {"title":"出售信息成功！","description":"我们会尽快与您取得联系，收到上线短信后，可进行查看出售进度"}
	 * errmsg : ok
	 */

	private int errno;
	private SaleSubmitResEntity data;
	private String errmsg;

	public void setErrno(int errno) {
		this.errno = errno;
	}

	public void setData(SaleSubmitResEntity data) {
		this.data = data;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public int getErrno() {
		return errno;
	}

	public SaleSubmitResEntity getData() {
		return data;
	}

	public String getErrmsg() {
		return errmsg;
	}
}

package com.haoche51.buyerapp.entity.response;

/**
 * Created by xuhaibo on 15/11/12
 */
public class RHCCommonEntity {

	/**
	 * errno : 0
	 * errmsg : 操作成功
	 */

	private int errno;
	private String errmsg;

	public void setErrno(int errno) {
		this.errno = errno;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public int getErrno() {
		return errno;
	}

	public String getErrmsg() {
		return errmsg;
	}
}

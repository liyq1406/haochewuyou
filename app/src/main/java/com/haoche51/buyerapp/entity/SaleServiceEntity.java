package com.haoche51.buyerapp.entity;

/**
 * Created by xuhaibo on 15/11/12
 */
public class SaleServiceEntity {
	/**
	 * cover_img : http://image3.haoche51.com/o_1a3m2odv71ic1gvi15bb1joo1uuoi.png?imageView2/1/w/220/h/87
	 * seller_num : 4154
	 * sell_phone : 400-801-9151
	 */

	private String cover_img;
	private int seller_num;
	private String sell_phone;

	public void setCover_img(String cover_img) {
		this.cover_img = cover_img;
	}

	public void setSeller_num(int seller_num) {
		this.seller_num = seller_num;
	}

	public void setSell_phone(String sell_phone) {
		this.sell_phone = sell_phone;
	}

	public String getCover_img() {
		return cover_img;
	}

	public int getSeller_num() {
		return seller_num;
	}

	public String getSell_phone() {
		return sell_phone;
	}
}

package com.haoche51.buyerapp.entity;

/**
 * Created by xuhaibo on 15/11/12
 */
public class SaleSubmitResEntity {
	/**
	 * title : 出售信息成功！
	 * description : 我们会尽快与您取得联系，收到上线短信后，可进行查看出售进度
	 */

	private String title;
	private String description;

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}
}

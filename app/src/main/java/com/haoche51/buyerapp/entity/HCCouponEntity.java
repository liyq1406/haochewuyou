package com.haoche51.buyerapp.entity;

/**
 * 优惠券列表实体
 */
public class HCCouponEntity {

    /**
     * id : 6
     * coupon_id : 86f9bcc8245fe3cb59aa5bb45e476f29
     * phone : 18511912698
     * type : 1
     * code : hc12345
     * amount : 233
     * from_time : 1415680000
     * expire_time : 1499980800
     * create_time : 1442306295
     * status : 0
     * update_time : 2015-09-15 16:38:15
     * title : 减免服务费
     * url : http://m.haoche51.com/coupon/bank_card
     */

    private String id;
    private String coupon_id;
    private String phone;
    private String type;
    private String code;
    private String amount;
    private long from_time;
    private long expire_time;
    private long create_time;
    private int status;
    private String update_time;
    private String title;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public long getFrom_time() {
        return from_time;
    }

    public void setFrom_time(long from_time) {
        this.from_time = from_time;
    }

    public long getExpire_time() {
        return expire_time;
    }

    public void setExpire_time(long expire_time) {
        this.expire_time = expire_time;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HCCouponEntity)) return false;

        HCCouponEntity entity = (HCCouponEntity) o;

        return this.getCoupon_id().equals(entity.getCoupon_id());
    }
}

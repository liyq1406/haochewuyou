package com.haoche51.buyerapp.entity;

import java.util.List;

public class BHCBookOrderEntity {

    /**
     * errno : 0
     * errmsg :
     * data : [{"id":"835","vehicle_source_id":"11268","vehicle_name":"宝马 5系GT 2011款 535i 典雅型","register_time":null,"mile":null,"trans_status":"3","geerbox":null,"image":"http://192.168.1.106:9999/76ba434446dabd8e7af6578c8d1c88636d7bfd21.jpg","time":"1425609300","type":"过户","place":"bj","desc":"过户专员","name":"过户","phone":"13800138000","price":"10","status":4,"comment":"过户材料：机动车登记证书，机动车行驶证，原始购置发票或二手车过户票，身份证。看详细说明http://m.haoche51.com/zrbz","vehicle_online":0},{"id":"909","vehicle_source_id":"11269","vehicle_name":"阿尔法罗密欧 ALFA 156 2004款 2.0 AT","register_time":null,"mile":null,"trans_status":"3","geerbox":null,"image":"http://192.168.1.106:9999/53bda99c98ae4fb7faf19c2014888be13f961477.jpg","time":"0","type":"过户","place":"过户地点","desc":"过户专员","name":"过户","phone":"13800138000","price":"6000","status":4,"comment":"过户材料：机动车登记证书，机动车行驶证，原始购置发票或二手车过户票，身份证。看详细说明http://m.haoche51.com/zrbz","vehicle_online":0},{"id":"915","vehicle_source_id":"11094","vehicle_name":"奔腾 B70 2011款 2.0L 自动智领型","register_time":null,"mile":null,"trans_status":"5","geerbox":null,"image":"http://192.168.0.106:9999/f4f748023e5f37982b2b21d8a9d0212d45f6fea9.jpg","time":"1431079200","type":"过户","place":"","desc":"","name":"","phone":"","price":"10000","status":8,"comment":"以后如需出售这辆车，好车无忧会帮您卖个好价。如有任何需要，请联系400-801-9151","vehicle_online":0},{"id":"984","vehicle_source_id":"11467","vehicle_name":"阿斯顿·马丁 Rapide 2014款 6.0L S 百年纪念版","register_time":null,"mile":null,"trans_status":"6","geerbox":null,"image":null,"time":"1435816800","type":"看车","place":"东莞市东莞市市下","desc":"","name":"","phone":"","price":"","status":3,"comment":"这辆车擦肩而过了。不过没关系，多看看别的车，把握下一辆","vehicle_online":0},{"id":"986","vehicle_source_id":"11465","vehicle_name":"阿尔法罗密欧 ALFA 156 2004款 2.0 AT","register_time":null,"mile":null,"trans_status":"6","geerbox":null,"image":null,"time":"1435798800","type":"看车","place":"北京市朝阳区北京市朝阳区民政局婚姻登记处","desc":"","name":"","phone":"","price":"","status":3,"comment":"这辆车擦肩而过了。不过没关系，多看看别的车，把握下一辆","vehicle_online":0},{"id":"999","vehicle_source_id":"8050","vehicle_name":"宝马5系 2012款 520Li 典雅型","register_time":null,"mile":null,"trans_status":"6","geerbox":null,"image":"http://image1.haoche51.com/d6fc07356677d8ed68abe74bd88329299c1f4fd6.jpg","time":"1435858200","type":"看车","place":"北京市石景山区古城","desc":"","name":"","phone":"","price":"","status":3,"comment":"这辆车擦肩而过了。不过没关系，多看看别的车，把握下一辆","vehicle_online":0},{"id":"1003","vehicle_source_id":"11055","vehicle_name":"2013款 北京汽车E系列 三厢 1.3L 手动乐活版","register_time":null,"mile":null,"trans_status":"3","geerbox":null,"image":"http://192.168.0.106:9999/01b695283cb1151db92b0d5e0b78cdc0d9f56c00.jpg","time":"0","type":"过户","place":"西二旗","desc":"过户专员","name":"过户","phone":"13800138000","price":"10000","status":4,"comment":"过户材料：机动车登记证书，机动车行驶证，原始购置发票或二手车过户票，身份证。看详细说明http://m.haoche51.com/zrbz","vehicle_online":0},{"id":"1004","vehicle_source_id":"10855","vehicle_name":"2011款 smart Fortwo进口 52kw mhd 硬顶 标准版","register_time":null,"mile":null,"trans_status":"6","geerbox":null,"image":"http://image1.haoche51.com/707ecde5d420e3622dfddca7229c1255cc927d95.jpg","time":"1436256000","type":"看车","place":"深圳市龙岗区龙岗中心城入口","desc":"","name":"","phone":"","price":"","status":3,"comment":"这辆车擦肩而过了。不过没关系，多看看别的车，把握下一辆","vehicle_online":0},{"id":"1053","vehicle_source_id":"20017","vehicle_name":"北汽幻速 S2 2014款 1.5L 手动豪华型","register_time":null,"mile":null,"trans_status":"3","geerbox":null,"image":"http://image1.haoche51.com/43de83ef9083a945e89f36719c2c755b38d856b9.jpg","time":"1440734460","type":"过户","place":"ghhh","desc":"过户专员","name":"过户","phone":"13800138000","price":"36","status":4,"comment":"过户材料：机动车登记证书，机动车行驶证，原始购置发票或二手车过户票，身份证。看详细说明http://m.haoche51.com/zrbz","vehicle_online":0},{"id":"1054","vehicle_source_id":"20008","vehicle_name":"法拉利 California 2009款 4.3L 标准型","register_time":"1167580800","mile":"2","trans_status":"5","geerbox":"3","image":"http://192.168.1.106:9999/69d8137ee8d112c719e0e742e4db94f79fc04b51.jpg","time":"1440644400","type":"过户","place":"","desc":"","name":"","phone":"","price":"70000","status":7,"comment":"已使用优惠券。返现金额将于5个工作日内到账，请注意查询","vehicle_online":0}]
     */

    private int errno;
    private String errmsg;
    private List<HCBookOrderEntity> data;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public List<HCBookOrderEntity> getData() {
        return data;
    }

    public void setData(List<HCBookOrderEntity> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BHCBookOrderEntity)) return false;

        BHCBookOrderEntity that = (BHCBookOrderEntity) o;

        if (getErrno() != that.getErrno()) return false;
        if (getErrmsg() != null ? !getErrmsg().equals(that.getErrmsg()) : that.getErrmsg() != null)
            return false;
        return !(getData() != null ? !getData().equals(that.getData()) : that.getData() != null);

    }

    @Override
    public int hashCode() {
        int result = getErrno();
        result = 31 * result + (getErrmsg() != null ? getErrmsg().hashCode() : 0);
        result = 31 * result + (getData() != null ? getData().hashCode() : 0);
        return result;
    }
}

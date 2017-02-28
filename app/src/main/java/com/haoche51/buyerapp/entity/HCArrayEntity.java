package com.haoche51.buyerapp.entity;

import java.util.List;

public class HCArrayEntity {
  /**
   * errno : 0
   * data : ["奔奔","奔奔LOVE","奔奔MINI","奔奔i","奔腾","奔腾B50","奔腾B70","奔腾B90","奔腾X80","奔驰","奔驰A级","奔驰A级AMG","奔驰B级","奔驰CLA级","奔驰CLA级AMG","奔驰CLK级","奔驰CLS级","奔驰CLS级AMG","奔驰CL级","奔驰C级","奔驰C级AMG","奔驰C级(进口)","奔驰E级","奔驰E级AMG","奔驰E级(进口)","奔驰GLA级AMG","奔驰GLA级(进口)","奔驰GLK级","奔驰GLK级(进口)","奔驰GL级","奔驰GL级AMG","奔驰G级","奔驰G级AMG","奔驰M级","奔驰M级AMG","奔驰R级","奔驰SLK级","奔驰SLK级AMG","奔驰SLS级AMG","奔驰SL级","奔驰SL级AMG","奔驰S级","奔驰S级AMG"]
   */

  private int errno;
  private List<String> data;

  public void setErrno(int errno) {
    this.errno = errno;
  }

  public void setData(List<String> data) {
    this.data = data;
  }

  public int getErrno() {
    return errno;
  }

  public List<String> getData() {
    return data;
  }
}

package com.haoche51.buyerapp.entity;

import java.util.List;

public class HCHomeDialogDataEntity {
  /**
   * type : 1
   * title : 您真的了解二手车吗?
   * content : [{"question":"2015年买到事故车的用户占多少？","option":["A.5%","B.10%","C.30%"],"option_correct":2,"answer":"C.30%,平均每三个人就有一个买到事故车"},{"question":"好车无忧是如何保证没有事故车的?","option":["A.车主承诺没有事故","B.218项专业检测，检查4S店保养记录"],"option_correct":1,"answer":"B.218项专业检测，检查4S店保养记录，好车无忧拥有最专业的评估师团队，保证无事故、无水泡、无火烧"}]
   */
  private int type;
  private String title;
  /**
   * question : 2015年买到事故车的用户占多少？
   * option : ["A.5%","B.10%","C.30%"]
   * option_correct : 2
   * answer : C.30%,平均每三个人就有一个买到事故车
   */

  private List<HomeDialogDataEntity> content;

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<HomeDialogDataEntity> getContent() {
    return content;
  }

  public void setContent(List<HomeDialogDataEntity> content) {
    this.content = content;
  }
}

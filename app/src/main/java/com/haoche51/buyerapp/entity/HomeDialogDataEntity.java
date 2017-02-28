package com.haoche51.buyerapp.entity;

import java.util.List;

public class HomeDialogDataEntity {
  private String question;
  private int option_correct;
  private String answer;
  private List<String> option;

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public int getOption_correct() {
    return option_correct;
  }

  public void setOption_correct(int option_correct) {
    this.option_correct = option_correct;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public List<String> getOption() {
    return option;
  }

  public void setOption(List<String> option) {
    this.option = option;
  }
}

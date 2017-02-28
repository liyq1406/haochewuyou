package com.haoche51.buyerapp.dao;

import android.os.Handler;

public abstract class DataObserver {
  protected Handler handler = null;

  public DataObserver(Handler handler) {
    this.handler = handler;
  }

  public abstract void onChanged();
}
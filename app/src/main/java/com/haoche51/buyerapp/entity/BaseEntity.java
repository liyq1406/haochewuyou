package com.haoche51.buyerapp.entity;

import com.google.gson.Gson;

public abstract class BaseEntity {
    protected static Gson gson = null;

    static {
        gson = new Gson();
    }
}


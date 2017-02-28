package com.haoche51.buyerapp;

import android.content.Context;
import com.haoche51.buyerapp.helper.DatabaseHelper;
import com.haoche51.buyerapp.helper.UserDataHelper;

public class GlobalData {
    public static DatabaseHelper mDbHelper = null;
    public static UserDataHelper userDataHelper = null;
    public static Context mContext;

    public static void init(Context context) {
        mContext = context;
        mDbHelper = new DatabaseHelper(context);
        userDataHelper = new UserDataHelper(context);
    }
}

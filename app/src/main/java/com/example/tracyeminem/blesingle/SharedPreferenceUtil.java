package com.example.tracyeminem.blesingle;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Glen on 9/18/2017.
 */

public class SharedPreferenceUtil {
    private Context mContext;


    public SharedPreferenceUtil(Context mContext) {
        this.mContext = mContext;
    }

    public void setTime(String mModuleId) {
        SharedPreferences mSP = mContext.getSharedPreferences("E-Call", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString("Time", mModuleId);
        editor.apply();
    }

    public String getTime() {
        SharedPreferences mSP = mContext.getSharedPreferences("E-Call", Activity.MODE_PRIVATE);
        return mSP.getString("Time", "3");
    }

    public void setCreateTime(String mModuleId) {
        SharedPreferences mSP = mContext.getSharedPreferences("E-Call", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString("CreateTime", mModuleId);
        editor.apply();
    }

    public String getCreateTime() {
        SharedPreferences mSP = mContext.getSharedPreferences("E-Call", Activity.MODE_PRIVATE);
        return mSP.getString("CreateTime", "3");
    }

    public void setDestoryTime(String mModuleId) {
        SharedPreferences mSP = mContext.getSharedPreferences("E-Call", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSP.edit();
        editor.putString("DestoryTime", mModuleId);
        editor.apply();
    }

    public String getDestroyTime() {
        SharedPreferences mSP = mContext.getSharedPreferences("E-Call", Activity.MODE_PRIVATE);
        return mSP.getString("DestoryTime", "3");
    }

}

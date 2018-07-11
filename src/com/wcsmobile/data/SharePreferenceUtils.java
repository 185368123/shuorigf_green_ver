package com.wcsmobile.data;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharePreference的工具类, 保存和配置一些设置信息
 * 
 * @author clx
 * 
 */
public class SharePreferenceUtils {
	public static final String PREF_PUTTED_PWD = "password";

	
	private static final String SHARE_PREFS_NAME = "config";
	private static SharedPreferences mSharedPreferences;
	

	public static void putString(Context ctx, String key, String value) {
		if (mSharedPreferences == null) {
			mSharedPreferences = ctx.getSharedPreferences(SHARE_PREFS_NAME,
					Context.MODE_PRIVATE);
		}

		mSharedPreferences.edit().putString(key, value).commit();
	}

	public static String getString(Context ctx, String key, String defaultValue) {
		if (mSharedPreferences == null) {
			mSharedPreferences = ctx.getSharedPreferences(SHARE_PREFS_NAME,
					Context.MODE_PRIVATE);
		}

		return mSharedPreferences.getString(key, defaultValue);
	}
	
	
}

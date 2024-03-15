package com.artifex.mupdfdemo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil{

        private static SharedPreferences sharedPreferences;

        /**
         * 初始化 在application中
         *
         * @param application
         */
        public static void init(Application application) {

            sharedPreferences = application.getSharedPreferences(SPConsts.SP_NAME, Context.MODE_PRIVATE);
        }

        /**
         * 获取搜索文字颜色值
         * @return
         */
        public static int getSearchTextColor() {
            int color;
            color = sharedPreferences.getInt(SPConsts.SP_COLOR_SEARCH_TEXT, 0x80ff5722);
            return color;
        }

        /**
         * 插入
         *
         * @param key
         * @param value
         */
        public static void put(String key, Object value) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (value.getClass() == Boolean.class) {
                editor.putBoolean(key, (Boolean) value);
            }
            if (value.getClass() == String.class) {
                editor.putString(key, (String) value);
            }
            if (value.getClass() == Integer.class) {
                editor.putInt(key, ((Integer) value).intValue());
            }
            editor.commit();
        }

        /**
         * @param context
         * @param keys
         */
        public static void cleanStringValue(Context context, String... keys) {
            for (String key : keys) {
                SharedPreferences settings = context.getSharedPreferences(
                        SPConsts.SP_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                if (settings.contains(key)) {
                    editor.remove(key).commit();
                }
            }
        }

        /**
         * 清除
         */
        public static void clear() {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(SPConsts.SP_COLOR_SEARCH_TEXT);
            editor.commit();
        }

}


package com.mumu.jsrecyclerview8;

import android.app.Application;


/**
 * @author : zlf
 * date    : 2019/5/31
 * github  : https://github.com/mamumu
 * blog    : https://www.jianshu.com/u/281e9668a5a6
 * desc    : app，用于初始化一些方法
 */
public class App extends Application {
    public static App instance;
    private boolean isOpen = false;

    public static App getInstance() {
        return instance;
    }

    public static App getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.instance = (App) getApplicationContext();
        isOpen = false;
    }
}

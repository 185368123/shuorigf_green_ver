package com.wcsmobile;

import android.util.Log;

import com.wcsmobile.bean.NameSaveBean;

import org.litepal.crud.DataSupport;

import java.util.List;

public class DBHelp {
    private static DBHelp dbHelp;

    private DBHelp() {
    }

    public static DBHelp getInstance() {
        synchronized (DBHelp.class) {
            if (dbHelp == null) {
                dbHelp = new DBHelp();
            }
            return dbHelp;
        }
    }

    public String getDeviceName(String name) {
        List<NameSaveBean> newsList = DataSupport.where("name=?",name).find(NameSaveBean.class);
        if (newsList != null && newsList.size() > 0) {
            return newsList.get(0).getName_new();
        } else
            return name;
    }

    public boolean setDeviceName(String name, String name_new) {
        DataSupport.deleteAll(NameSaveBean.class, "name = ?", name);
        return new NameSaveBean(name, name_new).save();

    }
}

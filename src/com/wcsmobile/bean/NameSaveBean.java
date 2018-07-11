package com.wcsmobile.bean;

import org.litepal.crud.DataSupport;

public class NameSaveBean extends DataSupport{

    private String name;

    private String name_new;

    public NameSaveBean(String name, String name_new) {
        this.name = name;
        this.name_new = name_new;
    }

    public String getName() {
        return name;
    }

    public String getName_new() {
        return name_new;
    }
}

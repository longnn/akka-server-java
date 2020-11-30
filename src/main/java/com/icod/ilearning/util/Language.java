package com.icod.ilearning.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class Language {

    private static Language INSTANCE = null;

    final Config config;
    final String locale;

    private Language(){
        config = ConfigFactory.load();
        locale = config.getString("lang.default");
    }

    public static Language getInstance(){
        if(INSTANCE == null){
            INSTANCE = new Language();
        }
        return INSTANCE;
    }

    public String trans(String label){
        String key = "lang."+locale+"."+label;
        return config.getString(key);
    }
}

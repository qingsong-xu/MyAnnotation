package com.xmaroon.myannotation;

import android.app.Activity;

/**
 * @author xuqingsong
 * @date 2022/9/13
 * @desc
 **/
public class XButterknife {
    public static void bind(Activity activity) {
        String name = activity.getClass().getName() + "_ViewBinding";
        try {
            Class<?> aClass = Class.forName(name);
            IBinder iBinder = (IBinder) aClass.newInstance();
            iBinder.bind(activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}

package com.zxxkj.util;

public class SleepUtil {

	public static void sleep( long time ){
        try {
            Thread.sleep( time );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

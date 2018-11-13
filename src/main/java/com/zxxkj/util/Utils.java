package com.zxxkj.util;

import java.io.*;

public class Utils {
    public static <T> T deserialize(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;

        try {
            in = new ObjectInputStream(bis);

            return (T) in.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                bis.close();
            } catch (IOException ex) {
                // silently ignore
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // silently ignore
            }
        }
    }
    public static byte[] toBytes(String string) {
        try {
            return string == null ? null : string.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toStr(byte[] bytes) {
        return bytes == null ? null : new String(bytes);
    }

    public static boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0 || ("null").equals(value);
    }

    public static boolean deleteFile (String fileName) throws Exception{
    	boolean flag = false;
        File file = new File(fileName);
        
        if(file.isFile() && file.exists()){
            Boolean succeedDelete = file.delete();
            if(succeedDelete){
            	flag = true;
            }
        }
        return flag;
    }
}

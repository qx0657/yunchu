package fun.qianxiao.yunchu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.SPUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

import fun.qianxiao.yunchu.MyApplication;

/**
 * Create by QianXiao
 * On 2020/6/6
 */
public class MySpUtils {
    private static SPUtils spUtils;
    private final static String MODE = AppUtils.getAppPackageName()+"_preferences";

    static {
        spUtils = SPUtils.getInstance(MODE);
    }

    public static void remove(String key){
        spUtils.remove(key);
    }

    public static boolean contain(String key){
        return spUtils.contains(key);
    }

    public static void save(String key, int value) {
        spUtils.put(key, value);
    }

    public static void save(String key, String value) {
        if(!"login_pwd".equals(key)){
            spUtils.put(key,value);
        }else{
            spUtils.put(key, ConvertUtils.bytes2HexString(value.getBytes()));
        }
    }

    public static void save(String key, long value) {
        spUtils.put(key, value);
    }

    public static void save(String key, float value) {
        spUtils.put(key, value);
    }

    public static void save(String key, boolean value) {
        spUtils.put(key, value);
    }

    public static int getInt(String key) {
        return spUtils.getInt(key,0);
    }

    public static String getString(String key) {
        if(!"login_pwd".equals(key)){
            return spUtils.getString(key);
        }else{
            return ConvertUtils.bytes2String(ConvertUtils.hexString2Bytes(spUtils.getString(key)));
        }
    }

    public static long getLong(String key) {
        return spUtils.getLong(key);
    }

    public static float getFloat(String key) {
        return spUtils.getFloat(key);
    }

    public static boolean getBoolean(String key) {
        return spUtils.getBoolean(key);
    }

    public static Set<String> getStringSet(String key){
        return spUtils.getStringSet(key);
    }
    /**
     * ?????????????????????
     * ?????????????????????????????????????????? ????????????????????????
     * @param keyname
     * @param obj
     */
    public static void SaveObjectData(String keyname, Object obj) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(MODE, Context.MODE_PRIVATE);// ????????????
        SharedPreferences.Editor editor = sp.edit();// ??????????????????
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// ?????????????????????
        ObjectOutputStream out = null;// ???????????????????????????
        try {
            out = new ObjectOutputStream(baos);// ??????????????????????????????64???????????????sp???
            out.writeObject(obj);
            String objectValue = new String(Base64.encode(baos.toByteArray(),
                    Base64.DEFAULT));
            editor.putString(keyname, objectValue);
            editor.commit();// ??????????????????
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * ?????????????????????
     *
     * @param keyname
     * @param <T>
     * @return
     */
    public static <T> T getObjectData(String keyname) {
        SharedPreferences sp = MyApplication.getInstance().getSharedPreferences(MODE, Context.MODE_PRIVATE);// ?????????????????????
        if (sp.contains(keyname)) {
            String objectValue = sp.getString(keyname, null);
            byte[] buffer = Base64.decode(objectValue, Base64.DEFAULT);
            // ???????????????????????????????????????????????????????????????????????????????????????
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bais != null) {
                        bais.close();
                    }

                    if (ois != null) {
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}

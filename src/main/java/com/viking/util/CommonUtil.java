package com.viking.util;


import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 通用工具类
 * Created by Viking on 2020/8/1
 */
public class CommonUtil {
    public static final int BANK = 0;// 空
    public static final int TREE = 1;// 树
    public static final int SHEEP = 4;// 羊
    public static final int WOLF = 5;// 狼
    public static final int CAGE = 8;// 笼子
    public static final int SHEEP_IN_CAGE = 12;// 有羊的笼子

    public static ImageIcon getImageIcon(String path){
        try {
            InputStream inputStream = CommonUtil.class.getResourceAsStream(path);
            assert inputStream != null;
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1){
                byteOutputStream.write(buffer,0,length);
            }
            byteOutputStream.close();
            inputStream.close();
            return new ImageIcon(byteOutputStream.toByteArray());
        }catch (IOException e){
            e.printStackTrace();
        }
        throw new RuntimeException("文件读取失败");
    }
}

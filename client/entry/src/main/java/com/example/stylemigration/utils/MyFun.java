package com.example.stylemigration.utils;

import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.agp.window.dialog.ToastDialog;

public class MyFun {
    /**
     * 根据日期随机生成文件名
     * @param ext
     * @return
     */
    public static String createFileName(String ext) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return simpleDateFormat.format(new Date()) + (int) (Math.random() * 900 + 100)  + (ext == null ? "" : ext);
    }

}

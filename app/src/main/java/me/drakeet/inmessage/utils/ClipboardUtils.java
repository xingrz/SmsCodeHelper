package me.drakeet.inmessage.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Created by shengkun on 15/6/12.
 */
public class ClipboardUtils {

    public static void putTextIntoClipboard(Context context, String text) {
        // 得到剪贴板管理器
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("copy text", text);
        clipboardManager.setPrimaryClip(clipData);
    }
}

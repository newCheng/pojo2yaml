package com.nc.pojo2yaml.util;


import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class ClipboardUtil {

    public static void copyToClipboard(String content) {

        StringSelection selection = new StringSelection(content);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }
}

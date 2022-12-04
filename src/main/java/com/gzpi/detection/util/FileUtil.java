package com.gzpi.detection.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    public static boolean write(File file, String t) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(t);
            fileWriter.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}

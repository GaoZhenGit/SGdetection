package com.gzpi.detection.util;

import org.springframework.util.FileSystemUtils;

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

    public static void deleteDirectoryLegacyIO(File file) {

        File[] list = file.listFiles();  //无法做到list多层文件夹数据
        if (list != null) {
            for (File temp : list) {     //先去递归删除子文件夹及子文件
                deleteDirectoryLegacyIO(temp);   //注意这里是递归调用
            }
        }
        file.delete();
    }

    public static boolean isFileExist(String file) {
        File f = new File(file);
        return f.exists();
    }

    public static void copyRecursively(File src, File des) throws IOException {
        FileSystemUtils.copyRecursively(src, des);
    }
    public static void copyRecursively(String src, String des) throws IOException {
        File srcF = new File(src);
        File desF = new File(des);
        FileSystemUtils.copyRecursively(srcF, desF);
    }
}

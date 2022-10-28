package com.gzpi.detection.controller;

import com.gzpi.detection.bean.BaseResponse;
import com.gzpi.detection.bean.FileListResponse;
import com.gzpi.detection.operation.PathSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

@Controller
public class FileController {
    Logger logger = LoggerFactory.getLogger(FileController.class);
    @Autowired
    private PathSelector pathSelector;


    @RequestMapping(value = "uploadImg",method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse uploadImg(@RequestParam("file") MultipartFile file, @RequestParam("name") String name) {
        if (name == null) {
            name = file.getOriginalFilename();
        }
        logger.info("uploading file:" + name);
        boolean ret = upload(file, pathSelector.getRealDir(), name);
        if (ret) {
            return BaseResponse.success();
        } else {
            return BaseResponse.fail("upload fail");
        }
    }

    @RequestMapping(value = "imgs", method = RequestMethod.GET)
    @ResponseBody
    public FileListResponse listFiles() {
        FileListResponse response = new FileListResponse();
        File dir = new File(pathSelector.getRealDir());
        String[] files = dir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                File f = new File(dir + File.separator + name);
                boolean isTif = name.endsWith(".tif") || name.endsWith(".tiff");
                return !f.isDirectory() && isTif;
            }
        });
        if (files != null) {
            response.files = Arrays.asList(files);
        }
        response.msg = pathSelector.getRealDir();
        return response;
    }

    public static boolean upload(MultipartFile file, String path, String fileName) {

        // 生成新的文件名
        //String realPath = path + "/" + FileNameUtils.getFileName(fileName);
        //使用原文件名
        String realPath = path + fileName;

        File dest = new File(realPath);

        //判断文件父目录是否存在
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }

        if (dest.exists()) {
            dest.delete();
        }

        try {
            //保存文件
            file.transferTo(dest);
            return true;
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

package com.gzpi.detection.controller;

import com.gzpi.detection.bean.BaseResponse;
import com.gzpi.detection.bean.FileListResponse;
import com.gzpi.detection.operation.CommandExecutor;
import com.gzpi.detection.operation.PathSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

@Controller
@CrossOrigin
@RequestMapping("/file/")
public class FileController {
    Logger logger = LoggerFactory.getLogger(FileController.class);
    @Autowired
    private PathSelector pathSelector;
    @Value("${cog.python.path}")
    private String cogPythonPath;
    private final ResourceLoader resourceLoader = new DefaultResourceLoader();


    @RequestMapping(value = "uploadImg", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse uploadImg(@RequestParam("file") MultipartFile file, @RequestParam(value = "name",required = false) String name) {
        if (name == null) {
            name = file.getOriginalFilename();
        }
        logger.info("uploading file:" + name);
        boolean ret = upload(file, pathSelector.getTempDir(), name);
        if (!ret) {
            return BaseResponse.fail("upload fail");
        }
        try {
            String originImagePath = pathSelector.getTempDir() + name;
            String cogImagePath = pathSelector.getUploadImageDir() + name;
            String cmd = "python " + cogPythonPath.replace("$src", originImagePath).replace("$des", cogImagePath);
            CommandExecutor cogExecutor = new CommandExecutor(cmd, cogImagePath);
            cogExecutor.run();
            ret = cogExecutor.getResultCode() == 0;
        } catch (Exception e) {
            logger.error("file process fail", e);
        }
        if (ret) {
            return BaseResponse.success();
        } else {
            return BaseResponse.fail("cog transform fail");
        }
    }

    @RequestMapping(value = "uploadGeojson", method = RequestMethod.POST)
    @ResponseBody
    public BaseResponse uploadGeojson(@RequestParam("file") MultipartFile file, @RequestParam(value = "name",required = false) String name) {
        if (name == null) {
            name = file.getOriginalFilename();
        }
        if (!name.endsWith(".geojson")) {
            return BaseResponse.fail("not geojson format");
        }
        logger.info("uploading file:" + name);
        boolean ret = upload(file, pathSelector.getUploadImageDir(), name);
        if (ret) {
            return BaseResponse.success();
        } else {
            return BaseResponse.fail("geojson upload fail");
        }
    }

    @RequestMapping(value = "images", method = RequestMethod.GET)
    @ResponseBody
    public FileListResponse images(@RequestParam(required = false) String type) {
        FileListResponse response = new FileListResponse();
        File workspace = new File(pathSelector.getUploadImageDir());
        String prefix = "";
        if (type != null && !type.isEmpty()) {
            switch (type) {
                case "predict":
                    prefix = "predict_";
                    break;
                case "mission":
                    prefix = "mi_";
                    break;
                default:
                    prefix = "";
                    break;
            }
        }
        String finalPrefix = prefix;
        String[] files = workspace.list((dir, name) -> {
            File f = new File(dir + File.separator + name);
            boolean isTif = name.endsWith(".tif") || name.endsWith(".tiff");
            boolean isGeojson = name.endsWith(".geojson") || name.endsWith(".json");
            boolean pre = name.startsWith(finalPrefix);
            return !f.isDirectory() && (isTif || isGeojson) && pre;
        });
        if (files != null) {
            response.files = Arrays.asList(files);
            response.files.sort(String::compareTo);
        }
        response.msg = pathSelector.getUploadImageDir();
        return response;
    }

    public FileListResponse listFiles() {
        FileListResponse response = new FileListResponse();
        File workspace = new File(pathSelector.getUploadImageDir());
        String[] files = workspace.list((dir, name) -> {
            File f = new File(dir + File.separator + name);
            boolean isTif = name.endsWith(".tif") || name.endsWith(".tiff");
            boolean isGeojson = name.endsWith(".geojson");
            return !f.isDirectory() && (isTif || isGeojson);
        });
        if (files != null) {
            response.files = Arrays.asList(files);
        }
        response.msg = pathSelector.getUploadImageDir();
        return response;
    }

    @RequestMapping(value = "image", produces = MediaType.MULTIPART_FORM_DATA_VALUE, method = RequestMethod.GET)
    public ResponseEntity<?> image(String fileName) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment;filename=" + fileName);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resourceLoader.getResource("file:" + pathSelector.getRealDir() + fileName));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "result/image/{id}", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> resultImg(@PathVariable(name = "id") String taskId) {
        try {
            String name = "labels.tif";
            String path = pathSelector.getPredictTaskOutputPath(taskId) + "result" + File.separator + name;
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment;filename=" + name);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resourceLoader.getResource("file:" + path));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @RequestMapping(value = "result/vector/{id}", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> result(@PathVariable(name = "id") String taskId) {
        try {
            String name = "0-polygons.json";
            String path = pathSelector.getPredictTaskOutputPath(taskId) + "result" + File.separator + name;
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment;filename=" + name);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resourceLoader.getResource("file:" + path));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
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

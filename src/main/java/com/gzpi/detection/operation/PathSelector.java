package com.gzpi.detection.operation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
@Component
public class PathSelector {
    private static final String DOCKER_BASE_PATH = "/opt/src/code/input/run/";
    @Value("${upload.path.windows}")
    private String windowsPath;

    @Value("${upload.path.linux}")
    private String linuxPath;

    public String getDockerBasePath() {
        return DOCKER_BASE_PATH;
    }

    public String getDockerBasePath(String fileName) {
        return DOCKER_BASE_PATH + fileName;
    }

    public String getRealPath(String fileName) {
        return getRealDir() + fileName;
    }

    public String getRealDir() {
        if (File.separator.equals("/")) {
            return linuxPath;
        } else {
            return windowsPath;
        }
    }

    public String getUploadImageDir() {
        String dir = getRealDir() + "images" + File.separator;
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
        return dir;
    }

    public String getTempDir() {
        String dir = getRealDir() + "tmp" + File.separator;
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
        return dir;
    }

    public String getModelBundleDir() {
        String dir = getRealDir() + "model" + File.separator;
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
        return dir;
    }

    public String getPredictImagePath(String imageName) {
        return getUploadImageDir() + imageName;
    }

    public String getPredictTaskOutputPath(String taskId) {
        return getRealDir() + "resultset" + File.separator + taskId + File.separator;
    }

    public String getDatasetLabelDir() {
        String dir = getRealDir() + "labels" + File.separator;
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
        return dir;
    }

    public String getTrainingWorkspaceDir(String modelId) {
        String dir = getRealDir() + "workspace" + File.separator + modelId;
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
        return dir;
    }
}

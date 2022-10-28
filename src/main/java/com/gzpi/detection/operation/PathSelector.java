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
        return getRealDir() + File.separator + fileName;
    }

    public String getRealDir() {
        if (File.separator.equals("/")) {
            return linuxPath;
        } else {
            return windowsPath;
        }
    }
}
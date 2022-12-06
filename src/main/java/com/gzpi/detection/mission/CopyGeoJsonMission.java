package com.gzpi.detection.mission;

import com.gzpi.detection.bean.PredictRequest;
import com.gzpi.detection.operation.PathSelector;
import org.slf4j.Logger;
import org.springframework.util.FileSystemUtils;

import java.io.File;

public class CopyGeoJsonMission implements Runnable{
    private final Logger logger;
    private final PathSelector pathSelector;
    private final PredictRequest predictRequest;
    public CopyGeoJsonMission(PredictRequest predictRequest, PathSelector pathSelector, Logger logger) {
        this.predictRequest = predictRequest;
        this.pathSelector = pathSelector;
        this.logger = logger;
    }
    @Override
    public void run() {
        logger.info("checking vector result of task:" + predictRequest.id);
        String path = pathSelector.getPredictTaskOutputPath(predictRequest.id) + "result" + File.separator + "0-polygons.json";
        File src = new File(path);
        if (!src.exists()) {
            logger.error("task " + predictRequest.id + " result not exist!");
            return;
        }
        if (predictRequest.output == null || predictRequest.output.isEmpty()) {
            predictRequest.output = predictRequest.id + ".geojson";
        }
        String output = pathSelector.getUploadImageDir() + "predict_" + predictRequest.output.replace(".tif", ".geojson");
        File des = new File(output);
        try {
            FileSystemUtils.copyRecursively(src, des);
            logger.info("copy finish vector result of task:" + predictRequest.id);
        } catch (Exception exception) {
            logger.error("task " + predictRequest.id + " vector result copy fail", exception);
        }
    }
}

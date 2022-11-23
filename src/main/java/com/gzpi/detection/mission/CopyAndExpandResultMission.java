package com.gzpi.detection.mission;

import com.gzpi.detection.bean.PredictRequest;
import com.gzpi.detection.operation.CommandExecutor;
import com.gzpi.detection.operation.PathSelector;
import org.slf4j.Logger;

import java.io.File;

public class CopyAndExpandResultMission implements Runnable{
    private final Logger logger;
    private final PathSelector pathSelector;
    private final PredictRequest predictRequest;
    private final String expandBandCommand;
    public CopyAndExpandResultMission(PredictRequest predictRequest, PathSelector pathSelector, String cmd, Logger logger) {
        this.predictRequest = predictRequest;
        this.pathSelector = pathSelector;
        this.expandBandCommand = cmd;
        this.logger = logger;
    }
    @Override
    public void run() {
        logger.info("checking result of task:" + predictRequest.id);
        String path = pathSelector.getPredictTaskOutputPath(predictRequest.id) + "result" + File.separator + "labels.tif";
        File src = new File(path);
        if (!src.exists()) {
            logger.error("task " + predictRequest.id + " result not exist!");
            return;
        }
        if (predictRequest.output == null || predictRequest.output.isEmpty()) {
            predictRequest.output = predictRequest.id + ".tif";
        }
        File des = new File(pathSelector.getUploadImageDir() + predictRequest.output);
        String expandCmd = "python " + expandBandCommand;
        expandCmd = expandCmd.replace("$src", src.getAbsolutePath()).replace("$des", des.getAbsolutePath());
        CommandExecutor expandBandExecutor = new CommandExecutor(expandCmd, null);
        expandBandExecutor.run();
        if (expandBandExecutor.getResultCode() == 0) {
            logger.info("copy finish result of task:" + predictRequest.id);
        } else {
            logger.error("task " + predictRequest.id + " result copy fail");
        }
    }
}

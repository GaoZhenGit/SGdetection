package com.gzpi.detection.operation;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CommandExecutor implements Runnable {
    Logger logger = LoggerFactory.getLogger(CommandExecutor.class);
    private String cmd;
    private String outputDir;
    private boolean hasFinished = false;
    private int resultCode = 0;
    public List<String> usingFiles;
    private final List<Runnable> mPostMission = new ArrayList<>();

    public CommandExecutor(String cmd, String outputDir) {
        this.cmd = cmd;
        this.outputDir = outputDir;
    }

    public void addPostMission(Runnable runnable) {
        mPostMission.add(runnable);
    }

    @Override
    public void run() {
        logger.info("predict command:" + cmd);
        String[] command = cmd.split(" ");
        executeCommand(command);
        hasFinished = true;
    }

    public boolean hasFinished() {
        return hasFinished;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void executeCommand(String[] command) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec(command);
            // 标准输入流（必须写在 waitFor 之前）
            String inStr = consumeInputStream(process.getInputStream(), false);
            // 标准错误流（必须写在 waitFor 之前）
            String errStr = consumeInputStream(process.getErrorStream(), true); //若有错误信息则输出
            int proc = process.waitFor();
            if (proc == 0) {
                logger.info("==========execute success==========");
            } else {
                logger.error("==========execute fail==========");
            }
            resultCode = proc;
            for (Runnable r : mPostMission) {
                r.run();
            }
        } catch (IOException | InterruptedException e) {
            logger.error("", e);
        } finally {
            if (resultCode != 0) {
                File dir = new File(outputDir);
                deleteDirectoryLegacyIO(dir);
            }
            hasFinished = true;
        }
    }

    private String consumeInputStream(InputStream is, boolean isError) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String s;
        StringBuilder sb = new StringBuilder();
        while ((s = br.readLine()) != null) {
            if (isError) {
                logger.error(s);
            } else {
                logger.info(s);
            }
            sb.append(s);
        }
        return sb.toString();
    }

    private void deleteDirectoryLegacyIO(File file) {

        File[] list = file.listFiles();  //无法做到list多层文件夹数据
        if (list != null) {
            for (File temp : list) {     //先去递归删除子文件夹及子文件
                deleteDirectoryLegacyIO(temp);   //注意这里是递归调用
            }
        }
        file.delete();
    }
}

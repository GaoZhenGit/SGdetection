package com.gzpi.detection.bean;

import java.util.ArrayList;
import java.util.List;

public class PredictResponse extends BaseResponse{
    public List<PredictItem> list = new ArrayList<>();
    public static class PredictItem {
        public String id;
        public String status;
    }
}

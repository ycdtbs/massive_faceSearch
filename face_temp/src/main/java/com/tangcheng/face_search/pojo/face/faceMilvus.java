package com.tangcheng.face_search.pojo.face;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "定义了milvus中最基础的数据结构")
public class faceMilvus {
    @Schema(name = "表名称")
    public static final String COLLECTION_NAME = "face_home";
    @Schema(name = "特征值长度")
    public static final Integer FEATURE_DIM = 256;
    @Schema(name = "topN")
    public static final Integer SEARCH_K = 5;                       // TopK
    @Schema(name = "Params")
    public static final String SEARCH_PARAM = "{\"nprobe\":10}";    // Params
    @Data
    @Schema(name = "字段")
    public static class Field {
        public static final String USER_NAME = "user_name";
        public static final String USER_CODE = "user_code";
        public static final String FEATURE = "user_feature";
    }

}

package com.tangcheng.face_search.pojo.milvusPojo;

import lombok.Data;

@Data
public class MilvusInfo {
    /**
     * 集合名称(库名)
     */
    public static final String COLLECTION_NAME = "face_home";
    /**
     * 分片数量
     */
    public static final Integer SHARDS_NUM = 8;
    /**
     * 分区数量
     */
    public static final Integer PARTITION_NUM = 16;

    /**
     * 分区前缀
     */
    public static final String PARTITION_PREFIX = "shards_";
    /**
     * 特征值长度
     */
    public static final Integer FEATURE_DIM = 256;

    /**
     * 字段
     */
    public static class Field {
        /**
         * 档案id
         */
        public static final String ARCHIVE_ID = "archive_id";
        /**
         * 小区id
         */
        public static final String ORG_ID = "org_id";
        /**
         * 档案特征值
         */
        public static final String ARCHIVE_FEATURE = "archive_feature";
    }

    /**
     * 通过组织id计算分区名称
     * @param orgId
     * @return
     */
    public static String getPartitionName(Integer orgId) {
        return PARTITION_PREFIX + (orgId % PARTITION_NUM);
    }

}

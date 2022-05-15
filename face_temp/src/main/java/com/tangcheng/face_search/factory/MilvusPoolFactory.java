package com.tangcheng.face_search.factory;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.beans.factory.annotation.Value;

public class MilvusPoolFactory extends BasePooledObjectFactory<MilvusServiceClient> {
    @Value("${milvus.host}")
    private String host; //milvus所在服务器地址
    @Value("${milvus.port}")
    private Integer port; //milvus端口
    @Override
    public MilvusServiceClient create() throws Exception {
        ConnectParam connectParam = ConnectParam.newBuilder()
                .withHost("180.76.227.9")
                .withPort(19530)
                .build();
        return new MilvusServiceClient(connectParam);
    }

    @Override
    public PooledObject<MilvusServiceClient> wrap(MilvusServiceClient milvusServiceClient) {
        return new DefaultPooledObject<>(milvusServiceClient);

    }
}

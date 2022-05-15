package com.tangcheng.face_search.common.milvus;

import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceInfo;
import com.tangcheng.face_search.factory.MilvusPoolFactory;
import io.milvus.Response.SearchResultsWrapper;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.DataType;
import io.milvus.grpc.IDs;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.SearchResults;
import io.milvus.param.MetricType;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import io.milvus.param.collection.*;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.stereotype.Component;
import com.tangcheng.face_search.pojo.face.faceMilvus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// milvys 工具类
@Data
@Component
@Log4j2
public class milvusOperateUtils {
    private GenericObjectPool<MilvusServiceClient> milvusServiceClientGenericObjectPool;  // 管理链接对象的池子
    // https://milvus.io/cn/docs/v2.0.x/load_collection.md
    private final int MAX_POOL_SIZE = 5;

    private milvusOperateUtils() {
        // 私有构造方法创建一个池
        // 对象池工厂
        MilvusPoolFactory milvusPoolFactory = new MilvusPoolFactory();
        // 对象池配置
        GenericObjectPoolConfig<FaceEngine> objectPoolConfig = new GenericObjectPoolConfig<>();
        objectPoolConfig.setMaxTotal(8);
        AbandonedConfig abandonedConfig = new AbandonedConfig();

        abandonedConfig.setRemoveAbandonedOnMaintenance(true); //在Maintenance的时候检查是否有泄漏

        abandonedConfig.setRemoveAbandonedOnBorrow(true); //borrow 的时候检查泄漏

        abandonedConfig.setRemoveAbandonedTimeout(MAX_POOL_SIZE); //如果一个对象borrow之后10秒还没有返还给pool，认为是泄漏的对象

        // 对象池
        milvusServiceClientGenericObjectPool = new GenericObjectPool(milvusPoolFactory, objectPoolConfig);
        milvusServiceClientGenericObjectPool.setAbandonedConfig(abandonedConfig);
        milvusServiceClientGenericObjectPool.setTimeBetweenEvictionRunsMillis(5000); //5秒运行一次维护任务
        log.info("milvus 对象池创建成功 维护了" + MAX_POOL_SIZE + "个对象");
    }

    // 创建一个Collection 类似于创建关系型数据库中的一张表
    private void createCollection(String collection) {
        MilvusServiceClient milvusServiceClient = null;
        try {
            // 通过对象池管理对象
            milvusServiceClient = milvusServiceClientGenericObjectPool.borrowObject();
            FieldType fieldType1 = FieldType.newBuilder()
                    .withName(faceMilvus.Field.USER_NAME)
                    .withDescription("用户名")
                    .withDataType(DataType.Int64)
                    .build();
            FieldType fieldType2 = FieldType.newBuilder()
                    .withName(faceMilvus.Field.USER_CODE)
                    .withDescription("编号")
                    .withDataType(DataType.Int64)
                    .withPrimaryKey(true)
                    .withAutoID(false)
                    .build();
            FieldType fieldType3 = FieldType.newBuilder()
                    .withName(faceMilvus.Field.FEATURE)
                    .withDescription("特征向量")
                    .withDataType(DataType.FloatVector)
                    .withDimension(faceMilvus.FEATURE_DIM)
                    .build();
            CreateCollectionParam createCollectionReq = CreateCollectionParam.newBuilder()
                    .withCollectionName(collection)
                    .withDescription("人脸特征向量库")
                    .withShardsNum(2)
                    .addFieldType(fieldType2)
                    .addFieldType(fieldType1)
                    .addFieldType(fieldType3)
                    .build();
            R<RpcStatus> result = milvusServiceClient.createCollection(createCollectionReq);
            log.info("创建结果" + result.getStatus() + "0 为成功");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 回收对象到对象池
            if (milvusServiceClient != null) {
                milvusServiceClientGenericObjectPool.returnObject(milvusServiceClient);
            }
        }


    }
    public void loadingLocation(String collection) {
        MilvusServiceClient milvusServiceClient = null;
        try {
            // 通过对象池管理对象
            milvusServiceClient = milvusServiceClientGenericObjectPool.borrowObject();
            R<RpcStatus> rpcStatusR = milvusServiceClient.loadCollection(
                    LoadCollectionParam.newBuilder()
                            .withCollectionName(collection)
                            .build());
            log.info("加载结果" + rpcStatusR);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 回收对象到对象池
            if (milvusServiceClient != null) {
                milvusServiceClientGenericObjectPool.returnObject(milvusServiceClient);
            }
        }


    }
    public void freedLoaction(String collection) {
        MilvusServiceClient milvusServiceClient = null;
        try {
            // 通过对象池管理对象
            milvusServiceClient = milvusServiceClientGenericObjectPool.borrowObject();
            R<RpcStatus> rpcStatusR = milvusServiceClient.releaseCollection(
                    ReleaseCollectionParam.newBuilder()
                            .withCollectionName(collection)
                            .build());
            log.info("加载结果" + rpcStatusR);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 回收对象到对象池
            if (milvusServiceClient != null) {
                milvusServiceClientGenericObjectPool.returnObject(milvusServiceClient);
            }
        }


    }

    // 删除一个Collection
    private void delCollection(String collection) {
        MilvusServiceClient milvusServiceClient = null;
        try {
            // 通过对象池管理对象
            milvusServiceClient = milvusServiceClientGenericObjectPool.borrowObject();
            R<RpcStatus> book = milvusServiceClient.dropCollection(
                    DropCollectionParam.newBuilder()
                            .withCollectionName(collection)
                            .build());
            log.info("删除" + book.getStatus() + " 0 为成功");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 回收对象到对象池
            if (milvusServiceClient != null) {
                milvusServiceClientGenericObjectPool.returnObject(milvusServiceClient);
            }
        }


    }

    // 插入数据 和对应的字段相同
    public long insert(String collectionName, String partitionName, List<Long> userName, List<Long> userCode, List<List<Float>> feature) {
        MilvusServiceClient milvusServiceClient = null;
        try {
            // 通过对象池管理对象
            milvusServiceClient = milvusServiceClientGenericObjectPool.borrowObject();
            List<InsertParam.Field> fields = new ArrayList<>();
            fields.add(new InsertParam.Field(faceMilvus.Field.USER_NAME, DataType.Int64, userName));
            fields.add(new InsertParam.Field(faceMilvus.Field.USER_CODE, DataType.Int64, userCode));
            fields.add(new InsertParam.Field(faceMilvus.Field.FEATURE, DataType.FloatVector, feature));
            InsertParam insertParam = InsertParam.newBuilder()
                    .withCollectionName(collectionName)
                    .withPartitionName(partitionName)
                    .withFields(fields)
                    .build();
            R<MutationResult> insertResult = milvusServiceClient.insert(insertParam);
            if (insertResult.getStatus() == 0) {
                return insertResult.getData().getIDs().getIntId().getData(0);
            } else {
                log.error("特征值上传失败 加入失败队列稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;

        } finally {
            // 回收对象到对象池
            if (milvusServiceClient != null) {
                milvusServiceClientGenericObjectPool.returnObject(milvusServiceClient);
            }
        }
        return 0;
    }

    // 根据向量搜索数据
    public List<?> searchByFeature(String collection,List<List<Float>> search_vectors) {
        MilvusServiceClient milvusServiceClient = null;
        try {
            // 通过对象池管理对象
            milvusServiceClient = milvusServiceClientGenericObjectPool.borrowObject();
            List<String> search_output_fields = Arrays.asList(faceMilvus.Field.USER_CODE);
            SearchParam searchParam = SearchParam.newBuilder()
                    .withCollectionName(collection)
                    .withPartitionNames(Arrays.asList("one"))
                    .withMetricType(MetricType.L2)
                    .withOutFields(search_output_fields)
                    .withTopK(faceMilvus.SEARCH_K)
                    .withVectors(search_vectors)
                    .withVectorFieldName(faceMilvus.Field.FEATURE)
                    .withParams(faceMilvus.SEARCH_PARAM)
                    .build();
            R<SearchResults> respSearch = milvusServiceClient.search(searchParam);
            if (respSearch.getStatus() == 0){
                SearchResultsWrapper wrapperSearch = new SearchResultsWrapper(respSearch.getData().getResults());
                List<?> fieldData = wrapperSearch.getFieldData(faceMilvus.Field.USER_CODE, 0);
                return fieldData;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();

        } finally {
            // 回收对象到对象池
            if (milvusServiceClient != null) {
                milvusServiceClientGenericObjectPool.returnObject(milvusServiceClient);
            }
        }
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        milvusOperateUtils milvusOperateUtils = new milvusOperateUtils();
        milvusOperateUtils.createCollection("face_home");
        //milvusOperateUtils.delCollection("");
    }
}

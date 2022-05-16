### SpringBoot 基于向量搜索引擎及虹软人脸识别SDK的大规模人脸搜索



[TOC]



#### 在线环境demo

>为了方便大家测试效果，开放了一个在线环境供大家测试并降低了识别门槛和难度，使得照片也可以通过筛选，大家使用前无比观看视频，按照视频方式操作。由于服务器昂贵，资源有限，生产环境的配置为2C 8G，所以服务比较慢用户体验一般，若想测试性能，请在本地部署

视频地址:https://www.bilibili.com/video/BV1YY4y147jz/

在线环境(演示):http://120.48.10.164:9528/ admin 123456

联系我：ycdtbs@163.com 

Github: https://github.com/ycdtbs/massive_faceSearch/tree/main

##### 在线环境说明

在线环境会收集大家数据，请勿上传敏感照片，项目测试数据集均来源于网络公开照片，利用Python脚本爬取，脚本存放于目录中

**在线环境仅用于演示 请勿上传自己数据** 

**在线环境仅用于演示 请勿上传自己数据** 

**在线环境仅用于演示 请勿上传自己数据** 

**在线环境仅用于演示 请勿上传自己数据** 

#### 

#### 前言

​		大四毕业时做毕业设计，用到了百度云人脸识别的API,当时制作了一个demo发到Bilibli上，之后不少同学来问我，于是制作了一个利用虹软SDK的人脸识别的包含人脸库管理的一套服务，一年半来有不少朋友前来咨询人脸识别相关的问题，由于博主本人工作业务不涉及这部分，所以一直无心研究。最近北京疫情在家有了一些时间，利用了几天时间完善了基于虹软的代码。

​		首先说明一下上个版本的缺陷是什么，首先之前的人脸数据缓存在了Redis当中，当我们解析出特征值时，将数据缓存到redis中，进行逐个必对和判断，**优化的方式也只是单纯的利用多线程和虹软的人基本特征（性别、年龄）**等进行分库，几百个人脸时还好，在上千个人脸时就会出现非常明显的延迟，用户体验效率非常低，因此基于上个版本只满足部分同学的毕业设计、小组作业的场景。偶然在工作中了解到了向量搜索引擎，于是考虑是否可以结合虹软的人脸识别SDK提取特征向量，然后进行分析处理。由于这个demo主要是搭建一个大规模人脸搜索和识别服务的demo，因此没有工程化，系统设计的也比较冗余，没有详细的功能设计，基本是博主想到什么做什么。最后跪求一个 **STAR** 重要的事情说三遍 **STAR STAR STAR**

#### 系统架构

![](https://chengpicture.oss-cn-beijing.aliyuncs.com/%E6%9E%B6%E6%9E%84%E5%9B%BE.png)

#### 功能设计

​		系统功能模块较为简单，主要功能就是**新增人脸**和**人脸搜索**两个功能，其中新增人脸使用页面上传和压缩包批量上传两个方式，压缩包上传时文件名称为用户名，下面主要说明人脸搜索的功能流程

##### Milvues

​		在介绍前需要说明一下Mulvus

​		Milvus 向量数据库能够帮助用户轻松应对海量非结构化数据（图片 / 视频 / 语音 / 文本）检索。单节点 Milvus 可以在秒内完成十亿级的向量搜索

​		因此虹软的SDK只能提取向量及对比的功能，在大规模人脸识别中，需要搜索引擎对于人脸数据进行初步筛选到一个较小的范围后在利用虹软的SDK进行测试，值得一提的是，博主多次测试后Milvues返回的匹配率足以满足人脸匹配的要求，Milvus的安装部署和使用文档参考 https://milvus.io/cn/docs/v2.0.x

​		**特别说明的是**虹软提取的数组是一个经过归一后的1032长度的byte数组，我们需要对数组进行转换，去除前8位的版本号，并将1024长度的byte转为256长度的float向量，这部分可以利用Arrays提供的方法进行转换，代码中也有相应的工具类

##### 人脸上传（单张）

![](https://chengpicture.oss-cn-beijing.aliyuncs.com/%E6%9C%AA%E5%91%BD%E5%90%8D%E6%96%87%E4%BB%B6.png)

##### 人脸上传（批量）

​		批量上传采用本地打包压缩上传到服务器，后台进程进行解压，放到队列中处理，处理结果存储在ES数据库中，实时结果及处理进度通过Websocket发送至前台

​		![](https://chengpicture.oss-cn-beijing.aliyuncs.com/%E6%9C%AA%E5%91%BD%E5%90%8D%E6%96%87%E4%BB%B6%20%281%29.png)

##### 人脸搜索

![](https://chengpicture.oss-cn-beijing.aliyuncs.com/%E6%9C%AA%E5%91%BD%E5%90%8D%E6%96%87%E4%BB%B6%20%282%29.png)

#### 技术架构

##### 前端框架

​		前端使用了Vue admin temlate 及 Element UI

##### 后端框架

​		后端框架主要是SpringBoot

##### 数据库

- mysql：存储用户信息，所有的数据以Mysql数据为准
- Elasticsearch：由于批量上传操作是异步的，用ES来收集日志并分析热点数据、成功数据、失败数据（当前版本未实现）
- InfluxDB：用于涉及到数据源较多，事务处理过于麻烦，架构设计中以Mysql中的数据为准，以Mysql数据进行数据同步
- 阿里云OSS：负责存储裁切后的人脸照片，负责前台展示及缓存失效时重新加载
- Milvues：**项目的核心数据库向量搜索引擎**

##### 中间件

- ActiveMq：由于大规模人脸搜索服务需要大量的照片,一个个手动上传不现实，因此开发了批量上传的功能，需要ActiveMq进行异步上传

##### 前后端交互

- restful：前后端交互主要使用restful接口
- websocket：负责将后端处理照片的过程及照片实时显示在前端



#### 安装部署

##### 前端

- .env.development 文件配置后端交互地址，**只需要修改所有的IP+端口** 其他路径不要改变

  ```properties
  # just a flag
  ENV = 'development'
  # base api
  VUE_APP_BASE_API = 'http://127.0.0.1:8080/' 
  #VUE_APP_BASE_API = 'http://120.48.10.164:8080/'
  
  # uploadFile
  VUE_APP_BASE_API_UPFILE = 'http://127.0.0.1:8080/file/getImageUrl'
  
  VUE_APP_BASE_API_UPFILE_LIST = 'http://127.0.0.1:8080/file/getListImageUrl'
  
  VUE_APP_BASE_API_WEBSOCKET = 'ws://127.0.0.1:8080/api/pushMessage/'
  
  VUE_APP_BASE_API_UPFILE = 'http://120.48.10.164:8080/file/getImageUrl'
  
  #VUE_APP_BASE_API_UPFILE_LIST = 'http://120.48.10.164:8080/file/getListImageUrl'
  
  #VUE_APP_BASE_API_WEBSOCKET = 'ws://120.48.10.164:8080/api/pushMessage/'
  ```

  VUE_APP_BASE_API：后端服务接口

  VUE_APP_BASE_API_UPFILE：单个文件上传地址

  VUE_APP_BASE_API_UPFILE_LIST：文件列表上传地址

  VUE_APP_BASE_API_WEBSOCKET：Websocket地址

- 运行

  ```shell
  npm install
  npm run dev
  服务端口：ip:9528
  ```

  

##### 后端配置

- application.yml 主要是服务地址
  - 修改Redis配置
  - 修改Mysql配置
  - 修改ActiveMq配置
  - 修改Milvues配置
  - 修改阿里云对象存储地址
  - uploadFile配置本地缓存路径，主要是压缩包上传时需要用到
- FaceEngineConfig 类
  - 配置虹软SDK的APID、SK，引擎地址

##### 服务

- mysql
- redis
- activeMq
- Elasticsearch（此版本不用安装）
- InfluxDB（此版本不用安装）
- Milvus

##### 数据库

- 执行face.sql

##### 人脸数据

- 利用python脚本自行爬取

#### 核心方法

##### FaceEngineConfig 类

> 类的主要功能是配置faceEngine的认证配置信息

```java
public  class FaceEngineConfig {
    public static final String APPID = "";
    public static final String SDKKEY = "";
    //public static final String SDKKEY = "";//linux
    public static final String LIB = "D:\\face_web\\ArcSoft_ArcFace_Java_Windows_x64_V3.0\\libs\\WIN64";
    //public static final String LIB = ""; // linux
    
}
```

##### FaceEnginePoolFactory 引擎对象工厂类

> 引擎对象工厂类，负责维护一个对象池

```java
@Log4j2
@Component
public class FaceEnginePoolFactory extends BasePooledObjectFactory<FaceEngine> {
    /**
     * 在对象池中创建对象
     * @return
     * @throws Exception
     */
    @Override
    public FaceEngine create() throws Exception {
        FaceEngine faceEngine = new FaceEngine(FaceEngineConfig.LIB);
        //激活引擎
        int errorCode = faceEngine.activeOnline(FaceEngineConfig.APPID, FaceEngineConfig.SDKKEY);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("引擎激活失败");
        }
        ActiveFileInfo activeFileInfo=new ActiveFileInfo();
        errorCode = faceEngine.getActiveFileInfo(activeFileInfo);
        if (errorCode != ErrorInfo.MOK.getValue() && errorCode != ErrorInfo.MERR_ASF_ALREADY_ACTIVATED.getValue()) {
            System.out.println("获取激活文件信息失败");
        }
        //引擎配置
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_IMAGE);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_ALL_OUT);
        engineConfiguration.setDetectFaceMaxNum(10);
        engineConfiguration.setDetectFaceScaleVal(16);
        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);
        //初始化引擎
        errorCode = faceEngine.init(engineConfiguration);

        if (errorCode != ErrorInfo.MOK.getValue()) {
            log.error("初始化引擎失败");
        }
        return faceEngine;

    }

    /**
     * 包装对象
     * @param faceEngine
     * @return
     */
    @Override
    public PooledObject<FaceEngine> wrap(FaceEngine faceEngine) {
        return new DefaultPooledObject<>(faceEngine);
    }
    /**
     * 销毁对象
     * @param faceEngine 对象池
     * @throws Exception 异常
     */
    @Override
    public void destroyObject(PooledObject<FaceEngine> faceEngine) throws Exception {
        super.destroyObject(faceEngine);
    }

    /**
     * 校验对象是否可用
     * @param faceEngine 对象池
     * @return 对象是否可用结果，boolean
     */
    @Override
    public boolean validateObject(PooledObject<FaceEngine> faceEngine) {
        return super.validateObject(faceEngine);
    }

    /**
     * 激活钝化的对象系列操作
     * @param faceEngine 对象池
     * @throws Exception 异常信息
     */
    @Override
    public void activateObject(PooledObject<FaceEngine> faceEngine) throws Exception {
        super.activateObject(faceEngine);
    }

    /**
     * 钝化未使用的对象
     * @param faceEngine 对象池
     * @throws Exception 异常信息
     */
    @Override
    public void passivateObject(PooledObject<FaceEngine> faceEngine) throws Exception {
        super.passivateObject(faceEngine);
    }

}

```



##### faceUtils 人脸识别工具类

>核心的人脸识别类，负责提取特征值、截取人脸、特征值对比

```java
public class faceUtils {
    private GenericObjectPool<FaceEngine> faceEngineGenericObjectPool;
    faceUtils(){
        // 对象池工厂
        FaceEnginePoolFactory personPoolFactory = new FaceEnginePoolFactory();
        // 对象池配置
        GenericObjectPoolConfig<FaceEngine> objectPoolConfig = new GenericObjectPoolConfig<>();
        objectPoolConfig.setMaxTotal(5);
        AbandonedConfig abandonedConfig = new AbandonedConfig();

        abandonedConfig.setRemoveAbandonedOnMaintenance(true); //在Maintenance的时候检查是否有泄漏

        abandonedConfig.setRemoveAbandonedOnBorrow(true); //borrow 的时候检查泄漏

        abandonedConfig.setRemoveAbandonedTimeout(10); //如果一个对象borrow之后10秒还没有返还给pool，认为是泄漏的对象

        // 对象池
        faceEngineGenericObjectPool = new GenericObjectPool<>(personPoolFactory, objectPoolConfig);
        faceEngineGenericObjectPool.setAbandonedConfig(abandonedConfig);
        faceEngineGenericObjectPool.setTimeBetweenEvictionRunsMillis(5000); //5秒运行一次维护任务
        log.info("引擎池开启成功");
    }
    /**
     * 人脸检测
     *
     * @param fileInputStream
     * @return
     */
    public  List<FaceInfo> faceFind(InputStream fileInputStream) throws IOException {
        FaceEngine faceEngine = null;
        try {
            faceEngine = faceEngineGenericObjectPool.borrowObject();
            ImageInfo imageInfo = getRGBData(fileInputStream);
            List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
            int errorCode = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
            return faceInfoList;
        } catch (Exception e) {
            log.error("出现了异常");
            e.printStackTrace();
            return new ArrayList<FaceInfo>();
        } finally {
            fileInputStream.close();
            // 回收对象到对象池
            if (faceEngine != null) {
                faceEngineGenericObjectPool.returnObject(faceEngine);
            }
        }

    }

    /**
     * 人脸截取
     *
     * @param fileInputStream
     * @param rect
     * @return
     */
    public  String faceCrop(InputStream fileInputStream, Rect rect) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BufferedImage bufImage = ImageIO.read(fileInputStream);
            int height = bufImage.getHeight();
            int width = bufImage.getWidth();
            int top = rect.getTop();
            int bottom = rect.getBottom();
            int left = rect.getLeft();
            int right = rect.getRight();
            //System.out.println(top + "-" + bottom + "-" + left + "-" + right);
            try {
                BufferedImage subimage = bufImage.getSubimage(left, top, right - left, bottom - left);
                ImageIO.write(subimage, "png", stream);
                String base64 = Base64.encode(stream.toByteArray());
                return base64;
            }catch (Exception e){
                return null;
            }finally {
                stream.close();
                fileInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

        }
        return null;
    }

    /**
     * 人脸特征值提取
     */
    public byte[] faceFeature(InputStream fileInputStream,FaceInfo faceInfo) throws IOException {
        FaceEngine faceEngine = null;
        FaceFeature faceFeature = new FaceFeature();
        try {
            faceEngine = faceEngineGenericObjectPool.borrowObject();
            ImageInfo imageInfo = getRGBData(fileInputStream);
            int errorCode = faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfo, faceFeature);
            byte[] featureData = faceFeature.getFeatureData();
            return featureData;

        } catch (Exception e) {
            log.error("出现了异常");
            e.printStackTrace();
            return new byte[0];
        } finally {
            fileInputStream.close();
            // 回收对象到对象池
            if (faceEngine != null) {
                faceEngineGenericObjectPool.returnObject(faceEngine);
            }
        }
    }

    /**
     * 人脸对比
     */
    public float faceCompared(byte [] source,byte [] des) throws IOException {
        FaceEngine faceEngine = null;
        try {
            faceEngine = faceEngineGenericObjectPool.borrowObject();
            FaceFeature targetFaceFeature = new FaceFeature();
            targetFaceFeature.setFeatureData(source);
            FaceFeature sourceFaceFeature = new FaceFeature();
            sourceFaceFeature.setFeatureData(des);
            FaceSimilar faceSimilar = new FaceSimilar();
            faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
            float score = faceSimilar.getScore();
            return score;
        } catch (Exception e) {
            log.error("出现了异常");
            e.printStackTrace();
            return 0;
        } finally {
            // 回收对象到对象池
            if (faceEngine != null) {
                faceEngineGenericObjectPool.returnObject(faceEngine);
            }
        }
    }
```



##### milvusOperateUtils Milvues工具类

```java
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
```





#### 相关文档

##### 虹软

https://ai.arcsoft.com.cn/

##### Milvus

https://milvus.io/cn/docs/v2.0.x/create_collection.md






























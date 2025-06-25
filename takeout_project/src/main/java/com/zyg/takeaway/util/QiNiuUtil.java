package com.zyg.takeaway.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.BatchStatus;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 七牛云上传下载工具类
 */
public class QiNiuUtil {

    /**
     * 上传文件方法
     *
     * @param fileContent 被上传文件的字节数组
     * @param fileName 文件的名称
     */
    public static void upload(byte[] fileContent, String fileName) {

        // AK:mT9WmjYDYNp0X5zv_VsxRTt3rQLAuQz15ZkImJPN
        // sk:hfhqlCIuxzIhfVU8Tv6_YJRyL7kOYz34NEM6i2gI
        // 构造一个带指定 Region 对象的配置类
        ////////////////////////////////////////Region.createWithRegionId("z2")////////////////////////////////////////
        Configuration cfg = new Configuration(Region.createWithRegionId("z2"));
        // 优化上传空间
        cfg.resumableUploadAPIVersion = Configuration.ResumableUploadAPIVersion.V2;// 指定分片上传版本
        //...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);
        ///////////////////////////凭证就是绑定自己七牛云的账号信息
        //...生成上传凭证，然后准备上传
        String accessKey = "1gQpeVHb_iZQaTuZUEJBVXtmLsaZ2Jv60RmVS-TT";
        String secretKey = "KaOh3Mhixsx4ssNvXSRNmHu34cQ88euJBfGdEv6b";
        String bucket = "8848-takeaway-system";

        // 默认不指定key的情况下，以文件内容的hash值作为文件名
        // String key = null;

        // try {
        // 测试代码注释掉
        // byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);

        try {
            ////////////////////////////////////////////////////////////////////////////////
            // Response response = uploadManager.put(uploadBytes, key, upToken);
            Response response = uploadManager.put(fileContent, fileName, upToken);
            // 解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            System.out.println(putRet.key);
            System.out.println(putRet.hash);
        } catch (QiniuException ex) {
            ex.printStackTrace();
            if (ex.response != null) {
                System.err.println(ex.response);

                try {
                    String body = ex.response.toString();
                    System.err.println(body);
                } catch (Exception ignored) {
                }
            }
        }
        // } catch (UnsupportedEncodingException ex) {
        // ignore -忽视
        // }

    }

    /**
     * 文件下载
     * @param name 文件名称
     * @return 输出流
     */
    public static InputStream download(String name) {
        ///////////////////////////官网复制///////////////////////////////////
        // 传输的就是文件名称 不需要再次定义
        // String fileName = "a/b/qiniu.jpg";
        // domainOfBucket 中的域名为用户 bucket 绑定的下载域名，下面域名仅为示例，不可使用
        String domainOfBucket = "http://sy13svfwv.hn-bkt.clouddn.com";
        // 有异常尽量try...catch处理不要往上抛
        String encodedFileName = null;
        try {
            // 把文件名称的输出存储格式设置为utf-8，把URL编码中的 +号替换为 %20 -> URL格式
            encodedFileName = URLEncoder.encode(name, "utf-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // domainOfBucket=http://sk8v689gx.hn-bkt.clouddn.com  encodedFileName=name(文件名称)
        // 这里已经拼接好图片的访问的URL 但是调用方法所需要的是输出流
        String finalUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
        System.out.println(finalUrl);

        // 把URL图片网络地址转换为流 手动修改
        // 图片实例化
        Request request = new Request.Builder().url(finalUrl).build();

        // 模拟游览器发送图片 获取图片资源做响应流
        try {
            okhttp3.Response response = new OkHttpClient().newCall(request).execute();
            // 判断请求是否成功 是否获取到图片资源
            if (response.isSuccessful()) {
                // 断言 确保response.body 不为null 发送成功却没有资源
                assert response.body() != null;
                // 把资源转换成流 返回出去一个流
                return response.body().byteStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 不需要处理
        return null;
    }

    /**
     * 文件单个删除
     * @param fileName 文件名称
     */
    public static void deleteImg(String fileName) {
        // 构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.createWithRegionId("z2"));
        //...其他参数参考类注释
        String accessKey = "1gQpeVHb_iZQaTuZUEJBVXtmLsaZ2Jv60RmVS-TT";
        String secretKey = "KaOh3Mhixsx4ssNvXSRNmHu34cQ88euJBfGdEv6b";
        String bucket = "8848-takeaway-system";

        // String key = "your file key";

        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, fileName);
        } catch (QiniuException ex) {
            // 如果遇到异常，说明删除失败
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
        }
    }

    /**
     * 文件多个删除
     * @param keys 集合/数组
     */
    // public static void deletesImg(String[] keys){
    public static void deletesImg(List<String> keys){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.createWithRegionId("z2"));
        //...其他参数参考类注释

        String accessKey = "1gQpeVHb_iZQaTuZUEJBVXtmLsaZ2Jv60RmVS-TT";
        String secretKey = "KaOh3Mhixsx4ssNvXSRNmHu34cQ88euJBfGdEv6b";
        String bucket = "8848-takeaway-system";

        Auth auth = Auth.create(accessKey, secretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);

        try {
            //单次批量请求的文件数量不得超过1000
            // String[] keyList = new String[]{
            //         "qiniu.jpg",
            //         "qiniu.mp4",
            //         "qiniu.png",
            // };
            // List<String> keyList 转换成数组
            String[] keyList = keys.stream().map(String::valueOf).toArray(String[]::new);

            BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
            batchOperations.addDeleteOp(bucket, keyList);
            Response response = bucketManager.batch(batchOperations);
            BatchStatus[] batchStatusList = response.jsonToObject(BatchStatus[].class);

            for (int i = 0; i < keyList.length; i++) {
                BatchStatus status = batchStatusList[i];
                String key = keyList[i];
                System.out.print(key + "\t");
                if (status.code == 200) {
                    System.out.println("delete success");
                } else {
                    System.out.println(status.data.error);
                }
            }
        } catch (QiniuException ex) {
            System.err.println(ex.response.toString());
        }

    }
}

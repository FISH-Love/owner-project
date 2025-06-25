package com.zyg.takeaway.util;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.internal.Mimetypes;
import com.aliyun.oss.model.*;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 阿里云文件存储工具类
 */
public class AliYunOssUtil {
    private static final String OSS_ACCESS_KEY_ID = "LTAI5tAJdUSpsP56bugGKRFX";
    private static final String OSS_ACCESS_KEY_SECRET = "gefZFwFtEltFCwYjLypgqkyQqDE73Y";
    private static final String BUCKET_NAME = "takeaway-system32";
    private static final String ENDPOINT = "https://oss-cn-hangzhou.aliyuncs.com";

    // domainOfBucket
    private static final String DOMAIN_OF_BUCKET = "https://takeaway-system32.oss-cn-hangzhou.aliyuncs.com/";

    public static void upload(InputStream inputStream, String fileName) {

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, OSS_ACCESS_KEY_ID, OSS_ACCESS_KEY_SECRET);

        try {
            ossClient.putObject(BUCKET_NAME, fileName, inputStream);
            /////////////////////////////////////////////////////////////////////////////////
            // 设置访问文件为公开路径 权限只读
            ossClient.setObjectAcl(BUCKET_NAME, fileName, CannedAccessControlList.PublicRead);
            // 把访问文件路径输出到控制台  https://takeaway-system32.oss-cn-hangzhou.aliyuncs.com/bbbbb.jpg
            System.out.println(DOMAIN_OF_BUCKET + fileName);
            //////////////////////////////////////////////////////////////////////////////////////////////

        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 文件响应
     * @param name 文件名称
     * @return 输出流
     */
    public static InputStream download(String name) {
        ///////////////////////////官网复制///////////////////////////////////
        // 传输的就是文件名称 不需要再次定义
        // String fileName = "a/b/qiniu.jpg";
        // domainOfBucket 中的域名为用户 bucket 绑定的下载域名，下面域名仅为示例，不可使用
        // https://takeaway-system32.oss-cn-hangzhou.aliyuncs.com/98f17a7e4c404abda6fac5b2e9709770.png

        // 有异常尽量try...catch处理不要往上抛
        String encodedFileName = null;
        try {
            // 把文件名称的输出存储格式设置为utf-8，把URL编码中的 +号替换为 %20 -> URL格式
            encodedFileName = URLEncoder.encode(name, "utf-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // 这里已经拼接好图片的访问的URL 但是调用方法所需要的是输出流
        String finalUrl = String.format("%s/%s", DOMAIN_OF_BUCKET, encodedFileName);
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

    // 单个删除图片

    // 批量删除图片
    public static void deletesImg(List<String> keys){
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。
        EnvironmentVariableCredentialsProvider credentialsProvider = null;
        try {
            credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        } catch (com.aliyuncs.exceptions.ClientException e) {
            System.out.println("删除失败，报错：" + e.getMessage());
        }
        // 填写Bucket名称，例如examplebucket。
        String bucketName ="takeaway-system32";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);

        try {
            // // 删除文件。
            // // 填写需要删除的多个文件完整路径。文件完整路径中不能包含Bucket名称。
            // List<String> keys = new ArrayList<String>();
            // keys.add("000701d15fde4ae1af771f1a11d4a39d.jpeg");
            // keys.add("1e341d42ad2f40d09dd53b4d27823904.jpg");
            // keys.add("2e3436a04cd244f4b6f364a947d315de.jpg");
            // keys.add("382a42bc058440f588c9abb42e5f65a3.png");
            // keys.add("958727d0de174d0592bc31e68cdd5050.jpg");

            DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(keys).withEncodingType("url"));
            List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
            try {
                for(String obj : deletedObjects) {
                    String deleteObj =  URLDecoder.decode(obj, "UTF-8");
                    System.out.println(deleteObj);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

}


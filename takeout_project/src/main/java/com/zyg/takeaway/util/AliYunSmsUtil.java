package com.zyg.takeaway.util;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import darabonba.core.client.ClientOverrideConfiguration;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * 阿里云短信工具类
 */
public class AliYunSmsUtil {

    /**
     * Java异步发送短信 V2.0
     * @param phone 手机号
     * @param code 验证码
     */
    public static void sendSms(String phone,String code){
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                // Please ensure that the environment variables ALIBABA_CLOUD_ACCESS_KEY_ID and ALIBABA_CLOUD_ACCESS_KEY_SECRET are set.
                .accessKeyId("LTAI5tQ38Tu5oSdKiM9DxYq9")
                .accessKeySecret("slgVpKC33ra3WsZOUSTqolQPsppIWT")
                //.securityToken(System.getenv("ALIBABA_CLOUD_SECURITY_TOKEN")) // use STS token
                .build());

        // Configure the Client
        AsyncClient client = AsyncClient.builder()
                .region("cn-hangzhou") // Region ID
                //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
                .credentialsProvider(provider)
                //.serviceConfiguration(Configuration.create()) // Service-level configuration
                // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
                                .setEndpointOverride("dysmsapi.aliyuncs.com")
                        //.setConnectTimeout(Duration.ofSeconds(30))
                )
                .build();

        // 处理验证码是json格式的字符串类型 {"code":"1234"}  - > Map-K V
        // 使用gson里面的Map格式转json格式方法
        Gson gson = new GsonBuilder().create();
        // 创建HashMap填充转换字段
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("code",code);
        //格式转换 HashMap转JSON格式
        String jsonCode = gson.toJson(stringStringHashMap);


        // Parameter settings for API request
        SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
                .signName("阿里云短信测试")
                .templateCode("SMS_154950909")
                // 手机号直接使用变量替换
                .phoneNumbers(phone)
                // 验证码：json格式 + 转义符=json格式的字符串 无法直接填充变量
                // .templateParam("{\"code\":\"1234\"}")
                .templateParam(jsonCode)
                // Request-level configuration rewrite, can set Http request parameters, etc.
                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                .build();

        // Asynchronously get the return value of the API request
        CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);

        SendSmsResponse resp = null;
        try {
            // Synchronously get the return value of the API request
            resp = response.get();
        } catch (Exception e) {
            System.out.println("同步获取 API 请求的返回值异常："+e.getMessage());
        }
        System.out.println(new Gson().toJson(resp));

        // Finally, close the client
        client.close();

    }
}

package com.zyg.takeaway.service.impl;

import com.zyg.takeaway.common.R;
import com.zyg.takeaway.service.CommonQiNiuService;
import com.zyg.takeaway.util.QiNiuUtil;
import org.apache.http.HttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 七牛云文件上传下载
 */
@Service
public class CommonQiNiuServiceImpl implements CommonQiNiuService {
    @Override
    public R<String> upload(MultipartFile file) {
        // 1.获取文件内容：io流字节方式
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            // 打印到栈跟踪异常处理
            e.printStackTrace();
            // 响应失败
            return R.error("文件上传失败！");
        }
        // 2.生成新的文件名称 防止上传文件名重复
        String fileName = UUID.randomUUID().toString().replace("-", "");
        // 3.调用工具 传输到七牛云上
        QiNiuUtil.upload(bytes, fileName);
        // 4.响应参数返回
        return R.success(fileName);
    }

    @Override
    public void download(String name, HttpServletResponse response) {
        try {
            // 输入流，通过输入流读取文件内容
            // FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            // 通过七牛云工具类只需要传递一个图片名称，接受一个图片输出流响应给前端就可以了
            InputStream inputStream = QiNiuUtil.download(name);
            // 输出流，通过输出流将文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("image/jpeg");
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
            // 关闭资源
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package com.zyg.takeaway.service;

import com.zyg.takeaway.common.R;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件上传下载业务接口-七牛云
 */
public interface CommonQiNiuService {
    /**
     * 文件上传
     *
     * @param file 文件路径
     * @return 文件名称
     */
    R<String> upload(MultipartFile file);
    /**
     * 文件下载
     *
     * @param name     文件名称
     * @param response 响应图片
     */
    void download(String name, HttpServletResponse response);
}
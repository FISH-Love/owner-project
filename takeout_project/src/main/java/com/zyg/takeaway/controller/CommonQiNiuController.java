package com.zyg.takeaway.controller;

import com.zyg.takeaway.common.R;
import com.zyg.takeaway.service.CommonQiNiuService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件上传下载-七牛云
 */
@RestController
@RequestMapping("/common")
public class CommonQiNiuController {
    // 引入文件上传接口
    private final CommonQiNiuService commonQiNiuService;
    public CommonQiNiuController(CommonQiNiuService commonQiNiuService) {
        this.commonQiNiuService = commonQiNiuService;
    }
    /**
     * 文件上传
     *
     * @param file 文件路径
     * @return 文件名称
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        // 根据端口测试发现前端需要的响应是文件名称
        return commonQiNiuService.upload(file);
    }
    /**
     * 文件下载
     *
     * @param name     文件名称
     * @param response 响应图片
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        commonQiNiuService.download(name, response);
    }
}
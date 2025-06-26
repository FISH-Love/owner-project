package com.example.CultivationCimulator.Controller;

/**
 * 功能：
 * 作者：jar良
 * 日期：2025/6/12下午9:17
 */

import com.example.CultivationCimulator.Util.VerifyUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class CaptchaController {

    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. 生成验证码及图片
        VerifyUtil verifyUtil = VerifyUtil.newBuilder()
                .setSize(4)         // 4位验证码
                .setWidth(120)       // 宽度120px（与前端img宽度一致）
                .setHeight(40)       // 高度40px（与前端img高度一致）
                .build();
        Object[] result = verifyUtil.createImage();
        String captcha = (String) result[0];
        BufferedImage image = (BufferedImage) result[1];

        // 2. 将验证码存入Session（有效期通常为2分钟）
        request.getSession().setAttribute("captcha", captcha);
        request.getSession().setAttribute("captchaTime", System.currentTimeMillis());

        // 3. 输出图片到响应
        response.setContentType("image/jpeg");
        ImageIO.write(image, "jpeg", response.getOutputStream());
    }

    /**
     * 验证验证码（可单独作为接口，或在登录接口中验证）
     */
    public boolean verifyCaptcha(HttpServletRequest request, String userInput) {
        if (userInput == null) return false;

        // 从Session获取正确验证码
        String correctCaptcha = (String) request.getSession().getAttribute("captcha");
        Long captchaTime = (Long) request.getSession().getAttribute("captchaTime");

        // 验证码有效期检查（如2分钟）
        if (correctCaptcha == null || captchaTime == null ||
                System.currentTimeMillis() - captchaTime > 120000) {
            // 验证码过期或不存在
            request.getSession().removeAttribute("captcha");
            request.getSession().removeAttribute("captchaTime");
            return false;
        }

        // 验证验证码（忽略大小写）
        boolean result = userInput.equalsIgnoreCase(correctCaptcha);
        if (result) {
            // 验证成功后移除Session中的验证码（防止重复使用）
            request.getSession().removeAttribute("captcha");
            request.getSession().removeAttribute("captchaTime");
        }
        return result;
    }
}
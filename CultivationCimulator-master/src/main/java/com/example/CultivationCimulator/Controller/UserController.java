package com.example.CultivationCimulator.Controller;

import com.example.CultivationCimulator.POJO.User;
import com.example.CultivationCimulator.Service.UserService;
import com.example.CultivationCimulator.Util.Result;
import com.example.CultivationCimulator.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private JwtUtil jwtUtil;
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Result<User> registerController(@RequestBody User newUser){
        User user = userService.registerService(newUser);
        if(user!=null){
            return Result.success(user,"注册成功！");
        }else{
            return Result.error("456","用户名已存在！");
        }
    }
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        // 1. 提取参数
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        String captcha = (String) params.get("captcha");

        // 2. 参数校验
        if (username == null || password == null || captcha == null) {
            return Result.error("400", "参数不完整");
        }

        // 3. 验证码验证（调用CaptchaController的验证方法）
        CaptchaController captchaController = new CaptchaController();
        if (!captchaController.verifyCaptcha(request, captcha)) {
            return Result.error("400", "验证码错误或已过期");
        }

        // 4. 原有登录逻辑
        User user = userService.loginService(username, password);
        if (user != null) {
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            Map<String, Object> data = new HashMap<>();
            data.put("user", user);
            data.put("token", token);
            return Result.success(data, "登录成功");
        } else {
            return Result.error("123", "账号或密码错误");
        }
    }
    //检查用户是否绑定角色的接口
    @GetMapping("/{username}/hasRole")
    public boolean checkUserHasRole(@PathVariable String username) {
        return userService.checkUserHasRole(username);
    }

    //根据用户名绑定角色id的接口
    @PostMapping("/{username}/bindRole")
    public Result<?> bindRoleToUser(
            @PathVariable String username,
            @RequestBody Long roleId
    ) {
        boolean success = userService.bindRoleToUser(username, roleId);
        if (success) {
            return Result.success(null, "角色绑定成功");
        } else {
            return Result.error("500", "角色绑定失败：用户不存在或已绑定角色");
        }
    }
    //根据用户名查用户所有信息
    @PostMapping("/findUserByUname")
    public User findUserByUsername(@RequestParam String username) {
        return userService.findUserByUsername(username);
    }
}
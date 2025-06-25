package com.zyg.takeaway.filter;

import com.alibaba.fastjson.JSON;
import com.zyg.takeaway.common.R;
import com.zyg.takeaway.entity.Employee;
import com.zyg.takeaway.util.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * 检查用户是否已经完成登录
 */
@Slf4j // 生成日志
@Component // 将这个类放入容器
@WebFilter(urlPatterns = "/**") // 表示我是一个Filter(拦截器) 拦截规则
public class LoginCheckFilter implements Filter {
    /**
     * 路径匹配器，支持通配符
     */
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1、获取本次请求的URI
        String requestURI = request.getRequestURI();// /backend/index.html
        // 打印日志
        log.info("拦截到请求：{}", requestURI);
        // 定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
//                "/common/**",   测试 暂时注释
                "/user/sendMsg",
                "/user/login"

        };

        // 2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        // 3、如果不需要处理，则直接放行
        if (check) {
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }


        // sec-ch-ua-platform:"Android" -- 设备码
        // sec-ch-ua-platform:"Windows"
        // 根据前端设备登录标识来判断是移动端还是管理端
        // 获取请求头的设备标识
        String header = request.getHeader("sec-ch-ua-platform");
        // C端
        if (header.equalsIgnoreCase("\"Android\"")){
            // 判断C端用户登录状态，如果已登录，则直接放行
            if(request.getSession().getAttribute("user") != null){
                log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));
                Long userId = (Long) request.getSession().getAttribute("user");
                BaseContext.setCurrentId(userId);
                filterChain.doFilter(request,response);
                return;
            }
        }
        // 管理端
        if (header.equalsIgnoreCase("\"Windows\"")){
            // 4、判断登录状态，如果已登录，则直接放行
            Employee employee = (Employee) request.getSession().getAttribute("employee");
            if (!Objects.isNull(employee)) {
                log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("employee"));
                // 获取当前登录的ID 存储到ThreadLocal
                BaseContext.setCurrentId(employee.getId());
                // 检查当前线程是否是一个
                System.out.println(Thread.currentThread().getName() + "在过滤器中获取的线程1");
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     *
     * @param urls       多个请求路径
     * @param requestURI 请求 URL
     * @return 结果响应
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
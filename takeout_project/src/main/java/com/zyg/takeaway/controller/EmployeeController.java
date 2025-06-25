package com.zyg.takeaway.controller;

import com.zyg.takeaway.common.R;
import com.zyg.takeaway.common.dto.employee.EmployeeAddDTO;
import com.zyg.takeaway.common.dto.employee.EmployeeLoginDTO;
import com.zyg.takeaway.common.dto.employee.EmployeeUpdateDTO;
import com.zyg.takeaway.entity.Employee;
import com.zyg.takeaway.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @PostMapping("/login")
    @CrossOrigin(origins = "*")
    public R<Employee> login(@RequestBody EmployeeLoginDTO dto, HttpServletRequest request) {
        R<Employee> r = employeeService.login(dto);
        request.getSession().setAttribute("employee", r.getData());
        return r;
    }
    @CrossOrigin
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        // 因为退出没有业务层所有直接在表现层判断是否退出成功并给出提示
        try {
            request.getSession().removeAttribute("employee");
        } catch (Exception e) {
            return R.error("退出失败！");
        }
        return R.success("退出成功！");
    }
    /**
     * 员工新增
     *
     * @param dto 新增前端传递用户信息
     * @return 请求结果
     */
    @PostMapping
    public R<String> addEmployee(@RequestBody EmployeeAddDTO dto) {
        return employeeService.addEmployee(dto);
    }
    /**
     * 分页查询员工信息
     *
     * @param page     当前页
     * @param pageSize 页面大小
     * @param name     查询条件
     * @return 请求结果
     */
    @GetMapping("/page")
    public R page(Long page, Long pageSize, String name) {
        R r = employeeService.findByPage(page, pageSize, name);
        return r;
    }
//    /**
//     * 员工状态更新
//     * 与后面修改（更新）员工重复这个注释掉
//     */
//    @PutMapping
//    public R editStatus(@RequestBody EmployeeEditStatusDTO dto) {
//        R r = employeeService.editStatus(dto);
//        return r;
//    }
    /*
     * 根据ID查询/数据回显
     *
     * @param id 查询id
     * @return 返回结果
     */
    @GetMapping("{id}")
    public R getById(@PathVariable("id") Long id) {
        R r = employeeService.getOneById(id);
        return r;
    }
    /**
     * 更新（修改）员工信息
     *
     * @param dto
     * @return 更新员工信息接口与更新员工状态请求重叠
     * 注释掉上面更新员工状态接口后面在此次请求中做更新适配
     */
    @PutMapping
    public R updateEmpById(@RequestBody EmployeeUpdateDTO dto) {
        R r = employeeService.updates(dto);
        return r;
    }


    @GetMapping("/test")
    public String test() {
        return "接口测试成功";
    }
    // 其他方法（如登出）...

}

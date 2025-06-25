package com.zyg.takeaway.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zyg.takeaway.common.R;
import com.zyg.takeaway.common.dto.employee.EmployeeAddDTO;
import com.zyg.takeaway.common.dto.employee.EmployeeLoginDTO;
import com.zyg.takeaway.common.dto.employee.EmployeeUpdateDTO;
import com.zyg.takeaway.entity.Employee;
import com.zyg.takeaway.mapper.EmployeeMapper;
import com.zyg.takeaway.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.aliyun.core.utils.StringUtils;
import java.util.Objects;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Override
    public R<Employee> login(EmployeeLoginDTO dto) {
        // 1、参数校验
        if (StringUtils.isBlank(dto.getUsername())) {
            return R.error("账号不能为空！");
        }
        if (StringUtils.isBlank(dto.getPassword())) {
            return R.error("密码不能为空！");
        }
        // 2、业务处理（登录业务处理） 根据用户名去数据库查询
        LambdaQueryWrapper<Employee> wrapper = Wrappers.lambdaQuery();
        // 代码优化 投影查询
        wrapper.select(
                Employee::getId,
                Employee::getUsername,
                Employee::getName,
                Employee::getPassword,
                Employee::getStatus
        );
        wrapper.eq(Employee::getUsername, dto.getUsername());
        // 查询的是一条数据
        Employee employeeInDb = this.getOne(wrapper);
        // ① 查不到   -- 员工身份非法
        if (Objects.isNull(employeeInDb)) {
            return R.error("大哥身份非法！");
        }
        // ② 查到   -- 密码比对(对前端传递密码先加密在和数据中密码比对)
        // 获取数据库中的密码
        String passwordEncoder = employeeInDb.getPassword();
        // 获取网页传输的密码
        String passwordFront = dto.getPassword();
        // 对网页密码进行加密
        String passwordFrontEncoder = DigestUtils.md5DigestAsHex(passwordFront.getBytes());
        // 判断加密后的密码是否相同
        if (!passwordFrontEncoder.equals(passwordEncoder)) {
            return R.error("密码错误，请重新输入！");
        }
        // 用户状态比对
        if (employeeInDb.getStatus() == 0) {
            return R.error("当前账号已经被禁用，请联系店长！");
        }
        // 3、数据返回 数据脱敏处理
        employeeInDb.setPassword("");
        employeeInDb.setIdNumber("");
        return R.success(employeeInDb);
    }
    @Override
    public R<String> addEmployee(EmployeeAddDTO dto) {
        // 1. 参数校验
        if (StringUtils.isBlank(dto.getName())) {
            return R.error("员工姓名不能为空！");
        }
        if (StringUtils.isBlank(dto.getUsername())) {
            return R.error("员工账号不能为空！");
        }
        if (StringUtils.isBlank(dto.getPhone())) {
            return R.error("手机号不能为空！");
        }
        if (StringUtils.isBlank(dto.getSex())) {
            return R.error("性别不能为空！");
        }
        // 2. 核心业务处理
        Employee employee = new Employee();
        // Bean 拷贝
        BeanUtils.copyProperties(dto, employee);
        // 设置员工的默认密码 123456
        // 密码加密处理：
        String passwordEncoder = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(passwordEncoder);
        employee.setStatus(0); // 新添加员工状态 禁用！
        // 使用自动填充
        // employee.setCreateTime(LocalDateTime.now());
        // employee.setUpdateTime(LocalDateTime.now());
        // employee.setCreateUser(1L);
        // employee.setUpdateUser(1L);
        this.save(employee);
        // 3. 数据返回
        return R.success("员工添加成功！");
    }
    @Override
    public R<Page> findByPage(Long page, Long pageSize, String name) {
        // 1. 参数校验
        if (Objects.isNull(page)) {
            return R.error("参数非法！");
        }
        if (page <= 0L) {
            page = 1L;
        }
        if (Objects.isNull(pageSize)) {
            return R.error("参数非法！");
        }
        if (pageSize <= 0L) {
            pageSize = 10L;
        }
        // 2. 核心业务处理 查询所有含带有多余字段 建议使用投影查询
        IPage<Employee> pageQuery = new Page<>(page, pageSize);
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isBlank(name) ? false : true, Employee::getName, name);
        wrapper.orderByDesc(Employee::getCreateTime);
        Page<Employee> result = (Page<Employee>) this.page(pageQuery, wrapper);
        // 3.数据封装返回
        return R.success(result);
    }
    @Override
    public R editStatus(EmployeeUpdateDTO dto) {
        // 1、参数校验
        if (Objects.isNull(dto.getId())) {
            return R.error("参数非法！");
        }
        if (Objects.isNull(dto.getStatus())) {
            return R.error("参数非法！");
        }
        // 必须校验：衡量程序员逻辑完整性标准！
        if (dto.getStatus() != 0 && dto.getStatus() != 1) {
            return R.error("参数非法！");
        }
        // 2、核心逻辑（根据员工id更新员工状态）
        Employee entity = new Employee();
        entity.setId(dto.getId());
        entity.setStatus(dto.getStatus());
        this.saveOrUpdate(entity);
        // 3、数据封装返回
        return R.success("修改状态成功！");
    }
    @Override
    public R getOneById(Long id) {
        // 1. 参数校验
        if (Objects.isNull(id)) {
            return R.error("参数非法！");
        }
        // 2 .核心业务逻辑  （投影查询）  select * from tb_表   ：  select name ，age  from tb_表
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper();
        wrapper.eq(Employee::getId, id);
        // 注意有问题：需要携带ID！！！！！！！！！！！！！！！！！！！！
        wrapper.select(Employee::getId, Employee::getUsername, Employee::getName, Employee::getPhone, Employee::getIdNumber, Employee::getSex);
        // wrapper.select会造成覆盖导致返回参数只包含一个数据 直接使用上面代码一次查询
        // wrapper.select(Employee::getName);
        // wrapper.select(Employee::getPhone);
        // wrapper.select(Employee::getIdNumber);
        Employee one = this.getOne(wrapper);
        // 3. 数据封装
        R<Employee> success = R.success(one);
        return success;
    }
    @Transactional   // 事务控制 新增记得加
    public R updateEmpById(EmployeeUpdateDTO dto) {

        // 1.参数校验

        if (StringUtils.isBlank(dto.getName())) {
            return R.error("员工姓名不能为空！");
        }
        if (StringUtils.isBlank(dto.getUsername())) {
            return R.error("员工账号不能为空！");
        }
        if (StringUtils.isBlank(dto.getPhone())) {
            return R.error("手机号不能为空！");
        }
        if (StringUtils.isBlank(dto.getSex())) {
            return R.error("性别不能为空！");
        }
        if (Objects.isNull(dto.getId())) {
            return R.error("参数非法！");
        }
        // 2. 处理核心逻辑
        Employee entity = new Employee();
        BeanUtils.copyProperties(dto, entity);
        this.updateById(entity);
        // 3. 数据封装
        return R.success("更新成功！");
    }

    /**
     * 做员工状态和员工修改判断
     *
     * @param dto
     * @return
     * 适配两个功能
     */
    @Override
    public R updates(EmployeeUpdateDTO dto) {
        // 如果传递status 就说明是启用或者禁用 功能
        if (!Objects.isNull(dto.getStatus())) {
            return this.editStatus(dto);
        }
        // 否则就是更新员工信息功能
        return this.updateEmpById(dto);
    }

}


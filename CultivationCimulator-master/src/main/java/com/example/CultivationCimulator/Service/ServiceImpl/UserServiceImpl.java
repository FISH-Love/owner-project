package com.example.CultivationCimulator.Service.ServiceImpl;

import com.example.CultivationCimulator.Dao.UserDao;
import com.example.CultivationCimulator.POJO.User;
import com.example.CultivationCimulator.Service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;
    @Override
    public User loginService(String username, String password) {
        // 如果账号密码都对则返回登录的用户对象，若有一个错误则返回null
        User user = userDao.findByUsernameAndPassword(username, password);
        // 重要信息置空
        if(user != null){
            user.setPassword("");
        }
        return user;
    }

    @Override
    public User registerService(User user) {
        //当新用户的用户名已存在时
        if(userDao.findByUsername(user.getUsername())!=null){
            // 无法注册
            return null;
        }else{
            //返回创建好的用户对象
            User newUser = userDao.save(user);
            if(newUser != null){
                newUser.setPassword("");
            }
            return newUser;
        }
    }

    @Override
    public boolean checkUserHasRole(String username) {
        return userDao.existsByUsernameAndRoleIdNotNull(username);
    }
    //为指定用户绑定角色的方法
    @Override
    @Transactional
    public boolean bindRoleToUser(String username, Long roleId) {
        // 检查用户是否存在
        User user = userDao.findByUsername(username);
        if (user == null) {
            return false; // 用户不存在
        }

        // 检查用户是否已绑定角色
        if (user.getRoleId() != null) {
            return false; // 已绑定角色
        }

        // 执行更新操作
        int updatedRows = userDao.updateRoleIdByUsername(username, roleId);
        return updatedRows > 0;
    }

    @Override
    public User findUserByUsername(String username) {
        return userDao.findByUsername(username);
    }
}

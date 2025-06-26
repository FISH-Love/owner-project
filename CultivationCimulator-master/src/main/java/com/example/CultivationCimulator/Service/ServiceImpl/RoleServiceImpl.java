package com.example.CultivationCimulator.Service.ServiceImpl;

import com.example.CultivationCimulator.Dao.RoleDao;
import com.example.CultivationCimulator.POJO.Role;
import com.example.CultivationCimulator.Service.RoleService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
@Service
public class RoleServiceImpl implements RoleService {
    @Resource
    private RoleDao roleDao;

    @Override
    public List<String> findAllRoleNames() {
        return roleDao.findAllRoleName();
    }

    @Override
    public List<Role> findAllRoles() {
        return roleDao.findAllRole();
    }
}

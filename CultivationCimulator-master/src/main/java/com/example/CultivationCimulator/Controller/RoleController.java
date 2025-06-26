package com.example.CultivationCimulator.Controller;

import com.example.CultivationCimulator.POJO.Role;
import com.example.CultivationCimulator.Service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping("/names")
    public List<String> findAllRoleNames(){
        return roleService.findAllRoleNames();
    }
    @PostMapping("/roles")
    public List<Role> findAllRoles(){
        return roleService.findAllRoles();
    }
}

package com.example.askforleavedemo.mapper;

import com.example.askforleavedemo.model.Role;
import com.example.askforleavedemo.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper//直接写mapper注解，不需要再作扫描的配置了。
public interface UserMapper {
    User loadUserByUsername(String username);

    List<Role> getUserRolesByUserId(Integer id);

    List<User> getAllUsers();

    List<User> getAllUsersExcludeCurrent(String name);
}

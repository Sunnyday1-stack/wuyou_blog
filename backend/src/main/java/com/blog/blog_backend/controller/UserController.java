package com.blog.blog_backend.controller;

import com.blog.blog_backend.entity.User;
import com.blog.blog_backend.mapper.UserMapper;
import com.blog.blog_backend.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    // 获取所有用户列表（仅管理员）
    @GetMapping("/users")
    public List<Map<String, Object>> listUsers() {
        checkAdmin();
        List<User> users = userMapper.selectList(null);
        return users.stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("username", user.getUsername());
            map.put("email", user.getEmail());
            map.put("role", user.getRole());
            map.put("createTime", user.getCreateTime());
            return map;
        }).collect(Collectors.toList());
    }

    // 删除用户（仅管理员，不能删除自己）
    @DeleteMapping("/users/{id}")
    public Map<String, Object> deleteUser(@PathVariable Integer id) {
        checkAdmin();
        Map<String, Object> result = new HashMap<>();
        User currentUser = UserHolder.getCurrentUser();
        if (currentUser.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不能删除自己的账号");
        }
        User target = userMapper.selectById(id);
        if (target == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        userMapper.deleteById(id);
        result.put("success", true);
        result.put("message", "账号已删除");
        return result;
    }

    private void checkAdmin() {
        var currentUser = UserHolder.getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权限，需要管理员身份");
        }
    }
}
package com.blog.blog_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.blog_backend.entity.User;
import com.blog.blog_backend.mapper.UserMapper;
import com.blog.blog_backend.utils.JwtUtil;
import com.blog.blog_backend.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        // 检查用户名是否已存在
        User exist = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (exist != null) {
            result.put("success", false);
            result.put("message", "用户名已存在");
            return result;
        }
        user.setPassword(encoder.encode(user.getPassword()));
        if (user.getEmail() == null) user.setEmail("");
        user.setRole("USER");
        userMapper.insert(user);
        result.put("success", true);
        result.put("message", "注册成功");
        return result;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        User dbUser = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
        if (dbUser == null || !encoder.matches(user.getPassword(), dbUser.getPassword())) {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
            return result;
        }
        String token = jwtUtil.generateToken(dbUser.getUsername());
        result.put("success", true);
        result.put("token", token);
        result.put("username", dbUser.getUsername());
        result.put("avatar", dbUser.getAvatar());
        // 新增返回 userId 和 role
        result.put("userId", dbUser.getId());
        result.put("role", dbUser.getRole());
        return result;
    }

    // 账号注销接口
    @DeleteMapping("/delete")
    public Map<String, Object> deleteAccount(@RequestParam String password) {
        Map<String, Object> result = new HashMap<>();
        // 获取当前登录用户
        User currentUser = UserHolder.getCurrentUser();
        if (currentUser == null) {
            result.put("success", false);
            result.put("message", "未登录");
            return result;
        }
        // 验证密码
        if (!encoder.matches(password, currentUser.getPassword())) {
            result.put("success", false);
            result.put("message", "密码错误");
            return result;
        }
        // 删除用户（级联删除评论）
        userMapper.deleteById(currentUser.getId());
        result.put("success", true);
        result.put("message", "账号已注销");
        return result;
    }
}
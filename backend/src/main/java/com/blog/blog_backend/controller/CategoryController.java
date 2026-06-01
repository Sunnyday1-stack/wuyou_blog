package com.blog.blog_backend.controller;

import com.blog.blog_backend.entity.Category;
import com.blog.blog_backend.mapper.CategoryMapper;
import com.blog.blog_backend.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    // 公开接口：获取所有分类
    @GetMapping
    public List<Category> list() {
        return categoryMapper.selectList(null);
    }

    // 管理接口：创建分类（仅管理员）
    @PostMapping
    public Category create(@RequestBody Category category) {
        checkAdmin();
        category.setCreateTime(LocalDateTime.now());
        categoryMapper.insert(category);
        return category;
    }

    // 管理接口：更新分类（仅管理员）
    @PutMapping("/{id}")
    public Category update(@PathVariable Integer id, @RequestBody Category category) {
        checkAdmin();
        category.setId(id);
        categoryMapper.updateById(category);
        return category;
    }

    // 管理接口：删除分类（仅管理员）
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        checkAdmin();
        categoryMapper.deleteById(id);
    }

    private void checkAdmin() {
        var currentUser = UserHolder.getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权限，需要管理员身份");
        }
    }
}
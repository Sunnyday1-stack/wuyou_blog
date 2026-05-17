package com.blog.blog_backend.controller;

import com.blog.blog_backend.entity.Tag;
import com.blog.blog_backend.mapper.TagMapper;
import com.blog.blog_backend.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    @Autowired
    private TagMapper tagMapper;

    // 获取所有标签（公开）
    @GetMapping
    public List<Tag> list() {
        return tagMapper.selectList(null);
    }

    // 管理接口：创建标签（仅管理员）
    @PostMapping
    public Tag create(@RequestBody Tag tag) {
        checkAdmin();
        tag.setCreateTime(LocalDateTime.now());
        tagMapper.insert(tag);
        return tag;
    }

    // 管理接口：删除标签（仅管理员，会级联删除关联关系）
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        checkAdmin();
        tagMapper.deleteById(id);
    }

    private void checkAdmin() {
        var currentUser = UserHolder.getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权限");
        }
    }
    @PutMapping("/{id}")
    public Tag update(@PathVariable Integer id, @RequestBody Tag tag) {
        checkAdmin();
        tag.setId(id);
        tagMapper.updateById(tag);
        return tag;
    }
}

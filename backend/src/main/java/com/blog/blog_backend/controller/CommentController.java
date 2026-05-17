package com.blog.blog_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.blog_backend.entity.Comment;
import com.blog.blog_backend.entity.User;
import com.blog.blog_backend.mapper.CommentMapper;
import com.blog.blog_backend.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentMapper commentMapper;

    @GetMapping("/article/{articleId}")
    public List<Comment> listByArticle(@PathVariable Integer articleId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getArticleId, articleId)
               .orderByDesc(Comment::getCreateTime);
        return commentMapper.selectList(wrapper);
    }

    @PostMapping
    public Comment create(@RequestBody Comment comment) {
        User currentUser = UserHolder.getCurrentUser();
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        comment.setUserId(currentUser.getId());
        // 如果前端没有传 nickname，则使用用户名
        if (comment.getNickname() == null || comment.getNickname().isEmpty()) {
            comment.setNickname(currentUser.getUsername());
        }
        comment.setCreateTime(null);
        commentMapper.insert(comment);
        return comment;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        User currentUser = UserHolder.getCurrentUser();
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "请先登录");
        }
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "评论不存在");
        }
        // 判断权限：评论作者本人 或 管理员
        boolean isOwner = comment.getUserId().equals(currentUser.getId());
        boolean isAdmin = "ADMIN".equals(currentUser.getRole());
        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除此评论");
        }
        commentMapper.deleteById(id);
    }
}
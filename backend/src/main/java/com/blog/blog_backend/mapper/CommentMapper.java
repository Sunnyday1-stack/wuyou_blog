package com.blog.blog_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.blog_backend.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
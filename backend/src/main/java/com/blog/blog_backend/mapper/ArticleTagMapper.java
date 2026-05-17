package com.blog.blog_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.blog_backend.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {
}
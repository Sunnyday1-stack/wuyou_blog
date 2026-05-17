package com.blog.blog_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.blog_backend.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    @Select("SELECT * FROM article WHERE is_featured = true ORDER BY create_time DESC")
    List<Article> selectFeaturedArticles();
}
package com.blog.blog_backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("article_tag")
public class ArticleTag {
    private Integer articleId;
    private Integer tagId;

    // getters and setters
    public Integer getArticleId() { return articleId; }
    public void setArticleId(Integer articleId) { this.articleId = articleId; }
    public Integer getTagId() { return tagId; }
    public void setTagId(Integer tagId) { this.tagId = tagId; }
}
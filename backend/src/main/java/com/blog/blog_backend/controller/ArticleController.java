package com.blog.blog_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.blog_backend.entity.Article;
import com.blog.blog_backend.entity.ArticleTag;
import com.blog.blog_backend.entity.Category;
import com.blog.blog_backend.entity.Tag;
import com.blog.blog_backend.mapper.ArticleMapper;
import com.blog.blog_backend.mapper.ArticleTagMapper;
import com.blog.blog_backend.mapper.CategoryMapper;
import com.blog.blog_backend.mapper.TagMapper;
import com.blog.blog_backend.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private CategoryMapper categoryMapper;   // 新增

    // 公开接口：获取文章列表（支持分类筛选、标签筛选）
    @GetMapping
    public List<Article> listArticles(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer tagId) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }
        if (tagId != null) {
            LambdaQueryWrapper<ArticleTag> tagWrapper = new LambdaQueryWrapper<>();
            tagWrapper.eq(ArticleTag::getTagId, tagId);
            List<Integer> articleIds = articleTagMapper.selectList(tagWrapper)
                    .stream()
                    .map(ArticleTag::getArticleId)
                    .collect(Collectors.toList());
            if (articleIds.isEmpty()) {
                return List.of();
            }
            wrapper.in(Article::getId, articleIds);
        }
        wrapper.orderByDesc(Article::getCreateTime);
        return articleMapper.selectList(wrapper);
    }

    // 公开接口：获取单篇文章
    @GetMapping("/{id}")
    public Article getArticleById(@PathVariable Integer id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "文章不存在");
        }
        return article;
    }

    // 管理接口：创建文章（仅管理员）
    @PostMapping
    public Article createArticle(@RequestBody Article article) {
        checkAdmin();
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        if (article.getViewCount() == null) {
            article.setViewCount(0);
        }
        if (article.getIsFeatured() == null) {
            article.setIsFeatured(false);
        }
        // 验证 category_id 是否有效，无效则设为 null
        if (article.getCategoryId() != null) {
            LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Category::getId, article.getCategoryId());
            if (categoryMapper.selectCount(wrapper) == 0) {
                article.setCategoryId(null);
            }
        }
        articleMapper.insert(article);
        return article;
    }

    // 管理接口：更新文章（仅管理员）
    @PutMapping("/{id}")
    public Article updateArticle(@PathVariable Integer id, @RequestBody Article article) {
        checkAdmin();
        article.setId(id);
        article.setUpdateTime(LocalDateTime.now());
        // 同样验证 category_id
        if (article.getCategoryId() != null) {
            LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Category::getId, article.getCategoryId());
            if (categoryMapper.selectCount(wrapper) == 0) {
                article.setCategoryId(null);
            }
        }
        articleMapper.updateById(article);
        return article;
    }

    // 管理接口：删除文章（仅管理员）
    @DeleteMapping("/{id}")
    public void deleteArticle(@PathVariable Integer id) {
        checkAdmin();
        articleMapper.deleteById(id);
    }

    // 获取某篇文章的标签列表（公开）
    @GetMapping("/{id}/tags")
    public List<Tag> getArticleTags(@PathVariable Integer id) {
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getArticleId, id);
        List<ArticleTag> relations = articleTagMapper.selectList(wrapper);
        if (relations.isEmpty()) return List.of();
        List<Integer> tagIds = relations.stream()
                .map(ArticleTag::getTagId)
                .collect(Collectors.toList());
        return tagMapper.selectBatchIds(tagIds);
    }

    // 更新文章的标签（管理员接口）
    @PutMapping("/{id}/tags")
    public void updateArticleTags(@PathVariable Integer id, @RequestBody List<Integer> tagIds) {
        checkAdmin();
        LambdaQueryWrapper<ArticleTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleTag::getArticleId, id);
        articleTagMapper.delete(wrapper);
        for (Integer tagId : tagIds) {
            ArticleTag relation = new ArticleTag();
            relation.setArticleId(id);
            relation.setTagId(tagId);
            articleTagMapper.insert(relation);
        }
    }

    // 全局搜索（根据关键词搜索文章标题或内容，公开）
    @GetMapping("/search")
    public List<Article> searchArticles(@RequestParam String keyword) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Article::getTitle, keyword)
               .or()
               .like(Article::getContent, keyword);
        wrapper.orderByDesc(Article::getCreateTime);
        return articleMapper.selectList(wrapper);
    }

    // 增加阅读量（公开）
    @PutMapping("/{id}/view")
    public void incrementViewCount(@PathVariable Integer id) {
        Article article = articleMapper.selectById(id);
        if (article != null) {
            article.setViewCount(article.getViewCount() + 1);
            articleMapper.updateById(article);
        }
    }

    private void checkAdmin() {
        var currentUser = UserHolder.getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权限，需要管理员身份");
        }
    }
}
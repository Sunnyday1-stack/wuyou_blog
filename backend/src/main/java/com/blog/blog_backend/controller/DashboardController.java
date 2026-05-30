package com.blog.blog_backend.controller;

import com.blog.blog_backend.entity.Article;
import com.blog.blog_backend.entity.Category;
import com.blog.blog_backend.mapper.ArticleMapper;
import com.blog.blog_backend.mapper.CategoryMapper;
import com.blog.blog_backend.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping("/total-views")
    public Map<String, Object> getTotalViews() {
        checkAdmin();
        List<Article> articles = articleMapper.selectList(null);
        int totalViews = articles.stream().mapToInt(Article::getViewCount).sum();
        Map<String, Object> result = new HashMap<>();
        result.put("totalViews", totalViews);
        result.put("articleCount", articles.size());
        return result;
    }

    @GetMapping("/views")
    public Map<String, Object> getViewsStats() {
        checkAdmin();
        List<Article> articles = articleMapper.selectList(null);

        List<Map<String, Object>> ranking = articles.stream()
                .sorted((a, b) -> b.getViewCount().compareTo(a.getViewCount()))
                .map(article -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", article.getId());
                    item.put("title", article.getTitle());
                    item.put("viewCount", article.getViewCount());
                    return item;
                })
                .collect(Collectors.toList());

        int totalViews = articles.stream().mapToInt(Article::getViewCount).sum();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalViews", totalViews);
        result.put("articleCount", articles.size());
        result.put("ranking", ranking);
        return result;
    }

    @GetMapping("/category-stats")
    public Map<String, Object> getCategoryStats() {
        checkAdmin();
        List<Article> articles = articleMapper.selectList(null);
        List<Category> categories = categoryMapper.selectList(null);

        Map<Integer, String> categoryNameMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        Map<String, Integer> countMap = new LinkedHashMap<>();
        for (Article article : articles) {
            String catName = categoryNameMap.getOrDefault(article.getCategoryId(), "未分类");
            countMap.merge(catName, 1, Integer::sum);
        }

        List<Map<String, Object>> list = countMap.entrySet().stream()
                .map(e -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("name", e.getKey());
                    item.put("value", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("stats", list);
        return result;
    }

    private void checkAdmin() {
        var currentUser = UserHolder.getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权限，需要管理员身份");
        }
    }
}
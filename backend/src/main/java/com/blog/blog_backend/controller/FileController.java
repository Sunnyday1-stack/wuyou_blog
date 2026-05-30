package com.blog.blog_backend.controller;

import com.blog.blog_backend.utils.UserHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileController {

    // 图片保存的文件夹（你可以改成其他盘符，比如 D:/blog-images/）
    private final String UPLOAD_DIR = "E:/my_blog/backend/uploads/";

    @PostMapping("/image")
    public Map<String, Object> uploadImage(@RequestParam("file") MultipartFile file) {
        // 1. 只有管理员可以上传
        var currentUser = UserHolder.getCurrentUser();
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权限");
        }

        // 2. 检查是不是图片
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "只能上传图片文件");
        }

        // 3. 限制大小（5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "图片不能超过5MB");
        }

        // 4. 生成唯一文件名（避免重名覆盖）
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + ext;

        // 5. 确保文件夹存在
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "无法创建上传目录");
            }
        }

        // 6. 保存文件到硬盘
        try {
            file.transferTo(new File(UPLOAD_DIR + filename));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "文件保存失败");
        }

        // 7. 返回图片的访问URL
        String imageUrl = "http://localhost:8080/uploads/" + filename;
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("url", imageUrl);
        return result;
    }
}
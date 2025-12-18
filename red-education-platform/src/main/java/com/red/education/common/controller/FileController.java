package com.red.education.common.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.red.education.common.result.Result;
import com.red.education.common.service.SystemConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 文件上传Controller
 */
@Slf4j
@Api(tags = "文件管理")
@RestController
@RequestMapping("/files")
public class FileController {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 获取文档最大上传大小（字节）
     */
    private long getDocMaxSize() {
        String value = systemConfigService.getConfigValue("upload.doc_max_size", "50");
        return Long.parseLong(value) * 1024 * 1024; // MB转字节
    }

    /**
     * 获取视频最大上传大小（字节）
     */
    private long getVideoMaxSize() {
        String value = systemConfigService.getConfigValue("upload.video_max_size", "500");
        return Long.parseLong(value) * 1024 * 1024; // MB转字节
    }

    /**
     * 获取图片最大上传大小（字节）
     */
    private long getImageMaxSize() {
        String value = systemConfigService.getConfigValue("upload.image_max_size", "10");
        return Long.parseLong(value) * 1024 * 1024; // MB转字节
    }

    @ApiOperation("上传文件（需要登录）")
    @PostMapping("/upload")
    public Result<String> uploadFile(Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        // 获取当前登录用户ID
        Long userId = (Long) authentication.getPrincipal();

        if (file.isEmpty()) {
            return Result.fail("文件不能为空");
        }

        try {
            // 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = FileUtil.extName(originalFilename);

            // 验证文件类型和大小
            String fileType = getFileType(extension);
            if (fileType == null) {
                return Result.fail("不支持的文件格式");
            }

            // 检查文件大小（从系统配置读取）
            long fileSize = file.getSize();
            if ("document".equals(fileType) && fileSize > getDocMaxSize()) {
                String maxSizeMB = systemConfigService.getConfigValue("upload.doc_max_size", "50");
                return Result.fail("文档文件大小不能超过" + maxSizeMB + "MB");
            }
            if ("video".equals(fileType) && fileSize > getVideoMaxSize()) {
                String maxSizeMB = systemConfigService.getConfigValue("upload.video_max_size", "500");
                return Result.fail("视频文件大小不能超过" + maxSizeMB + "MB");
            }
            if ("image".equals(fileType) && fileSize > getImageMaxSize()) {
                String maxSizeMB = systemConfigService.getConfigValue("upload.image_max_size", "10");
                return Result.fail("图片文件大小不能超过" + maxSizeMB + "MB");
            }

            // 按日期创建目录：/upload-path/2025/11/28/
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String dirPath = uploadPath + File.separator + datePath;
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名：UUID + 扩展名
            String filename = IdUtil.simpleUUID() + "." + extension;
            String filePath = dirPath + File.separator + filename;

            // 保存文件
            file.transferTo(new File(filePath));

            // 返回访问URL：/files/2025/11/28/xxx.jpg
            String url = "/files/" + datePath + "/" + filename;

            log.info("文件上传成功：用户ID={}, URL={}", userId, url);
            return Result.success("上传成功", url);

        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.fail("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 根据扩展名判断文件类型
     */
    private String getFileType(String extension) {
        extension = extension.toLowerCase();

        // 文档类型
        if (extension.matches("pdf|doc|docx|txt")) {
            return "document";
        }

        // 图片类型
        if (extension.matches("jpg|jpeg|png|gif|bmp|webp")) {
            return "image";
        }

        // 视频类型
        if (extension.matches("mp4|avi|mov|wmv|flv|mkv|ts")) {
            return "video";
        }

        return null;
    }
}

package com.red.education.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * 文件清理服务
 * 用于删除磁盘上的文件
 */
@Slf4j
@Service
public class FileCleanupService {

    @Value("${file.upload-path}")
    private String uploadPath;

    /**
     * 删除文件
     * 
     * @param fileUrl 文件的URL路径（如 /files/2025/12/17/xxx.mp4）
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return true;
        }

        try {
            // 将URL路径转换为本地文件路径
            // fileUrl 格式: /files/2025/12/17/xxx.mp4
            // 需要转换为: D:/upload/red-education/2025/12/17/xxx.mp4
            String relativePath = fileUrl;
            if (relativePath.startsWith("/files/")) {
                relativePath = relativePath.substring(7); // 去掉 "/files/"
            } else if (relativePath.startsWith("/files")) {
                relativePath = relativePath.substring(6); // 去掉 "/files"
            }

            String filePath = uploadPath + File.separator + relativePath;
            // 处理路径分隔符
            filePath = filePath.replace("/", File.separator).replace("\\", File.separator);

            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    log.info("文件删除成功: {}", filePath);
                } else {
                    log.warn("文件删除失败: {}", filePath);
                }
                return deleted;
            } else {
                log.info("文件不存在，跳过删除: {}", filePath);
                return true;
            }
        } catch (Exception e) {
            log.error("删除文件异常: {}", fileUrl, e);
            return false;
        }
    }

    /**
     * 批量删除文件
     * 
     * @param fileUrls 文件URL数组
     */
    public void deleteFiles(String... fileUrls) {
        for (String url : fileUrls) {
            deleteFile(url);
        }
    }
}

package com.jinsi.housedemo.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 文件上传工具类 —— 负责图片保存到本地磁盘，返回访问 URL
 * <p>
 * 用法：
 * <pre>
 *   String url = FileUploadUtil.save(file, "D:/nginx/html/", "house/housePic/");
 *   // 返回 "/house/housePic/a1b2c3.jpg"
 * </pre>
 * </p>
 */
public class FileUploadUtil {

    /** 允许的图片扩展名（白名单，防止上传恶意文件） */
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"
    ));

    /** 单文件最大 10MB */
    private static final long MAX_SIZE = 10 * 1024 * 1024;

    /**
     * 保存上传文件到指定目录，返回相对路径
     *
     * @param file     前端传来的 MultipartFile
     * @param rootPath 根目录（如 D:/nginx-1.28.1/html/）
     * @param subPath  子目录（如 house/housePic/）
     * @return 文件访问相对路径（如 /house/housePic/uuid.jpg）
     * @throws IOException              磁盘写入失败
     * @throws IllegalArgumentException 文件类型不允许或文件过大
     */
    public static String save(MultipartFile file, String rootPath, String subPath) throws IOException {
        // 1. 空文件检查
        if (file == null || file.isEmpty()) {
            throw new MyException(ErrorType.ERROR, "请选择要上传的文件");
        }

        // 2. 大小检查
        if (file.getSize() > MAX_SIZE) {
            throw new MyException(ErrorType.ERROR, "文件不能超过 10MB");
        }

        // 3. 扩展名校验（白名单）
        String originalName = file.getOriginalFilename();
        String ext = ".jpg"; // 默认
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
        }
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new MyException(ErrorType.ERROR, "不支持的文件类型，仅允许 jpg/png/gif/bmp/webp");
        }

        // 4. 生成唯一文件名（UUID + 原扩展名）
        String fileName = UUID.randomUUID().toString().replace("-", "") + ext;

        // 5. 确保目录存在
        File dir = new File(rootPath, subPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 6. 写入磁盘
        file.transferTo(new File(dir, fileName));
        return "/" + subPath + fileName;
    }
}
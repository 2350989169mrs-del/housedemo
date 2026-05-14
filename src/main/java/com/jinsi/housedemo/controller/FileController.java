package com.jinsi.housedemo.controller;

import com.jinsi.housedemo.util.ResultData;
import com.jinsi.housedemo.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件上传 Controller
 */
@RestController
@RequestMapping("/api/upload")
public class FileController {

    @Value("${upload.root-path}")
    private String rootPath;

    @Value("${upload.admin-head-path}")
    private String adminHeadPath;

    @Value("${upload.book-pic-path}")
    private String bookPicPath;

    /** 上传头像 */
    @PostMapping("/avatar")
    public ResultData uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        String url = FileUploadUtil.save(file, rootPath, adminHeadPath);
        return new ResultData("data", url, "上传成功");
    }

    /** 上传房源图片 */
    @PostMapping("/house-image")
    public ResultData uploadHouseImage(@RequestParam("file") MultipartFile file) throws IOException {
        String url = FileUploadUtil.save(file, rootPath, bookPicPath);
        return new ResultData("data", url, "上传成功");
    }
}
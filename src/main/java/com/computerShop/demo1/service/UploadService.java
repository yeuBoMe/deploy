package com.computerShop.demo1.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class UploadService {

    public String handleUploadUserAvatar(MultipartFile multipartFile) {
        if (multipartFile.isEmpty() || multipartFile.getSize() == 0) {
            return "";
        }

        String uploadDir = "uploads/images/avatar";
        String fileName = "";
        try {
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            fileName = System.currentTimeMillis() + "-" + multipartFile.getOriginalFilename();
            File serverFile = new File(uploadFolder, fileName);

            byte[] bytes = multipartFile.getBytes();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(serverFile));
            bos.write(bytes);
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }

    public String handleUploadProductImage(MultipartFile multipartFile) {
        if (multipartFile.isEmpty() || multipartFile.getSize() == 0) {
            return "";
        }

        String uploadDir = "uploads/images/product";
        String fileName = "";
        try {
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            fileName = System.currentTimeMillis() + "-" + multipartFile.getOriginalFilename();
            File serverFile = new File(uploadFolder, fileName);

            byte[] bytes = multipartFile.getBytes();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(serverFile));
            bos.write(bytes);
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }

}



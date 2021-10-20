package com.fangjun.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.fangjun.xmltojava.XmlMetadata;
import com.fangjun.xmltojava.XmlToJavaFile;
import com.fangjun.xmltojava.XmlToJavaReq;

import lombok.extern.slf4j.Slf4j;

/**
 * TODO
 * @author: fangjun
 * @date: 2021/09/30
 */
@Controller
@Slf4j
public class FileController {
    @SuppressWarnings("rawtypes")
    @PostMapping(value = "/xmlToJavaFile")
    public ResponseEntity getFile(@RequestBody XmlToJavaReq requestBody) throws FileNotFoundException {
        System.out.println("requestBody:"+requestBody);
        log.info("requestBody-xml:"+requestBody.getXml());
        if(StringUtils.isBlank(requestBody.getClassName())) {
            return ResponseEntity.badRequest().body("类名不能为空");
        }
        if(StringUtils.isBlank(requestBody.getXml())) {
            return ResponseEntity.badRequest().body("xml不能为空");
        }
        XmlToJavaFile bean = new XmlToJavaFile(requestBody.getClassName(), requestBody.getPackageName());
        Document doc;
        try {
            doc = DocumentHelper.parseText(requestBody.getXml());
        } catch (DocumentException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("xml格式异常");
        }
        String generateJavaContent;
        try {
            XmlMetadata xmlMetadata = XmlToJavaFile.getXmlMetadata(doc);
            generateJavaContent = bean.generateJavaContent(xmlMetadata);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("生成异常");
        }
        log.info("requestBody-generateJavaContent:"+generateJavaContent);
        if (StringUtils.isNotEmpty(generateJavaContent)) {
            if(StringUtils.isEmpty(requestBody.getDownload())) {
                return ResponseEntity.ok(generateJavaContent);
            }
            return export(generateJavaContent);
        }
        return ResponseEntity.badRequest().body("生成失败");
    }
     
     
    public ResponseEntity<byte[]> export(String str) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity.ok().headers(headers).contentLength(str.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(str.getBytes(StandardCharsets.UTF_8));
    }
    public ResponseEntity<FileSystemResource> export2(File file) {
        if (file == null) {
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment; filename=" + file.getName());
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity.ok().headers(headers).contentLength(file.length()).contentType(MediaType.parseMediaType("application/octet-stream")).body(new FileSystemResource(file));
    }
     
}

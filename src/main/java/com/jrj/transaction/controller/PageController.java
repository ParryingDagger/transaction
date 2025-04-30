package com.jrj.transaction.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 更习惯前后分离的方式开发
 * 这里先简单返回一个 html 作为 page
 */
@Controller
public class PageController {
    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<String> getIndexPage() throws IOException {
        Resource resource = new ClassPathResource("static/index.html");
        String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        
        return ResponseEntity.ok()
            .contentType(MediaType.TEXT_HTML)
            .body(content);
    }
}

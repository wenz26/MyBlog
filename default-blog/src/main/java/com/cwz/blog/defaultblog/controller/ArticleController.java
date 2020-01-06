package com.cwz.blog.defaultblog.controller;

import com.cwz.blog.defaultblog.entity.Article;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleController {

    @PostMapping("/getArticle")
    public Article getArticle(Article article){
        return article;
    }
}

package com.cwz.blog.defaultblog.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author: 陈文振
 * @date: 2019/12/2
 * @description: 文章
 */
@Table(name = "article")
public class Article {

    /**
     * 文章id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 文章作者
     */
    // private String author;

    /**
     * 文章原作者
     */
    private String originalAuthor;

    /**
     * 文章标题
     */
    private String articleTitle;

    /**
     * 文章内容
     */
    private String articleContent;

    /**
     * 文章类型（转载或原创）
     */
    private String articleType;

    /**
     * 博客分类
     */
    private Integer articleCategories;

    /**
     * 发布时间
     */
    private LocalDateTime publishDate;

    /**
     * 最后一次修改时间
     */
    private LocalDateTime updateDate;

    /**
     * 原文链接
     * 转载：则是转载的链接
     * 原创：则是在本博客中的链接
     */
    private String articleUrl;

    /**
     * 文章图片url
     */
    private String imageUrl;

    /**
     * 文章摘要
     */
    private String articleTabloid;

    /**
     * 文章喜欢数
     */
    private Integer likes = 0;

    /**
     * 文章收藏数
     */
    private Integer favorites = 0;

    /**
     * 上一篇文章id
     */
    private Integer lastArticleId = 0;

    /**
     * 下一篇文章id
     */
    private Integer nextArticleId = 0;

    /**
     * 该文章为草稿还是已发布（1为已发布，0为草稿）
     */
    private Integer draft;

    /**
     * 文章对应的标签名称
     */
    private String tagName;

    /**
     * 文章对应的标签类
     */
    private List<Tags> tags;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /*public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }*/

    public String getOriginalAuthor() {
        return originalAuthor;
    }

    public void setOriginalAuthor(String originalAuthor) {
        this.originalAuthor = originalAuthor;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleContent() {
        return articleContent;
    }

    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent;
    }

    public String getArticleType() {
        return articleType;
    }

    public void setArticleType(String articleType) {
        this.articleType = articleType;
    }

    public Integer getArticleCategories() {
        return articleCategories;
    }

    public void setArticleCategories(Integer articleCategories) {
        this.articleCategories = articleCategories;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getArticleTabloid() {
        return articleTabloid;
    }

    public void setArticleTabloid(String articleTabloid) {
        this.articleTabloid = articleTabloid;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getFavorites() {
        return favorites;
    }

    public void setFavorites(Integer favorites) {
        this.favorites = favorites;
    }

    public Integer getLastArticleId() {
        return lastArticleId;
    }

    public void setLastArticleId(Integer lastArticleId) {
        this.lastArticleId = lastArticleId;
    }

    public Integer getNextArticleId() {
        return nextArticleId;
    }

    public void setNextArticleId(Integer nextArticleId) {
        this.nextArticleId = nextArticleId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public Integer getDraft() {
        return draft;
    }

    public void setDraft(Integer draft) {
        this.draft = draft;
    }

    public List<Tags> getTags() {
        return tags;
    }

    public void setTags(List<Tags> tags) {
        this.tags = tags;
    }
}

package com.cwz.blog.defaultblog.mapper;

import com.cwz.blog.defaultblog.entity.Article;
import com.cwz.blog.defaultblog.mapper.bean.BeanMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper extends BeanMapper<Article> {

}

package com.cwz.blog.defaultblog.mapper;

import com.cwz.blog.defaultblog.entity.Tags;
import com.cwz.blog.defaultblog.mapper.bean.BeanMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TagsMapper extends BeanMapper<Tags> {

    @Select("select COUNT(*) from article_tags where tag_id = #{tagId}")
    int tagArticleCountNum(@Param("tagId") int tagId);

    @Select("select IFNULL((select id from tags where tag_name = #{tagName}), 0)")
    int findIsExistByTagName(@Param("tagName") String tagName);

    @Select("select * from tags order by create_date desc")
    List<Tags> selectAllTags();

    @Select("select COUNT(id) from tags")
    int countAllTags();

    @Select("select * from tags where tag_name like '%${keyWords}%' order by create_date desc")
    List<Tags> searchByKeyWordsByTagName(@Param("keyWords") String keyWords);

    @Select("select COUNT(id) from tags where tag_name like '%${keyWords}%'")
    int countSearchByKeyWordsByTagName(@Param("keyWords") String keyWords);

    List<Tags> selectAllTagsToXML(@Param("tagSearch") String tagSearch,
                                  @Param("firstDate") String firstDate, @Param("lastDate")String lastDate);

    int countAllTagsToXML(@Param("tagSearch") String tagSearch,
                          @Param("firstDate") String firstDate, @Param("lastDate") String lastDate);
}

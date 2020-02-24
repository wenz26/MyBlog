package com.cwz.blog.defaultblog.mapper;

import com.cwz.blog.defaultblog.entity.FeedBack;
import com.cwz.blog.defaultblog.mapper.bean.BeanMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FeedBackMapper extends BeanMapper<FeedBack> {

    @Select("select COUNT(id) from feedback")
    int countAllFeedBack();

    @Select("select COUNT(id) from feedback where is_read = 1")
    int countAllFeedBackNotRead();

    @Update("update feedback set is_read = 0 where id = #{id}")
    int readOneFeedBackRecord(@Param("id") int id);

    @Update("update feedback set is_read = 0")
    int readAllFeedBack();

    List<FeedBack> selectAllFeedBackToXML(@Param("isRead") Integer isRead, @Param("firstDate") String firstDate,
                                          @Param("lastDate") String lastDate);

    int countAllFeedBackToXML(@Param("isRead") Integer isRead, @Param("firstDate") String firstDate,
                              @Param("lastDate") String lastDate);

}

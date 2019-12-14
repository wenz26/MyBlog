package com.cwz.blog.defaultblog.mapper;

import com.cwz.blog.defaultblog.entity.Archives;
import com.cwz.blog.defaultblog.mapper.bean.BeanMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArchivesMapper extends BeanMapper<Archives> {

    /**
     * @description: 查找归档日期有没有重复
     * @author: 陈文振
     * @date: 2019/12/6
     * @return:
     */
    @Select("select IFNULL(max(id),0) from archives where archive_name = #{archiveName}")
    int findArchiveNameByArchiveName(@Param("archiveName") String archiveName);

}

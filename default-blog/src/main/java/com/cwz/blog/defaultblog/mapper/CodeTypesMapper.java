package com.cwz.blog.defaultblog.mapper;

import com.cwz.blog.defaultblog.entity.CodeTypes;
import com.cwz.blog.defaultblog.mapper.bean.BeanMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CodeTypesMapper extends BeanMapper<CodeTypes> {

    @Select("select * from code_types")
    List<CodeTypes> selectAllCodeTypes();

    @Select("select * from code_types where code like '%${code}%'")
    List<CodeTypes> selectSomeCodeTypes(@Param("code") String code);
}

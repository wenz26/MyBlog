package com.cwz.blog.defaultblog.mapper;

import com.cwz.blog.defaultblog.entity.Categories;
import com.cwz.blog.defaultblog.mapper.bean.BeanMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CategoriesMapper extends BeanMapper<Categories> {

    @Select("select IFNULL((select id from categories where category_name = #{categoryName}), 0)")
    int findIsExistByCategoryName(@Param("categoryName") String categoryName);
}

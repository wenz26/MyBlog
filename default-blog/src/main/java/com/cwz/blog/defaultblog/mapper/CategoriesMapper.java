package com.cwz.blog.defaultblog.mapper;

import com.cwz.blog.defaultblog.entity.Categories;
import com.cwz.blog.defaultblog.mapper.bean.BeanMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoriesMapper extends BeanMapper<Categories> {

    @Select("select IFNULL((select id from categories where category_name = #{categoryName}), 0)")
    int findIsExistByCategoryName(@Param("categoryName") String categoryName);

    @Select("select * from categories order by create_date desc")
    List<Categories> selectAllCategories();

    @Select("select * from categories where category_name like '%${keyWords}%' order by create_date desc")
    List<Categories> searchByKeyWordsByCategoryName(@Param("keyWords") String keyWords);

    @Select("select COUNT(id) from categories where category_name like '%${keyWords}%'")
    int countSearchByKeyWordsByCategoryName(@Param("keyWords") String keyWords);

    List<Categories> selectAllCategoriesToXML(@Param("categorySearch") String categorySearch,
                                              @Param("firstDate") String firstDate, @Param("lastDate")String lastDate);

    int countAllCategoriesToXML(@Param("categorySearch") String categorySearch,
                                @Param("firstDate") String firstDate, @Param("lastDate") String lastDate);
}

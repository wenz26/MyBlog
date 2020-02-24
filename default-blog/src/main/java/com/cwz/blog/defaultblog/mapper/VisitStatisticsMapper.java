package com.cwz.blog.defaultblog.mapper;

import com.cwz.blog.defaultblog.entity.VisitStatistics;
import com.cwz.blog.defaultblog.mapper.bean.BeanMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface VisitStatisticsMapper extends BeanMapper<VisitStatistics> {

    @Update("update visit_statistics set visit_num = #{visitNum} where statistics_name = #{statisticsName}")
    void updateVisitNumByStatisticsName(@Param("statisticsName") String statisticsName, @Param("visitNum") long visitNum);

    @Delete("delete from visit_statistics where statistics_name = #{statisticsName}")
    void deleteVisitByStatisticsName(@Param("statisticsName") String statisticsName);
}

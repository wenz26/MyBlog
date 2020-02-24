package com.cwz.blog.defaultblog.mapper;

import com.cwz.blog.defaultblog.entity.UserLog;
import com.cwz.blog.defaultblog.mapper.bean.BeanMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserLogMapper extends BeanMapper<UserLog> {

    List<UserLog> findAllUserLogByExample(@Param("userLog") UserLog userLog, @Param("firstDate") String firstDate,
                                          @Param("lastDate") String lastDate);

    List<UserLog> findAllUserLogByLogin(@Param("userLog") UserLog userLog, @Param("firstDate") String firstDate,
                                        @Param("lastDate") String lastDate);

    List<UserLog> findAllUserLogByOperation(@Param("userLog") UserLog userLog, @Param("firstDate") String firstDate,
                                            @Param("lastDate") String lastDate);

    @Select("select * from user_log where id = #{id}")
    UserLog getUserLogOneById(@Param("id") int id);
}

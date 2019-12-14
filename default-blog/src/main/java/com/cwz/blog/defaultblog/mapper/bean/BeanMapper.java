package com.cwz.blog.defaultblog.mapper.bean;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface BeanMapper<T> extends Mapper<T>, MySqlMapper<T> {
}

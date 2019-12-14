package com.cwz.blog.defaultblog.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author: 陈文振
 * @date: 2019/12/3
 * @description: Druid 数据库连接池配置
 */
@Configuration
@ConditionalOnClass(com.alibaba.druid.pool.DruidDataSource.class)
/* name 中的值与 havingValue 比较，如果相同就返回true */
@ConditionalOnProperty(name = "spring.datasource.type", havingValue = "com.alibaba.druid.pool.DruidDataSource", matchIfMissing = true)
public class DruidDataSourceConfig {

    private Logger logger = LoggerFactory.getLogger(DruidDataSourceConfig.class);

    @Bean(name = "druidDataSource")
    @Primary
    public DataSource dataSource(@Autowired Environment environment){
        logger.info("正在初始化 Druid 数据连接池");
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(environment.getProperty("spring.datasource.url"));
        dataSource.setUsername(environment.getProperty("spring.datasource.data-username"));
        dataSource.setPassword(environment.getProperty("spring.datasource.password"));
        dataSource.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name"));

        dataSource.setInitialSize(Integer.parseInt(environment.getProperty("spring.datasource.druid.initial-size")));
        dataSource.setMinIdle(Integer.parseInt(environment.getProperty("spring.datasource.druid.min-idle")));
        dataSource.setMaxActive(Integer.parseInt(environment.getProperty("spring.datasource.druid.max-active")));
        // 配置获取连接等待超时的时间
        dataSource.setMaxWait(Long.parseLong(environment.getProperty("spring.datasource.druid.max-wait")));
        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        dataSource.setTimeBetweenEvictionRunsMillis(Long.parseLong(environment.getProperty("spring.datasource.druid.time-between-eviction-runs-millis")));
        // 配置一个连接在池中最小生存的时间Mi，单位是毫秒
        dataSource.setMinEvictableIdleTimeMillis(Long.parseLong(environment.getProperty("spring.datasource.druid.min-evictable-idle-time-millis")));
        dataSource.setValidationQuery(environment.getProperty("spring.datasource.druid.validation-query"));
        dataSource.setTestWhileIdle(Boolean.parseBoolean(environment.getProperty("spring.datasource.druid.test-while-idle")));
        dataSource.setTestOnBorrow(Boolean.parseBoolean(environment.getProperty("spring.datasource.druid.test-on-borrow")));
        dataSource.setTestOnReturn(Boolean.parseBoolean(environment.getProperty("spring.datasource.druid.test-on-return")));
        dataSource.setPoolPreparedStatements(Boolean.parseBoolean(environment.getProperty("spring.datasource.druid.pool-prepared-statements")));
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(Integer.parseInt(environment.getProperty("spring.datasource.druid.max-pool-prepared-statement-per-connection-size")));
        try {
            dataSource.setFilters(environment.getProperty("spring.datasource.druid.filters"));
        } catch (SQLException e) {
            logger.error("druid 配置初始化过滤器出错："  + e);
        }
        return dataSource;
    }

    /**
     * @description: 配置Druid监控的StatViewServlet和WebStatFilter
     *      1.配置一个管理后台的Servlet
     * @author: 陈文振
     * @date: 2019/12/3
     */
    @Bean
    public ServletRegistrationBean statViewServlet(){
        logger.info("正在初始化 Druid Servlet 配置");

        // 新建一个 Servlet
        ServletRegistrationBean bean = new ServletRegistrationBean();
        bean.setServlet(new StatViewServlet());
        bean.addUrlMappings("/druid/*");

        Map<String, String> initParams = new HashMap<>();
        initParams.put("loginUsername", "root"); // 设置druid登录账号
        initParams.put("loginPassword", "294348"); //设置druid登录密码
        initParams.put("resetEnable", "true"); // 开启后 点击重置按钮，可使计数器清零重新计数
        // 下面是黑白名单，多个ip地址之间用逗号隔开
        initParams.put("allow", ""); // 白名单ip，默认就是允许所有访问
        //params.put("deny", ""); // 黑名单ip
        bean.setInitParameters(initParams);

        return bean;
    }

    /**
     * @description: 2、配置一个web监控的filter
     * @author: 陈文振
     * @date: 2019/12/3
     */
    @Bean
    public FilterRegistrationBean webStatFilter(){
        logger.info("正在初始化 Druid Filter 配置");
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new WebStatFilter());
        bean.addUrlPatterns("/*");

        Map<String, String> initParams = new HashMap<>();
        initParams.put("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        bean.setInitParameters(initParams);

        return bean;
    }
}

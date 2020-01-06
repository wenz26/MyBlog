package com.cwz.blog.defaultblog.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author: 陈文振
 * @date: 2019/12/2
 * @description: 访问量统计表
 */
@Table(name = "visit_statistics")
public class VisitStatistics {

    /**
     * 访问量id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 访问量统计数
     */
    private Long visitNum;

    /**
     * 具体需要统计东西的名称（包括文章访问量、用户数、总访问量等等）
     */
    private String statisticsName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getVisitNum() {
        return visitNum;
    }

    public void setVisitNum(Long visitNum) {
        this.visitNum = visitNum;
    }

    public String getStatisticsName() {
        return statisticsName;
    }

    public void setStatisticsName(String statisticsName) {
        this.statisticsName = statisticsName;
    }
}

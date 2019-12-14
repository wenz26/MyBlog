package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.Archives;
import com.cwz.blog.defaultblog.mapper.ArchivesMapper;
import com.cwz.blog.defaultblog.service.ArchivesService;
import com.cwz.blog.defaultblog.service.ArticleService;
import com.cwz.blog.defaultblog.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import java.util.List;

/**
 * @author: 陈文振
 * @date: 2019/12/3
 * @description: 归档业务操作实现类
 */
@Service("archivesService")
public class ArchivesServiceImpl implements ArchivesService {

    @Autowired
    private ArchivesMapper archivesMapper;

    @Autowired
    private ArticleService articleService;

    @Override
    public JSONObject findArchiveNameAndArticleNum() {
        Example archivesExample = new Example(Archives.class);
        archivesExample.orderBy("id").desc();
        List<Archives> archives = archivesMapper.selectByExample(archivesExample);
        TimeUtil timeUtil = new TimeUtil();

        JSONArray archivesJsonArray = new JSONArray();
        JSONObject archiveJson;
        for (Archives archive : archives){
            archiveJson = new JSONObject();
            archiveJson.put("archiveName", archive.getArchiveName());
            // 把 2019年12月转化为 2019-12 (时间中的年转换为横杠)
            String archiveName = timeUtil.timeYearToWhippletree(archive.getArchiveName());
            // 计算该归档日期文章的数目
            archiveJson.put("archiveArticleNum", articleService.countArticleArchiveByArchive(archiveName));
            archivesJsonArray.add(archiveJson);
        }
        JSONObject returnJson = new JSONObject();
        returnJson.put("status", 200);
        returnJson.put("result", archivesJsonArray);
        return returnJson;
    }

    @Override
    public int addArchives(Archives archives) {
        int archiveNameIsExist = archivesMapper.findArchiveNameByArchiveName(archives.getArchiveName());
        if (archiveNameIsExist == 0){
            return archivesMapper.insert(archives);
        }
        return 0;
    }
}

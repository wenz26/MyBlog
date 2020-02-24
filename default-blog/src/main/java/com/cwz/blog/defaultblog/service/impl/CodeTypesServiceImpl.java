package com.cwz.blog.defaultblog.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.entity.CodeTypes;
import com.cwz.blog.defaultblog.mapper.CodeTypesMapper;
import com.cwz.blog.defaultblog.service.CodeTypesService;
import com.cwz.blog.defaultblog.service.common.CommonReturn;
import com.cwz.blog.defaultblog.utils.DataMap;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: 陈文振
 * @date: 2020/2/1
 * @description: 返回状态码类业务操作实现类
 */
@Service("codeTypesService")
public class CodeTypesServiceImpl implements CodeTypesService {

    @Autowired
    private CodeTypesMapper codeTypesMapper;

    @Override
    public DataMap selectSomeCodeTypes(int rows, int pageNum, String code) {
        PageHelper.startPage(pageNum, rows);
        List<CodeTypes> codeTypes;

        if (StringUtils.isBlank(code)) {
            codeTypes = codeTypesMapper.selectAllCodeTypes();
        } else {
            codeTypes = codeTypesMapper.selectSomeCodeTypes(code);
        }
        PageInfo<CodeTypes> pageInfo = new PageInfo<>(codeTypes);

        CommonReturn commonReturn = new CommonReturn();
        JSONArray resultArray = new JSONArray();
        JSONObject resultObject = new JSONObject();

        resultArray.addAll(codeTypes);

        resultObject.put("result", resultArray);
        resultObject.put("pageInfo", commonReturn.jsonObjectToPageInfo(pageInfo));

        return DataMap.success().setData(resultObject);
    }
}

package com.cwz.blog.defaultblog.redis_test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cwz.blog.defaultblog.redis.HashRedisServiceImpl;
import com.cwz.blog.defaultblog.security.code.sms.SmsCode;
import com.cwz.blog.defaultblog.utils.StringUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;


@SpringBootTest
class HashRedisTest {

    @Autowired
    private HashRedisServiceImpl hashRedisService;

    @Test
    void redisTest() {
        LinkedHashMap map = (LinkedHashMap) hashRedisService.getAllFieldAndValue("visit");
        System.out.println(JSON.toJSONString(map));
        String statisticsName;
        for (Object e : map.keySet()) {
            statisticsName = String.valueOf(e);
            System.out.println(statisticsName);
            System.out.println(map.get(statisticsName));
            System.out.println(map.get(statisticsName).getClass());
        }
    }

    @Test
    void codeRedisTest(){
        SmsCode code = (SmsCode) hashRedisService.get(StringUtil.PREFIX_IMAGE_CODE, "2020-01-03T23:45:30.996");
        System.out.println(code);
        System.out.println(code.isExpired());
    }

    @Test
    void testVisit() {
        //LocalDateTime localDateTime = LocalDateTime.now();
        //System.out.println(localDateTime);
        // LocalDateTime localDateTime = LocalDateTime.parse("2020-01-23T00:00:00.000");
        LocalDateTime localDateTime = LocalDateTime.now().plusSeconds(-86400);
        System.out.println(localDateTime);
        String string = localDateTime.getDayOfWeek().toString();
        if (hashRedisService.hasHashKey("visit", string)) {
            hashRedisService.put("visit", string, 3);
        } else {
            hashRedisService.put("visit", string, 1);
        }

        /*LocalDateTime localDateTime1 = LocalDateTime.parse("2020-01-27T20:16:15.095");
        String string1 = localDateTime1.getDayOfWeek().toString();
        if (hashRedisService.hasHashKey("visit", string1)) {
            hashRedisService.put("visit", string1, 2);
        } else {
            hashRedisService.put("visit", string1, 1);
        }

        LocalDateTime localDateTime2 = LocalDateTime.parse("2020-01-28T20:16:15.095");
        String string2 = localDateTime2.getDayOfWeek().toString();
        if (hashRedisService.hasHashKey("visit", string2)) {
            hashRedisService.put("visit", string2, 3);
        } else {
            hashRedisService.put("visit", string2, 1);
        }

        LocalDateTime localDateTime3 = LocalDateTime.parse("2020-01-29T20:16:15.095");
        String string3 = localDateTime3.getDayOfWeek().toString();
        if (hashRedisService.hasHashKey("visit", string3)) {
            hashRedisService.put("visit", string3, 4);
        } else {
            hashRedisService.put("visit", string3, 1);
        }*/
    }

    @Test
    void testHashPaiXu(){
        LinkedHashMap map = (LinkedHashMap) hashRedisService.getAllFieldAndValue("searchHistory_1");

        JSONArray jsonArray = new JSONArray();
        List<String> sort = new ArrayList<>();

        for (Object object : map.keySet()) {
            sort.add((String) object);
        }

        System.out.println(JSON.toJSONString(sort));
        Collections.sort(sort);
        System.out.println(JSON.toJSONString(sort));
        Collections.reverse(sort);
        System.out.println(JSON.toJSONString(sort));


        JSONObject jsonObject;
        int i = 0;
        List<String> check = new ArrayList<>();
        while (jsonArray.size() < 10 && i < sort.size()) {
            String s = sort.get(i);
            String o = (String) hashRedisService.get("searchHistory_1", s);

            if (check.contains(o)) {
                i++;
                continue;
            }

            jsonObject = new JSONObject();
            jsonObject.put("key", s);
            jsonObject.put("value", o);
            jsonArray.add(jsonObject);
            check.add(o);
            i++;

        }

        System.out.println(jsonArray);
        System.out.println(JSON.toJSONString(check));

        /*ListIterator<Object> objectListIterator = jsonArray.listIterator(jsonArray.size());
        int i = 0;
        JSONArray jsonArray1 = new JSONArray();
        JSONObject jsonObject1;
        List<Object> list = new ArrayList<>();
        while (objectListIterator.hasPrevious()){
            if (i == 10){ break;}
            jsonObject1 = new JSONObject();

            JSONObject previous = (JSONObject) objectListIterator.previous();
            jsonObject1.put("key", previous.keySet().toArray()[0]);
            Object o = previous.values().toArray()[0];
            jsonObject1.put("value", o);

            if (list.contains(o)) {
                continue;
            }

            list.add(o);
            jsonArray1.add(jsonObject1);
            i++;
        }

        System.out.println(JSON.toJSONString(list));
        System.out.println(jsonArray1);*/

    }
}

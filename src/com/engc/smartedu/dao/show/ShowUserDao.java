package com.engc.smartedu.dao.show;

import com.engc.smartedu.bean.UserBean;
import com.engc.smartedu.dao.URLHelper;
import com.engc.smartedu.support.error.WeiboException;
import com.engc.smartedu.support.http.HttpMethod;
import com.engc.smartedu.support.http.HttpUtility;
import com.engc.smartedu.support.utils.AppLogger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Jiang Qi
 * Date: 12-8-14
 */
public class ShowUserDao {

    public UserBean getUserInfo() throws WeiboException {
        String url = URLHelper.USER_SHOW;
        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("uid", uid);
        map.put("screen_name", screen_name);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);


        Gson gson = new Gson();

        UserBean value = null;
        try {
            value = gson.fromJson(jsonData, UserBean.class);
        } catch (JsonSyntaxException e) {

            AppLogger.e(e.getMessage());
        }


        return value;

    }

    private String access_token;
    private String uid;
    private String screen_name;

    public ShowUserDao(String access_token) {

        this.access_token = access_token;
    }

    public ShowUserDao setScreen_name(String screen_name) {
        this.screen_name = screen_name;
        return this;
    }

    public ShowUserDao setUid(String uid) {
        this.uid = uid;
        return this;
    }
}

package com.engc.smartedu.dao.destroy;

import com.engc.smartedu.bean.MessageBean;
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
 * User: qii
 * Date: 12-9-10
 */
public class DestroyStatusDao {

    private String access_token;
    private String id;

    public DestroyStatusDao(String access_token, String id) {
        this.access_token = access_token;
        this.id = id;
    }

    public boolean destroy() throws WeiboException {
        String url = URLHelper.STATUSES_DESTROY;
        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("id", id);


        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, url, map);
        Gson gson = new Gson();

        try {
            MessageBean value = gson.fromJson(jsonData, MessageBean.class);
        } catch (JsonSyntaxException e) {
            AppLogger.e(e.getMessage());
            return false;
        }

        return true;


    }
}

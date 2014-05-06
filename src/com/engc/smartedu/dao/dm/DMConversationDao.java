package com.engc.smartedu.dao.dm;

import com.engc.smartedu.bean.DMListBean;
import com.engc.smartedu.dao.URLHelper;
import com.engc.smartedu.support.error.WeiboException;
import com.engc.smartedu.support.http.HttpMethod;
import com.engc.smartedu.support.http.HttpUtility;
import com.engc.smartedu.support.settinghelper.SettingUtility;
import com.engc.smartedu.support.utils.AppLogger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.Map;

/**
 * User: qii
 * Date: 12-11-15
 */
public class DMConversationDao {
    private String access_token;
    private String uid;

    private String page;
    private String count;

    public DMConversationDao(String token) {
        this.access_token = token;
        this.count = SettingUtility.getMsgCount();
    }

    public DMConversationDao setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public DMConversationDao setPage(int page) {
        this.page = String.valueOf(page);
        return this;
    }

    public DMListBean getConversationList() throws WeiboException {
        String url = URLHelper.DM_CONVERSATION;
        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("uid", uid);
        map.put("page", page);
        map.put("count",count);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);
        DMListBean value = null;
        try {
            value = new Gson().fromJson(jsonData, DMListBean.class);
        } catch (JsonSyntaxException e) {

            AppLogger.e(e.getMessage());

        }
        return value;
    }
}

package com.engc.smartedu.dao.destroy;

import com.engc.smartedu.bean.CommentBean;
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
 * Date: 12-9-11
 */
public class DestroyCommentDao {
    private String access_token;
    private String cid;

    public DestroyCommentDao(String access_token, String cid) {
        this.access_token = access_token;
        this.cid = cid;
    }

    public boolean destroy() throws WeiboException {
        String url = URLHelper.COMMENT_DESTROY;
        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("cid", cid);


        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, url, map);
        Gson gson = new Gson();

        try {
            CommentBean value = gson.fromJson(jsonData, CommentBean.class);
        } catch (JsonSyntaxException e) {
            AppLogger.e(e.getMessage());
            return false;
        }

        return true;


    }
}

package com.engc.smartedu.support.http;

import java.util.Map;

import com.engc.eop.DefaultEngcOpenClient;
import com.engc.eop.EopClient;
import com.engc.smartedu.support.error.WeiboException;
import com.engc.smartedu.support.file.FileDownloaderHttpHelper;
import com.engc.smartedu.support.file.FileUploaderHttpHelper;
import com.engc.smartedu.support.utils.Utility;

public class HttpUtility {

	public static EopClient getEopClient() {
		EopClient client = new DefaultEngcOpenClient(Utility
				.getContextProperties().getProperty("EOPURI"), Utility
				.getContextProperties().getProperty("EOPKEY"), Utility
				.getContextProperties().getProperty("EOPSECRET"));
		return client;
	}

	private static HttpUtility httpUtility = new HttpUtility();

	private HttpUtility() {
	}

	public static HttpUtility getInstance() {
		return httpUtility;
	}

	public String executeNormalTask(HttpMethod httpMethod, String url,
			Map<String, String> param) throws WeiboException {
		return new JavaHttpUtility().executeNormalTask(httpMethod, url, param);
	}

	public boolean executeDownloadTask(String url, String path,
			FileDownloaderHttpHelper.DownloadListener downloadListener) {
		return !Thread.currentThread().isInterrupted()
				&& new JavaHttpUtility().doGetSaveFile(url, path,
						downloadListener);
	}

	public boolean executeUploadTask(String url, Map<String, String> param,
			String path, FileUploaderHttpHelper.ProgressListener listener)
			throws WeiboException {
		return !Thread.currentThread().isInterrupted()
				&& new JavaHttpUtility().doUploadFile(url, param, path,
						listener);
	}

}

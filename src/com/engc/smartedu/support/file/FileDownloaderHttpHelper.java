package com.engc.smartedu.support.file;

public class FileDownloaderHttpHelper {
    public static interface DownloadListener {
        public void pushProgress(int progress, int max);
    }
}

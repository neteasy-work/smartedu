package com.engc.smartedu.support.file;


public class FileUploaderHttpHelper {

    public static interface ProgressListener {
        public void transferred(long data);

        public void completed();
    }
}

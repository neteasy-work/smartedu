package com.engc.smartedu.support.asyncdrawable;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.engc.smartedu.R;

import com.engc.smartedu.support.file.FileDownloaderHttpHelper;
import com.engc.smartedu.support.file.FileLocationMethod;
import com.engc.smartedu.support.imagetool.ImageTool;
import com.engc.smartedu.support.lib.MyAsyncTask;
import com.engc.smartedu.support.utils.GlobalContext;

/**
 * User: qii
 * Date: 12-8-5
 */
@SuppressLint("NewApi")
public class ProfileAvatarAndDetailMsgPicTask extends MyAsyncTask<String, Integer, Bitmap> {

    private LruCache<String, Bitmap> lruCache;
    private String data = "";
    private ImageView view;
    private FileLocationMethod method;

    private ProgressBar pb;

    private boolean pbFlag = false;

    private GlobalContext globalContext;


    public ProfileAvatarAndDetailMsgPicTask(ImageView view, FileLocationMethod method) {

        this.lruCache = GlobalContext.getInstance().getAvatarCache();
        this.view = view;
        this.method = method;
        this.globalContext = GlobalContext.getInstance();
    }

    public ProfileAvatarAndDetailMsgPicTask(ImageView view, FileLocationMethod method, ProgressBar pb) {
        this.globalContext = GlobalContext.getInstance();
        this.lruCache = GlobalContext.getInstance().getAvatarCache();
        this.view = view;
        this.method = method;
        this.pb = pb;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (pb != null) {
            pb.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected Bitmap doInBackground(String... url) {
        data = url[0];
        if (!isCancelled()) {
            switch (method) {
                case picture_bmiddle:
                    return ImageTool.getMiddlePictureInBrowserMSGActivity(data, new FileDownloaderHttpHelper.DownloadListener() {
                        @Override
                        public void pushProgress(int progress, int max) {
                            publishProgress(progress, max);
                        }
                    });
                case avatar_large:
                    int avatarWidth = globalContext.getResources().getDimensionPixelSize(R.dimen.profile_avatar_width);
                    int avatarHeight = globalContext.getResources().getDimensionPixelSize(R.dimen.profile_avatar_height);
                    return ImageTool.getRoundedCornerPic(this.data, avatarWidth, avatarHeight, FileLocationMethod.avatar_large);

             }
        }

        return null;
    }

    /**
     * sometime picture has been cached in sd card,so only set indeterminate equal false to show progress when downloading
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (pb != null) {
            if (!pbFlag) {
                pb.setIndeterminate(false);
                pbFlag = true;
            }
            Integer progress = values[0];
            Integer max = values[1];
            pb.setMax(max);
            pb.setProgress(progress);
        }
    }

    @Override
    protected void onCancelled(Bitmap bitmap) {

        if (pb != null)
            pb.setVisibility(View.INVISIBLE);

        super.onCancelled(bitmap);
        clean();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (pb != null)
            pb.setVisibility(View.INVISIBLE);

        if (bitmap != null) {

            view.setVisibility(View.VISIBLE);
            view.setImageBitmap(bitmap);

            switch (method) {
                case avatar_small:
                    lruCache.put(data, bitmap);
                    break;
                case avatar_large:
                    lruCache.put(data, bitmap);
                    break;
            }

        } else {
            view.setImageDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        clean();
    }

    private void clean() {

        lruCache = null;
        globalContext = null;
    }
}
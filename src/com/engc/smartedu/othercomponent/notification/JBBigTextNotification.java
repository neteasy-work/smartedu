package com.engc.smartedu.othercomponent.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.engc.smartedu.R;

import com.engc.smartedu.bean.*;
import com.engc.smartedu.support.utils.NotificationUtility;
import com.engc.smartedu.support.utils.Utility;
import com.engc.smartedu.ui.main.MainTimeLineActivity;

/**
 * User: qii
 * Date: 12-12-5
 */
@SuppressLint("NewApi")
public class JBBigTextNotification {
    private Context context;

    private AccountBean accountBean;

    private CommentListBean comment;
    private MessageListBean repost;
    private CommentListBean mentionCommentsResult;

    private UnreadBean unreadBean;

    public JBBigTextNotification(Context context,
                                 AccountBean accountBean,
                                 CommentListBean comment,
                                 MessageListBean repost,
                                 CommentListBean mentionCommentsResult, UnreadBean unreadBean) {
        this.context = context;
        this.accountBean = accountBean;
        this.comment = comment;
        this.repost = repost;
        this.mentionCommentsResult = mentionCommentsResult;
        this.unreadBean = unreadBean;
    }


    private PendingIntent getPendingIntent() {
        Intent i = new Intent(context, MainTimeLineActivity.class);
        i.putExtra("account", accountBean);
        i.putExtra("comment", comment);
        i.putExtra("repost", repost);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, Long.valueOf(accountBean.getUid()).intValue(), i, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    public Notification get() {
        Notification.Builder builder = new Notification.Builder(context)
                .setTicker(NotificationUtility.getTicker(unreadBean))
                .setContentText(accountBean.getUsernick())
                .setSmallIcon(R.drawable.notification)
                .setAutoCancel(true)
                .setContentIntent(getPendingIntent())
                .setOnlyAlertOnce(true);

        builder.setContentTitle(NotificationUtility.getTicker(unreadBean));

        if (NotificationUtility.getCount(unreadBean) > 1) {
            builder.setNumber(NotificationUtility.getCount(unreadBean));
        }

        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle(builder);


        if (repost != null) {
            for (MessageBean m : repost.getItemList()) {
                bigTextStyle.setBigContentTitle(m.getUser().getScreen_name() + "：");
                bigTextStyle.bigText(m.getText());
            }
        }

        if (mentionCommentsResult != null) {
            for (CommentBean m : mentionCommentsResult.getItemList()) {
                bigTextStyle.setBigContentTitle(m.getUser().getScreen_name() + "：");
                bigTextStyle.bigText(m.getText());
            }
        }


        bigTextStyle.setSummaryText(accountBean.getUsernick());

        builder.setStyle(bigTextStyle);
        Utility.configVibrateLedRingTone(builder);

        return builder.build();
    }

}

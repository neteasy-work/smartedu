package com.engc.smartedu.support.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.Display;

import com.engc.smartedu.R;
import com.engc.smartedu.baidupush.server.BaiduPush;
import com.engc.smartedu.bean.AccountBean;
import com.engc.smartedu.bean.GroupListBean;
import com.engc.smartedu.bean.User;
import com.engc.smartedu.support.database.DatabaseManager;
import com.engc.smartedu.support.database.GroupDBTask;
import com.engc.smartedu.support.database.MessageDB;
import com.engc.smartedu.support.database.RecentDB;
import com.engc.smartedu.support.database.UserDB;
import com.engc.smartedu.support.settinghelper.SettingUtility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * User: Jiang Qi
 * Date: 12-7-27
 */
@SuppressLint("NewApi")
public final class GlobalContext extends Application {

    //singleton
    private static GlobalContext globalContext = null;
    private SharedPreferences sharedPref = null;

    //image size
    private Activity activity = null;
    private DisplayMetrics displayMetrics = null;

    //image memory cache
    private LruCache<String, Bitmap> avatarCache = null;

    //current account info
    private AccountBean accountBean = null;

    public boolean startedApp = false;


    private Map<String, String> emotions = null;

    private Map<String, Bitmap> emotionsPic = new HashMap<String, Bitmap>();

    private GroupListBean group = null;
    
    public final static String API_KEY = "H9BPGKapVqbztGCrVDO5c1U2";
	public final static String SECRIT_KEY = "TfGw9xXS9jWvl3GyrZZqIvndQKCEWLWk";
	public static final String SP_FILE_NAME = "push_msg_sp";
	
	public static final int NUM_PAGE = 6;// 总共有多少页
	public static int NUM = 20;// 每页20个表情,还有最后一个删除button
	private BaiduPush mBaiduPushServer;
	private Map<String, Integer> mFaceMap = new LinkedHashMap<String, Integer>();
	private SharePreferenceUtil mSpUtil;
	private UserDB mUserDB;
	private MessageDB mMsgDB;
	private RecentDB mRecentDB;
	private List<User> mUserList;
	private MediaPlayer mMediaPlayer;
	private NotificationManager mNotificationManager;
	private Notification mNotification;
	private Gson mGson;
	private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间


   
    @Override
    public void onCreate() {
        super.onCreate();
        globalContext = this;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        buildCache();
       // CrashHandler.getInstance().init(this);
		initFaceMap();
		initData();
    }

    public static GlobalContext getInstance() {
        return globalContext;
    }

    private void initData() {
		mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST,
				SECRIT_KEY, API_KEY);
		// 不转换没有 @Expose 注解的字段
		mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
				.create();
		mSpUtil = new SharePreferenceUtil(this, SP_FILE_NAME);
		mUserDB = new UserDB(this);
		mMsgDB = new MessageDB(this);
		mRecentDB = new RecentDB(this);
		mUserList = mUserDB.getUser();
		mMediaPlayer = MediaPlayer.create(this, R.raw.office);
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
	}

	public synchronized BaiduPush getBaiduPush() {
		if (mBaiduPushServer == null)
			mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST,
					SECRIT_KEY, API_KEY);
		return mBaiduPushServer;

	}

	public synchronized Gson getGson() {
		if (mGson == null)
			// 不转换没有 @Expose 注解的字段
			mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
					.create();
		return mGson;
	}

	public NotificationManager getNotificationManager() {
		if (mNotificationManager == null)
			mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		return mNotificationManager;
	}

	public synchronized MediaPlayer getMediaPlayer() {
		if (mMediaPlayer == null)
			mMediaPlayer = MediaPlayer.create(this, R.raw.office);
		return mMediaPlayer;
	}

	public synchronized UserDB getUserDB() {
		if (mUserDB == null)
			mUserDB = new UserDB(this);
		return mUserDB;
	}

	public synchronized RecentDB getRecentDB() {
		if (mRecentDB == null)
			mRecentDB = new RecentDB(this);
		return mRecentDB;
	}

	public synchronized MessageDB getMessageDB() {
		if (mMsgDB == null)
			mMsgDB = new MessageDB(this);
		return mMsgDB;
	}

	public synchronized List<User> getUserList() {
		if (mUserList == null)
			mUserList = getUserDB().getUser();
		return mUserList;
	}

	public synchronized SharePreferenceUtil getSpUtil() {
		if (mSpUtil == null)
			mSpUtil = new SharePreferenceUtil(this, SP_FILE_NAME);
		return mSpUtil;
	}

	public Map<String, Integer> getFaceMap() {
		if (!mFaceMap.isEmpty())
			return mFaceMap;
		return null;
	}

	/**
	 * 创建挂机图标
	 */
	@SuppressWarnings("deprecation")
	public void showNotification() {
		if (!mSpUtil.getMsgNotify())// 如果用户设置不显示挂机图标，直接返回
			return;

		/*int icon = R.drawable.notify_general;
		CharSequence tickerText = getResources().getString(
				R.string.app_is_run_background);
		long when = System.currentTimeMillis();
		mNotification = new Notification(icon, tickerText, when);

		// 放置在"正在运行"栏目中
		mNotification.flags = Notification.FLAG_ONGOING_EVENT;

		RemoteViews contentView = new RemoteViews(getPackageName(),
				R.layout.notify_status_bar_latest_event_view);
		contentView.setImageViewResource(R.id.icon,
				heads[mSpUtil.getHeadIcon()]);
		contentView.setTextViewText(R.id.title, mSpUtil.getNick());
		contentView.setTextViewText(R.id.text, tickerText);
		contentView.setLong(R.id.time, "setTime", when);
		// 指定个性化视图
		mNotification.contentView = contentView;

		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		// 指定内容意图
		mNotification.contentIntent = contentIntent;
		// 下面是4.0notify
		// Bitmap icon = BitmapFactory.decodeResource(getResources(),
		// heads[mSpUtil.getHeadIcon()]);
		// Notification.Builder notificationBuilder = new Notification.Builder(
		// this).setContentTitle(mSpUtil.getNick())
		// .setContentText(tickerText)
		// .setSmallIcon(R.drawable.notify_general)
		// .setWhen(System.currentTimeMillis())
		// .setContentIntent(contentIntent).setLargeIcon(icon);
		// Notification n = notificationBuilder.getNotification();
		// n.flags |= Notification.FLAG_NO_CLEAR;

		mNotificationManager.notify(PushMessageReceiver.NOTIFY_ID,
				mNotification);*/
	}

    public GroupListBean getGroup() {
        if (group == null) {
            group = GroupDBTask.get(GlobalContext.getInstance().getCurrentAccountId());
        }
        return group;
    }

    public void setGroup(GroupListBean group) {
        this.group = group;
    }

    private Map<String, String> getEmotions() {
        if (emotions == null) {
            InputStream inputStream = getResources().openRawResource(R.raw.emotions);
            emotions = new Gson().fromJson(new InputStreamReader(inputStream), new TypeToken<Map<String, String>>() {
            }.getType());
        }

        return emotions;
    }


    public DisplayMetrics getDisplayMetrics() {
        if (displayMetrics != null) {
            return displayMetrics;
        } else {
            Activity a = getActivity();
            if (a != null) {
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                DisplayMetrics metrics = new DisplayMetrics();
                display.getMetrics(metrics);
                this.displayMetrics = metrics;
                return metrics;
            } else {
                //default screen is 800x480
                DisplayMetrics metrics = new DisplayMetrics();
                metrics.widthPixels = 480;
                metrics.heightPixels = 800;
                return metrics;
            }
        }
    }

    public void setAccountBean(AccountBean accountBean) {
        this.accountBean = accountBean;
    }

    public AccountBean getAccountBean() {
        if (accountBean == null) {
            String id = SettingUtility.getDefaultAccountId();
            if (!TextUtils.isEmpty(id)) {
                accountBean = DatabaseManager.getInstance().getAccount(id);
            } else {
                List<AccountBean> accountList = DatabaseManager.getInstance().getAccountList();
                if (accountList != null && accountList.size() > 0) {
                    accountBean = accountList.get(0);
                }
            }
        }

        return accountBean;
    }

    public String getCurrentAccountId() {
        return getAccountBean().getUid();
    }


    public String getCurrentAccountName() {

        return getAccountBean().getUsernick();
    }


    public synchronized LruCache<String, Bitmap> getAvatarCache() {
        if (avatarCache == null) {
            buildCache();
        }
        return avatarCache;
    }

    public String getSpecialToken() {
        if (getAccountBean() != null)
            return getAccountBean().getAccess_token();
        else
            return "";
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private void buildCache() {
        int memClass = ((ActivityManager) getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();

//        int cacheSize = 1024 * 1024 * memClass / 5;

        int cacheSize = 1024 * 1024 * 8;

        avatarCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {

                return bitmap.getByteCount();
            }
        };
    }

    public synchronized Map<String, Bitmap> getEmotionsPics() {
        if (emotionsPic != null && emotionsPic.size() > 0) {
            return emotionsPic;
        } else {
            getEmotionsTask();
            return emotionsPic;
        }
    }


    private void getEmotionsTask() {
        Map<String, String> emotions = GlobalContext.getInstance().getEmotions();
        List<String> index = new ArrayList<String>();
        index.addAll(emotions.keySet());
        for (String str : index) {
            String url = emotions.get(str);
            int position = url.lastIndexOf("/");
            String name = url.substring(position + 1);
            AssetManager assetManager = GlobalContext.getInstance().getAssets();
            InputStream inputStream;
            try {
                inputStream = assetManager.open(name);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap != null) {
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, Utility.dip2px(20), Utility.dip2px(20), true);
                    if (bitmap != scaledBitmap) {
                        bitmap.recycle();
                        bitmap = scaledBitmap;
                    }
                    emotionsPic.put(str, bitmap);
                }
            } catch (IOException ignored) {

            }
        }
    }
    
    private void initFaceMap() {
		// TODO Auto-generated method stub
		mFaceMap.put("[呲牙]", R.drawable.f000);
		mFaceMap.put("[调皮]", R.drawable.f001);
		mFaceMap.put("[流汗]", R.drawable.f002);
		mFaceMap.put("[偷笑]", R.drawable.f003);
		mFaceMap.put("[再见]", R.drawable.f004);
		mFaceMap.put("[敲打]", R.drawable.f005);
		mFaceMap.put("[擦汗]", R.drawable.f006);
		mFaceMap.put("[猪头]", R.drawable.f007);
		mFaceMap.put("[玫瑰]", R.drawable.f008);
		mFaceMap.put("[流泪]", R.drawable.f009);
		mFaceMap.put("[大哭]", R.drawable.f010);
		mFaceMap.put("[嘘]", R.drawable.f011);
		mFaceMap.put("[酷]", R.drawable.f012);
		mFaceMap.put("[抓狂]", R.drawable.f013);
		mFaceMap.put("[委屈]", R.drawable.f014);
		mFaceMap.put("[便便]", R.drawable.f015);
		mFaceMap.put("[炸弹]", R.drawable.f016);
		mFaceMap.put("[菜刀]", R.drawable.f017);
		mFaceMap.put("[可爱]", R.drawable.f018);
		mFaceMap.put("[色]", R.drawable.f019);
		mFaceMap.put("[害羞]", R.drawable.f020);

		mFaceMap.put("[得意]", R.drawable.f021);
		mFaceMap.put("[吐]", R.drawable.f022);
		mFaceMap.put("[微笑]", R.drawable.f023);
		mFaceMap.put("[发怒]", R.drawable.f024);
		mFaceMap.put("[尴尬]", R.drawable.f025);
		mFaceMap.put("[惊恐]", R.drawable.f026);
		mFaceMap.put("[冷汗]", R.drawable.f027);
		mFaceMap.put("[爱心]", R.drawable.f028);
		mFaceMap.put("[示爱]", R.drawable.f029);
		mFaceMap.put("[白眼]", R.drawable.f030);
		mFaceMap.put("[傲慢]", R.drawable.f031);
		mFaceMap.put("[难过]", R.drawable.f032);
		mFaceMap.put("[惊讶]", R.drawable.f033);
		mFaceMap.put("[疑问]", R.drawable.f034);
		mFaceMap.put("[睡]", R.drawable.f035);
		mFaceMap.put("[亲亲]", R.drawable.f036);
		mFaceMap.put("[憨笑]", R.drawable.f037);
		mFaceMap.put("[爱情]", R.drawable.f038);
		mFaceMap.put("[衰]", R.drawable.f039);
		mFaceMap.put("[撇嘴]", R.drawable.f040);
		mFaceMap.put("[阴险]", R.drawable.f041);

		mFaceMap.put("[奋斗]", R.drawable.f042);
		mFaceMap.put("[发呆]", R.drawable.f043);
		mFaceMap.put("[右哼哼]", R.drawable.f044);
		mFaceMap.put("[拥抱]", R.drawable.f045);
		mFaceMap.put("[坏笑]", R.drawable.f046);
		mFaceMap.put("[飞吻]", R.drawable.f047);
		mFaceMap.put("[鄙视]", R.drawable.f048);
		mFaceMap.put("[晕]", R.drawable.f049);
		mFaceMap.put("[大兵]", R.drawable.f050);
		mFaceMap.put("[可怜]", R.drawable.f051);
		mFaceMap.put("[强]", R.drawable.f052);
		mFaceMap.put("[弱]", R.drawable.f053);
		mFaceMap.put("[握手]", R.drawable.f054);
		mFaceMap.put("[胜利]", R.drawable.f055);
		mFaceMap.put("[抱拳]", R.drawable.f056);
		mFaceMap.put("[凋谢]", R.drawable.f057);
		mFaceMap.put("[饭]", R.drawable.f058);
		mFaceMap.put("[蛋糕]", R.drawable.f059);
		mFaceMap.put("[西瓜]", R.drawable.f060);
		mFaceMap.put("[啤酒]", R.drawable.f061);
		mFaceMap.put("[飘虫]", R.drawable.f062);

		mFaceMap.put("[勾引]", R.drawable.f063);
		mFaceMap.put("[OK]", R.drawable.f064);
		mFaceMap.put("[爱你]", R.drawable.f065);
		mFaceMap.put("[咖啡]", R.drawable.f066);
		mFaceMap.put("[钱]", R.drawable.f067);
		mFaceMap.put("[月亮]", R.drawable.f068);
		mFaceMap.put("[美女]", R.drawable.f069);
		mFaceMap.put("[刀]", R.drawable.f070);
		mFaceMap.put("[发抖]", R.drawable.f071);
		mFaceMap.put("[差劲]", R.drawable.f072);
		mFaceMap.put("[拳头]", R.drawable.f073);
		mFaceMap.put("[心碎]", R.drawable.f074);
		mFaceMap.put("[太阳]", R.drawable.f075);
		mFaceMap.put("[礼物]", R.drawable.f076);
		mFaceMap.put("[足球]", R.drawable.f077);
		mFaceMap.put("[骷髅]", R.drawable.f078);
		mFaceMap.put("[挥手]", R.drawable.f079);
		mFaceMap.put("[闪电]", R.drawable.f080);
		mFaceMap.put("[饥饿]", R.drawable.f081);
		mFaceMap.put("[困]", R.drawable.f082);
		mFaceMap.put("[咒骂]", R.drawable.f083);

		mFaceMap.put("[折磨]", R.drawable.f084);
		mFaceMap.put("[抠鼻]", R.drawable.f085);
		mFaceMap.put("[鼓掌]", R.drawable.f086);
		mFaceMap.put("[糗大了]", R.drawable.f087);
		mFaceMap.put("[左哼哼]", R.drawable.f088);
		mFaceMap.put("[哈欠]", R.drawable.f089);
		mFaceMap.put("[快哭了]", R.drawable.f090);
		mFaceMap.put("[吓]", R.drawable.f091);
		mFaceMap.put("[篮球]", R.drawable.f092);
		mFaceMap.put("[乒乓球]", R.drawable.f093);
		mFaceMap.put("[NO]", R.drawable.f094);
		mFaceMap.put("[跳跳]", R.drawable.f095);
		mFaceMap.put("[怄火]", R.drawable.f096);
		mFaceMap.put("[转圈]", R.drawable.f097);
		mFaceMap.put("[磕头]", R.drawable.f098);
		mFaceMap.put("[回头]", R.drawable.f099);
		mFaceMap.put("[跳绳]", R.drawable.f100);
		mFaceMap.put("[激动]", R.drawable.f101);
		mFaceMap.put("[街舞]", R.drawable.f102);
		mFaceMap.put("[献吻]", R.drawable.f103);
		mFaceMap.put("[左太极]", R.drawable.f104);

		mFaceMap.put("[右太极]", R.drawable.f105);
		mFaceMap.put("[闭嘴]", R.drawable.f106);
	}
    
    /**
	 * 保存对象
	 * 
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 读取对象
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file) {
		if (!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException) {
				File data = getFileStreamPath(file);
				data.delete();
			}
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return null;
	}
	
	/**
	 * 判断缓存是否存在
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile) {
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists())
			exist = true;
		return exist;
	}

	/**
	 * 判断缓存是否失效
	 * 
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile) {
		boolean failure = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists()
				&& (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
			failure = true;
		else if (!data.exists())
			failure = true;
		return failure;
	}
	
	public boolean containsProperty(String key) {
		Properties props = getProperties();
		return props.containsKey(key);
	}

	public void setProperties(Properties ps) {
		globalContext.setProperties(ps);
		
	}

	public Properties getProperties() {
		return globalContext.getProperties();
	}

	public void setProperty(String key, String value) {
		globalContext.setProperty(key, value);
	}

	public String getProperty(String key) {
		return globalContext.getProperty(key);
	}

	public void removeProperty(String... key) {
		globalContext.removeProperty(key);
	}

}


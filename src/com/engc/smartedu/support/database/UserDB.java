package com.engc.smartedu.support.database;

import java.util.LinkedList;
import java.util.List;

import com.engc.smartedu.bean.User;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class UserDB {
	private UserDBHelper helper;

	public UserDB(Context context) {
		helper = new UserDBHelper(context);
	}

	public User selectInfo(String userId) {
		User u = new User();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("select * from user where userId=?",
				new String[] { userId + "" });
		if (c.moveToFirst()) {
			u.setHeadpic(c.getString(c.getColumnIndex("img")));
			u.setUsername(c.getString(c.getColumnIndex("nick")));
			u.setChannelId(c.getString(c.getColumnIndex("channelId")));
		} else {
			return null;
		}
		return u;
	}

	public void addUser(List<User> list) {
		SQLiteDatabase db = helper.getWritableDatabase();
		for (User u : list) {
			db.execSQL(
					"insert into user (userId,nick,img,channelId,_group) values(?,?,?,?,?)",
					new Object[] { u.getUsercode(), u.getUsername(), u.getHeadpic(),
							u.getChannelId(), 0 });
		}
		db.close();
	}

	public void addUser(User u) {
		if (selectInfo(u.getUsercode()) != null) {
			update(u);
			return;
		}
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(
				"insert into user (userId,nick,img,channelId,_group) values(?,?,?,?,?)",
				new Object[] { u.getUsercode(), u.getUsername(), u.getHeadpic(),
						u.getChannelId(), 0 });
		db.close();

	}

	public User getUser(String userId) {
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor c = db.rawQuery("select * from user where userId=?",
				new String[] { userId });
		User u = new User();
		if (c.moveToNext()) {
			u.setUsercode(c.getString(c.getColumnIndex("userId")));
			u.setUsername(c.getString(c.getColumnIndex("nick")));
			u.setHeadpic(c.getString(c.getColumnIndex("img")));
			u.setChannelId(c.getString(c.getColumnIndex("channelId")));
		}
		return u;
	}

	public void updateUser(List<User> list) {
		if (list.size() > 0) {
			delete();
			addUser(list);
		}
	}

	public List<User> getUser() {
		SQLiteDatabase db = helper.getWritableDatabase();
		List<User> list = new LinkedList<User>();
		Cursor c = db.rawQuery("select * from user", null);
		while (c.moveToNext()) {
			User u = new User();
			u.setUsercode(c.getString(c.getColumnIndex("userId")));
			u.setUsername(c.getString(c.getColumnIndex("nick")));
			u.setHeadpic(c.getString(c.getColumnIndex("img")));
			u.setChannelId(c.getString(c.getColumnIndex("channelId")));
			list.add(u);
		}
		c.close();
		db.close();
		return list;
	}

	public void update(User u) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(
				"update user set nick=?,img=?,_group=? where userId=?",
				new Object[] { u.getUsername(), u.getHeadpic(), 0,
						u.getUsercode() });
		db.close();
	}

	public User getLastUser() {
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor c = db.rawQuery("select * from user", null);
		User u = new User();
		while (c.moveToLast()) {
			u.setUsercode(c.getString(c.getColumnIndex("userId")));
			u.setUsername(c.getString(c.getColumnIndex("nick")));
			u.setHeadpic(c.getString(c.getColumnIndex("img")));
			u.setChannelId(c.getString(c.getColumnIndex("channelId")));
		}
		c.close();
		db.close();
		return u;
	}

	public void delUser(User u) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from user where userId=?",
				new Object[] { u.getUsercode() });
		db.close();
	}

	public void delete() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from user");
		db.close();
	}
}

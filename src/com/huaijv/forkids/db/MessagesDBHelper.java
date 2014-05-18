package com.huaijv.forkids.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MessagesDBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "Messages.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "Messages_table";

	// 表里面的三个内容
	private static final String ID = "_id";
	private static final String TYPE = "_type";
	private static final String RealID = "_realid";

	private static final String Tag = "MessagesDB";

	public MessagesDBHelper(Context context) // , String name, CursorFactory factory,
										// int version
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE " + TABLE_NAME + " (" + ID
				+ " INTEGER primary key autoincrement, " + RealID
				+ " INTEGER, " + TYPE + " INTEGER);"; //
		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}

	public Cursor select() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
				RealID + " ASC");
		return cursor;

	}

	/* 增加操作 */
	public long insert(int type) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		int realid = getRealId();
		cv.put(RealID, realid);
		cv.put(TYPE, type);
		long row = db.insert(TABLE_NAME, null, cv);
		return realid;
		// return row; //row是行的意思
	}

	public void insertWithRealIdAndType(int realid, int type) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(RealID, realid);
		cv.put(TYPE, type);
		long row = db.insert(TABLE_NAME, null, cv);
		// return row; //row是行的意思
	}

	/* 得到一个真实的id */
	public int getRealId() {
		Cursor cursor = select();
		int realid = 1;

		// 如果cursor为空，返回id = 1
		if (!cursor.moveToFirst()) {
			return realid;
		} else {
			while (true) {
				if (realid != cursor.getInt(2)) {
					return realid;
				} else {
					realid++;
					if (!cursor.moveToNext())
						return realid;
				}
			}
		}
	}

	/* 删除操作 */
	public void deleteById(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = ID + "=?";
		String[] whereValue = { Integer.toString(id) };
		db.delete(TABLE_NAME, where, whereValue);

	}

	public void deleteByRealId(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = RealID + "=?";
		String[] whereValue = { Integer.toString(id) };
		db.delete(TABLE_NAME, where, whereValue);

	}

	/* 修改操作 */
	// id是你要修改的id号，name是新的名字
	public void updateById(int id, int type) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = ID + "=?";
		String[] whereValue = { Integer.toString(id) };

		ContentValues cv = new ContentValues();
		cv.put(TYPE, type);

		db.update(TABLE_NAME, cv, where, whereValue);
	}

	public void updateByRealId(int id, int type) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = RealID + "=?";
		String[] whereValue = { Integer.toString(id) };

		ContentValues cv = new ContentValues();
		cv.put(TYPE, type);

		db.update(TABLE_NAME, cv, where, whereValue);
	}

}

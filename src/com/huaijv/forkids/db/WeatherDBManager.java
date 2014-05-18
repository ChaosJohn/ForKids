package com.huaijv.forkids.db;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.os.Environment;

import com.huaijv.forkids.R;

public class WeatherDBManager {
	private final int BUFFER_SIZE = 400000;
	public static final String PACKAGE_NAME = "com.huaijv.forkids";
	public static final String DB_NAME = "WeatherZipCode.db";
	public static final String DB_PATH = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME + "/databases";

	private Context context;

	public WeatherDBManager(Context context) {
		this.context = context;
	}

	/** copy the database under raw */
	public void copyDatabase() {

		File file = new File(DB_PATH);
		if (!file.isDirectory())
			file.mkdir();
		String dbfile = DB_PATH + "/" + DB_NAME;// 自己应用数据库的名字

		try {
			if (new File(dbfile).length() == 0) {

				FileOutputStream fos = new FileOutputStream(dbfile);
				byte[] buffer = new byte[BUFFER_SIZE];

				readDB(fos, buffer, R.raw.citychina);// 数据库文件的名称

				fos.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void readDB(FileOutputStream fos, byte[] buffer, int db_id)
			throws IOException {
		int count;
		InputStream is;
		is = this.context.getResources().openRawResource(db_id);
		while ((count = is.read(buffer)) > 0) {
			fos.write(buffer, 0, count);
		}
		is.close();
	}
}

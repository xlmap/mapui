package com.map.ui;


import android.app.Application;
import android.view.WindowManager;

import java.util.*;
import java.io.*;
import android.os.*;
import java.lang.Thread.UncaughtExceptionHandler;
//import android.util.*;
import android.content.*;



import android.preference.*;
import com.xl.game.math.*;

import java.text.*;
import android.content.pm.*;
import com.pgyersdk.crash.*;
import com.pgyersdk.update.*;
import java.util.Locale;
import java.util.Date;
import com.xl.game.tool.Log;

/**
 * 
 * @author <a href="mailto:kris@krislq.com">Kris.lee</a>
 * @website www.krislq.com
 * @date Nov 29, 2012
 * @version 1.0.0
 * 
 */
public class xlApplication extends Application
{
	private WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
	public SharedPreferences sharedPreferences;

	public boolean isAutoLine;//自动换行
	public int edit_textSize;//编辑框字体大小
	public boolean isDrawLineText;//显示行号
	public boolean isHiLight; //高亮
	public boolean isSaveLastFile;//保存最后文件
	public boolean autoSave;//自动保存
	public boolean screen_all; //音量全屏
	public int theme; //主题
	public String themeText;//主题文字
	public boolean fileTable; //文件选项卡
	public boolean isAutoText;//自动缩进
	public boolean isAutoWord; //自动提示
  public String screen;
	public int orientation;
	
	public int blackSize;
	public String filePath; //默认文件路径
		public static String URL="http://www.pgyer.com/QTBW";
	
//实现悬浮窗到状态栏
	public xlApplication()
	{
		windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
	}
	public WindowManager.LayoutParams getWindowParams()
	{
		return windowParams;
	}




	public static  String DIR = "mnt/sdcard/";
	//Environment.getExternalStorageDirectory()
	//.getAbsolutePath() + "/survey/log/";
	public static String NAME ="log.txt"; //getCurrentDateString() + ".txt";

	@Override
	public void onCreate() {
		super.onCreate();
		Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
		String temp;
		
		//蒲公英日志
		PgyCrashManager.register(this);
		sharedPreferences = //getSharedPreferences("settings",Context.MODE_PRIVATE);
			PreferenceManager.getDefaultSharedPreferences(this);
		isAutoLine= sharedPreferences.getBoolean("isAutoLine",true);
		edit_textSize= Str.atoi( sharedPreferences.getString("edit_textSize","16"));
		isDrawLineText=	sharedPreferences.getBoolean("isDrawLineText",false);
		isHiLight=	sharedPreferences.getBoolean("isHiLight",false);
		isSaveLastFile=	sharedPreferences.getBoolean("isSaveLastFile",true);
		autoSave=	sharedPreferences.getBoolean("autoSave",true);
		screen_all=	sharedPreferences.getBoolean("screen_all",true);
		isAutoText = sharedPreferences.getBoolean("isAuto",true);
		themeText=	sharedPreferences.getString("theme","白色");
		fileTable = sharedPreferences.getBoolean("fileTable",false);
		isAutoWord = sharedPreferences.getBoolean("isAutoWord",false);
		
		
		temp = sharedPreferences.getString("blackSize","64");
		filePath= sharedPreferences.getString("filePath","MyTile");
		
		
		
		blackSize = Str.atoi(temp);
		temp = sharedPreferences.getString("screen","横屏");
		if(temp.equals("横屏"))
		{
			orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
		}
		else if(temp.equals("竖屏"))
		{
			orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
		}
		else
		{
			orientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
		}
		if(themeText.equals("白色"))
		{
			if(Build.VERSION.SDK_INT >= 11)
			{
				theme=android.R.style.Theme_Holo_Light;

			}
			else
				theme = android.R.style.Theme_Light;
		}
		else if(themeText.equals("黑色"))
		{
			if(Build.VERSION.SDK_INT >= 11)
			{
				theme=android.R.style.Theme_Holo;

			}
			else
				theme = android.R.style.Theme_Black;
		
		}
		
		else
		{
			if(Build.VERSION.SDK_INT >= 11)
			{
				theme=android.R.style.Theme_Holo;

			}
			else
				theme = android.R.style.Theme_Light;
			
			theme=android.R.style.Theme_Holo;
		}
		setTheme(theme);
		sharedPreferences.getBoolean("fileTable",true);
    
		createDir();

		//tvCheckout.setText(settings.getBoolean(Consts.CHECKOUT_KEY, false)+"");
		//tvEditText.setText(settings.getString(Consts.EDIT_KEY, ""));
	}

	/**
	 * 捕获错误信息的handler
	 */
	private UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {

			DIR= "mnt/sdcard/";
			Log.e("App","我崩溃了"+DIR);

			String info = null;
			ByteArrayOutputStream baos = null;
			PrintStream printStream = null;
			try {
				baos = new ByteArrayOutputStream();
				printStream = new PrintStream(baos);
				ex.printStackTrace(printStream);
				byte[] data = baos.toByteArray();
				info = new String(data);
				data = null;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (printStream != null) {
						printStream.close();
					}
					if (baos != null) {
						baos.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			writeErrorLog(info);
			//info=info.substring(info.lastIndexOf(  "com.xl"));
			Log.e("_ERROR",info);
			/*
			 Intent intent = new Intent(getApplicationContext(),		 printActivity.class);
			 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 startActivity(intent);
			 */
			android.os.Process.killProcess(android.os.Process.myPid());
			//System.exit(0);
		}
	};

	/**
	 * 向文件中写入错误信息
	 * 
	 * @param info
	 */
	protected void writeErrorLog(String info) {
		File dir = new File(DIR);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File file = new File(dir, NAME);
		try
		{
			if (!file.isFile())file.createNewFile();
		}
		catch (IOException e)
		{}
		try
		{
			FileOutputStream fileOutputStream = new FileOutputStream(file, false);
			fileOutputStream.write(info.getBytes());
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取当前日期
	 * 
	 * @return
	 */
	private static String getCurrentDateString() {
		String result = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
																								Locale.getDefault());
		Date nowDate = new Date();
		result = sdf.format(nowDate);
		return result;
	}
	
	//创建文件夹
	public void createDir()
	{
		File file=new File(getSDPath()+"/"+ filePath);
		if(!file.isDirectory())
		{
			file.mkdirs();
		}
	}
	public String getSDPath()
	{
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
		if (sdCardExist)
		{
			sdDir = Environment.getExternalStorageDirectory();//获取sd卡目录
		}
		else 
		{
			return null;
		}
		return sdDir.toString();
	}
}


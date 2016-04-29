package com.map.ui;



import android.os.Bundle;
import android.preference.PreferenceActivity;


import android.widget.*;
import android.content.*;

import android.os.*;
import android.preference.*;
import android.preference.Preference.*;
import com.xl.game.math.*;
import android.content.pm.*;

public class SetActivity extends PreferenceActivity implements OnPreferenceChangeListener
{

	
	EditTextPreference blackSize;
	CheckBoxPreference isSave;
	ListPreference  screen;

	ListPreference  theme;
	
	
	@Override
	public boolean onPreferenceChange(Preference p1, Object p2)
	{
		if(p1==blackSize)
		{
			application.blackSize=Str.atoi((String)p2);
			toast(""+p2,0);
		}
		else if(p1==isSave)
		{
			application.isSaveLastFile=(boolean)p2;
		}
		else if(p1==screen)
		{
			String temp=(String)p2;
			application.screen=(String)p2;
			if(temp.equals("横屏"))
			{
			application.	orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			}
			else if(temp.equals("竖屏"))
			{
			application.	orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			}
			else
			{
			application.	orientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
			}
		}
		else if(p1==theme)
		{
			String themeText=(String)p2;
			if(themeText.equals("白色"))
			{
				if(Build.VERSION.SDK_INT >= 11)
				{
				application.	theme=android.R.style.Theme_Holo_Light;

				}
				else
					application.theme = android.R.style.Theme_Light;
			}
			else if(themeText.equals("黑色"))
			{
				if(Build.VERSION.SDK_INT >= 11)
				{
					application.theme=android.R.style.Theme_Holo;

				}
				else
					application.theme = android.R.style.Theme_Black;
				}
		}
		return true;
	}

	xlApplication application;

	protected void onCreate(Bundle savedInstanceState) 
	{
		application= (xlApplication)getApplication();
		if(Build.VERSION.SDK_INT >= 11)
		{
			setTheme(application.theme);

		}
		else
		{
			setTheme(android.R.style.Theme_Black);
		}
		setRequestedOrientation(application.orientation);
		
		
		super.onCreate(savedInstanceState);
		
		
		//	exb.write("setting");
		String key="setting";
		
		


		this.addPreferencesFromResource(R.xml.setting);

		//CheckBoxPreference组件 

		 blackSize=(EditTextPreference)findPreference("blackSize");
		 isSave = (CheckBoxPreference)findPreference("isSaveLastFile");
		  screen=(ListPreference)findPreference("screen");
		
		  theme=(ListPreference)findPreference("theme");
		
		blackSize.setOnPreferenceChangeListener(this);
		isSave.setOnPreferenceChangeListener(this);
		screen.setOnPreferenceChangeListener(this);
		theme.setOnPreferenceChangeListener(this);
		
		
		/*
		edit_textSize.setOnPreferenceChangeListener(new OnPreferenceChangeListener()
			{

				@Override
				public boolean onPreferenceChange(Preference p1, Object p2)
				{
					// TODO: Implement this method
					application.edit_textSize= Str.atoi((String)p2);
					toast("设置成功，重启后生效",0);
					return false;
				}


			});



		CheckBoxPreference Checkbox_isAutoLine = (CheckBoxPreference) findPreference("isAutoLine");
		/*	Checkbox_isAutoLine.setOnPreferenceClickListener(new OnPreferenceClickListener() { 

		 @Override  
		 public boolean onPreferenceClick(Preference preference) { 
		 //这里可以监听到这个CheckBox 的点击事件  
		 return true; 
		 }  
		 }); 
		 */
		 /*
		Checkbox_isAutoLine.setOnPreferenceChangeListener(new OnPreferenceChangeListener() { 
				@Override 
				public boolean onPreferenceChange(Preference arg0, Object newValue) { 
					//这里可以监听到checkBox中值是否改变了 
					//并且可以拿到新改变的值  
					//Toast.makeText(mContext,"checkBox_0改变的值为" +  (Boolean)newValue, Toast.LENGTH_LONG).show(); 
					application.isAutoLine=(boolean)newValue;
					toast("设置成功，重启后生效",0);
					return true;
				} 
			}); 

		CheckBoxPreference Checkbox_isDrawLineText = (CheckBoxPreference) findPreference("isDrawLineText");
		/*		Checkbox_isDrawLineText.setOnPreferenceClickListener(new OnPreferenceClickListener() { 
		 @Override  
		 public boolean onPreferenceClick(Preference preference) { 
		 //这里可以监听到这个CheckBox 的点击事件 
		 return true; 
		 }  
		 }); */
		 /*
		Checkbox_isDrawLineText.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {  
				@Override  
				public boolean onPreferenceChange(Preference arg0, Object newValue) { 
					//这里可以监听到checkBox中值是否改变了
					//并且可以拿到新改变的值  
					//Toast.makeText(mContext,"checkBox_1改变的值为" +  (Boolean)newValue, Toast.LENGTH_LONG).show();  
					application.isDrawLineText=(boolean)newValue;
					return true;
				}
			}); 

		CheckBoxPreference checkBox_isSaveLastFile=(CheckBoxPreference) findPreference("isSaveLastFile");

		CheckBoxPreference autoSave=(CheckBoxPreference)findPreference("autoSave");

		ListPreference  theme=(ListPreference)findPreference("theme");
		theme.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {  
				@Override  
				public boolean onPreferenceChange(Preference arg0, Object newValue) { 
					//这里可以监听到checkBox中值是否改变了
					//并且可以拿到新改变的值  
					//Toast.makeText(mContext,"checkBox_1改变的值为" +  (Boolean)newValue, Toast.LENGTH_LONG).show();  
					String themeText=(String)newValue;
					if(themeText.equals("白色"))
					{
						application. theme=android.R.style.Theme_Holo_Light;
					}
					else if(themeText.equals("黑色"))
					{
						application.	theme=android.R.style.Theme_Holo;
					}
					else if(themeText.equals("深灰"))
					{
						application.	theme=android.R.style.Theme;
					}
					else
					{
						application.	theme=android.R.style.Theme_Holo;
					}
					application.themeText=themeText;
					toast("设置成功，重启后生效",0);
					return true;
				}
			}); 



		CheckBoxPreference Checkbox_isHighLine = (CheckBoxPreference) findPreference("isHiLight");
		/*		Checkbox_isDrawLineText.setOnPreferenceClickListener(new OnPreferenceClickListener() { 
		 @Override  
		 public boolean onPreferenceClick(Preference preference) { 
		 //这里可以监听到这个CheckBox 的点击事件 
		 return true; 
		 }  
		 }); */
		 
		 /*
		Checkbox_isHighLine.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {  
				@Override  
				public boolean onPreferenceChange(Preference arg0, Object newValue) { 
					//这里可以监听到checkBox中值是否改变了
					//并且可以拿到新改变的值  
					//Toast.makeText(mContext,"checkBox_1改变的值为" +  (Boolean)newValue, Toast.LENGTH_LONG).show();  
					application.isHiLight=(boolean)newValue;
					return true;
				}
			}); 


		CheckBoxPreference Checkbox_isAutoWord = (CheckBoxPreference) findPreference("isAutoWord");
		/*		Checkbox_isDrawLineText.setOnPreferenceClickListener(new OnPreferenceClickListener() { 
		 @Override  
		 public boolean onPreferenceClick(Preference preference) { 
		 //这里可以监听到这个CheckBox 的点击事件 
		 return true; 
		 }  
		 }); */
		 /*
		Checkbox_isAutoWord.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {  
				@Override  
				public boolean onPreferenceChange(Preference arg0, Object newValue) { 
					//这里可以监听到checkBox中值是否改变了
					//并且可以拿到新改变的值  
					//Toast.makeText(mContext,"checkBox_1改变的值为" +  (Boolean)newValue, Toast.LENGTH_LONG).show();  
					application.isAutoWord=(boolean)newValue;
					return true;
				}
			}); 

    */
	}


	void toast(String text,int type)
	{
		Toast.makeText(this,text,type).show();
	}
}



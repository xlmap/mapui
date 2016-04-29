package com.map.ui;
import com.xl.filelist.FileListDialog;
import android.os.*;
import android.graphics.*;
import com.xl.game.tool.*;

public class FileListActivity extends FileListDialog
{
  Bitmap bitmap_tiled;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		xlApplication application = (xlApplication)getApplication();
		setTheme( application.theme);
    bitmap_tiled=XL.ReadBitMap(this,R.drawable.ex_tiled);
		
		setRequestedOrientation(application.orientation);
		
		super.onCreate(savedInstanceState);
		setHidDir(true);
	}
	
	public Bitmap getFileBitmap(String filename)
	{
		if(filename.endsWith(".tmx") || filename.endsWith(".TMX"))
		{
			return bitmap_tiled;
		}
		else
			return super.getFileBitmap(filename);
	}
	
}

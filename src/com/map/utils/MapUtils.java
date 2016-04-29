package com.map.utils;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.os.*;
import android.widget.*;
import com.map.ui.*;
import java.io.*;
import java.util.*;

import java.lang.Process;

public class MapUtils extends AsyncTask<Object,Void,Void> {

	private Context context;

	public MapUtils(Context context) {
		this.context = context;
		//extractFile();
	}

	@Override
	protected Void doInBackground(Object...params) {
		// TODO: Implement this method
		//调用jni中c的函数，保存数组和资源图片id
	//	Native.saveArray((long[][])params[0]);
		//保存图片
		List<Bitmap> bitmapList = (List<Bitmap>)params[1];
		List<String> nameList = (List<String>)params[2];
		if (!nameList.isEmpty()) {
			String imageName[] = new String[nameList.size()];
			for (int i=0;i < imageName.length;i++) {
				//保存图片到drawable-hdpi
				savaBitmap(bitmapList.get(i),nameList.get(i));
				//去掉图片的后缀名.png .jpg ...
				int index = nameList.get(i).indexOf(".");
				if (index > 0) {
					imageName[i] = "R.drawable." + nameList.get(i).substring(0, index);
				} else {
					imageName[i] = "R.drawable." + nameList.get(i);
				}

			}
			//Native.imageRes(imageName);
		}
		
		return null;
	}



	//执行shell命令解压文件到sdcard
	public void extractFile() {

		String[] comnand = new String[]{"cd /sdcard/地图编辑器/Test/",
			"tar -xvf *.tar","rm -rf *.tar"};
		try {
			//获得assets文件夹下所有文件
			String[] files = context.getAssets().list("");
			for (String fileName:files) {
				//加载文件到/data/data/com.map.ui/files
				copyAssetFileToFiles(context, fileName);
			}
		} catch (Exception e) {}
		//判断sdcard是否存在
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File f = new File("mnt/sdcard/地图编辑器/Test");
			if (!f.exists()) {
				String path = "cp " + context.getFilesDir().getAbsolutePath() + "/*.tar /sdcard/地图编辑器/Test";
				execShell(new String[]{"cd mnt/sdcard","mkdir -p 地图编辑器/Test",path});
			}
			execShell(comnand);

		} else {
			Toast.makeText(context.getApplicationContext(), "sdcard卡不存在！", Toast.LENGTH_LONG).show();
		}
	}

	//执行shell命令
    public static void execShell(String[] commands) {
        Process p = null;
        DataOutputStream os = null;
        BufferedReader buf = null;
        try {
            p = Runtime.getRuntime().exec("sh");
            os = new DataOutputStream(p.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }
                os.write(command.getBytes());
                os.writeBytes("\n");
                os.flush();
            }
            os.writeBytes("exit\n");
            os.flush();

            buf = new BufferedReader(new InputStreamReader(p.getInputStream()));

            while (buf.readLine() != null) {
            }
            p.waitFor();
        } catch (Exception e) {
        } finally {
            try {
                if (buf != null) {
                    buf.close();
                }

                if (os != null) {
                    os.close();
                }
                if (p != null) {
                    p.destroy();
                }
            } catch (Exception e) {

            }
        }
    }


	//复制一个目录
    public static void copyAssetDirToFiles(Context context, String dirName)throws Exception {

        File dir = new File(context.getFilesDir() + "/" + dirName);
		if (!dir.exists()) {
			dir.mkdir();
		} else {
			dir.delete();
		}
        AssetManager assetManager = context.getAssets();
        String[] children = assetManager.list(dirName);
        for (String child : children) {
            child = dirName + '/' + child;
            String[] grandChildren = assetManager.list(child);
            if (grandChildren.length == 0)
                copyAssetFileToFiles(context, child);
            else
                copyAssetDirToFiles(context, child);
        }
    }


    //复制一个文件
    public static void copyAssetFileToFiles(Context context, String fileName)throws Exception {
        InputStream in = context.getAssets().open(fileName);
        byte[] buffer = new byte[in.available()];
        in.read(buffer);
        in.close();

        File file = new File(context.getFilesDir() + "/" + fileName);
		if (!file.exists()) {
			file.createNewFile();
		} else {
			file.delete();
		}
        FileOutputStream out = new FileOutputStream(file);
        out.write(buffer);
        out.close();
    }



	//将所有的图片进行保存
	public static void savaBitmap(Bitmap bitmap,String imgName) {
		//如果选择的图片没有扩展名，则添加默认扩展名.png
		if(imgName.indexOf(".")<0){
			imgName = imgName+".png";
		}
		File file = new File("mnt/sdcard/地图编辑器/Test/res/drawable-hdpi/"+imgName);
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
		} catch (Exception e) {

		} finally {
			if (out != null) {
				try {
					out.flush();
					out.close();
				} catch (Exception ex) {

				}
			}
		}

	}


	/**
	 * 因为Bitmap没有实现序列化，所以不能直接在序列化类(MyBitmap)中使用
	 * BytesBitmap用于实现Bitmap和byte[]间的相互转换
	 * 把Bitmap数组转换为Bitmap对象
	 */

	public static Bitmap getBitmap(byte[] data) {
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}

	//把Bitmap对象转换为Bitmap数组
	public static byte[] getBytes(Bitmap bitmap) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
		//把输出流转换为byte数组
		return bos.toByteArray();
	}


	/**
	 * MyBitmap是要被序列化的类
	 * 其中包含了通过BytesBitmap类得到的Bitmap中数据的数组
	 * 和一个保存位图的名字的字符串，用于标识图片
	 *
	 */

	class MyBitmap implements Serializable {
		private static final long serialVersionUID = 1L;
		private byte[] bitmapBytes = null;
		private String imageName = null;

		public MyBitmap(byte[] bitmapBytes, String imageName) {
			// TODO Auto-generated constructor stub
			this.bitmapBytes = bitmapBytes;
			this.imageName = imageName;
		}

		public byte[] getBitmapBytes() {
			return this.bitmapBytes;
		}

		public String getName() {
			return this.imageName;
		}
	}
}

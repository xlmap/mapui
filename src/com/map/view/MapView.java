package com.map.view;

import android.content.*;
import android.graphics.*;
//import android.util.*;
import android.view.*;
//import com.map.ui.*;
import java.util.*;
import android.os.*;
import com.map.utils.*;
import android.widget.*;
import com.map.tool.*;
import java.io.*;
import com.xl.game.math.*;
import com.map.*;
import com.xl.game.tool.*;
import android.util.AttributeSet;


//Time：2015-06-30
//作者：Liuzhiyong

/**
 * 绘制地图View 实现原理主要是通过一个二维数组保存每一个坐标点
 * 坐标点：比如一个二维数组int[3][4]，它就有12个点，至于每个点的坐标是多少就和地图块的大小有关
 * 二维数组初始化时，每个点的值都为0，绘制地图也就是修改点上的值
 
 * 绘制一张图片时把数组int[i][j]的值进行修改，修改成不同的值，表示不同的图片，0就表示这个点没有图片
 * 比如说int[0][0]=1第一个点等于1，表示用这个点绘制图片1，int[0][1]=2第二个点等于2，表示这个点绘制图片2
 
 * 绘制完地图后，把所有的Bitmap和二维数组进行保存，直接输出到文件或者序列化都是可以的
 * 其实每个点上还可以有不同的图片，这就需要用到三维数组，如果加上游戏关卡的话，就需要四维数组，处理起来会麻烦一些
 * GameView类除了一些坐标处理会稍微麻烦些，其他的都是很比较简单的  (\/)
 (*.* )
c_(")(")
 */

public class MapView extends View implements ParamsChangeListener {


	private Bitmap bitmap ;         //绘制图片的Bitmap对象
	private Paint paint,bitmapPaint;//定义画笔
	private Paint horText ;//水平方向文本
	private Paint verText ;//垂直方向文本
	
	private Point start,end; //光标
	
	private Map<Bitmap,Integer> bitType;//通过图片的Bitmap，获得图片的类型
	private Map<Integer,Bitmap> typeBit;//通过图片的类型,获得图片的Bitmap

	//private int lineX,lineY;		//绘制线条开始的X Y坐标
	//private int rectX,rectY;		//绘制小矩形的X Y坐标
	//private int offsetX,offsetY;    //偏移X Y坐标
  private boolean isEdit;
	private boolean isDrawGrid;
	private boolean isDrawMark;
	//private boolean isDraw
	private boolean flag = false;   //判断按下的点是否在地图范围内
	private int mapWidth,mapHeight; //地图的宽度 高度
	//private int TEXT_WIDTH = 30;    //绘制文本的宽度
//双指模式
	private boolean isTouch;
	private boolean isDelete;
	private int step,next;          //上一步 下一步
	//private int screenWidth,screenHeight;//屏幕的宽度 高度
	private int blockWidth,blockHeight;	 //地图块的宽度 高度
	private int itemWidth,itemHeight;
	private float zoom;  //画面缩放倍数
	private ArrayList<Bitmap> bitmapList;//保存选择的图片的Bitmap
	private ArrayList<String> nameList;//保存选择的图片名称
	private ArrayList<Integer> firstList; //起始id
	private int gid; //当前id进度
	
	private List<Point> pointList;      //保存绘制了地图的坐标点
	private List<Integer> dataList;		//保存绘制了地图的点上的图片类型
	private int data[][];//data2[][];	//保存所有坐标点
	//private int mVerLines,mHorLines;    //地图可见区域水平与垂直线条数
	//private int viewVerLines,viewHorLines;//View可见区域水平与垂直线条数
	private int srcX=-1,srcY=-1,srcData=-1;
// Context context;
	//边框
	//Paint paint_rect;
	private Context context;
	private String mText;
	public static final int WITHOUT = 1;
 
  
	public MapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		zoom=1;
		init(context);
	}


	public MapView(Context context) {
		super(context);
		this.context=context;
		zoom=1;
		init(context);
	}


	//参数初始化
	public void init(Context context) 
	{
		initScrollView();
		scrollTo(0,0);
		this.context=context;
		gid=1;
		setPadding(30,30,30,30);
		bitmap=null;
		isDrawGrid=true;
		isDrawMark=true;
		setFocusableInTouchMode(true);
		mapWidth=mapHeight=100;
		
		
		//TEXT_WIDTH = (blockWidth + blockHeight) / 2 - 20;
		
		//lineX = lineY = TEXT_WIDTH;
		start=new Point(0,0);
		end=new Point(0,0);
    bitmapList = new ArrayList<Bitmap>();
		nameList = new ArrayList<String>();
		firstList = new ArrayList<Integer>();
		//setGravity(Gravity.LEFT|Gravity.BOTTOM);
		data = new int[mapWidth][mapHeight];
	//	data2 = new long[mapHeight][mapWidth];
		dataList = new ArrayList<Integer>();
		pointList = new ArrayList<Point>();
		bitType = new HashMap<Bitmap,Integer>();
		typeBit = new HashMap<Integer,Bitmap>();
		

		//MainActivity.setParamsListener(this);
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		//screenWidth = windowManager.getDefaultDisplay().getWidth();
		//screenHeight = windowManager.getDefaultDisplay().getHeight();

		bitmapPaint = new Paint();
		paint = new Paint();
		 horText = new Paint();//水平方向文本
		 verText = new Paint();//垂直方向文本
		
		
		
		paint.setColor(0xfff0f0f0);
		paint.setTextSize(DisplayUtil. sp2px(context,16));
		paint.setAntiAlias(true);
		
		paramsChange(100,100,50,50);
		
	}
	
	private void update_mapItem()
	{
		itemWidth=(int)(blockWidth*zoom);
		itemHeight=(int)(blockHeight*zoom);
		//设置字体的大小
		horText.setTextSize((itemWidth + itemHeight) / 4);
		verText.setTextSize((itemWidth + itemHeight) / 4);
		horText.setColor(0xffcbcbcb);
		verText.setColor(0xffcbcbcb);

		horText.setAntiAlias(true);
		verText.setAntiAlias(true);
		verText.setTextAlign(Paint.Align.RIGHT);
		horText.setTextAlign(Paint.Align.CENTER);
		setPadding(itemWidth,itemHeight,itemWidth,itemHeight);
		
		invalidate();
	}

	public void setText(String text)
	{
		this.mText=text;
	}

	//修改地图尺寸参数
	@Override
	public void paramsChange(int mapWidth, int mapHeight, int blockWidth, int blockHeight) {
		// TODO: Implement this method
		if(mapWidth<=0 || mapHeight<=0) return;
		if(blockWidth<=0 || blockHeight<=0)return;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
		update_mapItem();
		setText("地图大小："+mapWidth+"×"+mapHeight);
		this.blockWidth = blockWidth;
		this.blockHeight = blockHeight;
		
		//将data数组进行复制
		int arr[][] = data.clone();
		//将所有arr数组拷贝到新的data数组
		data = new int[mapWidth][mapHeight];
	//	data2 = new long[mapHeight][mapWidth];
		int a = arr.length >= data.length ?data.length: arr.length;
		int b = arr[0].length >= data[0].length ?data[0].length: arr[0].length;
		for (int i=0;i < a;i++) {
			for (int j=0;j < b;j++) {
				data[i][j] = arr[i][j];
			}
		}

		//修改尺寸后，移除超出地图的点
		for (int i=pointList.size() - 1;i >= 0;i--) {
			if (pointList.get(i).x >= (mapWidth - 1) * blockWidth
				|| pointList.get(i).y >= (mapHeight - 1) * blockHeight) {
				pointList.remove(i);
				dataList.remove(i);
			}
		}
		step = next = pointList.size() - 1;
    
		
			//修改地图参数后，如果小矩形的坐标大于地图最后一格的坐标,则重新绘制小矩形，将其放在最后一格
			if (start.x >= mapWidth)
			{
				start.x =  (mapHeight - 1);
			}
			if(start.y >= mapHeight ) 
			{
				start.y =  (mapWidth - 1) ;
				
			}
		update_mapItem();
		invalidate();
	}

	//当拖动条移动时，改变网格的坐标
	@Override
	public void moveSeekBar(int moveX, int moveY) {
		// TODO: Implement this method
		if (moveY == -1) {
			//水平拖动条移动
			scrollTo( (moveX * blockWidth - getPaddingLeft()),getScrollY());
		}
		if (moveX == -1) {
			//垂直拖动条移动
			scrollTo(getScrollX(), (moveY * blockHeight - getPaddingTop()));
		}
		//correct();//调整坐标
		invalidate();
	}

	//获得图片的Bitmap对象，进行绘图
	@Override
	public void sendImage(Bitmap bitmap, int id) {
		// TODO: Implement this method
		this.bitmap = bitmap;
		bitType.put(bitmap, id);
		typeBit.put(id, bitmap);
	}

	//同一个点，保存不同的图片
	public void samePoint(int row, int col) {
		boolean bool = false;
		for (int i=step;i >= 0;i--) {
			if (i <= 0) {
				if(row<data.length && col<data[0].length)
				data[row][col] = 0;
			}
			for (int j=step - 1;j >= 0;j--) {
				if (pointList.get(i) == pointList.get(j)) {
					data[row][col] = dataList.get(j);
					bool = true;
				} else {
					data[row][col] = 0;
				}
				break;
			}
			if (bool == true) {
				break;
			}
		}
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect)
	{
		// TODO: Implement this method
		//super.onFocusChanged(focused, direction, previouslyFocusedRect);
		/*
		if(focused)
		{
			paint_rect.setColor(0xfff00000);
			Toast.makeText(context,"",0);
		}
		else
		{
			paint_rect.setColor(0xff000000);
		}
		
		invalidate();
		*/
	}
	
	

	//上一步 下一步操作
	@Override
	public void stepNext(Handler handler, int requestCode) {
		// TODO: Implement this method
		if (pointList.isEmpty()) {
			return;
		}

		switch (requestCode) {
			case 0://0表示上一步
				if (step == -1) {
					step = 0;
				} else if (step > pointList.size() - 1) {
					step = pointList.size() - 1;
				}
				int  row= pointList.get(step).x;//数组行数
				int  col= pointList.get(step).y ;//数组列数
				data[row][col] = 0;
				//samePoint(row, col);
				step--;
				if (step < 0) {
					//没有上一步，已经是第一步了
					Message msg = handler.obtainMessage(WITHOUT, requestCode);
					handler.sendMessage(msg);
					step = -1;
				}
				break;
			case 1://1表示下一步
				next = ++step;
				if (next > pointList.size() - 1) {
					next = pointList.size() - 1;
					//没有下一步，已经是最后一步了
					Message msg = handler.obtainMessage(WITHOUT, requestCode);
					handler.sendMessage(msg);
				}
				int  rows= pointList.get(next).x ;//数组行数
				int  cols= pointList.get(next).y ;//数组列数
				data[rows][cols] = dataList.get(next);
				break;
			case -1://-1表示地图重置
				//地图重置后，清空所有集合中数据
				data = new int[mapWidth][mapHeight];
				bitType.clear();
				typeBit.clear();
				pointList.clear();
				dataList.clear();
				break;
		}
		invalidate();
	}
	
	public List<Bitmap> getBitmapList()
	{
		return bitmapList;
	}
	
	public List<String> getNameList()
	{
		return nameList;
	}
	
	//打开tmx地图
	public void openMap(String filename)
	{
		int i;
		String text;
		int type=0;
		String head = null;
		String key = null;
		String word = null;
		int start=0;
		File file=new File(filename);
		int id=1;
		int ix=0,iy=0;
		int mapWidth=0;
		int mapHeight=0;
		int blockWidth=0;
		int blockHeight=0;
		init(context);
		try
		{
			text = ReadWriteFileWithEncode.read(filename, "UTF-8");
		}
		catch (IOException e)
		{
			toast("io错误",0);
			return ;
		}
		if (filename.endsWith(".tmx") || filename.endsWith(".TMX"))
		{
			for(i=0;i<text.length();i++)
			{
				char c= text.charAt(i);
		
				switch(type)
				{
					case 0:
						if(c=='<')
						{
							type=1;
						}
						break;
					case 1:
						if(c=='?')
						{
							
						}
						else if(c=='/')
						{
							type=11;
						}
						else if(c>='a' && c<='z')
						{
							start=i;
							type=2;
						}
						break;
						
					case 2: //head
						if(c==' ' || c=='\n' || c=='\t')
						{
							head= text.substring(start,i);
							type=3;
						}
						if(c=='>')
						{
							type=0;
						}
						break;
					case 3:
						if(c>='a' && c<='z')
						{
							start=i;
							type=4;
						}
						else if(c=='>')
						{
							type=0;
						}
						break;
					case 4: //key
						if(c=='=')
						{
							key=text.substring(start,i);
							type=5;
						}
						break;
					case 5://第一个冒号
						if(c=='\"')
						{
							start=i+1;
							type=6;
						}
						break;
					case 6: //第二个冒号
						if(c=='\"')
						{
							word=text.substring(start,i);
							type=3;
							
							//分析参数
							if(head.equals("map"))
							{
								if(key.equals("width"))
								{
									mapWidth=Str.atoi(word);
									if(mapWidth>0 && mapHeight>0 && blockWidth>=0 &&blockHeight>=0)
										paramsChange(mapWidth,mapHeight,blockWidth,blockHeight);
								}
								else if(key.equals("height"))
								{
									
									mapHeight=Str.atoi(word);
									if(mapWidth>0 && mapHeight>0 && blockWidth>=0 &&blockHeight>=0)
										paramsChange(mapWidth,mapHeight,blockWidth,blockHeight);
								}
								else if(key.equals("tilewidth"))
								{
									blockWidth=Str.atoi(word);
									if(mapWidth>0 && mapHeight>0 && blockWidth>=0 &&blockHeight>=0)
										paramsChange(mapWidth,mapHeight,blockWidth,blockHeight);
									
								}
								else if(key.equals("tileheight"))
								{
									blockHeight=Str.atoi(word);
									if(mapWidth>0 && mapHeight>0 && blockWidth>=0 &&blockHeight>=0)
										paramsChange(mapWidth,mapHeight,blockWidth,blockHeight);
									
								}
							}
							else if(head.equals("tileset"))
							{
								if(key.equals("tilewidth"))
								{
									blockWidth=Str.atoi(word);
								}
								else if(key.equals("tileheight"))
								{
									blockHeight=Str.atoi(word);
								}
							}
							
							else if(head.equals("image"))
							{
								if(key.equals("source"))
								{
									File bitmapfile=new File(word);
									//先检测绝对路径
									if(bitmapfile.isFile())
									{
										addBitmap(bitmapfile.getAbsolutePath());
									}
									else
									addBitmap(file.getParent()+"/"+word);
								}
								
							}
							else if(head.equals("tile"))
							{
								if(key.equals("gid"))
								{
									data[ix][iy]=Str.atoi(word);
									
									ix++;
									if(ix>=mapWidth)
									{
										ix=0;
										iy++;
									}
								}
							}
							
							
						}
						break;
					case 7:
						
						break;
					case 8:
						
						break;
					case 9:
						
						break;
					case 11: //跳过反括号
						if(c=='>')
						{
							type=0;
						}
						break;
						
				}
			}
		}
		//
		for(i=0;i<bitmapList.size();i++)
		{
			sendImage(bitmapList.get(i),i+1);
		}
		update_mapItem();
		invalidate();
	}
	//按指定宽高拆分bitmap
	ArrayList<Bitmap> clipBitmap(Bitmap bitmap,int itemw,int itemh)
	{
		ArrayList<Bitmap> temp=new ArrayList<Bitmap>();
		for(int y=0;y+itemh<=bitmap.getHeight();y+=itemh)
		{
			for(int x=0;x+itemw<=bitmap.getWidth();x+=itemw)
			{
				temp.add( Bitmap.createBitmap(bitmap,x,y,itemw,itemh));
			}
		}
		return temp;
	}
	
	
	//添加Bitmap，网格布局设置adapter
	public void addBitmap(String  filename) 
	{
		File file=new File(filename);
		Bitmap bitmap=null;
		try
		{
			 bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
		}
		catch(java.lang.OutOfMemoryError e)
		{
			//e.printStackTrace();
			return;
		}
			nameList.add(file.getAbsolutePath());
		firstList.add(gid);
		//判断图片是否大于图块
		if(bitmap.getWidth()>=blockWidth*2 || bitmap.getHeight()>=blockHeight*2)
		{
			ArrayList<Bitmap> temp=clipBitmap(bitmap, blockWidth,blockHeight);
			if(temp.size()<=0)Toast.makeText(context,"图块加载失败",0).show();
			else
			{
				for(int i=0;i<temp.size();i++)
				{
					bitmapList.add(temp.get(i));
					sendImage(getBitmapList()  .get(gid-1), gid);
					gid++;
				}
			}
		}
		else
		{
			bitmapList.add(bitmap);
			sendImage(getBitmapList()  .get(gid-1), gid);
      gid++;
    }
		
		
		//toast("addBitmap",0);
	//	adapter = new GridAdapter(context, bitmapList);
	//	gridView.setAdapter(adapter);
	}
	
	//获取数组
	public String toString()
	{
		StringBuffer buf=new StringBuffer();
		buf.append("[header]\r\nwidth="+mapWidth+"\r\n");
		buf.append("height="+mapHeight+"\r\n");
		buf.append("tilewidth="+blockWidth+"\r\n");
		buf.append("tileheight="+blockHeight+"\r\n");
		buf.append("orientation=orthogonal\r\n\r\n");
		buf.append("[tilesets]\r\n");
		for(int id=0;id<nameList.size();id++)
		{
			String name=nameList.get(id);
			File file=new File(name);
		buf.append("tileset="+file.getName()+","+blockWidth+","+blockHeight+","+"0,0\r\n");
		
		}
		buf.append("[layer]\r\n");
		buf.append("type=块层 1\r\n");
		buf.append("data=");
		for(int y=0;y<data[0].length;y++)
		{
		for(int x=0;x<data.length;x++)
		{
			buf.append(data[x][y]);
			if(y!=data[0].length-1|| x!=data.length-1)
			buf.append(",");
		}
		buf.append("\r\n");
		}
		return buf.toString();
	}
	
	//地图放大
	public int view_blowup()
	{
		zoom+=1;
		update_mapItem();
		return 0;
	}
	//地图缩小
	public int view_lessen()
	{
		zoom-=1;
		if(zoom<1)zoom=1;
		update_mapItem();
		return 0;
	}
	
	public void setDrawGrid(boolean isDraw)
	{
		this.isDrawGrid=isDraw;
		invalidate();
	}
	
	public void setDrawMark(boolean isDraw)
	{
		this.isDrawMark=isDraw;
		invalidate();
	}
	
	public String toArray()
	{
		StringBuffer buf=new StringBuffer();
		
		
		
		buf.append("int data["+mapWidth+"]["+mapHeight+"]=");
		buf.append("\n{\n");
		for(int x=0;x<data.length;x++)
		{
			buf.append(" {");
			for(int y=0;y<data[0].length;y++)
			{
				buf.append(data[x][y]);
				if(y!=data[0].length-1 || x!=data.length-1)
				{
					if(y!=data[0].length-1)
					buf.append(",");
					}
			}
			if(x!=data.length-1)
			buf.append(" },\r\n");
			else
				buf.append(" }\r\n");
		}
		buf.append("};");
		return buf.toString();
	}

	//保存地图
	@Override
	public void saveMap(String filename) {
		
		//把data数组中的行和列对调，保存在data2中(为什么要对调，jni中数组输出有点问题)
		/*for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				data2[j][i] = data[i][j];
			}
		}*/
		//Native.passParams(String.valueOf(mapWidth), String.valueOf(mapHeight)
				//		  , String.valueOf(blockWidth), String.valueOf(blockHeight));
     StringBuffer databuf =new StringBuffer();
		 File file_name=new File(filename);
		 //头信息
		 databuf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		 databuf.append("<map version=\"1.0\" orientation=\"orthogonal\" renderorder=\"right-down\" width=\""+mapWidth+"\" height=\""+mapHeight+"\" tilewidth=\""+blockWidth+ "\" tileheight=\""+blockHeight+"\" nextobjectid=\"1\">\r\n");
		 for(int id=0;id<nameList.size();id++)
		 {
			 String name=nameList.get(id);
			 File file=new File(name);
			 Bitmap bitmap = BitmapFactory.decodeFile(name);
			 if(!file_name.getParent().equals(file.getParent()))
			 {
			 CopyFile.copyFile(name,file_name.getParent()+"/"+file.getName());
			 Log.e("mapView","转移文件"+file_name.getAbsoluteFile()+file.getAbsoluteFile());
			 }
			 
			 
			 //图块信息
			 databuf.append(" <tileset firstgid=\""+firstList.get(id)+"\" name=\""+file.getName()+"\" tilewidth=\""+blockWidth+"\" tileheight=\""+blockHeight+"\">\r\n");
			 
		 //toast(name,0);
		 databuf.append("  <image source=\""+new File(name).getName()+"\" width=\""+bitmap.getWidth()+"\" height=\""+bitmap.getHeight()+"\"/>\r\n");
		 databuf.append(" </tileset>\r\n");
			
		 }
		 		 //图层
		 databuf.append(" <layer name=\"块层 1\" width=\""+mapWidth+"\" height=\""+ mapHeight+"\">\r\n");
		 //数据
		 databuf.append("  <data>\r\n");
		 for (int y = 0; y < data[0].length; y++) 
			for (int x = 0; x < data.length; x++) 
			{
				databuf.append("    <tile gid=\""+data[x][y]+"\"/>\r\n");
			}
		 databuf.append("  </data>\r\n");
		 databuf.append(" </layer>\r\n");
		 databuf.append("</map>\r\n");
		 try
{
ReadWriteFileWithEncode.write(file_name, databuf.toString(), "UTF-8");
}
catch (IOException e)
 {
	 
 }
		 toast("保存成功\n" + filename, 0);
		//new MapUtils(context).execute(data2, bitmapList,nameList);
	}


	@Override
	public void onDraw(Canvas canvas) {
		// TODO: Implement this method
		int scroll_x=getScrollX();
		int scroll_y=getScrollY();
		canvas.clipRect(scroll_x,scroll_y,scroll_x+ getWidth(),scroll_y+ getHeight());
		//绘制灰色的背景View
		//canvas.drawColor(0xff8a8a8a);
		//canvas.drawRect(new Rect(0,0,getMeasuredWidth()-1,getMeasuredHeight()-1),paint_rect);
		//canvas.drawRect(new Rect(0, 0, screenWidth - 50, screenHeight - 150), paint);
		
		//绘制游戏地图
		drawMap(canvas);

		//绘制网格
		if(isDrawGrid)
		drawGrid(canvas);
		//绘制黄色的小矩形
		drawSelectRect(canvas);
		//绘制滚动条
		drawScroll(canvas);
		//显示标尺
		if(isDrawMark)
		drawMark(canvas);
		
		
		//绘制文字
		canvas.drawText(mText,scroll_x+getPaddingLeft(),getHeight()-getPaddingBottom()+scroll_y,paint);
		super.onDraw(canvas);
	}

	//地图可见区域内绘制的线条数
	public void visualRangeLines(boolean bool) {
		/*
		if (bool == true) {
			//绘制地图
			mVerLines = Math.abs(lineX) / blockWidth;
			mHorLines = Math.abs(lineY) / blockHeight;
		} else {
			//绘制网格
			mVerLines = Math.abs((lineX - TEXT_WIDTH) / blockWidth);
			mHorLines = Math.abs((lineY - TEXT_WIDTH) / blockHeight);
		}

		viewVerLines = (getMeasuredWidth()) / blockWidth;
		viewHorLines = (getMeasuredHeight()) / blockHeight;
		//地图的X坐标在背景View的范围内
		if (TEXT_WIDTH + mapWidth * blockWidth <= getMeasuredWidth()) {
			mVerLines = 0;
			viewVerLines = mapWidth;
		} else {
			if (lineX + mapWidth * blockWidth <= getMeasuredWidth()) {
				viewVerLines = (lineX + mapWidth * blockWidth) / blockWidth;
			}
		}

		//地图的Y坐标在背景View的范围内
		if (TEXT_WIDTH + mapHeight * blockHeight <= getMeasuredHeight()) {
			mHorLines = 0;
			viewHorLines = mapHeight;
		} else {
			if (lineY + mapHeight * blockHeight <= getMeasuredHeight()) {
				viewHorLines = (lineY + TEXT_WIDTH + mapHeight * blockHeight) / blockHeight;	
			}
		}
		*/
	}

	//绘制游戏地图
	private void drawMap(Canvas canvas) {
		visualRangeLines(true);
		int scroll_x=getScrollX();
		int scroll_y=getScrollY();
		int left=getPaddingLeft();
		int top=getPaddingTop();
		
		int start_x=(scroll_x-left)/itemWidth;
		int start_y=(scroll_y-top)/itemHeight;
		int end_x=start_x+getWidth()/itemWidth+2;
		int end_y=start_y+getHeight()/itemHeight+2;
		end_x=Math.min(end_x,mapWidth);
		end_y=Math.min(end_y,mapHeight);
		start_x=Math.max(0,start_x);
		start_y=Math.max(0,start_y);
		
		Rect dst=new Rect();
		Rect src=new Rect();
		if (bitmap != null) {
			for (int x=start_x;x < end_x;x++) {
				for (int y= start_y;y < end_y;y++) {
					
					if (data[x][y] != 0) 
					{
						Bitmap bitmap=typeBit.get(data[x][y]);
						dst.set(left+ x * itemWidth ,  top+y * itemHeight, (int)(left+x*itemWidth+bitmap.getWidth()*zoom),(int)(top+y*itemHeight+bitmap.getHeight()*zoom));
						src.set(0,0,bitmap.getWidth(),bitmap.getHeight());
						canvas.drawBitmap(bitmap,src,dst,bitmapPaint);
					}
				
				}
			}
		}
	}

	//绘制网格
	private void drawGrid(Canvas canvas) {
		Paint horLine = new Paint();//水平线条
		Paint verLine = new Paint();//垂直线条
		int lineX = getPaddingLeft();
		int lineY = getPaddingTop();
    int scroll_x=getScrollX();
		int scroll_y=getScrollY();
		int x= (scroll_x-lineX)/itemWidth;
		int y=(scroll_y-lineY)/itemHeight;
		x=Math.max(0,x);
		y=Math.max(0,y);
		int x_max= Math.min(mapWidth,x+getWidth()/itemWidth+1);
		int y_max=Math.min(mapHeight, y+getHeight()/itemHeight+1);
		horLine.setColor(0xffcbcbcb);
		verLine.setColor(0xffcbcbcb);
    
		visualRangeLines(false);
		//绘制垂直线条，只绘制当前View可见区域的线条
		for (int i=x;i <= x_max ;i++) {
			
			canvas.drawLine(lineX + i * itemWidth, lineY, lineX + i * itemWidth, mapHeight * itemHeight + lineY, horLine);
		}
		//绘制水平线条，只绘制当前View可见区域的线条
		for (int j=y;j <= y_max ;j++) {
			
			canvas.drawLine(lineX, lineY + j * itemHeight, lineX + mapWidth * itemWidth, lineY + j * itemHeight, verLine);
		}

		//绘制黄色的小矩形
		drawSelectRect(canvas);
	}
	
	//显示标尺
	void drawMark(Canvas canvas)
	{
		
		int lineX = getPaddingLeft();
		int lineY = getPaddingTop();
    int scroll_x=getScrollX();
		int scroll_y=getScrollY();
		int x= (scroll_x-lineX)/itemWidth;
		int y=(scroll_y-lineY)/itemHeight;
		x=Math.max(0,x);
		y=Math.max(0,y);
		int x_max= Math.min(mapWidth,x+getWidth()/itemWidth+1);
		int y_max=Math.min(mapHeight, y+getHeight()/itemHeight+1);
		

		visualRangeLines(false);
		//绘制垂直线条，只绘制当前View可见区域的线条
		for (int i=x;i <= x_max ;i++) {
			canvas.drawText(String.valueOf(i), lineX + i * itemWidth, lineY-2 , horText);
			
		}
		//绘制水平线条，只绘制当前View可见区域的线条
		for (int j=y;j <= y_max ;j++) {
			if (j > 0) {
				canvas.drawText(String.valueOf(j), lineX, lineY + j * itemHeight-2, verText);
			}
			
		}

		;
	}


	//绘制黄色的小矩形
	private void drawSelectRect(Canvas canvas) {
		Paint rectPaint = new Paint();
		rectPaint.setColor(Color.YELLOW);
		rectPaint.setStrokeWidth(3);
		rectPaint.setStyle(Paint.Style.STROKE);

		canvas.drawRect(getPaddingLeft()+ start.x*itemWidth, getPaddingTop()+ start.y*itemHeight,getPaddingLeft()+ start.x*itemWidth+itemWidth, getPaddingTop()+ start.y*itemHeight+itemHeight, rectPaint);
	}
	
	//绘制滚动条
	private void drawScroll(Canvas canvas)
	{
		Paint paint_scroll;
		paint_scroll=new Paint();
		paint_scroll.setColor(0xffc0c0c0);
		int scroll_x=getScrollX();
		int scroll_y=getScrollY();
		int x_max=getScrollRangeX();
		int y_max=getScrollRangeY();
		int max_width=x_max+getWidth();
		int max_height=y_max+getHeight();
		int endx=scroll_x+getWidth();
		int endy=scroll_y+getHeight();
		int w=DisplayUtil. dip2px(context,4);
		//横向
		if(max_width>getWidth())
		canvas.drawRect(scroll_x+ getWidth()*scroll_x/max_width, scroll_y+ getHeight()-w-2,scroll_x+ getWidth()* endx/max_width,scroll_y+ getHeight()-2,paint_scroll);
		//纵向
		if(max_height>getHeight())
		canvas.drawRect(scroll_x+getWidth()-w-2, scroll_y+getHeight()*scroll_y/max_height, scroll_x+getWidth()-2, scroll_y+getHeight()*endy/max_height,paint_scroll);
	}

	//调整小矩形的坐标
	public boolean setStart(int x, int y) 
	{
		
		//X Y的坐标不能超出地图的宽度和高度(此时最后一点的X Y坐标)
		if (x <  mapWidth&& y <   mapHeight
			&& x >= 0 && y >= 0) 
		{
		  
			start.set(x,y);
			return true;
		} else {
			return false;
		}
	}
	int startX=0,startY=0 ;
	int moveX,moveY;
	
	boolean isMove;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO: Implement this method
		
		int x= (int)event.getX(0);
		int y=(int)event.getY(0);
		int x2=0,y2=0;
		int count=event.getPointerCount();
		int index;
		if(count>=2)
		{
			x2=(int)event.getX(1);
			y2=(int)event.getY(1);
		}
		int scroll_x,scroll_y;
		scroll_x=getScrollX();
		scroll_y=getScrollY();
		scroll_onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
						case MotionEvent.ACTION_POINTER_1_DOWN:
				startX=x;
				startY=y;
				moveX=x;
				moveY=y;
				isMove=false;
				//小矩形按下点与绘制线条的开始坐标间偏移量
				//offsetX = (int)(startX - lineX);
				//offsetY = (int)(startY - lineY);
				//调整小矩形的坐标
				//flag = setStart(startX+scroll_x, startY+scroll_y);
				
				invalidate();
                break;
						case MotionEvent.ACTION_POINTER_2_DOWN:
							isTouch=true;
							break;
            case MotionEvent.ACTION_MOVE:
				 index=event.findPointerIndex(1);
				
				if(index>=0)
				{
					isTouch=true;
							if (isEdit) 
							{
								if(isDelete)
									delete((x2+scroll_x -getPaddingLeft())/itemWidth,(y2+scroll_y- getPaddingTop())/itemHeight);
								else
								addPoint(bitmap,(x2+scroll_x -getPaddingLeft())/itemWidth,(y2+scroll_y- getPaddingTop())/itemHeight);
							}
				}
				
				if(event.findPointerIndex(0)>=0 && event.getPointerCount()==1)
				{
				if(!isMove)
				{
					if(x!=startX && y!=startY)
					{
					isMove=true;
					}
				}
				if(isMove)
				{
					int dx=x-moveX;
					int dy=y-moveY;
					scrollTo(getScrollX()-dx,getScrollY()-dy);
					moveX=x;
					moveY=y;
				}
				}
				//在网格的尺寸之内才能拖动，编辑模式下不能拖动网格
				if (flag == true && isEdit == false) {
					//lineX = (int)(endX - offsetX);
					//lineY = (int)(endY - offsetY);
				}
				//调整坐标
				//correct();//X Y不能越界
				
				//如果是编辑模式，则进行绘图
				/*
				if (MainActivity.editMode == true) 
				{
							addPoint(bitmap,(x+scroll_x -getPaddingLeft())/blockWidth,(y+scroll_y -getPaddingTop())/blockHeight);
				}
				*/
				invalidate();
                break;
						case MotionEvent.ACTION_POINTER_1_UP:
						//toast("1 up",0);
						isTouch=true;
							break;
						case MotionEvent.ACTION_POINTER_2_UP:
							isTouch=true;
						 index=event.findPointerIndex(1);

						if(index>=0)
						{
							isTouch=true;
							if (isEdit) 
							{
								if(isDelete)
									delete((x2+scroll_x -getPaddingLeft())/itemWidth,(y2+scroll_y- getPaddingTop())/itemHeight);
								else
									addPoint(bitmap,(x2+scroll_x -getPaddingLeft())/itemWidth,(y2+scroll_y- getPaddingTop())/itemHeight);
							}
						}
							break;
						
            case MotionEvent.ACTION_UP:
						
						//如果是编辑模式，则进行绘图
						setSelection((x+scroll_x -getPaddingLeft())/itemWidth,(y+scroll_y- getPaddingTop())/itemHeight);
						if(!isMove && !isTouch)
						{
						  if (isEdit) 
						  {
							
								if(isDelete)
								{
									delete((x+scroll_x -getPaddingLeft())/itemWidth,(y+scroll_y- getPaddingTop())/itemHeight);
									
									
								}
								else
								{
									addPoint(bitmap,(x+scroll_x -getPaddingLeft())/itemWidth,(y+scroll_y- getPaddingTop())/itemHeight);
								}
						  }
						  
						}
						
						
						
						isTouch=false;
                invalidate();
                break;
        }
		return true;
	}
	
	public void setEdit(boolean isEdit)
	{
		this.isEdit=isEdit;
	}
	
	public boolean isEdit()
	{
		return this.isEdit;
	}
	/*
	public void scrollTo(int x,int y)
	{
		//int lineX	=getPaddingLeft();
		//int lineY = getPaddingTop();
		int padding_w=getPaddingLeft()+getPaddingRight();
		int padding_h=getPaddingTop()+getPaddingBottom();
		if(x<0)x=0;
		if(y<0)y=0;
		
		if(blockWidth*mapWidth+padding_w> getWidth())
		if(x>blockWidth*mapWidth+padding_w-getWidth())x=blockWidth*mapWidth+padding_w-getWidth();
		if(blockHeight*mapHeight+padding_h>getHeight())
		if(y>blockHeight*mapHeight+padding_h-getHeight())y=blockHeight*mapHeight+padding_h-getHeight();
		super.scrollTo(x,y);
	}
	*/
	
	public void delete(int x,int y)
	{
		//setText(""+x+" "+y+ ""+mapWidth+" "+mapHeight);
		if(x<0 || y<0 || x>=mapWidth || y>=mapHeight)
			return;
		
		data[x][y]=0;
		invalidate();
	}
	
	public void setDelete(boolean isDelete)
	{
		this.isDelete=isDelete;
	}

	//添加坐标点
	public void addPoint(Bitmap bitmap,int x,int y) {
		if(x<0 || y<0 || x>=mapWidth || y>=mapHeight || bitmap==null)
			return;
		if (bitmap != null) {
			Point point = new Point(x,y);
			//point.x = rectX - lineX + TEXT_WIDTH;
			//point.y = rectY - lineY + TEXT_WIDTH;
			int  row= x;//数组行数
			int  col= y;//数组列数

			//修改坐标点的值，值表示图片类型的Bitmap对象
			if(row<mapWidth &&col<mapHeight)
			{
			data[row][col] = bitType.get(bitmap);
			//如果原来的X Y坐标或者图片类型与当前的值不相等，则添加至集合
			//防止多次添加同一个值，此处系统会多次调用addPoint方法
			if (srcX != point.x || srcY != point.y || srcData != data[row][col])
			{
				srcX = point.x;
				srcY = point.y;
				srcData = (int)data[row][col];
				pointList.add(point);
				dataList.add((int)data[row][col]);
				step = next = pointList.size() - 1;
				setStart(point.x,point.y);
			} 
			}
		}
	}
	
	public void moveMap(int movex,int movey)
	{
		int startx,starty;
		int x,y;
		if(Math.abs(movex)>=mapWidth || Math.abs(movey)>=mapHeight)
			return;
		if(movex>0)
		startx=mapWidth-movex-1;
		else
			startx=-movex;
		
		if(movey>0)
		starty=mapHeight-movey-1;
		else
			starty=-movey;
		if(startx<0 || starty<0)//超出地图范围，清空地图
			{
				for(int i=0;i<data.length;i++)
				for(int j=0;j<data[0].length;j++)
				{
				data[i][j]=0;
				}
				invalidate();
				return;
			}
		if(movex>0)
		{
			
			
			for(y=0;y<mapHeight;y++)
		  for( x=startx;x>=0;x--)
		  {
			data[x+movex][y]=data[x][y];
		  }
			
			//将前面清空
			
			
			for(y=0;y<mapHeight;y++)
			for(x=0;x<movex;x++)
			{
				data[x][y]=0;
			}
		}
		else if(movex<0)
		{
			for(y=0;y<mapHeight;y++)
			for(x=startx;x<mapWidth;x++)
			data[x+movex][y]=data[x][y];
			
			//将后面清空
			for(y=0;y<mapHeight;y++)
			for(x=mapWidth+movex;x<mapWidth;x++)
			data[x][y]=0;
			
		}
		
		
		if(movey>0)
		{
			for(x=0;x<mapWidth;x++)
				for( y=starty;y>=0;y--)
				{
					data[x][y+movey]=data[x][y];
				}
				
			for(x=0;x<mapWidth;x++)
				for(y=0;y<movey;y++)
				{
					data[x][y]=0;
				}
		}
		else if(movey<0)
		{
			for(x=0;x<mapWidth;x++)
				for(y=starty;y<mapHeight;y++)
					data[x][y+movey]=data[x][y];

			//将后面清空
			for(x=0;x<mapWidth;x++)
				for(y=mapHeight+movey;y<mapHeight;y++)
					data[x][y]=0;
		}
		invalidate();
	}

	public void setSelection(int startx,int starty, int endx,int endy) 
	{
		this.start.set(startx,starty);
		this.end.set(endx,endy);
		invalidate();
	}

	public boolean setSelection(int x,int y) 
	{
		if (x <  mapWidth&& y <   mapHeight
				&& x >= 0 && y >= 0) 
		{

			start.set(x,y);
			invalidate();
			return true;
		} else {
			return false;
		}
		
	}
	
	public Point getSelectionStart()
	{
		return this.start;
	}
	
	public Point getSelectionEnd()
	{
		return this.end;
	}
	
	public int getBlockWidth()
	{
		return blockWidth;
	}
	
	public int getBlockHeight()
	{
		return blockHeight;
	}
	
	
	public int getMapWidth()
	{
		return mapWidth;
	}
	
	
	public int getMapHeight()
	{
		return mapHeight;
	}

	//坐标调整(去掉此方法网格可以任意的拖动)
	/*
	public void correct() {
		
		//地图的尺寸大于背景View的尺寸
		if (TEXT_WIDTH + blockWidth * mapWidth > getMeasuredWidth()
			|| TEXT_WIDTH + blockHeight * mapHeight > getMeasuredHeight()) {
			//开始坐标只能左移或者上移
			if (lineX > TEXT_WIDTH) {
				lineX = TEXT_WIDTH;
			}

			if (lineY > TEXT_WIDTH) {
				lineY = TEXT_WIDTH;
			}

			//地图宽度大于背景View的高宽度
			if (TEXT_WIDTH + mapWidth * blockWidth > getMeasuredWidth()) {
				//结束点坐标只能向右或者下移
				if (lineX <= -(TEXT_WIDTH + blockWidth * mapWidth - (getMeasuredWidth()))) {
					//地图宽度大于背景View的宽度
					lineX = -(TEXT_WIDTH + blockWidth * mapWidth - (getMeasuredWidth()));
				}
			} else {
				lineX = TEXT_WIDTH;
			}

			//地图高度大于背景View的高度
			if (TEXT_WIDTH + mapHeight * blockHeight > getMeasuredHeight()) {
				if (lineY <= -(TEXT_WIDTH + blockHeight * mapHeight - (getMeasuredHeight()))) {
					lineY = -(TEXT_WIDTH + blockHeight * mapHeight - (getMeasuredHeight()));
				}
			} else {
				lineY = TEXT_WIDTH;
			}

		} else {
			//地图的尺寸在背景View的范围之内
			if (lineX <= TEXT_WIDTH) {
				lineX = TEXT_WIDTH;
			}

			if (lineY <= TEXT_WIDTH) {
				lineY = TEXT_WIDTH;
			}

			if (lineX >= getMeasuredWidth() - (TEXT_WIDTH + blockWidth * mapWidth)) {
				lineX = getMeasuredWidth()  - (TEXT_WIDTH + blockWidth * mapWidth);
			}

			if (lineY >= getMeasuredHeight()  - (TEXT_WIDTH + blockHeight * mapHeight)) {
				lineY = getMeasuredHeight() - (TEXT_WIDTH + blockHeight * mapHeight);
			}
		}
	}
	*/
	public void toast(String text,int type)
	{
		Toast.makeText(context,text,type).show();
	}

	
	
	//boolean isMove;
	private static final int INVALID_POINTER = -1;
	private static final float OVER_MOVE_SCALE = 0.25f;

	private int mOverScrollDistance = 100;
	private int mOverflingDistance = mOverScrollDistance/2;

	private float lastX,lastY;
	private int mActivePointerId;

	private boolean mIsBeingDragged;

	private VelocityTracker mVelocityTracker;
	private OverScroller mScroller;
	private int mMinimumVelocity;
	private int mMaximumVelocity;
	private int mTouchSlop;

	boolean isHor;

	@Override
	public void scrollTo(int x, int y)
	{
		// TODO: Implement this method
		//if(y<0)debug(""+y);
		if(x<0)x=0;
		if(y<0)y=0;

		int width=getScrollRangeX();
		int height=getScrollRangeY();
		if(x>width)x=width;
		if(y>height)y=height;
		super.scrollTo(x, y);
	} //横向滑动条

	/*
	 public MyScrollerView3(Context context, AttributeSet attrs, int defStyle) {
	 super(context, attrs, defStyle);
	 initScrollView();
	 }

	 public MyScrollerView3(Context context, AttributeSet attrs) {
	 this(context, attrs, -1);
	 }

	 public MyScrollerView3(Context context) {
	 this(context, null);
	 }
	 */




	boolean isFling;

//	@SuppressLint({ "ClickableViewAccessibility"})
	int hx,hy;
	public boolean scroll_onTouchEvent(MotionEvent event) {
		initVelocityTrackerIfNotExists();
		mVelocityTracker.addMovement(event);

		final int action = event.getAction();

		switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN: 
				isMove=false;
				mScroller.forceFinished(true);
				isFling=false;

				// Remember where the motion event started
				lastX = (int) event.getX();
				lastY = (int)event.getY();
				hx=(int)lastX;
				hy=(int)lastY;
				mActivePointerId = event.getPointerId(0);
				break;

			case MotionEvent.ACTION_MOVE:

				//float x=event.getRawX();
				//float y=event.getRawY();


				final int activePointerIndex = event
					.findPointerIndex(mActivePointerId);
				if(activePointerIndex>=0)
				{
				final int x = (int)event.getX(activePointerIndex);
				final int y= (int)event.getY(activePointerIndex);

				int deltaX = (int) (hx - x);
        int deltaY = (int)(hy-y);
				if (!mIsBeingDragged) {
					isFling=true;
					// 判断是否为一个可用move的依据
					if (Math.abs(deltaX) > mTouchSlop) {
						mIsBeingDragged = true;
					}
					if(Math.abs(deltaY)>mTouchSlop)
					{
						mIsBeingDragged = true;
					}

				}

				if (mIsBeingDragged) {
					if(!isMove)
					{
						isMove=true;
						if(Math.abs(x-lastX)>Math.abs(y-lastY)) //横向滑动
						{

							isHor=true;
						}
						else //纵向滑动
						{
							isHor=false;
						}
					}
					// Scroll to follow the motion event
					hx = x;
          hy = y;
					// over scroll时 模拟一个难以拖动的效果
					if(getScrollX() <= 0 || getScrollX() >= getScrollRangeX()) {
						deltaX *= OVER_MOVE_SCALE;
					}

					int oldScrollX = getScrollX();
					int oldScrollY = getScrollY();
					int scrollX = oldScrollX + deltaX;
					int scrollY = oldScrollY + deltaY;
          if(scrollX<0)scrollX=0;
					//overScrollBy(deltaX, 0, (int)scrollX, getScrollY(), getScrollRangeX(), 0, mOverScrollDistance, 0, true);
					/*
					 if(isHor)
					 scrollTo(scrollX, getScrollY());
					 else
					 scrollTo(getScrollX(), scrollY);
					 */
				}
				}
				break;
			case MotionEvent.ACTION_UP:
				//isMove=false;
				if (mIsBeingDragged) {
					final VelocityTracker velocityTracker = mVelocityTracker;
					if(isMove)
					{
					velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);

					int initialVelocity;
					if(isHor)
						initialVelocity=  (int) velocityTracker
							.getXVelocity(mActivePointerId);
          else
						initialVelocity=  (int) velocityTracker
							.getYVelocity(mActivePointerId);

					// 速度超过某个阀值时才视为fling
					if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
						fling(-initialVelocity);
					} else {
						// 没有fling时,自定义回弹
						/*
						 if(isHor)
						 {
						 if (mScroller.springBack(getScrollX(), getScrollY(), 
						 0, getScrollRangeX(), 0, 0)) {
						 postInvalidate();
						 }
						 }
						 else
						 {
						 if (mScroller.springBack(getScrollX(), getScrollY(), 
						 0, 0, 0, getScrollRangeY())) {
						 postInvalidate();
						 }
						 }
						 */
					}
					}

					mActivePointerId = INVALID_POINTER;
					endDrag();
					
				}
				break;
		}
		return true;
	}

	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
																boolean clampedY) {
		// 模拟"扯断效果",即拉到over scroll边界时,弹回去
		/*
		 if(isHor)
		 {
		 if(clampedX) {

		 scrollTo(0, scrollY);

		 //mScroller.springBack(getScrollX(), getScrollY(), 0, getScrollRangeX(), 0, 0);
		 postInvalidate();
		 } 
		 }
		 else
		 {
		 if(clampedY) {


		 scrollTo(scrollX,0);
		 //mScroller.springBack(getScrollX(), getScrollY(), 0, getScrollRangeX(), 0, 0);
		 postInvalidate();
		 } 

		 }


		 */


	}

	private int getScrollRangeX() {
		int scrollRange = 0;
		scrollRange = Math.max(0, itemWidth*mapWidth+getPaddingLeft()+getPaddingRight() -getWidth());
		return scrollRange;
	}
	private int getScrollRangeY() {
		int scrollRange = 0;


		scrollRange = Math.max(0, itemHeight*mapHeight+ getPaddingTop() + getPaddingBottom() -getHeight());

		return scrollRange;
	}

	private void endDrag() {
		mIsBeingDragged = false;

		recycleVelocityTracker();
	}

	private void recycleVelocityTracker() {
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	/**
	 * Fling the scroll view
	 * 
	 * @param velocityX
	 *            The initial velocitX in the X direction. Positive numbers mean
	 *            that the finger/cursor is moving down the screen, which means
	 *            we want to scroll towards the top.
	 */
	public void fling(int velocityX) {
		if (isFling) {

			if(isHor)
			{
				
				 mScroller.fling(getScrollX(), getScrollY(), 
				 velocityX, 0, 

				 0, getScrollRangeX(),
				 0, getScrollRangeY(),

				 mOverflingDistance, 0);
				 //else 
				 //scrollTo(0,getScrollY());
				 
			}
			else
			{

				mScroller.fling(getScrollX(), getScrollY(), 
												0, velocityX, 
												0, getScrollRangeX(),
												0, getScrollRangeY(),
												0, mOverflingDistance);
				//else
				//scrollTo(getScrollX(),0);
			}

			invalidate();
		}
	}

	@Override
	public void computeScroll() {
		if(isFling)
		{
			if (mScroller.computeScrollOffset()) {
				int oldX = getScrollX();
				int oldY = getScrollY();
				int x = mScroller.getCurrX();
				int y = mScroller.getCurrY();

				if(x<0 || y<0)mScroller.forceFinished(true);

				/*
				 int width=getScrollRangeX();
				 int height=getScrollRangeY();
				 if(x>width)x=width;
				 if(y>height)y=height;
				 */
				if (oldX != x || oldY != y) 
				{
					scrollTo(x, y);
					invalidate();
				}

				// Keep on drawing until the animation has finished.

				return;
			}
		}
	}

	private void initScrollView() {
		mScroller = new OverScroller(getContext());
		final ViewConfiguration configuration = ViewConfiguration
			.get(getContext());
		//mTouchSlop = ViewConfigurationCompat
		//		.getScaledPagingTouchSlop(configuration);
		mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
		setOverScrollMode(OVER_SCROLL_ALWAYS);
		mTouchSlop = 1;
	}

	private void initVelocityTrackerIfNotExists() {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
	}
	
	public void onSave(Bundle outState)
	{
		ArrayList<Bitmap> bitmapList;//保存选择的图片的Bitmap
		/*
		Parcel parcel=new Parcel();
		parcel.writeValue(data);
		*/
		/*
		 Logcat.e("onSaveInstanceState");
		 outState.putString("filepath", ProjectPath);
		 outState.putString("filename", filename);
		 outState.putString("coding", coding);
		 */
		int data[]=new int[mapWidth*mapHeight];
		//填充
		for(int x=0;x<this.data.length;x++)
		for(int y=0;y<this.data[0].length;y++)
		{
			data[y*mapWidth+x]=this.data[x][y];
		}
		outState.putInt("mapWidth",mapWidth);
		outState.putInt("mapHeight",mapHeight);
		outState.putStringArrayList("xl_nameList",nameList);
		//outState.putIntegerArrayList("firstList",firstList);
		outState.putIntArray("data",data);
		outState.putFloat("zoom",zoom);
		//super.onSaveInstanceState(outState);
	}

	@Override
	public void onRestore(Bundle savedInstanceState)
	{
		ArrayList<String> nameList= savedInstanceState.getStringArrayList("xl_nameList");
		//firstList = savedInstanceState.getIntegerArrayList("firstList");
		mapWidth=savedInstanceState.getInt("mapWidth");
		mapHeight=savedInstanceState.getInt("mapHeight");
		zoom=savedInstanceState.getFloat("zoom");
		this.data=new int[mapWidth][mapHeight];
		int data[]=savedInstanceState.getIntArray("data");
		for(int x=0;x<mapWidth;x++)
		for(int y=0;y<mapHeight;y++)
		this.data[x][y]=data[y*mapWidth+x];
		//bitmapList=new ArrayList<Bitmap>();
		Log.e("mapView","nameList "+nameList.size());
		
		
		for(int i=0;i<nameList.size();i++)
		{
			String name=nameList.get(i);
			//Log.e("mapview",name);
			addBitmap(name);
		  
		}
		update_mapItem();
		/*
		 ProjectPath=savedInstanceState.getString("filepath");
		 filename=savedInstanceState.getString("filename");
		 coding=savedInstanceState.getString("coding");
		 String path= sdPath+ ProjectPath+"/"+filename;
		 editfile=new editFile();
		 editfile.setFile(new File(path));

		 if(ProjectPath!=null)
		 {


		 //EmuPath.PROJECT_DIR=ProjectPath;

		 }

		 setTitle(filename);

		 // TODO: Implement this method
		 super.onRestoreInstanceState(savedInstanceState);

		 toast("异常恢复成功"+ProjectPath);
		 */
	}
}

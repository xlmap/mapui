package com.map.ui;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.database.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.text.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;
import android.widget.SeekBar.*;
import com.map.*;
import com.map.view.*;
import java.io.*;
import java.util.*;
import com.baidu.appx.BDInterstitialAd;
import com.baidu.appx.BDInterstitialAd;

import android.view.View.OnClickListener;
import java.lang.Process;

//import static com.nineoldandroids.view.ViewPropertyAnimator.animate;
import com.xl.filelist.FileListDialog;
import com.map.tool.*;
import com.pgyersdk.feedback.*;
import com.pgyersdk.update.*;
import com.baidu.appx.BDBannerAd;
import com.baidu.appx.BDBannerAd.BannerAdListener;
import android.view.*;
import android.util.*;
import android.widget.*;


/*
 *程序为ndk工程，主要是为了方便使用c/c++来开发
 *本程序完全由Liuzhiyong所开发
 *作者：Liuzhiyong
 *邮箱：975434530@qq.com
 */

public class MainActivity extends Activity {

 private xlApplication application;
	private MapView mapView;//绘制地图View
	
	private Button menuButton,stepButton;//菜单 上一步按钮
	private Button nextButton;//下一步按钮
	Button menu_file,menu_view,menu_edit,menu_c;
	Button fileButton,editBitton,viewButton,menu_map;
	Button btn_setting;

	private ImageView imageButton,selectImage;//icon图标 选中的图片
	private GridView gridView;//定义网格布局
	private Button mapMode,saveButton;//图片尺寸 保存按钮

	private PopupMenu popupMenu;//弹出菜单
	private Dialog selectDialog;//图片选择对话框
	/*
	private List<Bitmap> bitmapList;//保存选择的图片的Bitmap
	private List<String> nameList;//保存选择的图片名称
  private List<Integer> firstList; //起始id
	*/
	private GridAdapter adapter;//网格布局适配器
	//public static boolean editMode = false;//判断是否为编辑模式
	private SeekBar horSeekBar,verSeekBar;//水平垂直方向的拖动条

	private int blockWidth,blockHeight,mapWidth,mapHeight;	//定义地图块的大小 地图尺寸
	boolean isDrawGird,isDrawMark;
	
	private static ParamsChangeListener paramsLis;

	private static final int SELECT_PIC_KITKAT = 1;

	private static final int SELECT_PIC = 0;
    private static final int EX_SETTING = 100;
	String filepath;
	String SDCard="mnt/sdcard/";
	String filename="1.tmx";
	
	//地图完整路径
	String mapfilename;

	private static Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO: Implement this method
			super.handleMessage(msg);
			switch (msg.what) {
				case VerticalSeekBar.SET:
					paramsLis.moveSeekBar(-1, Integer.parseInt(msg.obj.toString()));
					//Toast.makeText(MainActivity.this, "Y："+msg.obj.toString(), 50).show();
					break;
			}
		}
	};

	private static final int REQUEST_EX = 1000;

	private static final int REQUEST_TMX = 1001;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    
		application = (xlApplication)getApplication();
		filepath = application.filePath;
			//KEY：Quh0lsMWFGgzQmde1kMpAPhm
	//	new BDInterstitialAd(this,"apikey","id");
		
			blockWidth = blockHeight = application.blackSize;//默认的地图块大小
			mapWidth = mapHeight = 100;//默认的地图尺寸
			isDrawGird=true;
			isDrawMark=true;
			setTheme( application.theme);
			
		requestWindowFeature(Window.FEATURE_NO_TITLE);//设置无标题
			setRequestedOrientation(application.orientation);
		//全屏显示
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        
		
				
			
		initView();
			super.onCreate(savedInstanceState);
		Toast.makeText(this,getResources().getString(R.string.hello),0).show();
    }


	public void setParamsListener(ParamsChangeListener mParamsLis) {
		paramsLis = mParamsLis;
	}

	//组件初始化
	public void initView() {
		
		setContentView(R.layout.main);
		//检查更新
		PgyUpdateManager.register(this);
		menuButton = (Button) super.findViewById(R.id.menuFile);
		fileButton = (Button)findViewById(R.id.menuFile);
		menu_map = (Button)findViewById(R.id.menuMap);
		
		
		nextButton = (Button) super.findViewById(R.id.nextButton);
		stepButton = (Button) super.findViewById(R.id.stepButton);
		mapMode = (Button) super.findViewById(R.id.mapMode);
		saveButton = (Button) super.findViewById(R.id.saveButton);
		//imageButton = (ImageView) super.findViewById(R.id.imageButton);
		gridView =  (GridView) super.findViewById(R.id.gridView);
		selectImage = (ImageView) super.findViewById(R.id.selectImage);
		mapView = (MapView) super.findViewById(R.id.view_map);
		mapView.paramsChange(this.mapWidth, this.mapHeight, this.blockWidth, this.blockHeight);
		mapView.setDrawGrid(isDrawGird);
		mapView.setDrawMark(isDrawMark);
		
		horSeekBar = (SeekBar) super.findViewById(R.id.horSeekBar);
		verSeekBar = (SeekBar) super.findViewById(R.id.verSeekBar);

		menu_file = (Button)findViewById(R.id.menuFile);
		menu_edit= (Button)findViewById(R.id.menuEdit);
		menu_view = (Button)findViewById(R.id.menuView);
		menu_c = (Button)findViewById(R.id.menuC);
		btn_setting = (Button)findViewById(R.id.setting);
		
		paramsLis = (ParamsChangeListener)findViewById(R.id.view_map);
		
		
		menu_file.setOnClickListener(clickLis);
		menu_edit.setOnClickListener(clickLis);
		menu_view.setOnClickListener(clickLis);
		menu_c.setOnClickListener(clickLis);
		
		menuButton.setOnClickListener(clickLis);
		fileButton.setOnClickListener(clickLis);
		menu_map.setOnClickListener(clickLis);
		
		
		stepButton.setOnClickListener(clickLis);
		nextButton.setOnClickListener(clickLis);
		mapMode.setOnClickListener(clickLis);
		saveButton.setOnClickListener(clickLis);
		//imageButton.setOnClickListener(clickLis);
		gridView.setOnItemClickListener(itemLis);
		btn_setting.setOnClickListener(clickLis);
		
		horSeekBar.setOnSeekBarChangeListener(seekbarLis);
		verSeekBar.setMax(100);
		horSeekBar.setMax(100);
		VerticalSeekBar.setHandler(handler);
		SDCard = getSDPath();
		//添加广告视图
		LinearLayout layout= (LinearLayout)findViewById(R.id.layout_adView);
		addAdView(layout);
		
	}


	//Button按钮监听处理
	private OnClickListener clickLis = new OnClickListener(){

      Intent intent=null;

      
		@Override
		public void onClick(View v) {
			// TODO: Implement this method
			switch (v.getId()) {
				
				case R.id.menuFile://菜单按钮
					showPopupMenu(1);
					break;
				case R.id.menuEdit://编辑
					showPopupMenu(2);
					break;
				case R.id.menuView://视图
					showPopupMenu(3);
					break;
				case R.id.menuMap://地图
					showPopupMenu(4);
					break;
				case R.id.menuC:
					showPopupMenu(5);
					break;
				case R.id.stepButton://上一步
					paramsLis.stepNext(handler, 0);
					break;
				case R.id.nextButton://下一步
					paramsLis.stepNext(handler, 1);
					break;
				case R.id.mapMode://地图模式
					setEdit();
					break;
				case R.id.saveButton://地图保存
				  
					saveMap();
					break;
				case R.id.album_load://相册加载
					break;
				case R.id.gallery_load://图库加载
					selectDialog.dismiss();
					galleryImage();
					break;
				case R.id.file_load://文件加载
					break;
				case R.id.setting: //设置
					intent=new Intent(MainActivity.this,SetActivity.class);
					startActivityForResult(intent,EX_SETTING);
			}
		}
	};
	
	
	void setEdit()
	{
		if (mapView.isEdit() == false) {
			mapView.setEdit( true);
			mapMode.setText("查看模式");
		} else {
			mapView.setEdit(false);
			mapMode.setText("编辑模式");
		}
	}


	//GridView的item单击监听处理
	private OnItemClickListener itemLis = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View convertView, int position, long id) {
			// TODO: Implement this method
			selectImage.setImageBitmap(mapView.getBitmapList()  .get(position));
			paramsLis.sendImage(mapView.getBitmapList()  .get(position), position + 1);
			mapView.setEdit( true);
			mapView.setDelete(false);
			mapMode.setText("查看模式");
		}

	};
	


	//显示弹出菜单
	public void showPopupMenu(int resId) {
	  switch(resId)
	  {
		
	  case 1:
		popupMenu = new PopupMenu(MainActivity.this, menu_file);
		popupMenu.getMenu().add(0,0,1,"新建");
	  popupMenu.getMenu().add(0,1,1,"打开");
	 // popupMenu.getMenu().add(0,2,1,"最近的文件");
	  popupMenu.getMenu().add(0,3,1,"保存");
	  popupMenu.getMenu().add(0,4,1,"另存为");
	  popupMenu.getMenu().add(0,5,1,"导出为文本");
		popupMenu.getMenu().add(0,6,1,"导出为数组");
	 // popupMenu.getMenu().add(0,6,1,"Export As Image");
	  popupMenu.getMenu().add(0,7,1,"退出");
	  break;
	  case 2:
				popupMenu = new PopupMenu(MainActivity.this, menu_edit);
				popupMenu.getMenu().add(0,10,1,"撤销");
				popupMenu.getMenu().add(0,11,1,"恢复");
				//popupMenu.getMenu().add(0,12,1,"选择");
				//popupMenu.getMenu().add(0,13,1,"复制");
				//popupMenu.getMenu().add(0,14,1,"粘贴");
				popupMenu.getMenu().add(0,15,1,"删除");
		break;
		
	  case 3:
				popupMenu = new PopupMenu(MainActivity.this, menu_view);
				popupMenu.getMenu().add(0,20,1,"显示网格");
				popupMenu.getMenu().add(0,21,1,"显示标尺");
				//popupMenu.getMenu().add(0,22,1,"当前层高亮");
				popupMenu.getMenu().add(0,23,1,"放大");
				popupMenu.getMenu().add(0,24,1,"缩小");
				
	    break;
	  case 4:
				popupMenu = new PopupMenu(MainActivity.this, menu_map);
				popupMenu.getMenu().add(0,30,1,"新图块");
				popupMenu.getMenu().add(0,31,1,"调整地图大小");
				popupMenu.getMenu().add(0,32,1,"挪动地图");
				
				
		break;
		
		case 5:
				//popupMenu = new PopupMenu(MainActivity.this, menu_c);
				//popupMenu.getMenu().add(0,40,1,"添加图层");
				//popupMenu.getMenu().add(0,41,1,"添加对象层");
				//popupMenu.getMenu().add(0,42,1,"增加图像图层");
				//popupMenu.getMenu().add(0,43,1,"图层列表");
				//popupMenu.getMenu().add(0,44,1,"删除图层");
				//popupMenu.getMenu().add(0,45,1,"前置图层");
				//popupMenu.getMenu().add(0,46,1,"后置图层");
				
	  }
	  
		//getMenuInflater().inflate(resId, popupMenu.getMenu());
		popupMenu.setOnMenuItemClickListener(menuLis);
		popupMenu.show();
		
		
	}
	
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
	// TODO: Implement this method

	menu.add(0,0,1,"新建");
	menu.add(0,1,1,"打开");
	menu.add(0,2,1,"保存");
	menu.add(0,3,1,"另存为");
	menu.add(0,4,1,"检查更新");

	menu.add(0,5,1,"帮助");
	menu.add(0,6,1,"关于");


	return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
	// TODO: Implement this method
	switch(item.getItemId())
	{
	  case 0://新建
		newMapFileDialog();
		break;
	  case 1://打开
		openFile();
		//createEdit("请输入文件名",filename);
		break;
	  case 2://保存
		saveMap();
		break;
	  case 3://另存为
		saveDialog();
		break;
	  case 4://检查更新
		download();
		break;
	  case 5://帮助
		Intent intent = new Intent(this,HelpActivity.class);
		startActivity(intent);
		break;
	  case 6://关于
		Intent intent_about = new Intent(this,AboutActivity.class);
		startActivity(intent_about);
		break;
	}



	return super.onOptionsItemSelected(item);
  }
  //弹出菜单监听处理
	private PopupMenu.OnMenuItemClickListener menuLis = new PopupMenu.OnMenuItemClickListener(){

		@Override
		public boolean onMenuItemClick(MenuItem item) {
			// TODO: Implement this method
			switch (item.getItemId()) {
				case 0: //新建
				newMapFileDialog();
				break;
				case 1://打开图片
					openFile();
					break;
				case 3://保存
				saveMap();
				break;
				case 4: //另存为
					saveDialog();
					break;
				case 5:
					saveTxt(SDCard+filepath+ "/"+"1.txt","txt");
					break;
				case 6:
						saveTxt(SDCard+filepath+ "/"+"1.txt","array");
				  break;
				case R.id.map_reset://地图重置
					paramsLis.stepNext(handler, -1);
					break;
				case R.id.image_size://图片大小
					break;
				case 7://程序退出
					finish();
					break;
				case 10: 	//上一步
					paramsLis.stepNext(handler, 0);
					break;
					
				case 11://下一步
					paramsLis.stepNext(handler, 1);
					break;
				case 15: //删除
				  mapView.setDelete(true);
					mapView.setEdit(true);
					mapMode.setText("查看模式");
					selectImage.setImageBitmap(null);
				  break;
				case R.id.game_example://游戏示例
					break;
				case R.id.about_help://关于帮助
					break;
				case R.id.app_list://应用推荐
					break;
				case 20: //显示网格
				if(isDrawGird)
					isDrawGird=false;
				else isDrawGird=true;
				mapView.setDrawGrid(isDrawGird);
					break;
				case 21: //显示标尺
				if(isDrawMark)
					isDrawMark=false;
				else isDrawMark=true;
				mapView.setDrawMark(isDrawMark);
					break;
				case 22: //当前层高亮
				
					break;
				case 23: //放大
				mapView.view_blowup();
					break;
				case 24: //缩小
				mapView.view_lessen();
					break;
				case 30://新图块
				  openfilelist();
				break;
				case 31://地图设置
					settingMapDialog();
					break;
				case 32: //挪动地图
				moveMapDialog();
				break;
			}
			return false;
		}
	};
  
  
  
	
	public static String getSDPath()
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
	
	
	public void openFile()
	{
		//传送
		Intent intent = new Intent();
		intent.putExtra("explorer_title", "打开tmx地图");
		intent.putExtra("homepath", SDCard);
		intent.putExtra("returnFile",".tmx|.TMX");//设置返回类型
		intent.putExtra("filepath", filepath/*EmuPath.getProjectDir()*/);
		//intent.setDataAndType(Uri.fromFile(new File(filepath)), "*/*");
		intent.setClass(this, FileListActivity.class);
		startActivityForResult(intent, REQUEST_TMX);
		
	}
	
	//保存地图
	void saveMap()
	{
		if(mapfilename!=null)
		{
			paramsLis.saveMap(mapfilename);
		}
		else
			paramsLis.saveMap(SDCard+filepath+"/"+filename);
		
	}
	
	void saveTxt(String filename,final String type)
	{
		final EditText edit = new EditText(MainActivity.this);
		if(filename!=null)
		{
			edit.setText(filename);
		}
		else
			edit.setText(mapfilename);
		Dialog dialog = new AlertDialog.Builder(this)
			.setTitle("另存为")
			.setView(edit)
			.setPositiveButton("确定", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO: Implement this method
					dialog.dismiss();
					String name=edit.getText().toString();
					if(type.equals("txt"))
					{
						
					try
					{
						ReadWriteFileWithEncode.write(new File(name), mapView.toString(), "UTF-8");
						toast("保存成功\n"+name,1);
					}
					catch (IOException e)
					{
						toast("保存失败",0);
					}
					}
					else if(type.equals("array"))
					{
						try
							{
								ReadWriteFileWithEncode.write(new File(name), mapView.toArray(), "UTF-8");
								toast("保存成功\n"+name,1);
							}
						catch (IOException e)
							{
								toast("保存失败",0);
							}
					}
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO: Implement this method
					dialog.dismiss();

				}
			})
			.create();
		dialog.show();
	}
	
	//另存为
	void saveDialog()
	{
		View view = LayoutInflater.from(this).inflate(R.layout.export, null);
		final EditText edit = (EditText)view.findViewById(R.id.export_Edit);
		final Spinner spin=(Spinner)view.findViewById(R.id.export_Spinner);
		final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,new String[]{"tmx","txt","array"});
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(adapter);
		
		 
		 
		spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
				{

					@Override
					public void onItemSelected(AdapterView<?> p1, android.view.View p2, int pos, long p4)
						{
							int len=edit.getText().toString().lastIndexOf('.');
							if(len>=0)
							{
							String name=edit.getText().toString().substring(0,len);
							if(pos==0)
							{
								
								
								
								edit.setText(name+".tmx");
							}
							else if(pos==1)
							{
								edit.setText(name+".txt");
							}
							else if(pos==2)
							{
								edit.setText(name+".txt");
							}
							}
						}

					@Override
					public void onNothingSelected(AdapterView<?> p1)
						{
							
						}
					

				
					
			
		}
		);
		if(mapfilename==null)
		{
			edit.setText(SDCard+filepath+"/"+filename);
		}
		else
		edit.setText(mapfilename);
		Dialog dialog = new AlertDialog.Builder(this)
			.setTitle("另存为")
			.setView(view)
			.setPositiveButton("确定", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO: Implement this method
					dialog.dismiss();
					String filename = edit.getText().toString();
					
					long type=spin.getSelectedItemId();
					if(type==1)
					{
						try
							{
								ReadWriteFileWithEncode.write(new File(filename), mapView.toString(), "UTF-8");
							}
						catch (IOException e)
							{
								toast("IO错误",0);
							}
					}
					else if(type==2)
					{
						try
							{
								ReadWriteFileWithEncode.write(new File(filename), mapView.toArray(), "UTF-8");
							}
						catch (IOException e)
							{
								toast("IO错误",0);
							}
					}
					else if(type==0)
					{
						paramsLis.saveMap(filename);
					}
					
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO: Implement this method
					dialog.dismiss();
					
				}
			})
			.create();
		dialog.show();
	}
	
	public void newMapFileDialog()
	{
		View view = LayoutInflater.from(this).inflate(R.layout.newmap, null);
		//块宽
		final NumEdit blockWidth = (NumEdit) view.findViewById(R.id.blockWidth);
		final NumEdit blockHeight = (NumEdit) view.findViewById(R.id.blockHeight);
		final EditText edit_file = (EditText)view.findViewById(R.id.newmapEditText);
		edit_file.setText(SDCard+filepath+"/"+filename);
		this.blockWidth=mapView.getBlockWidth();
		this.blockHeight=mapView.getBlockHeight();
		blockWidth.setSize(this.blockWidth);
		blockHeight.setSize(this.blockHeight);
		//地图宽度
		final NumEdit mapWidth = (NumEdit) view.findViewById(R.id.mapWidth);
		final NumEdit mapHeight = (NumEdit) view.findViewById(R.id.mapHeight);
		mapWidth.setpositive();
		mapHeight.setpositive();
		blockWidth.setpositive();
		blockHeight.setpositive();


		this.mapWidth=mapView.getMapWidth();
		this.mapHeight=mapView.getMapHeight();
		mapWidth.setSize(this.mapWidth);
		mapHeight.setSize(this.mapHeight);
		Dialog dialog = new AlertDialog.Builder(this)
			.setTitle("新建地图")
			.setView(view)
			.setPositiveButton("确定", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO: Implement this method
					dialog.dismiss();
					mapfilename = edit_file.getText().toString();
					selectImage.setImageResource(R.drawable.title);
					adapter.clear();
					gridView.invalidate();
					mapView.init(MainActivity.this);
					validate(blockWidth, blockHeight, mapWidth, mapHeight);
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO: Implement this method
					dialog.dismiss();
					validate(blockWidth, blockHeight, mapWidth, mapHeight);
				}
			})
			.create();
		dialog.show();
	}
	
	//图片选择方式对话框
	public void openImage() {
		View view = LayoutInflater.from(this).inflate(R.layout.select_image, null) ;
		ImageView album_load = (ImageView) view.findViewById(R.id.album_load);
		ImageView gallery_load = (ImageView) view.findViewById(R.id.gallery_load);
		ImageView file_load = (ImageView) view.findViewById(R.id.file_load);

		album_load.setOnClickListener(clickLis);
		gallery_load.setOnClickListener(clickLis);
		file_load.setOnClickListener(clickLis);

		selectDialog = new AlertDialog.Builder(this)
			.setTitle("请选择打开图片方式")
			.setView(view)
			.setNegativeButton("取消", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO: Implement this method
					dialog.dismiss();
				}
			})
			.create();
		selectDialog.show();
	}



	

	//跳转到系统图库
	public void galleryImage() {
		Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		intent.putExtra("return-data", true);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
			startActivityForResult(intent, SELECT_PIC_KITKAT);
		} else {
			startActivityForResult(intent, SELECT_PIC);
		}
	}

	void openfilelist()
  {
		//传送
		Intent intent = new Intent();
		intent.putExtra("explorer_title", "选择图片");
		intent.putExtra("homepath", SDCard);
		intent.putExtra("returnFile",".jpg|.JPG|.png|.PNG|.gif|.bmp|.BMP");//设置返回类型
		intent.putExtra("filepath", filepath/*EmuPath.getProjectDir()*/);
		//intent.setDataAndType(Uri.fromFile(new File(filepath)), "*/*");
		intent.setClass(this, FileListActivity.class);
		startActivityForResult(intent, REQUEST_EX);
		//overridePendingTransition(R.anim.fade, R.anim.hold);
  }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == SELECT_PIC_KITKAT)
		{
			switch (resultCode) {
				case RESULT_OK:
					Uri uri = intent.getData();
					
						//Bitmap bitmap = BitmapFactory.decodeStream();
						mapView.addBitmap(uri.getPath());
						getImageInfo(uri);
					
					break;
				case RESULT_CANCELED:
					break;
			}
			}
			else if(requestCode==REQUEST_EX)
			{
				switch(resultCode)
				{
					case RESULT_OK:
						Uri uri = intent.getData();

						if (uri.getPath().equals("back"))
						{
							return ;
						}
						Bundle bundle= intent.getExtras();
						this. SDCard=bundle.getString("homepath");
						 this.filepath=bundle.getString("filepath");
                  String filename=bundle.getString("filename");
				  
Toast.makeText(this,filepath,0).show();
						File file=new File(SDCard+filepath+"/"+filename);
						int filelen=(int)file.length();
						
							
							mapView.addBitmap(file.getAbsolutePath());
							bitmapList_update();
							//getImageInfo(uri);
						
						
						//filepath=new File(filepath).getParent();
						
           // nameList.add(homepath+filepath);
						
						break;
				}
			}
			else if(requestCode == REQUEST_TMX)
			{
				switch(resultCode)
				{
					case RESULT_OK:
				Bundle bundle= intent.getExtras();
				this. SDCard=bundle.getString("homepath");
				this.filepath=bundle.getString("filepath");
				String filename=bundle.getString("filename");
						mapfilename=SDCard+filepath+"/"+filename;
				mapView.openMap(mapfilename);
				
				//toast("打开文件："+filename,0);
				setBlockList(mapView.getBitmapList());
				
				}
			}
      else if(requestCode== EX_SETTING)
      {
        xlApplication application=(xlApplication)getApplication();
        setRequestedOrientation(application.orientation);
        switch(resultCode)
        {
          case RESULT_CANCELED:
            
            break;
        }
      }
		
	}
	
	void setBlockList(List<Bitmap> list)
	{
		adapter = new GridAdapter(this, list);
		gridView.setAdapter(adapter);
	}
	
	//按指定宽高拆分bitmap
	ArrayList<Bitmap> clipBitmap(Bitmap bitmap,int itemw,int itemh)
	{
		ArrayList<Bitmap> temp=new ArrayList<Bitmap>();
		for(int y=0;y+itemh<bitmap.getHeight();y+=itemh)
		{
			for(int x=0;x+itemw<bitmap.getWidth();x+=itemw)
			{
				temp.add( Bitmap.createBitmap(bitmap,x,y,itemw,itemh));
			}
		}
		return temp;
	}



	//从系统图库加载图片获取图片信息
	public void getImageInfo(Uri uri) {

		Cursor cursor = getContentResolver().query(uri, null,
												   null, null, null); 
		cursor.moveToFirst(); 
		String imgNo = cursor.getString(0);   // 图片编号 
		String imgPath = cursor.getString(1); // 图片文件路径 
		String imgSize = cursor.getString(2); // 图片大小 
		String imgName = cursor.getString(3); // 图片文件名 
		mapView.getNameList().add(imgName);
		cursor.close(); 
	}


	//添加Bitmap，网格布局设置adapter
	
	
	//更新图标列表
	void bitmapList_update()
	{
		
		adapter = new GridAdapter(this, mapView.getBitmapList());
		gridView.setAdapter(adapter);
	}

	//地图设置对话框
	public void settingMapDialog() {
		View view = LayoutInflater.from(this).inflate(R.layout.map_setting, null);
		//块宽
		final NumEdit blockWidth = (NumEdit) view.findViewById(R.id.blockWidth);
		final NumEdit blockHeight = (NumEdit) view.findViewById(R.id.blockHeight);
		this.blockWidth=mapView.getBlockWidth();
		this.blockHeight=mapView.getBlockHeight();
		blockWidth.setSize(this.blockWidth);
		blockHeight.setSize(this.blockHeight);
		//地图宽度
		final NumEdit mapWidth = (NumEdit) view.findViewById(R.id.mapWidth);
		final NumEdit mapHeight = (NumEdit) view.findViewById(R.id.mapHeight);
		mapWidth.setpositive();
		mapHeight.setpositive();
		blockWidth.setpositive();
		blockHeight.setpositive();
		
		
		this.mapWidth=mapView.getMapWidth();
		this.mapHeight=mapView.getMapHeight();
		mapWidth.setSize(this.mapWidth);
		mapHeight.setSize(this.mapHeight);
		Dialog dialog = new AlertDialog.Builder(this)
			.setTitle("地图设置")
			.setView(view)
			.setPositiveButton("确定", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO: Implement this method
					dialog.dismiss();
					validate(blockWidth, blockHeight, mapWidth, mapHeight);
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO: Implement this method
					dialog.dismiss();
					validate(blockWidth, blockHeight, mapWidth, mapHeight);
				}
			})
			.create();
		dialog.show();
	}

	
	public void moveMapDialog() {
		View view = LayoutInflater.from(this).inflate(R.layout.move, null);
		//块宽
		final NumEdit edit_x = (NumEdit) view.findViewById(R.id.moveNumEdit1);
		final NumEdit edit_y = (NumEdit) view.findViewById(R.id.moveNumEdit2);
		
	 
		
		Dialog dialog = new AlertDialog.Builder(this)
			.setTitle("挪动地图")
			.setView(view)
			.setPositiveButton("确定", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO: Implement this method
					dialog.dismiss();
					mapView.moveMap(edit_x.getSize(),edit_y.getSize());
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO: Implement this method
					dialog.dismiss();
					mapView.moveMap(edit_x.getSize(),edit_y.getSize());
				}
			})
			.create();
		dialog.show();
	}
	
	//判断EditText输入的内容是否为空
	public void validate(NumEdit blockWidth, NumEdit blockHeight
						 , NumEdit mapWidth, NumEdit mapHeight) {
		/*if(!(blockWidth.getText().toString()==null||blockWidth.getText().toString().equals(""))&&){

		 }*/
		 /*
		bw = Integer.parseInt((blockWidth.getText().toString()));
		bh = Integer.parseInt((blockHeight.getText().toString()));
		mw = Integer.parseInt((mapWidth.getText().toString()));
		mh = Integer.parseInt((mapHeight.getText().toString()));
      */
	  this.blockWidth=blockWidth.getSize();
	  this.blockHeight=blockHeight.getSize();
	  this.mapWidth=mapWidth.getSize();
	  this.mapHeight=mapHeight.getSize();
		paramsLis.paramsChange(this.mapWidth, this.mapHeight, this.blockWidth, this.blockHeight);
		horSeekBar.setMax(this.mapWidth);
		verSeekBar.setMax(this.mapHeight);
	}


	//SeekBar监听处理
	private OnSeekBarChangeListener seekbarLis = new OnSeekBarChangeListener(){

		//进度条改变时调用
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			// TODO: Implement this method
			//Toast.makeText(MainActivity.this, "X："+String.valueOf(progress), 50).show();
			paramsLis.moveSeekBar(progress, -1);
		}

		//拖动时调用
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO: Implement this method

		}

		//停止拖动时调用
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO: Implement this method
		}

	};



    //对EditText输入内容进行判断
    private TextWatcher watcher = new TextWatcher(){

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO: Implement this method
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO: Implement this method
        }

        @Override
        public void afterTextChanged(Editable edit) {
            // TODO: Implement this method
            //匹配不包含中文[^\u4e00-\u9fa5]+   /*只包含中文[\u4e00-\u9fa5]+*/

        }
	};

	protected void onSaveInstanceState(Bundle outState)
	{
   
		super.onSaveInstanceState(outState);
		mapView.onSave(outState);
		outState.putString("homepath",SDCard);
		outState.putString("filepath",filepath);
		outState.putString("filename",filename);
	/*
		Logcat.e("onSaveInstanceState");
		outState.putString("filepath", ProjectPath);
		outState.putString("filename", filename);
    outState.putString("coding", coding);
*/
  
		
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		mapView.onRestore(savedInstanceState);
		setBlockList(mapView.getBitmapList());
		SDCard=savedInstanceState.getString("homepath");
		filepath=savedInstanceState.getString("filepath");
		filename=savedInstanceState.getString("filename");
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
		
		toast("异常恢复成功"+ProjectPath);
		*/
	}
  
	long time=0;
	@Override
	public void onBackPressed()
	{
	
		// TODO: Implement this method
	if(System.currentTimeMillis()-time>3000)
	{
			time=System.currentTimeMillis();
			toast("再按一次返回键退出",0);
		}
		else
		super.onBackPressed();
	}
	
	
	
	
  void toast(String text,int type)
	{
		Toast.makeText(this,text,type).show();
	}
	
	
	//反馈界面
	void showuser()
	{
	// 以对话框的形式弹出
	PgyFeedback.getInstance().showDialog(this);

// 以Activity的形式打开，这种情况下必须在AndroidManifest.xml配置FeedbackActivity
// 打开沉浸式,默认为false
// FeedbackActivity.setBarImmersive(true);
	//PgyFeedback.getInstance().showActiivty(MainActivity.this);
	}
	
	
	
	private static String TAG = "AppX_BannerAd";
	BDBannerAd bannerAdView;

	public void addAdView(ViewGroup layout)
	{

		// 创建广告视图
		// 发布时请使用正确的ApiKey和广告位ID
		// 此处ApiKey和推广位ID均是测试用的
		// 您在正式提交应用的时候，请确认代码中已经更换为您应用对应的Key和ID
		// 具体获取方法请查阅《百度开发者中心交叉换量产品介绍.pdf》
		bannerAdView = new BDBannerAd(this, "lE8Daaqs6PuTmXbzhVncafWan2q5Pw1l",
																	"g6cIazBYxBfFt6Obl2fEZvVd");

		// 设置横幅广告展示尺寸，如不设置，默认为SIZE_FLEXIBLE;
		bannerAdView.setAdSize(BDBannerAd.SIZE_320X50);

		// 设置横幅广告行为监听器
		bannerAdView.setAdListener(new BannerAdListener() {

				@Override
				public void onAdvertisementDataDidLoadFailure() {
					Log.e(TAG, "load failure");
				}

				@Override
				public void onAdvertisementDataDidLoadSuccess() {
					Log.e(TAG, "load success");
				}

				@Override
				public void onAdvertisementViewDidClick() {
					Log.e(TAG, "on click");
				}

				@Override
				public void onAdvertisementViewDidShow() {
					Log.e(TAG, "on show");
				}

				@Override
				public void onAdvertisementViewWillStartNewIntent() {
					Log.e(TAG, "leave app");
				}
			});

		// 创建广告容器
		//appxBannerContainer = (RelativeLayout) findViewById(R.id.appx_banner_container);

		// 显示广告视图
		layout.addView(bannerAdView);

	}
		//下载手机C语言
		public boolean download()
			{

				Intent intent=new Intent("android.intent.action.VIEW",Uri.parse(xlApplication.URL));


				try
					{
						startActivity(intent);
						return true;
					}
				catch(Exception e)
					{
						Toast.makeText(this,"你没有安装浏览器",0);
					}
				return false;
			}
}

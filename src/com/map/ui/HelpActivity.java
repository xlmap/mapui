package com.map.ui;
import android.app.*;
import android.os.*;
//import com.baidu.appx.BDBannerAd;
//import com.baidu.appx.BDBannerAd.BannerAdListener;
//import android.view.*;
//import android.util.*;
import android.widget.*;
import com.xl.game.tool.*;
import android.view.ViewGroup;
import com.map.ui.adv.BanView;

public class HelpActivity extends Activity
{
	private static String TAG = "AppX_BannerAd";
	BanView bannerAdView;

	public void addAdView(ViewGroup layout)
	{

		// 创建广告视图
		// 发布时请使用正确的ApiKey和广告位ID
		// 此处ApiKey和推广位ID均是测试用的
		// 您在正式提交应用的时候，请确认代码中已经更换为您应用对应的Key和ID
		// 具体获取方法请查阅《百度开发者中心交叉换量产品介绍.pdf》
		bannerAdView = new BanView(this);

		// 设置横幅广告展示尺寸，如不设置，默认为SIZE_FLEXIBLE;
		//bannerAdView.setAdSize(BDBannerAd.SIZE_320X50);

		// 设置横幅广告行为监听器
		/*
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
*/
		// 创建广告容器
		//appxBannerContainer = (RelativeLayout) findViewById(R.id.appx_banner_container);

		// 显示广告视图
		layout.addView(bannerAdView);

	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		xlApplication application=(xlApplication)getApplication();
		setTheme( application.theme);

		//requestWindowFeature(Window.FEATURE_NO_TITLE);//设置无标题
		setRequestedOrientation(application.orientation);
		setTitle(R.string.help);
		setContentView(R.layout.help);
		TextView textview_help=(TextView)findViewById(R.id.help_text);
		textview_help.setText(XL.getTextFromAssets(this,"help"));
		LinearLayout layout= (LinearLayout)findViewById(R.id.layout_adView);
		addAdView(layout);
		
		
		super.onCreate(savedInstanceState);
	}
	
	
}

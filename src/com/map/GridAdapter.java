package com.map;
import android.content.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;
import java.util.*;
import android.widget.LinearLayout.*;
import com.map.ui.*;

/*
 *网格布局适配器
 */
public class GridAdapter extends BaseAdapter {

	private List<Bitmap> list;
	private Context context;
	
	public GridAdapter(Context context,List<Bitmap> list) {
		this.context = context;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		// TODO: Implement this method
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO: Implement this method
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO: Implement this method
		return position;
	}
	
	public void clear()
	{
		list.clear();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO: Implement this method
		final ViewHolder holder;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.grid_item,null);
			holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
			convertView.setTag(holder);
		}else{
			holder = (GridAdapter.ViewHolder) convertView.getTag();
		}
		
		//设置图片
		holder.imageView.setImageBitmap(list.get(position));
		return convertView;
	}

	
	public final class ViewHolder{
		public ImageView imageView;
	}
	
}

package com.werxltd.bibleflash;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class LessonSetAdapter  extends BaseAdapter {

    private Context context;
	private List<LessonSet> listLessonSet;
	
	public LessonSetAdapter(Context context, List<LessonSet> listLessonSet){
		this.context = context;
		this.listLessonSet = listLessonSet;
	}
	public int getCount() {
		return listLessonSet.size();
	}

	public Object getItem(int position) {
		return listLessonSet.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup viewGroup) {
		LessonSet entry = listLessonSet.get(position);
		return new LessonSetAdapterView(context,entry);
	}
}

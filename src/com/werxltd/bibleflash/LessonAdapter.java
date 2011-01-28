package com.werxltd.bibleflash;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class LessonAdapter  extends BaseAdapter {

    private Context context;
	private List<Lesson> listLesson;
	
	public LessonAdapter(Context context, List<Lesson> listLesson){
		this.context = context;
		this.listLesson = listLesson;
	}
	public int getCount() {
		return listLesson.size();
	}

	public Object getItem(int position) {
		return listLesson.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup viewGroup) {
		Lesson entry = listLesson.get(position);
		return new LessonAdapterView(context,entry);
	}
}

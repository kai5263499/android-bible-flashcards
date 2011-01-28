package com.werxltd.bibleflash;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LessonAdapterView extends LinearLayout {
	public LessonAdapterView(Context context, Lesson entry) {
		super(context);
		
		this.setOrientation(VERTICAL);
		this.setTag(entry);
		
		View v = inflate(context, R.layout.lesson_item, null);
		
		TextView lessonText = (TextView)v.findViewById(R.id.lesson_name);
		lessonText.setText(entry.getName());
		
		addView(v);
	}
}

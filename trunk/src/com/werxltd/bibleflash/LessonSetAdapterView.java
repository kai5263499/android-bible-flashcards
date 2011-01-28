package com.werxltd.bibleflash;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LessonSetAdapterView extends LinearLayout {
	public LessonSetAdapterView(Context context, LessonSet entry) {
		super(context);
		
		this.setOrientation(VERTICAL);
		this.setTag(entry);
		
		View v = inflate(context, R.layout.lessonset_item, null);
		
		TextView lessonsetText = (TextView)v.findViewById(R.id.lessonset_name);
		lessonsetText.setText(entry.getName());
		
		addView(v);
	}
}

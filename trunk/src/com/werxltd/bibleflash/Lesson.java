package com.werxltd.bibleflash;

public class Lesson {
	private int id;
	private String name;
	private String lessonset;
	private String font;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setLessonset(String lessonset) {
		this.lessonset = lessonset;
	}
	public String getLessonset() {
		return lessonset;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setFont(String font) {
		this.font = font;
	}
	public String getFont() {
		return font;
	}
}

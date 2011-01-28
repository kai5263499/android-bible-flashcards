package com.werxltd.bibleflash;

public class Card {
	private int id;
	private String front;
	private String back;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFront() {
		return front;
	}

	public void setFront(String front) {
		this.front = front;
	}

	public String getBack() {
		return back;
	}

	public void setBack(String back) {
		this.back = back;
	}

	public boolean isGreek() {
		if(getFront() == null) return false;
		
		int firstGlyph = getFront().charAt(0);
		return ((firstGlyph >= 880 && firstGlyph <= 1023) || (firstGlyph >= 7936 && firstGlyph <= 8191));
	}

	public boolean isHebrew() {
		if(getFront() == null) return false;
		
		int firstGlyph = getFront().charAt(0);
		return ((firstGlyph >= 1424 && firstGlyph <= 1535) || (firstGlyph >= 64256 && firstGlyph <= 64335));
	}
}

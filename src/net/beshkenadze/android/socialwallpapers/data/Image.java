package net.beshkenadze.android.socialwallpapers.data;

import org.simpleframework.xml.Element;

public class Image {
	@Element
	private String width;
	@Element
	private String height;
	@Element
	private String source;

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

}

package net.beshkenadze.android.socialwallpapers.data;

import org.simpleframework.xml.Element;

public class Like {
	@Element
	private String object_id = "";

	public String getObjectId() {
		return object_id;
	}

	public void setObjectId(String object_id) {
		this.object_id = object_id;
	}
}

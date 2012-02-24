package net.beshkenadze.android.socialwallpapers.data;

import org.simpleframework.xml.Element;

public class Album {
	@Element
	private String aid;
	@Element
	private String name;
	@Element
	private String object_id;
	@Element
	private String owner;

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getObject_id() {
		return object_id;
	}

	public void setObject_id(String object_id) {
		this.object_id = object_id;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

}

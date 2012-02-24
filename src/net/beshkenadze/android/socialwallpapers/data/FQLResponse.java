package net.beshkenadze.android.socialwallpapers.data;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "fql_query_response")
public class FQLResponse {
	@Attribute
	private String list;
	@ElementList(name = "photo", inline = true, required = false)
	private List<Photo> photos = new ArrayList<Photo>();
	@ElementList(name = "album", inline = true, required = false)
	private List<Album> albums = new ArrayList<Album>();
	@ElementList(name = "like", inline = true, required = false)
	private List<Like> likes = new ArrayList<Like>();

	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public List<Album> getAlbums() {
		return albums;
	}

	public void setAlbums(List<Album> albums) {
		this.albums = albums;
	}

	public List<Like> getLikes() {
		return likes;
	}

	public void setLikes(List<Like> likes) {
		this.likes = likes;
	}
}

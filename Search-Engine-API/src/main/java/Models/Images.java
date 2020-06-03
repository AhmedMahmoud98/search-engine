package Models;

import java.util.List;


public class Images {
	private List<Image> images;
	private int size;
	
	public Images() {
		
	}
	public Images(List<Image> images, int size) {
		super();
		this.images = images;
		this.size = size;
	}
	@Override
	public String toString() {
		return "Images [images=" + images + ", size=" + size + "]";
	}
	public List<Image> getImages() {
		return images;
	}
	public void setImages(List<Image> images) {
		this.images = images;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
}

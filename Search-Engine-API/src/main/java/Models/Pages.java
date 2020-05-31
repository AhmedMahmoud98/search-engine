package Models;

import java.util.List;

public class Pages {
	private List<Page> pages;
	private int size;
	
	public Pages() {
		
	}
	public Pages(List<Page> pages, int size) {
		super();
		this.pages = pages;
		this.size = size;
	}
	@Override
	public String toString() {
		return "Pages [pages=" + pages + ", size=" + size + "]";
	}
	public List<Page> getPages() {
		return pages;
	}
	public void setPages(List<Page> pages) {
		this.pages = pages;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
}

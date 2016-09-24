package data;

public class Picture {
	String name;
	String imageB64;
	
	
	public Picture(String name, String imageBytes) {
		super();
		this.name = name;
		this.imageB64 = imageBytes;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImageBytes() {
		return imageB64;
	}
	public void setImageBytes(String imageBytes) {
		this.imageB64 = imageBytes;
	}
}

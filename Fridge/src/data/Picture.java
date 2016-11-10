package data;

public class Picture {
	String name;
	String imageB64;
	
	
	/**
	 * @param name name  of the picture
	 * @param imageBytes bytes of the picture
	 */
	public Picture(String name, String imageBytes) {
		super();
		this.name = name;
		this.imageB64 = imageBytes;
	}
	
	/**
	 * @return name of the picture
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name name of the picture
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * returns the bytes of the image
	 * @return bytes of the image
	 */
	public String getImageBytes() {
		return imageB64;
	}
	/**
	 * sets the byts of the image
	 * @param imageBytes bytes of the image
	 */
	public void setImageBytes(String imageBytes) {
		this.imageB64 = imageBytes;
	}
}

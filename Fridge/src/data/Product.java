package data;


import java.util.Calendar;
import java.util.Date;

public class Product {
	
	long barcode;
	String productname;
	String categorie;
	int shelflife;
	Date stored;
	Date expireDate;
	int productID;
	Date storeDate;
	int storeID;
	
	
	/**
	 * @return store id of the product
	 */
	public int getStoreID() {
		return storeID;
	}

	/**
	 * @param storeID store id of the product
	 */
	public void setStoreID(int storeID) {
		this.storeID = storeID;
	}

	/**
	 * @return store date of the product
	 */
	public Date getStoreDate() {
		return storeDate;
	}

	/**
	 * @param storeDate store date of the product
	 */
	public void setStoreDate(Date storeDate) {
		this.storeDate = storeDate;
	}

	/**
	 * @param expireDate expiredate of the product
	 */
	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	/**
	 * @return id of the product
	 */
	public int getProductID() {
		return productID;
	}
	
	/**
	 * @param productID id of the product
	 */
	public void setProductID(int productID) {
		this.productID = productID;
	}
	/**
	 * @param productname name of the product
	 */
	public Product(String productname){
		this.productname = productname;
	}
	/**
	 * @param barcode barcode of the product
	 * @param productname name  of the product
	 * @param categorie categorie of the product
	 * @param shelflife shelflife of the product
	 * @param productID id of the product
	 */
	public Product (long barcode, String productname, String categorie, int shelflife, int productID)
	{
		this.barcode = barcode;
		this.productname = productname;
		this.categorie = categorie;
		this.shelflife = shelflife;
		this.productID = productID;

	}
	/**
	 * @param barcode barcode of the product
	 * @param productname name  of the product
	 * @param categorie categorie of the product
	 * @param shelflife shelflife of the product
	 * @param productID id of the product
	 * @param storeDate storeDate of the product
	 * @param expDate expireDate of the product
	 */
	public Product (long barcode, String productname, String categorie, int shelflife, int productID, Date storeDate, Date expDate)
	{
		this.barcode = barcode;
		this.productname = productname;
		this.categorie = categorie;
		this.shelflife = shelflife;
		this.productID = productID;
		this.storeDate = storeDate;
		this.expireDate = expDate;
		

	}
	/**
	 * @param barcode
	 * @param productname
	 * @param categorie
	 * @param shelflife
	 * @param productID
	 * @param storeDate
	 * @param expDate
	 * @param storeID
	 */
	public Product (long barcode, String productname, String categorie, int shelflife, int productID, Date storeDate, Date expDate, int storeID)
	{
		this.barcode = barcode;
		this.productname = productname;
		this.categorie = categorie;
		this.shelflife = shelflife;
		this.productID = productID;
		this.storeDate = storeDate;
		this.expireDate = expDate;
		this.storeID = storeID;
		

	}
	
	/**
	 * @param date date where days want to be added
	 * @param days amount of days wich want to be added to a date
	 * @return
	 */
	private Date addDaysToDate(Date date, int days)
	{
		if(date != null){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); 
        return cal.getTime();
		}
		else return null;

	}
	
	/**
	 * @return barcode of the porduct
	 */
	public long getBarcode() {
		return barcode;
	}
	/**
	 * @param barcode barcode of the porduct
	 */
	public void setBarcode(long barcode) {
		this.barcode = barcode;
	}
	/**
	 * returns the name of a product
	 * @return name of the product
	 */
	public String getProductname() {
		return productname;
	}
	/**
	 * sets the name of a product
	 * @param productname
	 */
	public void setProductname(String productname) {
		this.productname = productname;
	}
	/**
	 * returns the id of the categorie of a product
	 * @return
	 */
	public String getCategorie() {
		return categorie;
	}
	/**
	 * sets the categorie of a product
	 * @param categorie categorie name
	 */
	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}
	/**
	 * @return Shelflife of a product
	 */
	public int getShelflife() {
		return shelflife;
	}
	/**
	 * @param shelflife
	 */
	public void setShelflife(int shelflife) {
		this.shelflife = shelflife;
	}
	/**
	 * @return
	 */
	public Date getStored() {
		return stored;
	}
	/**
	 * @param stored
	 */
	public void setStored(Date stored) {
		this.expireDate = addDaysToDate(stored,shelflife);
		this.stored = stored;
	}
	/**
	 * @return
	 */
	public Date getExpireDate() {
		this.expireDate = addDaysToDate(stored,shelflife);
		return expireDate;
	}

	

}

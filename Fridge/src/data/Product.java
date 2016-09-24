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
	
	
	public int getStoreID() {
		return storeID;
	}

	public void setStoreID(int storeID) {
		this.storeID = storeID;
	}

	public Date getStoreDate() {
		return storeDate;
	}

	public void setStoreDate(Date storeDate) {
		this.storeDate = storeDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public int getProductID() {
		return productID;
	}
	
	public void setProductID(int productID) {
		this.productID = productID;
	}
	public Product(String productname){
		this.productname = productname;
	}
	public Product (long barcode, String productname, String categorie, int shelflife, int productID)
	{
		this.barcode = barcode;
		this.productname = productname;
		this.categorie = categorie;
		this.shelflife = shelflife;
		this.productID = productID;

	}
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
	
	public long getBarcode() {
		return barcode;
	}
	public void setBarcode(long barcode) {
		this.barcode = barcode;
	}
	public String getProductname() {
		return productname;
	}
	public void setProductname(String productname) {
		this.productname = productname;
	}
	public String getCategorie() {
		return categorie;
	}
	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}
	public int getShelflife() {
		return shelflife;
	}
	public void setShelflife(int shelflife) {
		this.shelflife = shelflife;
	}
	public Date getStored() {
		return stored;
	}
	public void setStored(Date stored) {
		this.expireDate = addDaysToDate(stored,shelflife);
		this.stored = stored;
	}
	public Date getExpireDate() {
		this.expireDate = addDaysToDate(stored,shelflife);
		return expireDate;
	}

	

}

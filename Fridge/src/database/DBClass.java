package database;

import grpio.PinController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.DatatypeConverter;

import data.Categorie;
import data.Picture;
import data.Product;
import data.ShoppingList;

/**
 * Class to  access the Database
 * @author poiz
 *
 */
public class DBClass {

	private static DBClass instance;
	private static PinController pinC;

	/**
	 * 
	 */
	private DBClass() {
	}

	/**
	 * Singleton pattern
	 * @return an object of the class DBClass
	 */
	public static DBClass getInstance() {
		if (DBClass.instance == null) {
			DBClass.instance = new DBClass();
			pinC = new PinController();
		}
		return DBClass.instance;
	}

	/**
	 * returns a Connection to the Mysql Database
	 * @return Connection  to the Mysql Database
	 */
	private Connection getDBConnection() {
		Connection con = null;
		String url = "jdbc:mysql://localhost:3306/fridgedb_rpi";
		String user = "root";
		String password = "";

		try {
			// loadDriver
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {

			con = DriverManager.getConnection(url, user, password);

		} catch (SQLException ex) {

			Logger lgr = Logger.getLogger(DBClass.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		}
		return con;
	}
	/**
	 * Searches in Database for a Product by a given Name. if no Product is found, a Product with the given name will be created
	 * @param productName name of the product wich want to be searched or created
	 * @return the product
	 * @throws SQLException
	 */
	public Product searchOrCreateProduct(String productName) throws SQLException
	{
		Product prod = null;
		productName = removeQuote(productName);
		
		prod = searchProductByName(productName);
		if(prod.getProductID()==0){
			insertNewProduct(0, productName);
		}
		prod = searchProductByName(productName);
		
		
		
		try {
			storeProduct(prod);
			deleteDeletedItem(prod.getProductID());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		prod.setStoreID(getLastSotoreIDByProductID(prod.getProductID()));
		
		return prod;
	}
	/**
	 * returns the id of the last product which has been stored 
	 * @param id  id of a product
	 * @return storeid of the last product with the given id wich has been stored
	 * @throws SQLException
	 */
	public int getLastSotoreIDByProductID(int id) throws SQLException{

		Connection con = getDBConnection();
		Statement stmt = con.createStatement();

		String strSelect = "select storeid from storedproducts where product_id = "+id+" ORDER BY storeid DESC LIMIT 1";
		System.out.println(strSelect);
		ResultSet rset = stmt.executeQuery(strSelect);	
		int storeID = 0;
		while (rset.next()) { // Move the cursor to the next row		
			storeID = rset.getInt("storeid");	
		}
		stmt.close();
		rset.close();
		con.close();
		return  storeID;
	}
	/**
	 * Searches a Product in the Database by a given name
	 * @param productName name of a product
	 * @return object of the product
	 * @throws SQLException
	 */
	public Product searchProductByName(String productName) throws SQLException
	{
		productName = removeQuote(productName);
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();

		String strSelect = "select * from products where productname = '"+productName+"'";
		System.out.println(strSelect);
		ResultSet rset = stmt.executeQuery(strSelect);
		
		
		String product = "";
		String productname = "";
		String categorie = "";
		int produkt_id = 0;
		int shelflife = 0;
		long barcode = 0l;

		while (rset.next()) { // Move the cursor to the next row
			
			productname = rset.getString("productname");
			productname = removeQuote(productname);
			categorie = rset.getString("categorie_id");
			shelflife = rset.getInt("shelflife");
			produkt_id = rset.getInt("id");
			barcode = rset.getLong("barcode");
					
		}
		stmt.close();
		rset.close();
		con.close();
		return  new Product(barcode,productname,categorie,shelflife,produkt_id);
	}

	/**
	 * searches a product in databse by a given id
	 * @param id id of a product
	 * @return object of a product
	 * @throws SQLException
	 */
	public Product searchProductByID(int id) throws SQLException {

		Connection con = getDBConnection();
		Statement stmt = con.createStatement();

		String strSelect = "select * from products where id = "+id;
		ResultSet rset = stmt.executeQuery(strSelect);
		
		
		String product = "";
		String productname = "";
		String categorie = "";
		int produkt_id = 0;
		int shelflife = 0;
		long barcode = 0l;

		while (rset.next()) { // Move the cursor to the next row
			
			productname = rset.getString("productname");
			productname = removeQuote(productname);
			categorie = rset.getString("categorie_id");
			shelflife = rset.getInt("shelflife");
			produkt_id = rset.getInt("id");
			barcode = rset.getLong("barcode");
					
		}
		stmt.close();
		rset.close();
		con.close();
		return  new Product(barcode,productname,categorie,shelflife,produkt_id);
	}
	
	/**
	 * inserts a new Product into the database
	 * @param barcode barcode of the product wich want to be added to database
	 * @param productname name of the product wich want to be added to database
	 * @throws SQLException
	 */
	public void insertNewProduct(long barcode, String productname) throws SQLException {
		productname = removeQuote(productname);
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("INSERT INTO products(productname,shelflife,barcode) VALUES ('"+productname+"',0,"+barcode+")");
		stmt.close();
		con.close();
	}
	
	/**
	 * updates the Categorie of a Product
	 * @param id id of the product where the categorie want to be changed
	 * @param categorieID categorieid of the new categorie
	 * @throws SQLException
	 */
	public void updateCategorieOfProductByID(int id, int categorieID) throws SQLException
	{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("UPDATE products SET categorie_id = "+categorieID+" WHERE id = "+id);
		stmt.close();
		con.close();
		pinC.positionIndicatorLed(categorieID);
	}
	
	/**
	 * updates the expiredate of a product by the productid
	 * @param id of the product where the expiredate want to be changed
	 * @param expDate new expiredate
	 * @throws SQLException
	 */
	public void updateExpireDateOfdStoredProductByID(int id, Date expDate) throws SQLException
	{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("UPDATE storedproducts SET expiredate = '"+new java.sql.Date(expDate.getTime())+"' WHERE product_id = "+id);
		stmt.close();
		con.close();
		updateShelfLifeByID (id,getDateDiff(getTodaysDate(),expDate));
		
	}
	/**
	 * updates the expiredate of a product by the storeid
	 * @param id  storeid of the product where the expiredate want to be changed
	 * @param expDate new expiredate
	 * @throws SQLException
	 */
	public void updateExpireDateOfdStoredProductByStoreID(int id, Date expDate) throws SQLException
	{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("UPDATE storedproducts SET expiredate = '"+new java.sql.Date(expDate.getTime())+"' WHERE storeid = "+id);
		stmt.close();
		con.close();
		
	}
	/**
	 * updates the shelflife of a product by the productid
	 * @param id id of the product where the shelflife want to be changed
	 * @param days new shelflife
	 * @throws SQLException
	 */
	public void updateShelfLifeByID(int id, int days) throws SQLException{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("UPDATE products SET shelflife = "+days+" WHERE id = "+id);
		stmt.close();
		con.close();
	}
	
	/**
	 * stores a product in database
	 * @param prod object of a product
	 * @throws SQLException
	 * @throws ParseException
	 */
	public void storeProduct(Product prod) throws SQLException, ParseException{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		prod.setStored(getTodaysDate());
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
		java.sql.Date stored =  new java.sql.Date(prod.getStored().getTime());
		java.sql.Date exp = new java.sql.Date(prod.getExpireDate().getTime());
	
		stmt.executeUpdate("INSERT INTO storedproducts (product_id,storedate,expiredate) VALUES ("+prod.getProductID()+", '"+stored+"', '"+exp+"')");
		stmt.close();
		con.close();
		System.out.println("store");
		pinC.positionIndicatorLed(Integer.parseInt(prod.getCategorie()));
	}
	
	
	/**
	 * returns todays date
	 * @return the date of today
	 */
	public Date getTodaysDate(){
		
		Calendar today = Calendar.getInstance();
		today.clear(Calendar.HOUR); today.clear(Calendar.MINUTE); today.clear(Calendar.SECOND);
		Date todayDate = today.getTime();
		
		return todayDate;
	}
	
	/**
	 * calculates the amount of days between two dates
	 * @param date1 first date
	 * @param date2 second date
	 * @return days between the two given dates
	 */
	public int getDateDiff(Date date1, Date date2) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return (int)TimeUnit.DAYS.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
	//id: 0 = h�hlschrank, 1= gefrierfach
	/**
	 * stores a picture in Database
	 * @param id
	 * @param picture
	 * @throws SQLException
	 * @throws FileNotFoundException
	 */
	public void storePic(int id, File picture ) throws SQLException, FileNotFoundException{
		
		FileInputStream fis = new FileInputStream(picture);
		PreparedStatement ps = null;
		

		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		String INSERT_PICTURE = "UPDATE prictures set photo = ? where id = "+id;
		ps = con.prepareStatement(INSERT_PICTURE);
		ps.setBinaryStream(1, fis, (int) picture.length());
	    ps.executeUpdate();
		con.close();
	}
		
	/**
	 * gets all products from databay by a given categorieid
	 * @param stored
	 * @param categorieID
	 * @return javalist of all Products of the given categorie
	 * @throws SQLException
	 */
	public List<Product> getProductsByCategorie(int stored,int categorieID) throws SQLException{
		
		List<Product> prodList = new ArrayList<Product>();
		String strSelect;

		
		if(stored == 0){
			if(categorieID == 0){
				
				strSelect = "select * from products";
				
			}else{
				 strSelect = "select * from products where categorie_id = "+categorieID;
			}
		}else{
			if(categorieID == 0){
				
				strSelect = "select products.productname, products.categorie_id, products.shelflife, products.id, products.barcode, storedproducts.storedate, storedproducts.expiredate,storedproducts.storeid from products inner join storedproducts where storedproducts.product_id = products.id";
				
			}else{
				strSelect = "select products.productname, products.categorie_id, products.shelflife, products.id, products.barcode, storedproducts.storedate, storedproducts.expiredate, storedproducts.storeid from products inner join storedproducts where storedproducts.product_id = products.id and products.categorie_id = "+categorieID;
			}
			
		}
		
		
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		ResultSet rset = stmt.executeQuery(strSelect);
		
		
		
		while (rset.next()) { // Move the cursor to the next row
			Product p = null;
			if(stored == 0){
				p = new Product(rset.getLong("barcode"), rset.getString("productname"), rset.getString("categorie_id"), rset.getInt("shelflife"), rset.getInt("id"));
			}else{
				p = new Product(rset.getLong("barcode"), rset.getString("productname"), rset.getString("categorie_id"), rset.getInt("shelflife"), rset.getInt("id"),rset.getDate("storedate"),rset.getDate("expiredate"),rset.getInt("storeid"));
			}
			
			
			prodList.add(p);
		}
		stmt.close();
		rset.close();
		con.close();
		return prodList;
	}
	
	
	/**
	 * gets the id of a product by a given barcode
	 * @param barcode
	 * @return id of the broduct with the given barcode
	 * @throws SQLException
	 */
	public int getIDByBarcode(long barcode) throws SQLException
	{
		
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();

		String strSelect = "select id from products where barcode = "+barcode;
		ResultSet rset = stmt.executeQuery(strSelect);
		
		
		
		int produkt_id = 0;

		while (rset.next()) { // Move the cursor to the next row

			produkt_id = rset.getInt("id");
					
		}
		stmt.close();
		rset.close();
		con.close();
		
		return produkt_id;
	}
	
	
	/**
	 * gets all categories from database
	 * @return JavaList of all categories
	 * @throws SQLException
	 */
	public List<Categorie> getallCategories() throws SQLException{

		List<Categorie> catList = new ArrayList<Categorie>();
		String strSelect;
		
		strSelect = "select * from categories";
				
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		ResultSet rset = stmt.executeQuery(strSelect);
		
	
		while (rset.next()) { // Move the cursor to the next row
			Categorie c = new Categorie(rset.getString("categorie"), rset.getInt("id"));
			catList.add(c);
		}
		stmt.close();
		rset.close();
		con.close();
		return catList;
	}
	
	//delete by productID
	/**
	 * deletes a product from database by a given id
	 * @param id id of the product wich want to be deleted
	 * @throws SQLException
	 */
	public void deleteStoredProductByID(int id) throws SQLException
	{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("Delete FROM storedproducts where product_id = "+id+" ORDER BY expireDate ASC LIMIT 1 ");
		stmt.close();
		con.close();
	}
	//delete by storeID
	/**
	 * unstore a product by a given product id
	 * @param id storeid of the product wich want to be deleted
	 */
	public void deleteStoredProductByStoreID(int id)
	{
		
		try{
			Connection con = getDBConnection();
			Statement stmt = con.createStatement();
			stmt.executeUpdate("Delete FROM storedproducts where storeid = "+id);
			stmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		try {
			addDeletedItem(getProductIdOfStoredItem(id));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		
	}
	/**
	 * get all products wich has been deletet an not stored again
	 * @return JavaList of all deleted Products
	 * @throws SQLException
	 */
	public List<Product>getDeletedProducts() throws SQLException{
		List<Product> prodList = new ArrayList<Product>();
		String strSelect;
				
		strSelect = "select products.productname,products.barcode, products.categorie_id, products.shelflife,products.id from products inner join deletedproducts where deletedproducts.productid = products.id";

		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		ResultSet rset = stmt.executeQuery(strSelect);

		while (rset.next()) { // Move the cursor to the next row
			Product p = null;
			p = new Product(rset.getLong("barcode"), rset.getString("productname"), rset.getString("categorie_id"), rset.getInt("shelflife"), rset.getInt("id"));
			prodList.add(p);
		}
		stmt.close();
		rset.close();
		con.close();
		return prodList;
	}
	/**
	 * limits the items in the table deletedproducts to the newest 20
	 * @throws SQLException
	 */
	public void limitDeletedItemsTo20() throws SQLException{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("DELETE FROM deletedproducts ORDER BY id DESC LIMIT 20");
		stmt.close();
		con.close();
		
	}
	/**
	 * adds a item to the table of deleted items
	 * @param id id of the item which want to be added to deleted items
	 * @throws SQLException
	 */
	public void addDeletedItem(int id) throws SQLException{ //product has been deleted, is suggested for shoppinglist now
		//limitDeletedItemsTo20();
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("INSERT INTO deletedproducts(productid) VALUES ("+id+")");
		stmt.close();
		con.close();
		
	}	
	/**
	 * deletes a item from the table of deletet items if it has been stored
	 * @param id 
	 * @throws SQLException
	 */
	public void deleteDeletedItem(int id) throws SQLException{ // product hast been stored, not suggested for shoppinglist anymore
		//limitDeletedItemsTo20();
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("DELETE FROM deletedproducts where productid = "+id);
		stmt.close();
		con.close();
		
	}
	/**
	 * gets the id of a stored item by the storeid
	 * @param storeid storeid of a product
	 * @return the productid of the product
	 * @throws SQLException
	 */
	public int getProductIdOfStoredItem(int storeid) throws SQLException{
		int productid = 0;
		String strSelect;
		strSelect = "select product_id from storedproducts where storeid = "+storeid;	
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		ResultSet rset = stmt.executeQuery(strSelect);
		while (rset.next()) { // Move the cursor to the next row		
			productid = rset.getInt("product_id");
		}
		
		stmt.close();
		rset.close();
		con.close();
		return productid;
	
	}
	/**
	 * inserts the photos wich have been taken by the webcam into the database
	 * @throws SQLException
	 * @throws IOException
	 */
	public void inserPic() throws SQLException, IOException{
		Connection con = getDBConnection();
		 String INSERT_PICTURE = "Update prictures set photo=? where id = 1";

		    FileInputStream fis = null;
		    PreparedStatement ps = null;

		      File file = new File("img.jpg");
		      fis = new FileInputStream(file);
		      ps = con.prepareStatement(INSERT_PICTURE);
		      ps.setBinaryStream(1, fis, (int) file.length());
		      ps.executeUpdate();
		      ps.close();
		      fis.close();
	
	}
	/**
	 * returns a stored Product by Name
	 * @param name name of a product which has bee stored
	 * @return JavaList of all Product where the name contains the given Productname
	 * @throws SQLException
	 */
	public List<Product> getStoredProductsByName(String name) throws SQLException{
		
		List<Product> prodList = new ArrayList<Product>();
		String strSelect;
		String producktname = removeQuote(name);
		strSelect = "select products.productname, products.categorie_id, products.shelflife, products.id, products.barcode, storedproducts.storedate, storedproducts.expiredate,storedproducts.storeid from products inner join storedproducts where storedproducts.product_id = products.id and LOWER(products.productname) LIKE'%"+producktname.toLowerCase()+"%'";	
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		ResultSet rset = stmt.executeQuery(strSelect);
		System.out.println("gefundenes produkt "+name);
		while (rset.next()) { // Move the cursor to the next row
			Product p = null;
			p = new Product(rset.getLong("barcode"), rset.getString("productname"), rset.getString("categorie_id"), rset.getInt("shelflife"), rset.getInt("id"),rset.getDate("storedate"),rset.getDate("expiredate"),rset.getInt("storeid"));	
			System.out.println("gefundenes produkt "+rset.getString("productname"));
			prodList.add(p);
		}
		
		stmt.close();
		rset.close();
		con.close();
	
		return prodList;
	}
	
	/**
	 * gets the stored pictures from the databse
	 * @return JavaList of all Pictures
	 * @throws SQLException
	 */
	public List<Picture> getPircturesFromDB() throws SQLException{
		List<Picture> picList = new ArrayList<Picture>();
		String strSelect;
		
		strSelect = "select * from prictures";
				
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		ResultSet rset = stmt.executeQuery(strSelect);
		
	
		while (rset.next()) { // Move the cursor to the next row
			Blob imageBlob = rset.getBlob("Photo");
			String name = rset.getString("name");

			byte[] imageBytes = imageBlob.getBytes(1, (int) imageBlob.length());

			String encoded = DatatypeConverter.printBase64Binary(imageBytes);
			Picture pic = new Picture(name, encoded);
			picList.add(pic);	
		}
		stmt.close();
		rset.close();
		con.close();
		return picList;
		
	}
	/**
	 * gets the shoppinglists from the database
	 * @return JavaList of all Shoppinglists
	 * @throws SQLException
	 */
	public List<ShoppingList> getAllShoppingLists() throws SQLException{
		List<ShoppingList> sLists = new ArrayList<ShoppingList>();
		String strSelect;
		
		strSelect = "select * from shoppinglist";
				
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		ResultSet rset = stmt.executeQuery(strSelect);
		
	
		while (rset.next()) { // Move the cursor to the next row
			int id = rset.getInt("id");
			String name = rset.getString("name");
			Date date = rset.getDate("date");
			ShoppingList sList = new ShoppingList(id,name,date);
			sList.setProducts(getAllProductsOfShoppingList(id));
			sLists.add(sList);
		}
		stmt.close();
		rset.close();
		con.close();
		return sLists;
		
	}
	/**
	 * returns all Products of a shoppinglist by a given shoppinglist id
	 * @param shoppingListID id of a shoppinglist
	 * @return JavaList of all Products of a Shoppinglist
	 * @throws SQLException
	 */
	public List<Product> getAllProductsOfShoppingList(int shoppingListID) throws SQLException{
		

		
		List<Product> pList = new ArrayList<Product>();
		String strSelect;
		
		strSelect = "SELECT DISTINCT p.* FROM products p JOIN shoppinlist_product sp ON p.id = sp.productid where sp.shoppinlistid = "+shoppingListID;
				
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		ResultSet rset = stmt.executeQuery(strSelect);
		
	
		while (rset.next()) { // Move the cursor to the next row
			pList.add(new Product(rset.getLong("barcode"), rset.getString("productname"), rset.getString("categorie_id"), rset.getInt("shelflife"), rset.getInt("id")));	
		}	
		stmt.close();
		rset.close();
		con.close();
		return pList;
		
	}
	/**
	 * delets a product from a shoppinglist
	 * @param productId
	 * @param shoppingListID
	 * @throws SQLException
	 */
	public void deleteProductFromShoppingList(int productId, int shoppingListID) throws SQLException
	{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("Delete FROM shoppinlist_product where productid = "+productId+" and  shoppinlistid = "+shoppingListID);
		stmt.close();
		con.close();
	}
	/**
	 * adds a product top a shoppinglist
	 * @param productId
	 * @param shoppingListID
	 * @throws SQLException
	 */
	public void addProducttoShoppingList(int productId, int shoppingListID) throws SQLException
	{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("INSERT INTO shoppinlist_product(shoppinlistid,productid) VALUES ("+shoppingListID+","+productId+")");
		stmt.close();
		con.close();
	}
	/**
	 * delets a shoppinglist by a given shoppinglistid
	 * @param shoppingListID
	 * @throws SQLException
	 */
	public void deleteShoppingListById( int shoppingListID) throws SQLException
	{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("DELETE FROM shoppinglist where id = "+shoppingListID);
		stmt.close();
		con.close();
	}
	/**
	 * updates the name of a shoppinglist by a given shoppinglistid
	 * @param id
	 * @param name
	 * @throws SQLException
	 */
	public void updateShoppingListNameById (int id, String name) throws SQLException{
		name = removeQuote(name);
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("UPDATE shoppinglist SET name = '"+name+"' WHERE id = "+id);
		stmt.close();
		con.close();
	}

	/**
	 * creates a new Shoppinglist
	 * @return
	 * @throws SQLException
	 */
	public ShoppingList createNewShoppingList() throws SQLException{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		int id = 0;

		ShoppingList sList = null;


		stmt.executeUpdate("INSERT INTO shoppinglist(name) VALUES ('Neue Einkaufsliste')", Statement.RETURN_GENERATED_KEYS);
		ResultSet rs = stmt.getGeneratedKeys();
		if (rs != null && rs.next()) {
			id = rs.getInt(1);
		}
		rs.close();

		String strSelect;	
		strSelect = "select * from shoppinglist where id = "+id;
		ResultSet rset = stmt.executeQuery(strSelect);
		
	
		while (rset.next()) { // Move the cursor to the next row
			String name = rset.getString("name");
			Date date = rset.getDate("date");
			sList = new ShoppingList(id,name,date);
			sList.setProducts(getAllProductsOfShoppingList(id));
		}
		
		stmt.close();
		rset.close();
		con.close();
		
		
		return sList;
	}


	
	/**
	 * removes all " from a string
	 * @param str
	 * @return String without "
	 */
	private String removeQuote(String str){
		str = str.replaceAll("\"", "");
		return str;
	}
	
	


}

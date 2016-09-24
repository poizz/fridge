package database;

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
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import data.Categorie;
import data.Picture;
import data.Product;
import data.ShoppingList;

public class DBClass {

	private static DBClass instance;


	private DBClass() {
	}

	public static DBClass getInstance() {
		if (DBClass.instance == null) {
			DBClass.instance = new DBClass();
		}
		return DBClass.instance;
	}

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
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		prod.setStoreID(getLastSotoreIDByProductID(prod.getProductID()));
		
		return prod;
	}
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
	public Product searchProductByName(String productName) throws SQLException
	{
		productName = removeQuote(productName);
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();

		String strSelect = "select * from Products where productname = '"+productName+"'";
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

	public Product searchProductByID(int id) throws SQLException {

		Connection con = getDBConnection();
		Statement stmt = con.createStatement();

		String strSelect = "select * from Products where id = "+id;
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
	
	public void insertNewProduct(long barcode, String productname) throws SQLException {
		productname = removeQuote(productname);
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("INSERT INTO Products(productname,shelflife,barcode) VALUES ('"+productname+"',0,"+barcode+")");
		stmt.close();
		con.close();
	}
	
	public void updateCategorieOfProductByID(int id, int categorieID) throws SQLException
	{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("UPDATE products SET categorie_id = "+categorieID+" WHERE id = "+id);
		stmt.close();
		con.close();
	}
	
	public void updateExpireDateOfdStoredProductByID(int id, Date expDate) throws SQLException
	{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("UPDATE storedproducts SET expiredate = '"+new java.sql.Date(expDate.getTime())+"' WHERE product_id = "+id);
		stmt.close();
		con.close();
		updateShelfLifeByID (id,getDateDiff(getTodaysDate(),expDate));
		
	}
	public void updateExpireDateOfdStoredProductByStoreID(int id, Date expDate) throws SQLException
	{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("UPDATE storedproducts SET expiredate = '"+new java.sql.Date(expDate.getTime())+"' WHERE storeid = "+id);
		stmt.close();
		con.close();
		
	}
	public void updateShelfLifeByID(int id, int days) throws SQLException{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("UPDATE products SET shelflife = "+days+" WHERE id = "+id);
		stmt.close();
		con.close();
	}
	
	public void storeProduct(Product prod) throws SQLException, ParseException{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		prod.setStored(getTodaysDate());
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
		java.sql.Date stored =  new java.sql.Date(prod.getStored().getTime());
		java.sql.Date exp = new java.sql.Date(prod.getExpireDate().getTime());
	
		stmt.executeUpdate("INSERT INTO storedProducts (product_id,storedate,expiredate) VALUES ("+prod.getProductID()+", '"+stored+"', '"+exp+"')");
		stmt.close();
		con.close();
	}
	
	
	public Date getTodaysDate(){
		
		Calendar today = Calendar.getInstance();
		today.clear(Calendar.HOUR); today.clear(Calendar.MINUTE); today.clear(Calendar.SECOND);
		Date todayDate = today.getTime();
		
		return todayDate;
	}
	
	public int getDateDiff(Date date1, Date date2) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return (int)TimeUnit.DAYS.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
	//id: 0 = hühlschrank, 1= gefrierfach
	public void storePic(int id, File picture ) throws SQLException, FileNotFoundException{
		
		FileInputStream fis = new FileInputStream(picture);
		PreparedStatement ps = null;
		

		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		String INSERT_PICTURE = "UPDATE pictures set photo = ? where id = "+id;
		ps = con.prepareStatement(INSERT_PICTURE);
		ps.setBinaryStream(3, fis, (int) picture.length());
	    ps.executeUpdate();
	    con.commit();
		con.close();
	}
		
	public List<Product> getProductsByCategorie(int stored,int categorieID) throws SQLException{
		
		List<Product> prodList = new ArrayList<Product>();
		String strSelect;

		
		if(stored == 0){
			if(categorieID == 0){
				
				strSelect = "select * from Products";
				
			}else{
				 strSelect = "select * from Products where categorie_id = "+categorieID;
			}
		}else{
			if(categorieID == 0){
				
				strSelect = "select Products.productname, Products.categorie_id, Products.shelflife, Products.id, Products.barcode, storedproducts.storedate, storedproducts.expiredate,storedproducts.storeid from Products inner join storedproducts where storedproducts.product_id = products.id";
				
			}else{
				strSelect = "select Products.productname, Products.categorie_id, Products.shelflife, Products.id, Products.barcode, storedproducts.storedate, storedproducts.expiredate, storedproducts.storeid from Products inner join storedproducts where storedproducts.product_id = products.id and Products.categorie_id = "+categorieID;
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
	
	
	public int getIDByBarcode(long barcode) throws SQLException
	{
		
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();

		String strSelect = "select id from Products where barcode = "+barcode;
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
	public void deleteStoredProductByID(int id) throws SQLException
	{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("Delete FROM storedproducts where product_id = "+id+" ORDER BY expireDate ASC LIMIT 1 ");
		stmt.close();
		con.close();
	}
	//delete by storeID
	public void deleteStoredProductByStoreID(int id) throws SQLException
	{
		addDeletedItem(getProductIdOfStoredItem(id));
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("Delete FROM storedproducts where storeid = "+id);
		stmt.close();
		con.close();
		
	}
	public List<Product>getDeletedProducts() throws SQLException{
		List<Product> prodList = new ArrayList<Product>();
		String strSelect;
				
		strSelect = "select Products.productname,Products.barcode, Products.categorie_id, Products.shelflife,Products.id from Products inner join deletedproducts where deletedproducts.productid = Products.id";

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
	public void limitDeletedItemsTo20() throws SQLException{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("DELETE FROM deletedproducts ORDER BY id DESC LIMIT 20");
		stmt.close();
		con.close();
		
	}
	public void addDeletedItem(int id) throws SQLException{
		//limitDeletedItemsTo20();
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("INSERT INTO deletedproducts(productid) VALUES ("+id+")");
		stmt.close();
		con.close();
		
	}
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
	public List<Product> getStoredProductsByName(String name) throws SQLException{
		
		List<Product> prodList = new ArrayList<Product>();
		String strSelect;
		String producktname = removeQuote(name);
		strSelect = "select Products.productname, Products.categorie_id, Products.shelflife, Products.id, Products.barcode, storedproducts.storedate, storedproducts.expiredate,storedproducts.storeid from Products inner join storedproducts where storedproducts.product_id = products.id and LOWER(Products.productname) LIKE'%"+producktname.toLowerCase()+"%'";	
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

			String encoded = Base64.getEncoder().encodeToString(imageBytes);

			Picture pic = new Picture(name, encoded);
			picList.add(pic);	
		}
		stmt.close();
		rset.close();
		con.close();
		return picList;
		
	}
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
	public void deleteProductFromShoppingList(int productId, int shoppingListID) throws SQLException
	{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("Delete FROM shoppinlist_product where productid = "+productId+" and  shoppinlistid = "+shoppingListID);
		stmt.close();
		con.close();
	}
	public void addProducttoShoppingList(int productId, int shoppingListID) throws SQLException
	{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("INSERT INTO shoppinlist_product(shoppinlistid,productid) VALUES ("+shoppingListID+","+productId+")");
		stmt.close();
		con.close();
	}
	public void deleteShoppingListById( int shoppingListID) throws SQLException
	{
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("DELETE FROM shoppinglist where id = "+shoppingListID);
		stmt.close();
		con.close();
	}
	public void updateShoppingListNameById (int id, String name) throws SQLException{
		name = removeQuote(name);
		Connection con = getDBConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("UPDATE shoppinglist SET name = '"+name+"' WHERE id = "+id);
		stmt.close();
		con.close();
	}

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


	
	private String removeQuote(String str){
		str = str.replaceAll("\"", "");
		return str;
	}
	
	


}

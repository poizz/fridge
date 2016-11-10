package SocketConnection;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import RPIgetItem.BarcodeScannerListener;
import RPIgetItem.JsonAPI;
import data.Product;
import database.DBClass;

public class communicationProtocol {
	private DBClass dbC = DBClass.getInstance( );
	private JsonAPI jAPI = JsonAPI.getInstance( );
	private BarcodeScannerListener bSL = BarcodeScannerListener.getInstance( );

	//The first 3 bits of a input define the task
	//000-> all Products in database
	//001-> all Products in database of categorie Fleisch und Fisch
	//002-> all Products in database fo categorie Milchprodukte und Eier
	//003-> all Products in database of categorie Obst und Gemüse
	//004-> all Products in database of categorie Sonstiges
	//005-> all stored Products in database
	//006-> all stored Products in database of categorie Fleisch und Fisch
	//007-> all stored Products in database fo categorie Milchprodukte und Eier
	//008-> all stored Products in database of categorie Obst und Gemüse
	//009-> all stored Products in database of categorie Sonstiges
	//010-> set store mode
	//011-> set delete mode
	//012 register token
	//013 get all categories
	//014 update Categorie
	//015 update shelflife
	//016 searchOrCreateProduct and store it after
	//017 get photos of fridge and freezer 
	//018 delete stored product by id
	//19 update expDate of already stored Product
	//20 search stored Products by Name
	//21 search all Shoppinglists
	//22 del product from shoppinglist
	//23 get prductnames for autocomplete
	//24 add product to shoppinglist
	//25 add Product to database by Name
	//26 dele Shoppinglist by id
	//27 update name if Shoppinglist
	//28 create new shoppingList
	//29 get list of deleted items for shoppinglist suggestions
	
	
	
	
	 /**
	  * the  processInput method knows which ProcessCode stands for which Task eg "000" -> all Products in database
	 * @param theInput from client received message
	 * @return 
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public String processInput(String theInput) throws NumberFormatException, Exception {
		System.out.println("income: "+theInput);
		 String taskCode = "";
		 if(theInput.length()>=3){
			 taskCode  = theInput.substring(0,3);
		 }
		 String information = "";
		 if(theInput.length()>3){
			
			 information = theInput.substring(3,theInput.length());
		 }
		 System.out.println("code: "+taskCode+" info:"+information);
		 Gson gson = new Gson();
		 
		 if(taskCode.equals("000")){	 					 
			 return  taskCode+jAPI.searchAllProductsByCategorie(0);	
		 }
		 if(taskCode.equals("001")){
			 return  taskCode+jAPI.searchAllProductsByCategorie(1);
		 }
		 if(taskCode.equals("002")){
			 return  taskCode+jAPI.searchAllProductsByCategorie(2);
		 }
		 if(taskCode.equals("003")){
			 return  taskCode+jAPI.searchAllProductsByCategorie(3);
		 }
		 if(taskCode.equals("004")){
			 return  taskCode+jAPI.searchAllProductsByCategorie(4);
		 }
		 if(taskCode.equals("005")){	
			 
			 return  taskCode+jAPI.searchStoredProductsByCategorie(0);	
		 }
		 if(taskCode.equals("006")){
			 return  taskCode+jAPI.searchStoredProductsByCategorie(1);
		 }
		 if(taskCode.equals("007")){
			 return  taskCode+jAPI.searchStoredProductsByCategorie(2);
		 }
		 if(taskCode.equals("008")){
			 return  taskCode+jAPI.searchStoredProductsByCategorie(3);
		 }
		 if(taskCode.equals("009")){
			 return  taskCode+jAPI.searchStoredProductsByCategorie(4);
		 }
		 if(taskCode.equals("010")){
			 bSL.setScanStatus(0);
			 return  taskCode+"storeMode";
		 }
		 if(taskCode.equals("011")){
			 bSL.setScanStatus(1);
			 return  taskCode+"deleteMode";
		 }
		 if(taskCode.equals("012")){
			bSL.setRegisterToken(information);
			 return  taskCode+"registered";
		 }
		 if(taskCode.equals("013")){
			 return taskCode+jAPI.searchAllCategories();
		 }
		 if(taskCode.equals("014")){
			 	JsonElement jelement = new JsonParser().parse(information);
			    JsonObject  jobject = jelement.getAsJsonObject();
			    int productID =  Integer.parseInt(jobject.get("productID").toString());
			    int categorieID =  Integer.parseInt(jobject.get("categorieID").toString());	
			    dbC.updateCategorieOfProductByID(productID,categorieID);
			 return taskCode+"updated";
		 }
		 if(taskCode.equals("015")){
			 	JsonElement jelement = new JsonParser().parse(information);
			    JsonObject  jobject = jelement.getAsJsonObject();
			    int productID =  Integer.parseInt(jobject.get("productID").toString());
			    int shelflife =  Integer.parseInt(jobject.get("shelflife").toString());	
			    dbC.updateShelfLifeByID(productID,shelflife);
			 return taskCode+"updated";
		 }
		 if(taskCode.equals("016")){
			 JsonElement jelement = new JsonParser().parse(information);
			 JsonObject  jobject = jelement.getAsJsonObject();
			 String productName =  jobject.get("productName").toString();
			 return taskCode+jAPI.searchOrCreateProduct(productName);
		 }
		 if(taskCode.equals("017")){
			 return  taskCode+jAPI.getAllPhotos();
		 }
		 if(taskCode.equals("018")){
			 	JsonElement jelement = new JsonParser().parse(information);
			    JsonObject  jobject = jelement.getAsJsonObject();
			    int storedId =  Integer.parseInt(jobject.get("storedID").toString());	
			   dbC.deleteStoredProductByStoreID(storedId);
			 return taskCode+"deleted";
		 }
		 if(taskCode.equals("019")){
			 	JsonElement jelement = new JsonParser().parse(information);
			    JsonObject  jobject = jelement.getAsJsonObject();
			    int storedId =  Integer.parseInt(jobject.get("storedID").toString());
			    String newExpDateStr = jobject.get("newExpDate").toString().substring(1,jobject.get("newExpDate").toString().length()-1);
			    //Fri Sep 16 19:24:43 GMT+00:00 2016
			    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
			   
			    Date newExpDate = sdf.parse(newExpDateStr);
			   dbC.updateExpireDateOfdStoredProductByStoreID(storedId,newExpDate);
			 return taskCode+"deleted";
		 }
		 if(taskCode.equals("020")){
			 JsonElement jelement = new JsonParser().parse(information);
			 JsonObject  jobject = jelement.getAsJsonObject();
			 String productName =  jobject.get("productName").toString();
			 return  taskCode+jAPI.searchStoredProductsByName(productName);
		 }
		 if(taskCode.equals("021")){
			 System.out.println(taskCode+jAPI.searchAllShoppingLists());
			
			 return  taskCode+jAPI.searchAllShoppingLists();
		 }
		 if(taskCode.equals("022")){
			 JsonElement jelement = new JsonParser().parse(information);
			    JsonObject  jobject = jelement.getAsJsonObject();
			    int productid =  Integer.parseInt(jobject.get("productid").toString());
			    int shoppinglistid =  Integer.parseInt(jobject.get("shoppinglistid").toString());
			    dbC.deleteProductFromShoppingList(productid, shoppinglistid);
			
			 return  taskCode+"deleted";
		 }
		 
		 if(taskCode.equals("023")){
			return taskCode+jAPI.searchProductsForAutocomplete();
		 }
		 if(taskCode.equals("024")){
			 	JsonElement jelement = new JsonParser().parse(information);
			    JsonObject  jobject = jelement.getAsJsonObject();
			    int productid =  Integer.parseInt(jobject.get("productid").toString());
			    int shoppinglistid =  Integer.parseInt(jobject.get("shoppinglistid").toString());
			    dbC.addProducttoShoppingList(productid, shoppinglistid);
			
			 return  taskCode+"added";
		 }
		 if(taskCode.equals("025")){
			 	JsonElement jelement = new JsonParser().parse(information);
			    JsonObject  jobject = jelement.getAsJsonObject();
			    String productname =  jobject.get("name").toString();

			    dbC.insertNewProduct(0,productname);
			
			 return  taskCode+jAPI.searchProductByName(productname);
		 }
		 if(taskCode.equals("026")){
			 	JsonElement jelement = new JsonParser().parse(information);
			    JsonObject  jobject = jelement.getAsJsonObject();
			    int shoppinglistid =  Integer.parseInt(jobject.get("shoppinglistid").toString());
			    dbC.deleteShoppingListById(shoppinglistid);
			
			 return  taskCode+"added";
		 }
		 if(taskCode.equals("027")){
			 	JsonElement jelement = new JsonParser().parse(information);
			    JsonObject  jobject = jelement.getAsJsonObject();
			    int shoppinglistid =  Integer.parseInt(jobject.get("shoppinglistid").toString());
			    String newshoppinglistname =  jobject.get("name").toString();
			    dbC.updateShoppingListNameById(shoppinglistid,newshoppinglistname);
			
			 return  taskCode+"updated";
		 }
		 if(taskCode.equals("028")){

			
			 return  taskCode+jAPI.getNewShoppingList();
		 }
		 if(taskCode.equals("029")){

				
			 return  taskCode+jAPI.getDeletedProducts();
		 }
 

	
		 
		 return taskCode+"fail";
		 
	 }

}

package RPIgetItem;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.Gson;

import data.Product;
import data.ShoppingList;
import database.DBClass;

public class JsonAPI {
	
	
	private static JsonAPI instance;
	private DBClass dbC = DBClass.getInstance( );
	
	private JsonAPI() {
	}

	public static JsonAPI getInstance() {
		if (JsonAPI.instance == null) {
			JsonAPI.instance = new JsonAPI();
		}
		return JsonAPI.instance;
	}
	
	public String searchAllProductsByCategorie(int categorieID) throws SQLException{
		
		return objectToJson(dbC.getProductsByCategorie(0,categorieID));
	}
	public String searchStoredProductsByCategorie(int categorieID) throws SQLException{
		
		return objectToJson(dbC.getProductsByCategorie(1,categorieID));
	}
	public String searchAllCategories() throws SQLException{
		
		return objectToJson(dbC.getallCategories());
	}
	public String searchOrCreateProduct(String productName) throws SQLException{
		
		return objectToJson(dbC.searchOrCreateProduct(productName));
	}
	
	public String objectToJson(Object obj){
		
		return new Gson().toJson(obj);
	}
	public String getAllPhotos() throws SQLException{
		
		return objectToJson(dbC.getPircturesFromDB());
	}
	public String searchStoredProductsByName(String name) throws SQLException{
		
		return objectToJson(dbC.getStoredProductsByName(name));
	}
	public String searchAllShoppingLists() throws SQLException{
		return objectToJson(dbC.getAllShoppingLists());
	}
	public String searchProductsForAutocomplete() throws SQLException{
		 List<Product> pList =  dbC.getProductsByCategorie(0,0);
		 JSONArray jArray = new JSONArray();
		 
		 for(Product p : pList){
			 JSONObject  jObject = new JSONObject ();
			 jObject.put("id", p.getProductID());
			 jObject.put("name",p.getProductname());
			 jArray.add(jObject);				 			 
		 }
		
		return jArray.toJSONString();
	}
	public String searchProductByName(String name) throws SQLException{
		return objectToJson(dbC.searchProductByName(name));
	}
	public String getNewShoppingList() throws SQLException{
		return objectToJson(dbC.createNewShoppingList());
	}
	public String getDeletedProducts() throws SQLException{
		return objectToJson(dbC.getDeletedProducts());
	}

	
	
	
	

}

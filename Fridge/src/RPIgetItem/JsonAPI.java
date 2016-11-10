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

/**
 * Converts Java Objects to JSON
 */
public class JsonAPI {
	
	

	private static JsonAPI instance;

	private DBClass dbC = DBClass.getInstance( );
	
	/**
	 * Constructor
	 */
	private JsonAPI() {
	}

	/**
	 * Singleton pattern, returns a Object of JsonAPI if it already exists or creats a new Object if it does not exist yet
	 * @return
	 */
	public static JsonAPI getInstance() {
		if (JsonAPI.instance == null) {
			JsonAPI.instance = new JsonAPI();
		}
		return JsonAPI.instance;
	}
	
	/**
	 * converts the return value of  getProductsByCategorie to Json
	 * @param categorieID
	 * @return
	 * @throws SQLException
	 */
	public String searchAllProductsByCategorie(int categorieID) throws SQLException{
		
		return objectToJson(dbC.getProductsByCategorie(0,categorieID));
	}
	/**
	 * converts the return value of  getProductsByCategorie to Json
	 * @param categorieID
	 * @return
	 * @throws SQLException
	 */
	public String searchStoredProductsByCategorie(int categorieID) throws SQLException{
		
		return objectToJson(dbC.getProductsByCategorie(1,categorieID));
	}
	/**
	 * converts the return value of  getallCategories to Json
	 * @return
	 * @throws SQLException
	 */
	public String searchAllCategories() throws SQLException{
		
		return objectToJson(dbC.getallCategories());
	}
	/**
	 * converts the return value of  searchOrCreateProduct to Json
	 * @param productName
	 * @return
	 * @throws SQLException
	 */
	public String searchOrCreateProduct(String productName) throws SQLException{
		
		return objectToJson(dbC.searchOrCreateProduct(productName));
	}
	
	/**
	 * converts the returns a given object to Json
	 * @param obj
	 * @return
	 */
	public String objectToJson(Object obj){
		
		return new Gson().toJson(obj);
	}
	/**
	 * converts the return value of  getPircturesFromDB to Json
	 * @return
	 * @throws SQLException
	 */
	public String getAllPhotos() throws SQLException{
		
		return objectToJson(dbC.getPircturesFromDB());
	}
	/**
	 * converts the return value of  getStoredProductsByName to Json
	 * @param name
	 * @return
	 * @throws SQLException
	 */
	public String searchStoredProductsByName(String name) throws SQLException{
		
		return objectToJson(dbC.getStoredProductsByName(name));
	}
	/**
	 * converts the return value of  getAllShoppingLists to Json
	 * @return
	 * @throws SQLException
	 */
	public String searchAllShoppingLists() throws SQLException{
		return objectToJson(dbC.getAllShoppingLists());
	}
	/**
	 * converts the return value of  getProductsByCategorie to Json
	 * @return
	 * @throws SQLException
	 */
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
	/**
	 * converts the return value of  searchProductByName to Json
	 * @param name
	 * @return
	 * @throws SQLException
	 */
	public String searchProductByName(String name) throws SQLException{
		return objectToJson(dbC.searchProductByName(name));
	}
	/**
	 * converts the return value of  createNewShoppingList to Json
	 * @return
	 * @throws SQLException
	 */
	public String getNewShoppingList() throws SQLException{
		return objectToJson(dbC.createNewShoppingList());
	}
	/**
	 * converts the return value of  getDeletedProducts to Json
	 * @return
	 * @throws SQLException
	 */
	public String getDeletedProducts() throws SQLException{
		return objectToJson(dbC.getDeletedProducts());
	}

	
	
	
	

}

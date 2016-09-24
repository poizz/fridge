package RPIgetItem;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import data.Product;
import database.DBClass;

public class testProject {
	
	static DBClass dbC = DBClass.getInstance( );
	static JsonAPI i =  JsonAPI.getInstance( );
	
	public static void main(String[] args) throws Exception {
		
		test();
	} 
	
	public static void test() throws Exception
	{
		//testSearchProduct();
		//testStoreProduct();
		//testExpDateupdate();
		//testUpdateCat();
		//testSearchProductsByCategorie();
		testAddImg();

		
		
	}

public static void testAddImg() throws SQLException, IOException{
	dbC.inserPic();
}
		
	public static void testExpDateupdate() throws SQLException
	{
		Calendar today = Calendar.getInstance();
		today.clear(Calendar.HOUR); today.clear(Calendar.MINUTE); today.clear(Calendar.SECOND);
		Date todayDate = today.getTime();
		
		dbC.updateExpireDateOfdStoredProductByID(2, addDaysToDate2(todayDate, 120));
	}
	
	public static void testUpdateCat() throws SQLException
	{
		dbC.updateCategorieOfProductByID(2,4);
	}
	
	private static Date addDaysToDate2(Date date, int days)
	{
		if(date != null){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); 
        return cal.getTime();
		}
		else return null;

	}

}

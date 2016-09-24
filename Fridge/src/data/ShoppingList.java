package data;

import java.util.Date;
import java.util.List;

public class ShoppingList {
	
		private int id;
	  	private String name;
	    private Date dateOfCreation;
	    private int productCount;
	    List<Product> products;

	    public ShoppingList(String name,int id, Date dateOfCreation, List<Product> products) {
	        this.name = name;
	        this.dateOfCreation = dateOfCreation;
	        this.products = products;
	        this.id = id;
	        productCount = products.size();
	    }
	    
	    public ShoppingList(int id,String name, Date dateOfCreation) {
	    	this.id = id;
	        this.name = name;
	        this.dateOfCreation = dateOfCreation;
	        
	    }

	    public void setProductCount(int productCount) {
	        this.productCount = productCount;
	    }

	    public String getName() {

	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public Date getDateOfCreation() {
	        return dateOfCreation;
	    }

	    public int getProductCount() {
	        return productCount;
	    }

	    public void setDateOfCreation(Date dateOfCreation) {
	        this.dateOfCreation = dateOfCreation;
	    }

	    public List<Product> getProducts() {
	    	productCount = products.size();
	        return products;
	    }

	    public void setProducts(List<Product> products) {
	        this.products = products;
	    }

}

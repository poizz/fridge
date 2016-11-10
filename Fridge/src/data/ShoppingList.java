package data;

import java.util.Date;
import java.util.List;

public class ShoppingList {
	
		private int id;
	  	private String name;
	    private Date dateOfCreation;
	    private int productCount;
	    List<Product> products;

	    /**
	     * constructor
	     * @param name
	     * @param id
	     * @param dateOfCreation
	     * @param products
	     */
	    public ShoppingList(String name,int id, Date dateOfCreation, List<Product> products) {
	        this.name = name;
	        this.dateOfCreation = dateOfCreation;
	        this.products = products;
	        this.id = id;
	        productCount = products.size();
	    }
	    
	    /**
	     * constructor
	     * @param id
	     * @param name
	     * @param dateOfCreation
	     */
	    public ShoppingList(int id,String name, Date dateOfCreation) {
	    	this.id = id;
	        this.name = name;
	        this.dateOfCreation = dateOfCreation;
	        
	    }

	    /**
	     * sets the productCount
	     * @param productCount
	     */
	    public void setProductCount(int productCount) {
	        this.productCount = productCount;
	    }

	    /**
	     * returns the name of the shoppinglist
	     * @return
	     */
	    public String getName() {

	        return name;
	    }

	    /**
	     * sets the name of the shoppinglist
	     * @param name
	     */
	    public void setName(String name) {
	        this.name = name;
	    }

	    /**
	     * returns the date of creation of a shoppinglist
	     * @return
	     */
	    public Date getDateOfCreation() {
	        return dateOfCreation;
	    }

	    /**
	     * returns the amount of products in the shoppinglist
	     * @return
	     */
	    public int getProductCount() {
	        return productCount;
	    }

	    /**
	     * sets the date of creation of the shoppinglist
	     * @param dateOfCreation
	     */
	    public void setDateOfCreation(Date dateOfCreation) {
	        this.dateOfCreation = dateOfCreation;
	    }

	    /**
	     * returns a list of all products of the shoppinglist
	     * @return
	     */
	    public List<Product> getProducts() {
	    	productCount = products.size();
	        return products;
	    }

	    /**
	     * sets the list of all products of the shoppinglist
	     * @param products
	     */
	    public void setProducts(List<Product> products) {
	        this.products = products;
	    }

}

package data;

public class Categorie {
	
private String categorieName;
private int categorieID;

/**
 * @param categorieName name of the categorie
 * @param categorieID id of the categorie
 */
public Categorie(String categorieName,int categorieID){
	this.categorieName = categorieName;
	this.categorieID = categorieID;
}

/**
 * @return name of the categorie
 */
public String getCategorieName() {
	return categorieName;
}
/**
 * @param categorieName name of the categorie
 */
public void setCategorieName(String categorieName) {
	this.categorieName = categorieName;
}
/**
 * @return id of the categorie
 */
public int getCategorieID() {
	return categorieID;
}
/**
 * @param categorieID id of the categorie
 */
public void setCategorieID(int categorieID) {
	this.categorieID = categorieID;
}
}

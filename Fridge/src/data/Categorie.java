package data;

public class Categorie {
	
private String categorieName;
private int categorieID;

public Categorie(String categorieName,int categorieID){
	this.categorieName = categorieName;
	this.categorieID = categorieID;
}

public String getCategorieName() {
	return categorieName;
}
public void setCategorieName(String categorieName) {
	this.categorieName = categorieName;
}
public int getCategorieID() {
	return categorieID;
}
public void setCategorieID(int categorieID) {
	this.categorieID = categorieID;
}
}

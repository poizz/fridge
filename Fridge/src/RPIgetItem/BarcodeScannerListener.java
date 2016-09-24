package RPIgetItem;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.LogManager;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import data.Product;
import database.DBClass;
import gcm.SendGCM;

public class BarcodeScannerListener implements NativeKeyListener {
	private SendGCM sendgcm = new SendGCM();
	private DBClass dbC = DBClass.getInstance( );
	private JsonAPI jAPI = JsonAPI.getInstance( );
	private String barcode = "";
	long lastInput;
	int scanStatus = 1;
	private String registerToken;
	
	
	public String getRegisterToken() {
		return registerToken;
	}

	public void setRegisterToken(String registerToken) {
		this.registerToken = registerToken;
		System.out.println("Register token is set: "+registerToken);
	}

	

	
	
	public int getScanStatus() {
		return scanStatus;
	}

	public void setScanStatus(int scanStatus) {
		this.scanStatus = scanStatus;
	}

	private static BarcodeScannerListener instance;


	public static BarcodeScannerListener getInstance() {
		if (BarcodeScannerListener.instance == null) {
			BarcodeScannerListener.instance = new BarcodeScannerListener();
		}
		return BarcodeScannerListener.instance;
	}

	public void startListener()
	{
		LogManager.getLogManager().reset();  	
        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }
        GlobalScreen.addNativeKeyListener(BarcodeScannerListener.getInstance());    
	}
	
    public void nativeKeyPressed(NativeKeyEvent e) {
         	 	
        if(barcode.length()==13){
        	try {
        		
        		//überprüft ob produkt im system, falls nein speichere produkt
        		jAPI.objectToJson(scannedProduct(Long.parseLong(barcode)));
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	barcode = "";
        }else{
        	if(System.currentTimeMillis()-lastInput>10){
        		barcode = "";
        	}
        	barcode = barcode + NativeKeyEvent.getKeyText(e.getKeyCode());
        	lastInput=System.currentTimeMillis();       	
        }
        if (e.getKeyCode() == NativeKeyEvent.VC_KP_DIVIDE) {
            try {
				GlobalScreen.unregisterNativeHook();
			} catch (NativeHookException e1) {
				// TODO Auto-generated catch blocka4002604009256				
				e1.printStackTrace();
			}
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {

    }
    public void nativeKeyTyped(NativeKeyEvent e) {

    }
	 public Product scannedProduct(long barcode) throws Exception
		{
			Product prod = null;
			String itemName = "";
			
			HttpUrlCon httpCon = HttpUrlCon.getInstance( );		
			prod = dbC.searchProductByID(dbC.getIDByBarcode(barcode));	
			
			if(prod.getProductname().length() <= 1){	
				
								
				System.out.println("Item not in Database, searching Online");
				itemName = httpCon.getProduktName(barcode);
				
				if(itemName.length()>1){
					
					dbC.insertNewProduct(barcode,itemName);
					prod = dbC.searchProductByID(dbC.getIDByBarcode(barcode));
					storeOrDelete(prod);
				
				}else{
					itemName = "Item not Found";
					prod = new Product(itemName);
				}
			}else{			
				storeOrDelete(prod);
			}
			
			return prod;
		}
	    public void storeOrDelete(Product p) throws SQLException, ParseException, IOException
	    {
	    	System.out.println("Der Status ist: "+scanStatus);;
			if(scanStatus == 0){
				System.out.println(p.getProductname()+" has been stored");
				dbC.storeProduct(p);				
				sendgcm.sendGCMMsg(jAPI.objectToJson(p), "010", registerToken);
				
				
			}else{
				System.out.println(p.getProductname()+" has been deletet");
				dbC.deleteStoredProductByID(p.getProductID());
				dbC.addDeletedItem(p.getProductID());
				System.out.println("das json: "+jAPI.objectToJson(p));
				sendgcm.sendGCMMsg(jAPI.objectToJson(p), "011", registerToken);

			}
	    }
   
    public void sendCloudMassege(String msg){
    	
    }
}
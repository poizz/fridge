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
	
	
	/**
	 * returns the registertoken from the client
	 * @return the registertoken of the client
	 */
	public String getRegisterToken() {
		return registerToken;
	}

	/**
	 * sets the registertoken
	 * @param registerToken the registertoken of the client
	 */
	public void setRegisterToken(String registerToken) {
		this.registerToken = registerToken;
		System.out.println("Register token is set: "+registerToken);
	}

	

	
	
	/**
	 * returns the scanstatus (1 = delete item, 0 = add item)
	 * @return the status of the scanner (1 = delete item, 0 = add item)
	 */
	public int getScanStatus() {
		return scanStatus;
	}

	/**
	 * sets the scanstatus (1 = delete item, 0 = add item)
	 * @param scanStatus
	 */
	public void setScanStatus(int scanStatus) {
		this.scanStatus = scanStatus;
	}

	private static BarcodeScannerListener instance;


	/**
	 * singleton pattern
	 * @return an object of the class BarcodeScannerListener
	 */
	public static BarcodeScannerListener getInstance() {
		if (BarcodeScannerListener.instance == null) {
			BarcodeScannerListener.instance = new BarcodeScannerListener();
		}
		return BarcodeScannerListener.instance;
	}

	/**
	 * starts the keyboardlistener which listens to the barcodescanner (Barcodes are deliverd as Keyboardsignal)
	 */
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
	
    /* (non-Javadoc)
     * @see org.jnativehook.keyboard.NativeKeyListener#nativeKeyPressed(org.jnativehook.keyboard.NativeKeyEvent)
     */
    public void nativeKeyPressed(NativeKeyEvent e) {
         	 	
        if(barcode.length()==13){
        	try {
        		
        		//�berpr�ft ob produkt im system, falls nein speichere produkt
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

    /* (non-Javadoc)
     * @see org.jnativehook.keyboard.NativeKeyListener#nativeKeyReleased(org.jnativehook.keyboard.NativeKeyEvent)
     */
    public void nativeKeyReleased(NativeKeyEvent e) {

    }
    /* (non-Javadoc)
     * @see org.jnativehook.keyboard.NativeKeyListener#nativeKeyTyped(org.jnativehook.keyboard.NativeKeyEvent)
     */
    public void nativeKeyTyped(NativeKeyEvent e) {

    }
	 /**
	  * looks in the database the product of the given barcode. If the product is not in the databse, httpCon will be asked to look online for the prodcuct.
	  * 
	 * @param barcode
	 * @return the product which has been scannd
	 * @throws Exception
	 */
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
	    /**
	     * Stores or delets a given product depending on the scanstatus
	     * @param p Product which want to be stored or deleted
	     * @throws SQLException
	     * @throws ParseException
	     * @throws IOException
	     */
	    public void storeOrDelete(Product p) throws SQLException, ParseException, IOException
	    {
	    	System.out.println("Der Status ist: "+scanStatus);;
			if(scanStatus == 0){
				System.out.println(p.getProductname()+" has been stored");
				dbC.storeProduct(p);				
				sendgcm.sendGCMMsg(jAPI.objectToJson(p), "010", registerToken);
				
				
			}else{
				sendgcm.sendGCMMsg(jAPI.objectToJson(p), "011", registerToken);
				System.out.println(p.getProductname()+" has been deletet");
				dbC.deleteStoredProductByID(p.getProductID());
				dbC.addDeletedItem(p.getProductID());
				System.out.println("das json: "+jAPI.objectToJson(p));


			}
	    }
   

    public void sendCloudMassege(String msg){
    	
    }
}
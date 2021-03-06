package grpio;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.ImageIO;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import database.DBClass;

/**
 * handles the gpio pin communication
 * @author poiz
 *
 */
public class PinController {
	
	
	private static GpioPinDigitalOutput pinMeatAndFish;
	private static GpioPinDigitalOutput pinOthers;
	private static GpioPinDigitalOutput pinMilkAndEggs;
	private static GpioPinDigitalOutput pinFruitAndVegtables;
	//private static GpioPinDigitalOutput pinBarcodeScanner;
	private static final GpioController gpioC = GpioFactory.getInstance();;
	GpioPinDigitalInput photoSwitch;
	private DBClass dbC;
	
	/**
	 * constructor
	 */
	public PinController(){
		
		dbC = DBClass.getInstance( );
		
		System.out.println("initiiert");
		
		//Position Indicatior Pins
		pinMeatAndFish = gpioC.provisionDigitalOutputPin(RaspiPin.GPIO_00,"MeatAndFish",PinState.LOW);
		pinMilkAndEggs = gpioC.provisionDigitalOutputPin(RaspiPin.GPIO_01,"pinMilkAndEggs",PinState.LOW);
		pinFruitAndVegtables = gpioC.provisionDigitalOutputPin(RaspiPin.GPIO_02,"pinFruitAndVegtables",PinState.LOW);
		pinOthers = gpioC.provisionDigitalOutputPin(RaspiPin.GPIO_03,"pinOthers",PinState.LOW);
		
		//BarcodeScanner Pin
		//pinBarcodeScanner = gpioC.provisionDigitalOutputPin(RaspiPin.GPIO_05,"BarcodeScanner",PinState.LOW);
		//photoswitch
		photoSwitch = gpioC.provisionDigitalInputPin(RaspiPin.GPIO_07, PinPullResistance.PULL_DOWN);
		photoSwitch.setShutdownOptions(true);
		photoSwitch.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
            	
                System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
                if(event.getState()==PinState.HIGH){
                	System.out.println("yey");
                	try {
						TakeAndSavePicture();
					} catch (IOException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }

        });
		

				
	
		
	}
	
	//Position Indicatior Functions
	
	/**
	 * Sets the GPIO-Pin for the LED of a given Categorie to High 
	 * @param categorie categorie of a product to light up the right LED
	 */
	public void positionIndicatorLed(int categorie){
		
		setAllPinsLow();
		System.out.println("storeInPinC "+ categorie);
		
		switch(categorie){
			case 1:
				pinMeatAndFish.setState((PinState.HIGH));
				setPinStateLowAfterDelay(pinMeatAndFish);
				break;
			case 2:
				pinMilkAndEggs.setState((PinState.HIGH));
				setPinStateLowAfterDelay(pinMilkAndEggs);
				break;
			case 3:
				pinFruitAndVegtables.setState((PinState.HIGH));
				setPinStateLowAfterDelay(pinFruitAndVegtables);
				break;
			case 4:
				pinOthers.setState((PinState.HIGH));
				setPinStateLowAfterDelay(pinOthers);
				break;
		}
	}
	
	/**
	 * calls setAllPinsLow after 5 seconds
	 * @param pin Gpio pin which hast to be low
	 */
	private void setPinStateLowAfterDelay(GpioPinDigitalOutput pin){
		new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	setAllPinsLow();
		            }
		        }, 
		        5000 
		);

	}
	/**
	 * Sets all GPIO-Pins for the LED�s to low
	 */
	private void setAllPinsLow(){
		
		pinMeatAndFish.setState(PinState.LOW);
		pinMilkAndEggs.setState(PinState.LOW);
		pinFruitAndVegtables.setState(PinState.LOW);
		pinOthers.setState(PinState.LOW);
	}
	
	//BarcodeScanner
	/**
	 * not in use
	 */
	private void toggleBarcodeScanner(){
		//pinBarcodeScanner.toggle();
	}
	
	//TakeAndSavePicture
	/**
	 * takes a picture of the Webcam and saves it as fridge.jpg
	 * @throws IOException
	 * @throws SQLException
	 */
	private void TakeAndSavePicture() throws IOException, SQLException{
		
		String currentDir = System.getProperty("user.dir");
        Runtime.getRuntime().exec("sudo fswebcam -r 1280x7 -d /dev/video0 "+currentDir+"/fridge.jpg");    
	    File file = new File("fridge.jpg");
	    System.out.println("ok2");
        dbC.storePic(0, file);
        
	}
	

}

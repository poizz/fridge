package grpio;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

public class PinController {
	
	
	private static GpioPinDigitalOutput pinMeatAndFish;
	private static GpioPinDigitalOutput pinOthers;
	private static GpioPinDigitalOutput pinMilkAndEggs;
	private static GpioPinDigitalOutput pinFruitAndVegtables;
	private static GpioPinDigitalOutput pinBarcodeScanner;
	private static GpioController gpioC;
	final GpioPinDigitalInput photoSwitch;
	private DBClass dbC;
	
	public PinController(){
		
		dbC = DBClass.getInstance( );
		
		gpioC = GpioFactory.getInstance();
		
		//Position Indicatior Pins
		pinMeatAndFish = gpioC.provisionDigitalOutputPin(RaspiPin.GPIO_00,"MeatAndFish",PinState.LOW);
		pinMilkAndEggs = gpioC.provisionDigitalOutputPin(RaspiPin.GPIO_01,"pinMilkAndEggs",PinState.LOW);
		pinFruitAndVegtables = gpioC.provisionDigitalOutputPin(RaspiPin.GPIO_02,"pinFruitAndVegtables",PinState.LOW);
		pinOthers = gpioC.provisionDigitalOutputPin(RaspiPin.GPIO_03,"pinOthers",PinState.LOW);
		
		//BarcodeScanner Pin
		pinOthers = gpioC.provisionDigitalOutputPin(RaspiPin.GPIO_05,"BarcodeScanner",PinState.LOW);
				
		//Photo switch options
		photoSwitch = gpioC.provisionDigitalInputPin(RaspiPin.GPIO_04, PinPullResistance.PULL_DOWN);
		photoSwitch.setShutdownOptions(true);
		photoSwitch.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
                if(event.getState()==PinState.HIGH){
                	try {
						TakeAndSavePicture();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	toggleBarcodeScanner();
                }
            }
        });
		
		
	}
	
	//Position Indicatior Functions
	
	public void positionIndicatorLed(int categorie){
		setAllPinsLow();
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
	
	private void setPinStateLowAfterDelay(GpioPinDigitalOutput pin){
		
		new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	pin.setState(PinState.LOW);
		            }
		        }, 
		        5000 
		);
	}
	private void setAllPinsLow(){
		
		pinMeatAndFish.setState(PinState.LOW);
		pinMilkAndEggs.setState(PinState.LOW);
		pinFruitAndVegtables.setState(PinState.LOW);
		pinOthers.setState(PinState.LOW);
	}
	
	//BarcodeScanner
	private void toggleBarcodeScanner(){
		pinBarcodeScanner.toggle();
	}
	
	//TakeAndSavePicture
	private void TakeAndSavePicture() throws IOException, SQLException{
		
		String currentDir = System.getProperty("user.dir");
        Runtime.getRuntime().exec("sudo fswebcam -r 640x480 -d /dev/video0 "+currentDir+"/fridge.jpg");      
	    File file = new File(currentDir+"/fridge.jpg");
        dbC.storePic(0, file);
        
	}
}
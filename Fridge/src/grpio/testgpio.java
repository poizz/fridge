package grpio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * test purpose
 */
public class testgpio {
	private static GpioPinDigitalOutput tPin;
	private static GpioPinDigitalInput input;
	private static GpioController gpioC;
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		testgpio t = new testgpio();
		try {
			t.test();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	/**
	 * @throws IOException
	 */
	public void test() throws IOException{
		 
		gpioC = GpioFactory.getInstance();
		tPin = gpioC.provisionDigitalOutputPin(RaspiPin.GPIO_00,"MeatAndFish",PinState.LOW);
		input = gpioC.provisionDigitalInputPin(RaspiPin.GPIO_07, PinPullResistance.PULL_DOWN);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		input.setShutdownOptions(true);
		input.addListener(new GpioPinListenerDigital() {
        @Override
        public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        	System.out.println("--------------------------------------------------------------------");
            // display pin state on console
            if(event.getState()==PinState.HIGH){
            		System.out.println("imput");
            	
            }
        }
    });

		
		
		/*while(true){
		      
		        System.out.print("Enter String");
		        String s = br.readLine();
		        if(s.equals("k")){
		        	if(tPin.getState()==PinState.HIGH){
		        		tPin.setState(PinState.LOW);
		        		System.out.println("ist low");
		        	}else{
		        		tPin.setState(PinState.HIGH);
		        		System.out.println("ist high");
		        	}
		        }

		}*/
	}

}

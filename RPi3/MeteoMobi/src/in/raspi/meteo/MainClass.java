package in.raspi.meteo;

import in.raspi.meteo.DHT22;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;
import java.util.*;
import java.lang.Thread;
import java.time.LocalDateTime;


public class MainClass extends java.lang.Thread {
	private final GpioController gpio = GpioFactory.getInstance();
	private final GpioPinDigitalOutput trigger1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, PinState.LOW);
    private final GpioPinDigitalOutput trigger2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_23, PinState.LOW);
    
    public MainClass(GpioController gpio){ 
        super(); 
    } 

	public static void main(String[] args) throws InterruptedException {
		MainClass mainmethod = new MainClass(GpioFactory.getInstance()); 
		
        mainmethod.loop(args); 
	}
	
	protected void loop(String[] args) throws InterruptedException {
		DHT22 dht22 = new DHT22();
		ArrayList<Double> wind_measureNS = new ArrayList<>(); // = new ArrayList<>();
		ArrayList<Double> wind_measureEW = new ArrayList<>();
		ArrayList<Float> temp_measure = new ArrayList<>();
		ArrayList<Float> humidity_measure = new ArrayList<>();
		do { 
			double AirspeedNS = ping(trigger1, 24);
	    	double AirspeedEW = ping(trigger2, 22);
	    	float[] temphumi = dht22.getTemperature(12);
	    	wind_measureNS.add(AirspeedNS);
	    	wind_measureEW.add(AirspeedEW);
	    	if (temphumi != null) {
	    		humidity_measure.add(temphumi[0]);
		    	temp_measure.add(temphumi[1]);
	    	}
	    	if (LocalDateTime.now().getSecond() % 15 == 0) {
	    		Collections.sort(wind_measureNS);
	    		Collections.sort(wind_measureEW);
	    		Collections.sort(humidity_measure);
	    		Collections.sort(temp_measure);
	    		try {
	    			compare(wind_measureNS.get(wind_measureNS.size()/2), wind_measureEW.get(wind_measureEW.size()/2), 
	    				temp_measure.get(temp_measure.size()/2), humidity_measure.get(humidity_measure.size()/2));
	    		}
	            catch (IndexOutOfBoundsException e) {
	            	Thread.sleep(1000);
	            }
	    		Thread.sleep(1000);
	    	}
	    	
		} while(!interrupted());
	}
	
	public double ping(GpioPinDigitalOutput trigger, int pins) throws InterruptedException {
		double StartTime = 0;
        double StopTime = 0;
		trigger.high();
    	Thread.sleep(0, 10000);
    	trigger.low();
    	while (Gpio.digitalRead(pins) == 0) {
    		StartTime = System.nanoTime();
    	}
    	while (Gpio.digitalRead(pins) == 1) {
    		StopTime = System.nanoTime();
    	}
    	double diff = (StopTime-StartTime)/Math.pow(10,  9);
    	if (pins == 24) {
    		return (0.115 / 2) * ((0.000321 - diff) / (0.000321 * diff));
    	}
    	else {
    		return (0.115 / 2) * ((0.000345 - diff) / (0.000345 * diff));
    	}
	}
	
	public void compare(double NS, double EW, float temp, float hum) {
		String direct = new String();
		double windspeed;
        if (NS >= 0.0 && EW >= 0.0) {
        	if (NS/EW > 2.0)
                direct = "N";
            if (EW/NS > 2.0)
                direct = "E";
            else
                direct = "NE";
        }  
        else if (NS >= 0.0 && 0.0 > EW) {
            if (NS/EW < -2.0)
                direct = "N";
            else if (EW/NS < -2.0)
                direct = "W";
            else
                direct = "NW";
        }
        else if (NS < 0.0 && 0.0 <= EW) {
            if (NS/EW < -2.0)
                direct = "S";
            else if (EW/NS < -2.0)
                direct = "E";
            else
                direct = "SE";
        }
        else if (NS < 0.0 && EW < 0.0) {
            if (NS/EW > 2.0)
                direct = "S";
            else if (EW/NS > 2.0)
                direct = "W";
            else
                direct = "SW";
        }
        windspeed = Math.sqrt(Math.pow(NS, 2) + Math.pow(EW, 2));
        if (windspeed == 0)
        	direct = null;
        try {
    		Connector conn = new Connector((float)windspeed, direct, temp, hum);
        	conn.connector();
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
	    System.out.println(String.format("%.1f", windspeed).replace(",", ".") + " " + direct + " " + temp + " " + hum);
	}
	
	
}

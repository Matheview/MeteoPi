package in.raspi.meteo;

import com.pi4j.wiringpi.Gpio;

public class DHT22 {
	private int pulseCounts[] = new int[82];
	private int DHT_PULSES = 41;
	private int DHT_MAXCOUNT = 32000;
	private float temperature = 0.0f;
	private float humidity = 0.0f;
	
	public DHT22() {
		super();
	}
	
	public float[] getTemperature(final int pin) throws InterruptedException {
	    Gpio.pinMode(pin, Gpio.OUTPUT);
	    Gpio.digitalWrite(pin, Gpio.HIGH);
	    Gpio.delay(500);
	    Gpio.digitalWrite(pin, Gpio.LOW);
	    Gpio.delay(20);
	    Gpio.pinMode(pin, Gpio.INPUT);
	    for (int i = 0; i < 50; ++i) {
	    }
	    
	    int count = 0;
	    while (Gpio.digitalRead(pin) == 1) {
	        if (++count >= DHT_MAXCOUNT) {
	            return null;
	        }
	    }
	
	    for (int i=0; i < 82; i+=2) {
	        while (Gpio.digitalRead(pin) == 0) {
	            if (++pulseCounts[i] >= DHT_MAXCOUNT) {
	                return null;
	            }
	        }
	
	        while (Gpio.digitalRead(pin) == 1) {
	            if (++pulseCounts[i+1] >= DHT_MAXCOUNT) {
	            	return null;
	            }
	        }
	    }
	    return decoder(pulseCounts);
	}
	    
	public float[] decoder(int pulseCounts[]) {
	    int threshold = 0;
	    for (int i=2; i < DHT_PULSES*2; i+=2) {
	    	threshold += pulseCounts[i];
	    }
	    threshold /= DHT_PULSES-1;
	
	    int data[] = new int[5];
	    for (int i=3; i < DHT_PULSES*2; i+=2) {
	    	int index = (i-3)/16;
	    	data[index] <<= 1;
	    	if (pulseCounts[i] >= threshold) {
	    		data[index] |= 1;
	    	}
	    }
	
	    if (data[4] == ((data[0] + data[1] + data[2] + data[3]) & 0xFF)) {
	    	humidity = (data[0] * 256 + data[1]) / 10.0f;
	    	temperature = ((data[2] & 0x7F) * 256 + data[3]) / 10.0f;
	    	if ((data[2] & 0x80) != 0) {
	    		temperature *= -1.0f;
	    	}
	    	return new float[] {humidity, temperature};
	    } 
	    else {
	    	return null;
	    }
	}
}

package meteoapp;

import java.util.Date;

public class DataDoubleFormat {
	public double wind;
	public Date data;
	
	public DataDoubleFormat(double wind, Date data) {
		this.wind = wind;
		this.data = data;
	}
	
	public double getDouble() {
		return this.wind;
	}
	
	public Date getDate() {
		return this.data;
	}
}

package in.raspi.meteo;

import java.sql.*;


public class Connector {
	float windspeed;
	String direct;
	float temp; 
	float humidity;
	String symbol = "MMRZ02";
	
	public Connector(float windspeed, String direct, float temp, float humidity) {
		this.windspeed = windspeed;
		this.direct = direct;
		this.temp = temp;
		this.humidity = humidity;
	}
	
	private double getWind() {
		return Double.parseDouble(String.format("%.1f", this.windspeed).replace(",", "."));
	}
	
	private String getDirection() {
		return this.direct;
	}
	
	private double getTemperature() {
		return Double.parseDouble(String.format("%.1f", this.temp).replace(",", "."));
	}
	
	private double getHumidity() {
		return Double.parseDouble(String.format("%.1f", this.humidity).replace(",", "."));
	}
	
	public void connector() throws ClassNotFoundException, SQLException {
		Class.forName("org.postgresql.Driver");
		String url = "*";
		String user = "*";
		String password = "*";
		Connection conn = DriverManager.getConnection(url, user, password);
		String sql = "INSERT INTO measures(andon_id, temp, humidity, wind_speed, wind_direct, evt_time) VALUES ((SELECT id FROM andons WHERE symbol=?), ?, ?, ?, ?, NOW())";
		PreparedStatement prpstmt = conn.prepareStatement(sql);
		prpstmt.setString(1,  this.symbol);
		prpstmt.setDouble(2,  getTemperature());
		prpstmt.setDouble(3,  getHumidity());
		prpstmt.setDouble(4,  getWind());
		prpstmt.setString(5,  getDirection());
		prpstmt.executeUpdate();
		conn.close();
	}
}

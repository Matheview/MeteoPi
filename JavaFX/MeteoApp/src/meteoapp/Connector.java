/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meteoapp;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chokm
 */
public class Connector {
    private String url = "*";
    private String user = "*";
    private String password = "*";
    
    //public double
    
    public String[] mainQuery(String symbol) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection(url, user, password);
    	String[] result = new String[7];
        String sql = "SELECT temp, humidity, wind_speed, wind_direct, location, longtitude, latitude FROM measures, andons WHERE measures.andon_id=(SELECT id FROM andons WHERE symbol=?) AND andons.symbol=? ORDER BY measures.id DESC LIMIT 1";
        PreparedStatement prpstmt;
        try {
            prpstmt = conn.prepareStatement(sql);
            prpstmt.setString(1,  symbol);
            prpstmt.setString(2,  symbol);
            ResultSet rs = prpstmt.executeQuery();
            while(rs.next()) {
	            result[0] = String.format("%.1f", rs.getDouble("temp"))+"°C";
	            result[1] = String.format("%.1f", rs.getDouble("humidity"))+"%";
	            result[2] = String.format("%.1f", rs.getDouble("wind_speed"))+"m/s";
	            result[3] = rs.getString("wind_direct");
	            result[4] = rs.getString("location");
	            result[5] = String.format("%.5f", rs.getDouble("longtitude"));
	            result[6] = String.format("%.5f", rs.getDouble("latitude"));
            }
            rs.close();
            conn.close();
            return result;
            
        } catch (SQLException ex) {
            Logger.getLogger(Connector.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            conn.close();
        }
        return null;
    }
    
    public ArrayList tempAverage(String tabler, String symbol, int end_data) throws ClassNotFoundException, SQLException {
    	ArrayList<DataDoubleFormat> result = new ArrayList<DataDoubleFormat>();
    	Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection(url, user, password);
    	String sql = "SELECT AVG("+tabler+") as avg, evt_time::date + (EXTRACT(hour FROM evt_time)::int) * '1h'::interval + (EXTRACT(minute FROM evt_time)::int) * '5m'::interval as time FROM measures WHERE andon_id=(SELECT id FROM andons WHERE symbol=?) AND evt_time<NOW()-INTERVAL '"+Integer.toString(end_data)+" hours' AND evt_time>NOW()-INTERVAL '"+Integer.toString(end_data+6)+ "hours' HOUR GROUP BY 2 ORDER BY 2";
    	PreparedStatement prpstmt;
        try {
            prpstmt = conn.prepareStatement(sql);
            prpstmt.setString(1, symbol);
            ResultSet rs = prpstmt.executeQuery();
            while(rs.next()) {
            	result.add(new DataDoubleFormat(rs.getDouble("avg"), new Date(rs.getTimestamp("time").getTime())));
            }
            rs.close();
            conn.close();
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(Connector.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            conn.close();
        }
        return null;
    }
    
    public Connection connector() throws ClassNotFoundException, SQLException {
        return connector();
    }
}

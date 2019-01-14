/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package meteoapp;

import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author chokm
 */
public class FXMLDocumentController implements Initializable{
	
	private Connector conn = new Connector();
    
    @FXML
    private Label label;
    @FXML
    private Label label_temperature;
    @FXML
    private Label label_humidity;
    @FXML
    private Label label_wind_speed;
    @FXML
    private Label label_wind_direction;
    @FXML
    private Label label_place;
    @FXML
    private Label label_updated_last;
    @FXML
    private Label label_status;
    @FXML
    private Button autorefreshing;
    @FXML
    private Circle circle_status;
    @FXML
    private ComboBox<String> combobox_location;
    @FXML
    private AreaChart<String, Number> line_charts;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    private String symbol = "MMRZ01";
    private int choose = 0;
    private int history = 0;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
    private SimpleDateFormat stddata = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Alert alert = new Alert(AlertType.ERROR, "BÅ‚Ä…d poÅ‚Ä…cenia z bazÄ… danych, sprÃ³buj ponownie pÃ³Åºniej", ButtonType.OK);
    
    
    @FXML
    private void comboboxLocationOnAction(ActionEvent event) throws ClassNotFoundException, SQLException {
    	if (combobox_location.getSelectionModel().getSelectedItem() != symbol) {
    		symbol = combobox_location.getSelectionModel().getSelectedItem();
    		try {
                    this.setSeries();
                }
                catch (ClassNotFoundException | SQLException e) {
		    label_status.setText("Offline");
                    circle_status.setFill(Color.GRAY);
                    alert.showAndWait();
                }
    	}
    }
    
    @FXML
    private void tempbuttonOnAction(ActionEvent event) throws ClassNotFoundException, SQLException {
        if (choose != 0) {
        	choose = 0;
        	try {
                    this.setSeries();
                }
                catch (ClassNotFoundException | SQLException e) {
		    label_status.setText("Offline");
                    circle_status.setFill(Color.GRAY);
                    alert.showAndWait();
                }
        };
    }
    
    @FXML
    private void humibuttonOnAction(ActionEvent event) throws ClassNotFoundException, SQLException {
    	if (choose != 1) {
        	choose = 1;
        	history = 0;
        	try {
                    this.setSeries();
                }
                catch (ClassNotFoundException | SQLException e) {
		    label_status.setText("Offline");
                    circle_status.setFill(Color.GRAY);
                    alert.showAndWait();
                }
        };
    }
    
    @FXML
    private void windsOnAction(ActionEvent event) throws ClassNotFoundException, SQLException {
    	if (choose != 2) {
        	choose = 2;
        	try {
                    this.setSeries();
                }
                catch (ClassNotFoundException | SQLException e) {
		    label_status.setText("Offline");
                    circle_status.setFill(Color.GRAY);
                    alert.showAndWait();
                }
        };
    }
    
    @FXML
    private void windbuttonOnAction(ActionEvent event) throws ClassNotFoundException, SQLException {
    	if (choose != 3) {
        	choose = 3;
        	try {
                    this.setSeries();
                }
                catch (ClassNotFoundException | SQLException e) {
		    label_status.setText("Offline");
                    circle_status.setFill(Color.GRAY);
                    alert.showAndWait();
                }
        };
    }
    
    
    @FXML
    private void backArrowOnAction(ActionEvent event) throws ClassNotFoundException, SQLException {
    	history += 6;
    	try {
                    this.setSeries();
                }
                catch (ClassNotFoundException | SQLException e) {
		    label_status.setText("Offline");
                    circle_status.setFill(Color.GRAY);
                    alert.showAndWait();
                }
    }
    
    @FXML
    private void fowardArrowOnAction(ActionEvent event) throws ClassNotFoundException, SQLException {
        if (history != 0) {
        	history -= 6;
        	try {
                    this.setSeries();
                }
                catch (ClassNotFoundException | SQLException e) {
		    label_status.setText("Offline");
                    circle_status.setFill(Color.GRAY);
                    alert.showAndWait();
                }
        }
    }
    
    @FXML
    private void resetButtonOnAction(ActionEvent event) throws ClassNotFoundException, SQLException {
    	history = 0;
    	try {
                    this.setSeries();
                }
                catch (ClassNotFoundException | SQLException e) {
		    label_status.setText("Offline");
                    circle_status.setFill(Color.GRAY);
                    alert.showAndWait();
                }
    }
    
    @FXML
    private void autoreloadingOnAction(ActionEvent event) throws ClassNotFoundException, SQLException {
    	try {
             this.setSeries();
        }
        catch (ClassNotFoundException | SQLException e) {
	     label_status.setText("Offline");
             circle_status.setFill(Color.GRAY);
             alert.showAndWait();
        }
    }
    
    public void setSeries() throws ClassNotFoundException, SQLException {
    	String[] rs = conn.mainQuery(symbol);
        label_temperature.setText(rs[0]);
        label_humidity.setText(rs[1]);
        label_wind_speed.setText(rs[2]);
        label_wind_direction.setText(rs[3]);
        label_place.setText(rs[4]+",  "+rs[5]+"N, "+rs[6]+"E");
        label_updated_last.setText(stddata.format(new Date()));
    	line_charts.getData().clear();
    	line_charts.getData().removeAll();
    	XYChart.Series series = new XYChart.Series();
    	ArrayList<DataDoubleFormat> temp=null;
    	if (choose == 0) {
    		temp = conn.tempAverage("temp", symbol, history);
    		line_charts.setTitle("Temperatura");
    		yAxis.setLabel("°C");
    	}
    	if (choose == 1) {
    		temp = conn.tempAverage("humidity", symbol, history);
    		line_charts.setTitle("Wilgotnoœæ");
    		yAxis.setLabel("%");
    	}
    	if (choose == 2) {
    		temp = conn.tempAverage("wind_speed", symbol, history);
    		line_charts.setTitle("Prêdkoœæ wiatru");
    		yAxis.setLabel("m/s");
    	}
    	if (choose == 3) {
    		temp = conn.tempAverage("wind_direct", symbol, history);
    		line_charts.setTitle("Kierunek wiatru");
    	}
    	for(DataDoubleFormat d : temp) {
        	series.getData().add(new XYChart.Data(dateFormat.format(d.getDate()), d.getDouble()));
        }
        line_charts.setStyle("#chart1 .chart-series-line { -fx-stroke-width: 2px; -fx-stroke: #e9967a;}");
        line_charts.getData().add(series);
        
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
		try {
	        combobox_location.getItems().addAll("MMRZ01", "MMRZ02");
	        combobox_location.setValue("MMRZ01");
	        line_charts.setLegendVisible(false);
		yAxis.setAutoRanging(true);
	    	xAxis.setAutoRanging(true);
	    	this.setSeries();
		label_status.setText("Online");
		} catch (ClassNotFoundException | SQLException e) {
			label_status.setText("Offline");
			alert.showAndWait();
		}
        
    }    
    
    
}

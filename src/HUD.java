import java.awt.Rectangle;
import java.io.FileInputStream;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group; 
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javafx.scene.image.*;

public class HUD extends Application {
	
	private final double _scale = 4.0;
	
	private NetworkTableEntry _endAnglesEntry;
	private NetworkTableEntry _currAnglesEntry;
	private NetworkTableEntry _constantMeasurementsEntry;
	private NetworkTableEntry _handStateEntry;
	
	private double[] _endArmAngles = {0.0, 0.0, 0.0};
	private double[] _currArmAngles = {0.0, 0.0, 0.0};
	private double[] _armMeasurements = {0.0, 0.0, 0.0};
	
	private String _handState;
	

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Robotic Arm HUB");
		
		Group root = new Group();
		root.setId("pane");
		Scene scene = new Scene(root, 800, 600, Color.rgb(255, 255, 255));
		
		Canvas canvas = new Canvas();
		canvas.widthProperty().bind(primaryStage.widthProperty());
		canvas.heightProperty().bind(primaryStage.heightProperty());
			    
		final GraphicsContext gc = canvas.getGraphicsContext2D();
		new AnimationTimer() {
			@Override
			public void handle(long now) {
				updateInfoFromRobot();
				gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
				
				double drawCenterX = canvas.getWidth() * 0.5;
				double drawCenterY = canvas.getHeight() * 0.5;
		    	gc.translate(drawCenterX, drawCenterY);
		    	gc.save();
		    	
		    	drawRobotArm(gc);
		    	gc.restore();
		    	gc.save();
		    	drawStaticObjects(gc);
		    	gc.restore();
		    	gc.translate(-drawCenterX, -drawCenterY);
				drawInfo(gc);
			}
		}.start();
		
		root.getChildren().add(canvas);
		
		//primaryStage.getScene().getStylesheets().add(HUD.class.getResource("style.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	    
		
	}
	
    private void drawRobotArm(GraphicsContext gc) {
    	gc.setLineWidth(2);
    	gc.setLineCap(StrokeLineCap.ROUND);
    	gc.setStroke(Color.rgb(255, 00, 00));

    	gc.save();
    	gc.rotate(-90);
    	gc.beginPath();
    	gc.moveTo(0, 0);
    	gc.rotate(_endArmAngles[0]);
    	gc.lineTo(_armMeasurements[0] * _scale, 0);
    	gc.translate(_armMeasurements[0] * _scale, 0);
    	gc.rotate(_endArmAngles[1]);
    	gc.lineTo(_armMeasurements[1] * _scale, 0);
    	gc.translate(_armMeasurements[1] * _scale, 0);
    	gc.rotate(_endArmAngles[2]);
    	gc.lineTo(_armMeasurements[2] * _scale, 0);
    	gc.translate(_armMeasurements[2] * _scale, 0);
    	gc.stroke();
    	gc.restore();
    	
    	gc.setStroke(Color.rgb(33, 33, 33));
    	gc.rotate(-90);
    	gc.beginPath();
    	gc.moveTo(0, 0);
    	gc.rotate(_currArmAngles[0]);
    	gc.lineTo(_armMeasurements[0] * _scale, 0);
    	gc.translate(_armMeasurements[0] * _scale, 0);
    	gc.rotate(_currArmAngles[1]);
    	gc.lineTo(_armMeasurements[1] * _scale, 0);
    	gc.translate(_armMeasurements[1] * _scale, 0);
    	gc.rotate(_currArmAngles[2]);
    	gc.lineTo(_armMeasurements[2] * _scale, 0);
    	gc.translate(_armMeasurements[2] * _scale, 0);
    	gc.stroke();
    }
    private void drawStaticObjects(GraphicsContext gc) {
    	gc.setStroke(Color.rgb(33, 33, 33));
    	gc.beginPath();
    	gc.moveTo(0, 0);
    	gc.lineTo(0, 21 * _scale);
    	gc.stroke();
    	
    	gc.translate(0, 21 * _scale);
    	gc.fillRect(-8 * _scale, 0,  31.75 * _scale, 5 * _scale);
    }
    
   
	
	private void updateInfoFromRobot() {
		NetworkTableInstance.getDefault().startClientTeam(4711);
		NetworkTableInstance.getDefault().startDSClient();
		NetworkTable armFeed = NetworkTableInstance.getDefault().getTable("robotArmFeed");

		_endAnglesEntry = armFeed.getEntry("endAngles");
		_currAnglesEntry = armFeed.getEntry("currentAngles");
		_constantMeasurementsEntry = armFeed.getEntry("measurements");
		_handStateEntry = armFeed.getEntry("currentHandState");
		
		double[] anglesArray = {0.0, 0.0, 0.0};
		double[] measurementsArray = {0.0, 0.0, 0.0}; 
		
		if(_endAnglesEntry != null) {
			_endArmAngles = _endAnglesEntry.getDoubleArray(anglesArray);
			_currArmAngles = _currAnglesEntry.getDoubleArray(anglesArray);
			_armMeasurements = _constantMeasurementsEntry.getDoubleArray(measurementsArray);
			_handState = _handStateEntry.getString("");
		}
	}
	
	private void drawInfo(GraphicsContext gc) {
		gc.setLineWidth(2.0);
		gc.setLineCap(StrokeLineCap.ROUND);
		gc.setFill(Color.rgb(33, 33, 33));
		gc.fillText("End Arm Angles = (Shoulder: " + _endArmAngles[0] + ", Elbow: " + _endArmAngles[1] +  ", wrist: " + _endArmAngles[2] + ")", 10, 10);
		gc.fillText("Current Arm Angles = (Shoulder: " + _currArmAngles[0] + ", Elbow: " + _currArmAngles[1] +  ", wrist: " + _currArmAngles[2] + ")", 10, 25);
		gc.fillText("Arm Measurements = (Shoulder: " + _armMeasurements[0] + ", Elbow: " + _armMeasurements[1] +  ", wrist: " + _armMeasurements[2] + ")", 10, 40);
		gc.fillText("Current handstate: " + _handState, 10, 55);
		switch(_handState){
			case "LOCKED": 
				gc.fillText("RB - Switch to PICKUP state", 10, 85);
				gc.fillText("LB - Switch to PLACE state", 10, 100);
				gc.fillText("A - Forward Shoulder", 10, 115);
				gc.fillText("B - Forward Elbow ", 10, 130);
				gc.fillText("Y - Forward Wrist",10, 145);
				gc.fillText("X - Get up on step", 10, 160);
				gc.fillText("Arrow Down - Backward Shoulder", 10, 175);
				gc.fillText("Arrow Left - Backward Elbow", 10, 190);
				gc.fillText("Arrow Up - Backward Wrist", 10, 205);
				gc.fillText("Arrow Right - Arm reset/home", 10, 220);
				break;
				
			case "PICKUP":
				gc.fillText("RB - Switch to PLACE state", 10, 85);
				gc.fillText("LB - Switch to LOCKED state", 10, 100);
				gc.fillText("X - Pickup", 10, 115);
				break;
				
			case "PLACE":
				gc.fillText("RB - Switch to LOCKED state", 10, 85);
				gc.fillText("LB - Switch to PICKUP state", 10, 100);
				gc.fillText("A - Move to lowest position", 10, 115);
				gc.fillText("B - Move to middle position", 10, 130);
				gc.fillText("Y - Move to highest positon", 10, 145);
				break;
		}
	}
}

package gui;

import unused.SimpleConsoleLogger;
import de.se.NxtBrickBluetoothThread;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainWindow extends Application {
	
	public static String myBrick = "elteik";
	private MapDrawer mapDrawer;
	private NxtBrickBluetoothThread myNxt;
	
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        Canvas canvas = new Canvas(300, 250);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        mapDrawer = new MapDrawer(gc);
        
        myNxt = new NxtBrickBluetoothThread(new SimpleConsoleLogger(), myBrick);
        myNxt.addObserver(mapDrawer);
        final Thread nxtBluetoothThread = new Thread(myNxt);
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				myNxt.terminate();
			}
        });
        
        nxtBluetoothThread.start();
        
        Button start = new Button("START");
        
        
        start.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String msg = "START";
				myNxt.send(1, msg);
			}
        });
        
        mapDrawer.drawShapes();
        root.getChildren().addAll(canvas, start);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}

package gui;

import unused.SimpleConsoleLogger;
import de.se.NxtBrickBluetoothThread;
import de.se.SimpleCoordSender;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
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
        primaryStage.setTitle("NXT Tracker");
        VBox mainPane = new VBox(5);
        mainPane.setPrefSize(400, 400);
        mainPane.setPadding(new Insets(10));
        mainPane.setAlignment(Pos.CENTER);
        Canvas canvas = new Canvas(350, 350);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        mapDrawer = new MapDrawer(gc);
        
//        SimpleCoordSender simpleC = new SimpleCoordSender();
//        simpleC.addObserver(mapDrawer);
//        final Thread simpleCThread = new Thread(simpleC);
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
        
        final Button start = new Button("START");
        
        start.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String msg = "START";
				myNxt.send(1, msg);
				start.setDisable(true);
			}
        });
        
        mainPane.getChildren().addAll(canvas, start);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
        
    }
}

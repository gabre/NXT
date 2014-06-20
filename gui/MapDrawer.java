package gui;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import com.sun.prism.image.Coords;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
 
public class MapDrawer implements Observer {
	
	private static final double DISTANCE = 5.0;
	private GraphicsContext gc;
	private Integer rotation;
	private LinkedList<Coord> points;
	private Coord latest;
	

	public MapDrawer(GraphicsContext gc) {
		this.gc = gc;
		rotation = 0;
		points = new LinkedList<Coord>();
		latest = new Coord(10,10);
		gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
	}

    public void drawShapes() {
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);
        gc.strokeLine(40, 10, 10, 40);
        gc.fillOval(10, 60, 30, 30);
        gc.strokeOval(60, 60, 30, 30);
        gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        gc.strokeRoundRect(160, 60, 30, 30, 10, 10);
        gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        gc.fillPolygon(new double[]{10, 40, 10, 40},
                       new double[]{210, 210, 240, 240}, 4);
        gc.strokePolygon(new double[]{60, 90, 60, 90},
                         new double[]{210, 210, 240, 240}, 4);
        gc.strokePolyline(new double[]{110, 140, 110, 140},
                          new double[]{210, 210, 240, 240}, 4);
    }

    public void update(Observable obj, Object arg) {
        System.out.println("Received Response: " + (String)arg );
        String message = (String)arg;
        if(message.startsWith("STEER")) {
        	Integer rotation = Integer.parseInt(message.substring(5));
        	this.rotation += rotation;
        }
        if(message.startsWith("FWD")) {
        	double xInc = Math.cos(Math.toRadians(rotation)) * DISTANCE;
        	double yInc = Math.sin(Math.toRadians(rotation)) * DISTANCE;
        	Coord newCoord = new Coord(latest);
        	newCoord.x += xInc;
        	newCoord.y += yInc;
			points.add(newCoord);
			System.out.println("---");
			System.out.println(latest.x);
			System.out.println(latest.y);
			System.out.println(newCoord.x);
			System.out.println(newCoord.y);
			gc.strokeLine(latest.x, latest.y, newCoord.x, newCoord.y);
			latest = newCoord;
        }
        if(points.size() > 1000) {
        	points.clear();
        }
    }
}

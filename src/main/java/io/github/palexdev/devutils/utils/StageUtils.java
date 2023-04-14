package io.github.palexdev.devutils.utils;

import io.github.palexdev.devutils.settings.SettingsDB;
import io.github.palexdev.mfxcore.base.beans.Size;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicBoolean;

public class StageUtils {

	//================================================================================
	// Constructors
	//================================================================================
	private StageUtils() {
	}

	//================================================================================
	// Static Methods
	//================================================================================
	public static void makeDraggable(Stage stage, Node byNode) {
		Delta dragDelta = new Delta();
		AtomicBoolean allowed = new AtomicBoolean(true);
		byNode.setOnMousePressed(e -> {
			// record a delta distance for the drag and drop operation.
			if (!allowed.get()) return;
			dragDelta.x = stage.getX() - e.getScreenX();
			dragDelta.y = stage.getY() - e.getScreenY();
			byNode.setCursor(Cursor.MOVE);
		});
		byNode.setOnMouseReleased(e -> byNode.setCursor(Cursor.HAND));
		byNode.setOnMouseDragged(e -> {
			if (!allowed.get()) return;
			stage.setX(e.getScreenX() + dragDelta.x);
			stage.setY(e.getScreenY() + dragDelta.y);
		});
		byNode.setOnMouseMoved(e -> {
			Node iNode = e.getPickResult().getIntersectedNode();
			allowed.set(iNode == byNode);
			byNode.setCursor(!allowed.get() ? Cursor.DEFAULT : Cursor.HAND);
		});
	}

	public static void makeResizable(Stage stage, Region byNode) {
		StageResizer resizer = new StageResizer(byNode, stage);
		resizer.makeResizable();
	}

	public static Size getWindowSizes() {
		Size ws = Size.of(800, 600);
		String windowSizes = SettingsDB.WINDOW_SIZES.get("800,600");
		try {
			String[] tmp = windowSizes.split(",");
			ws.setWidth(Double.parseDouble(tmp[0]));
			ws.setHeight(Double.parseDouble(tmp[1]));
		} catch (Exception ignored) {
		}
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();
		ws.setWidth(Math.min(ws.getWidth(), bounds.getWidth() - 50));
		ws.setHeight(Math.min(ws.getHeight(), bounds.getHeight() - 50));
		return ws;
	}

	//================================================================================
	// Internal Classes
	//================================================================================
	private static class Delta {
		private double x, y;
	}
}

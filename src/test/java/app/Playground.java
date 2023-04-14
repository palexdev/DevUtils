package app;

import fr.brouillard.oss.cssfx.CSSFX;
import io.github.palexdev.devutils.components.FloatingField;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXFilledButton;
import io.github.palexdev.mfxcomponents.theming.enums.MFXThemeManager;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.scenicview.ScenicView;

public class Playground extends Application {

	@Override
	public void start(Stage stage) {
		VBox pane = new VBox(20);
		pane.setAlignment(Pos.CENTER);

		FloatingField ff = new FloatingField("This is the text", "This is floating");
		pane.getChildren().addAll(ff, new MFXFilledButton("String"));

		Scene scene = new Scene(pane, 600, 600);
		MFXThemeManager.PURPLE_LIGHT.addOn(scene);

		stage.setScene(scene);
		stage.show();

		CSSFX.start(scene);
		ScenicView.show(scene);
	}
}

package margit;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Launches the JavaFX user interface from its FXML view. */
public class Main extends Application {
    private final Margit margit = new Margit();

    @Override
    public void start(Stage stage) {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane mainLayout = loadMainLayout(fxmlLoader);
        MainWindow mainWindow = fxmlLoader.getController();
        mainWindow.setMargit(margit);

        stage.setTitle("Margit");
        stage.setResizable(false);
        stage.setScene(new Scene(mainLayout));
        stage.show();
    }

    /** Loads the main FXML view and reports a clear error if it cannot be read. */
    private AnchorPane loadMainLayout(FXMLLoader fxmlLoader) {
        try {
            return fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the main window.", e);
        }
    }
}

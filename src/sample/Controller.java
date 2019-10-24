package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private TextArea textArea;
    @FXML
    private Canvas canvas;
    @FXML
    private Button selectImageFile;
    @FXML
    private Button applyMatrixFilter;
    @FXML
    private Button generateImage;
    @FXML
    private Button restoreOriginalImage;
    @FXML
    private RadioButton originalImage;
    @FXML
    private RadioButton modifiedImage;
    @FXML
    private ImageView imageView;

    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        this.applyMatrixFilter.setDisable(true);
        this.restoreOriginalImage.setDisable(true);
        this.originalImage.setDisable(true);
        this.modifiedImage.setDisable(true);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}

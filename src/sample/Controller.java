package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
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

    private Stage window = Main.getPrimaryStage();

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

    @FXML
    public void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("FAKE PHOTOSHOP");
        String s = "SOME INFO ABOUT APP";
        alert.setContentText(s);
        alert.show();
    }

    @FXML
    public void exit() {
        Stage stage = (Stage) window.getScene().getWindow();
        stage.close();

    }

    @FXML
    public void openImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files",
                        "*.bmp", "*.png", "*.jpg", "*.gif"));
        File file = chooser.showOpenDialog(new Stage());
        if (file != null) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                imageView.setImage(image);
                imageView.fitHeightProperty().bind(window.heightProperty());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Please Select a File");
            alert.showAndWait();
        }
    }

    @FXML
    public void saveImage() {
        FileChooser stegoImageSaver = new FileChooser();
        stegoImageSaver.setTitle("Save Image File");
        File file = stegoImageSaver.showSaveDialog(null);
        System.out.println(file.getAbsolutePath());
        BufferedImage bImage = SwingFXUtils.fromFXImage(imageView.getImage(), null);
        try {
            ImageIO.write(bImage, ".jpg", file.getAbsoluteFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

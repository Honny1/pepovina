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
import java.nio.file.Files;
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
    private Button restoreOriginalImage;
    @FXML
    private ImageView imageView;

    private Stage window = Main.getPrimaryStage();

    @FXML
    private RadioButton originalImageRadio;

    @FXML
    private RadioButton modifiedImageRadio;

    private Image newImage = null;
    private Image originalImage = null;


    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        this.applyMatrixFilter.setDisable(true);
        this.restoreOriginalImage.setDisable(true);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    @FXML
    void use_modified() {
        imageView.setImage(newImage);
        originalImageRadio.setSelected(false);
        modifiedImageRadio.setSelected(true);
    }

    @FXML
    void use_original() {
        imageView.setImage(originalImage);
        originalImageRadio.setSelected(true);
        modifiedImageRadio.setSelected(false);
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
                imageView.fitHeightProperty().bind(window.heightProperty());
                originalImage = image;
                use_original();
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
        BufferedImage bImage = SwingFXUtils.fromFXImage(this.imageView.getImage(), null);
        try {
            ImageIO.write(bImage, "png", file.getAbsoluteFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage creatImage() {
        BufferedImage bImage = new BufferedImage((int) imageView.getFitWidth(), (int) imageView.getFitHeight(), BufferedImage.TYPE_3BYTE_BGR);
        for (int x = 0; x < bImage.getWidth(); x++) {
            for (int y = 0; y < bImage.getHeight(); y++) {
                int r = Math.round((x + y) % 200);
                int g = Math.round(y % 120);
                int b = Math.round((x + y) % 150);
                bImage.setRGB(x, y, new java.awt.Color(r, g, b).getRGB());
            }
        }
        return bImage;
    }

    @FXML
    void genImage() {
        BufferedImage img = creatImage();
        originalImage = SwingFXUtils.toFXImage(img, null);
        use_original();
    }

    @FXML
    public void negateImage() {
        BufferedImage used_image = SwingFXUtils.fromFXImage(originalImage, null);
        if (newImage != null) {
            used_image = SwingFXUtils.fromFXImage(newImage, null);
        }
        BufferedImage new_Image = new BufferedImage(
                used_image.getWidth(),
                used_image.getHeight(),
                used_image.getType()
        );
        for (int x = 0; x < used_image.getWidth(); x++) {
            for (int y = 0; y < used_image.getHeight(); y++) {
                int originalPixel = used_image.getRGB(x, y);
                java.awt.Color originalColor = new java.awt.Color(originalPixel);
                new_Image.setRGB(x, y, new java.awt.Color(
                        Math.abs(255 - originalColor.getRed()),
                        Math.abs(255 - originalColor.getGreen()),
                        Math.abs(255 - originalColor.getBlue()))
                        .getRGB());
            }
        }
        newImage = SwingFXUtils.toFXImage(new_Image, null);
        use_modified();
    }
}

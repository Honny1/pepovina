package sample;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.event.MouseEvent;
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

    public void autoThreshold() {
        BufferedImage used_image = SwingFXUtils.fromFXImage(originalImage, null);
        if (newImage != null) {
            used_image = SwingFXUtils.fromFXImage(newImage, null);
        }
        BufferedImage new_Image = new BufferedImage(
                used_image.getWidth(),
                used_image.getHeight(),
                used_image.getType()
        );
        //compute treshold
        long currentTreshold = 0;
        for (int x = 0; x < used_image.getWidth(); x++) {
            for (int y = 0; y < used_image.getHeight(); y++) {
                java.awt.Color c = new java.awt.Color(used_image.getRGB(x, y));
                currentTreshold = currentTreshold + c.getRed() + c.getGreen() + c.getBlue();
            }
        }
        currentTreshold = (currentTreshold / (used_image.getHeight() * used_image.getWidth() * 3));

        int black = java.awt.Color.BLACK.getRGB();
        int white = java.awt.Color.WHITE.getRGB();
        for (int x = 0; x < used_image.getWidth(); x++) {
            for (int y = 0; y < used_image.getHeight(); y++) {
                int rgb = used_image.getRGB(x, y);
                java.awt.Color c = new java.awt.Color(rgb);
                if (c.getRed() > currentTreshold ||
                        c.getGreen() > currentTreshold ||
                        c.getBlue() > currentTreshold) {
                    new_Image.setRGB(x, y, white);
                } else {
                    new_Image.setRGB(x, y, black);
                }
            }
        }
        newImage = SwingFXUtils.toFXImage(new_Image, null);
        use_modified();
    }

    public Image threshold(int currentTreshold) {
        BufferedImage used_image = SwingFXUtils.fromFXImage(originalImage, null);
        if (newImage != null) {
            used_image = SwingFXUtils.fromFXImage(newImage, null);
        }
        BufferedImage new_Image = new BufferedImage(
                used_image.getWidth(),
                used_image.getHeight(),
                used_image.getType()
        );

        int black = java.awt.Color.BLACK.getRGB();
        int white = java.awt.Color.WHITE.getRGB();
        for (int x = 0; x < used_image.getWidth(); x++) {
            for (int y = 0; y < used_image.getHeight(); y++) {
                int rgb = used_image.getRGB(x, y);
                java.awt.Color c = new java.awt.Color(rgb);
                if (c.getRed() > currentTreshold ||
                        c.getGreen() > currentTreshold ||
                        c.getBlue() > currentTreshold) {
                    new_Image.setRGB(x, y, white);
                } else {
                    new_Image.setRGB(x, y, black);
                }
            }
        }
        return SwingFXUtils.toFXImage(new_Image, null);
    }

    public void thresholdDialog(ActionEvent event) {
        final Stage dialog = new Stage();
        dialog.setTitle("Threshold");
        dialog.initModality(Modality.NONE);

        Button apply = new Button("Apply");
        Button cancle = new Button("Cancle");
        BufferedImage used_image = SwingFXUtils.fromFXImage(originalImage, null);
        ImageView dialogImageView = new ImageView(
                SwingFXUtils.toFXImage(
                        scale(used_image,
                                (int) originalImage.getWidth() / 2,
                                (int) originalImage.getHeight() / 2),
                        null));


        Slider slider = new Slider(0, 255, 255);
        slider.setOnMouseDragged(e -> {
            BufferedImage dialogImage = SwingFXUtils.fromFXImage(
                    threshold((int) slider.getValue()), null);
            dialogImage = scale(dialogImage,
                    (int) originalImage.getWidth() / 2,
                    (int) originalImage.getHeight() / 2);
            dialogImageView.setImage(
                    SwingFXUtils.toFXImage(dialogImage, null));
        });

        BufferedImage dialogImage = SwingFXUtils.fromFXImage(
                threshold((int) slider.getValue()), null);
        dialogImage = scale(dialogImage,
                (int) originalImage.getWidth() / 2,
                (int) originalImage.getHeight() / 2);
        dialogImageView.setImage(
                SwingFXUtils.toFXImage(dialogImage, null));

        apply.setOnAction(e -> {
            newImage = threshold((int) slider.getValue());
            use_modified();
            dialog.close();
        });

        cancle.setOnAction(e -> {
            dialog.close();
        });

        GridPane gridPaneRow = new GridPane();
        gridPaneRow.setHgap(10);
        gridPaneRow.setVgap(10);
        gridPaneRow.add(apply, 0, 0);
        gridPaneRow.add(cancle, 1, 0);
        gridPaneRow.setAlignment(Pos.CENTER);

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(dialogImageView, 0, 0);
        gridPane.add(slider, 0, 1);
        gridPane.add(gridPaneRow, 0, 2);
        gridPane.setAlignment(Pos.CENTER);

        Scene dialogScene = new Scene(gridPane,
                originalImage.getWidth() / 2 + 100,
                originalImage.getHeight() / 2 + 100);
        dialog.setScene(dialogScene);
        dialog.show();

    }

    public static BufferedImage scale(BufferedImage src, int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int x, y;
        int ww = src.getWidth();
        int hh = src.getHeight();
        int[] ys = new int[h];
        for (y = 0; y < h; y++)
            ys[y] = y * hh / h;
        for (x = 0; x < w; x++) {
            int newX = x * ww / w;
            for (y = 0; y < h; y++) {
                int col = src.getRGB(newX, ys[y]);
                img.setRGB(x, y, col);
            }
        }
        return img;
    }

}

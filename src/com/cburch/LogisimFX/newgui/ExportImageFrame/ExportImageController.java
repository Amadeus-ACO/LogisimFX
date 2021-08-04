package com.cburch.LogisimFX.newgui.ExportImageFrame;

import com.cburch.LogisimFX.FileSelector;
import com.cburch.LogisimFX.circuit.Circuit;
import com.cburch.LogisimFX.data.Bounds;
import com.cburch.LogisimFX.file.LogisimFile;
import com.cburch.LogisimFX.newgui.AbstractController;
import com.cburch.LogisimFX.newgui.DialogManager;
import com.cburch.LogisimFX.newgui.PrintFrame.PrintCanvas;
import com.cburch.LogisimFX.proj.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;

public class ExportImageController extends AbstractController {

    private Stage stage;

    @FXML
    private Label CircuitsLbl;

    @FXML
    private ListView<Circuit> CircuitsLstVw;


    @FXML
    private Label FormatLbl;

    @FXML
    private RadioButton PngRb;

    @FXML
    private RadioButton GifRb;

    @FXML
    private RadioButton JpegRb;


    @FXML
    private CheckBox PrintViewChkBx;

    @FXML
    private Label PrintViewLbl;


    @FXML
    private Button OkBtn;

    @FXML
    private Button CancelBtn;

    private ObservableList<Circuit> circuits;
    private MultipleSelectionModel<Circuit> circSelectionModel;
    private Project proj;

    private FileSelector fileSelector;

    private PrintCanvas canvas;

    private String extension;

    @FXML
    public void initialize(){

        CircuitsLbl.textProperty().bind(LC.createStringBinding("labelCircuits"));

        FormatLbl.textProperty().bind(LC.createStringBinding("labelImageFormat"));

        PngRb.setOnAction(event -> {
            GifRb.setSelected(false);
            JpegRb.setSelected(false);
            fileSelector.setPngFilter();
            extension = ".png";
        });

        GifRb.setOnAction(event -> {
            PngRb.setSelected(false);
            JpegRb.setSelected(false);
            fileSelector.setGifFilter();
            extension = ".gif";
        });

        JpegRb.setOnAction(event -> {
            PngRb.setSelected(false);
            GifRb.setSelected(false);
            fileSelector.setJpgFilter();
            extension = ".jpg";
        });


        PrintViewLbl.textProperty().bind(LC.createStringBinding("labelPrinterView"));

        OkBtn.setText("Ok");
        OkBtn.setOnAction(event -> exportImage());

        CancelBtn.setText("Cancel");
        CancelBtn.setOnAction(event -> stage.close());

    }

    @Override
    public void postInitialization(Stage s, Project project) {

        stage = s;
        stage.titleProperty().bind(LC.createStringBinding("exportImageSelect"));
        stage.setHeight(325);
        stage.setWidth(375);

        stage.setResizable(false);

        fileSelector = new FileSelector(stage);

        proj = project;

        circuits = FXCollections.observableArrayList();

        boolean includeEmpty = true;

        circSelectionModel = CircuitsLstVw.getSelectionModel();
        circSelectionModel.setSelectionMode(SelectionMode.MULTIPLE);

        LogisimFile file = proj.getLogisimFile();
        Circuit current = proj.getCurrentCircuit();

        boolean currentFound = false;

        for (Circuit circ : file.getCircuits()) {
            if (!includeEmpty || circ.getBounds() != Bounds.EMPTY_BOUNDS) {
                if (circ == current) currentFound = true;
                circuits.add(circ);
            }
        }

        CircuitsLstVw.setItems(circuits);

        if (currentFound) CircuitsLstVw.getSelectionModel().select(current);

        canvas = new PrintCanvas(Screen.getPrimary().getBounds().getWidth(),
                Screen.getPrimary().getBounds().getHeight(), proj);

    }

    private void exportImage(){

        File dest;

        if (circSelectionModel.getSelectedItems().size() > 1) {
            dest = fileSelector.chooseDirectory(LC.get("exportImageDirectorySelect"));
        } else {
            dest = fileSelector.showSaveDialog(LC.get("exportImageFileSelect"));
        }

        for (Circuit circ : circSelectionModel.getSelectedItems()) {

            ImageView img = canvas.getImage(circ, PrintViewChkBx.isSelected());

            File where;
            if (dest.isDirectory()) {
                where = new File(dest, circ.getName()+extension);
           // } else if (filter.accept(dest)) {
                //where = dest;
            } else {
                String newName = dest.getName();
                where = new File(dest.getParentFile(), newName);
            }

            try {

                BufferedImage bImage = SwingFXUtils.fromFXImage(img.getImage(), null);

                if(PngRb.isSelected()){
                    ImageIO.write(bImage, "PNG", where);
                    //GifEncoder.toFile(img, where);
                }else if(GifRb.isSelected()){
                    ImageIO.write(bImage, "GIF", where);
                }else if(JpegRb.isSelected()){
                    ImageIO.write(bImage, "JPEG", where);
                }

            } catch (Exception e) {
                DialogManager.CreateErrorDialog(LC.get("couldNotCreateFile"), LC.get("couldNotCreateFile"));
                stage.close();
                return;
            }
        }

        stage.close();

    }

    @Override
    public void onClose() {
        System.out.println("Export image closed");
    }
}

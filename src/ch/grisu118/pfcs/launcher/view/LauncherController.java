package ch.grisu118.pfcs.launcher.view;

import ch.grisu118.pfcs.a1.AirTrain;
import ch.grisu118.pfcs.a2.ParkingCar;
import ch.grisu118.pfcs.a3.StormMain;
import ch.grisu118.pfcs.a4.BoomerangMain;
import ch.grisu118.pfcs.launcher.Main;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;

/**
 * Created by benjamin on 18.11.2015.
 */
public class LauncherController {

    private static final String EX1 = "Übung 1 - Luftkissenbahn";
    private static final String EX2 = "Übung 2 - Fahrendes Auto";
    private static final String EX3 = "Übung 3 - Fliegende Würfel";
    private static final String EX4 = "Übung 4 - Bumerang";

    private static final String[] exercises = {EX1, EX2, EX3, EX4};

    private static final String EX1_DESC = "Eindimensionale Bewegung\n" +
            "Stoss mit Reibungsfrei gleitenden Wagen.";
    private static final String EX2_DESC = "Parken mit diversen Fahrzeugen,\nsiehe Beschreibung im UI.";
    private static final String EX3_DESC = "Drücken Sie F11 für Vollbildmodus!\n";
    private static final String EX4_DESC = "";

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private TextArea infoArea;

    private Main main;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public LauncherController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        comboBox.getItems().addAll(exercises);
    }

    @FXML
    private void onComboBoxAction() {
        String v = comboBox.getValue();
        switch (v) {
            case EX1:
                infoArea.setText(EX1_DESC);
                break;
            case EX2:
                infoArea.setText(EX2_DESC);
                break;
            case EX3:
                infoArea.setText(EX3_DESC);
                break;
            case EX4:
                infoArea.setText(EX4_DESC);
                break;
            default:
                infoArea.setText("");
                break;
        }
    }

    @FXML
    private void doClose() {
        main.getPrimaryStage().close();
        System.exit(0);
    }

    @FXML
    private void doStart() {
        String v = comboBox.getValue();
        switch (v) {
            case EX1:
                SwingUtilities.invokeLater(AirTrain::new);
            break;
            case EX2:
                SwingUtilities.invokeLater(ParkingCar::new);
                break;
            case EX3:
                SwingUtilities.invokeLater(StormMain::new);
                break;
            case EX4:
                SwingUtilities.invokeLater(BoomerangMain::new);
                break;
            default:
                break;
        }
    }

    public void setMain(Main m) {
        this.main = m;
    }
}

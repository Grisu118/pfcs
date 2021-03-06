package ch.grisu118.pfcs.launcher.view;

import ch.grisu118.pfcs.a1.AirTrain;
import ch.grisu118.pfcs.a2.ParkingCar;
import ch.grisu118.pfcs.a3.StormMain;
import ch.grisu118.pfcs.a4.BoomerangMain;
import ch.grisu118.pfcs.a5.FlowMain;
import ch.grisu118.pfcs.a6.Storm2Main;
import ch.grisu118.pfcs.launcher.Main;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import javax.swing.*;

/**
 * Created by benjamin on 18.11.2015.
 */
public class LauncherController {

    private static final String EX1 = "Übung 1 - Luftkissenbahn";
    private static final String EX2 = "Übung 2 - Fahrendes Auto";
    private static final String EX3 = "Übung 3 - Fliegende Würfel";
    private static final String EX4 = "Übung 4 - Bumerang";
    private static final String EX5 = "Übung 5 - Strömung um Zylinder";
    private static final String EX6 = "Übung 6 - Fliegende Quader";
    private static final String EX6b = "Übung 6 - Fliegende Quader - ohne PreCalc";


    private static final String[] exercises = {EX1, EX2, EX3, EX4, EX5, EX6, EX6b};

    private static final String EX1_DESC = "Eindimensionale Bewegung\n" +
            "Stoss mit Reibungsfrei gleitenden Wagen.";
    private static final String EX2_DESC = "Parken mit diversen Fahrzeugen,\nsiehe Beschreibung im UI.";
    private static final String EX3_DESC = "Drücken Sie F11 für Vollbildmodus!\n";
    private static final String EX4_DESC = "Fliegende Bumerangs";
    private static final String EX5_DESC = "Strömung Simuliert mit Runge Kutta.\nZufälliges erzeugen von Punkten mit Taste 4 ein und ausschalten!";
    private static final String EX6_DESC = "Drücken Sie F11 für Vollbildmodus!\n";
    private static final String EX6b_DESC = "Drücken Sie F11 für Vollbildmodus!\nOhne Vorberechnen => Objekte kommen aus dem Nebel";

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
            case EX5:
                infoArea.setText(EX5_DESC);
                break;
            case EX6:
                infoArea.setText(EX6_DESC);
                break;
            case EX6b:
                infoArea.setText(EX6b_DESC);
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
            case EX5:
                SwingUtilities.invokeLater(FlowMain::new);
                break;
            case EX6:
                SwingUtilities.invokeLater(Storm2Main::new);
                break;
            case EX6b:
                SwingUtilities.invokeLater(() -> new Storm2Main(false));
                break;
            default:
                break;
        }
    }

    public void setMain(Main m) {
        this.main = m;
    }
}

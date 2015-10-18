package ch.grisu118.pfcs;

import ch.grisu118.pfcs.a1.AirTrain;
import ch.grisu118.pfcs.a2.ParkingCar;

import javax.swing.*;

/**
 * Created by benjamin on 18.10.2015.
 */
public class JarMain {

    private static final String EX1 = "Übung 1 - Luftkissenbahn";
    private static final String EX2 = "Übung 2 - Fahrendes Auto";

    public static void main(String[] args) {
        Object result = JOptionPane.showInputDialog(null, "Welche Übung starten?", "PFCS - Übungen", JOptionPane.QUESTION_MESSAGE, new ImageIcon("res/Avaatar_o.png"), new String[]{EX1, EX2}, EX1);
        if (result == null)
            System.exit(0);
        switch (result.toString()) {
            case EX1:
                new AirTrain();
                break;
            case EX2:
                new ParkingCar();
                break;
            default:
                break;
        }


    }
}

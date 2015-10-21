package ch.grisu118.pfcs;

import ch.grisu118.pfcs.a1.AirTrain;
import ch.grisu118.pfcs.a2.ParkingCar;

import javax.swing.*;

/**
 * Main Class for jar.
 * @author Benjamin Leber
 */
public class JarMain {

    private static final String EX1 = "Übung 1 - Luftkissenbahn";
    private static final String EX2 = "Übung 2 - Fahrendes Auto";

    public static void main(String[] args) {
        new JarMain();


    }

    public JarMain() {
        Icon icon =  new ImageIcon("res/icon.png");
        if (icon.getIconHeight() < 0) {
            icon = new ImageIcon(getClass().getClassLoader().getResource("icon.png"));
        }
        Object result = JOptionPane.showInputDialog(null, "Welche Übung starten?", "PFCS - Übungen", JOptionPane.QUESTION_MESSAGE, icon, new String[]{EX1, EX2}, EX1);
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

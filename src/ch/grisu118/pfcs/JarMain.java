package ch.grisu118.pfcs;

import ch.grisu118.pfcs.a1.AirTrain;
import ch.grisu118.pfcs.a2.ParkingCar;
import ch.grisu118.pfcs.a3.StormMain;
import ch.grisu118.pfcs.a4.BoomerangMain;

import javax.swing.*;

/**
 * Main Class for jar.
 *
 * @author Benjamin Leber
 */
public class JarMain {

    private static final String EX1 = "Übung 1 - Luftkissenbahn";
    private static final String EX2 = "Übung 2 - Fahrendes Auto";
    private static final String EX3 = "Übung 3 - Fliegende Würfel";
    private static final String EX4 = "Übung 4 - Bumerang";

    public static void main(String[] args) {
        new JarMain();


    }

    public JarMain() {
        Icon icon = new ImageIcon("res/icon.png");
        if (icon.getIconHeight() < 0) {
            icon = new ImageIcon(getClass().getClassLoader().getResource("icon.png"));
        }
        Object result = JOptionPane.showInputDialog(null, "Welche Übung starten?", "PFCS - Übungen", JOptionPane.QUESTION_MESSAGE, icon, new String[]{EX1, EX2, EX3, EX4}, EX1);
        if (result == null)
            System.exit(0);
        switch (result.toString()) {
            case EX1:
                new AirTrain();
                break;
            case EX2:
                new ParkingCar();
                break;
            case EX3:
                new StormMain();
                break;
            case EX4:
                new BoomerangMain();
                break;
            default:
                break;
        }
    }
}

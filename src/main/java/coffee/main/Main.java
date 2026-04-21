package coffee.main;

import coffee.view.UngDungQuanLyCafe;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UngDungQuanLyCafe().setVisible(true));
    }
}

import javax.swing.*;
import java.awt.*;

public class ColorFrame extends JFrame {



    public ColorFrame(SettingsFrame settingsFrame, Color backgroundColor, int colorChangeSpeed) {
        setTitle("Color Frame");
        setSize(200, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(backgroundColor);
        add(panel);

        setVisible(true);
    }

    public static void setBackgroundColor(Color backgroundColor) {
    }


    public void setVisible(boolean b) {
    }
}

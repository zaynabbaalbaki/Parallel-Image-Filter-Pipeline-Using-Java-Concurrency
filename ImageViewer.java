import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ImageViewer {
    public static void showImage(BufferedImage image, String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JLabel(new ImageIcon(image)));
        frame.pack();
        frame.setLocationRelativeTo(null); // center
        frame.setVisible(true);
    }
}

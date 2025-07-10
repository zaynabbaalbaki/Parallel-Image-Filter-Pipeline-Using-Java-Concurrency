import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;



public class ImageUtils {
    public static BufferedImage loadImage(String path) throws Exception {
        return ImageIO.read(new File(path));
    }

    public static void saveImage(BufferedImage img, String path) throws Exception {
        ImageIO.write(img, "jpg", new File(path));
    }

    public static BufferedImage deepCopy(BufferedImage img) {
        return new BufferedImage(img.getColorModel(),
                img.copyData(null),
                img.isAlphaPremultiplied(), null);
    }
}

import java.awt.Color;
import java.awt.image.BufferedImage;


public class SequentialFilter {
    public static BufferedImage applyFilter(BufferedImage input, double[][] kernel) {
        int width = input.getWidth();
        int height = input.getHeight();
        int kSize = kernel.length;
        int kHalf = kSize / 2;

        BufferedImage output = ImageUtils.deepCopy(input);

        for (int y = kHalf; y < height - kHalf; y++) {
            for (int x = kHalf; x < width - kHalf; x++) {
                double r = 0, g = 0, b = 0;
                for (int i = -kHalf; i <= kHalf; i++) {
                    for (int j = -kHalf; j <= kHalf; j++) {
                        Color c = new Color(input.getRGB(x + j, y + i));
                        double weight = kernel[i + kHalf][j + kHalf];
                        r += c.getRed() * weight;
                        g += c.getGreen() * weight;
                        b += c.getBlue() * weight;
                    }
                }
                Color newColor = new Color(
                        clamp((int) r), clamp((int) g), clamp((int) b));
                output.setRGB(x, y, newColor.getRGB());
            }
        }
        return output;
    }

    private static int clamp(int val) {
        return Math.max(0, Math.min(255, val));
    }
}


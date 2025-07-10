import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


public class ParallelFilter {
    private static final int THRESHOLD = 100; // rows per task

    public static BufferedImage applyFilter(BufferedImage input, double[][] kernel) {
        BufferedImage output = ImageUtils.deepCopy(input);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new FilterTask(input, output, 0, input.getHeight(), kernel));
        return output;
    }

    static class FilterTask extends RecursiveAction {
        BufferedImage input, output;
        int startY, endY;
        double[][] kernel;

        FilterTask(BufferedImage in, BufferedImage out, int startY, int endY, double[][] k) {
            this.input = in; this.output = out;
            this.startY = startY; this.endY = endY; this.kernel = k;
        }

        @Override
        protected void compute() {
            if (endY - startY <= THRESHOLD) {
                apply();
                return;
            }
            int mid = (startY + endY) / 2;
            invokeAll(
                new FilterTask(input, output, startY, mid, kernel),
                new FilterTask(input, output, mid, endY, kernel)
            );
        }

        void apply() {
            int width = input.getWidth();
            int height = input.getHeight();
            int kSize = kernel.length;
            int kHalf = kSize / 2;

            for (int y = Math.max(startY, kHalf); y < Math.min(endY, height - kHalf); y++) {
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
        }

        private int clamp(int val) {
            return Math.max(0, Math.min(255, val));
        }
    }
}



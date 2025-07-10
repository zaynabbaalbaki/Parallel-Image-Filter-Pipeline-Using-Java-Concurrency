import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.ForkJoinPool;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageFilterApp extends JFrame {
    private BufferedImage originalImage;
    private BufferedImage seqImage;
    private BufferedImage parImage;

    private JLabel originalLabel;
    private JLabel speedupLabel;
    private JLabel seqLabel;
    private JLabel parLabel;

    private JComboBox<String> filterBox;
    private JLabel seqTimeLabel;
    private JLabel parTimeLabel;

    public ImageFilterApp() {
        setTitle("Java Image Filter GUI with Parallel Processing");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        JButton loadButton = new JButton("Load Image");
        JButton applyButton = new JButton("Apply Filter");

        String[] filters = {"Blur", "Gaussian Blur", "Sharpen", "Edge Detection", "Emboss"};
        filterBox = new JComboBox<>(filters);

        seqTimeLabel = new JLabel("Sequential Time: N/A");
        parTimeLabel = new JLabel("Parallel Time: N/A");

        topPanel.add(loadButton);
        topPanel.add(filterBox);
        topPanel.add(applyButton);
        topPanel.add(seqTimeLabel);
        topPanel.add(parTimeLabel);

        add(topPanel, BorderLayout.NORTH);

        originalLabel = new JLabel("Original");
        speedupLabel = new JLabel("Speedup: N/A");
        seqLabel = new JLabel("Sequential");
        parLabel = new JLabel("Parallel");

        originalLabel.setHorizontalTextPosition(JLabel.CENTER);
        originalLabel.setVerticalTextPosition(JLabel.BOTTOM);
        seqLabel.setHorizontalTextPosition(JLabel.CENTER);
        seqLabel.setVerticalTextPosition(JLabel.BOTTOM);
        parLabel.setHorizontalTextPosition(JLabel.CENTER);
        parLabel.setVerticalTextPosition(JLabel.BOTTOM);

        JPanel imagePanel = new JPanel(new GridLayout(1, 3));
        imagePanel.add(new JScrollPane(originalLabel));
        topPanel.add(speedupLabel);
        imagePanel.add(new JScrollPane(seqLabel));
        imagePanel.add(new JScrollPane(parLabel));
        add(imagePanel, BorderLayout.CENTER);

        loadButton.addActionListener(e -> loadImage());
        applyButton.addActionListener(e -> applyFilters());

        setSize(1300, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                originalImage = ImageUtils.loadImage(file.getAbsolutePath());
                originalLabel.setIcon(new ImageIcon(originalImage));
                seqLabel.setIcon(null);
                parLabel.setIcon(null);
                seqTimeLabel.setText("Sequential Time: N/A");
                parTimeLabel.setText("Parallel Time: N/A");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to load image.");
            }
        }
    }

    private void applyFilters() {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(this, "Please load an image first.");
            return;
        }

        String selectedFilter = (String) filterBox.getSelectedItem();
        double[][] kernel = getKernel(selectedFilter);
        if (kernel == null) return;

        normalizeKernel(kernel);

        long startSeq = System.nanoTime();
        seqImage = SequentialFilter.applyFilter(originalImage, kernel);
        long endSeq = System.nanoTime();
        double seqTimeMs = (endSeq - startSeq) / 1e6;

        long startPar = System.nanoTime();
        parImage = ParallelFilter.applyFilter(originalImage, kernel);
        long endPar = System.nanoTime();
        double parTimeMs = (endPar - startPar) / 1e6;

        seqLabel.setIcon(new ImageIcon(seqImage));
        parLabel.setIcon(new ImageIcon(parImage));
        seqTimeLabel.setText(String.format("Sequential Time: %.2f ms", seqTimeMs));
        parTimeLabel.setText(String.format("Parallel Time: %.2f ms", parTimeMs));

        double speedup = seqTimeMs / parTimeMs;
        speedupLabel.setText(String.format("Speedup: %.2fx", speedup));

        try (PrintWriter writer = new PrintWriter("benchmark.csv")) {
            writer.println("threads,time_ms,speedup,efficiency");
            for (int threads = 1; threads <= 100; threads *= 2) {
                ForkJoinPool pool = new ForkJoinPool(threads);
                BufferedImage copy = ImageUtils.deepCopy(originalImage);

                long benchStart = System.nanoTime();
                pool.submit(() -> ParallelFilter.applyFilter(copy, kernel)).get();
                long benchEnd = System.nanoTime();

                long timeMs = (benchEnd - benchStart) / 1_000_000;
                double spd = seqTimeMs / timeMs;
                double eff = spd / threads;
                writer.printf("%d,%d,%.2f,%.2f%n", threads, timeMs, spd, eff);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private double[][] getKernel(String filter) {
        return switch (filter) {
            case "Blur" -> new double[][]{
                {1, 1, 1},
                {1, 1, 1},
                {1, 1, 1}
            };
            case "Gaussian Blur" -> new double[][]{
                {1, 2, 1},
                {2, 4, 2},
                {1, 2, 1}
            };
            case "Sharpen" -> new double[][]{
                {0, -1, 0},
                {-1, 5, -1},
                {0, -1, 0}
            };
            case "Edge Detection" -> new double[][]{
                {-1, 0, 1},
                {-2, 0, 2},
                {-1, 0, 1}
            };
            case "Emboss" -> new double[][]{
                {-2, -1, 0},
                {-1, 1, 1},
                {0, 1, 2}
            };
            default -> null;
        };
    }
    private void normalizeKernel(double[][] kernel) {
        double sum = 0;
        for (double[] row : kernel)
            for (double val : row) sum += val;
        if (sum == 0) return;
        int i = 0;
        for (double[] row : kernel) {
            int j = 0;
            for (double val : row) {
                row[j] = val / sum;
                j++;
            }
            kernel[i++] = row;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ImageFilterApp::new);
    }
}

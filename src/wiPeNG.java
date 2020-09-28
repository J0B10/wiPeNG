import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class wiPeNG {

    public static void main(String[] args) {
        try {
            final int threads = parseParam("threads", 't', args).map(Integer::valueOf).orElse(2);
            final File directory = parseParam("dir", 'd', args).map(File::new).orElse(new File("."));
            final File[] pngs = listAllPNGs(directory);
            final ExecutorService executor = Executors.newFixedThreadPool(threads);
            for (File png : pngs) {
                executor.submit(new PNGCheck(png));
            }
            executor.shutdown();
            while (!executor.isTerminated()) {
                Thread.sleep(100);
            }
            System.out.println("DONE!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File[] listAllPNGs(File directory) throws IOException {
        final File[] files = directory.listFiles(
                (dir, name) -> name.toLowerCase().endsWith(".png")
        );
        if (files == null) {
            throw new IOException("Not a directory");
        } else if (files.length == 0) {
            throw new IOException("Directory does not contain png files");
        }
        return files;
    }

    public static Optional<String> parseParam(String key, char alias, String[] args) {
        String arg;
        String value;
        for (int i = 0; i < args.length - 1; i++) {
            arg = args[i];
            value = args[i + 1];
            if (arg.equals("--" + key) || arg.equals("-" + alias)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    public static class PNGCheck implements Runnable {

        private final File file;

        public PNGCheck(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            try {
                if (isEmpty()) {
                    System.out.println(file.getName() + " is empty. Removed it.");
                    deletePNG();
                }
            } catch (InterruptedException e) {
                System.out.println("x");
            } catch (IOException e) {
                System.out.println("Error reading " + file.getName() + ":");
                e.printStackTrace();
            }
        }

        private boolean isEmpty() throws IOException, InterruptedException {
            final Image img = ImageIO.read(file);
            final int width = img.getWidth(null);
            final int height = img.getHeight(null);
            final int[] pixels = new int[width * height];
            new PixelGrabber(img, 0, 0, width, height, pixels, 0, width).grabPixels();
            final int step_size = (int) Math.sqrt(width);

            Color color;
            for (int i = 0; i < step_size; i++) {
                for (int j = 0; j < pixels.length; j += step_size) {
                    color = new Color(pixels[j + i], true);
                    if (color.getAlpha() != 0) return false;
                }
            }
            return true;
        }

        private void deletePNG() throws IOException {
            Files.delete(file.toPath());
        }

    }
}

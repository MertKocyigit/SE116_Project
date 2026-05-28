import city.City;
import city.Simulation;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;

public class ObjectVilleGame {
    public static void main(String[] args) {
        if (args.length < 2 || args.length > 3) {
            System.err.println("Usage: java -jar ObjectVilleGame.jar <map-file> <ticks> [output-file]");
            System.exit(1);
        }

        try {
            Path mapPath = Path.of(args[0]);
            int ticks = parseTicks(args[1]);
            City city = City.fromFile(mapPath);

            if (args.length == 3) {
                try (PrintStream out = new PrintStream(new FileOutputStream(args[2]))) {
                    new Simulation(city, out).run(ticks);
                }
            } else {
                new Simulation(city, System.out).run(ticks);
            }
        } catch (NumberFormatException ex) {
            System.err.println("Configuration error: Tick count must be an integer.");
            System.exit(2);
        } catch (IllegalArgumentException | IOException ex) {
            System.err.println("Configuration error: " + ex.getMessage());
            System.exit(2);
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
            System.exit(3);
        }
    }

    private static int parseTicks(String text) {
        int ticks = Integer.parseInt(text);
        if (ticks < 0) {
            throw new IllegalArgumentException("Tick count cannot be negative.");
        }
        return ticks;
    }
}

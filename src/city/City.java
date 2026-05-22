package city;

import cell.Cell;
import cell.EmptyCell;
import cell.Road;
import cell.service.Hospital;
import cell.service.PoliceStation;
import cell.service.School;
import cell.service.ServiceProvider;
import cell.utility.InternetHub;
import cell.utility.PowerPlant;
import cell.utility.UtilityProvider;
import cell.utility.WaterStation;
import cell.zone.Commercial;
import cell.zone.Housing;
import cell.zone.Industrial;
import cell.zone.Zone;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class City {
    private final Cell[][] grid;
    private final List<Zone> zones;
    private final List<UtilityProvider> utilityProviders;
    private final List<ServiceProvider> serviceProviders;

    private City(Cell[][] grid, List<Zone> zones, List<UtilityProvider> utilityProviders, List<ServiceProvider> serviceProviders) {
        this.grid = grid;
        this.zones = zones;
        this.utilityProviders = utilityProviders;
        this.serviceProviders = serviceProviders;
    }

    public static City fromFile(Path path) throws IOException {
        List<String> rawLines = Files.readAllLines(path);
        List<String> lines = new ArrayList<>();

        for (String rawLine : rawLines) {
            String line = rawLine.strip();
            if (!line.isEmpty() && !line.startsWith("#")) {
                lines.add(line);
            }
        }

        if (lines.isEmpty()) {
            throw new IOException("Map file is empty.");
        }

        int startIndex = 0;
        String[] firstTokens = lines.get(0).split("\\s+");
        if (firstTokens.length == 2 && isInteger(firstTokens[0]) && isInteger(firstTokens[1])) {
            startIndex = 1;
        }

        if (startIndex >= lines.size()) {
            throw new IOException("Map file does not contain grid rows.");
        }

        List<char[]> rows = new ArrayList<>();
        int expectedColumns = -1;

        for (int i = startIndex; i < lines.size(); i++) {
            char[] row = parseRow(lines.get(i));
            if (row.length == 0) {
                continue;
            }
            if (expectedColumns == -1) {
                expectedColumns = row.length;
            } else if (row.length != expectedColumns) {
                throw new IOException("All map rows must have the same length.");
            }
            rows.add(row);
        }

        if (rows.isEmpty()) {
            throw new IOException("Map file does not contain usable cells.");
        }

        int rowCount = rows.size();
        int colCount = expectedColumns;
        Cell[][] grid = new Cell[rowCount][colCount];
        List<Zone> zones = new ArrayList<>();
        List<UtilityProvider> utilities = new ArrayList<>();
        List<ServiceProvider> services = new ArrayList<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                Cell cell = createCell(Character.toUpperCase(rows.get(r)[c]), r, c);
                grid[r][c] = cell;
                if (cell instanceof Zone zone) {
                    zones.add(zone);
                } else if (cell instanceof UtilityProvider provider) {
                    utilities.add(provider);
                } else if (cell instanceof ServiceProvider provider) {
                    services.add(provider);
                }
            }
        }

        return new City(grid, zones, utilities, services);
    }

    private static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private static char[] parseRow(String line) throws IOException {
        if (line.matches(".*\\s+.*")) {
            String[] tokens = line.split("\\s+");
            char[] chars = new char[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i].isEmpty()) {
                    throw new IOException("Invalid empty token in map row.");
                }
                chars[i] = tokens[i].charAt(0);
            }
            return chars;
        }
        return line.toCharArray();
    }

    private static Cell createCell(char symbol, int row, int col) throws IOException {
        return switch (symbol) {
            case 'H' -> new Housing(row, col);
            case 'I' -> new Industrial(row, col);
            case 'C' -> new Commercial(row, col);
            case 'P' -> new PowerPlant(row, col);
            case 'W' -> new WaterStation(row, col);
            case 'T' -> new InternetHub(row, col);
            case 'F' -> new PoliceStation(row, col);
            case 'D' -> new Hospital(row, col);
            case 'S' -> new School(row, col);
            case 'R' -> new Road(row, col);
            case 'E' -> new EmptyCell(row, col);
            default -> throw new IOException("Unknown map symbol '" + symbol + "' at row " + row + ", col " + col + ".");
        };
    }

    public int getRowCount() {
        return grid.length;
    }

    public int getColumnCount() {
        return grid[0].length;
    }

    public Cell getCell(int row, int col) {
        if (!isInside(row, col)) {
            return null;
        }
        return grid[row][col];
    }

    public boolean isInside(int row, int col) {
        return row >= 0 && row < getRowCount() && col >= 0 && col < getColumnCount();
    }

    public List<int[]> getNeighbors(int row, int col) {
        int[][] directions = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
        List<int[]> neighbors = new ArrayList<>();
        for (int[] direction : directions) {
            int nextRow = row + direction[0];
            int nextCol = col + direction[1];
            if (isInside(nextRow, nextCol)) {
                neighbors.add(new int[]{nextRow, nextCol});
            }
        }
        return neighbors;
    }

    public List<Zone> getZonesInRadius(int row, int col, int radius) {
        List<Zone> result = new ArrayList<>();
        for (Zone zone : zones) {
            double distance = Math.hypot(zone.getRow() - row, zone.getCol() - col);
            if (distance <= radius) {
                result.add(zone);
            }
        }
        return result;
    }

    public List<Zone> getZones() {
        return Collections.unmodifiableList(zones);
    }

    public List<UtilityProvider> getUtilityProviders() {
        return Collections.unmodifiableList(utilityProviders);
    }

    public List<ServiceProvider> getServiceProviders() {
        return Collections.unmodifiableList(serviceProviders);
    }
}

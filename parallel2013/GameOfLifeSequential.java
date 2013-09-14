import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.text.UnixTerminal;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class GameOfLifeSequential {
    int width, height;
    boolean[][] grid;
    int population;

    public GameOfLifeSequential(int height, int width) {
        this.height = height;
        this.width = width;
        this.grid = new boolean[height][width];
        this.population = 0;
    }

    public void randomise() {
        population = 0;
        Random random = new Random();
        for (int r = 0; r < height; ++r) {
            for (int c = 0; c < width; ++c) {
                grid[r][c] = random.nextBoolean();
                if (grid[r][c]) {
                    ++population;
                }
            }
        }
    }

    public void load(String name) throws FileNotFoundException, ArrayIndexOutOfBoundsException {
        grid = new boolean[height][width];
        population = 0;
        if (!name.startsWith("patterns/")) name = "patterns/" + name;
        if (!name.endsWith(".cells")) name = name + ".cells";
        Scanner scanner = new Scanner(new File(name));
        int r = 0;
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.startsWith("!")) continue;
            for (int c = 0; c < line.length(); ++c) {
                if (line.charAt(c) != ' ' && line.charAt(c) != '\n' && line.charAt(c) != '.') {
                    grid[r][c] = true;
                    ++population;
                }
            }
            ++r;
        }
    }

    public void update() {
        boolean[][] newGrid = new boolean[height][width];
        int population = 0;
        for (int r = 0; r < height; ++r) {
            for (int c = 0; c < width; ++c) {
                int neighbours = 0;
                for (int dr = -1; dr <= 1; ++dr) {
                    for (int dc = -1; dc <= 1; ++dc) {
                        if (dr == 0 && dc == 0) continue;
                        if (grid[(r + dr + height) % height][(c + dc + width) % width]) {
                            ++neighbours;
                        }
                    }
                }
                if (neighbours == 3 || (grid[r][c] && neighbours == 2)) {
                    newGrid[r][c] = true;
                    ++population;
                }
            }
        }
        grid = newGrid;
        this.population = population;
    }

    public void draw(Screen screen, int rows, int cols) {
        if (rows > height) rows = height;
        if (cols > width) cols = width;
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                if (grid[r][c]) {
                    screen.putString(c, r+1, "#", Color.WHITE, Color.WHITE);
                } else {
                    screen.putString(c, r+1, " ", Color.BLACK, Color.BLACK);
                }
            }
        }
    }

    public void draw(Screen screen) {
        draw(screen,
             screen.getTerminal().getTerminalSize().getRows() - 1,
             screen.getTerminal().getTerminalSize().getColumns());
    }

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(
                Arrays.asList("n", "no-draw"),
                "Do not draw the grid, so that performance can be benchmarked.  " +
                        "Requires width, height, and generations to be set.");
        parser.acceptsAll(
                Arrays.asList("g", "generations"),
                "Number of generations to run before halting.  If set to 0 it will run indefinitely.")
                .requiredIf("no-draw")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(0);
        parser.acceptsAll(
                Arrays.asList("w", "width"),
                "Set the width of the entire cell grid.  If set to 0 the width of the terminal is used.")
                .requiredIf("no-draw")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(0);
        parser.acceptsAll(
                Arrays.asList("h", "height"),
                "Set the height of the entire cell grid.  If set to 0 the height of the terminal is used.")
                .requiredIf("no-draw")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(0);
        parser.acceptsAll(
                Arrays.asList("p", "pattern"),
                "Load a specified pattern.")
                .withRequiredArg()
                .ofType(String.class);
        parser.acceptsAll(
                Arrays.asList("l", "list-patterns"),
                "List all available patterns");
        parser.acceptsAll(
                Arrays.asList("r", "random"),
                "Randomise the grid uniformly.  This is the default behaviour.");
        parser.acceptsAll(
                Arrays.asList("?", "help"),
                "Show help.")
                .forHelp();

        OptionSet options = parser.parse(args);

        if (options.has("help")) {
            try {
                parser.printHelpOn(System.out);
            } catch (IOException e) {
                System.exit(1);
            }
            return;
        }

        if (options.has("list-patterns")) {
            File[] files = (new File("./patterns")).listFiles();
            if (files == null) return;
            Arrays.sort(files);
            for (File file : files) {
                String name = file.getName();
                if (name.endsWith(".cells")) {
                    System.out.print(name.substring(0, name.length() - 6) + " ");
                }
            }
            System.out.println();
            return;
        }

        int width = (Integer) options.valueOf("width");
        int height = (Integer) options.valueOf("height");
        int generations = (Integer) options.valueOf("generations");

        Screen screen = null;
        if (options.has("no-draw")) {
            if (width == 0 || height == 0 || generations == 0) {
                System.out.println("width, height, and generations must be set to positive values");
                System.exit(2);
            }
        } else {
            try {
                screen = TerminalFacade.createScreen(new UnixTerminal(System.in, System.out, Charset.defaultCharset()));
            } catch (Exception e) {
                screen = TerminalFacade.createScreen();
            }
            screen.startScreen();
            if (width == 0) width = screen.getTerminal().getTerminalSize().getColumns();
            if (height == 0) height = screen.getTerminal().getTerminalSize().getRows() - 1;
        }

        GameOfLifeSequential gol = new GameOfLifeSequential(height, width);

        if (options.hasArgument("pattern") && !options.has("random")) {
            try {
                gol.load((String) options.valueOf("pattern"));
            } catch (FileNotFoundException e) {
                System.out.println("pattern not found");
                System.exit(2);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("pattern too large for grid");
                System.exit(2);
            }
        } else {
            gol.randomise();
        }

        long startTime = System.currentTimeMillis();
        if (options.has("no-draw")) {
            for (int gen = 0; gen < generations; ++gen) {
                gol.update();
            }
            System.out.println(String.format("generations = %d, population = %d, time = %.3f",
                    generations, gol.population, (System.currentTimeMillis() - startTime) * 1e-3));
        } else {
            assert screen != null;
            gol.draw(screen);
            screen.putString(0, 0,
                    String.format("generations = 0, population = %d, time = 0 | press esc to halt/exit", gol.population),
                    Color.DEFAULT, Color.DEFAULT);
            screen.refresh();
            for (int gen = 1; gen <= generations || generations == 0; ++gen) {
                Key key = screen.readInput();
                if (key != null && key.getKind() == Key.Kind.Escape) break;
                gol.update();
                gol.draw(screen);
                screen.putString(0, 0,
                        String.format("generations = %d, population = %d, time = %.3f | press esc to halt/exit",
                                gen, gol.population, (System.currentTimeMillis() - startTime) * 1e-3),
                        Color.DEFAULT, Color.DEFAULT);
                screen.refresh();
            }
            Key key;
            do {
                key = screen.readInput();
            } while (key == null || key.getKind() != Key.Kind.Escape);
            screen.stopScreen();
        }
    }
}

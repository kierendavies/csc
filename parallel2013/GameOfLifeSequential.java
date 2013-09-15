import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal.Color;
import com.googlecode.lanterna.terminal.text.UnixTerminal;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Random;

public class GameOfLifeSequential {
    static final double resizeFactor = Math.sqrt(2);
    int oldLeft, oldTop, oldWidth, oldHeight;
    int newLeft, newTop, newWidth, newHeight;
    boolean[][] oldGrid;
    boolean[][] newGrid;
    int population;

    public GameOfLifeSequential(int width, int height) {
        oldWidth = width;
        oldHeight = height;
        oldLeft = 0;
        oldTop = 0;
        newWidth = width;
        newHeight = height;
        newLeft = 0;
        newTop = 0;
        oldGrid = new boolean[oldWidth][oldHeight];
        newGrid = new boolean[newWidth][newHeight];
        population = 0;
    }

    private boolean getCell(int x, int y) {
        return getOldCell(x, y);
    }

    private boolean getOldCell(int x, int y) {
        if (x < oldLeft || x >= oldLeft +  oldWidth ||
                y < oldTop || y >= oldTop + oldHeight) {
            return false;
        }
        return oldGrid[x - oldLeft][y - oldTop];
    }

    private void setNewCellLive(int x, int y) {
        newGrid[x - newLeft][y - newTop] = true;
    }

    public void randomise() {
        population = 0;
        Random random = new Random();
        for (int x = 1; x < oldWidth - 1; ++x) {
            for (int y = 1; y < oldHeight - 1; ++y) {
                oldGrid[x][y] = random.nextBoolean();
                if (oldGrid[x][y]) {
                    ++population;
                }
            }
        }
        oldLeft = -oldWidth/2;
        oldTop = -oldHeight/2;
        newLeft = oldLeft;
        newTop = oldTop;
    }

    public void load(String name) throws IOException {
        population = 0;
        if (!name.startsWith("patterns/")) name = "patterns/" + name;
        if (!name.endsWith(".cells")) name = name + ".cells";
        BufferedReader reader = new BufferedReader(new FileReader(name));
        String line;

        // skip comments
        while (reader.readLine().startsWith("!")) {
            // mark beginning, gets overwritten until it is correct
            reader.mark(200000);  // should be an adequate readahead limit
        }

        // check pattern size
        int width = 0, height = 0;
        while ((line = reader.readLine()) != null) {
            ++height;
            if (line.length() > width) width = line.length();
        }
        width += 2;  // necessary padding
        height += 2;

        oldWidth = width;
        newWidth = width;
        oldHeight = height;
        newHeight = height;
        oldGrid = new boolean[oldWidth][oldHeight];
        newGrid = new boolean[newWidth][newHeight];

        // assign cells
        reader.reset();
        int y = 1;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("!")) continue;
            for (int x = 0; x < line.length(); ++x) {
                if (line.charAt(x) != ' ' && line.charAt(x) != '\n' && line.charAt(x) != '.') {
                    oldGrid[x + 1][y] = true;
                    ++population;
                }
            }
            ++y;
        }

        oldLeft = -oldWidth/2;
        oldTop = -oldHeight/2;
        newLeft = oldLeft;
        newTop = oldTop;
    }

    public void update() {
        int population = 0;
        boolean resizeLeft = false,
                resizeRight = false,
                resizeUp = false,
                resizeDown = false;

        // iterate over all cells
        for (int x = newLeft; x < newLeft + newWidth; ++x) {
            for (int y = newTop; y < newTop + newHeight; ++y) {
                int neighbours = 0;
                for (int dx = -1; dx <= 1; ++dx) {
                    for (int dy = -1; dy <= 1; ++dy) {
                        if (dx == 0 && dy == 0) continue;
                        if (getOldCell(x + dx, y + dy)) {
                            ++neighbours;
                        }
                    }
                }
                if (neighbours == 3 || (getOldCell(x, y) && neighbours == 2)) {
                    setNewCellLive(x, y);
                    ++population;
                    // check if grid needs resizing
                    if (x == newLeft) resizeLeft = true;
                    if (x == newLeft + newWidth - 1) resizeRight = true;
                    if (y == newTop) resizeUp = true;
                    if (y == newTop + newHeight - 1) resizeDown = true;
                }
            }
        }

        this.population = population;

        // switch grids
        boolean[][] tmp = oldGrid;
        oldGrid = newGrid;
        if (resizeLeft || resizeRight || resizeUp || resizeDown) {
            // reallocate new grid
            // scale from midpoint in designated direction
            if (resizeLeft) {
                newLeft -= (int) Math.ceil(resizeFactor * oldWidth / 2);
                newWidth += (int) Math.ceil(resizeFactor * oldWidth / 2);
            }
            if (resizeRight) {
                newWidth += (int) Math.ceil(resizeFactor * oldWidth / 2);
            }
            if (resizeUp) {
                newTop -= (int) Math.ceil(resizeFactor * oldHeight / 2);
                newHeight += (int) Math.ceil(resizeFactor * oldHeight / 2);
            }
            if (resizeDown) {
                newHeight += (int) Math.ceil(resizeFactor * oldHeight / 2);
            }
            newGrid = new boolean[newWidth][newHeight];
        } else if (oldWidth != newWidth || oldHeight != newHeight) {
            oldLeft = newLeft;
            oldTop = newTop;
            oldWidth = newWidth;
            oldHeight = newHeight;
            newGrid = new boolean[newWidth][newHeight];
        } else {
            // reuse old grid
            newGrid = tmp;
            for (int x = 0; x < newWidth; ++x) {
                for (int y = 0; y < newHeight; ++y) {
                    newGrid[x][y] = false;
                }
            }
        }
    }

    public void draw(Screen screen, int left, int top, int width, int height) {
        for (int r = 0; r < height; ++r) {
            for (int c = 0; c < width; ++c) {
                if (getCell(c + left, r + top)) {
                    screen.putString(c, r+1, "#", Color.WHITE, Color.WHITE);
                } else {
                    screen.putString(c, r+1, " ", Color.BLACK, Color.BLACK);
                }
            }
        }
    }

    public void draw(Screen screen, int width, int height) {
        draw(screen, -width/2, -height/2, width, height);
    }

    public void draw(Screen screen) {
        draw(screen,
             screen.getTerminal().getTerminalSize().getColumns(),
             screen.getTerminal().getTerminalSize().getRows() - 1);
    }

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(
                Arrays.asList("n", "no-draw"),
                "Do not draw the grid, so that performance can be benchmarked.  " +
                        "Requires generations to be set.");
        parser.acceptsAll(
                Arrays.asList("g", "generations"),
                "Number of generations to run before halting.  If set to 0 it will run indefinitely.")
                .requiredIf("no-draw")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(0);
        parser.acceptsAll(
                Arrays.asList("w", "width"),
                "Set the initial width of the cell grid.")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(100);
        parser.acceptsAll(
                Arrays.asList("h", "height"),
                "Set the initial height of the cell grid.")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(100);
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
//            if (width == 0) width = screen.getTerminal().getTerminalSize().getColumns();
//            if (height == 0) height = screen.getTerminal().getTerminalSize().getRows() - 1;
        }

        GameOfLifeSequential gol = new GameOfLifeSequential(width, height);

        if (options.hasArgument("pattern") && !options.has("random")) {
            try {
                gol.load((String) options.valueOf("pattern"));
            } catch (IOException e) {
                System.out.println("pattern not found");
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

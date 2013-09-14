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
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

class RangeInfo {
    public int population;
    public int liveTop;
    public int liveBottom;
    public int liveLeft;
    public int liveRight;

    RangeInfo(int population, int liveTop, int liveBottom, int liveLeft, int liveRight) {
        this.population = population;
        this.liveTop = liveTop;
        this.liveBottom = liveBottom;
        this.liveLeft = liveLeft;
        this.liveRight = liveRight;
    }
}

@SuppressWarnings("serial")
public class GameOfLife extends RecursiveTask<RangeInfo> {
    static int cutoff;
    int width, height;
    boolean[][] grid;
    boolean[][] newGrid;
    int population = 0;

    // used to store the live rectangle in the root instance, and the recursive range in subinstances
    // negative values are fine
    int top, bottom, left, right;

    public GameOfLife(int height, int width) {
        this.width = width;
        this.height = height;
        this.grid = new boolean[height][width];
        this.newGrid = new boolean[height][width];
    }

    public GameOfLife(boolean[][] grid, boolean[][] newGrid, int height, int width, int top, int bottom, int left, int right) {
        this.grid = grid;
        this.newGrid = newGrid;
        this.height = height;
        this.width = width;
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    private boolean isLiveAt(int r, int c) {
        r = (r + height) % height;
        c = (c + width) % width;
        return grid[r][c];
    }

    private void setLiveAt(int r, int c) {
        r = (r + height) % height;
        c = (c + width) % width;
        grid[r][c] = true;
    }

    private void setNewLiveAt(int r, int c) {
        r = (r + height) % height;
        c = (c + width) % width;
        newGrid[r][c] = true;
    }

    private void setNewDeadAt(int r, int c) {
        r = (r + height) % height;
        c = (c + width) % width;
        newGrid[r][c] = false;
    }

    public void randomise() {
        population = 0;
        Random random = new Random();
        for (int r = 0; r < height; ++r) {
            for (int c = 0; c < width; ++c) {
                if (random.nextBoolean()) {
                    setLiveAt(r, c);
                    ++population;
                }
            }
        }
        // probably the whole range has been covered
        top = 0;
        bottom = height;
        left = 0;
        right = width;
    }

    public void load(String name) throws FileNotFoundException, ArrayIndexOutOfBoundsException {
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
                    setLiveAt(r, c);
                    ++population;
                }
            }
            // update the live range with the longest line
            if (line.length() > right) right = line.length();
            ++r;
        }
        // and the last line
        bottom = r;
    }

    public void update(ForkJoinPool pool) {
        // widen live range
        top -= 1;
        bottom += 1;
        left -= 1;
        right += 1;
        // stop overflow
        if (bottom - top >= height) {
            top = 0;
            bottom = height;
        }
        if (right - left >= width) {
            left = 0;
            right = width;
        }

        // do parallel computation
        RangeInfo info = pool.invoke(new GameOfLife(grid, newGrid, height, width, top, bottom, left, right));

        // reset newGrid
        // swap the grids and clear to avoid reallocating memory
        boolean[][] temp = grid;
        grid = newGrid;
        newGrid = temp;
        // clear must be done with old values of live range
        for (int r = top; r < bottom; ++r) {
            for (int c = left; c < right; ++c) {
                setNewDeadAt(r, c);
            }
        }

        // update population and range
        population = info.population;
        top = info.liveTop;
        bottom = info.liveBottom + 1;
        left = info.liveLeft;
        right = info.liveRight + 1;
    }

    public RangeInfo compute() {
        // directions flipped for cunning reasons of max/min
        RangeInfo info = new RangeInfo(0, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE);
        if ((bottom - top) * (right - left) <= cutoff) {
            // compute sequentially
            for (int r = top; r < bottom; ++r) {
                for (int c = left; c < right; ++c) {
                    int neighbours = 0;
                    for (int dr = -1; dr <= 1; ++dr) {
                        for (int dc = -1; dc <= 1; ++dc) {
                            if (dr == 0 && dc == 0) continue;
                            if (isLiveAt(r + dr, c + dc)) {
                                ++neighbours;
                            }
                        }
                    }
                    if (neighbours == 3 || (isLiveAt(r, c) && neighbours == 2)) {
                        setNewLiveAt(r, c);
                        ++info.population;
                        if (r < info.liveTop) info.liveTop = r;
                        if (r > info.liveBottom) info.liveBottom = r;
                        if (c < info.liveLeft) info.liveLeft = c;
                        if (c > info.liveRight) info.liveRight = c;
                    }
                }
            }
        } else {
            // split into quadrants, compute concurrently
            int rMid = (top + bottom) / 2;
            int cMid = (left + right) / 2;
            GameOfLife tl = new GameOfLife(grid, newGrid, height, width, top, rMid, left, cMid);
            GameOfLife tr = new GameOfLife(grid, newGrid, height, width, top, rMid, cMid, right);
            GameOfLife bl = new GameOfLife(grid, newGrid, height, width, rMid, bottom, left, cMid);
            GameOfLife br = new GameOfLife(grid, newGrid, height, width, rMid, bottom, cMid, right);
            tl.fork();
            tr.fork();
            bl.fork();
            RangeInfo brInfo = br.compute();
            RangeInfo tlInfo = tl.join();
            RangeInfo trInfo = tr.join();
            RangeInfo blInfo = bl.join();
            for (RangeInfo i : Arrays.asList(tlInfo, trInfo, blInfo, brInfo)) {
                info.population += i.population;
                if (i.liveTop < info.liveTop) info.liveTop = i.liveTop;
                if (i.liveBottom > info.liveBottom) info.liveBottom = i.liveBottom;
                if (i.liveLeft < info.liveLeft) info.liveLeft = i.liveLeft;
                if (i.liveRight > info.liveRight) info.liveRight = i.liveRight;
            }
        }
        return info;
    }

    public void draw(Screen screen, int rows, int cols) {
        if (rows > height) rows = height;
        if (cols > width) cols = width;
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                if (isLiveAt(r, c)) {
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
                Arrays.asList("c", "cutoff"),
                "Set the number of cells for sequential cutoff.")
                .withRequiredArg()
                .ofType(Integer.class)
                .defaultsTo(10000);
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
        GameOfLife.cutoff = (Integer) options.valueOf("cutoff");

        Screen screen = null;
        if (options.has("no-draw")) {
            if (width == 0 || height == 0 || generations == 0) {
                System.out.println("width, height, and generations must be set to positive values");
                System.exit(2);
            }
        } else {
//            try {
//                screen = TerminalFacade.createScreen(new UnixTerminal(System.in, System.out, Charset.defaultCharset()));
//            } catch (Exception e) {
                screen = TerminalFacade.createScreen();
//            }
            screen.startScreen();
            if (width == 0) width = screen.getTerminal().getTerminalSize().getColumns();
            if (height == 0) height = screen.getTerminal().getTerminalSize().getRows() - 1;
        }

        GameOfLife gol = new GameOfLife(height, width);

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

        ForkJoinPool pool = new ForkJoinPool();

        long startTime = System.currentTimeMillis();
        if (options.has("no-draw")) {
            for (int gen = 0; gen < generations; ++gen) {
                gol.update(pool);
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
                gol.update(pool);
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

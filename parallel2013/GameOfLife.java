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
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

class GridInfo {
    public int population;
    public int left, right, top, bottom;

    public GridInfo() {
        population = 0;
        left = 0;
        right = 0;
        top = 0;
        bottom = 0;
    }

    public void add(GridInfo other) {
        population += other.population;
        if (other.left < left) left = other.left;
        if (other.right > right) right = other.right;
        if (other.top < top) top = other.top;
        if (other.bottom > bottom) bottom = other.bottom;
    }
}

@SuppressWarnings("serial")
public class GameOfLife extends RecursiveTask<GridInfo> {
    static final int cutoff = 25000;
    static final double resizeFactor = Math.sqrt(2);

    // grid dimensions
    int oldLeft, oldTop, oldWidth, oldHeight;
    int newLeft, newTop, newWidth, newHeight;
    boolean[][] oldGrid;
    boolean[][] newGrid;

    int population;
    GameOfLife parent;

    // live/recursive range dimensions
    int top, bottom, left, right;

    public GameOfLife(int width, int height) {
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
        parent = this;
        left = oldLeft;
        right = oldLeft + oldWidth;
        top = oldTop;
        bottom = oldTop + oldHeight;
    }

    public GameOfLife(GameOfLife parent, int left, int right, int top, int bottom) {
        this.parent = parent;
        oldGrid = parent.oldGrid;
        newGrid = parent.newGrid;
        oldWidth = parent.oldWidth;
        oldHeight = parent.oldHeight;
        oldLeft = parent.oldLeft;
        oldTop = parent.oldTop;
        newWidth = parent.newWidth;
        newHeight = parent.newHeight;
        newLeft = parent.newLeft;
        newTop = parent.newTop;
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    private boolean getCell(int x, int y) {
        return getOldCell(x, y);
    }

    private boolean getOldCell(int x, int y) {
        if (parent != this) return parent.getOldCell(x, y);
        if (x < oldLeft || x >= oldLeft +  oldWidth ||
                y < oldTop || y >= oldTop + oldHeight) {
            return false;
        }
        return oldGrid[x - oldLeft][y - oldTop];
    }

    private void setNewCellLive(int x, int y) {
        if (parent != this) {
            parent.setNewCellLive(x, y);
        } else {
            newGrid[x - newLeft][y - newTop] = true;
        }
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

    public void update(ForkJoinPool pool) {
        // do parallel computation
        GridInfo info = pool.invoke(new GameOfLife(this, left - 1, right + 1, top - 1, bottom + 1));
        population = info.population;
        left = info.left;
        right = info.right + 1;
        top = info.top;
        bottom = info.bottom + 1;

        // check if grid needs resizing
        boolean resizeLeft = (info.left <= newLeft),
                resizeRight = (info.right >= newLeft + newWidth - 1),
                resizeUp = (info.top <= newTop),
                resizeDown = (info.bottom >= newTop + newHeight - 1);

        // switch grids
        boolean[][] tmp = oldGrid;
        oldGrid = newGrid;
        if (resizeLeft || resizeRight || resizeUp || resizeDown) {
            // reallocate new grid
            // scale from midpoint in designated direction
            if (resizeLeft) {
                newLeft -=(int) Math.ceil(resizeFactor * oldWidth / 2);
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

    public GridInfo compute() {
        GridInfo info;
        if ((bottom - top) * (right - left) <= cutoff) {
            // compute sequentially
            info = new GridInfo();
            for (int x = left; x < right; ++x) {
                for (int y = top; y < bottom; ++y) {
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
                        ++info.population;
                        // check if grid needs resizing
                        if (x < info.left) info.left = x;
                        if (x > info.right) info.right = x;
                        if (y < info.top) info.top = y;
                        if (y > info.bottom) info.bottom = y;
                        //                        if (x == newLeft) info.resizeLeft = true;
//                        if (x == newLeft + newWidth - 1) info.resizeRight = true;
//                        if (y == newTop) info.resizeUp = true;
//                        if (y == newTop + newHeight - 1) info.resizeDown = true;
                    }
                }
            }
        } else {
            // split into quadrants, compute concurrently
            int xMid = (left + right) / 2;
            int yMid = (top + bottom) / 2;
            GameOfLife tl = new GameOfLife(parent, left, xMid, top, yMid);
            GameOfLife tr = new GameOfLife(parent, xMid, right, top, yMid);
            GameOfLife bl = new GameOfLife(parent, left, xMid, yMid, bottom);
            GameOfLife br = new GameOfLife(parent, xMid, right, yMid, bottom);
            tl.fork();
            tr.fork();
            bl.fork();
            info = br.compute();
            info.add(tl.join());
            info.add(tr.join());
            info.add(bl.join());
        }
        return info;
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

        GameOfLife gol = new GameOfLife(width, height);

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

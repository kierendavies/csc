import GameEngine.GameObject;

import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kieren
 * Date: 9/12/12
 * Time: 3:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class Grid {
    private int gameWidth;
    private int gameHeight;
    private int cellWidth;
    private int cellHeight;
    private Set<GameObject>[][] cells;

    public Grid(int gameWidth, int gameHeight, int cellWidth, int cellHeight) {
        // game width and height MUST NOT be underestimates
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        cells = new HashSet[divRndUp(gameWidth, cellWidth)][divRndUp(gameHeight, cellHeight)];
    }

    private int divRndUp(int a, int b) {
        // divide rounding up
        // sneaky maths
        return (a + b - 1) / b;
    }

    public void add(GameObject object) {
        Rectangle2D.Float box = object.getAABoundingBox();
        int left = (int) box.x / cellWidth;
        int right = divRndUp((int) (box.x + box.width), cellWidth);
        int top = (int) box.y / cellHeight;
        int bottom = divRndUp((int) (box.y + box.height), cellHeight);
        // top and bottom might be logically swapped, but it doesn't matter

        for (int x = left; x <= right; ++x) {
            for (int y = top; y <= bottom; ++y) {
                cells[x][y].add(object);
            }
        }
    }

    public void remove(GameObject object) {
        Rectangle2D.Float box = object.getAABoundingBox();
        int left = (int) box.x / cellWidth;
        int right = divRndUp((int) (box.x + box.width), cellWidth);
        int top = (int) box.y / cellHeight;
        int bottom = divRndUp((int) (box.y + box.height), cellHeight);

        for (int x = left; x <= right; ++x) {
            for (int y = top; y <= bottom; ++y) {
                cells[x][y].remove(object);
            }
        }
    }

    public Collection<GameObject> getHits(GameObject object) {
        Collection<GameObject> hits = new HashSet<GameObject>();

        Rectangle2D.Float box = object.getAABoundingBox();
        int left = (int) box.x / cellWidth;
        int right = divRndUp((int) (box.x + box.width), cellWidth);
        int top = (int) box.y / cellHeight;
        int bottom = divRndUp((int) (box.y + box.height), cellHeight);

        for (int x = left; x <= right; ++x) {
            for (int y = top; y <= bottom; ++y) {
                hits.addAll(cells[x][y]);
            }
        }

        // if we're in the grid, let's not collide with ourselves
        hits.remove(object);

        return hits;
    }
}

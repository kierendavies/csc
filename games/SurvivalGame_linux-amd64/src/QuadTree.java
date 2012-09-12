import GameEngine.GameObject;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Work in progress; not in use
 */
public class QuadTree {
    private Rectangle2D.Float boundary;
    private Point2D.Float point;

    private QuadTree northWest;
    private QuadTree northEast;
    private QuadTree southWest;
    private QuadTree southEast;

    public static void main(String[] args) {
        GameObject gameObject = new GameObject(0, 0);
        Point2D.Float point = gameObject.getPosition();
        Rectangle2D.Float box = gameObject.getAABoundingBox();
        box.contains(point);
        box.intersects(box);
    }

    public QuadTree(Rectangle2D.Float boundary) {
        this.boundary = boundary;
    }

    public boolean insert(Point2D.Float newPoint) {
        if (!boundary.contains(newPoint)) {
            return false;
        }
        if (point != null) {
            point = newPoint;
        }
        if (northWest != null) {
            subdivide();
        }
    }

    public void subdivide() {
        float bwot = boundary.width/2;
        float bhot = boundary.height/2;
        northWest = new QuadTree(new Rectangle2D.Float(boundary.x,      boundary.y,      bwot, bhot));
        northEast = new QuadTree(new Rectangle2D.Float(boundary.x+bwot, boundary.y,      bwot, bhot));
        southWest = new QuadTree(new Rectangle2D.Float(boundary.x,      boundary.y+bhot, bwot, bhot));
        southEast = new QuadTree(new Rectangle2D.Float(boundary.x+bwot, boundary.y+bhot, bwot, bhot));
    }

    public void queryRange(Rectangle2D.Float range) {

    }
}

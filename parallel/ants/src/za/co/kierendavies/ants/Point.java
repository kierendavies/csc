package za.co.kierendavies.ants;

public class Point {
    public int key;
    public double x, y;

    public Point(int key, double x, double y) {
        this.key = key;
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "(" + key + ": " + x + ", " + y + ")";
    }

    public boolean equals(Point that) {
        return this.x == that.x && this.y == that.y;
    }
}

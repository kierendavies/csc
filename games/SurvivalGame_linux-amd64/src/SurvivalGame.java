import GameEngine.Game;
import GameEngine.GameFont;
import GameEngine.GameObject;
import GameEngine.GameTexture;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;
import java.util.Vector;

public class SurvivalGame extends Game {
    // Offset of the screen
    private Point2D.Float offset = new Point2D.Float(0, 0);

    // game area size
    private int worldWidth;
    private int worldHeight;

    private boolean alive = true;

    // A Collection of GameObjects in the world that will be used with the collision detection system
    private Vector<GameObject> objects = new Vector<GameObject>();
    // and it's associated collisionGrid
    private Grid collisionGrid;

    // Grid GameObjects
    private GameObject[][] gridTile;

    // The cooldown of the gun (set this to 0 for a cool effect :> )
    private int cooldown = 10;
    private int cooldownTimer = 0;

    // Important GameObjects
    private PlayerObject player; // the player

    //Textures that will be used
    private GameTexture bulletTexture;

    //GameFonts that will be used
    private GameFont arial, serif;

    // The position of the mouse
    private Point2D.Float mousePos = new Point2D.Float(0, 0);

    // a counter for how far the mousewheel has been moved (just an example)
    private int mouseWheelTick = 0;

    // Information for the random line at the bottom of the screen
    Point2D.Float[] linePositions = {new Point2D.Float(0, 0), new Point2D.Float(100, 100)};
    float[][] lineColours = {{1.0f, 1.0f, 1.0f, 1.0f}, {1.0f, 0.0f, 0.0f, 1.0f}};

    public SurvivalGame(int GFPS) {
        super(GFPS);
    }

    public void initStep(ResourceLoader loader) {

        //Loading up some fonts
        arial = loader.loadFont(new Font("Arial", Font.ITALIC, 48));
        serif = loader.loadFont(new Font("Serif", Font.PLAIN, 48));


        //Loading up our textures
        GameTexture softRockTexture = loader.loadTexture("Textures/soft_rock.png");
        GameTexture wallTexture = loader.loadTexture("Textures/rock.png");
        GameTexture floorTexture = loader.loadTexture("Textures/sand_tile.png");
        bulletTexture = loader.loadTexture("Textures/bullet.png");

        int gridSize = 10;
        worldWidth = gridSize * floorTexture.getWidth();
        worldHeight = gridSize * floorTexture.getHeight();

        // creating the grid (with hacky sizing)
        collisionGrid = new Grid(worldWidth+floorTexture.getWidth(), worldHeight+floorTexture.getWidth(), wallTexture.getWidth(), wallTexture.getHeight());

        // creating the floor objects
        gridTile = new GameObject[gridSize][gridSize];
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                gridTile[i][j] = new GameObject(floorTexture.getWidth() * i, floorTexture.getHeight() * j);
                gridTile[i][j].addTexture(floorTexture, 0, 0);
            }
        }

        // Creating wall objects
        for (int i = 0; i < floorTexture.getWidth() * gridSize; i += wallTexture.getWidth()) {
            WallObject go = new WallObject(i, 0);
            go.addTexture(wallTexture, 0, 0);
            objects.add(go);
            collisionGrid.add(go);

            go = new WallObject(i, floorTexture.getHeight() * (gridSize - 1));
            go.addTexture(wallTexture, 0, 0);
            objects.add(go);
            collisionGrid.add(go);
        }
        for (int i = floorTexture.getHeight(); i < floorTexture.getHeight() * (gridSize - 1); i += wallTexture.getHeight()) {
            WallObject go = new WallObject(0, i);
            go.addTexture(wallTexture, 0, 0);
            objects.add(go);
            collisionGrid.add(go);

            go = new WallObject(wallTexture.getWidth() * (gridSize - 1), i);
            go.addTexture(wallTexture, 0, 0);
            objects.add(go);
            collisionGrid.add(go);
        }

        // creating some random rocks to shoot
        for (int i = 0; i < 8; i++) {

            float x = (float) ((Math.random() * (gridSize - 4) + 2) * floorTexture.getWidth());
            float y = (float) ((Math.random() * (gridSize - 4) + 2) * floorTexture.getHeight());

            GameObject go = new GameObject(x, y);
            go.addTexture(softRockTexture, 0, 0);
            objects.add(go);
            collisionGrid.add(go);
        }

        // Creating the player's ship
        player = new PlayerObject(
                (float) (floorTexture.getWidth() * gridSize) / 2f,
                (float) (floorTexture.getHeight() * gridSize) / 2f);

        for (int i = 0; i < 72; i++) {
            player.addTexture(loader.load("Textures/dude/dude" + i + ".png"), 64, 64);
        }

        // don't add player to grid - this is slightly faster because player is so dynamic
        // and it is safe because it is the only object not in the grid
        objects.add(player);
    }

    // this method is used to fire a bullet 
    public void fireBullet() {

        cooldownTimer = cooldown;

        float dir = 90 + player.getDegreesTo(mousePos);
        BulletObject bullet =
                new BulletObject(
                        player.getPosition().x + (float) Math.sin(Math.toRadians(dir - 5)) * 60,
                        player.getPosition().y - (float) Math.cos(Math.toRadians(dir - 5)) * 60,
                        1f, 300, bulletTexture, collisionGrid);

        //bullet.setVelocity(player.getVelocity());
        bullet.applyForceInDirection(dir, 6f);

        objects.add(bullet);
        collisionGrid.add(bullet);
    }

    public static boolean isPointInBox(final Point2D.Float point, final Rectangle2D.Float box) {
        return box.contains(point.x, point.y);
    }

    // This is a pretty bad implementation and faster ones exist, it is suggested you find a better one. At least try make use of the Rectangle2D's createIntersection method.
    public static boolean boxIntersectBox(final Rectangle2D.Float d, final Rectangle2D.Float d2) {
        return isPointInBox(new Point2D.Float(d.x, d.y), d2) ||
                isPointInBox(new Point2D.Float(d.x, d.y + d.height), d2) ||
                isPointInBox(new Point2D.Float(d.x + d.width, d.y), d2) ||
                isPointInBox(new Point2D.Float(d.x + d.width, d.y + d.height), d2) ||
                isPointInBox(new Point2D.Float(d2.x, d2.y), d) ||
                isPointInBox(new Point2D.Float(d2.x, d2.y + d2.height), d) ||
                isPointInBox(new Point2D.Float(d2.x + d2.width, d2.y), d) ||
                isPointInBox(new Point2D.Float(d2.x + d2.width, d2.y + d2.height), d);
    }

    private void handlePlayerMovement(GameInputInterface gii) {
        if(gii.keyDown(Config.keyExit)) {
            Runner.endGame();
        }

        Point2D.Float move = new Point2D.Float(0, 0);

        if (gii.keyDown(Config.keyUp)) {
            move.y++;
        }
        if (gii.keyDown(Config.keyDown)) {
            move.y--;
        }
        if (gii.keyDown(Config.keyRight)) {
            move.x++;
        }
        if (gii.keyDown(Config.keyLeft)) {
            move.x--;
        }

        player.inputMovement(move);

        if (cooldownTimer <= 0) {
            if (gii.mouseButtonDown(MouseEvent.BUTTON1)) {
                fireBullet();
            }
        }
        cooldownTimer--;

    }

    private void handleCollisions() {
        // destroy bullets outside the game area because they break collision detection
        for (int i = 0; i < objects.size(); i++) {
            if (objects.elementAt(i) instanceof BulletObject) {
                float x = objects.elementAt(i).getPosition().x;
                float y = objects.elementAt(i).getPosition().y;
                if (x < 0 || y < 0 || x >= worldWidth-7 || y >= worldHeight-7) {
                    collisionGrid.remove(objects.elementAt(i));
                    objects.remove(i);
                    i--;
                }
            }
        }

        //checking each units for collisions
        Vector<Point2D.Float> playerCollisions = new Vector<Point2D.Float>();
        for (int i = 0; i < objects.size(); i++) {
            GameObject o1 = objects.elementAt(i);
            // check against matches from collisionGrid
            for (GameObject o2 : collisionGrid.getHits(o1)) {
                if (o1 instanceof WallObject && o2 instanceof WallObject) continue;  // do nothing anyway
                if (!boxIntersectBox(o1.getAABoundingBox(), o2.getAABoundingBox())) continue;  // no box intersection

                boolean collides = false;
                // assume no collision, then do pixel checking
                ByteBuffer buffer1 = o1.getCurrentTexture().getByteBuffer();
                ByteBuffer buffer2 = o2.getCurrentTexture().getByteBuffer();

                Rectangle bb1 = o1.getIntAABoundingBox();
                Rectangle bb2 = o2.getIntAABoundingBox();
                int left = Math.max(bb1.x, bb2.x);  // truncate unnecessary stuff on the left
                int right = Math.min(bb1.x+bb1.width, bb2.x+bb2.width) - 1;  // and right
                int top = Math.max(bb1.y, bb2.y);  // etc.
                int bottom = Math.min(bb1.y+bb1.height, bb2.y+bb2.height) - 1;
                // top and bottom might be logically swapped, but it doesn't matter
                // this engine is so broken; that -1 shouldn't be necessary

                for (int x = left; x <= right; x++) {
                    for (int y = top; y <= bottom; y++) {
                        // turns out i was doing this upside down last time
                        int a1 = buffer1.get(((x-bb1.x) + (bb1.height-y+bb1.y-1)*bb1.width)*4 + 3);
                        int a2 = buffer2.get(((x-bb2.x) + (bb2.height-y+bb2.y-1)*bb2.width)*4 + 3);

                        if (a1 == -1 && a2 == -1) {  // full alpha on both pixels
                            collides = true;
                            if (o1 instanceof PlayerObject) {
                                playerCollisions.add(new Point2D.Float(x, y));
                            } else break;
                        }
                    }
                    if (collides && !(o1 instanceof PlayerObject)) break;
                }

                if (collides) {
                    if (o1 instanceof BulletObject && o2 instanceof WallObject) {
                        o1.setMarkedForDestruction(true); // just destroy the bullet, not the wall
                    } else if (o1 instanceof WallObject && o2 instanceof BulletObject) {
                        o2.setMarkedForDestruction(true);
                    } else if (o1 instanceof PlayerObject) {
                        Point2D.Float collisionAverage = new Point2D.Float(0, 0);
                        int count = 0;
                        for (Point2D.Float collision : playerCollisions) {
                            collisionAverage.x += collision.x;
                            collisionAverage.y += collision.y;
                            count++;
                        }
                        collisionAverage.x /= count;
                        collisionAverage.y /= count;
                        player.setCollisionAt(collisionAverage);
                    } else {
                        o1.setMarkedForDestruction(true);
                        o2.setMarkedForDestruction(true);
                    }
                }
            }
        }

        // destroying units that need to be destroyed
        for (int i = 0; i < objects.size(); i++) {
            if (objects.elementAt(i).isMarkedForDestruction()) {
                if (objects.elementAt(i) == player) {
                    alive = false;
                    player = null;
                }
                // removing object from list of GameObjects
                collisionGrid.remove(objects.elementAt(i));
                objects.remove(i);
                i--;
            }
        }
    }

    //==================================================================================================

    public void logicStep(GameInputInterface gii) {

        // some examples of the mouse interface
        mouseWheelTick += gii.mouseWheelRotation();
        mousePos.x = (float) gii.mouseXScreenPosition() - offset.x;
        mousePos.y = (float) gii.mouseYScreenPosition() - offset.y;

        //----------------------------------

        if (alive) {
            handlePlayerMovement(gii);
            player.setDirection(90 + player.getDegreesTo(mousePos));
        }

        // NOTE: you must call doTimeStep for ALL game objects once per frame!
        // updating step for each object
        for (int i = 0; i < objects.size(); i++) {
            objects.elementAt(i).doTimeStep();
        }

        // setting the camera offset
        offset.x = -player.getPosition().x + (this.getViewportDimension().width / 2);
        offset.y = -player.getPosition().y + (this.getViewportDimension().height / 2);

        handleCollisions();
    }

    public void renderStep(GameDrawer drawer) {
        //For every object that you want to be rendered, you must call the draw function with it as a parameter

        // NOTE: Always draw transparent objects last!

        // Offsetting the world so that all objects are drawn
        drawer.setWorldOffset(offset.x, offset.y);
        drawer.setColour(1.0f, 1.0f, 1.0f, 1.0f);

        // drawing the ground tiles
        for (int i = 0; i < gridTile.length; i++) {
            for (int j = 0; j < gridTile[i].length; j++) {
                drawer.draw(gridTile[i][j], -1);
            }
        }

        // drawing all the objects in the game
        for (GameObject o : objects) {
            drawer.draw(o, 1.0f, 1.0f, 1.0f, 1.0f, 0);
        }


        // this is just a random line drawn in the corner of the screen
        drawer.draw(GameDrawer.LINES, linePositions, lineColours, 0.5f);

        if (player != null) {
            Point2D.Float[] playerLines = {mousePos, player.getPosition()};
            drawer.draw(GameDrawer.LINES, playerLines, lineColours, 0.5f);
        }

        drawer.setColour(1.0f, 1.0f, 1.0f, 1.0f);

        // Changing the offset to 0 so that drawn objects won't move with the camera
        drawer.setWorldOffset(0, 0);

        // this is just a random line drawn in the corner of the screen (but not offsetted this time ;) )
        drawer.draw(GameDrawer.LINES, linePositions, lineColours, 0.5f);


        // Some debug type info to demonstrate the font drawing
        if (player != null) {
            //drawer.draw(arial, "" + player.getDirection(), new Point2D.Float(20, 120), 1.0f, 0.5f, 0.0f, 0.7f, 0.1f);
            drawer.draw(arial, "" + player.getActiveTexture(), new Point2D.Float(20, 120), 1.0f, 0.5f, 0.0f, 0.7f, 0.1f);
        }
        drawer.draw(arial, "" + mouseWheelTick, new Point2D.Float(20, 68), 1.0f, 0.5f, 0.0f, 0.7f, 0.1f);
        drawer.draw(serif, "" + mousePos.x + ":" + mousePos.y, new Point2D.Float(20, 20), 1.0f, 0.5f, 0.0f, 0.7f, 0.1f);
    }
}









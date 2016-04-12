package charles.mobiledevfinalproject;

import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

// this is the data structure that will manage all information about the game
// the hex cells, where the mouse is at, moving the mouse, etc

public class HexGrid {
    // grid is actually 9x9 with cells on the edges for mouse to escape to
    public static final int cellWidth = 11;
    public static final int cellHeight = 11;
    public static final int startingWalls = 11;

    public class Cell {
        private static final int EMPTY = 0;
        private static final int WALL = 1;
        private static final int MOUSE = 2;

        Cell (int X, int Y) {
            x = X;
            y = Y;
        }

        // position
        private int x = 0;
        private int y = 0;

        // contents of this cell
        int contains = EMPTY;

        // -1 means cell is disconnected from edge
        int distFromEdge = -1;

        //getters
        public boolean isEmpty() {
            return contains == EMPTY;
        }
        public boolean isWall() {
            return contains == WALL;
        }
        public boolean isEdge() {
            return x == 0 || x == cellWidth - 1 ||
                    y == 0 || y == cellHeight - 1;
        }
        public boolean isMouse() {
            return contains == MOUSE;
        }
        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }

        // references to adjacent cells
        Cell NW = null;
        Cell NE = null;

        Cell W = null;
        Cell E = null;

        Cell SW = null;
        Cell SE = null;
    }


    private Cell[][] cells;
    private int moveCounter = 0;

    // the cell the mouse is currently in
    // if this is null then the mouse has
    // exited the grid and the player has lost
    private Cell mouseCell = null;

    // initialize hex grid
    public HexGrid() {
        cells = new Cell[cellWidth][cellHeight];

        // initialize cells
        for (int x = 0; x < cellWidth; x++) {
            for (int y = 0; y < cellHeight; y++) {
                cells[x][y] = new Cell(x, y);
            }
        }

        // link up cells
        for (int x = 0; x < cellWidth; x++) {
            for (int y = 0; y < cellHeight; y++) {

                Cell i = cells[x][y];

                // 0,0 is the top left most cell

                // (0,0) (1,0) (2,0) (3,0)
                //    (0,1) (1,1) (2,1) (3,1)
                // (0,2) (1,2) (2,2) (3,2)
                //    (0,3) (1,3) (2,3) (3,3)

                // link Cells to their neighbors
                // Cells that still have null as one of their neighbors
                // means it is on the edge of the board

                if (x < cellWidth - 1) {
                    i.E = cells[x + 1][y];
                }
                if (x > 0) {
                    i.W = cells[x - 1][y];
                }

                // links are slightly different based on the row they're on
                if (y % 2 == 1 ) {
                    if (y > 0) {
                        i.NE = cells[x][y - 1];
                        if (x > 0) {
                            i.NW = cells[x - 1][y - 1];
                        }
                    }
                    if (y < cellHeight - 1) {
                        i.SE = cells[x][y + 1];
                        if (x > 0) {
                            i.SW = cells[x - 1][y + 1];
                        }
                    }
                }
                else {
                    if (y > 0) {
                        i.NW = cells[x][y - 1];
                        if (x < cellWidth - 1) {
                            i.NE = cells[x + 1][y - 1];
                        }
                    }
                    if (y < cellHeight - 1) {
                        i.SW = cells[x][y + 1];
                        if (x < cellWidth - 1) {
                            i.SE = cells[x + 1][y + 1];
                        }
                    }
                }
            }
        }

        initialize();
    }

    // set up a new game
    public void initialize() {

        //reset hexgrid
        for (int x = 0; x < cellWidth; x++) {
            for (int y = 0; y < cellHeight; y++) {
                cells[x][y].contains = Cell.EMPTY;
            }
        }

        Random r = new Random();

        // TODO

        //randomize mouse placement near center
        int centerX = cellWidth / 2;
        int centerY = cellHeight / 2;

        switch (r.nextInt(7)) {
            case 0: mouseCell = cells[centerX][centerY]; break;
            case 1: mouseCell = cells[centerX][centerY].E; break;
            case 2: mouseCell = cells[centerX][centerY].NE; break;
            case 3: mouseCell = cells[centerX][centerY].NW; break;
            case 4: mouseCell = cells[centerX][centerY].SE; break;
            case 5: mouseCell = cells[centerX][centerY].SW; break;
            case 6: mouseCell = cells[centerX][centerY].W; break;
        }
        mouseCell.contains = Cell.MOUSE;

        // randomly place walls on hex grid
        int count = 0;
        while (count < startingWalls) {

            //ignore outer edge
            int x = 1 + r.nextInt(cellWidth - 2);
            int y = 1 + r.nextInt(cellHeight - 2);

            Cell c = getCell(x, y);
            if (c != null && c.isEmpty()) {
                c.contains = Cell.WALL;
                count++;
            }
        }

        // reset moveCounter
        moveCounter = 0;
        updateCellDistances();
    }

    // return true if wall is successfully placed
    // also increments players move counter
    public boolean placeWall(int x, int y) {

        if (    x != 0 && x != cellWidth - 1 &&
                y != 0 && y != cellHeight - 1 &&
                cells[x][y].isEmpty()) {

            cells[x][y].contains = Cell.WALL;
            updateCellDistances();
            moveCounter++;
            return true;
        }
        return false;
    }


    public Cell moveMouse () {

        // TODO

        if (mouseCell == null)
            return null;

        LinkedList<Cell> moveOptions = new LinkedList<Cell>();
        moveOptions.add(mouseCell.E);
        moveOptions.add(mouseCell.W);
        moveOptions.add(mouseCell.NE);
        moveOptions.add(mouseCell.NW);
        moveOptions.add(mouseCell.SE);
        moveOptions.add(mouseCell.SW);

        // find adjacent cell with lowest distance
        int lowestDist = Integer.MAX_VALUE;
        for (int i = 0; i < moveOptions.size(); i++) {

            Cell c = moveOptions.get(i);

            if (c != null && c.isEmpty() && c.distFromEdge >= 0 && c.distFromEdge < lowestDist) {
                lowestDist = c.distFromEdge;
            }
        }

        //remove non options
        for (int i = 0; i < moveOptions.size(); i++) {
            Cell c = moveOptions.get(i);
            if (    c == null || (!c.isEmpty() ||
                    (c.distFromEdge >= 0 && c.distFromEdge > lowestDist))) {
                moveOptions.remove(i);
                i--;
            }
        }

        // if two or more cell are equidistant from out of grid, pick one at random
        // if the mouse is successfully moved return true
        if (moveOptions.size() > 1) {
            Random r = new Random();
            mouseCell.contains = Cell.EMPTY;
            mouseCell = moveOptions.get(r.nextInt(moveOptions.size()));
            mouseCell.contains = Cell.MOUSE;
        }
        else if (moveOptions.size() == 1) {
            mouseCell.contains = Cell.EMPTY;
            mouseCell = moveOptions.get(0);
            mouseCell.contains = Cell.MOUSE;
        }
       // else {
       // mouse cannot move
       // }

        // return mouse cell
        // if mouse cell's distance is zero then the mouse has won
        return mouseCell;
    }

    public Cell getCell (int x, int y) {
        if (x >= 0 && x < cellWidth && y >= 0 && y < cellHeight) {
            return cells[x][y];
        }
        return null;
    }

    public Cell getMouseCell() {
        return mouseCell;
    }

    public int getMoveCounter() {
        return moveCounter;
    }

    // this is used for mouse movement
    private void updateCellDistances() {

        //use wavefront algorithm to determine distances from edge

        //all the cells that have a distance
        Stack<Cell> visited = new Stack<>();
        Queue<Cell> fringe = new LinkedList<>();

        //start with edges
        for (int x = 0; x < cellWidth; x++) {
            for (int y = 0; y < cellHeight; y++) {
                Cell i = cells[x][y];

                if (    i.E  == null || i.W  == null ||
                        i.NE == null || i.NW == null ||
                        i.SE == null || i.SW == null) {
                    //cell is on the edge
                    i.distFromEdge = 0;

                    fringe.add(i);

                } else {
                    i.distFromEdge = Integer.MAX_VALUE;
                }
            }
        }

        while (!fringe.isEmpty()) {
            Cell c = fringe.peek();

            //search neighbors
            for (int i = 0; i < 6; i++) {
                Cell n = null;
                switch (i) {
                    case 0: n = c.E; break;
                    case 1: n = c.SE; break;
                    case 2: n = c.SW; break;
                    case 3: n = c.W; break;
                    case 4: n = c.NW; break;
                    case 5: n = c.NE; break;
                }

                if (n != null && !n.isWall() && !visited.contains(n)) {

                    //check if neighbor is in fringe first
                    if (fringe.contains(n)) {

                        n.distFromEdge = Math.min(n.distFromEdge, c.distFromEdge + 1);

                    }
                    //else it's a new node add to fringe
                    else {

                        n.distFromEdge = c.distFromEdge + 1;
                        fringe.add(n);

                    }

                }
            }

            //all neighbors evaluated, add to visited, remove from fringe
            visited.push(fringe.remove());
        }

        //testing
        /*
        for (int y = 0; y < cellHeight; y++) {
            StringBuilder debug = new StringBuilder();

            if (y % 2 == 1)
                debug.append(" ");

            for (int x = 0; x < cellWidth; x++) {
                if (getCell(x,y).isEmpty())
                    debug.append(getCell(x,y).distFromEdge + " ");
                else if (getCell(x,y).isWall())
                    debug.append("W ");
                else if (getCell(x,y).isMouse())
                    debug.append("M ");
            }
            Log.d("HEXGRID", debug.toString());
        }
        */
    }
}

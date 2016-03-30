package charles.mobiledevfinalproject;

import android.util.Log;

// this is the data structure that will manage all information about the game
// the hex cells, where the mouse is at, moving the mouse, etc

// TODO implement mouse movement

public class HexGrid {
    static final int cellWidth = 9;
    static final int cellHeight = 9;
    static final int startingWalls = 7;

    private class Cell {
        static final int EMPTY = 0;
        static final int WALL = 1;
        static final int MOUSE = 2;

        Cell (int X, int Y) {
            x = X;
            y = Y;
        }

        //position
        int x = 0;
        int y = 0;

        //contents of this cell
        int contains = EMPTY;

        //references to adjacent cells
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

                // 0. (0 )(1 )(2 )(3 )
                // 1.   (0 )(1 )(2 )(3 )
                // 2. (0 )(1 )(2 )(3 )
                // 4.   (0 )(1 )(2 )(3 )

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
                if (y % 2 == 0 ) {
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
    }

    // set up a new game
    public void initialize() {
        // set up game

        // randomly place mouse in/next to center

        // randomly place wall on board

        // reset moveCounter
        moveCounter = 0;
    }

    // return true if wall is successfully placed
    // also increments players move counter
    public boolean placeWall(int x, int y) {

        if (cells[x][y].contains == Cell.EMPTY) {
            cells[x][y].contains = Cell.WALL;
            moveCounter++;
            return true;
        }
        return false;
    }

    // if this returns false, the mouse cannot be moved, player wins
    public boolean moveMouse () {

        // try to find shortest path out of grid

        // if two or more cell are equidistance from out of grid, pick one at random

        // if mouse is trapped, move to random empty cell

        // if the mouse is successfully moved return true

        return false;
    }

    public Cell getCell (int x, int y) {
        try {
            return cells[x][y];
        } catch (IndexOutOfBoundsException e) {
            Log.d("HEXGRID: ", e.getLocalizedMessage());
        }
        return null;
    }

    public Cell getMouseCell() {
        return mouseCell;
    }

    public int getMoveCounter() {
        return moveCounter;
    }

}

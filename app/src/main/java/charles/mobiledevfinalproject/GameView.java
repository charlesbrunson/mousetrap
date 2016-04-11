package charles.mobiledevfinalproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

// this is a custom view object where the actual game will be
// should manage drawing the grid and animating the mouse

public class GameView extends View {

    private HexGrid grid;
    private Paint p;

    //used for drawing cells
    private float cellSize;
    private float cellRadius;
    private float cellDrawRadius;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        grid = new HexGrid();
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.FILL);

    }

    public boolean onTouchEvent(MotionEvent event) {

        boolean eventHandled = false;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            //try and map it to a cell
            float x = event.getX();
            float y = event.getY();

            int cellY = (int)(y / cellSize) + 1;

            x -= (cellY % 2 == 0 ? 0 : cellRadius);
            int cellX = (int)(x / cellSize) + 1;

            if (x >= 0 && cellX > 0 && cellX < HexGrid.cellWidth &&
                    cellY > 0 && cellY < HexGrid.cellHeight) {


                if (grid.placeWall(cellX, cellY)) {
                    HexGrid.Cell oldMousePos = grid.getMouseCell();
                    HexGrid.Cell newMousePos = grid.moveMouse();

                    if (oldMousePos == newMousePos) {
                        //mouse wasn't able to move, player won
                        Toast.makeText(getContext(), "You Win!", Toast.LENGTH_LONG).show();
                        //reset grid
                        grid.initialize();

                    } else if (newMousePos.isEdge()) {
                        //mouse escaped to edge, player lost
                        Toast.makeText(getContext(), "You Lose!", Toast.LENGTH_LONG).show();
                        //reset grid
                        grid.initialize();

                    }

                    eventHandled = true;
                    //redraw view
                    invalidate();
                }
            }
        }

        return eventHandled;

    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        cellSize = Math.min(getWidth(), getHeight()) * 2 /
                (Math.max(HexGrid.cellWidth - 2, HexGrid.cellHeight - 2) * 2 + 1);
        cellRadius = cellSize / 2;
        cellDrawRadius = cellRadius * 0.9f;

        for (int y = 1; y < HexGrid.cellHeight - 1; y++) {
            for (int x = 1; x < HexGrid.cellWidth - 1; x++) {

                float xOffset = (y % 2 == 0 ? 0 : cellRadius);

                HexGrid.Cell c = grid.getCell(x, y);

                if (c.isEmpty()) {
                    p.setColor(Color.LTGRAY);
                }
                else if (c.isWall()){
                    p.setColor(Color.argb(255, 255, 127, 0));
                }
                else if (c.isMouse()){
                    p.setColor(Color.BLUE);
                }

                //draw each cell
                canvas.drawCircle(
                        (x - 1) * cellSize + cellRadius + xOffset,
                        (y - 1) * cellSize + cellRadius,
                        cellDrawRadius, p);

            }
        }
    }
}

package charles.mobiledevfinalproject;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

// this is a custom view object where the actual game will be
// should manage drawing the grid and animating the mouse

public class GameView extends View {

    HexGrid grid;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        grid = new HexGrid();
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean eventHandled = false;

        // TODO
        // handle event
        // if touchevent touches a valid, empty cell on grid,
        // fill cell and use algorithm to move mouse to new cell
        // also call invalidate(), to redraw view

        return eventHandled;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO
        //draw hex grid

        //draw mouse image, animation can come later

    }

}

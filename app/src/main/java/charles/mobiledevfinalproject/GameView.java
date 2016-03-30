package charles.mobiledevfinalproject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

// this is a custom view object where the actual game will be
// should manage drawing the grid and animating the mouse

//TODO GameView drawing grid
//TODO GameView responding to touchEvents

public class GameView extends View {

    HexGrid grid;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        grid = new HexGrid();
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean eventHandled = false;

        //handle event
        //if touchevent touches a valid, empty cell on grid,
        //fill cell and use algorithm to move mouse to new cell
        //also call invalidate(), to redraw view

        return eventHandled;
    }

}

package charles.mobiledevfinalproject;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

// this is a custom view object where the actual game will be
// should manage drawing the grid and animating the mouse

public class GameView extends View implements Animator.AnimatorListener, ValueAnimator.AnimatorUpdateListener {

    private HexGrid grid;
    private Paint p;

    private boolean touchable = true;

    //animation
    private ValueAnimator anim;
    private AnimatorSet animator;
    private float mouseAlphaFraction = 1.f;

    private Vector mouseRealPos;
    private Vector mouseInterpolatedPos;
    private Vector mouseReadOldPos;

    //cell references
    private HexGrid.Cell oldMousePos;
    private HexGrid.Cell newMousePos;

    //used for drawing cells
    private float cellSize;
    private float cellRadius;
    private float cellDrawRadius;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        grid = new HexGrid();
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setStyle(Paint.Style.FILL);

        anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(333);
        anim.addUpdateListener(this);

        animator = new AnimatorSet();
        animator.play(anim);
        animator.addListener(this);
    }

    public boolean onTouchEvent(MotionEvent event) {

        boolean eventHandled = false;

        if (event.getAction() == MotionEvent.ACTION_DOWN && touchable) {

            //try and map touch input to a cell
            float x = event.getX();
            float y = event.getY();

            int cellY = (int)(y / cellSize) + 1;

            x -= (cellY % 2 == 1 ? 0 : cellRadius);
            int cellX = (int)(x / cellSize) + 1;

            if (x >= 0 && cellX > 0 && cellX < HexGrid.cellWidth &&
                    cellY > 0 && cellY < HexGrid.cellHeight) {


                if (grid.placeWall(cellX, cellY)) {

                    //update cell references
                    oldMousePos = grid.getMouseCell();
                    newMousePos = grid.moveMouse();

                    //start mouse animation
                    animator.start();

                    eventHandled = true;
                }
            }
        }

        return eventHandled;

    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //draw cells
        for (int y = 1; y < HexGrid.cellHeight - 1; y++) {
            for (int x = 1; x < HexGrid.cellWidth - 1; x++) {

                HexGrid.Cell c = grid.getCell(x, y);

                if (c.isWall()){
                    p.setColor(Color.argb(255, 255, 127, 0));
                }
                else {
                    p.setColor(Color.LTGRAY);
                }

                Vector v = getRealPos(x,y);

                //draw each cell
                canvas.drawCircle(v.x, v.y, cellDrawRadius, p);

            }
        }

        //draw mouse
        p.setColor(Color.BLUE);
        p.setAlpha((int)(255 * mouseAlphaFraction));
        canvas.drawCircle(mouseInterpolatedPos.x, mouseInterpolatedPos.y, cellDrawRadius, p);
        p.setAlpha(255);

    }

    //calculate cell dimensions on screen when ever size changes
    protected void onSizeChanged (int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        cellSize = Math.min(getWidth(), getHeight()) * 2 /
                (Math.max(HexGrid.cellWidth - 2, HexGrid.cellHeight - 2) * 2 + 1);
        cellRadius = cellSize / 2;
        cellDrawRadius = cellRadius * 0.9f;


        mouseRealPos = getRealPos(grid.getMouseCell().getX(), grid.getMouseCell().getY());
        mouseReadOldPos = mouseRealPos;
        mouseInterpolatedPos = mouseReadOldPos;

        //redraw view
        invalidate();

    }

    private class Vector {
        float x;
        float y;
    }

    //get position of cell center position on screen
    protected Vector getRealPos(int cellX, int cellY) {

        float xOffset = (cellY % 2 == 1 ? 0 : cellRadius);
        Vector v = new Vector();
        v.x = (cellX - 1) * cellSize + cellRadius + xOffset;
        v.y = (cellY - 1) * cellSize + cellRadius;

        return v;
    }

    private void reset() {

        //reset grid
        grid.initialize();

        //reset animation values
        mouseAlphaFraction = 1.f;
        mouseReadOldPos = getRealPos(grid.getMouseCell().getX(), grid.getMouseCell().getY());
        mouseRealPos = mouseReadOldPos;
        mouseInterpolatedPos = mouseReadOldPos;

    }


    //value animation listener methods
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {

        if (animation == anim) {

            float f = animation.getAnimatedFraction();

            mouseInterpolatedPos.x = mouseReadOldPos.x + f * (mouseRealPos.x - mouseReadOldPos.x);
            mouseInterpolatedPos.y = mouseReadOldPos.y + f * (mouseRealPos.y - mouseReadOldPos.y);

            if (newMousePos.isEdge()) {
                mouseAlphaFraction = 1.f - f;
            }
            else {
                mouseAlphaFraction = 1.f;
            }

            invalidate();
        }

    }

    //animationset listener methods
    @Override
    public void onAnimationStart(Animator animation) {

        mouseAlphaFraction = 1.f;
        mouseReadOldPos = mouseRealPos;
        mouseRealPos = getRealPos(grid.getMouseCell().getX(), grid.getMouseCell().getY());

        //stop user input until animation finishes
        touchable = false;
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        //review game state, re-enable input
        if (oldMousePos == newMousePos) {

            //mouse wasn't able to move, player won
            Toast.makeText(getContext(), "You Win!", Toast.LENGTH_LONG).show();

            reset();

        } else if (newMousePos.isEdge()) {

            //mouse escaped to edge, player lost
            Toast.makeText(getContext(), "You Lose!", Toast.LENGTH_LONG).show();

            reset();
        }

        touchable = true;
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

}

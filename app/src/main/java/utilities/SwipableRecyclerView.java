package utilities;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import inksell.inksell.R;

/**
 * Created by Abhinav on 11/08/15.
 */
public class SwipableRecyclerView{

    public static void setSwipeBehaviour(RecyclerView rv, final OnSwipeListener swipeListener) {
        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.RIGHT, ItemTouchHelper.RIGHT)
        {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                swipeListener.onSwiped(viewHolder.getAdapterPosition());
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Get RecyclerView item from the ViewHolder
                    View itemView = viewHolder.itemView;

                    Paint p = new Paint();
                    if (dX > 0) {
            /* Set your color for positive displacement */

                        int myColor =
                                ConfigurationManager.CurrentActivityContext.getResources().getColor(R.color.RedDark);
                        p.setColor(myColor);

                        // Draw Rect with varying right side, equal to displacement dX
                        c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                                (float) itemView.getBottom(), p);

                        Drawable d = ContextCompat.getDrawable(ConfigurationManager.CurrentActivityContext, R.drawable.ic_action_delete);

                        int leftBound = itemView.getLeft()+50;
                        int mindX =  Math.min(leftBound + 70, (int) dX);
                        int topBottomMargins = (itemView.getHeight()-70)/2;
                        d.setBounds(leftBound, itemView.getTop()+topBottomMargins, mindX, itemView.getBottom()-topBottomMargins);
                        d.draw(c);


                    }

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        });

        swipeToDismissTouchHelper.attachToRecyclerView(rv);
    }

    public interface OnSwipeListener {
        // TODO: Update argument type and name
        public void onSwiped(int position);
    }

}

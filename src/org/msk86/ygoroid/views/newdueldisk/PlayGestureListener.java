package org.msk86.ygoroid.views.newdueldisk;

import android.view.GestureDetector;
import android.view.MotionEvent;
import org.msk86.ygoroid.newaction.Action;
import org.msk86.ygoroid.newaction.dueldisk.dispatcherimpl.*;
import org.msk86.ygoroid.newop.impl.*;

import java.util.List;

public class PlayGestureListener extends GestureDetector.SimpleOnGestureListener {
    DuelDiskView view;

    public PlayGestureListener(DuelDiskView view) {
        this.view = view;
    }

    public void onUp(MotionEvent event) {
        Drag drag = view.getDuel().getDrag();
        if (drag != null) {
            view.getDuel().setDrag(null);
            drag.dropTo(event.getX(), event.getY());
            List<Action> actionChain = new DragDispatcher().dispatch(drag);
            for(Action action : actionChain) {
                action.execute();
            }
        }
        view.updateActionTime();
    }

    @Override
    public boolean onDown(MotionEvent event) {
        Click click = new Click(view.getDuel(), event.getX(), event.getY());
        List<Action> actions = new ClickDispatcher().dispatch(click);
        for(Action action : actions) {
            action.execute();
        }
        view.updateActionTime();
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        ClickConfirmed clickConfirmed = new ClickConfirmed(view.getDuel(), event.getX(), event.getY());
        List<Action> actions = new ClickConfirmedDispatcher().dispatch(clickConfirmed);
        for(Action action : actions) {
            action.execute();
        }
        view.updateActionTime();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        Press press = new Press(view.getDuel(), event.getX(), event.getY());
        List<Action> actionChain = new PressDispatcher().dispatch(press);
        for(Action action : actionChain) {
            action.execute();
        }
        view.updateActionTime();
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        DoubleClick dblClick = new DoubleClick(view.getDuel(), event.getX(), event.getY());
        List<Action> actionChain = new DoubleClickDispatcher().dispatch(dblClick);
        for(Action action : actionChain) {
            action.execute();
        }
        view.updateActionTime();
        return super.onDoubleTap(event);
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float dx, float dy) {
        Drag drag = view.getDuel().getDrag();
        if (drag == null) {
            StartDrag startDrag = new StartDrag(view.getDuel(), event1.getX(), event1.getY());
            List<Action> actionChain = new StartDragDispatcher().dispatch(startDrag);
            for(Action action: actionChain) {
                action.execute();
            }
            drag = new Drag(view.getDuel(), event1.getX(), event1.getY(), startDrag);
            view.getDuel().setDrag(drag);
        } else {
            drag.move(event2.getX(), event2.getY());
        }
        view.updateActionTime();
        return true;
    }
}

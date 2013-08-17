package android.ygo.views.deckbuilder;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.ygo.core.Card;
import android.ygo.layout.Layout;

public class DeckGestureListener extends GestureDetector.SimpleOnGestureListener {
    DeckBuilderView view;

    public DeckGestureListener(DeckBuilderView view) {
        this.view = view;
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (view.getCardWindow() == null) {
            if (view.isInInfo(x, y)) {
                Card card = (Card) view.getInfoWindow().getInfoItem();
                if (card != null) {
                    view.showCard(card);
                }
            } else if(view.isInDeckBuilder(x, y)) {
                if (view.getDeckBuilder().isInMain(x, y) || view.getDeckBuilder().isInEx(x, y)) {
                    view.getDeckBuilder().setIsMain(true);
                } else if (view.getDeckBuilder().isInSide(x, y)) {
                    view.getDeckBuilder().setIsMain(false);
                }
                Card card = view.getDeckBuilder().cardAt(x, y);

                view.select(card);
            }
        }
        view.updateActionTime();
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (view.getCardWindow() == null) {
            Card card = view.getDeckBuilder().cardAt(x, y);
            Layout layout = view.getDeckBuilder().layoutAt(x, y);
            if (layout != null && card != null) {
                layout.cards().remove(card);
            }
        }
        view.updateActionTime();
        return super.onDoubleTap(event);
    }
}

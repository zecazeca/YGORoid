package android.ygo.core;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.ygo.utils.Configuration;
import android.ygo.utils.Utils;

public class ShowCardWindow implements Item, Drawable {
    Card card;

    public ShowCardWindow(Card card) {
        this.card = card;
    }

    @Override
    public void draw(Canvas canvas, int x, int y) {
        drawBackground(canvas, x, y);
        drawText(canvas, x, y);
        drawBigPic(canvas, x, y);
    }

    private void drawBigPic(Canvas canvas, int x, int y) {
        Utils.DrawHelper helper = new Utils.DrawHelper(x, y);
        helper.drawBitmap(canvas, card.bmpCache.get(Utils.cardScreenWidth(), Utils.cardScreenHeight()), 0, 0, new Paint());
    }

    private void drawText(Canvas canvas, int x, int y) {
        Utils.DrawHelper helper = new Utils.DrawHelper(x, y);

        int cardWidth = Utils.bigCardWidth();
        int cardHeight = Utils.bigCardHeight();

        int fontSize = cardHeight / 25;
        Paint paint = new Paint();
        paint.setColor(Configuration.fontColor());
        paint.setTextSize(fontSize);
        paint.setAntiAlias(true);

        int top = fontSize * 3 / 2;
        int left = cardWidth + fontSize / 2;
        int lineHeight = fontSize * 3 / 2;

        helper.drawText(canvas, card.getName(), left, top, paint);
        top += lineHeight;
        helper.drawText(canvas, card.cardTypeDesc() + " " + card.attrAndRaceDesc(),
                left, top, paint);
        if (card.type == CardType.MONSTER) {
            top += lineHeight;
            helper.drawText(canvas, card.levelAndADDesc(), left, top, paint);
        }
        top += lineHeight;
        TextPaint textPaint = new TextPaint(paint);
        StaticLayout layout = new StaticLayout(card.desc, textPaint, width() - cardWidth - fontSize / 2, Layout.Alignment.ALIGN_NORMAL, 1, 0, true);
        helper.drawLayout(canvas, layout, left, top);
    }

    public void drawBackground(Canvas canvas, int x, int y) {
        Utils.DrawHelper helper = new Utils.DrawHelper(x, y);
        Paint paint = new Paint();
        paint.setColor(Configuration.windowBackgroundColor());
        paint.setAlpha(150);
        helper.drawRect(canvas, new Rect(0, 0, width(), height()), paint);
    }

    @Override
    public int width() {
        return Utils.unitLength() * 6;
    }

    @Override
    public int height() {
        return Utils.screenHeight();
    }
}

package android.ygo.core;

import android.graphics.Canvas;
import android.widget.Toast;
import android.ygo.core.tool.Coin;
import android.ygo.core.tool.Dice;
import android.ygo.op.Drag;
import android.ygo.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Duel implements Item, Drawable {
    private LifePoint lifePoint;

    private DuelFields duelFields;

    private HandCards handCards;

    private CardSelector cardSelector;

    private InfoWindow window;

    private SelectableItem currentSelectItem;

    private Drag drag;
    private List<Card> mainDeckCards;
    private List<Card> exDeckCards;
    private Item currentSelectItemContainer;

    private Dice dice;
    private Coin coin;

    public Duel() {
       start(null ,null);
    }

    public void start(List<Card> mainDeckCards, List<Card> exDeckCards) {
        if (mainDeckCards == null) {
            mainDeckCards = new ArrayList<Card>();
        }
        if (exDeckCards == null) {
            exDeckCards = new ArrayList<Card>();
        }

        if(!deckCheck(mainDeckCards, exDeckCards)) {
            return;
        }

        initDuelField();

        this.mainDeckCards = mainDeckCards;
        this.exDeckCards = exDeckCards;

        initDeck();
        Deck deck = (Deck) duelFields.getDeckField().getItem();
        deck.shuffle();
        initHandCards();
    }

    public void restart() {
        start(this.mainDeckCards, this.exDeckCards);
    }

    public boolean deckCheck(List<Card> mainDeckCards, List<Card> exDeckCards) {
        if (!deckCardCountCheck(mainDeckCards, exDeckCards)) return false;
        if (!singleCardCountCheck(mainDeckCards, exDeckCards)) return false;
        return true;

    }

    private boolean deckCardCountCheck(List<Card> mainDeckCards, List<Card> exDeckCards) {
        if(mainDeckCards.size() == 0 && exDeckCards.size() == 0) {
            return true;
        }
        String info = null;
        if(mainDeckCards.size() < 40 || mainDeckCards.size() > 60) {
            info = "主卡组卡片数量不符合要求";
        } else if(exDeckCards.size() > 15) {
            info = "额外卡组卡片数量不符合要求";
        }
        if(info != null) {
            Toast.makeText(Utils.getContext(), info, Toast.LENGTH_LONG).show();
            window.setInfo(info);
            return false;
        }
        return true;
    }

    private boolean singleCardCountCheck(List<Card> mainDeckCards, List<Card> exDeckCards) {
        List<Integer> invalidCardIds = new ArrayList<Integer>();
        List<Card> allCards = new ArrayList<Card>();
        allCards.addAll(mainDeckCards);
        allCards.addAll(exDeckCards);
        Map<String, Integer> cardCount = new TreeMap<String, Integer>();
        for (Card card : allCards) {
            if (!cardCount.containsKey(card.getRealId())) {
                cardCount.put(card.getRealId(), 0);
            }
            cardCount.put(card.getRealId(), cardCount.get(card.getRealId()) + 1);
        }

        for(Map.Entry<String, Integer> entry : cardCount.entrySet()) {
            if(entry.getValue() > 3) {
                invalidCardIds.add(Integer.parseInt(entry.getKey()));
            }
        }

        if(invalidCardIds.size() == 0) {
            return true;
        }

        List<String> invalidCardNames = Utils.getDbHelper().loadNamesByIds(invalidCardIds);
        String info = "卡片" + invalidCardNames.toString() + "数量不符合要求";
        Toast.makeText(Utils.getContext(), info, Toast.LENGTH_LONG).show();
        window.setInfo(info);
        return false;
    }

    public void initDuelField() {
        lifePoint = new LifePoint();

        duelFields = new DuelFields();
        Deck deck = new Deck("DECK");
        Deck exDeck = new Deck("EX");
        CardList graveyard = new CardList("GRAVEYARD");
        CardList removed = new CardList("REMOVED");
        CardList temp = new CardList("TEMPORARY");
        duelFields.getDeckField().setItem(deck);
        duelFields.getExDeckField().setItem(exDeck);
        duelFields.getGraveyardField().setItem(graveyard);
        duelFields.getRemovedField().setItem(removed);
        duelFields.getTempField().setItem(temp);

        handCards = new HandCards();

        window = new InfoWindow();

        dice = new Dice();
        coin = new Coin();
    }

    private void initDeck() {
        Deck deck = (Deck) duelFields.getDeckField().getItem();
        deck.push(mainDeckCards);

        Deck exDeck = (Deck) duelFields.getExDeckField().getItem();
        exDeck.push(exDeckCards);
    }

    private void initHandCards() {
        Deck deck = (Deck) duelFields.getDeckField().getItem();
        handCards.add(deck.pop(5));
    }

    public LifePoint getLifePoint() {
        return lifePoint;
    }

    public CardSelector getCardSelector() {
        return cardSelector;
    }

    public void setCardSelector(CardSelector cardSelector) {
        this.cardSelector = cardSelector;
    }

    public DuelFields getDuelFields() {
        return duelFields;
    }

    public HandCards getHandCards() {
        return handCards;
    }

    public InfoWindow getInfoWindow() {
        return window;
    }

    public void unSelect() {
        if (currentSelectItem != null) {
            currentSelectItem.unSelect();
            currentSelectItem = null;
            currentSelectItemContainer = null;
        }
        window.clearInfo();
    }

    public void select(SelectableItem item, Item container) {
        if (item != null) {
            if (item != currentSelectItem) {
                unSelect();
            }
            currentSelectItem = item;
            currentSelectItemContainer = container;
            currentSelectItem.select();
        } else {
            unSelect();
        }

        if (item != null) {
            window.setInfo(item);
        }
    }

    public SelectableItem itemAt(int x, int y) {
        if (inLifePoint(x, y)) {
            return lifePoint;
        } else if (inDice(x, y)) {
            return dice;
        } else if (inCoin(x, y)) {
            return coin;
        } else if (inDuelFields(x, y)) {
            return duelFields.itemOnFieldAt(x, y);
        } else if (inHand(x, y)) {
            return handCards.cardAt(x, y);
        } else if (inCardSelector(x, y)) {
            return cardSelector.cardAt(x, y);
        } else if (inInfo(x, y)) {
            return currentSelectItem;
        }
        return null;
    }

    public Item containerAt(int x, int y) {
        if (inLifePoint(x, y)) {
            return null;
        } else if (inDice(x, y)) {
            return null;
        } else if (inCoin(x, y)) {
            return null;
        } else if (inDuelFields(x, y)) {
            return duelFields.fieldAt(x, y);
        } else if (inHand(x, y)) {
            return handCards;
        } else if (inCardSelector(x, y)) {
            return cardSelector.cardList;
        } else if (inInfo(x, y)) {
            return window;
        }
        return null;
    }

    public Field fieldAt(int x, int y) {
        return duelFields.fieldAt(x, y);
    }

    public void setDrag(Drag drag) {
        this.drag = drag;
    }

    public Drag getDrag() {
        return drag;
    }

    public boolean inDice(int x, int y) {
        if (cardSelector != null) {
            return false;
        }
        if (x >= Utils.unitLength() * 2.4 && x < Utils.unitLength() * 2.9) {
            if (y < Utils.unitLength() / 2) {
                return true;
            }
        }
        return false;
    }

    public boolean inCoin(int x, int y) {
        if (cardSelector != null) {
            return false;
        }
        if (x >= Utils.unitLength() * 2.4 && x < Utils.unitLength() * 2.9) {
            if (y >= Utils.unitLength() / 2 && y < Utils.unitLength()) {
                return true;
            }
        }
        return false;
    }

    public boolean inLifePoint(int x, int y) {
        if (cardSelector != null) {
            return false;
        }
        if (x >= Utils.unitLength() && x < Utils.unitLength() * 2.2) {
            if (y < Utils.unitLength()) {
                return true;
            }
        }
        return false;
    }

    public boolean inCardSelector(int x, int y) {
        if (cardSelector == null) {
            return false;
        }
        if (y >= Utils.screenHeight() - Utils.cardHeight() / 6) {
            return false;
        }
        return true;
    }

    public boolean inDuelFields(int x, int y) {
        if (cardSelector != null) {
            return false;
        }
        return y < Utils.unitLength() * 3 && x < Utils.totalWidth();
    }

    public boolean inHand(int x, int y) {
        if (cardSelector != null) {
            return false;
        }
        return y >= Utils.unitLength() * 3 && y < Utils.screenHeight() - Utils.cardHeight() / 6;
    }

    public boolean inInfo(int x, int y) {
        return y >= Utils.screenHeight() - Utils.cardHeight() / 6;
    }

    @Override
    public int width() {
        return Utils.totalWidth();
    }

    @Override
    public int height() {
        return Utils.unitLength() * 4;
    }


    @Override
    public void draw(Canvas canvas, int x, int y) {
        Utils.DrawHelper helper = new Utils.DrawHelper(x, y);

        if (cardSelector == null) {
            helper.drawDrawable(canvas, lifePoint, Utils.unitLength(), (Utils.unitLength() - lifePoint.height()) / 2);
            helper.drawDrawable(canvas, dice, (int) (Utils.unitLength() * 2.4), 0);
            helper.drawDrawable(canvas, coin, (int) (Utils.unitLength() * 2.4), Utils.unitLength() / 2);
            helper.drawDrawable(canvas, duelFields, 0, 0);
            helper.drawDrawable(canvas, handCards, 0, duelFields.height() + 1);
        } else {
            helper.drawDrawable(canvas, cardSelector, 0, 0);
        }

        Drag dragged = drag;
        if (dragged != null && dragged.getItem() != null) {
            if (dragged.getItem() instanceof Drawable) {
                Drawable drawable = (Drawable) dragged.getItem();
                helper.drawDrawable(canvas, drawable, dragged.x() - drawable.width() / 2, dragged.y() - drawable.height() / 2);
            }
        }

        helper.drawDrawable(canvas, window, helper.center(width(), window.width()), helper.bottom(Utils.screenHeight(), window.height()));
    }

    public SelectableItem getCurrentSelectItem() {
        return currentSelectItem;
    }

    public Item getCurrentSelectItemContainer() {
        return currentSelectItemContainer;
    }

    public Dice getDice() {
        return dice;
    }

    public Coin getCoin() {
        return coin;
    }
}

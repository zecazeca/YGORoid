package org.msk86.ygoroid.views.deckbuilder.filter;

import org.msk86.ygoroid.core.Card;
import org.msk86.ygoroid.core.CardSubType;
import org.msk86.ygoroid.core.CardType;
import org.msk86.ygoroid.core.Race;

public class TypeFilter implements CardFilter {

    CardType type;
    CardSubType subType;

    public TypeFilter(CardType type, CardSubType subType) {
        this.type = type;
        this.subType = subType;
    }

    @Override
    public boolean accept(Card card) {
        return type == CardType.NULL || (card.getType() == type && (subType == CardSubType.NULL || card.getSubTypes().contains(subType)));
    }

    @Override
    public String where() {
        if(!isValid()) {
            return "";
        }
        String w = " AND d.type & " + type.getCode() + " = " + type.getCode();

        if(subType == CardSubType.NULL) {
            w += "";
        } else {
            w += " AND d.type & " + subType.getCode() + " = " + subType.getCode();
        }
        return w;
    }

    @Override
    public boolean isValid() {
        return type != CardType.NULL;
    }
}

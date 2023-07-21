package net.poppinger.legretto.server;

import java.util.ArrayList;
import java.util.List;

public class Application {

    public Table table;

    public void init(){
        table=new Table();

        for(int numPlayer=0;numPlayer<4;numPlayer++){
            fillSinglePlayer(numPlayer);
        }


    }

    private void fillSinglePlayer(int playerNum){
        // We have 160 cards
        // 40 for one person
/*
        4x red 1-10 :   0- 9
        4x gn 1-10  :  10-19
        4x bl 1-10  :  20-29
        4x ye 1-10  :  30-39

        For each player
        4 Slots
        1 Slot Array[10]
        1 Solt Array[40] Deck

        Target slot
        16x Array[10]
        */

        List<Integer> cards = new ArrayList<>();
        for(int x=0;x<40;x++){
            cards.add(x);
        }
        int pos=0;
        while (cards.size()>0){
            int card=cards.remove((int)(Math.random()*cards.size()));
            System.out.println(card);
            if (pos<4) {
                table.players[playerNum].slots[pos]=card;
            }
            else if (pos<14){
                table.players[playerNum].addCardToStackslot(card);
            }
            else{
                table.players[playerNum].addCardToDeckSlot(card);
            }
            pos++;
        }
    }

}

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

        for(int x=0;x<table.targets.length;x++){
            for(int y=0;y<table.targets[x].length;y++){
                table.targets[x][y]=-1;
            }
        }

        table.players[0].slots[0]=0;
        table.players[0].slots[1]=1;
        table.players[1].slots[2]=2;
        table.players[2].slots[3]=3;

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

    public int getCardValueFromCardInt(int card){
        return (card % 10) +1;
    }
    public int getCardColorFromCardInt(int card){
        return (card / 10);
    }

    public String ValidatePutCardCommand(PutCardCommand cmd){
        if (cmd.slot==null && !cmd.stackDeck.equals("S") && !cmd.stackDeck.equals("D")) return "Unknown source slot";
        if (cmd.player<0 || cmd.player>=table.players.length) return "No such player";
        if (cmd.target<0 || cmd.target>15) return "Illegal target";

        var player=table.players[cmd.player];
        // No Card in this slot

        int srcCard =-2;
        // Slot card is taken
        if (cmd.slot!=null) {
            if (cmd.slot < 0 || cmd.slot > 3) return "Illegal slot";
            if (player.slots[cmd.slot] < 0) return "No card in this slot";

            srcCard = player.slots[cmd.slot];

        }
        else {
            // Deck or Stack Card is taken
            if (cmd.stackDeck.equals("S")){
                // Stack card
                srcCard = player.getCardFromStackslot();
                if (srcCard==-1) return "No Cards in the stack left";
            }
            if (cmd.stackDeck.equals("D")){
                // Stack card
                srcCard = player.getCardFromDeckSlot();
                if (srcCard==-1) return "No Cards in the deck left";
            }
        }

        // check if the target is valid for this card
        if (table.targetsPointer[cmd.target]>=0){
            int currentTargetCard=table.targets[cmd.target][table.targetsPointer[cmd.target]];
            // Existing card must be one lower than the srcCard which wants to be put on top here
            if (getCardColorFromCardInt(currentTargetCard)!=getCardColorFromCardInt(srcCard)) return "Wrong color";
            if (getCardValueFromCardInt(currentTargetCard) + 1 != getCardValueFromCardInt(srcCard)) return "Incorrect number";
        }
        else {
            // First card in deck
            if (getCardValueFromCardInt(srcCard)!=1) return "First card in the target must be 1";
        }

        // Success up to here
        return null;

    }

    public String putCard(PutCardCommand cmd){
        var validationResult=ValidatePutCardCommand(cmd);
        if (validationResult==null){
            var player=table.players[cmd.player];
            int srcCard=-2;
            if (cmd.slot!=null) {
                // Slotcard was taken
                srcCard = player.slots[cmd.slot];
                player.slots[cmd.slot] = -1;
            }
            else if (cmd.stackDeck.equals("S")){
                //Stackcard was taken
                srcCard = player.popCardFromStackslot();
            }
            else {
                // Deckcard was taken
                srcCard = player.popCardFromDeckSlot();
            }
            if (table.targetsPointer[cmd.target]<0){
                table.targetsPointer[cmd.target]=-1;
            }
            table.targetsPointer[cmd.target]++;
                // First card in deck
            table.targets[cmd.target][table.targetsPointer[cmd.target]]=srcCard;
            return null;
        }
        return validationResult;
    }

}

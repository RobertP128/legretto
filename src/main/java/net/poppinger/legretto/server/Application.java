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

    public boolean ValidatePutCardCommand(PutCardCommand cmd){
        if (cmd.player<0 || cmd.player>=table.players.length) return false;
        if (cmd.slot<0 || cmd.slot>3) return false;
        if (cmd.target<0 || cmd.target>15) return false;

        var player=table.players[cmd.player];
        // No Card in this slot
        if (player.slots[cmd.slot]<0) return false;

        int srcCard=player.slots[cmd.slot];

        // check if the target is valid for this card
        if (table.targetsPointer[cmd.target]>=0){
            int currentTargetCard=table.targets[cmd.target][table.targetsPointer[cmd.target]];
            // Existing card must be one lower than the srcCard which wants to be put on top here
            if (getCardColorFromCardInt(currentTargetCard)!=getCardColorFromCardInt(srcCard)) return false;
            if (getCardValueFromCardInt(currentTargetCard) + 1 == getCardValueFromCardInt(srcCard)) return true;
        }
        else {
            // First card in deck
            if (getCardValueFromCardInt(srcCard)==1) return true;
        }

        return false;

    }

    public boolean putCard(PutCardCommand cmd){
        if (ValidatePutCardCommand(cmd)){
            var player=table.players[cmd.player];
            int srcCard=player.slots[cmd.slot];
            player.slots[cmd.slot]=-1;

            if (table.targetsPointer[cmd.target]<0){
                table.targetsPointer[cmd.target]=-1;
            }
            table.targetsPointer[cmd.target]++;
                // First card in deck
            table.targets[cmd.target][table.targetsPointer[cmd.target]]=srcCard;
            return true;
        }
        return false;
    }

}

package net.poppinger.legretto.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Application {

    public Table table;

    public void init(Table existingTable){
        table=(existingTable!=null) ? existingTable : new Table();

        for(int numPlayer=0;numPlayer<4;numPlayer++){
            fillSinglePlayer(numPlayer);
        }

        for(int x=0;x<table.targets.length;x++){
            for(int y=0;y<table.targets[x].length;y++){
                table.targets[x][y]=-1;
            }
        }

        //table.players[0].slots[0]=0;
        //table.players[0].slots[1]=1;
        //table.players[1].slots[2]=2;
        //table.players[2].slots[3]=3;


    }

    public boolean saintyCheck(){
        List<Integer> allCards=new ArrayList<>();
        // get for all players
        for (var player: table.players) {
            // get slots
            for (var slotCard: player.slots) {
                if (slotCard>=0) allCards.add(slotCard);
            }
            // get stack
            for (int x=0;x<=player.getStackSlotPointer();x++){
                var stackCard=player.getAllStackCards()[x];
                if (stackCard>=0) allCards.add(stackCard);
            }
            // get deck
            for (int x=0;x<=player.getDeckSlotPointer();x++){
                var stackCard=player.getAllDeckCards()[x];
                if (stackCard>=0) allCards.add(stackCard);
            }
        }

        // get all targetcards
        for (var target: table.targets) {
            for (var card: target) {
                if (card>=0) allCards.add(card);
            }
        }

        // Sort the array
        Collections.sort(allCards);

        if (allCards.size()!=160) return false;
        // check if we have consecutive numbers (4 times each)
        for(int x=0;x<allCards.size();x+=4){
            int cardValue=x/4;
            if (allCards.get(x)!=cardValue || allCards.get(x+1)!=cardValue || allCards.get(x+2)!=cardValue || allCards.get(x+3)!=cardValue){
                return false;
            }
        }


        return true;
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

    public String ValidatePutCardCommand(PutCardCommand cmd,Table table){
        if (cmd.slot==null && !cmd.stackDeck.equals("S") && !cmd.stackDeck.equals("D")) return "Unknown source slot";
        if (cmd.player<0 || cmd.player>=table.players.length) return "No such player";

        if (cmd.targetType.equals("T")) {
            if (cmd.target < 0 || cmd.target > 15) return "Illegal target";
        }
        else if (cmd.targetType.equals("P")){
            // Validate targetPlayer
            // Validate TargetSlot
            if (cmd.targetPlayer<0 || cmd.targetPlayer>3) return "Illegal Targetplayer";
            if (cmd.targetSlot<0 || cmd.targetSlot>3) return "Illegal targetSlot";

            if (cmd.player!=cmd.targetPlayer){
                return "You can only drop into your own area!";
            }

            // Only allowed if slot is empty
            if (table.players[cmd.targetPlayer].slots[cmd.targetSlot]!=-1) return "You can only drop it to an empty slot";

        }
        else{
            return "Illegal targetType";
        }

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

        if (cmd.targetType.equals("T")) {
            // check if the target is valid for this card
            if (table.targetsPointer[cmd.target] >= 0) {
                int currentTargetCard = table.targets[cmd.target][table.targetsPointer[cmd.target]];
                // Existing card must be one lower than the srcCard which wants to be put on top here
                if (getCardColorFromCardInt(currentTargetCard) != getCardColorFromCardInt(srcCard))
                    return "Wrong color";
                if (getCardValueFromCardInt(currentTargetCard) + 1 != getCardValueFromCardInt(srcCard))
                    return "Incorrect number";
            } else {
                // First card in deck
                if (getCardValueFromCardInt(srcCard) != 1) return "First card in the target must be 1";
            }
        }
        else if (cmd.targetType.equals("P")){

        }


        // Success up to here
        return null;

    }

    public String putCard(PutCardCommand cmd,int currentPlayerId,Table table){
        var validationResult=ValidatePutCardCommand(cmd,table);
        if (validationResult==null){
            if (currentPlayerId!=cmd.player){
                return "Not allowed. You are player#"+currentPlayerId;
            }
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

            if (cmd.targetType.equals("T")) {
                if (table.targetsPointer[cmd.target] < 0) {
                    table.targetsPointer[cmd.target] = -1;
                }
                table.targetsPointer[cmd.target]++;
                // First card in deck
                table.targets[cmd.target][table.targetsPointer[cmd.target]] = srcCard;
            }
            else if (cmd.targetType.equals("P")) {
                table.players[cmd.targetPlayer].slots[cmd.targetSlot]=srcCard;
            }
            return null;
        }
        return validationResult;
    }


    public String swapDeck(int player){
        // Validate Player
        if (player<0 || player>3) return "Illegal value";

        table.players[player].rotateDeck();


        return null;
    }

}

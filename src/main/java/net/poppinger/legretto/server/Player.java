package net.poppinger.legretto.server;

import java.util.List;

public class Player {
    public int slots[];

    private int stackSlot[];
    private int stackSlotPointer;
    private int deckSlot[];
    private int deckSlotPointer;


    public Player() {
        resetSlots();
    }

    public void resetSlots() {
        slots = new int[4];
        stackSlot = new int[10];
        for(int x=0;x<stackSlot.length;x++){
            stackSlot[x]=-1;
        }
        deckSlot = new int[40];
        for(int x=0;x<deckSlot.length;x++){
            deckSlot[x]=-1;
        }
        stackSlotPointer = -1;
        deckSlotPointer = -1;
    }

    /*
   returns boolean true....success
     */
    public boolean addCardToStackslot(int card) {
        if (stackSlotPointer < stackSlot.length - 1) {
            stackSlotPointer++;
            stackSlot[stackSlotPointer] = card;
            return true;
        }
        return false;
    }

    public int popCardFromStackslot() {
        if (stackSlotPointer >= 0) {
            int card = stackSlot[stackSlotPointer];
            stackSlot[stackSlotPointer]=-1;
            stackSlotPointer--;
            return card;
        }
        return -1;
    }

    public int[] getAllStackCards(){
        return stackSlot;
    }
    public int getStackSlotPointer(){
        return stackSlotPointer;
    }

    public int getCardFromStackslot() {
        if (stackSlotPointer >= 0) {
            int card = stackSlot[stackSlotPointer];
            return card;
        }
        return -1;
    }


    /*
returns boolean true....success
 */
    public boolean addCardToDeckSlot(int card) {
        if (deckSlotPointer < deckSlot.length - 1) {
            deckSlotPointer++;
            deckSlot[deckSlotPointer] = card;
            return true;
        }
        return false;
    }

    public int popCardFromDeckSlot() {
        if (deckSlotPointer >= 0) {
            int card = deckSlot[deckSlotPointer];
            deckSlot[deckSlotPointer]=-1;
            deckSlotPointer--;

            /*
            // optimize Deck
            for (int x=0;x<deckSlot.length-1;x++){
                if (deckSlot[x]<0){
                    // copy all higher elements to index one lower
                    for(int y=x;y<deckSlot.length-1;y++){
                        deckSlot[y]=deckSlot[y+1];
                    }
                }
            }
*/
            return card;
        }
        return -1;
    }

    public int getCardFromDeckSlot() {
        if (deckSlotPointer >= 0) {
            int card = deckSlot[deckSlotPointer];
            return card;
        }
        return -1;
    }

    public int[] getAllDeckCards(){
        return deckSlot;
    }
    public int getDeckSlotPointer(){
        return deckSlotPointer;
    }


    public void rotateDeck(){
        // Rotate the Deck
        // take the first one out
        // move other s 1 place forward
        // add the first at the end, done!

        if (deckSlotPointer > 0) {
            int card = deckSlot[0];
            for(int x=0;x<deckSlotPointer;x++){
                deckSlot[x]=deckSlot[x+1];
            }
            deckSlot[deckSlotPointer]=card;
        }
    }


}

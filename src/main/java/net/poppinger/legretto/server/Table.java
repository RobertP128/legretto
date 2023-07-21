package net.poppinger.legretto.server;

public class Table {
    public Player[] players;
    public int[][] targets;
    public int[] targetsPointer;

    public Table(){
        players=new Player[4];
        for (int x=0;x<4;x++){
            players[x]=new Player();
        }
        targets=new int[16][10];
        targetsPointer=new int[16];
        for(int x=0;x<targetsPointer.length;x++){
            targetsPointer[x]=-1;
        }
    }

}

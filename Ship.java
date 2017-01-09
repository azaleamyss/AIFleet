import java.util.ArrayList;
public class Ship{
    private int shipType;
    private int shipSize;
    private int shipDir;
    private int rest;
    private boolean issink;//轟沈
    private ArrayList<int[]> shipPos;

    Ship(int shipType, int shipDir, int shipSize){
        issink = false;
        rest = 0;
        this.shipType = shipType;
        this.shipSize = shipSize;
        this.shipDir = shipDir;
        shipPos = new ArrayList<int[]>();
    }

    Ship(int shipType, int shipSize){
        issink = false;
        rest = 0;
        this.shipType = shipType;
        this.shipSize = shipSize;
        shipPos = new ArrayList<int[]>();
    }

   public void setShipPiece(int x, int y){
       int[] pos = new int[2];
       pos[0] = x;
       pos[1] = y;
       shipPos.add(pos);
       rest++;
   } 

   public void removePos(int x, int y){
       int i = 0;
       int idx = 0;
       for(int[] pos: shipPos){ 
           if(pos[0] == x && pos[1] == y){
               idx = i;
               break;
           }
           i++;
       }
       shipPos.remove(idx);
       rest--;
       if(shipPos.size() == 0){
           issink = true;
       }
   }

   public int getShipSize(){
       return shipSize;
   }

   public boolean isSink(){
       return issink;
   }

   public int getShipDir(){
       return shipDir;
   }

   public void setShipDir(int dir){
       this.shipDir = dir;
   }

   public ArrayList<int[]> getShipPos(){
       return shipPos;
   }

   public int getShipType(){
       return shipType;
   }

   public int getRest(){
       return rest;
   }
}

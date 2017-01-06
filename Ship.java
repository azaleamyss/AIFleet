public class Ship{
    private int[][] shipPos;
    private int shipType;
    private int shipSize;
    private int shipDir;
    private int length;

    Ship(int shipType, int shipDir, int shipSize){
        length = 0;
        this.shipType = shipType;
        this.shipSize = shipSize;
        this.shipDir = shipDir;
        shipPos = new int[shipSize][2];
    }

   public void setShipPiece(int x, int y){
       shipPos[length][0] = x;
       shipPos[length][1] = y;
       length++;
   } 

   public int getShipSize(){
       return shipSize;
   }

   public int getShipDir(){
       return shipDir;
   }

   public int[][] getShipPos(){
       return shipPos;
   }

   public int getShipLength(){
       return length;
   }

   public int getShipType(){
       return shipType;
   }
}

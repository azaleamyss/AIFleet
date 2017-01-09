import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
public class MarineArea{
    public static final int VARTICAL = 1;
    public static final int HORIZONTAL = 2;
    private static final int NEAR_ARROWABLE = 5; //他船の近くに設置を許容する確率 
    private static final int EDGE_ARROWABLE = 15; //端に設置を許容する確率
    private int areaWidth;
    private int areaHeight;

    private static int[] shipSize;
    private static int[] log;//一度置けるかどうか計算した座標のログ(あまり意味がない気がする)

    public static int[][] myArea; // 2:船の位置 / 1:ダメージを受けた箇所 / 0:何もない 
    public static int[][] enemyArea; //1:船のダメージを受けた箇所 / 0: 何もない / -1: 未知

    public static ArrayList<Ship> ownFleet; //自分の艦隊
    public static ArrayList<Ship> enemyFleet; //敵残存艦隊
    public Ship attackedShip;
    //艦娘のタイプ
    public static final int DESTROYER = 0;//駆逐
    public static final int LIGHT_CRUISER = 1;//軽巡
    public static final int HEAVY_CRUISER = 2;//重巡
    public static final int BATTLE_SHIP = 3;//戦艦
    public static final int AIR_CARRIER = 4;//空母

    private boolean existShipEdge = false;

    MarineArea(int areaWidth, int areaHeight){
        this.areaWidth = areaWidth;
        this.areaHeight = areaHeight;

        myArea = new int[areaHeight][areaWidth];
        enemyArea = new int[areaHeight][areaWidth];
        fill(myArea,0);
        fill(enemyArea,-1);

        log = new int[areaHeight*areaWidth];

        shipSize = new int[5];
        shipSize[DESTROYER] = 2;
        shipSize[LIGHT_CRUISER] = 3;
        shipSize[HEAVY_CRUISER] = 3;
        shipSize[BATTLE_SHIP] = 4;
        shipSize[AIR_CARRIER] = 5;

        ownFleet = new ArrayList<Ship>();
        enemyFleet = new ArrayList<Ship>();
        initEnemyFleet();

        setMyArea();

        printArea(getMyArea());
    }

    //敵残存艦船リスト生成
    private void initEnemyFleet(){
        for(int shipType = 0;shipType < 5;shipType++){
            Ship aShip = new Ship(shipType,shipSize[shipType]);
            enemyFleet.add(aShip);
        }
    }

    public void fill(int[][] area, int value){
        for(int i = 0;i < getHeight();i++){
            for(int j = 0;j < getWidth();j++){
                area[i][j] = value;
            }
        }
    }

    public int[][] getMyArea(){
        return myArea;
    }

    public int[][] getEnemyArea(){
        return enemyArea;
    }

    public int getHeight(){
        return areaHeight;
    }

    public int getWidth(){
        return areaWidth;
    }

    public boolean isAttacked(int x, int y){
        if(myArea[y][x] == 1){
            return true;
        }else{
            return false;
        }
    }

    public void setAttackedShip(Ship theShip){
        this.attackedShip = theShip;
    }

    public Ship getAttackedShip(){
        return attackedShip;
    }

    public void updateArea(int[][] area, int x, int y, int value){
        area[y][x] = value;
    }

    public void printArea(int[][] area){
        for(int i = 0;i < 10;i++){
            for(int j = 0;j < 10;j++){
                System.out.printf("%2d ",area[i][j]);
            }
            System.out.print("\n");
        }
    }

    private void setMyArea(){
        setShipEdge(DESTROYER,LIGHT_CRUISER);
        setShip(AIR_CARRIER);
        setShip(BATTLE_SHIP);
        setShip(HEAVY_CRUISER);
    }

    private void setShip(int shipType){
        int[] initShipData;
        int posX,posY;
        int shipDir;
        Ship aShip;
        
        initShipData = getInitShipData(shipType);
        posX = initShipData[0];
        posY = initShipData[1];
        shipDir = initShipData[2];

        aShip = new Ship(shipType,shipDir,shipSize[shipType]);
        aShip.setShipPiece(posX,posY);
        ownFleet.add(aShip);

        myArea[posY][posX] = 2;

        setShipData(posX,posY,aShip);
        System.out.println("The ship have been set.");
    }

    //端に置く
    private void setShipEdge(int shipType1, int shipType2){
        Ship shipA = null;
        Ship shipB = null;
        int[][] edgePos = {{0,0},{9,0},{0,9},{9,9}};
        int randIdx = (int)(Math.random()*4);
        int randDir = (int)(Math.random()*2)+1;

        int[] cruiserPos = edgePos[randIdx];

        if(shipSize[shipType1] < shipSize[shipType2]){
            shipA = new Ship(shipType2,randDir,shipSize[shipType2]);
        }else if(shipSize[shipType2] < shipSize[shipType1]){
            shipA = new Ship(shipType1,randDir,shipSize[shipType1]); 
        }

        switch(randIdx){
            case 0:
                for(int i = 0;i < shipA.getShipSize();i++){
                    if(shipA.getShipDir() == HORIZONTAL){
                        shipA.setShipPiece(i,0);
                    }else{
                        shipA.setShipPiece(0,i);
                    }
                }

                if(shipA.getShipDir() == HORIZONTAL){
                    if(shipA.getShipType() == shipType2){
                        shipB = new Ship(shipType1,VARTICAL,shipSize[shipType1]);
                    }else{
                        shipB = new Ship(shipType2,VARTICAL,shipSize[shipType2]);
                    }
                    for(int i = 0;i < shipB.getShipSize();i++){
                        shipB.setShipPiece(0,i+1);
                    }
                }else{
                    if(shipA.getShipType() == shipType2){
                        shipB = new Ship(shipType1,HORIZONTAL,shipSize[shipType1]);
                    }else{
                        shipB = new Ship(shipType2,HORIZONTAL,shipSize[shipType2]);
                    }
                    for(int i = 0;i < shipB.getShipSize();i++){
                        shipB.setShipPiece(i+1,0);
                    }
                }
                break;

            case 1:
                for(int i = 0;i < shipA.getShipSize();i++){
                    if(shipA.getShipDir() == HORIZONTAL){
                        shipA.setShipPiece(9-i,0);
                    }else{
                        shipA.setShipPiece(9,i);
                    }
                }

                if(shipA.getShipDir() == HORIZONTAL){
                    if(shipA.getShipType() == shipType2){
                        shipB = new Ship(shipType1,VARTICAL,shipSize[shipType1]);
                    }else{
                        shipB = new Ship(shipType2,VARTICAL,shipSize[shipType2]);
                    }
                    for(int i = 0;i < shipB.getShipSize();i++){
                        shipB.setShipPiece(9,i+1);
                    }
                }else{
                    if(shipA.getShipType() == shipType2){
                        shipB = new Ship(shipType1,HORIZONTAL,shipSize[shipType1]);
                    }else{
                        shipB = new Ship(shipType2,HORIZONTAL,shipSize[shipType2]);
                    }
                    for(int i = 0;i < shipB.getShipSize();i++){
                        shipB.setShipPiece(9-i-1,0);
                    }
                }

                break;

            case 2:
                for(int i = 0;i < shipA.getShipSize();i++){
                    if(shipA.getShipDir() == HORIZONTAL){
                        shipA.setShipPiece(i,9);
                    }else{
                        shipA.setShipPiece(0,9-i);
                    }
                }

                if(shipA.getShipDir() == HORIZONTAL){
                    if(shipA.getShipType() == shipType2){
                        shipB = new Ship(shipType1,VARTICAL,shipSize[shipType1]);
                    }else{
                        shipB = new Ship(shipType2,VARTICAL,shipSize[shipType2]);
                    }
                    for(int i = 0;i < shipB.getShipSize();i++){
                        shipB.setShipPiece(0,9-i-1);
                    }
                }else{
                    if(shipA.getShipType() == shipType2){
                        shipB = new Ship(shipType1,HORIZONTAL,shipSize[shipType1]);
                    }else{
                        shipB = new Ship(shipType2,HORIZONTAL,shipSize[shipType2]);
                    }
                    for(int i = 0;i < shipB.getShipSize();i++){
                        shipB.setShipPiece(i+1,9);
                    }
                }
                break;

            case 3:
                for(int i = 0;i < shipA.getShipSize();i++){
                    if(shipA.getShipDir() == HORIZONTAL){
                        shipA.setShipPiece(9-i,9);
                    }else{
                        shipA.setShipPiece(9,9-i);
                    }
                }
                if(shipA.getShipDir() == HORIZONTAL){
                    if(shipA.getShipType() == shipType2){
                        shipB = new Ship(shipType1,VARTICAL,shipSize[shipType1]);
                    }else{
                        shipB = new Ship(shipType2,VARTICAL,shipSize[shipType2]);
                    }
                    for(int i = 0;i < shipB.getShipSize();i++){
                        shipB.setShipPiece(9,9-i-1);
                    }
                }else{
                    if(shipA.getShipType() == shipType2){
                        shipB = new Ship(shipType1,HORIZONTAL,shipSize[shipType1]);
                    }else{
                        shipB = new Ship(shipType2,HORIZONTAL,shipSize[shipType2]);
                    }
                    for(int i = 0;i < shipB.getShipSize();i++){
                        shipB.setShipPiece(9-i-1,9);
                    }
                }
                break;
            default:
                break;
        }

        ownFleet.add(shipA);
        ownFleet.add(shipB);

        for(Ship edgeShip: ownFleet){
            if(edgeShip.getShipType() == shipA.getShipType() || edgeShip.getShipType() == shipB.getShipType()){
                ArrayList<int[]> pos = edgeShip.getShipPos();
                for(int i = 0;i < edgeShip.getShipSize();i++){
                    updateArea(myArea,pos.get(i)[0],pos.get(i)[1],2);
                }
            }
        }
    }

    private int[] getInitShipData(int shipType){
        int posX,posY; 
        int shipDir;
        int randNum;
        int[] data = new int[3];

        Arrays.fill(log,-1);

        while(true){
            posX = (int)(Math.random()*10);
            posY = (int)(Math.random()*10);
            if(!existLog(posX,posY)){
                if(canSetMyShip(posX,posY)){
                    if(isNearShip(posX,posY,shipType)){
                        //ダメ
                    }else{
                        shipDir = calcSuitDir(posX,posY,shipType); 
                        if(shipDir != 0){
                            data[0] = posX;
                            data[1] = posY;
                            data[2] = shipDir;
                            break;
                        }
                    }
                }
            }

            setLog(posX,posY);
        }

        //printLog(log);

        return data;
    }

    private void printLog(int[] alog){
        int idx;
        for(int i = 0;i < areaHeight;i++){
            for(int j = 0;j < areaWidth;j++){
                idx = i * areaHeight + j;
                System.out.print(alog[idx]+" ");
            }
            System.out.print("\n");
        }
    }

    private boolean existLog(int x,int y){
        int idx = y * areaHeight + x;
        if(0 < log[idx]){
            return true;
        }else{
            return false;
        }
    }

    private void setLog(int x, int y){
        int idx = y * areaHeight + x;
        log[idx] = 1;
    }

    public boolean canSetEnemyShip(int x, int y){
        int randValue;

        if(0 <= x  && x < 10 && 0 <= y && y < 10){
            if(enemyArea[y][x] < 0){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public boolean canSetMyShip(int x, int y){
        int randValue;
        if((x == 0 && y == 0) || (x == 9 && y == 0) || (x == 0 && y == 9) || (x == 9 && y == 9)){
            return false;
        }else{ 
            if(0 <= x  && x < 10 && 0 <= y && y < 10){
                if(myArea[y][x] == 0){
                    if(x == 0 || x == 9 || y == 0 || y == 9){
                        randValue = (int)(Math.random()*100);
                        if(randValue < EDGE_ARROWABLE){
                            //EDGE_ARROWABLE % の確率で端に置く
                            return true;
                        }else{
                            return false;
                        }
                    }else{
                        return true;
                    }
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }
    }

    //オーバーロード
    public boolean canSetShip(int x, int y, int shipType, int dir, boolean isMyArea){
        boolean canSet = false;
        int cnt = 1;
        int space = 1;
        int sign;

        while(true){
            if(cnt % 2 == 1){
                sign = 1;
            }else{
                sign = -1;
            }

            if(dir == HORIZONTAL){
                x = x + (sign * cnt);
            }else{
                y = y + (sign * cnt);
            }

            if(isMyArea){
                if(canSetMyShip(x,y)){
                    if(isNearShip(x,y,shipType)){
                        //ダメ
                    }else{
                        space++;
                    }
                }
            }else{
                if(canSetEnemyShip(x,y)){
                    space++;
                }
            }

            cnt++; 

            if(space + 1 < cnt){
                canSet = false;
                break;
            }

            if(isMyArea){
                if(shipSize[shipType] < space){
                    canSet = true;
                    break;
                }
            }else{
                if(shipSize[shipType] <= space){
                    canSet = true;
                    break;
                }
            }

        }

        return canSet;
    }

    private int calcSuitDir(int x, int y, int shipType){
        int shipDir = 0;
        int randValue;
        boolean var_f = true;
        boolean hor_f = true;

        if(canSetShip(x,y,shipType,HORIZONTAL,true)){
            hor_f = true;
        }else{
            hor_f = false;
        }

        if(canSetShip(x,y,shipType,VARTICAL,true)){
            var_f = true;
        }else{
            var_f = false;
        }

        if(!var_f && !hor_f){
            shipDir = 0;
        }else if(!var_f && hor_f){
            shipDir = HORIZONTAL;
        }else if(var_f && !hor_f){
            shipDir = VARTICAL;
        }else{
            shipDir = (int)(Math.random()*2)+1;
        }

        return shipDir;
    }

    private boolean isNearShip(int x, int y,int ownShipType){
        int difX,difY;
        ArrayList<int[]> otherShipPos;

        for(Ship s: ownFleet){
            otherShipPos = s.getShipPos();
            if(s.getShipType() != ownShipType){ 
                for(int i = 0;i < s.getShipSize();i++){
                    difX = otherShipPos.get(i)[0] - x;
                    difY = otherShipPos.get(i)[1] - y;
                    double radius = Math.sqrt(difX*difX+difY*difY);//船の距離
                    if(radius <= Math.sqrt(2.0d)){
                        //半径√2以内
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void setShipData(int x, int y, Ship thisShip){
        int shipPiece = 1;
        int harf;

        int topY = y;
        int underY = y;
        int rightX = x;
        int leftX = x;

        int randNum;

        while(shipPiece < thisShip.getShipSize()){
            if(thisShip.getShipDir() == VARTICAL){
                harf = (int)(Math.random()*2);
                if(harf == 0){
                    topY--;
                    if(canSetMyShip(x,topY)){
                        if(!isNearShip(x,topY,thisShip.getShipType())){
                            thisShip.setShipPiece(x,topY);
                            myArea[topY][x] = 2;
                            shipPiece++;
                        }else{
                            randNum = (int)(Math.random()*100);
                            //NEAR_ARROWABLE%で設置
                            if(randNum < NEAR_ARROWABLE){
                                thisShip.setShipPiece(x,topY);
                                myArea[topY][x] = 2;
                                shipPiece++;
                            }else{
                                topY++;
                            }
                        }
                    }else{
                        topY++;
                    }
                }else{
                    underY++;
                    if(canSetMyShip(x,underY)){
                        if(!isNearShip(x,underY,thisShip.getShipType())){
                            thisShip.setShipPiece(x,underY);
                            myArea[underY][x] = 2;
                            shipPiece++;
                        }else{
                            randNum = (int)(Math.random()*100);
                            //NEAR_ARROWABLE%で設置
                            if(randNum < NEAR_ARROWABLE){
                                thisShip.setShipPiece(x,underY);
                                myArea[underY][x] = 2;
                                shipPiece++;
                            }else{
                                underY--;
                            }
                        }
                    }else{
                        underY--;
                    }
                }
            }else{
                harf = (int)(Math.random()*2);
                if(harf == 0){
                    rightX++;
                    if(canSetMyShip(rightX,y)){
                        if(!isNearShip(rightX,y,thisShip.getShipType())){
                            thisShip.setShipPiece(rightX,y);
                            myArea[y][rightX] = 2;
                            shipPiece++;
                        }else{
                            randNum = (int)(Math.random()*100);
                            //NEAR_ARROWABLE%で設置
                            if(randNum < NEAR_ARROWABLE){
                                thisShip.setShipPiece(rightX,y);
                                myArea[y][rightX] = 2;
                                shipPiece++;
                            }else{
                                rightX--;
                            }
                        }
                    }else{
                        rightX--;
                    }
                }else{
                    leftX--;
                    if(canSetMyShip(leftX,y)){
                        if(!isNearShip(leftX,y,thisShip.getShipType())){
                            thisShip.setShipPiece(leftX,y);
                            myArea[y][leftX] = 2;
                            shipPiece++;
                        }else{
                            randNum = (int)(Math.random()*100);
                            //NEAR_ARROWABLE%で設置
                            if(randNum < NEAR_ARROWABLE){
                                thisShip.setShipPiece(leftX,y);
                                myArea[y][leftX] = 2;
                                shipPiece++;
                            }else{
                                leftX++;
                            }
                        }
                    }else{
                        leftX++;
                    }
                }
            }
        }
    }

    public boolean isHit(int x, int y){
        ArrayList<int[]> pos;
        for(Ship s: ownFleet){
             pos = s.getShipPos();
            for(int i = 0;i < s.getRest();i++){
                if(pos.get(i)[0] == x && pos.get(i)[1] == y){
                    setAttackedShip(s);
                    return true;
                }
            }
        }
        return false;
    }
}

import java.io.*;
import java.util.ArrayList;
public class MarineArea{
    private static final int VARTICAL = 1;
    private static final int HORIZONTAL = 2;
    private static final int NEAR_ARROWABLE = 5; //他船の近くに設置を許容する確率 
    private static final int EDGE_ARROWABLE = 10; //端に設置を許容する確率
    private int areaWidth;
    private int areaHeight;

    private static int[] shipSize;

    public static int[][] myArea;

    public static ArrayList<Ship> ownFleet; //自分の艦隊
    //艦娘のタイプ
    public static final int DESTROYER = 0;//駆逐
    public static final int LIGHT_CRUISER = 1;//軽巡
    public static final int HEAVY_CRUISER = 2;//重巡
    public static final int BATTLE_SHIP = 3;//戦艦
    public static final int AIR_CARRIER = 4;//空母

    MarineArea(int areaWidth, int areaHeight){
        this.areaWidth = areaWidth;
        this.areaHeight = areaHeight;

        myArea = new int[areaHeight][areaWidth];

        shipSize = new int[5];
        shipSize[DESTROYER] = 2;
        shipSize[LIGHT_CRUISER] = 3;
        shipSize[HEAVY_CRUISER] = 3;
        shipSize[BATTLE_SHIP] = 4;
        shipSize[AIR_CARRIER] = 5;

        ownFleet = new ArrayList<Ship>();

        setMyArea();

        printMyArea();
    }

    private static void printMyArea(){
        for(int i = 0;i < 10;i++){
            for(int j = 0;j < 10;j++){
                System.out.print(myArea[i][j]+" ");
            }
            System.out.print("\n");
        }
    }

    private static void setMyArea(){
        setShip(AIR_CARRIER);
        setShip(BATTLE_SHIP);
        setShip(HEAVY_CRUISER);
        setShip(LIGHT_CRUISER);
        setShip(DESTROYER);
    }

    private static void setShip(int shipType){
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
        System.out.println("set first point");

        setShipData(posX,posY,aShip);
    }

    private static int[] getInitShipData(int shipType){
        int posX,posY; 
        int shipDir;
        int randNum;
        int[] data = new int[3];

        while(true){
            posX = (int)(Math.random()*10);
            posY = (int)(Math.random()*10);
            if(canSetShip(posX,posY)){
                if(isNearShip(posX,posY,shipType)){
                    randNum = (int)(Math.random()*100);
                    if(randNum < NEAR_ARROWABLE){
                        //近くに船があってもNEAR_ARROWABLE % で設置する
                        shipDir = calcSuitDir(posX,posY,shipType); 
                        if(shipDir != 0){
                            data[0] = posX;
                            data[1] = posY;
                            data[2] = shipDir;
                            break;
                        }
                    }
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

        return data;
    }

    private static boolean canSetShip(int x, int y){
        int randValue;
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

    private static int calcSuitDir(int x, int y, int shipType){
        int shipDir = 0;
        int cnt,absCnt;
        int randValue;
        int space;
        int sign;
        boolean var_f = true;
        boolean hor_f = true;

        int initX = x;
        int initY = y;

        int topY = y;
        int underY = y;
        int rightX = x;
        int leftX = x;

        //水平チェック
        cnt = 1;
        space = 1;
        while(true){
            if(cnt % 2 == 1){
                sign = 1;
            }else{
                sign = -1;
            }

            x = x + (sign * cnt);

            if(canSetShip(x,initY)){
                if(isNearShip(x,initY,shipType)){
                    randValue = (int)(Math.random()*100);
                    //近くに船があってもNEAR_ARROWABLE % で設置
                    if(randValue < NEAR_ARROWABLE){
                        space++;
                    }
                }else{
                    space++;
                }
            }

            if(cnt > space + 1){
                hor_f = false;
            }

            cnt++; 

            if(shipSize[shipType] < space){
                hor_f = true;
                break;
            }
        }

        //垂直チェック
        cnt = 1;
        space = 1;
        while(true){
            if(cnt % 2 == 1){
                sign = 1;
            }else{
                sign = -1;
            }

            y = y + (sign * cnt);

            if(canSetShip(initX,y)){
                if(isNearShip(initX,y,shipType)){
                    randValue = (int)(Math.random()*100);
                    //NEAR_ARROWABLE % の確率で設置
                    if(randValue < NEAR_ARROWABLE){
                        space++;
                    }
                }else{
                    space++;
                }
            }

            if(cnt > space + 1){
                var_f = false;
            }

            cnt++; 

            if(shipSize[shipType] < space){
                hor_f = true;
                break;
            }
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

    private static boolean isNearShip(int x, int y,int ownShipType){
        int difX,difY;
        for(Ship s: ownFleet){
            int[][] otherShipPos = s.getShipPos();
            if(s.getShipType() != ownShipType){ 
                for(int i = 0;i < s.getShipLength();i++){
                    difX = otherShipPos[i][0] - x;
                    difY = otherShipPos[i][1] - y;
                    double radius = Math.sqrt(difX*difX+difY*difY);//船の距離
                    if(radius < Math.sqrt(2)){
                        //半径√2以内
                        System.out.println("near");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static void setShipData(int x, int y, Ship thisShip){
        int shipPiece = 1;
        int harf;

        int topY = y;
        int underY = y;
        int rightX = x;
        int leftX = x;

        while(shipPiece < thisShip.getShipSize()){
            if(thisShip.getShipDir() == VARTICAL){
                harf = (int)(Math.random()*2);
                if(harf == 0){
                    topY--;
                    if(canSetShip(x,topY)){
                        if(!isNearShip(x,topY,thisShip.getShipType())){
                            thisShip.setShipPiece(x,topY);
                            myArea[topY][x] = 2;
                            shipPiece++;
                        }else{
                            topY++;
                        }
                    }else{
                        topY++;
                    }
                }else{
                    underY++;
                    if(canSetShip(x,underY)){
                        if(!isNearShip(x,underY,thisShip.getShipType())){
                            thisShip.setShipPiece(x,underY);
                            myArea[underY][x] = 2;
                            shipPiece++;
                        }else{
                            underY--;
                        }
                    }else{
                        underY--;
                    }
                }
            }else{
                harf = (int)(Math.random()*2);
                if(harf == 0){
                    rightX++;
                    if(canSetShip(rightX,y)){
                        if(!isNearShip(rightX,y,thisShip.getShipType())){
                            thisShip.setShipPiece(rightX,y);
                            myArea[y][rightX] = 2;
                            shipPiece++;
                        }else{
                            rightX--;
                        }
                    }else{
                        rightX--;
                    }
                }else{
                    leftX--;
                    if(canSetShip(leftX,y)){
                        if(!isNearShip(leftX,y,thisShip.getShipType())){
                            thisShip.setShipPiece(leftX,y);
                            myArea[y][leftX] = 2;
                            shipPiece++;
                        }else{
                            leftX++;
                        }
                    }else{
                        leftX++;
                    }
                }
            }
        }
    }
}

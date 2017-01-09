import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.io.*;
public class AIAdmiral{
    private static final int DEFAULT_SEQUENCE = 1;
    private static final int HIT_SEQUENCE = 2;
    private MarineArea marineArea;
    private static int[][] weightMap; //重み
    private static int[][] weightMapLog;
    private int searchMode;
    //各重み
    private int donutExtensionPoint;
    private int donutPoint;
    private int intervalPoint;
    private int setShipPoint;
    private int targetWeight;

    private int[] hitPos;//ヒットした時の座標
    private ArrayList<int[]> hitPosLog;//ヒットした座標のログ

    //組み合わせ
    private static int[][] mass1 = {{0,1},{1,0},{0,0,1},{0,1,0},{1,0,0},{0,0,0,1},{0,0,1,0},{0,1,0,0},{1,0,0,0},
                                 {0,0,0,0,1},{0,0,0,1,0},{0,0,1,0,0},{0,1,0,0,0},{1,0,0,0,0}};

    private static int[][] mass2 = {{0,1,1},{1,1,0},{0,0,1,1},{0,1,1,0},{1,1,0,0},
                                 {0,0,0,1,1},{0,0,1,1,0},{0,1,1,0,0},{1,1,0,0,0}};

    private static int[][] mass3 = {{0,1,1,1},{1,1,1,0},{0,0,1,1,1},{0,1,1,1,0},{1,1,1,0,0}};

    private static int[][] mass4 = {{0,1,1,1,1},{1,1,1,1,0}};

    AIAdmiral(MarineArea marineArea){
        this.marineArea = marineArea;
        weightMap = new int[marineArea.getHeight()][marineArea.getWidth()];
        searchMode = DEFAULT_SEQUENCE;
        donutExtensionPoint = 5;
        donutPoint = 10;
        intervalPoint = 10;
        setShipPoint = 5;
        hitPos = new int[2];
        hitPosLog = new ArrayList<int[]>();
    }

    //砲撃座標を決める
    public int[] order(int mode){
        int[] target;

        this.searchMode = mode;

        marineArea.fill(weightMap,0);

        /*ニューラルネット*/
        
        switch(searchMode){
            case DEFAULT_SEQUENCE:
                targetWeight = 80;
                weight1();
                weight2();
                weight3();
                weight4();
                //weight5();
                break;

            case HIT_SEQUENCE:
                int[] pos = hitPosLog.get(0);
                int dir;
                targetWeight = 100;

                int diffX = hitPos[0]-pos[0];
                int diffY = hitPos[1]-pos[1];
                if(diffX != 0 || diffY != 0){
                    //２つ以上ヒット
                    if(0 < Math.abs(diffX)){ 
                        dir = MarineArea.HORIZONTAL;
                    }else{
                        dir = MarineArea.VARTICAL;
                    }
                    weightB(dir);
                    System.out.println("hoge");
                }else{
                    weightA();
                    weightB();
                }

                break;

            default:
                break;
        }

        target = decide();
        return target; 
    }

    /*重み付け関数1
     * ドーナツの延長線
     */
    private void weight1(){
        for(int i = 0;i < marineArea.getHeight();i++){
           for(int j = 0;j < marineArea.getWidth();j++){
               if(j == 3 || j == 6 || i == 3 || i == 6){
                   if(!isAttackedPos(j,i)){
                       //ドーナツ+延長戦
                       weightMap[i][j] += donutExtensionPoint;
                   }else{

                   }
               }
           } 
        }
    }

    /*重み付け関数2
     * ドーナツ
     */
    private void weight2(){
        for(int i = 0;i < marineArea.getHeight();i++){
           for(int j = 0;j < marineArea.getWidth();j++){
               if(isDonutPos(j,i)){
                   if(!isAttackedPos(j,i)){
                       //ドーナツ
                       weightMap[i][j] += donutPoint;
                   }
               }
           } 
        }
    }

    /*重み付け関数3
     * 1つ飛ばし
     */
    private void weight3(){
        for(int i = 0;i < marineArea.getHeight();i++){
            for(int j = 0;j < marineArea.getWidth();j++){
                if(!isAttackedPos(j,i)){
                    if((i+j) % 2 == 0){
                        weightMap[i][j] += intervalPoint;
                    }
                }
            }
        }
    }

    /*重み付け関数4
     * 残存敵艦船に応じての重み付け
     */
    private void weight4(){
        for(int i = 0;i < marineArea.getHeight();i++){
            for(int j = 0;j < marineArea.getWidth();j++){
                if(!isAttackedPos(j,i)){
                    for(Ship s: marineArea.enemyFleet){
                        //残存艦船ごと確認
                        int theType = s.getShipType();
                        for(int dir = 1;dir <= 2;dir++){
                            if(marineArea.canSetShip(j,i,theType,dir,false)){
                                weightMap[i][j] += setShipPoint;
                            }
                        }
                    }
                }
            }
        }
    }

    private void weight5(){

    }

    private void weightA(){
        int[][] enemyArea = marineArea.getEnemyArea();
        int startPosX = hitPos[0];
        int startPosY = hitPos[1];

        //残存している艦船を置けるかどうか
        for(Ship s: marineArea.enemyFleet){
            //残存艦船ごと確認
            int theType = s.getShipType();
            if(marineArea.canSetShip(startPosX,startPosY,theType,MarineArea.HORIZONTAL,false)){
                if(marineArea.canSetEnemyShip(startPosX+1,startPosY)){
                    weightMap[startPosY][startPosX+1] += setShipPoint;
                }

                if(marineArea.canSetEnemyShip(startPosX-1,startPosY)){
                    weightMap[startPosY][startPosX-1] += setShipPoint;
                }
            }

            if(marineArea.canSetShip(startPosX,startPosY,theType,MarineArea.VARTICAL,false)){
                if(marineArea.canSetEnemyShip(startPosX,startPosY+1)){
                    weightMap[startPosY+1][startPosX] += setShipPoint;
                }

                if(marineArea.canSetEnemyShip(startPosX,startPosY-1)){
                    weightMap[startPosY-1][startPosX] += setShipPoint;
                }
            }
        }
    }

    private void weightB(){
        int[][] enemyArea = marineArea.getEnemyArea();
        int startPosX = hitPos[0];
        int startPosY = hitPos[1];

        //組み合わせによる配置
    }

    //オーバーロード
    private void weightB(int shipDir){
        int[][] enemyArea = marineArea.getEnemyArea();
        int startPosX = hitPos[0];
        int startPosY = hitPos[1];

        //組み合わせによる配置
    }

    private void weightC(){

    }

    public int[][] getWeightMapLog(){
        return weightMapLog;
    }

    public void setHitPos(int x, int y){
        this.hitPos[0] = x;
        this.hitPos[1] = y;
        hitPosLog.add(hitPos.clone());
    }

    //過去に攻撃してるか確認
    private boolean isAttackedPos(int x, int y){
        int[][] enemyArea = marineArea.getEnemyArea();
        if(enemyArea[y][x] < 0){
            return false;
        }else{
            return true;
        }
    } 

    //ドーナツ
    private boolean isDonutPos(int x, int y){
        if(3 <= x  && x <= 6  &&  3 <= y && y <= 6){
            if(x < 4 || 5 < x || y < 4 || 5 < y){
                return true;
            }else{
                return false;
            }
        }else{
           return false;
        } 
    }

    /*砲撃座標を決定する
     * 重みが大きいところを高確率で選ぶ
     */
    private int[] decide(){
        ArrayList<Integer> weightList = getWeightList();

        int randNum;
        int idx = 0;
        for(int i = 0;i < weightList.size();i++){
            idx = i;
            randNum = (int)(Math.random()*100);
            if(randNum < targetWeight){
                break;
            }
        }

        int[] slatePoint = getSlatePoint(weightList.get(idx));
        
        return slatePoint;
    }

    //その重みを持つ候補点取得
    private int[] getSlatePoint(int theWeight){
        ArrayList<int[]> points = new ArrayList<int[]>();
        int[] point = new int[2];
        for(int i = 0;i < marineArea.getHeight();i++){
            for(int j = 0;j < marineArea.getWidth();j++){
                if(!isAttackedPos(j,i)){
                    if(weightMap[i][j] == theWeight){
                        point[0] = j;
                        point[1] = i;
                        points.add(point.clone());
                    } 
                }
            }
        }

        int randNum = (int)(Math.random()*points.size());

        return points.get(randNum);
    }

    //重みのリストを取得
    private ArrayList<Integer> getWeightList(){
        ArrayList<Integer> theList = new ArrayList<Integer>();
        for(int i = 0;i < marineArea.getHeight();i++){
            for(int j = 0;j < marineArea.getWidth();j++){
                if(!theList.contains(weightMap[i][j])){
                    theList.add(weightMap[i][j]);
                }
            }
        }
        Collections.sort(theList, Comparator.reverseOrder());
        return theList;
    }

    //重みマップ表示
    public void printWeightMap(/*int[][] map*/){
        for(int i = 0;i < marineArea.getHeight();i++){
            for(int j = 0;j < marineArea.getWidth();j++){
                System.out.printf("%3d ",weightMap[i][j]);
            }
            System.out.print("\n");
        }
    }
}


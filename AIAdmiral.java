import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.io.*;
public class AIAdmiral{
    private static final int MAX_WEIGHT = 100;
    private MarineArea marineArea;
    private static int[][] weightMap; //重み

    AIAdmiral(MarineArea marineArea){
        this.marineArea = marineArea;
        weightMap = new int[marineArea.getHeight()][marineArea.getWidth()];
    }

    //砲撃座標を決める
    public int[] order(){
        int[] target;

        marineArea.fill(weightMap,0);

        //ニューラルネット
        weight1();
        weight2();
        weight3();
        weight4();

        printWeightMap();

        target = decide();

        return target; 
    }

    private void printWeightMap(){
        for(int i = 0;i < marineArea.getHeight();i++){
            for(int j = 0;j < marineArea.getWidth();j++){
                System.out.printf("%3d ",weightMap[i][j]);
            }
            System.out.print("\n");
        }
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
                       weightMap[i][j] += 10;
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
                       weightMap[i][j] += 20;
                   }
               }
           } 
        }
    }

    /*重み付け関数3
     * 1つ飛ばし
     */
    private void weight3(){

    }

    /*重み付け関数4
     * 船の位置が割れてるとき
     */
    private void weight4(){

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
        int[] point = new int[2];

        ArrayList<Integer> weightList = getWeightList();

        int randNum;
        int idx = 0;
        for(int i = 0;i < weightList.size();i++){
            idx = i;
            randNum = (int)(Math.random()*100);
            if(randNum < 80){
                break;
            }
        }

        ArrayList<int[]> slatePoints = getSlatePoints(weightList.get(idx));

        randNum = (int)(Math.random()*slatePoints.size());
        
        return slatePoints.get(randNum);
    }

    //その重みを持つ候補点取得
    private ArrayList<int[]> getSlatePoints(int theWeight){
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
        return points;
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
}


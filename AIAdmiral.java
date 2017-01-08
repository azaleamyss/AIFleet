import java.util.ArrayList;
import java.io.*;
public class AIAdmiral{
    private MarineArea marineArea;
    private static int[][] weightMap; //重み

    AIAdmiral(MarineArea marineArea){
        this.marineArea = marineArea;
        weightMap = new int[marineArea.getHeight()][marineArea.getWidth()];
    }

    //砲撃座標を決める
    public int[] order(){
        int[] target;

        //ニューラルネット
        weight1();
        weight2();
        weight3();

        target = decide();

        return target; 
    }


    /*重み付け関数1
     * ドーナツ
     */
    private void weight1(){

    }

    /*重み付け関数2
     * 1つ飛ばし
     */
    private void weight2(){

    }

    //重み付け関数3
    private void weight3(){

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

    /*砲撃座標を決定する
     * 重みが大きいところを高確率で選ぶ
     */
    private int[] decide(){
        int[] pos = new int[2];
        return pos;
    }
}


import java.io.*;
import java.util.ArrayList;
import java.lang.String;
public class ShipGameController{
    //モード
    private static final int ENEMY_ATTACK = 1;
    private static final int OUR_ATTACK   = 2;
    private static final int GAME_FINISH  = 3;
    private static final int PRINT_ENEMY_AREA = 4;
    private static final int PRINT_OWN_AREA = 5;
    private static final int PRINT_WEIGHT_MAP = 6;

    private static final int MISS = 0;
    private static final int HIT = 1;
    private static MarineArea marineArea;
    private static MarineArea testArea;
    private static AIAdmiral admiral;

    public static void main(String[] args) throws IOException{
        boolean isend = false;
        int next;
        Ship theShip;
        String[] inpos;
        int[] target;

        marineArea = new MarineArea(10,10);//自分
        System.out.println("自艦隊");
        testArea = new MarineArea(10,10); //敵(テスト用)
        System.out.println("敵艦隊");

        admiral = new AIAdmiral(marineArea);
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        while(!isend){
            System.out.println("\n敵艦隊の砲撃 - 1 / 自艦隊の砲撃 - 2 / ゲーム終了 - 3");
            System.out.println("敵エリア表示 - 4 / 自エリア表示 - 5 / 敵重みマップ表示 - 6");
            System.out.print("Next Mode: ");
            next = Integer.parseInt(br.readLine());

            switch(next){
                case ENEMY_ATTACK: //敵艦隊の砲撃
                    while(true){
                        System.out.print("\n砲撃された座標: ");
                        inpos = br.readLine().split(",",0);
                        System.out.println("この座標で良い？: 1 - yes / 2 - no ");
                        int judge = Integer.parseInt(br.readLine()); 
                        if(judge == 1){
                            break;
                        }
                    }
                    int x = Integer.parseInt(inpos[0]);
                    int y = Integer.parseInt(inpos[1]);
                    if(marineArea.isAttacked(x,y)){
                        System.out.println("The point is already attacked.");
                    }else{
                        if(marineArea.isHit(x,y)){
                            theShip = marineArea.getAttackedShip();
                            theShip.removePos(x,y);
                            marineArea.updateArea(marineArea.getMyArea(),x,y,HIT);
                            if(theShip.isSink()){
                                System.out.println("Congratulation !");
                            }else{
                                System.out.println("hit!");
                            }
                        }else{
                            System.out.println("miss!");
                        }
                    }
                    break;

                case OUR_ATTACK: //自艦隊の砲撃
                    int searchMode;
                    while(true){
                        System.out.println("\n探索モード: 1 - デフォルトシーケンス / 2 - ヒットシーケンス");
                        searchMode = Integer.parseInt(br.readLine());

                        if(0 < searchMode && searchMode < 3){
                            if(searchMode == 2){
                                if(admiral.haveHit()){
                                    break;
                                }else{
                                    System.out.println("まだヒットしていません");
                                }
                            }else{
                                break;
                            }
                        }else{
                            System.out.println("正しい値を入力して下さい");
                        }
                    }

                    System.out.println("\n座標計算中...");
                    target = admiral.order(searchMode);
                    System.out.println("\n目標: (" + target[0] + "," + target[1] + ")"+"\n");

                    //テスト用
                    int r = test(target);
                    if(r == HIT){
                        System.out.println("echo : HIT!");
                    }else{
                        System.out.println("echo : MISS!");
                    }

                    int result;
                    while(true){
                        System.out.println("砲撃結果: 0 - miss , 1 - hit");
                        result = Integer.parseInt(br.readLine());
                        if(result == 0 || result == 1){
                            break;
                        }else{
                            System.out.println("正しい値を入力して下さい");
                        }
                    }

                    if(result == HIT){
                        System.out.println("hit!");
                        admiral.setHaveHit(true);
                        admiral.setHitPos(target[0],target[1]);
                        admiral.increaseDamage();
                        System.out.println("\n1 - ヒットのみ / 2 - 敵艦轟沈");
                        if(Integer.parseInt(br.readLine()) == 2){
                            System.out.println("轟沈!");
                            admiral.setEstimateSinkShip();
                        }
                    }else{
                        System.out.println("miss!");
                    }

                    marineArea.updateArea(marineArea.getEnemyArea(),target[0],target[1],result);
                    break;

                case GAME_FINISH: //ゲーム終了
                    isend = true;
                    break;

                case PRINT_ENEMY_AREA: //敵エリア表示
                    marineArea.printArea(marineArea.getEnemyArea());
                    break;

                case PRINT_OWN_AREA: //自エリア表示
                    marineArea.printArea(marineArea.getMyArea());
                    break;

                case PRINT_WEIGHT_MAP: //敵の重みマップ表示
                    admiral.printWeightMap(/*admiral.getWeightMapLog()*/);
                    break;

                default:
                    break;
            }
        }
    }

    //テスト
    private static int test(int[] target){
        int[][] map = testArea.getMyArea();
        if(map[target[1]][target[0]] == 2){
            return 1;
        }else{
            return 0;
        }
    }
}

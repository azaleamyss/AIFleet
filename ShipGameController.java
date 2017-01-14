import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
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
        int[] target;
        String[] inpos = new String[2];
        Ship damagedShip;

        marineArea = new MarineArea(10,10);//自分
        System.out.println("自艦隊");
        //testArea = new MarineArea(10,10); //敵(テスト用)
        //System.out.println("敵艦隊");
        admiral = new AIAdmiral(marineArea);

        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        boolean isend = false;
        while(!isend){
            boolean next_f = false;
            String input = null;
            while(!next_f){
                System.out.println("\n敵艦隊の砲撃 - 1 / 自艦隊の砲撃 - 2 / ゲーム終了 - 3");
                System.out.println("敵エリア表示 - 4 / 自エリア表示 - 5 / 敵重みマップ表示 - 6");
                System.out.print("Next Mode: ");
                input = br.readLine();
                for(int k = 1;k < 7;k++){
                    if(input.equals(""+k)){
                        next_f = true;
                    }
                } 
            }

            int next = Integer.parseInt(input);
            switch(next){
                case ENEMY_ATTACK: //敵艦隊の砲撃
                    boolean return_f = false;
                    if(isCorrectSelection("enemy attack",br)){
                        while(true){
                            System.out.print("\n砲撃された座標(カンマ区切り): ");
                            inpos = br.readLine().split(",",0);
                            if(isCorrectSelection("この座標でいい",br)){
                                break;
                            }
                        }

                        int x = Integer.parseInt(inpos[0])-1;
                        int y = Integer.parseInt(inpos[1])-1;
                        if(marineArea.isAttacked(x,y)){
                            System.out.println("The point is already attacked.");
                        }else{
                            if(marineArea.isHit(x,y)){
                                damagedShip = marineArea.getAttackedShip();
                                damagedShip.removePos(x,y);
                                marineArea.updateArea(marineArea.getMyArea(),x,y,HIT);
                                if(damagedShip.isSink()){
                                    System.out.println("Congratulation !");
                                }else{
                                    System.out.println("hit!");
                                }
                            }else{
                                System.out.println("miss!");
                            }
                        }
                    }
                    break;

                case OUR_ATTACK: //自艦隊の砲撃
                    if(isCorrectSelection("our attack",br)){
                        System.out.println("\n座標計算中...");
                        target = admiral.order();
                        System.out.println("\n目標: (" + (target[0]+1) + "," + (target[1]+1)+ ")"+"\n");

                        while(true){
                            System.out.println("砲撃結果: 0 - miss , 1 - hit");
                            input = br.readLine();
                            if(input.equals("0") || input.equals("1")){
                                break;
                            }else{
                                System.out.println("正しい値を入力して下さい");
                            }
                        }

                        int result = Integer.parseInt(input);
                        if(result == HIT){
                            admiral.setHitPos(target[0],target[1]);
                            admiral.increaseDamage();
                            admiral.setSearchMode(AIAdmiral.HIT_SEQUENCE);//ヒットシーケンスに移行
                            System.out.println("\n1 - ヒットのみ / 2 - 敵艦轟沈");

                            if(br.readLine().equals("2")){
                                System.out.println("轟沈!");
                                admiral.setEstimateSinkList();
                                admiral.setSearchMode(AIAdmiral.DEFAULT_SEQUENCE);
                            }
                        }

                        marineArea.updateArea(marineArea.getEnemyArea(),target[0],target[1],result);
                    }
                    break;

                case GAME_FINISH: //ゲーム終了
                    if(isCorrectSelection("game finish",br)){
                        isend = true;
                    }
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

    private static boolean isCorrectSelection(String selection, BufferedReader br) throws IOException{
        System.out.println("\n"+selection +" ? : y / n");
        String judge = br.readLine();
        if(judge.equals("y")){
            return true;
        }else{
            return false;
        }
    }
}

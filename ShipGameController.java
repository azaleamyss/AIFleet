import java.io.*;
import java.util.ArrayList;
import java.lang.String;
public class ShipGameController{
    private static final int ENEMY_ATTACK = 1;
    private static final int OUR_ATTACK   = 2;
    private static final int GAME_FINISH  = 3;
    private static final int MISS = 0;
    private static final int HIT = 1;
    private static MarineArea marineArea;
    private static AIAdmiral admiral;

    public static void main(String[] args) throws IOException{
        boolean isend = false;
        int next;
        Ship theShip;
        String[] inpos;
        int[] target;

        marineArea = new MarineArea(10,10);
        admiral = new AIAdmiral(marineArea);
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        while(!isend){
            System.out.println("NextAction");
            System.out.println("相手の砲撃 - 1 / こちらの砲撃 - 2 / ゲーム終了 - 3");
            next = Integer.parseInt(br.readLine());

            switch(next){
                case ENEMY_ATTACK: //敵艦隊の砲撃
                    System.out.print("砲撃された座標: ");
                    inpos = br.readLine().split(",",0);
                    int x = Integer.parseInt(inpos[0]);
                    int y = Integer.parseInt(inpos[1]);
                    if(marineArea.isAttacked(x,y)){
                        System.out.println("The point is already attacked.");
                    }else{
                        if(isHit(x,y)){
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
                    marineArea.printArea(marineArea.getMyArea());
                    break;

                case OUR_ATTACK: //自艦隊の砲撃
                    target = admiral.order();
                    System.out.println("目標: (" + target[0] + "," + target[1] + ")");
                    System.out.print("砲撃結果: 0 - miss , 1 - hit");
                    int result = Integer.parseInt(br.readLine());
                    if(result == HIT){
                        System.out.println("hit!");
                    }else{
                        System.out.println("miss!");
                    }

                    marineArea.updateArea(marineArea.getEnemyArea(),target[0],target[1],result);
                    marineArea.printArea(marineArea.getEnemyArea());

                    break;

                case GAME_FINISH:
                    isend = true;
                    break;

                default:
                    break;
            }
        }
    }

    private static boolean isHit(int x, int y){
        ArrayList<int[]> pos;
        for(Ship s: marineArea.ownFleet){
             pos = s.getShipPos();
            for(int i = 0;i < s.getRest();i++){
                if(pos.get(i)[0] == x && pos.get(i)[1] == y){
                    marineArea.setAttackedShip(s);
                    return true;
                }
            }
        }
        return false;
    }
}

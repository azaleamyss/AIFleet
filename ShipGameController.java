import java.io.*;
import java.util.ArrayList;
import java.lang.String;
public class ShipGameController{
    private static final int ENEMY_ATTACK = 1;
    private static final int OUR_ATTACK   = 2;
    private static final int GAME_FINISH  = 3;
    private static MarineArea marineArea;

    public static void main(String[] args) throws IOException{
        boolean isend = false;
        int next;
        Ship theShip;
        String[] inpos;
        int[] target;

        marineArea = new MarineArea(10,10);
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
                            marineArea.updateMyArea(x,y);
                            if(theShip.isSink()){
                                System.out.println("Congratulation !");
                            }else{
                                System.out.println("hit");
                            }
                        }else{
                            System.out.println("miss");
                        }
                    }
                    marineArea.printMyArea();
                    break;

                case OUR_ATTACK: //自艦隊の砲撃
                    target = attack();
                    System.out.println("目標: (" + target[0] + "," + target[1] + ")");
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
            for(int i = 0;i < s.getShipSize();i++){
                if(pos.get(i)[0] == x && pos.get(i)[1] == y){
                    marineArea.setAttackedShip(s);
                    return true;
                }
            }
        }
        return false;
    }

    //砲撃座標を決める
    private static int[] attack(){
        int[] pos = new int[2];
        return pos; 
    }
}

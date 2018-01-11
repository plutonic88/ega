package ega;

import ega.games.EmpiricalMatrixGame;
import ega.games.MatrixGame;
import ega.games.MixedStrategy;
import ega.parsers.GamutParser;
import ega.solvers.EpsNESolver;

/**
 * Created by IntelliJ IDEA.
 * User: Oscar-XPS
 * Date: 10/10/13
 * Time: 5:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class EpsNEAgent {
    public static void main(String[] args) {
      int  player = 0;
      String filename = "";
      double lambda = 2.4;
      if(args.length==4){
             for(int i = 0; i < args.length; i++){
                if(args[i].equals("-game")){
                    try{
                        filename = args[++i];
                    }catch(Exception e){
                        System.err.println("Error parsing for QRE." );
                    }
                }else if(args[i].equals("-player")){
                    try{
                        player = Integer.parseInt(args[++i]);
                        player-=1;
                    }catch(Exception e){
                        System.err.println("Error parsing for QRE." );
                    }
                }
            }
      }
      System.out.println(runENE(new EmpiricalMatrixGame(readGame(filename)),player));
  }
  /*public static MatrixGame readGame(){
      return GamutParser.readGamutGame(GAME_FILES_PATH +"2501"+GAMUT_GAME_EXTENSION);
  }*/
  public static MatrixGame readGame(String path){
      //debug
      //System.out.println("Path: "+path);
      return GamutParser.readGamutGame(path);
  }
  public static MixedStrategy runENE (EmpiricalMatrixGame g, int player){
      EpsNESolver ene = new EpsNESolver();

      return ene.findMostStableOutcome(g,player);
  }
}

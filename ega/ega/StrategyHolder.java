package ega;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * User: Oscar-XPS
 * Date: 10/2/13
 * Time: 2:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class StrategyHolder {
    private HashMap<String,StrategyMap> strategyMap;
    private static StrategyHolder instance = null;
    double eps;

    private StrategyHolder(){
        //singleton constructor is not called directly
        strategyMap = new HashMap<String, StrategyMap>();
        StrategyMap temp;
        try{
            Scanner scan = new Scanner(new File(Parameters.GAME_FILES_PATH+"strategyMaps.csv"));
            while(scan.hasNext()){
                temp = new StrategyMap(scan.nextLine());
                strategyMap.put(temp.getStrategyName(),temp);
            }

        }catch (Exception e){e.printStackTrace();}
        eps = 0;
    }
    public static StrategyHolder getInstance() {
      if(instance == null) {
         instance = new StrategyHolder();
      }
      return instance;
   }
    public StrategyMap getMapping(String name){
        return strategyMap.get(name);
    }
    public void setEps(double eps){
        this.eps = eps;
    }
    public double getEps(){
        return eps;
    }


}

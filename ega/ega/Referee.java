package ega;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import ltarPractice.LtarMain;
import ega.games.DeviationIterator;
import ega.generate.Generate;
import ega.log.Logger;


/**
 * Created by IntelliJ IDEA.
 * User: Oscar-XPS
 * Date: 9/20/13
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Referee {
	private static int numberOfGames;
	private static ArrayList<String> agentPaths;
	private static ArrayList<String> agentNames;
	private static int[] actions;

	/*
	 * final variables:
	 * 
	 * 1. distance metric
	 * 2. taking the max distance or sum them and taking the max distance
	 * 3. delta: either average or max
	 */


	//public static boolean noisy = false;

	public static long START_TIME = 0;
	public static long END_TIME = 0; 
	//public static long clustertime = 0; 

	public static long[] clustertime = new long[2]; 


	private static  boolean RAND_POINTS_FROM_OBSERVATION = false; 
	
	private static  boolean RAND_ACTION_INIT_TO_CLUSTERS = true;






	private static  boolean DIST_METRIC_LINE = false; //used to create dir, if this is true, then other one is false, vice versa
	private static  boolean DIST_METRIC_EUCLIDEAN =  true;



	
	
	//used to create dir
	//make these false if euclidean
	private static   boolean MAX_DIST = false;   
	private static  final boolean SUM_DIST = false; 
	
	
	

	private static  boolean MAX_DELTA = false; //used to create dir
	private static  boolean AVRG_DELTA = true;
	

	public static  String experimentdir = null;//Referee.DIST_METRIC_LINE? "1": "0" + Referee.SUM_DIST ? "1":"0" + Referee.MAX_DELTA ? "1" : "0";


	public static double percremovedstrategy = 0; 



	
	
	
	
	public static void setRAND_POINTS_FROM_OBSERVATION(
			boolean rAND_POINTS_FROM_OBSERVATION) {
		RAND_POINTS_FROM_OBSERVATION = rAND_POINTS_FROM_OBSERVATION;
	}




	public static void setRAND_ACTION_INIT_TO_CLUSTERS(
			boolean rAND_ACTION_INIT_TO_CLUSTERS) {
		RAND_ACTION_INIT_TO_CLUSTERS = rAND_ACTION_INIT_TO_CLUSTERS;
	}



	public static int getBitValue(int var, int bitposition)
	{
		int x = var & (1<<bitposition);
		if(x>0)
			return 1;


		return 0; 
	}




	public static boolean isRandActionInitToClusters() {
		return RAND_ACTION_INIT_TO_CLUSTERS;
	}



	public static boolean isRandPointsFromObservation() {
		return RAND_POINTS_FROM_OBSERVATION;
	}



	public static boolean isDistMetricLine() {
		return DIST_METRIC_LINE;
	}




	public static boolean isDistMetricEuclidean() {
		return DIST_METRIC_EUCLIDEAN;
	}




	public static boolean isMaxDist() {
		return MAX_DIST;
	}




	public static boolean isSumDist() {
		return SUM_DIST;
	}




	public static boolean isMaxDelta() {
		return MAX_DELTA;
	}




	public static boolean isAvrgDelta() {
		return AVRG_DELTA;
	}












	public static void addAgents(){
		//numberOfGames = 500;
		numberOfGames = 100;
		agentPaths = new ArrayList<String>();
		agentNames = new ArrayList<String>();
		agentPaths.add(Parameters.GAME_FILES_PATH+ "QRE.jar");//lambda 2.4
		agentPaths.add(Parameters.GAME_FILES_PATH+ "QRE5.jar");//lambda 5
		agentPaths.add(Parameters.GAME_FILES_PATH+ "QRE100.jar");//MSNE
		agentPaths.add(Parameters.GAME_FILES_PATH+ "QRE0.jar");//uniform
		agentPaths.add(Parameters.GAME_FILES_PATH+ "BRQRE0.jar");//best response to uniform
		agentPaths.add(Parameters.GAME_FILES_PATH+ "ENE.jar");//MSNE
		agentNames.add("QRE.jar");
		agentNames.add("QRE5.jar");
		agentNames.add("QRE100.jar");
		agentNames.add("QRE0.jar");
		agentNames.add("BRQRE0.jar");
		agentNames.add("ENE.jar");
		actions = new int[2];
		actions[0]=20;
		actions[1]=20;
	}
	public static void generateGames(){
		Generate g = new Generate();
		g.generateGames();
	}
	public static void removeActions(){
		GamutModifier gm = new GamutModifier();
		HashMap<String,StrategyMap> strategyMaps = new HashMap<String,StrategyMap>();
		StrategyMap[] temp = new StrategyMap[2];
		//double[] fractions = {.1,.2,.3,.4,.5,.6,.7,.8,.9};
		double[] fractions = {.5, .6, .75, .9};
		for(int f = 0;f<fractions.length;f++){
			for(int i = 0;i<500;i++){
				gm.setGame(i+"");
				temp = gm.removeActions(fractions[f],1);
				strategyMaps.put("r" + fractions[f] + "-1-1-" + i, temp[0]);
				strategyMaps.put("r" + fractions[f] + "-2-1-" + i, temp[1]);
				temp = gm.removeActions(fractions[f],2);
				strategyMaps.put("r" + fractions[f] + "-1-2-" + i, temp[0]);
				strategyMaps.put("r" + fractions[f] + "-2-2-" + i, temp[1]);
			}
			for(int i = 2500;i<3000;i++){
				gm.setGame(i+"");
				temp = gm.removeActions(fractions[f],1);
				strategyMaps.put("r" + fractions[f] + "-1-1-" + i, temp[0]);
				strategyMaps.put("r" + fractions[f] + "-2-1-" + i, temp[1]);
				temp = gm.removeActions(fractions[f],2);
				strategyMaps.put("r" + fractions[f] + "-1-2-" + i, temp[0]);
				strategyMaps.put("r" + fractions[f] + "-2-2-" + i, temp[1]);
			}
		}
		try{
			PrintWriter pw = new PrintWriter(Parameters.GAME_FILES_PATH+"strategyMaps.csv","UTF-8");
			Collection<StrategyMap> strategies = strategyMaps.values();
			Iterator it = strategies.iterator();
			while (it.hasNext()) {pw.write(it.next().toString()+"\n");}
			pw.close();
		}catch (Exception e){e.printStackTrace();}
	}
	public static void bucket(){
		GamutModifier gm = new GamutModifier();
		for(int i = 0;i<500;i++){//zero sum
			gm.setGame(i+"");
			for(int b = 2;b<=10;b++)
				gm.bucket(b);
		}
		for(int i = 2500;i<3000;i++){//random
			gm.setGame(i+"");
			for(int b = 2;b<10;b++)
				gm.bucket(b);
		}
	}
	public static void cnag(){
		String desc = "control,no abstraction,generic random games";//typeOfGames variable used for generating games but games are already generated
		try{
			Tournament match = new Tournament(agentPaths,agentNames,desc,numberOfGames,actions,2500);
			match.saveInformation();
			match.setParams(0,0,0,0);
			match.run();
			match.wrapUp("cnag");
			System.out.println("completed cnag");
		}
		catch(Exception e){
			System.out.println("Error while running cnag "+e.toString());
			e.printStackTrace();
		}
	}
	public static void cnazs(){
		String desc = "control,no abstraction,zero-sum games";
		try{
			Tournament match = new Tournament(agentPaths,agentNames,desc,numberOfGames,actions,0);
			match.saveInformation();
			match.setParams(0,0,0,0);
			match.run();
			match.wrapUp("cnazs");
			System.out.println("completed cnazs");
		}
		catch(Exception e){
			System.out.println("Error while running cnazs "+e.toString());
			e.printStackTrace();
		}
	}
	public static void bsag(){
		String desc = "bucketing,same abs,generic random games";
		try{
			Tournament match = new Tournament(agentPaths,agentNames,desc,numberOfGames,actions,2500);
			match.saveInformation();
			for(int i=3;i<10;i+=2){
				System.out.println("Starting phase "+ i);
				match.setParams(1,1,i,i);
				match.run();
			}
			match.wrapUp("bsag");
			System.out.println("completed bsag");
		}
		catch(Exception e){
			System.out.println("Error while running bsag "+e.toString());
			e.printStackTrace();
		}
	}
	public static void bsazs(){
		String desc = "bucketing,same abs,zero sum";
		try{
			Tournament match = new Tournament(agentPaths,agentNames,desc,numberOfGames,actions,0);
			match.saveInformation();
			for(int i =3;i<10;i+=2){
				match.setParams(1,1,i,i);
				match.run();
			}
			match.wrapUp("bsazs");
			System.out.println("completed bsazs");
		}
		catch(Exception e){
			System.out.println("Error while running bsazs "+e.toString());
			e.printStackTrace();
		}
	}
	public static void bdag(){
		String desc = "bucketing,different abs,generic random games";
		try{
			Tournament match = new Tournament(agentPaths,agentNames,desc,numberOfGames,actions,2500);
			match.saveInformation();
			for(int i =2;i<9;i+=2){
				match.setParams(1,1,i,i+2);
				match.run();
			}
			match.wrapUp("dbag");
			System.out.println("completed bdag");
		}
		catch(Exception e){
			System.out.println("Error while running bdag "+e.toString());
			e.printStackTrace();
		}
	}
	public static void bdazs(){
		String desc = "bucketing,different abs,zero sum";
		try{
			Tournament match = new Tournament(agentPaths,agentNames,desc,numberOfGames,actions,0);
			match.saveInformation();
			for(int i =2;i<9;i+=2){
				match.setParams(1,1,i,i+2);
				match.run();
			}
			match.wrapUp("dbazs");
			System.out.println("completed bdazs");
		}
		catch(Exception e){
			System.out.println("Error while running bdazs "+e.toString());
			e.printStackTrace();
		}
	}
	public static void rrdrg(){
		String desc = "random removal,different removal,generic random games";
		try{
			Tournament match = new Tournament(agentPaths,agentNames,desc,numberOfGames,actions,2500);
			match.saveInformation();
			//double[] fractions = {.1,.2,.3,.4,.5,.6,.7,.8,.9};
			double[] fractions = {.5, .6, .75, .9};
			for(int i = 0;i<fractions.length;i++){
				match.setParams(2,2,fractions[i],fractions[i]);
				match.run();
			}
			match.wrapUp("rrdrg");
			System.out.println("completed rrdrg");
		}
		catch(Exception e){
			System.out.println("Error while running rrdrg "+e.toString());
			e.printStackTrace();
		}
	}
	public static void rrdrzs(){
		String desc = "random removal,different removal,zero sum games";
		try{
			Tournament match = new Tournament(agentPaths,agentNames,desc,numberOfGames,actions,0);
			match.saveInformation();
			//double[] fractions = {.1,.2,.3,.4,.5,.6,.7,.8,.9};
			double[] fractions = {.5, .6, .75, .9};
			for(int i = 0;i<fractions.length;i++){
				match.setParams(2,2,fractions[i],fractions[i]);
				match.run();
			}
			match.wrapUp("rrdrzs");
			System.out.println("completed rrdrzs");
		}
		catch(Exception e){
			System.out.println("Error while running rrdrzs "+e.toString());
			e.printStackTrace();
		}
	}
	/*public static void experiment8(){
        String desc = "Random removal 75%, different actions removed zero sum games";
        try{
            Tournament match = new Tournament(agentPaths,agentNames,desc,numberOfGames,actions,0);
            match.saveInformation();
            //double[] fractions = {.1,.2,.3,.4,.5,.6,.7,.8,.9};
            double[] fractions = {.75};
            for(int i = 0;i<fractions.length;i++){
                match.setParams(2,2,fractions[i],fractions[i]);
                match.run();
            }
            match.wrapUp();
            System.out.println("completed exp8");
        }
        catch(Exception e){
            System.out.println("Error while running exp8 "+e.toString());
            e.printStackTrace();
        }
    }
    public static void experiment9(){
        String desc = "Random removal, 50%, different actions removed zero sum games";
        try{
            Tournament match = new Tournament(agentPaths,agentNames,desc,numberOfGames,actions,0);
            match.saveInformation();
            //double[] fractions = {.1,.2,.3,.4,.5,.6,.7,.8,.9};
            double[] fractions = {.5};
            for(int i = 0;i<fractions.length;i++){
                match.setParams(2,2,fractions[i],fractions[i]);
                match.run();
            }
            match.wrapUp();
            System.out.println("completed exp9");
        }
        catch(Exception e){
            System.out.println("Error while running exp9 "+e.toString());
            e.printStackTrace();
        }
    }
    public static void experiment10(){
        String desc = "Random removal, 90%, different actions removed zero sum games";
        try{
            Tournament match = new Tournament(agentPaths,agentNames,desc,numberOfGames,actions,0);
            match.saveInformation();
            //double[] fractions = {.1,.2,.3,.4,.5,.6,.7,.8,.9};
            double[] fractions = {.9};
            for(int i = 0;i<fractions.length;i++){
                match.setParams(2,2,fractions[i],fractions[i]);
                match.run();
            }
            match.wrapUp();
            System.out.println("completed exp10");
        }
        catch(Exception e){
            System.out.println("Error while running exp10 "+e.toString());
            e.printStackTrace();
        }
    }
    public static void experiment11(){
        String desc = "Bucketed, one player with 5 buckets the other 6. zero sum games";//typeOfGames variable used for generating games but games are already generated
        try{
            Tournament match = new Tournament(agentPaths,agentNames,desc,numberOfGames,actions,0);
            match.saveInformation();
            match.setParams(1,1,5,6);
            match.run();
            match.wrapUp();
            System.out.println("completed exp11");
        }
        catch(Exception e){
            System.out.println("Error while running exp11 "+e.toString());
            e.printStackTrace();
        }
    }
    public static void experiment12(){
        String desc = "Bucketed, one player with 6 buckets the other 7. zero sum games";//typeOfGames variable used for generating games but games are already generated
        try{
            Tournament match = new Tournament(agentPaths,agentNames,desc,numberOfGames,actions,0);
            match.saveInformation();
            match.setParams(1,1,6,7);
            match.run();
            match.wrapUp();
            System.out.println("completed exp12");
        }
        catch(Exception e){
            System.out.println("Error while running exp12 "+e.toString());
            e.printStackTrace();
        }
    }
    public static void experiment13(){
        String desc = "Bucketed, one player with 8 buckets the other 9. zero sum games";//typeOfGames variable used for generating games but games are already generated
        try{
            Tournament match = new Tournament(agentPaths,agentNames,desc,numberOfGames,actions,0);
            match.saveInformation();
            match.setParams(1,1,8,9);
            match.run();
            match.wrapUp();
            System.out.println("completed exp13");
        }
        catch(Exception e){
            System.out.println("Error while running exp13 "+e.toString());
            e.printStackTrace();
        }
    }*/
	public static void loadRemovedStrategies(){
		StrategyHolder.getInstance();
	}

	public static void topN()
	{
		GamutModifier gm = new GamutModifier("GameZero"); // I think it's not correct. 
		gm.performNTopAbstraction(2);
	}


	
	public static void writePayoffsInFile()
	{
		try{
            PrintWriter pw = new PrintWriter(Parameters.GAME_FILES_PATH+"payoff.txt","UTF-8");
            
            
            GamutModifier gm = new GamutModifier("0");
            
            
            for(int i =0; i< gm.returnGame().getNumActions(0); i++)
            {
            	for(int j=0; j<gm.returnGame().getNumActions(1); j++)
            	{
            		int[] outcome = {i+1, j+1};
            		pw.write("  "+ gm.returnGame().getPayoff(outcome, 0));
            	}
            	pw.write("\n");
            	
            }
            
            pw.close();
		}
            catch(Exception ex)
            {
            	
            	
            }
		
		
		
	}
	
	
	
	public static void testDeviationItr()
	{
		
		
		int[] outcome = {3,2};
		int[] actions = {4, 4};
		
		DeviationIterator itr = new DeviationIterator(outcome, actions);
		
		
		while(itr.hasNext())
		{
			int[] devoutcome = itr.next();
			System.out.print("\n "+ devoutcome[0] + " "+ devoutcome[1]);
			
		}
		
	}
	
	

	public static void kMeansClustering(int numberofclusters)
	{




		/*	for(int c=0;c<Math.pow(2, 4); c++)
		{

			Logger.log("\n ++++++++++  Integer val++++++++++++: "+ c, false);
			for(int j=3; j>=0; j--)
			{




				if(j==3)
				{

					int v = Referee.getBitValue(c, j);

					if(v==1)
					{
						Referee.setRAND_POINTS_FROM_OBSERVATION(true);
						Referee.setRAND_ACTION_INIT_TO_CLUSTERS(false);
					}
					else if(v==0)
					{
						Referee.setRAND_POINTS_FROM_OBSERVATION(false);
						Referee.setRAND_ACTION_INIT_TO_CLUSTERS(true);

					}
				}







				if(j==2)
				{
					int v = Referee.getBitValue(c, j);

					if(v==1)
					{
						Referee.setDIST_METRIC_LINE(true);
						Referee.setDIST_METRIC_EUCLIDEAN(false);
					}
					else if(v==0)
					{
						Referee.setDIST_METRIC_LINE(false);
						Referee.setDIST_METRIC_EUCLIDEAN(true);

						Referee.setMAX_DIST(false);
						Referee.setSUM_DIST(false);


					}

				}

				if(j==1)
				{
					if(!Referee.isDIST_METRIC_EUCLIDEAN())
					{
						int v = Referee.getBitValue(c, j);

						if(v==1)
						{
							Referee.setSUM_DIST(true);
							Referee.setMAX_DIST(false);

						}
						else if(v==0)
						{

							Referee.setSUM_DIST(false);
							Referee.setMAX_DIST(true);

						}

					}
				}


				if(j==0)
				{
					int v= Referee.getBitValue(c, j);

					if(v==1)
					{
						Referee.setMAX_DELTA(true);
						Referee.setAVRG_DELTA(false);
					}
					else if(v==0)
					{
						Referee.setMAX_DELTA(false);
						Referee.setAVRG_DELTA(true);
					}
				}





			}


			if(Referee.isDIST_METRIC_EUCLIDEAN())
			{
				Referee.setMAX_DIST(false);
				Referee.setSUM_DIST(false);
			}
		 */




		/*
		 * make the experiment directory
		 */


		

		/*if(Referee.isRandPointsFromObservation())
		{
			Referee.experimentdir = "1";
		}
		else 
		{
			Referee.experimentdir = "0";
		}*/


		if(Referee.isDistMetricLine())
		{
			Referee.experimentdir = "1";
		}
		else 
		{
			Referee.experimentdir = "0";
		}

		if(Referee.MAX_DIST)
		{
			Referee.experimentdir += "1";
		}
		else 
		{
			Referee.experimentdir += "0";
		}


		if(Referee.MAX_DELTA)
		{
			Referee.experimentdir += "1";
		}
		else 
		{
			Referee.experimentdir += "0";
		}






		boolean isdircreated = false;
		
		File file = null;
		
		
		{
			 file = new File(Parameters.GAME_FILES_PATH+Referee.experimentdir);
		}

		
		if (!file.exists()) {
			if (file.mkdir()) {
				isdircreated = true;
				System.out.println("Directory is created!");
			} else {
				
				System.out.println("Failed to create directory!");
			}
		}
		else
		{
			/*try {
				FileUtils.deleteDirectory(file);
				file.mkdir();
				System.out.println("Directory is created!");
				isdircreated = true;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
		}





		if(isdircreated)
		{

			Logger.log("\n Clustering with: ", false);
			Logger.log("\n Initilization: ", false);
			Logger.log("\n RAND_POINTS_FROM_OBSERVATION : "+ Referee.isRandPointsFromObservation(), false);
			Logger.log("\n RAND_ACTION_INIT_TO_CLUSTERS() "+ Referee.isRandActionInitToClusters(),false);
			Logger.log("\n Distance Metric METRIC_LINE() "+ Referee.isDistMetricLine(), false );
			Logger.log("\n Distance Metric METRIC_EUCLIDEAN() "+ Referee.isDistMetricEuclidean(), false );
			Logger.log("\n Dist SUM_DIST() "+ Referee.isSumDist(), false);
			Logger.log("\n Dist MAX_DIST() "+ Referee.isMaxDist(), false);
			Logger.log("\n Delta MAX_DELTA() "+ Referee.isMaxDelta(), false);
			Logger.log("\n Delta AVRG_DELTA() "+ Referee.isAvrgDelta(), false);




			//	for(int gametype=0; gametype<2; gametype++)
			//{

			for(int clusternumber=numberofclusters; clusternumber>=2; clusternumber=clusternumber/2)
			{

				double[] sumdelta = {0,0,0};
				double[] sumepsilon = {0,0,0};
				final int ITERATION = 100;


				//for testing
				//int clusternumber = 4;
				//int gametype =0;



				/*
				 * reset time before iterations. 
				 */

				Referee.clustertime[0] = 0;
				Referee.clustertime[1] = 0;
				Referee.percremovedstrategy =0;


				for(int i=1; i<=ITERATION; i++)
				{


					double[][] res =  GamutModifier.clusteringAbstractionOldBothPlayer(clusternumber, i);
					
				
					
					



					//for testing
					//	double[][] res = GamutModifier.clusteringAbstraction(numberofclusters, i+1, 0);

					for(int j=0; j<3; j++)
					{
						sumdelta[j] = sumdelta[j] + res[j][0];
						sumepsilon[j] = sumepsilon[j] + res[j][1];
						Logger.log("\n Running Instance "+ i+ " player "+ j + " delta: "+ res[j][0]+ " epsilon: "+res[j][1], false);

					}



				}
				
				Referee.percremovedstrategy = (Referee.percremovedstrategy/ITERATION)*100;

				for(int j=0; j<3; j++)
				{
					sumdelta[j] = sumdelta[j]/ITERATION;
					sumepsilon[j] = sumepsilon[j]/ITERATION ;
					
					
					
					Logger.log("\n Player: "+j+ " final delta: "+ sumdelta[j]+  " Player: "+j+ " final epsilon: "+ sumepsilon[j], false);

				}



				try{
					PrintWriter pw = new PrintWriter(new FileOutputStream(new File(Parameters.GAME_FILES_PATH+Referee.experimentdir+"/"+"result"+".result"),true));

					for(int j=0; j<3; j++)
					{



						pw.append("\n "+sumdelta[j] + " "+ sumepsilon[j] + "  "+ Referee.clustertime[0]+ " "+Referee.percremovedstrategy);

						String x = " ";
						
						if(j==0)
						{
							x = "NE";
						}
						else if(j==1)
						{
							x = "SUbgame";
						}
						else if(j==1)
						{
							x = "Max Expected";
						}
						
						
						Logger.logit("\nFor clustering "+clusternumber+" and "+x+" profile, Final Average delta and epsilon "+sumdelta[j] + " "+ sumepsilon[j] + "  ");
					
					
					
					}
					pw.append("\n\n");
					pw.close();


				}
				catch(Exception e)
				{

				}




			} // for loop for cluster number


			//	}



			//	}






		} // if for directory creation
















	}
	

	public static void main (String[] args){
		//generateGames();
		//removeActions();
		//bucket();
		//  addAgents();
		//  loadRemovedStrategies();
		//cnag();
		//cnazs();
		//bsag();
		//bsazs();
		//  bdag();
		//  bdazs();
		//  rrdrg();
		//  rrdrzs();
		//topN();

		//GamutModifier.makeNoisyGame(0, .5, 256);
	//kMeansClustering(16); //two player only 0 and 1
		
	//	GamutModifier.testClusteringAbstraction(4);
	//	writePayoffsInFile();
	//	testDeviationItr();
		
		
		try 
		{
			//GameReductionBySubGame.testSubGameMethod();
			//GameReductionBySubGame.startGameSolving();
			
			
			//GameReductionBySubGame.deltaExperiment();
			//GameReductionBySubGame.LouvainVsKmean();
			GameReductionBySubGame.testSubGameSolverV3();
			//GameReductionBySubGame.clusterDistributionExperiment();
			//ClusteringForSubgame.testSubgameClustering();
			//SecurityGameAbstraction.wildlifeAbstraction();
			//SecurityGameAbstraction.testing1();
			//SecurityGameAbstraction.testingMMR();
			//LouvainClusteringActions.testLouvainClustering();
			
			
		} 
		catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	//	RegretLearner.startSolvingGameByRegret();
	//RegretLearner.doExPeriments();
	//	RegretClustering.doRegretClustering();

	}





	/**
	 * @param numberofclusters how many cluster you want
	 * @param player the player number, usually 0 or 1 for a two player game. 
	 */

}

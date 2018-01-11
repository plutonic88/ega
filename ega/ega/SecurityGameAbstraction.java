package ega;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import lpWrapper.LPSolverException;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import models.AttBoundStructure;
import models.PayoffStructure;
import models.SUQRAdversary;
import models.Target;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import src.tambe.usc.DOBSS.gameRepresentation.SSGPayoffs;
import src.tambe.usc.DOBSS.gameRepresentation.SSGSolution;
import src.tambe.usc.DOBSS.gameRepresentation.StructuredSecurityGame;
import src.tambe.usc.DOBSS.solvers.FastSecurityGameSolver;
import algorithms.MMR;


//import src.tambe.usc.DOBSS.solvers.FastSecurityGameSolver.TargetData;



public class SecurityGameAbstraction {


	public static double testing1(int[] numResources)
	{
		//int[] numResources = {5};
		long totalTimeOrigmai = 0;
		FastSecurityGameSolver fsgs = new FastSecurityGameSolver();
		//int[][] gamedata = createdummyData();
		int[][] gamedata = parseSecurityGameFile("inputr-0.100000.csv");
		convertToZeroSum(gamedata);
		StructuredSecurityGame samplegame = genStructuredSecurityGame(gamedata, 1, new int[]{numResources[0]});
		//SSGPayoffs[] payoffs = samplegame.getPayoffs();
		Long startTimeOrigami = System.currentTimeMillis();
		SSGSolution sampleSolutionOrigmai = fsgs.solveGame(samplegame);
		Long endTimeOrigami = System.currentTimeMillis();
		double coverageProb=0;
		sampleSolutionOrigmai.computeExpectedPayoffs(samplegame);

		System.out.println("Result: " + sampleSolutionOrigmai);

		long time = endTimeOrigami -startTimeOrigami;
		totalTimeOrigmai += time;
		//System.out.println("Total Time Origami: " + totalTimeOrigmai);
		//System.out.println("Average time origmai:" +avgTimeOrigami + "\n" );
		return sampleSolutionOrigmai.getDefenderPayoff();


	}

	public static void wildlifeAbstraction() throws Exception
	{
		int[] numResources = {5};
		testing1(numResources);
		int numCluster = 5;
		int[][] gamedata = parseSecurityGameFile("inputr-0.100000.csv");
		//int[][] dummydata = createdummyData();

		long totalTimeOrigmai = 0;
		FastSecurityGameSolver fsgs = new FastSecurityGameSolver();
		//int[][] gamedata = parseSecurityGameFile("inputr-0.100000.csv");
		StructuredSecurityGame samplegame = genStructuredSecurityGame(gamedata, 1, new int[]{numResources[0]});
		List<Integer>[] clusteredtargets = KmeanClustering.clusterTargets(numCluster, gamedata);

		StrategyMapping strmap = new StrategyMapping(clusteredtargets, numCluster, gamedata);
		int[][][] abstractgame = strmap.makeAbstractSecurityGame();
		//printAbstractSecurityGame(abstractgame);
		System.out.print("\n");
		strmap.printSecurityGameMapping();
		IntervalSecurityGame issg = IntervalSecurityGame.generateAbstractIntervalSecurityGame(abstractgame,numResources[0]);
		PolynomialIntervalSolver psolver = new PolynomialIntervalSolver();
		//psolver.init(issg);
		double [] coverage = psolver.solve(issg);
		if(!checkNormality(numResources[0], coverage))
		{
			throw new Exception("original Not normal");
		}
		//printSStrategy(coverage);
		System.out.println("Using abstractions : ");
		SSGSolution originalstr = buildOriginalSGStrategy(coverage, strmap);
		originalstr.computeExpectedPayoffs(samplegame);
		System.out.println("Result: " + originalstr);

		//printSStrategy(originalstr);


		//System.out.print("hi");
	}

	public static boolean checkNormality(int numResource, double[] coverage)
	{
		double sum = 0;
		for(double x: coverage)
		{
			sum += x;
		}
		//System.out.println("sum  : "+ sum);
		//sum = Math.ceil(sum);
		//System.out.println("after ceil sum  : "+ sum);
		if(sum==numResource)
			return true;
		else 
			return false;
	}

	public static void testingMMR() throws Exception
	{
		//int numPiece = 15;
		//int numPayoffs = 7;
		//int[][] gamedata = createdummyData();

		int numSamples = 1;
		//double interval = 8.0;
		boolean isZeroSum = true;
		int numTargets = 20;
		int numCluster = 5;
		int[] numResources = {5};
		int cov = 0;
		int payoffIndex = 0;
		double origexp = 0;
		double absexp = 0;
		int NUM_ITER = 1;
		for(int itr = 0; itr<NUM_ITER; itr++)
		{

			origexp += testing1(numResources);

			int[][] gamedata = parseSecurityGameFile("inputr-0.100000.csv");
			convertToZeroSum(gamedata);




			long totalTimeOrigmai = 0;
			FastSecurityGameSolver fsgs = new FastSecurityGameSolver();
			StructuredSecurityGame samplegame = genStructuredSecurityGame(gamedata, 1, new int[]{numResources[0]});
			List<Integer>[] clusteredtargets = KmeanClustering.clusterTargets(numCluster, gamedata);
			StrategyMapping strmap = new StrategyMapping(clusteredtargets, numCluster, gamedata);
			int[][][] abstractgame = strmap.makeAbstractSecurityGame();
			strmap.printSecurityGameMapping();
			//convertToZeroSum(abstractgame);
			printAbstractSecurityGame(abstractgame);
			//System.out.print("hi");
			try {
				lpWrapper.Configuration.loadLibrariesCplex();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//Random rand = new Random();
			System.gc();
			List<Target> targetList = new ArrayList<Target>();
			for(int target = 0; target < numCluster; target++)
			{	
				List<PayoffStructure> payoffList = null;
				double attRewardLB;
				double attPenaltyLB;
				double attRewardUB;
				double attPenaltyUB;
				attRewardLB = abstractgame[target][2][0];//payoff[target + 2 * numTargets];
				attRewardUB = abstractgame[target][2][1];//attRewardLB + intervalRand[target];
				attPenaltyLB = abstractgame[target][3][0];//payoff[target + 3 * numTargets] - interval;
				attPenaltyUB = abstractgame[target][3][1];//attPenaltyLB + intervalRand[target + numTargets];
				AttBoundStructure attBoundStructure = new AttBoundStructure(attRewardLB, attPenaltyLB, attRewardUB, attPenaltyUB);
				if(!isZeroSum)
				{
					payoffList = new ArrayList<PayoffStructure>();
					payoffList.add(new PayoffStructure(abstractgame[target][0][0], abstractgame[target][1][0], abstractgame[target][2][0], abstractgame[target][3][0]));
				}
				Target t = new Target(target, payoffList, attBoundStructure);
				targetList.add(t);
			}

			SUQRAdversary adversary = new SUQRAdversary(0, -9.85, 0.45, 0.32, 1.0);
			MMR mmr = new MMR(targetList, adversary, numResources[0], numSamples, isZeroSum);
			mmr.loadProblem();
			mmr.solve();
			mmr.deletePayoffConstraint();
			double [] coverage = mmr.getDefCov();

			//double[] coverage = mmr.getOptCov();
			if(!checkNormality(numResources[0], coverage))
			{
				//throw new Exception("abstract Not normal");
			}
			printSStrategy(coverage);
			System.out.println("Using abstractions : ");
			SSGSolution originalstr = buildOriginalSGStrategy(coverage, strmap);
			originalstr.computeExpectedPayoffs(samplegame);
			absexp += originalstr.getDefenderPayoff();

			System.out.println("Result: " + originalstr);

			mmr.end();
		}
		System.out.println(" original game Avg def payoff "+ origexp/NUM_ITER);
		System.out.println(" abstraction Avg def payoff "+ absexp/NUM_ITER);

	}

	private static void convertToZeroSum(int[][][] abstractgame) {

		for(int target = 0; target< abstractgame.length; target++)
		{
			abstractgame[target][0][0] = -abstractgame[target][3][0];
			abstractgame[target][0][1] = -abstractgame[target][3][1];

			abstractgame[target][1][0] = -abstractgame[target][2][0];
			abstractgame[target][1][1] = -abstractgame[target][2][1];


		}

	}

	/**
	 * converts the attacker's payoffs to opposite of defender
	 * @param gamedata
	 */
	public static void convertToZeroSum(int[][] gamedata)
	{
		for(int target = 0; target<gamedata.length; target++)
		{
			gamedata[target][2] = -(gamedata[target][1]);
			gamedata[target][3] = -(gamedata[target][0]);

		}
	}

	public static SSGSolution buildOriginalSGStrategy(double[] coverage, StrategyMapping strmap) {

		int numberoforigtargets = strmap.getSecuritygamedata().length;
		double[] originalstr = new double[numberoforigtargets];

		List<Integer>[] clusterfortargets = strmap.getClusterfortargets();

		for(int abstarget = 0; abstarget< coverage.length; abstarget++)
		{
			for(Integer origtarget: clusterfortargets[abstarget])
			{
				originalstr[origtarget] = coverage[abstarget]/clusterfortargets[abstarget].size();
			}
		}

		int numResource = 5;

		if(!checkNormality(numResource, originalstr))
		{
			//System.out.println(" Not mormal final cov");
		}



		SSGSolution solution = new SSGSolution(numberoforigtargets, 1);

		for (int t = 0; t < numberoforigtargets; t++) {
			solution.setProb(t, 0, originalstr[t]);
		}
		return solution;
	}


	private static void printSStrategy(double[] originalstr) {

		System.out.println();
		for(double x : originalstr)
		{
			System.out.print(x + " ");

		}
		System.out.println();

	}

	public static StructuredSecurityGame genStructuredSecurityGame(int[][] gamedata, int nDefenseTypes, int[] nDefenders)
	{
		StructuredSecurityGame ssg = new StructuredSecurityGame(gamedata.length, nDefenseTypes);
		ssg.setNDefenders(nDefenders);
		for (int target = 0; target < gamedata.length; target++) 
		{
			SSGPayoffs tmp = new SSGPayoffs(false);

			//-------------------------------------------
			//Attacker and Defender's covered payoff is set to zero here
			//-------------------------------------------
			//double r1 = Math.random() * -100;
			double r1=gamedata[target][2];
			double r3=gamedata[target][0];

			double r2 = gamedata[target][3];
			//double r3 = Math.random() * 100;
			double r4 = gamedata[target][1];

			// System.out.print("R2");
			//System.out.println( r2);

			//---------------------------------------------
			tmp.setAttackerCoveredPayoff(r1);
			tmp.setAttackerUncoveredPayoff(r2);
			tmp.setDefenderCoveredPayoff(r3);
			tmp.setDefenderUncoveredPayoff(r4);
			ssg.setPayoffs(target, tmp);
		}
		Random r = new Random();
		// first, assign at least 1 resource to each schedule (so there are no uncoverable schedules)
		for (int target = 0; target < gamedata.length; target++) 
		{
			ssg.setDefenseCapability(target, r.nextInt(nDefenseTypes), true);
		}

		// next, randomly add additional resource capabilities up to the density requested
		int cnt = gamedata.length;
		int max = (int)Math.round((double)(gamedata.length * nDefenseTypes) * 1.0d);
		while (cnt < max) 
		{
			int tmp1 = r.nextInt(gamedata.length);
			int tmp2 = r.nextInt(nDefenseTypes);
			if (ssg.getDefenseCapability(tmp1, tmp2)) continue;
			ssg.setDefenseCapability(tmp1, tmp2, true);
			cnt++;
		}
		return ssg;


	}

	public static int[][] createdummyData()
	{
		int[][] data = new int[20][4];
		int counter = 0;
		int val = -1;
		for(int i=0; i<20; i++)
		{

			//KmeanClustering.randInt(0, 10);
			if(counter==0)
			{
				val = KmeanClustering.randInt(0, 100);
			}
			//for(int j=0; j<4; j++)
			{
				data[i][0] = val;
				data[i][1] = -(val-20);
				data[i][2] = -data[i][1];
				data[i][3] = -data[i][0];

			}
			counter++;
			if(counter==4)
			{
				counter = 0;
			}

		}
		return data;
	}

	private static void printAbstractSecurityGame(int[][][] abstractgame) {

		for(int i=0; i<abstractgame.length; i++)
		{
			for(int j=0; j<4; j++)
			{
				System.out.print("[");
				for(int k=0; k<2; k++)
				{
					System.out.print(abstractgame[i][j][k]);
					if(k==0)
					{
						System.out.print(",");
					}
				}
				System.out.print("] ");
			}
			System.out.println();
		}

	}

	public static int[][] parseSecurityGameFile(String filename)
	{
		int[][] gamedata;
		File csvtrainData = new File("/Users/fake/Documents/workspace/ega/ega/resultedgames/wildlife/"+filename);
		try 
		{
			CSVParser parser = CSVParser.parse(csvtrainData, StandardCharsets.US_ASCII, CSVFormat.EXCEL);
			for (CSVRecord csvRecord : parser) 
			{
				int numberoftargets = csvRecord.size()/4;
				gamedata = new int[numberoftargets][4];
				Iterator<String> itr = csvRecord.iterator();
				int targetcounter = 0;
				int singlepayoffcounter = 0;
				while(itr.hasNext())
				{
					gamedata[targetcounter][singlepayoffcounter] = Integer.parseInt(itr.next());
					targetcounter++;
					//singlepayoffcounter++;
					if(targetcounter==numberoftargets)
					{
						targetcounter=0;
						singlepayoffcounter++;
						//targetcounter++;
					}
				}
				return gamedata;
			}

		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}

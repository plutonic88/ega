package mainFunctions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import lpWrapper.LPSolverException;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import models.AttBoundStructure;
import models.PayoffStructure;
import models.SUQRAdversary;
import models.Target;
import algorithms.MMR;
//import algorithms.MMRHeuristic;

public class Test_MMR {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws MatlabInvocationException 
	 * @throws MatlabConnectionException 
	 * @throws LPSolverException 
	 */
	public static void main(String[] args) throws IOException, LPSolverException, MatlabConnectionException, MatlabInvocationException {
		// TODO Auto-generated method stub
		try {
			lpWrapper.Configuration.loadLibrariesCplex();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int numTargets = 20;
		int numResources = 5;
		int numPiece = 15;
		int numPayoffs = 7;
		
		int numSamples = 1;
		double interval = 8.0;
		boolean isZeroSum = true;
//		String resultPath = "/Users/thanhnguyen/Documents/WORKS/UAV/JAVA/RESULTS";
//		String inputPath = "/Users/thanhnguyen/Documents/WORKS/UAV/JAVA/RESULTS/E1";
		
		int cov = 0;
		int payoffIndex = 0;
		Random rand = new Random();
//		System.out.println("Interval Size:");
		
//		System.out.println();
//		Random rand = new Random();
		
////		double bigInterva
		for(numTargets = 20; numTargets <= 20; numTargets += 10)
		{
			double[] intervalRand = new double[2 * numTargets];
		for(cov = 0; cov <= 0; cov += 2)
		{
			
			Vector<int[]> payoffs = loadData(numTargets, cov);
			for(payoffIndex = 0; payoffIndex < 1; payoffIndex++)
			{
				System.gc();
				for(int i = 0; i < 2 * numTargets; i++)
				{
					intervalRand[i] = rand.nextDouble() * interval;
				}
				int[] payoff = payoffs.get(payoffIndex);
				List<Target> targetList = new ArrayList<Target>();
				for(int id = 0; id < numTargets; id++)
				{	
					List<PayoffStructure> payoffList = null;
					double attRewardLB;
					double attPenaltyLB;
					double attRewardUB;
					double attPenaltyUB;
					attRewardLB = payoff[id + 2 * numTargets];
					attPenaltyLB = payoff[id + 3 * numTargets] - interval;
					attRewardUB = attRewardLB + intervalRand[id];
					attPenaltyUB = attPenaltyLB + intervalRand[id + numTargets];
					AttBoundStructure attBoundStructure = new AttBoundStructure(attRewardLB, attPenaltyLB, attRewardUB, attPenaltyUB);
					if(!isZeroSum)
					{
						payoffList = new ArrayList<PayoffStructure>();
						payoffList.add(new PayoffStructure(payoff[id], payoff[id + numTargets], -1, -1));
					}
					Target t = new Target(id, payoffList, attBoundStructure);
					targetList.add(t);
				}
//				double w1 = -15 * rand.nextDouble();
//				double w2 = rand.nextDouble();
//				double w3 = rand.nextDouble();
//				double[] w = loadAdversary(inputPath + "/" + numTargets + "T" + numResources + "R/MMRAdv" + cov + "_" + payoffIndex + ".csv");
//				SUQRAdversary adversary = new SUQRAdversary(0, w[0], w[1], w[2], 1.0);
//				SUQRAdversary adversary = new SUQRAdversary(0, w1, w2, w3, 1.0);
//				SUQRAdversary adversary = new SUQRAdversary(0, -10.0, 1.0, 1.0, 1.0);
				SUQRAdversary adversary = new SUQRAdversary(0, -9.85, 0.45, 0.32, 1.0);
				MMR mmr = new MMR(targetList, adversary, numResources, numSamples, isZeroSum);
				mmr.loadProblem();
				mmr.solve();
				mmr.deletePayoffConstraint();
				//mmr.saveResults(folderPath, runtimeFileName, regretFileName, strategyFileName, convergeFileName);
				// Add constraint
//				double[] coeffs1 = new double[numTargets];
//			    for(int t = 0; t < numTargets; t++)
//			    	coeffs1[t] =  2 * (t % 2) - 1;
//			    
//			    double[] coeffs2 = new double[numTargets];
//			    for(int t = 0; t < numTargets; t++)
//			    	coeffs2[t] =  0.0;
//			    coeffs2[0] = 1.0;
//			    coeffs2[1] = -5.0;
//			    
//			    mmr.addConstraint(coeffs1, 0);
//			    mmr.addConstraint(coeffs2, 0);
//			    mmr.solve();
//				String runtimeFileName = "/" + numTargets + "T" + numResources + "R/MMRRuntime" + cov + "_" + payoffIndex + ".csv";
//				String regretFileName = "/" + numTargets + "T" + numResources + "R/MMRRegret" + cov + "_" + payoffIndex + ".csv";
//				String convergeFileName = "/" + numTargets + "T" + numResources + "R/MMRConverge" + cov + "_" + payoffIndex + ".csv";
//				String strategyFileName = "/" + numTargets + "T" + numResources + "R/MMRStrategy" + cov + "_" + payoffIndex + ".csv";
//				String intervalFileName = "/" + numTargets + "T" + numResources + "R/MMRInterval" + cov + "_" + payoffIndex + ".csv"; 
//				save(resultPath, intervalFileName, intervalRand);
//				String advPath = resultPath + "/" + numTargets + "T" + numResources + "R/MMRAdv" + cov + "_" + payoffIndex + ".csv";
				
//				mmr.saveResults(resultPath, runtimeFileName, regretFileName, strategyFileName, convergeFileName);
////				saveAdversary(advPath, w1, w2, w3);
////				saveAdversary(advPath, w[0], w[1], w[2]);
//				
//				String sampleFilePath = resultPath + "/" + numTargets + "T" + numResources + "R/MMRSampleRuntime" + cov + "_" + payoffIndex + ".csv";
//				mmr.saveSampleRuntime(sampleFilePath);
				mmr.end();
			}
			payoffs.clear();
		}
		}
	}
	
	public static void save(String resultPath, String intervalFileName, double[] intervalRand)
	{
		FileOutputStream intervalFileStream;
		try {
			intervalFileStream = new FileOutputStream(resultPath + intervalFileName);
			PrintStream intervalStream = new PrintStream(intervalFileStream);
			for(int i = 0; i < intervalRand.length; i++)
			{
				intervalStream.println(intervalRand[i]);
			}
			intervalStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static double[] loadAdversary(String filePath)
	{
		double[] w = new double[3];
		FileInputStream in = null;
		FileReader fin = null;
		Scanner src = null;
		try {
			fin = new FileReader(filePath);
			src = new Scanner(fin);
			String[] data = src.nextLine().split(",");
			w[0] = Double.parseDouble(data[0]);
			w[1] = Double.parseDouble(data[1]);
			w[2] = Double.parseDouble(data[2]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Couldn't open file for reading.");
			e.printStackTrace();
		}
		src.close();
		return w;
	}
	public static Vector<int[]> loadData(int numTargets, int cov)
	{
		String payoffFile;
		if (cov < 10)
			payoffFile = "/Users/thanhnguyen/Documents/WORKS/UAV/GAMEGENERATION/" + numTargets + "Target/inputr-0." + cov + "00000.csv";
		else
			payoffFile = "/Users/thanhnguyen/Documents/WORKS/UAV/GAMEGENERATION/" + numTargets + "Target/inputr-1.000000.csv";
		Vector<int[]> payoffs = new Vector<int[]>();
		FileInputStream in = null;
		FileReader fin = null;
		Scanner src = null;
		try {
			fin = new FileReader(payoffFile);
			src = new Scanner(fin);
			while (src.hasNext()) {
				String line = src.nextLine();
				String[] values = line.split(",");
				int[] payoff = new int[4 * numTargets];
				for (int i = 0; i < 4 * numTargets; i++)
					payoff[i] = Integer.parseInt(values[i]);
				payoffs.add(payoff);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Couldn't open file for reading.");
			e.printStackTrace();
		}
		src.close();
		return payoffs;
	}
}

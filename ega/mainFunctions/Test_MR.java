package mainFunctions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

import algorithms.MR;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import models.AttBoundStructure;
import models.PayoffStructure;
import models.SUQRAdversary;
import models.Target;

public class Test_MR {

	/**
	 * @param args
	 * @throws MatlabInvocationException 
	 * @throws MatlabConnectionException 
	 */
	public static void main(String[] args) throws MatlabConnectionException, MatlabInvocationException {
		// TODO Auto-generated method stub
		int cov = 0;
		int payoffIndex = 0;
		int numIter = 40;
		int numTargets = 20;
		int numResources = 5;
		int numPayoffs = 7;
		double interval = 4.0;
		SUQRAdversary adversary = new SUQRAdversary(0, -2.0, 0.5, 0.1, 1.0);
		for(numTargets = 20; numTargets <= 20; numTargets += 10)
		for(cov = 0; cov <= 0; cov += 2)
		{
			Vector<int[]> payoffs = loadData(numTargets, cov);
			Vector<double[]> strategies = loadStrategy(numTargets, numResources, cov);
			double[] trueObj = new double[numPayoffs];
			for(payoffIndex = 0; payoffIndex < 1; payoffIndex++)
			{
				int[] payoff = payoffs.get(payoffIndex);
				double[] strategy = strategies.get(payoffIndex);
				
				// Create target list
				List<Target> targetList = new ArrayList<Target>();
				for(int id = 0; id < numTargets; id++)
				{	
					double attRewardLB = payoff[id + 2 * numTargets];
					double attPenaltyLB = payoff[id + 3 * numTargets] - interval;
					double attRewardUB = attRewardLB + interval;
					double attPenaltyUB = attPenaltyLB + interval;
					AttBoundStructure attBoundStructure = new AttBoundStructure(attRewardLB, attPenaltyLB, attRewardUB, attPenaltyUB);
					Target t = new Target(id, null, attBoundStructure);
					targetList.add(t);
				}
				Map<Target, Double> defCov = new HashMap<Target, Double>();

				for(Target t : targetList)
				{
					defCov.put(t, strategy[t.id]);
				}
				
				 // Call MR object 
				MR mr = new MR(targetList, defCov, adversary, numResources, numIter);
				mr.solve();
				System.out.println("\nMax Regret:" + mr.getMaxRegret());
				// Add constraints
			    double[] coeffs1 = new double[numTargets];
			    for(int t = 0; t < numTargets; t++)
			    	coeffs1[t] =  2 * (t % 2) - 1;
			    
			    double[] coeffs2 = new double[numTargets];
			    for(int t = 0; t < numTargets; t++)
			    	coeffs2[t] =  0.0;
			    coeffs2[0] = 1.0;
			    coeffs2[1] = -5.0;
			    
				mr.addConstraint(coeffs1, 0.0);
				mr.addConstraint(coeffs2, 0.0);
				
				// Solve 
				mr.solve();
				System.out.println("\nMax Regret:" + mr.getMaxRegret());
				
				
				// Add more constraint
				double[] coeffs3 = new double[numTargets];
			    for(int t = 0; t < numTargets; t++)
			    	coeffs3[t] =  0.0;
			    coeffs3[0] = 2.0;
			    coeffs3[1] = -3.0;
			    
				mr.addConstraint(coeffs3, 0.1);
				mr.solve();
				System.out.println("\nMax Regret:" + mr.getMaxRegret());
				
				mr.end();
			}
//			// Saving results
//			String obj_name = "/Users/thanhnguyen/Documents/WORKS/UAV/JAVA/RESULTS/PerfectMMR/" + numTargets + "T" + numResources + 
//					"R/trueObj" + cov + ".csv";
//			try{
//				FileOutputStream objFileStream = new FileOutputStream(obj_name);
//				PrintStream objStream = new PrintStream(objFileStream);
//				for(int j = 0; j < numPayoffs; j++)
//				{
//					objStream.println(trueObj[j]);
//				}
//				objStream.close();
//			}catch (FileNotFoundException e1)
//			{
//				e1.printStackTrace();
//			}
		}
	}
	public static Vector<double[]> loadStrategy(int nTargets, int nRes, int cov)
	{
		Vector<double[]> defCov = new Vector<double[]>();
		String def_name = "/Users/thanhnguyen/Documents/WORKS/UAV/JAVA/RESULTS/PerfectMMR/" + nTargets + "T" + nRes + 
				"R/strategy" + cov + ".csv";
		FileInputStream in = null;
		FileReader fin = null;
		Scanner src = null;
//		int idx = 0;
		try {
			fin = new FileReader(def_name);
			src = new Scanner(fin);
			while (src.hasNext()) {
				String line = src.nextLine();
				String[] values = line.split(",");
				double[] temp = new double[nTargets];
				for (int i = 0; i < nTargets; i++)
					temp[i] = Double.parseDouble(values[i]);
				defCov.add(temp);
//				idx++;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Couldn't open file for reading.");
			e.printStackTrace();
		}
		src.close();
		return defCov;
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

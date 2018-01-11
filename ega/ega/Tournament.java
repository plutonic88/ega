//package utep.ais.gametheory.main;
package ega;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import ega.games.EmpiricalMatrixGame;
import ega.games.GameUtils;
import ega.games.MatrixGame;
import ega.games.MixedStrategy;
import ega.games.OutcomeDistribution;
import ega.parsers.GamutParser;
import ega.solvers.SolverUtils;

public class Tournament implements Runnable {
	private int amountOfGames;
	private int[] actions;
	private String gameType;
	private ArrayList<String> agentPaths;
	private ArrayList<String> agentNames;
	private String agentOne;
	private String agentTwo;
	private String agentOneName;
	private String agentTwoName;
	private ArrayList<String> gameNames;
	private ArrayList<EmpiricalMatrixGame> arrayGames;
	private ArrayList<int[]> outcomes;
	private ArrayList<double[]> payoffs;

    private ArrayList<MixedStrategy[]> strategies;
    private int agent1Wins;
	private int agent2Wins;
	private int gamesTied;
	private double agent1AveragePayoff;
	private double agent2AveragePayoff;
	private double agent1WorstCasePayoff;
	private double agent2WorstCasePayoff;
	private double agent1BestCasePayoff;
	private double agent2BestCasePayoff;
	private double agent1AverageEpsilon;
	private double agent2AverageEpsilon;
	private double agent1StandardDeviation;
	private double agent2StandardDeviation;
	private SaveResults saveResults;
	private DateFormat dateFormat;
	private Date date;
    private int p1state;
    private int p2state;
    private double p1param;
    private double p2param;
    private int start;
    private ArrayList<Double> p1eps;
    private ArrayList<Double> p2eps;
    private double[][][] avgPayoff;
    private double[] maxPayoff;
    private double[][] stability;
    private double p1exp;
    private double p2exp;
    private double[][] exp;

    private ArrayList<String> rp1Name;
    private ArrayList<String> rp2Name;
    private ArrayList<String> rp1Level;
    private ArrayList<String> rp2Level;
    private ArrayList<String> rp1Payoff;
    private ArrayList<String> rp2Payoff;
    private ArrayList<String> rp1Exp;
    private ArrayList<String> rp2Exp;
    private ArrayList<String> rp1Var;
    private ArrayList<String> rp2Var;

	public Tournament(ArrayList<String> agentPaths, ArrayList<String> agentNames, String gameType,
                      int amountOfGames, int[] actions,int start) throws InterruptedException {
		this.agentPaths = new ArrayList<String>();
		this.agentNames = new ArrayList<String>();
		this.gameNames = new ArrayList<String>();
		this.arrayGames = new ArrayList<EmpiricalMatrixGame>();
		for (String element : agentPaths) {
			this.agentPaths.add(element);
		}
		for (String element : agentNames) {
			this.agentNames.add(element);
		}
		this.gameType = gameType;
		this.amountOfGames = amountOfGames;
		this.actions = actions.clone();
		this.saveResults = new SaveResults();
        strategies = new ArrayList<MixedStrategy[]>();
        this.start = start;
        for (int i = start; i < start+amountOfGames; i++) {
			gameNames.add("" + i);
            addGame(i);
		}
        p1eps = new ArrayList<Double>();
        p2eps = new ArrayList<Double>();
        avgPayoff = new double[agentNames.size()][agentNames.size()][2];
        maxPayoff = new double[agentNames.size()];
        stability = new double[agentNames.size()][agentNames.size()];
        exp = new double[agentNames.size()][agentNames.size()];
        rp1Name = new ArrayList<String>();
        rp2Name = new ArrayList<String>();
        rp1Level = new ArrayList<String>();
        rp2Level = new ArrayList<String>();
        rp1Payoff = new ArrayList<String>();
        rp2Payoff = new ArrayList<String>();
        rp1Exp = new ArrayList<String>();
        rp2Exp = new ArrayList<String>();
        rp1Var = new ArrayList<String>();
        rp2Var = new ArrayList<String>();

	}
    private void initializeVariables(int agent1Index, int agent2Index) {
        agent1Wins = 0;
        agent2Wins = 0;
        gamesTied = 0;
        agent1AveragePayoff = 0;
        agent2AveragePayoff = 0;
        agent1WorstCasePayoff = 0;
        agent2WorstCasePayoff = 0;
        agent1BestCasePayoff = 0;
        agent2BestCasePayoff = 0;
        agent1AverageEpsilon = 0;
        agent2AverageEpsilon = 0;
        agent1StandardDeviation = 0;
        agent2StandardDeviation = 0;
        agentOne = agentPaths.get(agent1Index);
        agentTwo = agentPaths.get(agent2Index);
        agentOneName = agentNames.get(agent1Index).substring(0,	agentNames.get(agent1Index).length() - 4);
        agentTwoName = agentNames.get(agent2Index).substring(0, agentNames.get(agent2Index).length() - 4);
        outcomes = new ArrayList<int[]>();
        payoffs = new ArrayList<double[]>();
        strategies = new ArrayList<MixedStrategy[]>();
        p1eps = new ArrayList<Double>();
        p2eps = new ArrayList<Double>();
        p1exp = 0;
        p2exp = 0;

        File log = new File(Parameters.GAME_FILES_PATH+"epsilon.txt");
        try{
            PrintWriter pw = new PrintWriter(new FileWriter(log, false));
            pw.write("");
            pw.close();
        }catch(Exception e){e.printStackTrace();}
    }

	public void startTournament() throws InterruptedException {
        dateFormat= new SimpleDateFormat("yyyyMMdd-HH-mm-ss");
	    date = new Date();
		System.out.println("=============================================\n");
		System.out.println("Date: " + dateFormat.format(date));
		System.out.println("Experiment Description: " + gameType);
		System.out.println("Amount of Games: " + amountOfGames);
		System.out.println("Actions Player 1: " + actions[0]);
		System.out.println("Actions Player 2: " + actions[1]);
        double percent=0.0;
        String decimalPlaces ="";
		for (int agent1Index = 0; agent1Index < agentPaths.size(); agent1Index++) {
			for (int agent2Index = agent1Index; agent2Index < agentPaths.size(); agent2Index++) {
				initializeVariables(agent1Index, agent2Index);//reset for new agents
				for (int i = 0; i < gameNames.size(); i++) {
                    percent = (i*1.0)/(1.0*gameNames.size());
                    decimalPlaces = ""+percent;
                    if(decimalPlaces.length()==3){//ie "0.1"
                        date = new Date();
                        System.out.println(dateFormat.format(date)+"\t"+((int)(percent*100))+"% complete");
                    }
                    getStrategies(i);
				}
                System.out.println(dateFormat.format(date)+"\t100% complete");
			    comparePayoffs();
                getStandardDeviation();
				calculateAverageEpsilon();
                displayResults();
				saveResults();
                avgPayoff[agent1Index][agent2Index][0]=agent1AveragePayoff;
                avgPayoff[agent1Index][agent2Index][1]=agent2AveragePayoff;
                stability[agent1Index][agent2Index]=agent1AveragePayoff;
                stability[agent2Index][agent1Index]=agent2AveragePayoff;
                exp[agent1Index][agent2Index]=p1exp;
                exp[agent2Index][agent1Index]=p2exp;

                rp1Name.add(agentOneName);
                rp2Name.add(agentTwoName);
                rp1Level.add(""+p1param);
                rp2Level.add(""+p2param);
                rp1Payoff.add(""+agent1AveragePayoff);
                rp2Payoff.add(""+agent2AveragePayoff);
                rp1Exp.add(""+p1exp);
                rp2Exp.add(""+p2exp);
                rp1Var.add(""+(agent1StandardDeviation*agent1StandardDeviation));
                rp2Var.add(""+(agent2StandardDeviation*agent2StandardDeviation));
			}
		}
        summary();
	}

    public void setParams(int p1state, int p2state, double p1param, double p2param){
        this.p1state = p1state;
        this.p2state = p2state;
        this.p1param = p1param;
        this.p2param = p2param;
    }

    public void wrapUp(String name){
        // Save the time when the tournament ends
        dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		date = new Date();
		saveResults.write("Date: " + dateFormat.format(date));
		saveResults.close();
        try{
            PrintWriter out  = new PrintWriter(Parameters.RESULTS_PATH + "SummaryResults_" +name+ ".csv","UTF-8");
            String s = gameType;
            out.println(s);
            s = "P1 Name";
            for(int i = 0; i < rp1Name.size(); i++)
                s = s+","+rp1Name.get(i);
            out.println(s);
            s = "P2 Name";
            for(int i = 0; i < rp2Name.size(); i++)
                s = s+","+rp2Name.get(i);
            out.println(s);
            s = "P1 Level";
            for(int i = 0; i < rp1Level.size(); i++)
                s = s+","+rp1Level.get(i);
            out.println(s);
            s = "P2 Level";
            for(int i = 0; i < rp2Level.size(); i++)
                s = s+","+rp2Level.get(i);
            out.println(s);
            s = "P1 Payoff";
            for(int i = 0; i < rp1Payoff.size(); i++)
                s = s+","+rp1Payoff.get(i);
            out.println(s);
            s = "P2 Payoff";
            for(int i = 0; i < rp2Payoff.size(); i++)
                s = s+","+rp2Payoff.get(i);
            out.println(s);
            s = "P1 Exploit";
            for(int i = 0; i < rp1Exp.size(); i++)
                s = s+","+rp1Exp.get(i);
            out.println(s);
            s = "P2 Exploit";
            for(int i = 0; i < rp2Exp.size(); i++)
                s = s+","+rp2Exp.get(i);
            out.println(s);
            s = "P1 Var";
            for(int i = 0; i < rp1Var.size(); i++)
                s = s+","+rp1Var.get(i);
            out.println(s);
            s = "P2 Var";
            for(int i = 0; i < rp2Var.size(); i++)
                s = s+","+rp2Var.get(i);
            out.println(s);
            out.close();

        }catch(Exception e){e.printStackTrace();}

    }

	private void addGame(int numberOfGame) {
        EmpiricalMatrixGame game = new EmpiricalMatrixGame(GamutParser.readGamutGame(Parameters.GAME_FILES_PATH+
				+ numberOfGame + Parameters.GAMUT_GAME_EXTENSION));
		arrayGames.add(game);
	}

    private void getStrategies (int numberOfGame)throws InterruptedException {
        StrategyHolder sh = StrategyHolder.getInstance();
        MixedStrategy[] strats = new MixedStrategy[2];
		AgentsThread agentsThread;
		Thread thread;
        int player = 1;
        StrategyMap sm;
        int tries = 0;
        do{
		    // RUN THREAD OF FIRST AGENT
            if(p1state < 2)
    		    agentsThread = new AgentsThread(agentOne, agentOneName, gameNames.get(numberOfGame), player ,p1state,p1param);
            else
                agentsThread = new AgentsThread(agentOne, agentOneName, gameNames.get(numberOfGame), player ,p1state,p1param,1);
            thread = new Thread(agentsThread);
            thread.start();
            thread.join(); // Wait for the Thread to return
            if(p1state < 2)
                strats[0]=agentsThread.getStrategy();
            else{
                sm = sh.getMapping("r"+p1param+"-"+player+"-1-"+gameNames.get(numberOfGame));
                strats[0] = sm.getStrategy(agentsThread.getStrategy());
            }
            tries++;
        }while(!strats[0].isValid() && tries<3);
        player = 2;tries=0;
            do{
            // RUN THREAD OF SECOND AGENT
            if(p2state < 2)
                agentsThread = new AgentsThread(agentTwo, agentTwoName,	gameNames.get(numberOfGame), player,p2state,p2param);
            else
                agentsThread = new AgentsThread(agentTwo, agentTwoName,	gameNames.get(numberOfGame), player,p2state,p2param,2);
            //agentsThread = new AgentsThread(agentTwo, agentTwoName,	gameNames.get(numberOfGame), player,p2state,p2param);
            thread = new Thread(agentsThread);
            thread.start();
            thread.join();
            if(p2state < 2)
                strats[1]=agentsThread.getStrategy();
            else{
                sm = sh.getMapping("r"+p2param+"-"+player+"-2-"+gameNames.get(numberOfGame));
                strats[1] = sm.getStrategy(agentsThread.getStrategy());
            }
            tries++;
        }while(!strats[1].isValid()&&tries<3);
        List<MixedStrategy> list = new ArrayList<MixedStrategy>();
        list.add(strats[0]);
        list.add(strats[1]);
        MatrixGame g = new MatrixGame(GamutParser.readGamutGame(Parameters.GAME_FILES_PATH+ gameNames.get(numberOfGame)+Parameters.GAMUT_GAME_EXTENSION));
        OutcomeDistribution distro = new OutcomeDistribution(list);
        double[]  payoff = SolverUtils.computeOutcomePayoffs(g, distro);
        payoffs.add(payoff);
        p1exp += GameUtils.computeExploitability(g,strats[0],0);
        p2exp += GameUtils.computeExploitability(g,strats[1],1);

        //swap and play again
        tries = 0;
        do{
            if(p1state < 2)
                agentsThread = new AgentsThread(agentOne, agentOneName, gameNames.get(numberOfGame), player ,p1state,p1param);
            else
                agentsThread = new AgentsThread(agentOne, agentOneName, gameNames.get(numberOfGame), player ,p1state,p1param,1);
            thread = new Thread(agentsThread);
            thread.start();
            thread.join(); // Wait for the Thread to return
            if(p1state < 2)
                strats[0]=agentsThread.getStrategy();
            else{
                sm = sh.getMapping("r"+p1param+"-"+player+"-1-"+gameNames.get(numberOfGame));
                strats[0] = sm.getStrategy(agentsThread.getStrategy());
            }
            tries++;
        }while(!strats[0].isValid() && tries <3);
        player = 1;tries = 0;
        do{
            if(p2state < 2)
                agentsThread = new AgentsThread(agentTwo, agentTwoName,	gameNames.get(numberOfGame), player,p2state,p2param);
            else
                agentsThread = new AgentsThread(agentTwo, agentTwoName,	gameNames.get(numberOfGame), player,p2state,p2param,2);
            thread = new Thread(agentsThread);
            thread.start();
            thread.join();
            if(p2state < 2)
                strats[1]=agentsThread.getStrategy();
            else{
                sm = sh.getMapping("r"+p2param+"-"+player+"-2-"+gameNames.get(numberOfGame));
                strats[1] = sm.getStrategy(agentsThread.getStrategy());
            }
            tries++;
        }while(!strats[1].isValid()&&tries<3);
        list = new ArrayList<MixedStrategy>();
        list.add(strats[1]);
        list.add(strats[0]);
        g = new MatrixGame(GamutParser.readGamutGame(Parameters.GAME_FILES_PATH+ gameNames.get(numberOfGame)+Parameters.GAMUT_GAME_EXTENSION));
        distro = new OutcomeDistribution(list);
        payoff = SolverUtils.computeOutcomePayoffs(g, distro);
        double temp = payoff[1];
        payoff[1] = payoff[0];
        payoff[0] = temp;
        payoffs.add(payoff);
        p1exp += GameUtils.computeExploitability(g,strats[1],1);
        p2exp += GameUtils.computeExploitability(g,strats[0],0);
    }

	private void comparePayoffs() {
        agent1WorstCasePayoff = payoffs.get(0)[0];
        agent2WorstCasePayoff = payoffs.get(0)[1];
        agent1BestCasePayoff = payoffs.get(0)[0];
        agent2BestCasePayoff = payoffs.get(0)[1];
        for(int i =0;i <payoffs.size(); i++){
            double[] payoff = payoffs.get(i);
            if (payoff[0] > payoff[1]) {
                agent1Wins++;
            } else if (payoff[1] > payoff[0]) {
                agent2Wins++;
            } else {
                gamesTied++;
            }
            agent1AveragePayoff += payoff[0];
            agent2AveragePayoff += payoff[1];
            if (payoff[0] < agent1WorstCasePayoff) {
                agent1WorstCasePayoff = payoff[0];
            }
            if (payoff[1] < agent2WorstCasePayoff) {
                agent2WorstCasePayoff = payoff[1];
            }
            if (payoff[0] > agent1BestCasePayoff) {
                agent1BestCasePayoff = payoff[0];
            }
            if (payoff[1] > agent2BestCasePayoff) {
                agent2BestCasePayoff = payoff[1];
            }
        }
        agent1AveragePayoff /= (amountOfGames*2);
		agent2AveragePayoff /= amountOfGames*2;
        p1exp /= (amountOfGames*2);
        p2exp /= (amountOfGames*2);
	}

    private void calculateAverageEpsilon(){
        try{
            File log = new File(Parameters.GAME_FILES_PATH+"epsilon.txt");
            Scanner scanner = new Scanner(log);
            double e1sum = 0;
            double e2sum = 0;
            Scanner scan;
            if(scanner.hasNext()){
                scan = new Scanner(scanner.nextLine());
                while (scan.hasNext()){
                    e1sum+=Double.parseDouble(scan.next());
                    if(scan.hasNext())
                        e2sum+=Double.parseDouble(scan.next());
                    //swap
                    if(scan.hasNext())
                        e2sum+=Double.parseDouble(scan.next());
                    if(scan.hasNext())
                        e1sum+=Double.parseDouble(scan.next());
                }
                agent1AverageEpsilon = e1sum / (gameNames.size()*2);
                agent2AverageEpsilon = e1sum / (gameNames.size()*2);
            }
        }catch(Exception e){e.printStackTrace();}

    }

	private void getStandardDeviation() {
        for (int i = 0; i < payoffs.size(); i++) {
            double[] payoff = payoffs.get(i);
            agent1StandardDeviation += Math.pow((payoff[0] - agent1AveragePayoff),2);
            agent2StandardDeviation += Math.pow((payoff[1] - agent2AveragePayoff),2);
        }
        agent1StandardDeviation /= (payoffs.size() - 1);
        agent2StandardDeviation /= (payoffs.size() - 1);
        agent1StandardDeviation = Math.sqrt(agent1StandardDeviation);
        agent2StandardDeviation = Math.sqrt(agent2StandardDeviation);
	}

    private void summary(){
        String s = "Average Payoffs\n\t";
        for(int i = 0;i<agentNames.size();i++)
            s = s+agentNames.get(i)+"\t";
        s = s+"\n";
        System.out.println();
        for(int i =0;i<avgPayoff.length;i++){
            s = s+agentNames.get(i)+"\t";
            for(int j = 0;j<avgPayoff[0].length;j++){
                s = s+avgPayoff[i][j][0]+","+avgPayoff[i][j][1]+"\t";
            }
            s = s+ "\n";
        }
        System.out.println(s);
        saveResults.write(s);
        double[] overallAverage = new double[agentNames.size()];
        for(int i = 0;i < avgPayoff.length;i++){
            maxPayoff[i] = avgPayoff[i][i][0];
            for(int j = 0;j<avgPayoff[0].length;j++){
                if(maxPayoff[i]<avgPayoff[i][j][0])
                    maxPayoff[i]=avgPayoff[i][j][0];
                overallAverage[i] += avgPayoff[i][j][0];
            }
            for(int j = 0;j<avgPayoff.length;j++){
                if(maxPayoff[i]<avgPayoff[j][i][1])
                    maxPayoff[i]=avgPayoff[j][i][1];
                overallAverage[i] += avgPayoff[j][i][1];
            }
        }
        s="Max Average Payoffs\n";
        for(int i = 0;i<agentNames.size();i++){
            s = s+agentNames.get(i)+"\t"+maxPayoff[i]+"\n";
        }
        System.out.println(s);
        saveResults.write(s);
        s="Overall Average\n";
        for(int i=0;i<overallAverage.length;i++)
            overallAverage[i] = overallAverage[i]/(agentNames.size()+1);
        for(int i =0; i<overallAverage.length;i++){
            s = s+agentNames.get(i)+"\t"+overallAverage[i]+"\n";
        }
        System.out.println(s);
        saveResults.write(s);
        double[] stab = getStability(stability);
        s="Stability\n";
        for(int i =0;i<stab.length;i++){
            s = s+agentNames.get(i)+"\t"+stab[i]+"\n";
        }
        System.out.println(s);
        saveResults.write(s);
        s="Average Exploit\n";
        for(int i =0;i<exp.length;i++){
            double t = 0;
            for(int j =0;j<exp[0].length;j++)
                t+=exp[i][j];
            s = s+agentNames.get(i)+"\t"+(t/(1.0*exp.length))+"\n";
        }
        System.out.println(s);
        saveResults.write(s);
    }
    public double[] getStability(double[][] solverPayoffs) {
        double[] stabilities = new double[solverPayoffs.length];
        Arrays.fill(stabilities, Double.NEGATIVE_INFINITY);
        for (int strat = 0;  strat < solverPayoffs.length; strat++) {
          double base = solverPayoffs[strat][strat];
          for (int row = 0; row < solverPayoffs.length; row++) {
            if (row == strat) continue;
            double btd = solverPayoffs[row][strat] - base;
            stabilities[strat] = Math.max(stabilities[strat], btd);
          }
        }
        return stabilities;
      }


	private void displayResults() {
		System.out.println("=============================================");
        System.out.println(agentOneName.toUpperCase() + "(P1) Vs. "	+ agentTwoName.toUpperCase() + "(P2)");
        System.out.println("P1 State "+p1state+" P2 State "+p2state);
        System.out.println("P1 Param "+p1param+" P2 Param "+p2param);
		System.out.println("Games Won by " + agentOneName + "(P1) : "+ agent1Wins);
		System.out.println("Games Won by " + agentTwoName + "(P2) : "+ agent2Wins);
		System.out.println("Games Tied: " + gamesTied);
		System.out.println("Average Payoff " + agentOneName + "(P1) : "	+ agent1AveragePayoff);
		System.out.println("Average Payoff " + agentTwoName + "(P2) : "	+ agent2AveragePayoff);
		System.out.println("Average Epsilon " + agentOneName+ "(P1) : " + agent1AverageEpsilon);
		System.out.println("Average Epsilon " + agentTwoName+ "(P2) : " + agent2AverageEpsilon);
		System.out.println("Worst-Case Payoff " + agentOneName + "(P1) : " + agent1WorstCasePayoff);
		System.out.println("Worst-Case Payoff " + agentTwoName + "(P2) : " + agent2WorstCasePayoff);
		System.out.println("Best-Case Payoff " + agentOneName + "(P1) : "+ agent1BestCasePayoff);
		System.out.println("Best-Case Payoff " + agentTwoName + "(P2) : "+ agent2BestCasePayoff);
        System.out.println("Average Exploitability " + agentOneName + "(P1) : "+ p1exp);
		System.out.println("Average Exploitability " + agentTwoName + "(P2) : "+ p2exp);
		System.out.println("Variance " + agentOneName + "(P1) : "+ agent1StandardDeviation);
		System.out.println("Variance " + agentTwoName + "(P2) : "+ agent2StandardDeviation+"\n");
	}

	public void saveResults() {
		saveResults.write("=============================================");
        saveResults.write(agentOneName.toUpperCase() + "(P1) Vs. "+ agentTwoName.toUpperCase() + "(P2)");
        saveResults.write("P1 State "+p1state+" P2 State "+p2state);
        saveResults.write("P1 Param "+p1param+" P2 Param "+p2param);
		saveResults.write("Games Won by " + agentOneName + "(P1) : "+ agent1Wins);
		saveResults.write("Games Won by " + agentTwoName + "(P2) : "+ agent2Wins);
		saveResults.write("Games Tied: " + gamesTied);
		saveResults.write("Average Payoff " + agentOneName + "(P1) : "+ agent1AveragePayoff);
		saveResults.write("Average Payoff " + agentTwoName + "(P2) : "+ agent2AveragePayoff);
		saveResults.write("Average Epsilon " + agentOneName + "(P1) : " + agent1AverageEpsilon);
		saveResults.write("Average Epsilon " + agentTwoName + "(P2) : " + agent2AverageEpsilon);
		saveResults.write("Worst-Case Payoff " + agentOneName + "(P1) : "+ agent1WorstCasePayoff);
		saveResults.write("Worst-Case Payoff " + agentTwoName + "(P2) : "+ agent2WorstCasePayoff);
		saveResults.write("Best-Case Payoff " + agentOneName + "(P1) : "+ agent1BestCasePayoff);
		saveResults.write("Best-Case Payoff " + agentTwoName + "(P2) : "+ agent2BestCasePayoff);
        saveResults.write("Average Exploitability " + agentOneName + "(P1) : "+ p1exp);
		saveResults.write("Average Exploitability " + agentTwoName + "(P2) : "+ p2exp);
		saveResults.write("Variance " + agentOneName + "(P1) : "+ agent1StandardDeviation);
		saveResults.write("Variance " + agentTwoName + "(P2) : "+ agent2StandardDeviation+"\n");
	}

	public void saveInformation() {
		dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		date = new Date();
		saveResults.write("Round-Robin Tournament");
		saveResults.write("=============================================\n");
		saveResults.write("Date: " + dateFormat.format(date));
		saveResults.write("Experiment Description: " + gameType);
		saveResults.write("Amount of Games: " + amountOfGames);
		saveResults.write("Actions Player 1: " + actions[0]);
		saveResults.write("Actions Player 2: " + actions[1]);
		for (int i = 1; i <= agentNames.size(); i++)
			saveResults.write("Agent "+ i+ ": "+ agentNames.get(i-1).substring(0, agentNames.get(i-1).length()- 4));
		saveResults.write("\n");
	}

	public void run() {
		try {startTournament();}
        catch (InterruptedException e) {e.printStackTrace();}
	}
}
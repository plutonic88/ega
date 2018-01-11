package ega;

import static ega.Parameters.GAME_FILES_PATH;

import java.util.ArrayList;

import ega.games.MatrixGame;
import ega.games.OutcomeDistribution;
import ega.observers.GameObserver;
import ega.observers.NoiselessObserver;
import ega.parsers.GamutParser;
import ega.solvers.IncrementalBestFirstSearch;
import ega.solvers.IncrementalBestResponseDynamics;
import ega.solvers.IncrementalGameOutcomePredictor;
import ega.solvers.IncrementalRandomSampling;
import ega.solvers.IncrementalSubgameAnalysis;
import ega.solvers.SolverUtils;
import ega.solvers.DeviationOrdering.AvePayoffDeviationTestOrder;

/**
 * Created by IntelliJ IDEA.
 * User: ckiekint
 * Date: Apr 5, 2008
 * Time: 4:45:47 AM
 */
public class EquilibriumFindingExperiments {

  public static void test() {
    ArrayList<IncrementalGameOutcomePredictor> solvers = new ArrayList<IncrementalGameOutcomePredictor>();
    ArrayList<String> names = new ArrayList<String>();

//    IncrementalRandomSampling irs = new IncrementalRandomSampling();
//    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.STABILITY_RANDOM);
//    solvers.add(irs);
//    names.add("Random Sampling: STABILITY_RANDOM");

    IncrementalRandomSampling irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.STABILITY_MAX_AVE_PAYOFF);
    solvers.add(irs);
    names.add("Random Sampling: STABILITY_MAX_AVE_PAYOFF");

    IncrementalBestFirstSearch bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.STABILITY_MIN_EPSILON);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.RANDOM);
    solvers.add(bfs);
    names.add("BFS: Random dev ordering");

    bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.STABILITY_MIN_EPSILON);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) bfs.getDeviationTestOrder()).setPosInitialValue();
    solvers.add(bfs);
    names.add("BFS: ave payoff pos; 0 min samples");

    bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.STABILITY_MIN_EPSILON);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) bfs.getDeviationTestOrder()).setPosInitialValue();
    ((AvePayoffDeviationTestOrder) bfs.getDeviationTestOrder()).setMinTotalSamplesPerAction(2.0);
    solvers.add(bfs);
    names.add("BFS: ave payoff pos; 2.0 min samples");

    bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.STABILITY_MIN_EPSILON);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) bfs.getDeviationTestOrder()).setPosInitialValue();
    ((AvePayoffDeviationTestOrder) bfs.getDeviationTestOrder()).setMinTotalSamplesPerAction(4.0);
    solvers.add(bfs);
    names.add("BFS: ave payoff pos; 4.0 min samples");

    IncrementalBestResponseDynamics brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) brd.getDeviationTestOrder()).setPosInitialValue();
    brd.setAspirationLevel(Double.POSITIVE_INFINITY);
    solvers.add(brd);
    names.add("BRD: ave payoff pos; infinite aspiriation");

    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.RANDOM);
    brd.setAspirationLevel(0.05);
    brd.setMinDeviationsTested(10);
    solvers.add(brd);
    names.add("BRD: random ordering; 0.05 aspiriation; min 10 tested");

    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) brd.getDeviationTestOrder()).setPosInitialValue();
    brd.setAspirationLevel(0.05);
    brd.setMinDeviationsTested(10);
    solvers.add(brd);
    names.add("BRD: ave payoff pos; 0.05 aspiriation; min 10 tested");

    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) brd.getDeviationTestOrder()).setPosInitialValue();
    ((AvePayoffDeviationTestOrder) brd.getDeviationTestOrder()).setMinTotalSamplesPerAction(2.0);
    brd.setAspirationLevel(0.05);
    brd.setMinDeviationsTested(10);
    solvers.add(brd);
    names.add("BRD: ave payoff pos; 0.05 aspiriation; min 10 tested; 2.0 min samples");

    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) brd.getDeviationTestOrder()).setPosInitialValue();
    ((AvePayoffDeviationTestOrder) brd.getDeviationTestOrder()).setMinTotalSamplesPerAction(4.0);
    brd.setAspirationLevel(0.05);
    brd.setMinDeviationsTested(10);
    solvers.add(brd);
    names.add("BRD: ave payoff pos; 0.05 aspiriation; min 10 tested; 4.0 min samples");


    String gameClass;
    int numGames, samplesPerGame, nProfiles;
    int[] bounds;

    gameClass = "RandomLEG_25_25";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {25, 50, 100, 200, 625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "fg_2_1_5_1_5_random";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {25, 50, 100, 200, 625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "Supermodular_25_25";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {25, 50, 100, 200, 625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "CongestionGame_31_31";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 961;
    bounds = new int[] {25, 75, 150, 300, 961};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "random_25_25";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {25, 50, 100, 200, 625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "RandomLEG_100_100";
    numGames = 100;
    samplesPerGame = 2;
    nProfiles = 10000;
    bounds = new int[] {100, 500, 1000, 2000};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "fg_2_1_10_1_10_random";
    numGames = 100;
    samplesPerGame = 2;
    nProfiles = 10000;
    bounds = new int[] {100, 500, 1000, 2000};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "fg_2_1_25_1_4_random";
    numGames = 100;
    samplesPerGame = 2;
    nProfiles = 10000;
    bounds = new int[] {100, 500, 1000, 2000};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "fg_2_1_4_1_25_random";
    numGames = 100;
    samplesPerGame = 2;
    nProfiles = 10000;
    bounds = new int[] {100, 500, 1000, 2000};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "Supermodular_100_100";
    numGames = 100;
    samplesPerGame = 2;
    nProfiles = 10000;
    bounds = new int[] {100, 500, 1000, 2000};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "random_100_100";
    numGames = 100;
    samplesPerGame = 2;
    nProfiles = 10000;
    bounds = new int[] {100, 500, 1000, 2000};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

  }

  public static void testNewISA1() {
    ArrayList<IncrementalGameOutcomePredictor> solvers = new ArrayList<IncrementalGameOutcomePredictor>();
    ArrayList<String> names = new ArrayList<String>();

    IncrementalSubgameAnalysis isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.getLes().setLambda(1000d);
    isa.setInitialSubgameSize(1);
    isa.setMaxSubgameIncreaseRate(1);
    solvers.add(isa);
    names.add("ISA: SUBGAME LES LES 1000 1 1");

    isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.getLes().setLambda(1000d);
    isa.setInitialSubgameSize(1);
    isa.setMaxSubgameIncreaseRate(2);
    solvers.add(isa);
    names.add("ISA: SUBGAME LES LES 1000 1 2");

    isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.getLes().setLambda(1000d);
    isa.setInitialSubgameSize(1);
    isa.setMaxSubgameIncreaseRate(3);
    solvers.add(isa);
    names.add("ISA: SUBGAME LES LES 1000 1 3");

    isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.getLes().setLambda(1000d);
    isa.setInitialSubgameSize(2);
    isa.setMaxSubgameIncreaseRate(2);
    solvers.add(isa);
    names.add("ISA: SUBGAME LES LES 1000 2 2");

    isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.getLes().setLambda(1000d);
    isa.setInitialSubgameSize(2);
    isa.setMaxSubgameIncreaseRate(3);
    solvers.add(isa);
    names.add("ISA: SUBGAME LES LES 1000 2 3");


    String gameClass;
    int numGames, samplesPerGame, nProfiles;
    int[] bounds;

    gameClass = "RandomLEG_25_25";
    numGames = 500;
    samplesPerGame = 1;
    nProfiles = 625;
    bounds = new int[] {25, 50, 100, 200, 625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "fg_2_1_5_1_5_random";
    numGames = 500;
    samplesPerGame = 1;
    nProfiles = 625;
    bounds = new int[] {25, 50, 100, 200, 625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "Supermodular_25_25";
    numGames = 500;
    samplesPerGame = 1;
    nProfiles = 625;
    bounds = new int[] {25, 50, 100, 200, 625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "CongestionGame_31_31";
    numGames = 500;
    samplesPerGame = 1;
    nProfiles = 961;
    bounds = new int[] {25, 75, 150, 300, 961};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "random_25_25";
    numGames = 500;
    samplesPerGame = 1;
    nProfiles = 625;
    bounds = new int[] {25, 50, 100, 200, 625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
  }

  public static void testISA() {
    ArrayList<IncrementalGameOutcomePredictor> solvers = new ArrayList<IncrementalGameOutcomePredictor>();
    ArrayList<String> names = new ArrayList<String>();

    IncrementalSubgameAnalysis isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.getLes().setLambda(1000d);
    isa.setInitialSubgameSize(1);
    isa.setMaxSubgameIncreaseRate(1);
    solvers.add(isa);
    names.add("ISA: SUBGAME LES LES 1000 1 1");

    isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.getLes().setLambda(1000d);
    isa.setInitialSubgameSize(1);
    isa.setMaxSubgameIncreaseRate(2);
    solvers.add(isa);
    names.add("ISA: SUBGAME LES LES 1000 1 2");

    isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.getLes().setLambda(1000d);
    isa.setInitialSubgameSize(1);
    isa.setMaxSubgameIncreaseRate(4);
    solvers.add(isa);
    names.add("ISA: SUBGAME LES LES 1000 1 4");

    isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.getLes().setLambda(1000d);
    isa.setInitialSubgameSize(2);
    isa.setMaxSubgameIncreaseRate(2);
    solvers.add(isa);
    names.add("ISA: SUBGAME LES LES 1000 2 2");

    isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.getLes().setLambda(1000d);
    isa.setInitialSubgameSize(2);
    isa.setMaxSubgameIncreaseRate(4);
    solvers.add(isa);
    names.add("ISA: SUBGAME LES LES 1000 2 4");

    String gameClass;
    int numGames, samplesPerGame, nProfiles;
    int[] bounds;

    gameClass = "RandomLEG_100_100";
    numGames = 100;
    samplesPerGame = 2;
    nProfiles = 10000;
    bounds = new int[] {10000};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "Supermodular_100_100";
    numGames = 100;
    samplesPerGame = 2;
    nProfiles = 10000;
    bounds = new int[] {10000};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "fg_2_1_10_1_10_random";
    numGames = 100;
    samplesPerGame = 2;
    nProfiles = 10000;
    bounds = new int[] {10000};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "fg_2_1_25_1_4_random";
    numGames = 100;
    samplesPerGame = 2;
    nProfiles = 10000;
    bounds = new int[] {10000};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "fg_2_1_4_1_25_random";
    numGames = 100;
    samplesPerGame = 2;
    nProfiles = 10000;
    bounds = new int[] {10000};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "random_100_100";
    numGames = 100;
    samplesPerGame = 2;
    nProfiles = 10000;
    bounds = new int[] {10000};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "covariant_-1_100_100";
    numGames = 100;
    samplesPerGame = 2;
    nProfiles = 10000;
    bounds = new int[] {10000};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

//    gameClass = "RandomLEG_25_25";
//    numGames = 500;
//    samplesPerGame = 2;
//    nProfiles = 625;
//    bounds = new int[] {625};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "random_25_25";
//    numGames = 500;
//    samplesPerGame = 2;
//    nProfiles = 625;
//    bounds = new int[] {625};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "covariant_-1_25_25";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 625;
//    bounds = new int[] {625};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "fg_2_1_5_1_5_random";
//    numGames = 500;
//    samplesPerGame = 2;
//    nProfiles = 625;
//    bounds = new int[] {625};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "Supermodular_25_25";
//    numGames = 500;
//    samplesPerGame = 2;
//    nProfiles = 625;
//    bounds = new int[] {625};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "CongestionGame_31_31";
//    numGames = 500;
//    samplesPerGame = 2;
//    nProfiles = 961;
//    bounds = new int[] {961};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
  }


  public static void testISA_covariance() {
    ArrayList<IncrementalGameOutcomePredictor> solvers = new ArrayList<IncrementalGameOutcomePredictor>();
    ArrayList<String> names = new ArrayList<String>();

    IncrementalSubgameAnalysis isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.getLes().setLambda(1000d);
    isa.setInitialSubgameSize(10);
    isa.setMaxSubgameIncreaseRate(5);
    solvers.add(isa);
    names.add("ISA: SUBGAME LES LES 1000 10 5");

//    isa = new IncrementalSubgameAnalysis();
//    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
//    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
//    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
//    isa.getLes().setLambda(1000d);
//    isa.setInitialSubgameSize(1);
//    isa.setMaxSubgameIncreaseRate(2);
//    solvers.add(isa);
//    names.add("ISA: SUBGAME LES LES 1000 1 2");
//
//    isa = new IncrementalSubgameAnalysis();
//    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
//    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
//    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
//    isa.getLes().setLambda(1000d);
//    isa.setInitialSubgameSize(1);
//    isa.setMaxSubgameIncreaseRate(4);
//    solvers.add(isa);
//    names.add("ISA: SUBGAME LES LES 1000 1 4");
//
//    isa = new IncrementalSubgameAnalysis();
//    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
//    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
//    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
//    isa.getLes().setLambda(1000d);
//    isa.setInitialSubgameSize(2);
//    isa.setMaxSubgameIncreaseRate(2);
//    solvers.add(isa);
//    names.add("ISA: SUBGAME LES LES 1000 2 2");
//
//    isa = new IncrementalSubgameAnalysis();
//    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
//    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
//    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
//    isa.getLes().setLambda(1000d);
//    isa.setInitialSubgameSize(2);
//    isa.setMaxSubgameIncreaseRate(4);
//    solvers.add(isa);
//    names.add("ISA: SUBGAME LES LES 1000 2 4");

    String gameClass;
    int numGames, samplesPerGame, nProfiles;
    int[] bounds;

    gameClass = "covariant_-1_100_100";
    numGames = 100;
    samplesPerGame = 2;
    nProfiles = 10000;
    bounds = new int[] {10000};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
  }


  public static void testBRD() {
    ArrayList<IncrementalGameOutcomePredictor> solvers = new ArrayList<IncrementalGameOutcomePredictor>();
    ArrayList<String> names = new ArrayList<String>();

    IncrementalBestResponseDynamics brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) brd.getDeviationTestOrder()).setPosInitialValue();
    brd.setAspirationLevel(Double.POSITIVE_INFINITY);
    solvers.add(brd);
    names.add("BRD: ave payoff pos; infinite aspiriation");

    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.RANDOM);
    brd.setAspirationLevel(0);
    brd.setMinDeviationsTested(0);
    solvers.add(brd);
    names.add("BRD: random ordering; 0 aspiriation; min 0 tested");

    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.RANDOM);
    brd.setAspirationLevel(0.1);
    brd.setMinDeviationsTested(0);
    solvers.add(brd);
    names.add("BRD: random ordering; 0.1 aspiriation; min 0 tested");

    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.RANDOM);
    brd.setAspirationLevel(0);
    brd.setMinDeviationsTested(10);
    solvers.add(brd);
    names.add("BRD: random ordering; 0 aspiriation; min 10 tested");

    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.RANDOM);
    brd.setAspirationLevel(0.1);
    brd.setMinDeviationsTested(10);
    solvers.add(brd);
    names.add("BRD: random ordering; 0.1 aspiriation; min 10 tested");

    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) brd.getDeviationTestOrder()).setPosInitialValue();
    brd.setAspirationLevel(0);
    brd.setMinDeviationsTested(0);
    solvers.add(brd);
    names.add("BRD: ave payoff pos; 0 aspiriation; min 0 tested; 0 min samples");

    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) brd.getDeviationTestOrder()).setPosInitialValue();
    brd.setAspirationLevel(0.1);
    brd.setMinDeviationsTested(10);
    solvers.add(brd);
    names.add("BRD: ave payoff pos; 0.1 aspiriation; min 10 tested; 0 min samples");

    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) brd.getDeviationTestOrder()).setPosInitialValue();
    ((AvePayoffDeviationTestOrder) brd.getDeviationTestOrder()).setMinTotalSamplesPerAction(5.0);
    brd.setAspirationLevel(0);
    brd.setMinDeviationsTested(0);
    solvers.add(brd);
    names.add("BRD: ave payoff pos; 0 aspiriation; min 0 tested; 0 min samples; 5 min samples");

    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) brd.getDeviationTestOrder()).setPosInitialValue();
    ((AvePayoffDeviationTestOrder) brd.getDeviationTestOrder()).setMinTotalSamplesPerAction(5.0);
    brd.setAspirationLevel(0.1);
    brd.setMinDeviationsTested(10);
    solvers.add(brd);
    names.add("BRD: ave payoff pos; 0.1 aspiriation; min 10 tested; 0 min samples; 5 min samples");


    String gameClass;
    int numGames, samplesPerGame, nProfiles;
    int[] bounds;

//    gameClass = "RandomLEG_100_100";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "random_100_100";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "covariant_-1_100_100";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "Supermodular_100_100";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "fg_2_1_10_1_10_random";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "fg_2_1_25_1_4_random";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "fg_2_1_4_1_25_random";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "RandomLEG_25_25";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "random_25_25";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "covariant_-1_25_25";
    numGames = 100;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "fg_2_1_5_1_5_random";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "Supermodular_25_25";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "CongestionGame_31_31";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 961;
    bounds = new int[] {961};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
  }

  public static void testBFS() {
    ArrayList<IncrementalGameOutcomePredictor> solvers = new ArrayList<IncrementalGameOutcomePredictor>();
    ArrayList<String> names = new ArrayList<String>();

    IncrementalBestFirstSearch bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.STABILITY_MIN_EPSILON);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.RANDOM);
    solvers.add(bfs);
    names.add("BFS: Random dev ordering");

    bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.STABILITY_MIN_EPSILON);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) bfs.getDeviationTestOrder()).setPosInitialValue();
    solvers.add(bfs);
    names.add("BFS: ave payoff pos; 0 min samples");

    bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.STABILITY_MIN_EPSILON);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) bfs.getDeviationTestOrder()).setPosInitialValue();
    ((AvePayoffDeviationTestOrder) bfs.getDeviationTestOrder()).setMinTotalSamplesPerAction(5.0);
    solvers.add(bfs);
    names.add("BFS: ave payoff pos; 5.0 min samples");

    bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.STABILITY_MIN_EPSILON);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) bfs.getDeviationTestOrder()).setPosInitialValue();
    ((AvePayoffDeviationTestOrder) bfs.getDeviationTestOrder()).setMinTotalSamplesPerAction(10.0);
    solvers.add(bfs);
    names.add("BFS: ave payoff pos; 10.0 min samples");

    String gameClass;
    int numGames, samplesPerGame, nProfiles;
    int[] bounds;

//    gameClass = "RandomLEG_100_100";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "random_100_100";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "covariant_-1_100_100";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "Supermodular_100_100";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "fg_2_1_10_1_10_random";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "fg_2_1_25_1_4_random";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "fg_2_1_4_1_25_random";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "RandomLEG_25_25";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "random_25_25";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "covariant_-1_25_25";
    numGames = 100;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);


    gameClass = "fg_2_1_5_1_5_random";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "Supermodular_25_25";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "CongestionGame_31_31";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 961;
    bounds = new int[] {961};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
  }

  public static void testRS() {
    ArrayList<IncrementalGameOutcomePredictor> solvers = new ArrayList<IncrementalGameOutcomePredictor>();
    ArrayList<String> names = new ArrayList<String>();

    IncrementalRandomSampling irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.STABILITY_RANDOM);
    solvers.add(irs);
    names.add("Random Sampling: STABILITY_RANDOM");

    irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.STABILITY_MIN_EPSILON);
    solvers.add(irs);
    names.add("Random Sampling: STABILITY_MIN_EPSILON");

    irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.STABILITY_MAX_AVE_PAYOFF);
    solvers.add(irs);
    names.add("Random Sampling: STABILITY_MAX_AVE_PAYOFF");

    irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.STABILITY_MAX_MIN_PAYOFF);
    solvers.add(irs);
    names.add("Random Sampling: STABILITY_MAX_MIN_PAYOFF");


    String gameClass;
    int numGames, samplesPerGame, nProfiles;
    int[] bounds;

//    gameClass = "RandomLEG_100_100";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "random_100_100";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "covariant_-1_100_100";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "Supermodular_100_100";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "fg_2_1_10_1_10_random";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "fg_2_1_25_1_4_random";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
//
//    gameClass = "fg_2_1_4_1_25_random";
//    numGames = 100;
//    samplesPerGame = 2;
//    nProfiles = 10000;
//    bounds = new int[] {10000};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "RandomLEG_25_25";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "random_25_25";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "covariant_-1_25_25";
    numGames = 100;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);


    gameClass = "fg_2_1_5_1_5_random";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "Supermodular_25_25";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "CongestionGame_31_31";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 961;
    bounds = new int[] {961};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
  }


  public static void Mix_Mix1() {
    ArrayList<IncrementalGameOutcomePredictor> solvers = new ArrayList<IncrementalGameOutcomePredictor>();
    ArrayList<String> names = new ArrayList<String>();

    IncrementalRandomSampling irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.STABILITY_RANDOM);
    solvers.add(irs);
    names.add("Random Sampling: STABILITY_RANDOM");

    irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.STABILITY_MAX_AVE_PAYOFF);
    solvers.add(irs);
    names.add("Random Sampling: STABILITY_MAX_AVE_PAYOFF");

    IncrementalBestResponseDynamics brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.RANDOM);
    brd.setAspirationLevel(Double.POSITIVE_INFINITY);
    solvers.add(brd);
    names.add("BRD: random deviations; infinite aspiriation");

    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) brd.getDeviationTestOrder()).setNegInitialValue();
    brd.setAspirationLevel(Double.POSITIVE_INFINITY);
    solvers.add(brd);
    names.add("BRD: best ave payoff; infinite aspiriation");

    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.RANDOM);
    brd.setAspirationLevel(0);
    solvers.add(brd);
    names.add("BRD: random; zero aspiriation");

    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) brd.getDeviationTestOrder()).setNegInitialValue();
    brd.setAspirationLevel(0);
    solvers.add(brd);
    names.add("BRD: best ave payoff; zero aspiration");

    IncrementalBestFirstSearch bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.STABILITY_RANDOM);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_RANDOM);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.RANDOM);
    solvers.add(bfs);
    names.add("BFS: STABILITY_RANDOM; Random dev ordering");

    bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.STABILITY_RANDOM);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_RANDOM);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) bfs.getDeviationTestOrder()).setPosInitialValue();
    solvers.add(bfs);
    names.add("BFS: STABILITY_RANDOM; AVE PAYOFF dev ordering");

    bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.STABILITY_MAX_AVE_PAYOFF);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MAX_AVE_PAYOFF);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.BEST_AVE_PAYOFF);
    ((AvePayoffDeviationTestOrder) bfs.getDeviationTestOrder()).setPosInitialValue();
    solvers.add(bfs);
    names.add("BFS: STABILITY_MAX_AVE_PAYOFF; AVE PAYOFF dev ordering");

    String gameClass;
    int numGames, samplesPerGame, nProfiles;
    int[] bounds;

    gameClass = "random_25_25";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {100, 200, 300, 500, 625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "Supermodular_25_25";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {100, 200, 300, 500, 625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "RandomLEG_25_25";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {100, 200, 300, 500, 625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "fg_2_1_5_1_5_random";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 625;
    bounds = new int[] {100, 200, 300, 500, 625};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    gameClass = "CongestionGame_31_31";
    numGames = 500;
    samplesPerGame = 2;
    nProfiles = 961;
    bounds = new int[] {100, 200, 400, 600, 961};
    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);

    //need to screen out bad (2x2) games from compact class..
//    gameClass = "Compact_25_25";
//    numGames = 500;
//    samplesPerGame = 2;
//    nProfiles = 625;
//    bounds = new int[] {100, 200, 300, 500, 625};
//    runExperiment(gameClass, numGames, samplesPerGame, bounds, nProfiles, solvers, names);
  }


  private static void runExperiment(String gameClass, int numGames, int samplesPerGame, int[] bounds, int nProfiles,
                                    ArrayList<IncrementalGameOutcomePredictor> solvers,
                                    ArrayList<String> names) {

    String path = GAME_FILES_PATH + gameClass + "/";
    String extension = ".gamut";
    int numSamples = numGames * samplesPerGame;

    ArrayList<double[]> stabilities = new ArrayList<double[]>();
    for (IncrementalGameOutcomePredictor solver : solvers) {
      stabilities.add(new double[bounds.length]);
    }
    double[] samplesToEquilibrium = new double[solvers.size()];
    double[] samplesToEquilibriumWithUnconfirmed = new double[solvers.size()];
    int[] noEquilibriumFound = new int[solvers.size()];

    for (int sample = 0; sample < numGames; sample++) {
      MatrixGame game = GamutParser.readGamutGame(path + sample + extension);
      GameObserver go = new NoiselessObserver(game);
      go.setDefaultPayoff(0.5d);

      //System.out.println("Analyzing class: " + gameClass + " gameID: " + sample);

      for (int solverIndex = 0; solverIndex < solvers.size(); solverIndex++) {
        IncrementalGameOutcomePredictor solver = solvers.get(solverIndex);
        for (int n = 0; n < samplesPerGame; n++) {
          go.reset();
          solver.initialize(go);
          for (int i = 0; i < bounds.length; i++) {
            go.setBound(bounds[i]);
            OutcomeDistribution od = solver.incrementalPredictOutcome(go);
            double stability = SolverUtils.computeOutcomeStability(game, od);
            stabilities.get(solverIndex)[i] += stability;
            System.out.println("Sample");
          }
          int samplesToConfirm = solver.getSamplesToConfirmEquilibrium();
          if (samplesToConfirm > 0) {
            samplesToEquilibrium[solverIndex] += samplesToConfirm;
            samplesToEquilibriumWithUnconfirmed[solverIndex] += samplesToConfirm;
          } else {
            noEquilibriumFound[solverIndex]++;
            samplesToEquilibriumWithUnconfirmed[solverIndex] += bounds[bounds.length - 1];
          }
        }
        // trying this to save memory
        solver.initialize(go);
      }
    }

    for (int i = 0; i < samplesToEquilibrium.length; i++) {
      samplesToEquilibrium[i] /= numSamples - noEquilibriumFound[i];
      samplesToEquilibriumWithUnconfirmed[i] /= numSamples;
    }
    for (double[] tmpStabilities : stabilities) {
      for (int i = 0; i < tmpStabilities.length; i++) {
        tmpStabilities[i] /= numSamples;
      }
    }

//    System.out.println("Game class: " + gameClass);
//    System.out.println("-----------------------------------------------");
//    for (int i = 0; i < solvers.size(); i++) {
//      System.out.println("Solver name: " + names.get(i));
//      System.out.println("Samples to confirm equilibrium: " + samplesToEquilibrium[i]);
//      System.out.println("Samples to confirm with unconfirmed: " + samplesToEquilibriumWithUnconfirmed[i]);
//      System.out.println("No confirmed equilibrium: " + noEquilibriumFound[i]);
//      System.out.println("Stabilities: " + Arrays.toString(stabilities.get(i)));
//    }

    System.out
            .print("Game Class, Solver Name, Solver Number, Samples to Equilibrium, Samples to Equilibrium (with none found), " +
                   " No Equilibrium Found Instances, ");
    for (int bound : bounds) {
      System.out.print(((double) bound / (double) nProfiles) + ", ");
    }
    System.out.println();
    for (int i = 0; i < solvers.size(); i++) {
      System.out.print(gameClass + ", ");
      System.out.print(names.get(i) + ", ");
      System.out.print(i + ", ");
      System.out.print(samplesToEquilibrium[i] + ", ");
      System.out.print(samplesToEquilibriumWithUnconfirmed[i] + ", ");
      System.out.print(noEquilibriumFound[i] + ", ");
      double[] tmp = stabilities.get(i);
      for (double aTmp : tmp) {
        System.out.print(aTmp + ", ");
      }
      System.out.println();
    }
  }

}

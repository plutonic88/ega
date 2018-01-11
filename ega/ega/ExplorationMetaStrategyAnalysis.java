package ega;

import static ega.Parameters.AGGREGATE_ANALYSIS_DIR;
import static ega.Parameters.RESULTS_PATH;
import ega.gnuplottools.DefaultPlots;
import ega.observers.GameObserver;
import ega.observers.NoiselessObserver;
import ega.solvers.IncrementalBestFirstSearch;
import ega.solvers.IncrementalBestResponseDynamics;
import ega.solvers.IncrementalLogitEquilibrium;
import ega.solvers.IncrementalLogitEquilibriumTest;
import ega.solvers.IncrementalRandomSampling;
import ega.solvers.IncrementalSubgameAnalysis;
import ega.solvers.SolverAnalysis;

/**
 * Created by IntelliJ IDEA.
 * User: ckiekint
 * Date: Apr 5, 2008
 * Time: 4:45:47 AM
 */
public class ExplorationMetaStrategyAnalysis {

  private static double[] Lambda_BFS_fg_2_1_2_1_5_random = {100d, 25d, 100d, 100d, 250d, 500d, 1000d};
  private static double[] Lambda_BFS_fg_2_1_5_1_2_random = {10d, 25d, 10d, 25d, 25d, 1000d, 500d};
  private static double[] Lambda_BFS_random_10_10 = {2d, 10d, 10d, 10d, 10d, 10d, 100d};
  private static double[] Lambda_BFS_RandomLEG_10_10 = {10d, 25d, 1000d, 1000d, 500d, 5d, 5d};
  private static double[] Lambda_BFS_Supermodular_10_10 = {100d, 10d, 10d, 25d, 50d, 50d, 500d};

  private static double[] Lambda_BRD_fg_2_1_2_1_5_random = {2d, 25d, 250d, 50d, 1000d, 1000d, 1000d};
  private static double[] Lambda_BRD_fg_2_1_5_1_2_random = {10d, 50d, 500d, 25d, 100d, 250d, 500d};
  private static double[] Lambda_BRD_random_10_10 = {5d, 500d, 50d, 50d, 10d, 1000d, 25d};
  private static double[] Lambda_BRD_RandomLEG_10_10 = {0.05d, 50d, 50d, 25d, 250d, 100d, 5d};
  private static double[] Lambda_BRD_Supermodular_10_10 = {2d, 25d, 25d, 25d, 25d, 25d, 50d};

  private static double[] Lambda_ISA_fg_2_1_2_1_5_random = {2d, 25d, 25d, 250d, 1000d, 500d, 250d};
  private static double[] Lambda_ISA_fg_2_1_5_1_2_random = {10d, 500d, 1000d, 25d, 250d, 500d, 500d};
  private static double[] Lambda_ISA_random_10_10 = {0.05d, 25d, 10d, 10d, 10d, 10d, 50d};
  private static double[] Lambda_ISA_RandomLEG_10_10 = {25d, 100d, 10d, 100d, 25d, 1000d, 1000d};
  private static double[] Lambda_ISA_Supermodular_10_10 = {0.05d, 25d, 25d, 50d, 25d, 25d, 25d};

  private static double[] Lambda_RS_fg_2_1_2_1_5_random = {2d, 5d, 10d, 5d, 10d, 25d, 250d};
  private static double[] Lambda_RS_fg_2_1_5_1_2_random = {5d, 5d, 2d, 10d, 10d, 10d, 1000d};
  private static double[] Lambda_RS_random_10_10 = {5d, 10d, 10d, 10d, 10d, 1000d, 1000d};
  private static double[] Lambda_RS_RandomLEG_10_10 = {2d, 2d, 0.05d, 0.05d, 0.05d, 5d, 2d};
  private static double[] Lambda_RS_Supermodular_10_10 = {2d, 50d, 50d, 25d, 10d, 10d, 25d};

  private static double[] Lambda_ILES_fg_2_1_2_1_5_random = {2d, 10d, 25d, 25d, 100d, 1000d, 1000d};
  private static double[] Lambda_ILES_fg_2_1_5_1_2_random = {5d, 10d, 25d, 25d, 100d, 100d, 1000d};
  private static double[] Lambda_ILES_random_10_10 = {5d, 10d, 10d, 10d, 10d, 10d, 1000d};
  private static double[] Lambda_ILES_RandomLEG_10_10 = {2d, 2.5d, 10d, 1000d, 10d, 25d, 1000d};
  private static double[] Lambda_ILES_Supermodular_10_10 = {2d, 10d, 10d, 10d, 10d, 10d, 10d};

  public static void paramScreenILES_new() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    double[] exp5_thresh = new double[] {0.10d, 0.15d, 0.2d, 0.25d, 0.3d, 0.35d, 0.4d, 0.5d, 0.6d, 0.7d, 1d};
    double[] exp5_lambdas = new double[] {0d, 25d, 25d, 25d, 20d, 20d, 15d, 15d, 10d, 5d, 5d};

    double[] lambdas = new double[] {1000d, 100d, 25d, 10d, 2.5d};

    IncrementalLogitEquilibrium iles;

    for (double lambda : lambdas) {
      iles = new IncrementalLogitEquilibrium();
      iles.setSamplingThresholds(exp5_thresh);
      iles.setSamplingLambdas(exp5_lambdas);
      iles.setFinalLambda(lambda);
      iles.setName("ILES 5 " + lambda);
      analysis.addAlgorithm(iles, observer);
    }

    String prefix = "ParamScreen4_ILES_";

    String[] gameClasses = {"random_10_10", "fg_2_1_2_1_5_random", "fg_2_1_5_1_2_random",
                            "RandomLEG_10_10", "Supermodular_10_10"};

    int[] observationLevels = {20, 30, 40, 50, 70};

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(1);
      for (int observationLevel : observationLevels) {
        observer.setBound(observationLevel);
        analysis.setSubExperimentName(Integer.toString(observationLevel));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }


  public static void paramScreenILES() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

//    double[] exp1_thresh = new double[] {0.1d, 0.2d, 0.3d, 0.4d, 0.5d, 0.6d, 0.7d, 0.8d, 0.9d, 1d};
//    double[] exp1_lambdas = new double[] {0d, 2.5d, 5d, 10d, 10d, 15d, 15d, 20d, 20d, 25d};
//
//    double[] exp2_thresh = new double[] {0.1d, 0.2d, 0.3d, 0.4d, 0.5d, 0.6d, 0.7d, 0.8d, 0.9d, 1d};
//    double[] exp2_lambdas = new double[] {0d, 25d, 20d, 15d, 10d, 10d, 5d, 5d, 2.5d, 2.5d};

//    double[] exp3_thresh = new double[] {0.10d, 0.15d, 0.2d, 0.25d, 0.3d, 0.35d, 0.4d, 0.5d, 0.7d, 1d};
//    double[] exp3_lambdas = new double[] {0d, 20d, 20d, 20d, 10d, 10d, 10d, 5d, 5d, 5d};

//    double[] exp4_thresh = new double[] {0.10d, 0.15d, 0.2d, 0.25d, 0.3d, 0.35d, 0.4d, 0.5d, 0.7d, 1d};
//    double[] exp4_lambdas = new double[] {0d, 5d, 5d, 5d, 10d, 10d, 10d, 20d, 20d, 20d};

    double[] exp5_thresh = new double[] {0.10d, 0.15d, 0.2d, 0.25d, 0.3d, 0.35d, 0.4d, 0.5d, 0.6d, 0.7d, 1d};
    double[] exp5_lambdas = new double[] {0d, 25d, 25d, 25d, 20d, 20d, 15d, 15d, 10d, 5d, 5d};

    IncrementalLogitEquilibrium iles;
//
//    iles = new IncrementalLogitEquilibrium();
//    iles.setSamplingThresholds(exp3_thresh);
//    iles.setSamplingLambdas(exp3_lambdas);
//    iles.setFinalLambda(7.5d);
//    iles.setName("ILES 3 7.5");
//    analysis.addAlgorithm(iles, observer);

//    iles = new IncrementalLogitEquilibrium();
//    iles.setSamplingThresholds(exp3_thresh);
//    iles.setSamplingLambdas(exp3_lambdas);
//    iles.setFinalLambda(25d);
//    iles.setName("ILES 3 25");
//    analysis.addAlgorithm(iles, observer);

    iles = new IncrementalLogitEquilibrium();
    iles.setSamplingThresholds(exp5_thresh);
    iles.setSamplingLambdas(exp5_lambdas);
    iles.setFinalLambda(7.5d);
    iles.setName("ILES 5 7.5");
    analysis.addAlgorithm(iles, observer);

//    iles = new IncrementalLogitEquilibrium();
//    iles.setSamplingThresholds(exp5_thresh);
//    iles.setSamplingLambdas(exp5_lambdas);
//    iles.setFinalLambda(25d);
//    iles.setName("ILES 4 25");
//    analysis.addAlgorithm(iles, observer);

    IncrementalLogitEquilibriumTest iles_test = new IncrementalLogitEquilibriumTest();
    iles_test.setFinalLambda(7.5d);
    iles_test.setName("ILES TEST 7.5");
    analysis.addAlgorithm(iles_test, observer);

    IncrementalRandomSampling irs;
    irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.LES);
    irs.getLes().setLambda(7.5d);
    irs.setName("RS LES");
    analysis.addAlgorithm(irs, observer);

    String prefix = "ParamScreen3_ILES_";

    String[] gameClasses = {"random_10_10", "fg_2_1_2_1_5_random", "fg_2_1_5_1_2_random",
                            "RandomLEG_10_10", "Supermodular_10_10"};

    int[] observationLevels = {20, 30, 40, 50, 70};

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(1);
      for (int observationLevel : observationLevels) {
        observer.setBound(observationLevel);
        analysis.setSubExperimentName(Integer.toString(observationLevel));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  public static void paramScreenRS() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    double[] lambdas = new double[] {1000d, 500d, 250d, 100d, 50d,
                                     25d, 10d, 5d, 2d, 0.5d};

    IncrementalRandomSampling irs;

    irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.STABILITY_MAX_MIN_PAYOFF);
    irs.setName("RS STABLE SET");
    analysis.addAlgorithm(irs, observer);

    for (double lambda : lambdas) {
      irs = new IncrementalRandomSampling();
      irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.LES);
      irs.getLes().setLambda(lambda);
      irs.setName("RS " + lambda);
      analysis.addAlgorithm(irs, observer);
    }

    String prefix = "ParamSet_RS_LES_";

    String[] gameClasses = {"random_10_10", "fg_2_1_2_1_5_random", "fg_2_1_5_1_2_random",
                            "RandomLEG_10_10", "Supermodular_10_10"};

    int[] observationLevels = {10, 20, 30, 40, 50, 70, 90};

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(1);
      for (int observationLevel : observationLevels) {
        observer.setBound(observationLevel);
        analysis.setSubExperimentName(Integer.toString(observationLevel));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  public static void paramScreenBRD() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    double[] lambdas = new double[] {1000d, 500d, 250d, 100d, 50d,
                                     25d, 10d, 5d, 2d, 0.5d};

    IncrementalBestResponseDynamics brd;

    for (double lambda : lambdas) {
      brd = new IncrementalBestResponseDynamics();
      brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.RANDOM);
      brd.setAspirationLevel(Double.POSITIVE_INFINITY);
      brd.setFinalSolver(IncrementalBestResponseDynamics.Solver.LES);
      brd.getLes().setLambda(lambda);
      brd.setName("BRD " + lambda);
      analysis.addAlgorithm(brd, observer);
    }

    String prefix = "ParamSet_BRD_LES_";

    String[] gameClasses = {"random_10_10", "fg_2_1_2_1_5_random", "fg_2_1_5_1_2_random",
                            "RandomLEG_10_10", "Supermodular_10_10"};

    int[] observationLevels = {10, 20, 30, 40, 50, 70, 90};

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(1);
      for (int observationLevel : observationLevels) {
        observer.setBound(observationLevel);
        analysis.setSubExperimentName(Integer.toString(observationLevel));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  public static void paramScreenBFS() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    double[] lambdas = new double[] {1000d, 500d, 250d, 100d, 50d,
                                     25d, 10d, 5d, 2d, 0.5d};

    IncrementalBestFirstSearch bfs;

    for (double lambda : lambdas) {
      bfs = new IncrementalBestFirstSearch();
      bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.LES);
      bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
      bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.RANDOM);
      bfs.getLes().setLambda(lambda);
      bfs.setName("BFS " + lambda);
      analysis.addAlgorithm(bfs, observer);
    }

    String prefix = "ParamSet_BFS_LES_";

    String[] gameClasses = {"random_10_10", "fg_2_1_2_1_5_random", "fg_2_1_5_1_2_random",
                            "RandomLEG_10_10", "Supermodular_10_10"};

    int[] observationLevels = {10, 20, 30, 40, 50, 70, 90};

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(1);
      for (int observationLevel : observationLevels) {
        observer.setBound(observationLevel);
        analysis.setSubExperimentName(Integer.toString(observationLevel));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  public static void paramScreenISA() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    double[] lambdas = new double[] {1000d, 500d, 250d, 100d, 50d,
                                     25d, 10d, 5d, 2d, 0.5d};

    IncrementalSubgameAnalysis isa;

    for (double lambda : lambdas) {
      isa = new IncrementalSubgameAnalysis();
      isa.setFinalData(IncrementalSubgameAnalysis.FinalData.FULL);
      isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.LES);
      isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
      isa.getLes().setLambda(lambda);
      isa.setInitialSubgameSize(1);
      isa.setMaxSubgameIncreaseRate(1);
      isa.setName("ISA " + lambda);
      analysis.addAlgorithm(isa, observer);
    }

    String prefix = "ParamSet_ISA_LES_";

    String[] gameClasses = {"random_10_10", "fg_2_1_2_1_5_random", "fg_2_1_5_1_2_random",
                            "RandomLEG_10_10", "Supermodular_10_10"};

    int[] observationLevels = {10, 20, 30, 40, 50, 70, 90};

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(1);
      for (int observationLevel : observationLevels) {
        observer.setBound(observationLevel);
        analysis.setSubExperimentName(Integer.toString(observationLevel));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  public static void runCombined_fg_2_1_2_1_5_random() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    String prefix = "Exploration_Combined2_";
    String[] gameClasses = {"fg_2_1_2_1_5_random"};

    int[] observationLevels = {10, 20, 30, 40, 50, 70, 90};

    // STABLE SET RS
    IncrementalRandomSampling irs;
    irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.STABILITY_MAX_MIN_PAYOFF);
    irs.setName("RS STABLE SET");
    analysis.addAlgorithm(irs, observer);

    // LES RS
    irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.LES);
    irs.setName("RS LES");
    analysis.addAlgorithm(irs, observer);

    // BRD STABLE
    IncrementalBestResponseDynamics brd;
    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.RANDOM);
    brd.setAspirationLevel(Double.POSITIVE_INFINITY);
    brd.setFinalSolver(IncrementalBestResponseDynamics.Solver.CURRENT_PROFILE);
    brd.setName("BRD STABLE");
    analysis.addAlgorithm(brd, observer);

    // BRD LES
    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.RANDOM);
    brd.setAspirationLevel(Double.POSITIVE_INFINITY);
    brd.setFinalSolver(IncrementalBestResponseDynamics.Solver.LES);
    brd.setName("BRD LES");
    analysis.addAlgorithm(brd, observer);

    // BFS STABLE
    IncrementalBestFirstSearch bfs;
    bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.STABILITY_MAX_MIN_PAYOFF);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.RANDOM);
    bfs.setName("BFS STABLE");
    analysis.addAlgorithm(bfs, observer);

    // BFS LES
    bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.LES);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.RANDOM);
    bfs.setName("BFS LES");
    analysis.addAlgorithm(bfs, observer);

    // ISA STABLE
    IncrementalSubgameAnalysis isa;
    isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInitialSubgameSize(1);
    isa.setMaxSubgameIncreaseRate(1);
    isa.setName("ISA STABLE");
    analysis.addAlgorithm(isa, observer);

    // ISA LES
    isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.FULL);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.LES);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInitialSubgameSize(1);
    isa.setMaxSubgameIncreaseRate(1);
    isa.setName("ISA LES");
    analysis.addAlgorithm(isa, observer);

    double[] exp5_thresh = new double[] {0.10d, 0.15d, 0.2d, 0.25d, 0.3d, 0.35d, 0.4d, 0.5d, 0.6d, 0.7d, 1d};
    double[] exp5_lambdas = new double[] {0d, 25d, 25d, 25d, 20d, 20d, 15d, 15d, 10d, 5d, 5d};
    IncrementalLogitEquilibrium iles = new IncrementalLogitEquilibrium();
    iles.setSamplingThresholds(exp5_thresh);
    iles.setSamplingLambdas(exp5_lambdas);
    iles.setName("ILES");
    analysis.addAlgorithm(iles, observer);


    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(1);
      for (int i = 0; i < observationLevels.length; i++) {
        observer.setBound(observationLevels[i]);

        irs.getLes().setLambda(Lambda_RS_fg_2_1_2_1_5_random[i]);
        brd.getLes().setLambda(Lambda_BRD_fg_2_1_2_1_5_random[i]);
        bfs.getLes().setLambda(Lambda_BFS_fg_2_1_2_1_5_random[i]);
        isa.getLes().setLambda(Lambda_ISA_fg_2_1_2_1_5_random[i]);
        iles.setFinalLambda(Lambda_ILES_fg_2_1_2_1_5_random[i]);

        analysis.setSubExperimentName(Double.toString(observationLevels[i]));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  public static void runCombined_fg_2_1_5_1_2_random() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    String prefix = "Exploration_Combined2_";
    String[] gameClasses = {"fg_2_1_5_1_2_random"};

    int[] observationLevels = {10, 20, 30, 40, 50, 70, 90};

    // STABLE SET RS
    IncrementalRandomSampling irs;
    irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.STABILITY_MAX_MIN_PAYOFF);
    irs.setName("RS STABLE SET");
    analysis.addAlgorithm(irs, observer);

    // LES RS
    irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.LES);
    irs.setName("RS LES");
    analysis.addAlgorithm(irs, observer);

    // BRD STABLE
    IncrementalBestResponseDynamics brd;
    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.RANDOM);
    brd.setAspirationLevel(Double.POSITIVE_INFINITY);
    brd.setFinalSolver(IncrementalBestResponseDynamics.Solver.CURRENT_PROFILE);
    brd.setName("BRD STABLE");
    analysis.addAlgorithm(brd, observer);

    // BRD LES
    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.RANDOM);
    brd.setAspirationLevel(Double.POSITIVE_INFINITY);
    brd.setFinalSolver(IncrementalBestResponseDynamics.Solver.LES);
    brd.setName("BRD LES");
    analysis.addAlgorithm(brd, observer);

    // BFS STABLE
    IncrementalBestFirstSearch bfs;
    bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.STABILITY_MAX_MIN_PAYOFF);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.RANDOM);
    bfs.setName("BFS STABLE");
    analysis.addAlgorithm(bfs, observer);

    // BFS LES
    bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.LES);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.RANDOM);
    bfs.setName("BFS LES");
    analysis.addAlgorithm(bfs, observer);

    // ISA STABLE
    IncrementalSubgameAnalysis isa;
    isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInitialSubgameSize(1);
    isa.setMaxSubgameIncreaseRate(1);
    isa.setName("ISA STABLE");
    analysis.addAlgorithm(isa, observer);

    // ISA LES
    isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.FULL);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.LES);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInitialSubgameSize(1);
    isa.setMaxSubgameIncreaseRate(1);
    isa.setName("ISA LES");
    analysis.addAlgorithm(isa, observer);

    double[] exp5_thresh = new double[] {0.10d, 0.15d, 0.2d, 0.25d, 0.3d, 0.35d, 0.4d, 0.5d, 0.6d, 0.7d, 1d};
    double[] exp5_lambdas = new double[] {0d, 25d, 25d, 25d, 20d, 20d, 15d, 15d, 10d, 5d, 5d};
    IncrementalLogitEquilibrium iles = new IncrementalLogitEquilibrium();
    iles.setSamplingThresholds(exp5_thresh);
    iles.setSamplingLambdas(exp5_lambdas);
    iles.setName("ILES");
    analysis.addAlgorithm(iles, observer);

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(1);
      for (int i = 0; i < observationLevels.length; i++) {
        observer.setBound(observationLevels[i]);

        irs.getLes().setLambda(Lambda_RS_fg_2_1_5_1_2_random[i]);
        brd.getLes().setLambda(Lambda_BRD_fg_2_1_5_1_2_random[i]);
        bfs.getLes().setLambda(Lambda_BFS_fg_2_1_5_1_2_random[i]);
        isa.getLes().setLambda(Lambda_ISA_fg_2_1_5_1_2_random[i]);
        iles.setFinalLambda(Lambda_ILES_fg_2_1_5_1_2_random[i]);

        analysis.setSubExperimentName(Double.toString(observationLevels[i]));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  public static void runCombined_random_10_10() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    String prefix = "Exploration_Combined2_";
    String[] gameClasses = {"random_10_10"};

    int[] observationLevels = {10, 20, 30, 40, 50, 70, 90};

    // STABLE SET RS
    IncrementalRandomSampling irs;
    irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.STABILITY_MAX_MIN_PAYOFF);
    irs.setName("RS STABLE SET");
    analysis.addAlgorithm(irs, observer);

    // LES RS
    irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.LES);
    irs.setName("RS LES");
    analysis.addAlgorithm(irs, observer);

    // BRD STABLE
    IncrementalBestResponseDynamics brd;
    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.RANDOM);
    brd.setAspirationLevel(Double.POSITIVE_INFINITY);
    brd.setFinalSolver(IncrementalBestResponseDynamics.Solver.CURRENT_PROFILE);
    brd.setName("BRD STABLE");
    analysis.addAlgorithm(brd, observer);

    // BRD LES
    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.RANDOM);
    brd.setAspirationLevel(Double.POSITIVE_INFINITY);
    brd.setFinalSolver(IncrementalBestResponseDynamics.Solver.LES);
    brd.setName("BRD LES");
    analysis.addAlgorithm(brd, observer);

    // BFS STABLE
    IncrementalBestFirstSearch bfs;
    bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.STABILITY_MAX_MIN_PAYOFF);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.RANDOM);
    bfs.setName("BFS STABLE");
    analysis.addAlgorithm(bfs, observer);

    // BFS LES
    bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.LES);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.RANDOM);
    bfs.setName("BFS LES");
    analysis.addAlgorithm(bfs, observer);

    // ISA STABLE
    IncrementalSubgameAnalysis isa;
    isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInitialSubgameSize(1);
    isa.setMaxSubgameIncreaseRate(1);
    isa.setName("ISA STABLE");
    analysis.addAlgorithm(isa, observer);

    // ISA LES
    isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.FULL);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.LES);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInitialSubgameSize(1);
    isa.setMaxSubgameIncreaseRate(1);
    isa.setName("ISA LES");
    analysis.addAlgorithm(isa, observer);

    double[] exp5_thresh = new double[] {0.10d, 0.15d, 0.2d, 0.25d, 0.3d, 0.35d, 0.4d, 0.5d, 0.6d, 0.7d, 1d};
    double[] exp5_lambdas = new double[] {0d, 25d, 25d, 25d, 20d, 20d, 15d, 15d, 10d, 5d, 5d};
    IncrementalLogitEquilibrium iles = new IncrementalLogitEquilibrium();
    iles.setSamplingThresholds(exp5_thresh);
    iles.setSamplingLambdas(exp5_lambdas);
    iles.setName("ILES");
    analysis.addAlgorithm(iles, observer);

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(1);
      for (int i = 0; i < observationLevels.length; i++) {
        observer.setBound(observationLevels[i]);

        irs.getLes().setLambda(Lambda_RS_random_10_10[i]);
        brd.getLes().setLambda(Lambda_BRD_random_10_10[i]);
        bfs.getLes().setLambda(Lambda_BFS_random_10_10[i]);
        isa.getLes().setLambda(Lambda_ISA_random_10_10[i]);
        iles.setFinalLambda(Lambda_ILES_random_10_10[i]);

        analysis.setSubExperimentName(Double.toString(observationLevels[i]));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  public static void runCombined_RandomLEG_10_10() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    String prefix = "Exploration_Combined2_";
    String[] gameClasses = {"RandomLEG_10_10"};

    int[] observationLevels = {10, 20, 30, 40, 50, 70, 90};

    // STABLE SET RS
    IncrementalRandomSampling irs;
    irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.STABILITY_MAX_MIN_PAYOFF);
    irs.setName("RS STABLE SET");
    analysis.addAlgorithm(irs, observer);

    // LES RS
    irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.LES);
    irs.setName("RS LES");
    analysis.addAlgorithm(irs, observer);

    // BRD STABLE
    IncrementalBestResponseDynamics brd;
    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.RANDOM);
    brd.setAspirationLevel(Double.POSITIVE_INFINITY);
    brd.setFinalSolver(IncrementalBestResponseDynamics.Solver.CURRENT_PROFILE);
    brd.setName("BRD STABLE");
    analysis.addAlgorithm(brd, observer);

    // BRD LES
    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.RANDOM);
    brd.setAspirationLevel(Double.POSITIVE_INFINITY);
    brd.setFinalSolver(IncrementalBestResponseDynamics.Solver.LES);
    brd.setName("BRD LES");
    analysis.addAlgorithm(brd, observer);

    // BFS STABLE
    IncrementalBestFirstSearch bfs;
    bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.STABILITY_MAX_MIN_PAYOFF);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.RANDOM);
    bfs.setName("BFS STABLE");
    analysis.addAlgorithm(bfs, observer);

    // BFS LES
    bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.LES);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.RANDOM);
    bfs.setName("BFS LES");
    analysis.addAlgorithm(bfs, observer);

    // ISA STABLE
    IncrementalSubgameAnalysis isa;
    isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInitialSubgameSize(1);
    isa.setMaxSubgameIncreaseRate(1);
    isa.setName("ISA STABLE");
    analysis.addAlgorithm(isa, observer);

    // ISA LES
    isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.FULL);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.LES);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInitialSubgameSize(1);
    isa.setMaxSubgameIncreaseRate(1);
    isa.setName("ISA LES");
    analysis.addAlgorithm(isa, observer);

    double[] exp5_thresh = new double[] {0.10d, 0.15d, 0.2d, 0.25d, 0.3d, 0.35d, 0.4d, 0.5d, 0.6d, 0.7d, 1d};
    double[] exp5_lambdas = new double[] {0d, 25d, 25d, 25d, 20d, 20d, 15d, 15d, 10d, 5d, 5d};
    IncrementalLogitEquilibrium iles = new IncrementalLogitEquilibrium();
    iles.setSamplingThresholds(exp5_thresh);
    iles.setSamplingLambdas(exp5_lambdas);
    iles.setName("ILES");
    analysis.addAlgorithm(iles, observer);

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(1);
      for (int i = 0; i < observationLevels.length; i++) {
        observer.setBound(observationLevels[i]);

        irs.getLes().setLambda(Lambda_RS_RandomLEG_10_10[i]);
        brd.getLes().setLambda(Lambda_BRD_RandomLEG_10_10[i]);
        bfs.getLes().setLambda(Lambda_BFS_RandomLEG_10_10[i]);
        isa.getLes().setLambda(Lambda_ISA_RandomLEG_10_10[i]);
        iles.setFinalLambda(Lambda_ILES_RandomLEG_10_10[i]);

        analysis.setSubExperimentName(Double.toString(observationLevels[i]));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  public static void runCombined_Supermodular_10_10() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    String prefix = "Exploration_Combined2_";
    String[] gameClasses = {"Supermodular_10_10"};

    int[] observationLevels = {10, 20, 30, 40, 50, 70, 90};

    // STABLE SET RS
    IncrementalRandomSampling irs;
    irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.STABILITY_MAX_MIN_PAYOFF);
    irs.setName("RS STABLE SET");
    analysis.addAlgorithm(irs, observer);

    // LES RS
    irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.LES);
    irs.setName("RS LES");
    analysis.addAlgorithm(irs, observer);

    // BRD STABLE
    IncrementalBestResponseDynamics brd;
    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.RANDOM);
    brd.setAspirationLevel(Double.POSITIVE_INFINITY);
    brd.setFinalSolver(IncrementalBestResponseDynamics.Solver.CURRENT_PROFILE);
    brd.setName("BRD STABLE");
    analysis.addAlgorithm(brd, observer);

    // BRD LES
    brd = new IncrementalBestResponseDynamics();
    brd.setDeviationOrdering(IncrementalBestResponseDynamics.DeviationOrdering.RANDOM);
    brd.setAspirationLevel(Double.POSITIVE_INFINITY);
    brd.setFinalSolver(IncrementalBestResponseDynamics.Solver.LES);
    brd.setName("BRD LES");
    analysis.addAlgorithm(brd, observer);

    // BFS STABLE
    IncrementalBestFirstSearch bfs;
    bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.STABILITY_MAX_MIN_PAYOFF);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.RANDOM);
    bfs.setName("BFS STABLE");
    analysis.addAlgorithm(bfs, observer);

    // BFS LES
    bfs = new IncrementalBestFirstSearch();
    bfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.LES);
    bfs.setCandidateOrdering(IncrementalBestFirstSearch.CandidateOrdering.STABILITY_MIN_EPSILON);
    bfs.setDeviationOrdering(IncrementalBestFirstSearch.DeviationOrdering.RANDOM);
    bfs.setName("BFS LES");
    analysis.addAlgorithm(bfs, observer);

    // ISA STABLE
    IncrementalSubgameAnalysis isa;
    isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.SUBGAME);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInitialSubgameSize(1);
    isa.setMaxSubgameIncreaseRate(1);
    isa.setName("ISA STABLE");
    analysis.addAlgorithm(isa, observer);

    // ISA LES
    isa = new IncrementalSubgameAnalysis();
    isa.setFinalData(IncrementalSubgameAnalysis.FinalData.FULL);
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.LES);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.MOST_STABLE);
    isa.setInitialSubgameSize(1);
    isa.setMaxSubgameIncreaseRate(1);
    isa.setName("ISA LES");
    analysis.addAlgorithm(isa, observer);

    double[] exp5_thresh = new double[] {0.10d, 0.15d, 0.2d, 0.25d, 0.3d, 0.35d, 0.4d, 0.5d, 0.6d, 0.7d, 1d};
    double[] exp5_lambdas = new double[] {0d, 25d, 25d, 25d, 20d, 20d, 15d, 15d, 10d, 5d, 5d};
    IncrementalLogitEquilibrium iles = new IncrementalLogitEquilibrium();
    iles.setSamplingThresholds(exp5_thresh);
    iles.setSamplingLambdas(exp5_lambdas);
    iles.setName("ILES");
    analysis.addAlgorithm(iles, observer);

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(1);
      for (int i = 0; i < observationLevels.length; i++) {
        observer.setBound(observationLevels[i]);

        irs.getLes().setLambda(Lambda_RS_Supermodular_10_10[i]);
        brd.getLes().setLambda(Lambda_BRD_Supermodular_10_10[i]);
        bfs.getLes().setLambda(Lambda_BFS_Supermodular_10_10[i]);
        isa.getLes().setLambda(Lambda_ISA_Supermodular_10_10[i]);
        iles.setFinalLambda(Lambda_ILES_Supermodular_10_10[i]);

        analysis.setSubExperimentName(Double.toString(observationLevels[i]));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

}

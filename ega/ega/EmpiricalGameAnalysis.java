package ega;

import static ega.Parameters.AGGREGATE_ANALYSIS_DIR;
import static ega.Parameters.GAME_FILES_PATH;
import static ega.Parameters.GAMUT_GAME_EXTENSION;
import static ega.Parameters.RESULTS_PATH;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ega.games.MatrixGame;
import ega.games.OutcomeDistribution;
import ega.games.OutcomeIterator;
import ega.gnuplottools.DefaultPlots;
import ega.gnuplottools.ExplorationStrategySelectionPlots;
import ega.observers.GameObserver;
import ega.observers.NoiselessObserver;
import ega.observers.NoisyObserver;
import ega.parsers.GamutParser;
import ega.solvers.BRUSolver;
import ega.solvers.EpsilonNashSolver;
import ega.solvers.ExplorationUtil;
import ega.solvers.IncrementalBestFirstSearch;
import ega.solvers.IncrementalBestResponseDynamics;
import ega.solvers.IncrementalGameOutcomePredictor;
import ega.solvers.IncrementalRandomSampling;
import ega.solvers.IncrementalSubgameAnalysis;
import ega.solvers.QRESolver;
import ega.solvers.RandomSolver;
import ega.solvers.ReplicatorDynamicsSolver;
import ega.solvers.SolverAnalysis;
import ega.solvers.SolverUtils;
import ega.util.GaussianSampler;

public class EmpiricalGameAnalysis {

  // empty constructor
  private EmpiricalGameAnalysis() {
  }

  public static void main(String[] args) {
    //UncertaintyMetaStrategyAnalysis.genStabilityPlot();
    //UncertaintyMetaStrategyAnalysis.genPairedBTDPlot();
    
    //UncertaintyMetaStrategyAnalysis.paramSetENS_Noisy();
    //UncertaintyMetaStrategyAnalysis.paramSetRDS_Noisy();
    //UncertaintyMetaStrategyAnalysis.paramSetLES_Noisy();
    //UncertaintyMetaStrategyAnalysis.paramSetENS_Incomplete();
    //UncertaintyMetaStrategyAnalysis.paramSetRDS_Incomplete();
    //UncertaintyMetaStrategyAnalysis.paramSetLES_Incomplete();

    //UncertaintyMetaStrategyAnalysis.combined_Noisy();
    //UncertaintyMetaStrategyAnalysis.combined_Incomplete();

    //plotExplorationMS();

//    ExplorationMetaStrategyAnalysis.runCombined_fg_2_1_2_1_5_random();
//    ExplorationMetaStrategyAnalysis.runCombined_fg_2_1_5_1_2_random();
//    ExplorationMetaStrategyAnalysis.runCombined_random_10_10();
//    ExplorationMetaStrategyAnalysis.runCombined_RandomLEG_10_10();
//    ExplorationMetaStrategyAnalysis.runCombined_Supermodular_10_10();

//    ExplorationMetaStrategyAnalysis.paramScreenILES_new();
//    ExplorationMetaStrategyAnalysis.paramScreenILES();
//    ExplorationMetaStrategyAnalysis.paramScreenRS();
//    ExplorationMetaStrategyAnalysis.paramScreenBRD();
//    ExplorationMetaStrategyAnalysis.paramScreenBFS();
//    ExplorationMetaStrategyAnalysis.paramScreenISA();

    //EquilibriumFindingExperiments.testRS();
    //EquilibriumFindingExperiments.testBRD();
    //EquilibriumFindingExperiments.testBFS();
    //EquilibriumFindingExperiments.testISA();

//    msAnalysisIncrementalSolvers1();
//    msAnalysisIncrementalSolvers2();
//    msAnalysisIncrementalSolvers3();

    //testIncrementalSolverEquilibriumFinding();

    //gatherAlgorithmPerfomanceData();
    //gatherParamVisualizationData();

    //runENS_incompleteObservation();
    //runRDS_incompleteObservation();
    //runQRE_IncompleteObservation();

    //runENS();
    //runQRE();
    //runRDS();

    //runENS_LES_RDS();
    //runENS_LES_RDS_incompleteInformation();
    //plotENS_LES_RDS();

    //runBRUvsPSNE();
    //runBRUvsQRE();
    //runENSvsLES();

    //generateGames();
    //runTest();
    //analyzeSCMGames();

    //TwoByTwoScorePlots.genTwoByTwoScorePlots();
    //genParamStabilityPlots();

    //genScorePlots("BRUvsPSNE_random_4_4_PSNE");

      //generateGames();
      runQRE();
  }

  private static void msAnalysisIncrementalSolvers1() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    String prefix = "IncrementalSolvers2_";
    String[] gameClasses = {"random_16_16"};
    //String[] gameClasses = {"fg_2_1_4_1_4_random"};

    // only sample for 10 games to start
    analysis.setMaxSamples(1000);

    int[] bounds = {24, 48, 72, 96, 128, 192};

    // add in the algorithms
    IncrementalRandomSampling irs = new IncrementalRandomSampling();
    //irs.getLes().setLambda(10);
    analysis.addAlgorithm(irs, observer);

    IncrementalBestResponseDynamics ibrd = new IncrementalBestResponseDynamics();
    //ibrd.getLes().setLambda(10);
    analysis.addAlgorithm(ibrd, observer);

    IncrementalBestFirstSearch ibfs = new IncrementalBestFirstSearch();
    //ibfs.getLes().setLambda(10);
    analysis.addAlgorithm(ibfs, observer);

    IncrementalSubgameAnalysis isa = new IncrementalSubgameAnalysis();
    //isa.getLes().setLambda(10);
    analysis.addAlgorithm(isa, observer);

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(1);
      for (int i = 0; i < bounds.length; i++) {
        observer.setBound(bounds[i]);
        analysis.setSubExperimentName(Integer.toString(bounds[i]));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  private static void msAnalysisIncrementalSolvers2() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    String prefix = "IncrementalSolvers5_";
    //String[] gameClasses = {"random_16_16"};
    String[] gameClasses = {"fg_2_1_4_1_4_random"};

    // only sample for 10 games to start
    analysis.setMaxSamples(1000);

    int[] bounds = {24, 48, 72, 96, 128, 192};

    // add in the algorithms
    IncrementalRandomSampling irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.ENS);
    irs.getEns().setTemperature(0.2);
    analysis.addAlgorithm(irs, observer);

    IncrementalBestResponseDynamics ibrd = new IncrementalBestResponseDynamics();
    ibrd.setFinalSolver(IncrementalBestResponseDynamics.Solver.ENS);
    ibrd.getEns().setTemperature(0.2);
    analysis.addAlgorithm(ibrd, observer);

    IncrementalBestFirstSearch ibfs = new IncrementalBestFirstSearch();
    ibfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.ENS);
    ibfs.getEns().setTemperature(0.2);
    analysis.addAlgorithm(ibfs, observer);

    IncrementalSubgameAnalysis isa = new IncrementalSubgameAnalysis();
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.ENS);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.ENS);
    isa.getEns().setTemperature(0.2);
    analysis.addAlgorithm(isa, observer);

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(1);
      for (int i = 0; i < bounds.length; i++) {
        observer.setBound(bounds[i]);
        analysis.setSubExperimentName(Integer.toString(bounds[i]));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  private static void msAnalysisIncrementalSolvers3() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    String prefix = "IncrementalSolvers5_";
    //String[] gameClasses = {"random_16_16"};
    String[] gameClasses = {"random_16_16"};

    // only sample for 10 games to start
    analysis.setMaxSamples(1000);

    int[] bounds = {24, 48, 72, 96, 128, 192};

    // add in the algorithms
    IncrementalRandomSampling irs = new IncrementalRandomSampling();
    irs.setDecisionMode(IncrementalRandomSampling.DecisionMode.ENS);
    irs.getEns().setTemperature(0.2);
    analysis.addAlgorithm(irs, observer);

    IncrementalBestResponseDynamics ibrd = new IncrementalBestResponseDynamics();
    ibrd.setFinalSolver(IncrementalBestResponseDynamics.Solver.ENS);
    ibrd.getEns().setTemperature(0.2);
    analysis.addAlgorithm(ibrd, observer);

    IncrementalBestFirstSearch ibfs = new IncrementalBestFirstSearch();
    ibfs.setDecisionMode(IncrementalBestFirstSearch.DecisionMode.ENS);
    ibfs.getEns().setTemperature(0.2);
    analysis.addAlgorithm(ibfs, observer);

    IncrementalSubgameAnalysis isa = new IncrementalSubgameAnalysis();
    isa.setFinalSolver(IncrementalSubgameAnalysis.Solver.ENS);
    isa.setInterimSolver(IncrementalSubgameAnalysis.Solver.ENS);
    isa.getEns().setTemperature(0.2);
    analysis.addAlgorithm(isa, observer);

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(1);
      for (int i = 0; i < bounds.length; i++) {
        observer.setBound(bounds[i]);
        analysis.setSubExperimentName(Integer.toString(bounds[i]));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  private static void testIncrementalSolverEquilibriumFinding() {
    String path = GAME_FILES_PATH + "random_16_16/";
    String extension = ".gamut";
    int numSamples = 100;

    ArrayList<IncrementalGameOutcomePredictor> solvers = new ArrayList<IncrementalGameOutcomePredictor>();
    solvers.add(new IncrementalRandomSampling());
    solvers.add(new IncrementalBestResponseDynamics());
    solvers.add(new IncrementalBestFirstSearch());
    solvers.add(new IncrementalSubgameAnalysis());

    int[] bounds = {24, 48, 72, 96, 128, 192};

    ArrayList<double[]> stabilities = new ArrayList<double[]>();
    for (IncrementalGameOutcomePredictor solver : solvers) {
      stabilities.add(new double[bounds.length]);
    }
    double[] samplesToEquilibrium = new double[solvers.size()];
    double[] samplesToEquilibriumWithUnconfirmed = new double[solvers.size()];
    int[] noEquilibriumFound = new int[solvers.size()];

    for (int sample = 1; sample <= numSamples; sample++) {
      MatrixGame game = GamutParser.readGamutGame(path + sample + extension);
      GameObserver go = new NoiselessObserver(game);
      go.setDefaultPayoff(100d);

      for (int solverIndex = 0; solverIndex < solvers.size(); solverIndex++) {
        IncrementalGameOutcomePredictor solver = solvers.get(solverIndex);
        go.reset();
        solver.initialize(go);
        for (int i = 0; i < bounds.length; i++) {
          go.setBound(bounds[i]);
          OutcomeDistribution od = solver.incrementalPredictOutcome(go);
          double stability = SolverUtils.computeOutcomeStability(game, od);
          System.out.println("Solver: " + solverIndex + " bound: " + i + " stability: " + stability);
          stabilities.get(solverIndex)[i] += stability;
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

    System.out.println("Solver 0: Random Sampling");
    System.out.println("Samples to confirm equilibrium: " + samplesToEquilibrium[0]);
    System.out.println("Samples to confirm with unconfirmed: " + samplesToEquilibriumWithUnconfirmed[0]);
    System.out.println("No confirmed equilibrium: " + noEquilibriumFound[0]);
    System.out.println("Stabilities: " + Arrays.toString(stabilities.get(0)));

    System.out.println("Solver 1: Best-Response Dynamics");
    System.out.println("Samples to confirm equilibrium: " + samplesToEquilibrium[1]);
    System.out.println("Samples to confirm with unconfirmed: " + samplesToEquilibriumWithUnconfirmed[1]);
    System.out.println("No confirmed equilibrium: " + noEquilibriumFound[1]);
    System.out.println("Stabilities: " + Arrays.toString(stabilities.get(1)));

    System.out.println("Solver 2: Best-First Search");
    System.out.println("Samples to confirm equilibrium: " + samplesToEquilibrium[2]);
    System.out.println("Samples to confirm with unconfirmed: " + samplesToEquilibriumWithUnconfirmed[2]);
    System.out.println("No confirmed equilibrium: " + noEquilibriumFound[2]);
    System.out.println("Stabilities: " + Arrays.toString(stabilities.get(2)));

    System.out.println("Solver 3: ISA");
    System.out.println("Samples to confirm equilibrium: " + samplesToEquilibrium[3]);
    System.out.println("Samples to confirm with unconfirmed: " + samplesToEquilibriumWithUnconfirmed[3]);
    System.out.println("No confirmed equilibrium: " + noEquilibriumFound[3]);
    System.out.println("Stabilities: " + Arrays.toString(stabilities.get(3)));

  }

  private static void runENS() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GaussianSampler sampler = new GaussianSampler(0, 0);
    GameObserver observer = new NoisyObserver(sampler);
    observer.setDensityBound(1d);

    double[] temperatures = new double[] {0.01, 0.025, 0.05, 0.075, 0.1,
                                          0.125, 0.15, 0.175, 0.2, 0.225,
                                          0.25, 0.275, 0.3, 0.35, 0.4,
                                          0.5, 1.0};

    EpsilonNashSolver ens;
    for (double temperature : temperatures) {
      ens = new EpsilonNashSolver();
      ens.setSamplingMode(ExplorationUtil.SamplingMode.ALL_EVEN_PLUS_RANDOM);
      ens.setDecisionMode(EpsilonNashSolver.DecisionMode.BR_BOLTZMANN);
      ens.setAbsoluteDeviationSamplingBound(1);
      ens.setTemperature(temperature);
      analysis.addAlgorithm(ens, observer);
    }

    String prefix = "ENS_";
    String[] gameClasses = {"exponential_4_4", "exponential_4_4_PSNE",
                            "random_4_4", "random_4_4_PSNE",
                            "covariant_1.0_4_4", "covariant_1.0_4_4_PSNE",
                            "covariant_-1_4_4", "covariant_-1_4_4_PSNE"};

    double[] stdDevs = {0.0, 0.001, 0.005, 0.01, 0.015, 0.02, 0.03, 0.05, 0.08, 0.1, 0.125, 0.15, 0.175,
                        0.2, 0.225, 0.25, 0.275, 0.3, 0.35, 0.4, 0.45, 0.5};

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(5);
      for (double stdDev : stdDevs) {
        sampler.setStdDev(stdDev);
        analysis.setSubExperimentName(Double.toString(stdDev));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  private static void runENS_incompleteObservation() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    double[] temperatures = new double[] {0.01, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.4, 0.5, 0.75, 1};

    EpsilonNashSolver ens;
    for (double temperature : temperatures) {
      ens = new EpsilonNashSolver();
      ens.setSamplingMode(ExplorationUtil.SamplingMode.RANDOM_WITHOUT_REPLACEMENT);
      ens.setDecisionMode(EpsilonNashSolver.DecisionMode.BR_BOLTZMANN);
      ens.setAbsoluteDeviationSamplingBound(1);
      ens.setTemperature(temperature);
      analysis.addAlgorithm(ens, observer);
    }

    String prefix = "IncompleteObservation_ENS_";
    String[] gameClasses = {"random_4_4", "random_4_4_PSNE",
                            "covariant_1.0_4_4", "covariant_1.0_4_4_PSNE",
                            "covariant_-1_4_4", "covariant_-1_4_4_PSNE"};

    int[] observationLevels = {16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(5);
      for (int observationLevel : observationLevels) {
        observer.setBound(observationLevel);
        analysis.setSubExperimentName(Integer.toString(observationLevel));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }


  private static void runRDS() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GaussianSampler sampler = new GaussianSampler(0, 0);
    GameObserver observer = new NoisyObserver(sampler);
    observer.setDensityBound(1d);

//    double[] RDS_params = new double[] {0, 0.01d, 0.025d, 0.05d, 0.075d,
//                                        0.1, 0.125d, 0.15d, 0.2d, 0.25d,
//                                        0.3d, 0.35d, 0.4d, 0.45, 0.5d};

    double[] RDS_params = new double[] {0, 0.1d, 0.2d, 0.3d, 0.4d, 0.5d,
                                        0.6d, 0.7d, 0.8d, 0.9d, 1.0d};

    for (double param : RDS_params) {
      ReplicatorDynamicsSolver rds = new ReplicatorDynamicsSolver(param);
      analysis.addAlgorithm(rds, observer);
    }

    String prefix = "RDS_new_";
    String[] gameClasses = {"exponential_4_4", "covariant_1.0_4_4", "covariant_-1_4_4"};

//    double[] stdDevs = {0.0, 0.001, 0.005, 0.01, 0.015, 0.02, 0.03, 0.05, 0.08, 0.1, 0.125, 0.15, 0.175,
//                        0.2, 0.225, 0.25, 0.275, 0.3, 0.35, 0.4, 0.45, 0.5};

    double[] stdDevs = {0.0, 0.01, 0.03, 0.05, 0.08, 0.1, 0.125, 0.15, 0.175,
                        0.2, 0.225, 0.25, 0.275, 0.3, 0.35, 0.4, 0.45, 0.5};

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(5);
      for (double stdDev : stdDevs) {
        sampler.setStdDev(stdDev);
        analysis.setSubExperimentName(Double.toString(stdDev));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  private static void runRDS_incompleteObservation() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

//    double[] RDS_params = new double[] {0, 0.01d, 0.025d, 0.05d, 0.075d,
//                                        0.1, 0.125d, 0.15d, 0.2d, 0.25d,
//                                        0.3d, 0.35d, 0.4d, 0.45, 0.5d};

    double[] RDS_params = new double[] {0, 0.1d, 0.2d, 0.3d, 0.4d, 0.5d,
                                        0.6d, 0.7d, 0.8d, 0.9d, 1.0d};

    for (double param : RDS_params) {
      ReplicatorDynamicsSolver rds = new ReplicatorDynamicsSolver(param);
      analysis.addAlgorithm(rds, observer);
    }

    String prefix = "IncompleteObservation_RDS_";
    String[] gameClasses = {"random_4_4", "random_4_4_PSNE",
                            "covariant_1.0_4_4", "covariant_1.0_4_4_PSNE",
                            "covariant_-1_4_4", "covariant_-1_4_4_PSNE"};

    int[] observationLevels = {16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};


    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(5);
      for (int observationLevel : observationLevels) {
        observer.setBound(observationLevel);
        analysis.setSubExperimentName(Integer.toString(observationLevel));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }


  private static void runQRE() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GaussianSampler sampler = new GaussianSampler(0, 0);
    GameObserver observer = new NoisyObserver(sampler);
    observer.setDensityBound(1d);

    double[] lambdas = new double[] {1000d, 100d, 75d, 50d, 40d,
                                     30d, 25d, 20d, 15d, 12.5d,
                                     10d, 7.5d, 5d, 2.5d, 1d};

    QRESolver qres;
    for (double lambda : lambdas) {
      qres = new QRESolver(lambda);
      qres.setDecisionMode(QRESolver.DecisionMode.BR);
      analysis.addAlgorithm(qres, observer);
    }


    String prefix = "QRE_";
//    String[] gameClasses = {"random_4_4", "exponential_4_4",
//                            "covariant_1.0_4_4", "covariant_-1_4_4"};
    String[] gameClasses = {"covariant_1.0_4_4"};
    double[] stdDevs = {0.35, 0.4, 0.45, 0.5};
//    double[] stdDevs = {0.0, 0.001, 0.005, 0.01, 0.015, 0.02, 0.03, 0.05, 0.08, 0.1, 0.125, 0.15, 0.175,
//                        0.2, 0.225, 0.25, 0.275, 0.3, 0.35, 0.4, 0.45, 0.5};

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(5);
      for (double stdDev : stdDevs) {
        sampler.setStdDev(stdDev);
        analysis.setSubExperimentName(Double.toString(stdDev));
        analysis.computeAlgorithmGame();
      }
        //oscar added if os
      if(System.getProperty("os.name").toLowerCase().contains("win")){
        DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "\\" + AGGREGATE_ANALYSIS_DIR + "\\",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
      }
      else{
        DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
      }
    }
  }

  private static void runQRE_IncompleteObservation() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    double[] lambdas = new double[] {1000d, 500d, 250d, 100d, 50d,
                                     25d, 10d, 5d, 2.5d, 1d, 0.5d};

    QRESolver qres;
    for (double lambda : lambdas) {
      qres = new QRESolver(lambda);
      qres.setDecisionMode(QRESolver.DecisionMode.BR);
      analysis.addAlgorithm(qres, observer);
    }


    String prefix = "IncompleteObservation_QRE_";

    String[] gameClasses = {"random_4_4", "random_4_4_PSNE",
                            "covariant_1.0_4_4", "covariant_1.0_4_4_PSNE",
                            "covariant_-1_4_4", "covariant_-1_4_4_PSNE"};

    int[] observationLevels = {16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(5);
      for (int observationLevel : observationLevels) {
        observer.setBound(observationLevel);
        analysis.setSubExperimentName(Integer.toString(observationLevel));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  private static void runENS_LES_RDS() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GaussianSampler sampler = new GaussianSampler(0, 0);
    GameObserver observer = new NoisyObserver(sampler);
    observer.setDensityBound(1d);

    String prefix = "Test2";
    //String[] gameClasses = {"random_4_4"};
    String[] gameClasses = {"covariant_-1_4_4"};

    double[] stdDevs = {0.0};

//    double[] stdDevs = {0.0, 0.01, 0.03, 0.05, 0.08,
//                        0.1, 0.125, 0.15, 0.175, 0.2,
//                        0.225, 0.25, 0.275, 0.3, 0.35,
//                        0.4, 0.45, 0.5};

//    double[] stdDevs = {0.0, 0.001, 0.005, 0.01, 0.015,
//                        0.02, 0.03, 0.05, 0.08, 0.1,
//                        0.125, 0.15, 0.175, 0.2, 0.225,
//                        0.25, 0.275, 0.3, 0.35, 0.4,
//                        0.45, 0.5};

    // random_4_4

//    double[] RDS_params = new double[] {0.0, 0.1, 0.2, 0.2, 0.4,
//                                        0.4, 0.5, 0.5, 0.6, 0.6,
//                                        0.7, 0.7, 0.7, 0.7, 0.7,
//                                        0.7, 0.7, 0.8};
//
//    double[] ENS_params = new double[] {0.05, 0.1, 0.125, 0.125, 0.15,
//                                        0.175, 0.225, 0.25, 0.3, 0.3,
//                                        0.4, 0.35, 0.4, 0.4, 0.5,
//                                        1.0, 1.0, 1.0};
//
//    double[] LES_params = new double[] {1000, 1000, 100, 25, 20,
//                                        15, 7.5, 5.0, 5.0, 5.0,
//                                        5.0, 2.5, 2.5, 2.5, 2.5,
//                                        2.5, 2.5, 1.0};

    // exponential_4_4

//    double[] RDS_params = new double[] {0, 0, 0.2, 0.1, 0.1,
//                                        0.2, 0.2, 0.1, 0.3, 0.2,
//                                        0.2, 0.4, 0.3, 0.3, 0.5,
//                                        0.4, 0.4, 0.6};
//
//    double[] ENS_params = new double[] {0.01, 0.01, 0.025, 0.075, 0.175,
//                                        0.225, 0.225, 0.225, 0.3, 0.25,
//                                        0.4, 0.3, 0.4, 0.4, 0.5,
//                                        0.5, 0.5, 1};
//
//    double[] LES_params = new double[] {1000, 1000, 1000, 100, 100,
//                                        25, 30, 7.5, 7.5, 40,
//                                        7.5, 10, 2.5, 5, 15,
//                                        2.5, 2.5, 2.5};

    // covariant_1.0_4_4

//    double[] RDS_params = new double[] {0.3, 0.2, 0.5, 0.6, 0.6,
//                                        0.6, 0.6, 0.7, 0.7, 0.8,
//                                        0.9, 0.8, 0.8, 0.9, 0.9,
//                                        0.8, 0.9, 1};
//
//    double[] ENS_params = new double[] {0.01, 0.01, 0.05, 0.075, 0.125,
//                                        0.125, 0.2, 0.25, 0.5, 0.4,
//                                        0.35, 0.4, 1, 0.5, 1,
//                                        1, 1, 1};
//
//    double[] LES_params = new double[] {20, 30, 100, 7.5, 5,
//                                        5, 5, 5, 2.5, 2.5,
//                                        2.5, 2.5, 2.5, 2.5, 1,
//                                        1, 1, 1};

    // covariant_-1.0_4_4

    double[] RDS_params = new double[] {0, 0, 0, 0, 0.1,
                                        0.1, 0.2, 0.3, 0.2, 0.4,
                                        0.5, 0.7, 0.7, 0.8, 0.9,
                                        0.9, 0.9, 1};

    double[] ENS_params = new double[] {0.025, 0.025, 0.05, 0.05, 0.075,
                                        0.1, 0.15, 0.15, 0.3, 0.2,
                                        0.4, 0.5, 0.4, 0.5, 1,
                                        1, 1, 1};

    double[] LES_params = new double[] {1000, 1000, 1000, 1000, 100,
                                        1000, 50, 12.5, 12.5, 7.5,
                                        5, 5, 2.5, 2.5, 2.5,
                                        1, 2.5, 1};

    // random
    analysis.addAlgorithm(new RandomSolver(), observer);

    // BRU
    BRUSolver bru = new BRUSolver();
    analysis.addAlgorithm(bru, observer);

    // ENS (most stable setting)
    EpsilonNashSolver ens = new EpsilonNashSolver();
    ens.setSamplingMode(ExplorationUtil.SamplingMode.ALL_EVEN_PLUS_RANDOM);
    ens.setDecisionMode(EpsilonNashSolver.DecisionMode.BR_BOLTZMANN);
    ens.setAbsoluteDeviationSamplingBound(1);
    ens.setName("ENS: most stable");
    analysis.addAlgorithm(ens, observer);

    // ENS (equilibrium approximation)
//    EpsilonNashSolver ens_equilibrium = new EpsilonNashSolver();
//    ens_equilibrium.setName("ENS: equilibrium");
//    analysis.addAlgorithm(ens_equilibrium, observer);

    // LES (most stable setting)
    QRESolver les = new QRESolver();
    les.setDecisionMode(QRESolver.DecisionMode.BR);
    les.setName("LES: most stable");
    analysis.addAlgorithm(les, observer);

    // LES (equibrium approximation)
//    QRESolver les_equilibrium = new QRESolver(10000);
//    les_equilibrium.setDecisionMode(QRESolver.DecisionMode.BR);
//    les.setName("LES: equilibrium");
//    analysis.addAlgorithm(les_equilibrium, observer);

    // RDS (most stable setting)
    ReplicatorDynamicsSolver rds = new ReplicatorDynamicsSolver();
    rds.setName("RDS: most stable");
    analysis.addAlgorithm(rds, observer);

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(5);
      for (int i = 0; i < stdDevs.length; i++) {
        sampler.setStdDev(stdDevs[i]);
        ens.setTemperature(ENS_params[i]);
        les.setLambda(LES_params[i]);
        rds.setDelta(RDS_params[i]);

        analysis.setSubExperimentName(Double.toString(stdDevs[i]));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  private static void runENS_LES_RDS_incompleteInformation() {
    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    String prefix = "IncompleteObservation_ENSvsLESvsRDS";
    String[] gameClasses = {"random_4_4"};

    int[] observationLevels = {16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};

    // random_4_4

    double[] ENS_params =
            new double[] {0.05, 0.15, 0.1, 0.2, 0.2, 0.25, 0.3, 0.2, 0.5, 0.25, 0.5, 0.3, 0.4, 0.4, 0.5, 0.5};

    double[] RDS_params = new double[] {0, 0.3, 0.2, 0.6, 0.5, 0.7, 0.7, 0.6, 0.8, 0.7, 0.6, 0.8, 0.5, 0.7, 0.8, 1.0};

    double[] LES_params = new double[] {1000, 25, 25, 10, 5, 5, 5, 5, 5, 5, 5, 2.5, 5, 2.5, 1, 2.5};

    // covariant 1.0

//    double[] ENS_params = new double[] {0.01, 0.05, 0.1, 0.25, 0.3, 0.25, 0.25, 0.25, 0.4, 0.3, 0.25, 0.3, 0.3, 0.25, 0.25, 0.01};
//
//    double[] RDS_params = new double[] {0.2, 0.3, 0.4, 0.7, 0.6, 0.7, 0.7, 0.8, 0.8, 0.7, 0.8, 0.7, 0.8, 1, 1, 0.7};
//
//    double[] LES_params = new double[] {50, 10, 1000, 5, 5, 5, 5, 5, 5, 2.5, 2.5, 2.5, 2.5, 0.5, 2.5, 500};

    // covariant -1

//    double[] ENS_params = new double[] {0.05, 0.05, 0.1, 0.2, 0.2, 0.2, 0.3, 0.4, 0.4, 0.75, 0.5, 0.75, 0.75, 0.5, 1, 0.4};
//
//    double[] RDS_params = new double[] {0, 0, 0.1, 0.2, 0.3, 0.4, 0.4, 0.6, 0.7, 0.9, 0.8, 0.8, 0.8, 0.9, 0.7, 1};
//
//    double[] LES_params = new double[] {1000, 500, 250, 25, 10, 10, 10, 5, 10, 2.5, 2.5, 2.5, 0.5, 2.5, 1, 25};

    // random
    analysis.addAlgorithm(new RandomSolver(), observer);

    // BRU
    BRUSolver bru = new BRUSolver();
    analysis.addAlgorithm(bru, observer);

    // ENS (most stable setting)
    EpsilonNashSolver ens = new EpsilonNashSolver();
    ens.setSamplingMode(ExplorationUtil.SamplingMode.RANDOM_WITHOUT_REPLACEMENT);
    ens.setDecisionMode(EpsilonNashSolver.DecisionMode.BR_BOLTZMANN);
    ens.setAbsoluteDeviationSamplingBound(1);
    ens.setName("ENS: most stable");
    analysis.addAlgorithm(ens, observer);

    // LES (most stable setting)
    QRESolver les = new QRESolver();
    les.setSamplingMode(ExplorationUtil.SamplingMode.RANDOM_WITHOUT_REPLACEMENT);
    les.setDecisionMode(QRESolver.DecisionMode.BR);
    les.setName("LES: most stable");
    analysis.addAlgorithm(les, observer);

    // RDS (most stable setting)
    ReplicatorDynamicsSolver rds = new ReplicatorDynamicsSolver();
    rds.setSamplingMode(ExplorationUtil.SamplingMode.RANDOM_WITHOUT_REPLACEMENT);
    rds.setName("RDS: most stable");
    analysis.addAlgorithm(rds, observer);

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      analysis.setSamplesPerGameInstance(5);
      for (int i = 0; i < observationLevels.length; i++) {
        observer.setBound(observationLevels[i]);
        ens.setTemperature(ENS_params[i]);
        les.setLambda(LES_params[i]);
        rds.setDelta(RDS_params[i]);

        analysis.setSubExperimentName(Integer.toString(observationLevels[i]));
        analysis.computeAlgorithmGame();
      }
      DefaultPlots.generatePlots(experimentName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }


  private static void plotExplorationMS() {
    String plotName = "Uniform Random Games";
    String prefix = "Exploration_Combined2_";
    String gameClass = "random_10_10";
    //String gameClass = "fg_2_1_2_1_5_random";
    //String gameClass = "fg_2_1_5_1_2_random";
    //String gameClass = "RandomLEG_10_10";
    //String gameClass = "Supermodular_10_10";

    String experimentName = prefix + gameClass;

    List<String> algorithmNames = new ArrayList<String>();
    algorithmNames.add("RS Stable Set");
    algorithmNames.add("RS LES");
    algorithmNames.add("TBRD Stable Set");
    algorithmNames.add("TBRD LES");
    algorithmNames.add("MRFS Stable Set");
    algorithmNames.add("MRFS LES");
    algorithmNames.add("SBRD Stable Set");
    algorithmNames.add("SBRD LES");
    algorithmNames.add("ILES");

    ExplorationStrategySelectionPlots
            .generatePlots(plotName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                           algorithmNames.size(), algorithmNames);
  }


  private static void plotENS_LES_RDS() {
    String plotName = "Random Games";
    String prefix = "IncompleteObservation_ENSvsLESvsRDS";
    String gameClass = "random_4_4";

    String experimentName = prefix + gameClass;

    List<String> algorithmNames = new ArrayList<String>();
    algorithmNames.add("Uniform Random");
    algorithmNames.add("BRU");
    algorithmNames.add("ENS");
    algorithmNames.add("LES");
    algorithmNames.add("RDS");

    DefaultPlots.generatePlots(plotName, RESULTS_PATH + experimentName + "/" + AGGREGATE_ANALYSIS_DIR + "/",
                               algorithmNames.size(), algorithmNames);
  }


  private static void gatherAlgorithmPerfomanceData() {
    String gameClassName = "covariant_-1_4_4";
    File gameFileDir = new File(GAME_FILES_PATH + gameClassName);

    double[] ENS_params = new double[] {0.01, 0.025, 0.05, 0.075, 0.1,
                                        0.125, 0.15, 0.175, 0.2, 0.225,
                                        0.25, 0.275, 0.3, 0.35, 0.4,
                                        0.5, 1.0};

    double[] LES_params = new double[] {1000d, 100d, 75d, 50d, 40d,
                                        30d, 25d, 20d, 15d, 12.5d,
                                        10d, 7.5d, 5d, 2.5d, 1d};

    double[] RDS_params = new double[] {0, 0.1, 0.2, 0.3, 0.4,
                                        0.5, 0.6, 0.7, 0.8, 0.9, 1};

    for (double param : LES_params) {
//      EpsilonNashSolver ens = new EpsilonNashSolver();
//      ens.setSamplingMode(EpsilonNashSolver.SamplingMode.ALL_EVEN_PLUS_RANDOM);
//      ens.setDecisionMode(EpsilonNashSolver.DecisionMode.BR_BOLTZMANN);
//      ens.setAbsoluteDeviationSamplingBound(1);
//      ens.setTemperature(param);

//      ReplicatorDynamicsSolver rds = new ReplicatorDynamicsSolver(param);

      QRESolver qres = new QRESolver(param);
      qres.setDecisionMode(QRESolver.DecisionMode.BR);

      int iteration = 0;
      double totEpsilon = 0d;
      double totEntropy = 0d;
      double maxEpsilon = Double.NEGATIVE_INFINITY;
      long startTime = System.currentTimeMillis();

      for (String str : gameFileDir.list()) {
        // not a gamut game file
        if (!str.endsWith(GAMUT_GAME_EXTENSION)) {
          continue;
        }

        // extract the game number
        String id = str.split("\\.")[0];
        String gameFileName = GAME_FILES_PATH + gameClassName + "/" + str;
        //System.out.println("Game file: " + gameFileName);

        // read in the game
        MatrixGame game = GamutParser.readGamutGame(gameFileName);
        GameObserver go = new NoiselessObserver(game);
        go.setDensityBound(1.0d);

//        OutcomeDistribution predictedOutcome = rds.predictOutcome(go);
//        OutcomeDistribution predictedOutcome = ens.predictOutcome(go);
        OutcomeDistribution predictedOutcome = qres.predictOutcome(go);

        //System.out.println("Final predicted outcome: " + predictedOutcome);

        double epsilon = SolverUtils.computeOutcomeStability(game, predictedOutcome);
        double entropy = predictedOutcome.computeEntropy();

        maxEpsilon = Math.max(maxEpsilon, epsilon);
        totEpsilon += epsilon;
        totEntropy += entropy;
        iteration++;
      }

      long totTime = System.currentTimeMillis() - startTime;
      double timePerIteration = (double) totTime / (double) iteration;
      totEpsilon /= iteration;
      totEntropy /= iteration;

      System.out.println(param + " " + totEpsilon + " " + maxEpsilon + " " + totEntropy + " " + timePerIteration);
    }

  }

  private static void gatherParamVisualizationData() {

    String gameFileName = GAME_FILES_PATH + "random_4_4/3.gamut";

    QRESolver qres1000 = new QRESolver(1000);
    QRESolver qres100 = new QRESolver(100);
    QRESolver qres10 = new QRESolver(10);
    QRESolver qres1 = new QRESolver(1);

    MatrixGame game = GamutParser.readGamutGame(gameFileName);
    GameObserver go = new NoiselessObserver(game);
    go.setDensityBound(1.0d);

    OutcomeDistribution predictedOutcome = qres1000.predictOutcome(go);
    ArrayList<Double> tmp1000 = new ArrayList<Double>();
    OutcomeIterator itr = predictedOutcome.iterator();
    while (itr.hasNext()) {
      int[] outcome = itr.next();
      tmp1000.add(predictedOutcome.getProb(outcome));
    }
    Collections.sort(tmp1000);
    Collections.reverse(tmp1000);

    go.reset();
    predictedOutcome = qres100.predictOutcome(go);
    ArrayList<Double> tmp100 = new ArrayList<Double>();
    itr = predictedOutcome.iterator();
    while (itr.hasNext()) {
      int[] outcome = itr.next();
      tmp100.add(predictedOutcome.getProb(outcome));
    }
    Collections.sort(tmp100);
    Collections.reverse(tmp100);

    go.reset();
    predictedOutcome = qres10.predictOutcome(go);
    ArrayList<Double> tmp10 = new ArrayList<Double>();
    itr = predictedOutcome.iterator();
    while (itr.hasNext()) {
      int[] outcome = itr.next();
      tmp10.add(predictedOutcome.getProb(outcome));
    }
    Collections.sort(tmp10);
    Collections.reverse(tmp10);

    go.reset();
    predictedOutcome = qres1.predictOutcome(go);
    ArrayList<Double> tmp1 = new ArrayList<Double>();
    itr = predictedOutcome.iterator();
    while (itr.hasNext()) {
      int[] outcome = itr.next();
      tmp1.add(predictedOutcome.getProb(outcome));
    }
    Collections.sort(tmp1);
    Collections.reverse(tmp1);

    for (int i = 0; i < tmp1000.size(); i++) {
      System.out.println(tmp1000.get(i) + " " + tmp100.get(i) + " " + tmp10.get(i) + " " + tmp1.get(i));
    }
  }
}







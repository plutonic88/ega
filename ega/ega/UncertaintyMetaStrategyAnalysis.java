package ega;

import static ega.Parameters.RESULTS_PATH;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ega.games.EmpiricalMatrixGame;
import ega.games.MatrixGame;
import ega.gnuplottools.BTDPlots;
import ega.gnuplottools.ParamStabilityPlots;
import ega.observers.GameObserver;
import ega.observers.NoiselessObserver;
import ega.observers.NoisyObserver;
import ega.parsers.GamutParser;
import ega.solvers.BRUSolver;
import ega.solvers.EpsilonNashSolver;
import ega.solvers.ExplorationUtil;
import ega.solvers.QRESolver;
import ega.solvers.ReplicatorDynamicsSolver;
import ega.solvers.SolverAnalysis;
import ega.solvers.StabilityAnalysis;
import ega.util.GaussianSampler;

/**
 * Created by IntelliJ IDEA.
 * User: ckiekint
 * Date: Apr 5, 2008
 * Time: 4:45:47 AM
 */
public class UncertaintyMetaStrategyAnalysis {

  //private static final String[] gameClasses = new String[] {"random_4_4", "covariant_-1_4_4", "covariant_1.0_4_4"};
  //private static final String[] gameClasses = new String[] {"random_4_4"};
  //private static final String[] gameClasses = new String[] {"covariant_-1_4_4"};
  private static final String[] gameClasses = new String[] {"covariant_1.0_4_4"};

  private static final int[] incompleteObservationLevels = new int[] {16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
  private static final double[] noisyObservationLevels = new double[] {0d, 0.025, 0.05, 0.075, 0.1, 0.125, 0.15, 0.175, 0.2,
                                                                       0.225, 0.25, 0.275, 0.3, 0.35, 0.4, 0.45, 0.5};

  private static final double[] ENS_params = new double[] {0.01, 0.05, 0.1, 0.15, 0.2, 0.3, 0.4, 0.5, 0.75, 1.0};
  private static final double[] RDS_params = new double[] {0, 0.11, 0.22, 0.33, 0.44, 0.55, 0.66, 0.77, 0.88, 0.99, 1.0}; // shouldn't have 1.0
  private static final double[] LES_params = new double[] {1, 5, 10, 25, 50, 75, 100, 250, 500, 1000};

  private static final HashMap<String, double[]> ENS_noisy_best_params = new HashMap<String, double[]>();
  private static final HashMap<String, double[]> ENS_incomplete_best_params = new HashMap<String, double[]>();
  private static final HashMap<String, double[]> RDS_noisy_best_params = new HashMap<String, double[]>();
  private static final HashMap<String, double[]> RDS_incomplete_best_params = new HashMap<String, double[]>();
  private static final HashMap<String, double[]> LES_noisy_best_params = new HashMap<String, double[]>();
  private static final HashMap<String, double[]> LES_incomplete_best_params = new HashMap<String, double[]>();

  static {
    ENS_noisy_best_params.put("random_4_4", new double[] {0.05, 0.1, 0.1, 0.15, 0.2, 0.2, 0.3, 0.3, 0.3, 0.3, 0.5, 0.4, 0.5, 1.0, 0.75, 0.75, 0.5});
    ENS_noisy_best_params.put("covariant_-1_4_4", new double[] {0.05, 0.05, 0.05, 0.1, 0.1, 0.15, 0.2, 0.3, 0.3, 0.4, 0.5, 0.5, 0.75, 1.0, 0.75, 1.0, 1.0});
    ENS_noisy_best_params.put("covariant_1.0_4_4", new double[] {0.01, 0.05, 0.1, 0.15, 0.15, 0.3, 0.3, 0.4, 0.75, 1.0, 0.5, 1.0, 1.0, 1.0, 0.75, 0.75, 1.0});

    ENS_incomplete_best_params.put("random_4_4", new double[] {0.05, 0.1, 0.15, 0.2, 0.1, 0.2, 0.3, 0.3, 0.15, 0.4, 0.3, 0.5, 0.75, 0.5, 0.4, 0.75});
    ENS_incomplete_best_params.put("covariant_-1_4_4", new double[] {0.05, 0.05, 0.1, 0.15, 0.15, 0.2, 0.3, 0.4, 1.0, 0.5, 1.0, 1.0, 1.0, 1.0, 0.75, 0.4});
    ENS_incomplete_best_params.put("covariant_1.0_4_4", new double[] {0.01, 0.01, 0.1, 0.15, 0.2, 0.2, 0.3, 0.3, 0.3, 0.4, 0.3, 0.4, 0.4, 0.2, 0.3, 0.05});

    RDS_noisy_best_params.put("random_4_4", new double[] {0.0, 0.11, 0.22, 0.33, 0.44, 0.55, 0.55, 0.55, 0.55, 0.66, 0.66, 0.66, 0.66, 0.66, 0.77, 0.88, 0.77});
    RDS_noisy_best_params.put("covariant_-1_4_4", new double[] {0.0, 0.0, 0.0, 0.0, 0.11, 0.11, 0.22, 0.33, 0.44, 0.77, 0.77, 0.77, 0.77, 0.88, 0.88, 0.99, 0.99});
    RDS_noisy_best_params.put("covariant_1.0_4_4", new double[] {0.0, 0.55, 0.55, 0.55, 0.55, 0.66, 0.66, 0.77, 0.77, 0.77, 0.77, 0.88, 0.88, 0.99, 0.88, 0.88, 0.88});

    RDS_incomplete_best_params.put("random_4_4", new double[] {0.0, 0.22, 0.44, 0.44, 0.33, 0.55, 0.66, 0.77, 0.66, 0.66, 0.55, 0.77, 0.66, 0.99, 0.88, 0.88});
    RDS_incomplete_best_params.put("covariant_-1_4_4", new double[] {0.0, 0.0, 0.11, 0.11, 0.44, 0.33, 0.44, 0.66, 0.88, 0.55, 0.66, 0.99, 0.88, 0.77, 0.66, 0.0});
    RDS_incomplete_best_params.put("covariant_1.0_4_4", new double[] {0.33, 0.33, 0.44, 0.44, 0.66, 0.55, 0.77, 0.77, 0.77, 0.77, 0.77, 0.77, 0.88, 1.0, 0.99, 0.88});

    LES_noisy_best_params.put("random_4_4", new double[] {1000.0, 50.0, 500.0, 50.0, 10.0, 10.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 1.0, 1.0, 1.0, 1.0});
    LES_noisy_best_params.put("covariant_-1_4_4", new double[] {1000.0, 1000.0, 500.0, 250.0, 1000.0, 25.0, 10.0, 10.0, 10.0, 5.0, 5.0, 5.0, 5.0, 1.0, 1.0, 1.0, 1.0});
    LES_noisy_best_params.put("covariant_1.0_4_4", new double[] {1000.0, 10.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0});

    LES_incomplete_best_params.put("random_4_4", new double[] {1000.0, 500.0, 10.0, 10.0, 5.0, 5.0, 10.0, 5.0, 5.0, 5.0, 10.0, 5.0, 5.0, 5.0, 1.0, 5.0});
    LES_incomplete_best_params.put("covariant_-1_4_4", new double[] {1000.0, 250.0, 500.0, 50.0, 25.0, 10.0, 10.0, 10.0, 5.0, 10.0, 5.0, 1.0, 1.0, 1.0, 5.0, 10.0});
    LES_incomplete_best_params.put("covariant_1.0_4_4", new double[] {1000.0, 10.0, 250.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 5.0, 1.0, 1.0, 1.0, 50.0, 1000.0});
  }

  public static void paramSetENS_Noisy() {
    String prefix = "Thesis_Final_ParamSet_ENS_Noisy_";

    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GaussianSampler sampler = new GaussianSampler(0, 0);
    GameObserver observer = new NoisyObserver(sampler);
    observer.setDensityBound(1d);
    observer.setDefaultPayoff(0.5d);

    for (double param : ENS_params) {
      EpsilonNashSolver ens = new EpsilonNashSolver();
      ens.setSamplingMode(ExplorationUtil.SamplingMode.ALL_EVEN_PLUS_RANDOM);
      ens.setDecisionMode(EpsilonNashSolver.DecisionMode.BR_BOLTZMANN);
      ens.setAbsoluteDeviationSamplingBound(1);
      ens.setTemperature(param);
      analysis.addAlgorithm(ens, observer);
    }

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);
      analysis.setAlgParams(ENS_params);

      //analysis.setMaxSamples(2);
      analysis.setSamplesPerGameInstance(1);
      for (double observationLevel : noisyObservationLevels) {
        sampler.setStdDev(observationLevel);
        analysis.setSubExperimentName(Double.toString(observationLevel));
        analysis.computeAlgorithmGame();
      }

//      DefaultPlots.generatePlots(experimentName, Parameters.RESULTS_PATH + experimentName + "/" + Parameters
//          .AGGREGATE_ANALYSIS_DIR + "/",analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  public static void paramSetRDS_Noisy() {
    String prefix = "Thesis_Final_ParamSet_RDS_Noisy_";

    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GaussianSampler sampler = new GaussianSampler(0, 0);
    GameObserver observer = new NoisyObserver(sampler);
    observer.setDensityBound(1d);
    observer.setDefaultPayoff(0.5d);

    for (double param : RDS_params) {
      ReplicatorDynamicsSolver rds = new ReplicatorDynamicsSolver(param);
      analysis.addAlgorithm(rds, observer);
    }

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);
      analysis.setAlgParams(RDS_params);

      //analysis.setMaxSamples(2);
      analysis.setSamplesPerGameInstance(1);
      for (double observationLevel : noisyObservationLevels) {
        sampler.setStdDev(observationLevel);
        analysis.setSubExperimentName(Double.toString(observationLevel));
        analysis.computeAlgorithmGame();
      }

//      DefaultPlots.generatePlots(experimentName, Parameters.RESULTS_PATH + experimentName + "/" + Parameters
//          .AGGREGATE_ANALYSIS_DIR + "/",analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  public static void paramSetLES_Noisy() {
    String prefix = "Thesis_Final_ParamSet_LES_Noisy_";

    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GaussianSampler sampler = new GaussianSampler(0, 0);
    GameObserver observer = new NoisyObserver(sampler);
    observer.setDensityBound(1d);
    observer.setDefaultPayoff(0.5d);

    for (double param : LES_params) {
      QRESolver qres = new QRESolver(param);
      qres.setDecisionMode(QRESolver.DecisionMode.BR);
      analysis.addAlgorithm(qres, observer);
    }

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);
      analysis.setAlgParams(LES_params);

      //analysis.setMaxSamples(2);
      analysis.setSamplesPerGameInstance(1);
      for (double observationLevel : noisyObservationLevels) {
        sampler.setStdDev(observationLevel);
        analysis.setSubExperimentName(Double.toString(observationLevel));
        analysis.computeAlgorithmGame();
      }

//      DefaultPlots.generatePlots(experimentName, Parameters.RESULTS_PATH + experimentName + "/" + Parameters
//          .AGGREGATE_ANALYSIS_DIR + "/",analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  public static void paramSetENS_Incomplete() {
    String prefix = "Thesis_Final_ParamSet_ENS_Incomplete_";

    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    for (double param : ENS_params) {
      EpsilonNashSolver ens = new EpsilonNashSolver();
      ens.setSamplingMode(ExplorationUtil.SamplingMode.ALL_EVEN_PLUS_RANDOM);
      ens.setDecisionMode(EpsilonNashSolver.DecisionMode.BR_BOLTZMANN);
      ens.setAbsoluteDeviationSamplingBound(1);
      ens.setTemperature(param);
      analysis.addAlgorithm(ens, observer);
    }

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);
      analysis.setAlgParams(ENS_params);

      //analysis.setMaxSamples(2);
      analysis.setSamplesPerGameInstance(1);
      for (int observationLevel : incompleteObservationLevels) {
        observer.setBound(observationLevel);
        analysis.setSubExperimentName(Double.toString(observationLevel));
        analysis.computeAlgorithmGame();
      }

//      DefaultPlots.generatePlots(experimentName, Parameters.RESULTS_PATH + experimentName + "/" + Parameters
//          .AGGREGATE_ANALYSIS_DIR + "/",analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  public static void paramSetRDS_Incomplete() {
    String prefix = "Thesis_Final_ParamSet_RDS_Incomplete_";

    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    for (double param : RDS_params) {
      ReplicatorDynamicsSolver rds = new ReplicatorDynamicsSolver(param);
      analysis.addAlgorithm(rds, observer);
    }

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);
      analysis.setAlgParams(RDS_params);

      //analysis.setMaxSamples(2);
      analysis.setSamplesPerGameInstance(1);
      for (int observationLevel : incompleteObservationLevels) {
        observer.setBound(observationLevel);
        analysis.setSubExperimentName(Double.toString(observationLevel));
        analysis.computeAlgorithmGame();
      }

//      DefaultPlots.generatePlots(experimentName, Parameters.RESULTS_PATH + experimentName + "/" + Parameters
//          .AGGREGATE_ANALYSIS_DIR + "/",analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  public static void paramSetLES_Incomplete() {
    String prefix = "Thesis_Final_ParamSet_LES_Incomplete_";

    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    for (double param : LES_params) {
      QRESolver qres = new QRESolver(param);
      qres.setDecisionMode(QRESolver.DecisionMode.BR);
      analysis.addAlgorithm(qres, observer);
    }

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);
      analysis.setAlgParams(LES_params);

      //analysis.setMaxSamples(2);
      analysis.setSamplesPerGameInstance(1);
      for (int observationLevel : incompleteObservationLevels) {
        observer.setBound(observationLevel);
        analysis.setSubExperimentName(Double.toString(observationLevel));
        analysis.computeAlgorithmGame();
      }

//      DefaultPlots.generatePlots(experimentName, Parameters.RESULTS_PATH + experimentName + "/" + Parameters
//          .AGGREGATE_ANALYSIS_DIR + "/",analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  public static void combined_Noisy() {
    String prefix = "Thesis_Final_Combined_Noisy_";

    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GaussianSampler sampler = new GaussianSampler(0, 0);
    GameObserver observer = new NoisyObserver(sampler);
    observer.setDensityBound(1d);
    observer.setDefaultPayoff(0.5d);

    // BRU
    BRUSolver bru = new BRUSolver();
    analysis.addAlgorithm(bru, observer);

    // ENS
    EpsilonNashSolver ens = new EpsilonNashSolver();
    ens.setSamplingMode(ExplorationUtil.SamplingMode.ALL_EVEN_PLUS_RANDOM);
    ens.setDecisionMode(EpsilonNashSolver.DecisionMode.BR_BOLTZMANN);
    ens.setAbsoluteDeviationSamplingBound(1);
    ens.setName("ENS");
    analysis.addAlgorithm(ens, observer);

    // RDS
    ReplicatorDynamicsSolver rds = new ReplicatorDynamicsSolver();
    rds.setName("RDS");
    analysis.addAlgorithm(rds, observer);

    // LES
    QRESolver les = new QRESolver();
    les.setDecisionMode(QRESolver.DecisionMode.BR);
    les.setName("LES");
    analysis.addAlgorithm(les, observer);

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      //analysis.setMaxSamples(2);
      analysis.setSamplesPerGameInstance(1);
      int index = 0;
      for (double observationLevel : noisyObservationLevels) {
        sampler.setStdDev(observationLevel);
        ens.setTemperature(ENS_noisy_best_params.get(gameClass)[index]);
        rds.setDelta(RDS_noisy_best_params.get(gameClass)[index]);
        les.setLambda(LES_noisy_best_params.get(gameClass)[index]);
        analysis.setSubExperimentName(Double.toString(observationLevel));
        analysis.computeAlgorithmGame();
        index++;
      }
//      DefaultPlots.generatePlots(experimentName,
//                                 RESULTS_PATH + experimentName + "/" +  AGGREGATE_ANALYSIS_DIR + "/",
//                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  public static void combined_Incomplete() {
    String prefix = "Thesis_Final_Combined_Incomplete_";

    SolverAnalysis analysis = new SolverAnalysis();
    analysis.setNumBenchmarks(0);

    GameObserver observer = new NoiselessObserver();
    observer.setDefaultPayoff(0.5d);

    // BRU
    BRUSolver bru = new BRUSolver();
    analysis.addAlgorithm(bru, observer);

    // ENS
    EpsilonNashSolver ens = new EpsilonNashSolver();
    ens.setSamplingMode(ExplorationUtil.SamplingMode.ALL_EVEN_PLUS_RANDOM);
    ens.setDecisionMode(EpsilonNashSolver.DecisionMode.BR_BOLTZMANN);
    ens.setAbsoluteDeviationSamplingBound(1);
    ens.setName("ENS");
    analysis.addAlgorithm(ens, observer);

    // RDS
    ReplicatorDynamicsSolver rds = new ReplicatorDynamicsSolver();
    rds.setName("RDS");
    analysis.addAlgorithm(rds, observer);

    // LES
    QRESolver les = new QRESolver();
    les.setDecisionMode(QRESolver.DecisionMode.BR);
    les.setName("LES");
    analysis.addAlgorithm(les, observer);

    for (String gameClass : gameClasses) {
      String experimentName = prefix + gameClass;
      analysis.setExperimentName(experimentName);
      analysis.setGameClassName(gameClass);

      //analysis.setMaxSamples(2);
      analysis.setSamplesPerGameInstance(1);
      int index = 0;
      for (int observationLevel : incompleteObservationLevels) {
        observer.setBound(observationLevel);
        ens.setTemperature(ENS_incomplete_best_params.get(gameClass)[index]);
        rds.setDelta(RDS_incomplete_best_params.get(gameClass)[index]);
        les.setLambda(LES_incomplete_best_params.get(gameClass)[index]);
        analysis.setSubExperimentName(Integer.toString(observationLevel));
        analysis.computeAlgorithmGame();
        index++;
      }
//      DefaultPlots.generatePlots(experimentName,
//                                 RESULTS_PATH + experimentName + "/" +  AGGREGATE_ANALYSIS_DIR + "/",
//                                 analysis.getNumAlgorithms(), analysis.getAlgorithmNames());
    }
  }

  public static void extractParameterSettings() {
    String prefix = "Thesis_Final_ParamSet_ENS_Incomplete_";

    StringBuilder sb = new StringBuilder();
    String gameFileName = "algorithm_game_symmetric.gamut";
    for (String gameClass : gameClasses) {
      for (int observationLevel : incompleteObservationLevels) {
        String fileName = RESULTS_PATH + prefix + gameClass + "/" + Double.toString(observationLevel) + "/" + gameFileName;
        MatrixGame mg = GamutParser.readGamutGame(fileName);
        EmpiricalMatrixGame emg = new EmpiricalMatrixGame(mg);

        StabilityAnalysis stability = new StabilityAnalysis(emg, 1);
        double[] btds = stability.getPureProfileBTD();

        double min = Double.POSITIVE_INFINITY;
        int minIndex = -1;
        for (int i = 1; i < btds.length; i++) {
          if (btds[i] < min) {
            min = btds[i];
            minIndex = i-1;
          }
        }
        sb.append(ENS_params[minIndex]).append(", ");
      }
      sb.append("\n");
    }
    System.out.println(sb.toString());
  }


  public static void genStabilityPlot() {
    ParamStabilityPlots.genPlots("Thesis_Final_ParamSet_ENS_Incomplete_random_4_4",
                                 "Incomplete Observations (ENS)",
                                 incompleteObservationLevels,
                                 ENS_params);
  }

  public static void genPairedBTDPlot() {
    List<String> stratNames = new ArrayList<String>();
    stratNames.add("BRU");
    stratNames.add("ENS");
    stratNames.add("RDS");
    stratNames.add("LES");

    BTDPlots.genPairedPlots("Thesis_Final_Combined_Noisy_covariant_-1_4_4",
                            "Stochastic Observations",
                            4, stratNames);
  }
  
}

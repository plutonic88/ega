package ega.gnuplottools;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import ega.EGAUtils;
import ega.Parameters;
import ega.games.EmpiricalMatrixGame;
import ega.games.MatrixGame;
import ega.parsers.GamutParser;
import ega.solvers.StabilityAnalysis;

/**
 * Utility class for generating gnuplot graphs from the meta-strategy analysis data
 */

public class ParamStabilityPlots {

  private static String fileName;
  private static final String gameFileName = "algorithm_game_symmetric.gamut";
  private static final String dataFileName = "most_stable_param_settings.txt";
  private static final String scriptFileName = "temp_script.p";


  private static final java.util.List<String> cmd = new ArrayList<String>();
  private static final ProcessBuilder pb = (new ProcessBuilder(cmd)).redirectErrorStream(true);

  // static class
  private ParamStabilityPlots() {
  }

  public static void genParamStabilityPlots() {

    int[] IncompleteObservation_noiseLevels = {16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
    double[] IncompleteObservation_ENS_params =
        new double[] {Integer.MIN_VALUE, 0.01, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.4, 0.5, 0.75, 1};
    double[] IncompleteObservation_LES_params =
        new double[] {Integer.MIN_VALUE, 1000d, 500d, 250d, 100d, 50d, 25d, 10d, 5d, 2.5d, 1d, 0.5d};
    double[] IncompleteObservation_RDS_params =
        new double[] {Integer.MIN_VALUE, 0, 0.1d, 0.2d, 0.3d, 0.4d, 0.5d, 0.6d, 0.7d, 0.8d, 0.9d, 1.0d};


    double[] Noisy_noiseLevels = {0.0, 0.01, 0.03, 0.05, 0.08, 0.1, 0.125, 0.15, 0.175, 0.2,
                                  0.225, 0.25, 0.275, 0.3, 0.35, 0.4, 0.45, 0.5};

    double[] Noisy_ENS_params = new double[] {Integer.MIN_VALUE, 0.01, 0.025, 0.05, 0.075, 0.1, 0.125, 0.15, 0.175, 0.2,
                                              0.225, 0.25, 0.275, 0.3, 0.35, 0.4, 0.5, 1.0};
    double[] Noisy_RDS_params =
        new double[] {Integer.MIN_VALUE, 0, 0.1d, 0.2d, 0.3d, 0.4d, 0.5d, 0.6d, 0.7d, 0.8d, 0.9d, 1.0d};
    double[] Noisy_LES_params = new double[] {Integer.MIN_VALUE, 1000d, 100d, 75d, 50d, 40d, 30d, 25d, 20d, 15d, 12.5d,
                                              10d, 7.5d, 5d, 2.5d, 1d};

//    ParamStabilityPlots.genPlots("QRE_random_4_4",
//                                 "Most Stable LES Parameters\\nNoisy Observation, Uniform Random Games",
//                                 Noisy_noiseLevels,
//                                 Noisy_LES_params);

//    genPlots("IncompleteObservation_ENS_covariant_1.0_4_4",
//                                 "None",
//                                 IncompleteObservation_noiseLevels,
//                                 IncompleteObservation_ENS_params);
//    genPlots("IncompleteObservation_ENS_covariant_-1_4_4",
//                                 "None",
//                                 IncompleteObservation_noiseLevels,
//                                 IncompleteObservation_ENS_params);
//    genPlots("IncompleteObservation_RDS_covariant_1.0_4_4",
//                                 "None",
//                                 IncompleteObservation_noiseLevels,
//                                 IncompleteObservation_RDS_params);
//    genPlots("IncompleteObservation_RDS_covariant_-1_4_4",
//                                 "None",
//                                 IncompleteObservation_noiseLevels,
//                                 IncompleteObservation_RDS_params);
//    genPlots("IncompleteObservation_QRE_covariant_1.0_4_4",
//                                 "None",
//                                 IncompleteObservation_noiseLevels,
//                                 IncompleteObservation_LES_params);
//    genPlots("IncompleteObservation_QRE_covariant_-1_4_4",
//                                 "None",
//                                 IncompleteObservation_noiseLevels,
//                                 IncompleteObservation_LES_params);
  }

  public static void genPlots(String experimentName, String title, int[] noiseLevels, double[] paramMapping) {

    fileName = experimentName + "_most_stable.ps";

    String dataPath = Parameters.RESULTS_PATH + experimentName + "/aggregate_analysis/";
    File outFile = new File(dataPath + dataFileName);

    try {

      // first create the data file used to plot the scores
      PrintWriter out = new PrintWriter(outFile);

      for (int noiseLevel : noiseLevels) {
        MatrixGame mg = GamutParser.readGamutGame(
                Parameters.RESULTS_PATH + experimentName + "/" + Double.toString(noiseLevel) + "/" + gameFileName);
        EmpiricalMatrixGame emg = new EmpiricalMatrixGame(mg);

        StabilityAnalysis stabilityAnalysis = new StabilityAnalysis(emg, 1);
        double mostStableParam = paramMapping[stabilityAnalysis.getMostStablePureProfile()-1];

        out.print((16-noiseLevel) + " ");
        out.println(mostStableParam);
      }
      out.close();

      // create the script to run gnuplot
      File tmpFile = new File(dataPath + scriptFileName);
      FileWriter outFW = new FileWriter(tmpFile, false);
      outFW.write(genGnuplotScript(dataPath, title));
      outFW.flush();
      outFW.close();

      // actually run gnuplot
      runGnuplot(tmpFile.toString(), dataPath);
      //tmpFile.delete();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static String genGnuplotScript(String dataPath,
                                        String title) {

    String dataFile = dataPath + dataFileName;

    StringBuilder sb = EGAUtils.getSB();
    sb.setLength(0);

//    sb.append("set title \"").append(strat1Name).append(" vs. ").append(strat2Name)
//            .append("\\n").append(gameClassDescription).append("\"\n");
    sb.append("set title \"").append(title).append("\"\n");
    sb.append("set key off\n");
    sb.append("set xlabel \"Number of Profiles Not Revealed\"\n");
    sb.append("set ylabel \"Most Stable Tau\"\n");
    sb.append("set yrange [0:1]\n");
    //sb.append("set logscale y\n");
    sb.append("set xrange [0:15]\n");
    //sb.append("set terminal epslatex monochrome\n");
    //sb.append("set terminal postscript eps monochrome\n");
    //sb.append("set pointsize 2.0");
    sb.append("set terminal postscript landscape monochrome dl 4.0 lw 4.0 \"Helvica-Bold\" 24\n");
    sb.append("set output \"").append(dataPath).append(fileName).append("\"\n");
    sb.append("plot ");

    sb.append("\"").append(dataFile).append("\"");
    sb.append(" using 1:2");
    sb.append(" with lines");
    sb.append("\n");

    return EGAUtils.returnSB(sb);
  }

  /**
   * Invoke gnuplot on each script
   */
  public static void runGnuplot(String scriptFileName, String dataPath) {
    String psFile = dataPath + fileName;
    String tmpFile = dataPath + "tmp_rotated.eps";

    try {
      cmd.clear();
      cmd.add("gnuplot");
      cmd.add("<");
      cmd.add(scriptFileName);

      Process proc = pb.start();
      InputStream is = proc.getInputStream();
      proc.waitFor();
      while (is.available() > 0) {
        System.out.write(is.read());
      }

//      cmd.clear();
//      cmd.add("pstops");
//      cmd.add("U(8.27in,11.69in)");
//      cmd.add(psFile);
//      cmd.add(tmpFile);
//
//      proc = pb.start();
//      is = proc.getInputStream();
//      proc.waitFor();
//      while (is.available() > 0) {
//        System.out.write(is.read());
//      }
//
//      cmd.clear();
//      cmd.add("mv");
//      cmd.add(tmpFile);
//      cmd.add(psFile);
//
//      proc = pb.start();
//      is = proc.getInputStream();
//      proc.waitFor();
//      while (is.available() > 0) {
//        System.out.write(is.read());
//      }


    } catch (Exception e) {
      throw new RuntimeException("Exception while running gnuplot: " + e.getMessage());
    }
  }
}

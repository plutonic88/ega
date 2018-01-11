package ega.gnuplottools;

import static ega.Parameters.RESULTS_PATH;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import ega.EGAUtils;
import ega.games.MatrixGame;
import ega.parsers.GamutParser;

/**
 * Utility class for generating gnuplot graphs from the meta-strategy analysis data
 */

public class TwoByTwoScorePlots {

  private static String fileName;
  private static final String gameFileName = "algorithm_game_symmetric.gamut";
  private static final String dataFileName = "scoredata.gamut";
  private static final String scriptFileName = "temp_script.p";


  private static final java.util.List<String> cmd = new ArrayList<String>();
  private static final ProcessBuilder pb = (new ProcessBuilder(cmd)).redirectErrorStream(true);

  private static int[] outcome11 = new int[] {1, 1};
  private static int[] outcome12 = new int[] {1, 2};
  private static int[] outcome22 = new int[] {2, 2};

  // static class
  private TwoByTwoScorePlots() {
  }

  public  static void genTwoByTwoScorePlots() {
    double[] stdDevs = {0.0, 0.001, 0.005, 0.01, 0.015, 0.02, 0.03, 0.05, 0.08, 0.1, 0.125, 0.15, 0.175,
                        0.2, 0.225, 0.25, 0.275, 0.3, 0.35, 0.4, 0.45, 0.5};

    TwoByTwoScorePlots.genScorePlots("BRUvsPSNE_random_4_4_PSNE", "BRU", "NPSNE",
                                     "4x4 Uniform Random with PSNE", stdDevs);
//    TwoByTwoScorePlots.genScorePlots("BRUvsPSNE_exponential_4_4_PSNE", "BRU", "NPSNE",
//                                     "4x4 Exponential Random with PSNE", stdDevs);
//    TwoByTwoScorePlots.genScorePlots("BRUvsPSNE_covariant_1.0_4_4_PSNE", "BRU", "NPSNE",
//                                     "4x4 Common Interest with PSNE", stdDevs);
//    TwoByTwoScorePlots.genScorePlots("BRUvsPSNE_covariant_-1_4_4_PSNE", "BRU", "NPSNE",
//                                     "4x4 Zero Sum with PSNE", stdDevs);

//    TwoByTwoScorePlots.genScorePlots("BRUvsPSNE_random_4_4", "BRU", "NPSNE",
//                                     "4x4 Uniform Random", stdDevs);
//    TwoByTwoScorePlots.genScorePlots("BRUvsPSNE_exponential_4_4", "BRU", "NPSNE",
//                                     "4x4 Exponential Random", stdDevs);
//    TwoByTwoScorePlots.genScorePlots("BRUvsPSNE_covariant_1.0_4_4", "BRU", "NPSNE",
//                                     "4x4 Common Interest", stdDevs);
//    TwoByTwoScorePlots.genScorePlots("BRUvsPSNE_covariant_-1_4_4", "BRU", "NPSNE",
//                                     "4x4 Zero Sum", stdDevs);
//
//    TwoByTwoScorePlots.genScorePlots("BRUvsQRE_random_4_4_PSNE", "BRU", "NLE",
//                                     "4x4 Uniform Random with PSNE", stdDevs);
//    TwoByTwoScorePlots.genScorePlots("BRUvsQRE_exponential_4_4_PSNE", "BRU", "NLE",
//                                     "4x4 Exponential Random with PSNE", stdDevs);
//    TwoByTwoScorePlots.genScorePlots("BRUvsQRE_covariant_1.0_4_4_PSNE", "BRU", "NLE",
//                                     "4x4 Common Interest with PSNE", stdDevs);
//    TwoByTwoScorePlots.genScorePlots("BRUvsQRE_covariant_-1_4_4_PSNE", "BRU", "NLE",
//                                     "4x4 Zero Sum with PSNE", stdDevs);
//
//    TwoByTwoScorePlots.genScorePlots("BRUvsQRE_random_4_4", "BRU", "NLE",
//                                     "4x4 Uniform Random", stdDevs);
//    TwoByTwoScorePlots.genScorePlots("BRUvsQRE_exponential_4_4", "BRU", "NLE",
//                                     "4x4 Exponential Random", stdDevs);
//    TwoByTwoScorePlots.genScorePlots("BRUvsQRE_covariant_1.0_4_4", "BRU", "NLE",
//                                     "4x4 Common Interest", stdDevs);
//    TwoByTwoScorePlots.genScorePlots("BRUvsQRE_covariant_-1_4_4", "BRU", "NLE",
//                                     "4x4 Zero Sum", stdDevs);

  }

  public static void genScorePlots(String experimentName, String strat1Name, String strat2Name,
                                   String gameClassDescription, double[] noiseLevels) {

    fileName = experimentName + "_2x2ScorePlot.ps";

    String dataPath = RESULTS_PATH + experimentName + "/aggregate_analysis/";
    File outFile = new File(dataPath + dataFileName);

    try {

      // first create the data file used to plot the scores
      PrintWriter out = new PrintWriter(outFile);
      for (double noiseLevel : noiseLevels) {
        MatrixGame mg =
                GamutParser.readGamutGame(RESULTS_PATH + experimentName + "/" + noiseLevel + "/" + gameFileName);
        out.print(noiseLevel + " ");
        out.print(mg.getPayoffs(outcome11)[0] + " ");
        out.print(mg.getPayoffs(outcome12)[0] + " ");
        out.print(mg.getPayoffs(outcome12)[1] + " ");
        out.print(mg.getPayoffs(outcome22)[0] + " ");
        out.println();
      }
      out.close();

      // create the script to run gnuplot
      File tmpFile = new File(dataPath + scriptFileName);
      FileWriter outFW = new FileWriter(tmpFile, false);
      outFW.write(genGnuplotScript(dataPath, strat1Name, strat2Name, gameClassDescription));
      outFW.close();

      // actually run gnuplot
      runGnuplot(tmpFile.toString(), dataPath);
      tmpFile.delete();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static String genGnuplotScript(String dataPath,
                                        String strat1Name, String strat2Name,
                                        String gameClassDescription) {

    String dataFile = dataPath + dataFileName;

    StringBuilder sb = EGAUtils.getSB();
    sb.setLength(0);

//    sb.append("set title \"").append(strat1Name).append(" vs. ").append(strat2Name)
//            .append("\\n").append(gameClassDescription).append("\"\n");
    sb.append("set title \"").append(gameClassDescription).append("\"\n");
    sb.append("set title \"").append("Uniform Random Games with PSNE").append("\"\n");
    sb.append("set key top right\n");
    sb.append("set xlabel \"Observation Noise Standard Deviation\"\n");
    sb.append("set ylabel \"Meta-Strategy Payoffs\"\n");
    sb.append("set yrange [0.5:0.85]\n");
    //sb.append("set terminal epslatex monochrome\n");
    //sb.append("set terminal postscript eps monochrome\n");
    //sb.append("set pointsize 2.0");
    sb.append("set terminal postscript landscape monochrome \"Helvica-Bold\" 24\n");
    sb.append("set output \"").append(dataPath).append(fileName).append("\"\n");
    sb.append("plot ");

    sb.append("\"").append(dataFile).append("\"");
    sb.append(" using 1:2");
    sb.append(" title \"").append(strat1Name).append(" vs. ").append(strat1Name).append("\"");
    sb.append(" with linespoints lw 4.0 ps 1.5");
    sb.append(", \\");
    sb.append("\n");

    sb.append("\"").append(dataFile).append("\"");
    sb.append(" using 1:3");
    sb.append(" title \"").append(strat1Name).append(" vs. ").append(strat2Name).append("\"");
    sb.append(" with linespoints lw 4.0 ps 1.5");
    sb.append(", \\");
    sb.append("\n");

    sb.append("\"").append(dataFile).append("\"");
    sb.append(" using 1:4");
    sb.append(" title \"").append(strat2Name).append(" vs. ").append(strat1Name).append("\"");
    sb.append(" with linespoints lw 4.0 ps 1.5");
    sb.append(", \\");
    sb.append("\n");

    sb.append("\"").append(dataFile).append("\"");
    sb.append(" using 1:5");
    sb.append(" title \"").append(strat2Name).append(" vs. ").append(strat2Name).append("\"");
    sb.append(" with linespoints lw 4.0 ps 1.5");
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
//

    } catch (Exception e) {
      throw new RuntimeException("Exception while running gnuplot: " + e.getMessage());
    }
  }
}

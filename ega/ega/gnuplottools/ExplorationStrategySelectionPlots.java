package ega.gnuplottools;

import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;

import ega.EGAUtils;
import ega.Parameters;

/**
 * Utility class for generating gnuplot graphs from the meta-strategy analysis data
 */

public class ExplorationStrategySelectionPlots {

  private static final String[] fileNames = {"Dominated.ps",
                                             "Most_stable_payoffs.ps",
                                             "Nash_payoffs.ps",
                                             "Nash_rank.ps",
                                             "Top10_payoffs.ps",
                                             "Top20_payoffs.ps",
                                             "Uniform_payoffs.ps",
                                             "Uniform_rank.ps",
                                             "Most_stable_profile.ps",
                                             "MinBTD_rank.ps",
                                             "Average_Profile.ps",
                                             "PureProfilePayoffs.ps",
                                             "PureProfileBTD.ps"};

  private static final String[] metricTitles = {"Dominated Counts",
                                                "Payoffs in most stable context",
                                                "Payoffs in Nash set context",
                                                "Ranking according to Nash Context Payoffs",
                                                "Payoffs in 10% most stable context",
                                                "Payoffs in 20% most stable context",
                                                "Payoffs in uniform context",
                                                "Ranking according to Uniform Context Payoffs",
                                                "Most stable profile BTD",
                                                "Ranking according to minimum benefit to deviating",
                                                "Average profile BTD",
                                                "Payoffs in symmetric pure profile",
                                                "Homogenous Profile Regret"};

  private static final Point[] metricColumns = {new Point(2, 5),
                                                new Point(2, 6),
                                                new Point(2, 7),
                                                new Point(2, 8),
                                                new Point(2, 9),
                                                new Point(2, 10),
                                                new Point(2, 11),
                                                new Point(2, 12),
                                                new Point(2, 13),
                                                new Point(2, 14),
                                                new Point(2, 15),
                                                new Point(2, 16),
                                                new Point(2, 17)};

  private static final java.util.List<String> cmd = new ArrayList<String>();
  private static final ProcessBuilder pb = (new ProcessBuilder(cmd)).redirectErrorStream(true);

  // static class
  private ExplorationStrategySelectionPlots() {
  }

  public static void generatePlots(String experimentName, String dataPath,
                                   int numStrategies, java.util.List<String> strategyNames) {
    try {
      File tmpFile = new File(dataPath + "temp_script.p");
      for (int metric = 0; metric < metricTitles.length; metric++) {
        FileWriter out = new FileWriter(tmpFile, false);
        out.write(genGnuplotScript(experimentName, dataPath, numStrategies, metric, strategyNames));
        out.close();
        runGnuplot(tmpFile.toString(), dataPath, fileNames[metric]);
      }
      tmpFile.delete();
    } catch (Exception e) {
      throw new RuntimeException("Exception while generating gnuplots: " + e.getMessage());
    }
  }

  public static String genGnuplotScript(String experimentName, String dataPath,
                                        int numStrategies, int metric, java.util.List<String> strategyNames) {

    //int[] mask = {0,0,0,1,1,1,1,1,1,1};

    StringBuilder sb = EGAUtils.getSB();
    sb.setLength(0);

//    sb.append("set title \"Meta-Strategy Analysis: ").append(experimentName).append("\\n").append(metricTitles[metric])
//            .append("\"\n");
//    sb.append("set title \"Meta-Strategy Analysis\\n").append(experimentName).append("\"\n");
    sb.append("set title\"").append(experimentName).append("\"\n");

    sb.append("set key top left\n");
    sb.append("set xzeroaxis\n");
    sb.append("set xlabel \"Profiles Explored\"\n");
    sb.append("set ylabel \"").append(metricTitles[metric]).append("\"\n");
    sb.append("set terminal postscript landscape monochrome \"Helvica-Bold\" 24\n\n");
    sb.append("set output \"").append(dataPath).append(fileNames[metric]).append("\"\n");
    sb.append("plot ");

    for (int strat = 1; strat <= numStrategies; strat++) {
      //if (mask[strat] == 0) continue;

      Point cols = metricColumns[metric];
      sb.append("\"").append(dataPath).append(strat).append(Parameters.EXTENSION).append("\"");
      sb.append(" using ").append(cols.x).append(":").append(cols.y);
      sb.append(" title \"").append(strategyNames.get(strat - 1)).append("\"");
      sb.append(" with linespoints lw 5 ps 1.75");
      if (strat < numStrategies) {
        sb.append(", \\");
      }
      sb.append("\n");
    }
    return EGAUtils.returnSB(sb);
  }

  /**
   * Invoke gnuplot on each script
   */
  public static void runGnuplot(String scriptFileName, String dataPath, String fileName) {
    String psFile = dataPath + fileName;
    String tmpFile = dataPath + "tmp_rotated.ps";

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

      cmd.clear();
      cmd.add("pstops");
      cmd.add("U(8.27in,11.69in)");
      cmd.add(psFile);
      cmd.add(tmpFile);

      proc = pb.start();
      is = proc.getInputStream();
      proc.waitFor();
      while (is.available() > 0) {
        System.out.write(is.read());
      }

      cmd.clear();
      cmd.add("mv");
      cmd.add(tmpFile);
      cmd.add(psFile);

      proc = pb.start();
      is = proc.getInputStream();
      proc.waitFor();
      while (is.available() > 0) {
        System.out.write(is.read());
      }
    } catch (Exception e) {
      throw new RuntimeException("Exception while running gnuplot: " + e.getMessage());
    }
  }
}

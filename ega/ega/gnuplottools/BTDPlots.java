package ega.gnuplottools;

import static ega.Parameters.EXTENSION;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ega.EGAUtils;
import ega.Parameters;

/**
 * Utility class for generating gnuplot graphs from the meta-strategy analysis data
 */

public class BTDPlots {

  private static final String extension = ".txt";
  private static final String prefix = "BTDAverages_";
  private static final String pairedPrefix = "BTDPairedMaxes_";
  private static final String scriptFileName = "temp_script.p";

  private static final java.util.List<String> cmd = new ArrayList<String>();
  private static final ProcessBuilder pb = (new ProcessBuilder(cmd)).redirectErrorStream(true);

  // static class
  private BTDPlots() {
  }

  public static void genPairedPlots(String experimentName, String title, int numStrategies, List<String> stratNames) {
    String dataPath = Parameters.RESULTS_PATH + experimentName + "/aggregate_analysis/";

    try {

//      // first create the data file used to plot the scores
//      PrintWriter out = new PrintWriter(outFile);
//
//      for (int noiseLevel : noiseLevels) {
//        MatrixGame mg = GamutParser.readGamutGame(
//                Parameters.RESULTS_PATH + experimentName + "/" + Double.toString(noiseLevel) + "/" + gameFileName);
//        EmpiricalMatrixGame emg = new EmpiricalMatrixGame(mg);
//
//        StabilityAnalysis stabilityAnalysis = new StabilityAnalysis(emg, 1);
//        double mostStableParam = paramMapping[stabilityAnalysis.getMostStablePureProfile()-1];
//
//        out.print((16-noiseLevel) + " ");
//        out.println(mostStableParam);
//      }
//      out.close();

      // create the script to run gnuplot
      File tmpFile = new File(dataPath + scriptFileName);
      FileWriter outFW = new FileWriter(tmpFile, false);
      outFW.write(genPairedGnuplotScript(dataPath, title, numStrategies, stratNames));
      outFW.flush();
      outFW.close();

      // actually run gnuplot
      runPairedGnuplot(tmpFile.toString(), dataPath);
      //tmpFile.delete();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static String genPairedGnuplotScript(String dataPath,
                                        String title,
                                        int numStrategies,
                                        List<String> stratNames) {
    StringBuilder sb = EGAUtils.getSB();
    sb.setLength(0);

    sb.append("set title\"").append(title).append("\"\n");
    sb.append("set key top right\n");
    sb.append("set xzeroaxis\n");
    sb.append("set xlabel \"Observation Noise Standard Deviation\"\n");
    sb.append("set ylabel \"").append("Homogeneous Profile Regret").append("\"\n");
    sb.append("set terminal postscript landscape monochrome \"Helvica-Bold\" 24\n\n");
    sb.append("set output \"").append(dataPath).append("BTDPairedMaxes.ps").append("\"\n");
    sb.append("plot ");

    for (int strat = 1; strat <= numStrategies; strat++) {
      sb.append("\"").append(dataPath).append(pairedPrefix).append(strat).append(EXTENSION).append("\"");
      //sb.append(" using (16-$1):2:3 ");
      sb.append(" title \"").append(stratNames.get(strat-1)).append("\"");
      sb.append(" with yerrorlines lw 5 ps 1.75");
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
  public static void runPairedGnuplot(String scriptFileName, String dataPath) {
    String psFile = dataPath + "BTDPairedMaxes.ps";
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

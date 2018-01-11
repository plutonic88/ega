package ega;

import static ega.Parameters.GAME_FILES_PATH;
import ega.games.EmpiricalMatrixGame;
import ega.games.SymmetricMatrixGame;
import ega.output.GambitOutput;
import ega.output.SimpleOutput;
import ega.parsers.EGATParser;
import ega.solvers.SolverAnalysis;

/**
 * Created by IntelliJ IDEA.
 * User: ckiekint
 * Date: Apr 5, 2008
 * Time: 4:45:47 AM
 */
public class SCMAnalysis {

  public static void analyzeSCMGames() {
    String path = GAME_FILES_PATH + "/SCM_testbed/";
    String extension = ".xml";
    String name;

//    name = "DM07-test1";
//    analyzeGame(path, name, extension, true);
//
//    name = "DM07-test2";
//    analyzeGame(path, name, extension, true);
//
//    name = "DM07-test3";
//    analyzeGame(path, name, extension, true);
//
//    name = "DM07-test4";
//    analyzeGame(path, name, extension, true);
//
//    name = "DM07-test5";
//    analyzeGame(path, name, extension, true);
//
//    name = "DM07-test6";
//    analyzeGame(path, name, extension, true);
//
//    name = "DM07-test7";
//    analyzeGame(path, name, extension, true);
//
//    name = "DM07-duration-test";
//    analyzeGame(path, name, extension, true);

    name = "TT07F-deviation";
    analyzeGame(path, name, extension, true);

    name = "TT07SF-deviation";
    analyzeGame(path, name, extension, true);

//    name = "All-07Tournament";
//    analyzeGame(path, name, extension, true);
  }

  public static void analyzeGame(String path, String name, String extension, boolean symmetric) {
    SymmetricMatrixGame smg = EGATParser.readSymmetricGame(path + name + extension);
    EmpiricalMatrixGame emg = new EmpiricalMatrixGame(smg);
    GambitOutput.writeGame(path + name + ".gambit", emg);
    SimpleOutput.writeGame(path + name + ".game", emg);

    SolverAnalysis.outputVerboseAnalysis(path, name + "_verbose_analysis.out",
                                         emg, 50, symmetric, 0);

//    MixedStrategy ms = new MixedStrategy(new double[]{0d,0d,0.55742d,0.25278,0.1898});
//    //MixedStrategy ms = new MixedStrategy(new double[]{0d,0.558d,0.254,0.188,0d});
//    List<MixedStrategy> stratList = new ArrayList<MixedStrategy>();
//    for (int i = 0; i < 3; i++) stratList.add(ms);
//    OutcomeDistribution od = new OutcomeDistribution(stratList);
//
//    ActionData ad = SolverUtils.computePureStrategyPayoffs(emg, od, false);
//    System.out.println(ad);

//    QRESolver qre = new QRESolver(10000d);
//    File outFile = new File(path + name + "_qre.out");
//    try {
//      PrintWriter pw = new PrintWriter(outFile);
//      pw.write(qre.getQRE(emg));
//      pw.close();
//    } catch (IOException e) {
//      throw new RuntimeException("Error writing qre: " + e.getMessage());
//    }

  }

}

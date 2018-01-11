package ega;

import java.io.PrintWriter;

import ega.games.MatrixGame;
import ega.output.SimpleOutput;
import ega.parsers.GamutParser;

public class RegretClustering {

	public static MatrixGame doRegretTable(MatrixGame matgame, int cappedval)
	{
		MatrixGame regrettable = RegretCalculator.getRegretPayoffs(matgame);
		double[] maxregret = RegretCalculator.getMaxRegret(regrettable);
		//RegretCalculator.capTheRegrets(regrettable, maxregret, cappedval);
		
		return regrettable;
	}

	public static void doRegretClustering() 
	{
		// TODO Auto-generated method stub
		MatrixGame mg = new MatrixGame(GamutParser.readGamutGame(Parameters.GAME_FILES_PATH+"testgame"+Parameters.GAMUT_GAME_EXTENSION));
		MatrixGame regrettable = RegretClustering.doRegretTable(mg, 50);
		String gamename = Parameters.GAME_FILES_PATH+"r-testgame"+Parameters.GAMUT_GAME_EXTENSION;
		try
		{

			PrintWriter pw = new PrintWriter(gamename,"UTF-8");
			SimpleOutput.writeGame(pw,regrettable);
			pw.close();
		}
		catch(Exception ex){
			System.out.println("StrategyMapping class :something went terribly wrong during regret calculation ");
		}
		System.out.println();
		
	}
}

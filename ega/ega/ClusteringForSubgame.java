package ega;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ega.games.Game;
import ega.games.MatrixGame;
import ega.log.Logger;
import ega.parsers.GamutParser;

public class ClusteringForSubgame 
{
	final static int RAND_ACTION_IN_CLUSTER = 1;
	final static int ACTION_IN_CLUSTER = 2;
	public static Random randint = new Random();

	public static void testSubgameClustering() throws Exception
	{
		MatrixGame tstgame = new MatrixGame(GamutParser.readGamutGame(Parameters.GAME_FILES_PATH+"0"+Parameters.GAMUT_GAME_EXTENSION));
		//GameReductionBySubGame.printgame(tstgame, 0);
		//clusterActionsV1(3,tstgame);

		//List<Integer>[][] clusters = clusterActionsV3(10,tstgame);
		int numberofcluster = 10;
		List<Integer>[][] clusters = hillClimbingClusteringV4(numberofcluster, tstgame);
		double [][] delta = new double[2][numberofcluster];



		//boolean changed = checkIfClustersChanged(clusters1, clusters2);
		calculateDeltas(delta, clusters, 2, numberofcluster, tstgame);
		printClusters(clusters, 0);
		printClusters(clusters, 1);
		System.out.print("x");
		//testInitializationOfClusters(tstgame,4);



	}

	private static void testInitializationOfClusters(MatrixGame game, int numerofclusters) {
		// TODO Auto-generated method stub
		List<Integer>[][] clusters = getEmptyClusters(2, numerofclusters);
		initialiZeClustersV2(clusters, game);
		System.out.println("hi");

	}

	/**
	 * Does kmean clustering for games, for subgame solution concept
	 * Iterates over the actions
	 * @param numberofclusters
	 * @param game
	 * @return clusters for players
	 * @throws Exception 
	 */
	public static List<Integer>[][] clusterActionsV1(int numberofclusters, MatrixGame game ) throws Exception
	{
		int numberofplayers = game.getNumPlayers();
		int[] numactions = game.getNumActions();
		int[] clustersize = {numactions[0]/numberofclusters, numactions[1]/numberofclusters};
		double[][][] centroids = new double[numberofplayers][numberofclusters][];
		double [][] delta = new double[numberofplayers][numberofclusters];

		/**
		 * create an empty cluster object
		 */
		List<Integer>[][] clusters = getEmptyClusters(numberofplayers,numberofclusters);
		/**
		 * initialize cluster with random actions
		 */
		initilizeClusters(clusters,clustersize, numactions, RAND_ACTION_IN_CLUSTER);
		/**
		 * create  empty centroid object to contain the centroids
		 */
		//createEmptyCentroids(centroids, numactions,clustersize);
		/**
		 * calculate the centroids
		 */
		//calculateCentroids(centroids,clusters,numactions,game);

		int iteration = 0;
		while(iteration++ < 50)
		{

			int playercounter = 0;
			List<Integer>[][] newclusters = getEmptyClusters(numberofplayers,numberofclusters);
			for(int player=0; player<2; player++)
			{
				/**
				 * a cluster object for new clustering 
				 */

				/**
				 * calculate delta for every player
				 */
				calculateDeltas(delta,clusters,numberofplayers,numberofclusters, game);
				//printDeltas(delta);
				double maxdelta[] = findMaxDelta(delta);
				System.out.println("Iteration "+iteration+" Maxdelta " + maxdelta[0] + " , "+ maxdelta[1]);
				Logger.logit("\nIteration "+iteration+" Maxdelta " + maxdelta[0] + " , "+ maxdelta[1]);
				/**
				 * test
				 */
				//List<Integer>[][] dummyparition = createDummyPartition();
				//calculateDeltas(delta,dummyparition,2,3, game);
				/**
				 * For every action calculate the delta if it is added to a cluster
				 * [#player][#action][#cluster]
				 */
				System.out.println("Cluster before actions reassignment for player "+player);
				Logger.logit("\nCluster before actions reassignment for player "+player+"\n");
				printClusters(clusters,player);
				double [][] deltasforactions = new double[numactions[0]][numberofclusters];
				double [][] deltaswithoutactions = new double[numactions[0]][numberofclusters];
				calculateDeltasIfAddedToClustersV1(delta, deltasforactions,deltaswithoutactions,clusters,numberofplayers,numberofclusters,clustersize,game, player);
				//clearCluster(clusters,player);
				/**
				 * create an empty cluster object
				 * after the new object is full
				 * use it as next iteration
				 */

				assignActionsToClusterV1(newclusters,delta,deltasforactions, deltaswithoutactions, clustersize, player);
				calculateDeltas(delta,clusters,numberofplayers,numberofclusters, game);
				printDeltas(delta);
				playercounter++;
				if(playercounter==2)
				{
					/**
					 * deep copy of newcluster to cluster
					 */
					System.out.println("Cluster after actions reassignment for player "+player);
					Logger.logit("\nCluster after actions reassignment for player "+player+"\n");
					printClusters(newclusters, player);
					deepCopyCuster(newclusters,clusters);
					newclusters = getEmptyClusters(numberofplayers,numberofclusters);
				}
				playercounter = playercounter  % 2;
			}


		}

		return clusters;
	}


	/**
	 * Does kmean clustering for games, for subgame solution concept
	 * Iterates over the actions
	 * CLustering is done for one player fixing the clustering for other player
	 * and vice versa untill no centroids changes
	 * @param numberofclusters
	 * @param game
	 * @return clusters for players
	 * @throws Exception 
	 */
	public static List<Integer>[][] clusterActionsV1_2(int numberofclusters, MatrixGame game ) throws Exception
	{
		int numberofplayers = game.getNumPlayers();
		int[] numactions = game.getNumActions();
		int[] clustersize = {numactions[0]/numberofclusters, numactions[1]/numberofclusters};
		double[][][] centroids = new double[numberofplayers][numberofclusters][];
		//double[][][] oldcentroids = new double[numberofplayers][numberofclusters][];
		double [][] delta = new double[numberofplayers][numberofclusters];

		/**
		 * create an empty cluster object
		 */
		List<Integer>[][] clusters = getEmptyClusters(numberofplayers,numberofclusters);
		/**
		 * cluster object to contain the clustering for previous round
		 */
		List<Integer>[][] oldclusters = getEmptyClusters(numberofplayers,numberofclusters);
		/**
		 * initialize cluster with random actions
		 */
		initilizeClusters(clusters,clustersize, numactions, RAND_ACTION_IN_CLUSTER);
		/**
		 * create  empty centroid object to contain the centroids
		 */
		//createEmptyCentroids(centroids, numactions,clustersize);
		/**
		 * calculate the centroids
		 */
		//calculateCentroids(centroids,clusters,numactions,game);
		/**
		 * copy cluster to old cluster
		 */
		deepCopyCuster(clusters, oldclusters);

		int iteration = 0;
		int playercounter = 0;
		int player = 0;
		/**
		 * do clustering until it is unchanged for one player
		 * Then move to the next player and so on...
		 */
		boolean clusterchnaged = true;
		double mindeltaamongiterations = Double.MAX_VALUE;
		double LIMIT = Double.MIN_VALUE;
		int iterationlimit = 100;
		double ultimatemindelta = Double.MAX_VALUE;
		//int iteration = 0;
		while(clusterchnaged)
		{
			/**
			 * empty cluster object
			 */
			List<Integer>[][] newclusters = getEmptyClusters(numberofplayers,numberofclusters);
			/**
			 * calculate delta for  player
			 */
			calculateDeltasForAPlayer(delta,clusters,numberofplayers,numberofclusters, game,player);
			double [][] deltasforactions = new double[numactions[0]][numberofclusters];
			double [][] deltaswithoutactions = new double[numactions[0]][numberofclusters];
			calculateDeltasIfAddedToClustersV1(delta, deltasforactions,deltaswithoutactions,clusters,numberofplayers,numberofclusters,clustersize,game, player);
			assignActionsToClusterV1(newclusters,delta,deltasforactions, deltaswithoutactions, clustersize, player);
			/**
			 * if the clusters for one player is unchanged then move to the n ext player
			 */
			boolean changed = checkIfClustersChangedForPlayer(newclusters,clusters,player);
			//System.out.println("Clusters changed for player "+ player + " : "+changed);
			deepCopyCluster(newclusters,clusters,player);
			calculateDeltasForAPlayer(delta,clusters,numberofplayers,numberofclusters, game,player);
			//System.out.println("Deltas : "+ delta[player][0] + ","+delta[player][1]+","+delta[player][2]);
			/**
			 * check if minnimum delta is below some certain threshold
			 */
			double mindelta = minDelta(delta[player],player);
			if(mindelta<mindeltaamongiterations)
			{
				mindeltaamongiterations = mindelta;

				//LIMIT = Double.MIN_VALUE;
			}
			if(iteration==iterationlimit)
			{
				iterationlimit *=4;
				LIMIT = mindeltaamongiterations;
				mindeltaamongiterations = Double.MAX_VALUE;
			}
			if(!changed || (delta[player][0] <=LIMIT) || (delta[player][1]<=LIMIT) || (delta[player][2]<=LIMIT))
			{
				mindeltaamongiterations = Double.MAX_VALUE;
				LIMIT = Double.MIN_VALUE;
				iteration=0;
				iterationlimit= 100;
				/**
				 * move to next player
				 */
				player = 1^player;
				//deepCopyCuster(newclusters, clusters);
				playercounter++;
				if(playercounter==2)
				{
					/**
					 * check if the cluster changed for both players
					 */

					if(!checkIfClustersChanged(oldclusters,clusters))
					{
						clusterchnaged = false;
					}
					calculateDeltas(delta,clusters,numberofplayers,numberofclusters, game);
					double d1 = minDelta(delta[0],0);
					double d2= minDelta(delta[0],0);
					if(Math.min(d1, d2) < ultimatemindelta)
					{
						System.out.println("Ultimatemin delta "+ ultimatemindelta);
						ultimatemindelta = Math.min(d1, d2);
					}
					if(d1<10 || d2<10)
					{
						System.out.println("hmm");
					}
					minDelta(delta[1],1);
					System.out.println("Deltas : "+ delta[0][0] + ","+delta[0][1]+","+delta[0][2]);
					System.out.println("Deltas : "+ delta[1][0] + ","+delta[1][1]+","+delta[1][2]);
					System.out.println("Ultimatemin delta "+ ultimatemindelta);
					double maxdelta[] = findMaxDelta(delta);
					System.out.println(" Maxdelta " + maxdelta[0] + " , "+ maxdelta[1]);
					deepCopyCuster(clusters, oldclusters);
					/**
					 * reset player counter
					 */
					playercounter = 0;
				}


			}

			iteration++;
		}

		return clusters;
	}




	private static double minDelta(double[] ds, int player) {

		double min = Double.MAX_VALUE;
		for(double x: ds)
		{
			if(x<min)
			{
				min=x;
			}
		}
		return min;


	}

	private static void minDelta(double[][] delta, int player) {
		// TODO Auto-generated method stub

	}

	private static void deepCopyCluster(List<Integer>[][] newclusters,
			List<Integer>[][] clusters, int player) {
		for(int j=0; j<newclusters[player].length; j++)
		{
			clusters[player][j].clear();
			for(int action: newclusters[player][j])
			{
				clusters[player][j].add(action);
			}
		}

	}


	/**
	 * checks if clusters changed for both players
	 * @param newclusters
	 * @param clusters
	 * @return
	 */
	private static boolean checkIfClustersChangedForAPlayer(
			List<Integer>[][] newclusters, List<Integer>[][] clusters, int player) {
		//for(int player=0; player<2; player++)
		//	{

		for(int cluster=0; cluster<newclusters[player].length; cluster++)
		{
			for(int action=0; action<newclusters[player][cluster].size(); action++)
			{
				if(newclusters[player][cluster].get(action) != clusters[player][cluster].get(action))
				{
					return true;
				}

			}
		}
		//}
		return false;
	}


	/**
	 * checks if clusters changed for both players
	 * @param newclusters
	 * @param clusters
	 * @return
	 */
	private static boolean checkIfClustersChanged(
			List<Integer>[][] newclusters, List<Integer>[][] clusters) {
		for(int player=0; player<2; player++)
		{

			for(int cluster=0; cluster<newclusters[player].length; cluster++)
			{
				for(int action=0; action<newclusters[player][cluster].size(); action++)
				{

					/**
					 * 
					 */
					boolean flag = true;
					for(int action2 : clusters[player][cluster])
					{
						if(newclusters[player][cluster].get(action)==action2)
						{
							flag = false;
							break;
						}
					}
					if(flag==true)
					{
						return true;
					}

				}
			}
		}
		return false;
	}

	private static boolean checkIfClustersChangedForPlayer(
			List<Integer>[][] newclusters, List<Integer>[][] clusters,
			int player) {

		for(int cluster=0; cluster<newclusters[player].length; cluster++)
		{
			for(int action=0; action<newclusters[player][cluster].size(); action++)
			{

				/**
				 * 
				 */
				boolean flag = true;
				for(int action2 : clusters[player][cluster])
				{
					if(newclusters[player][cluster].get(action)==action2)
					{
						flag = false;
						break;
					}
				}
				if(flag==true)
				{
					return true;
				}

			}
		}

		return false;
	}

	private static void calculateDeltasForAPlayer(double[][] delta,
			List<Integer>[][] clusters, int numberofplayers,
			int numberofclusters, MatrixGame game, int player) {
		for(int cluster=0; cluster<numberofclusters; cluster++)
		{
			delta[player][cluster] = calculateDelta(clusters, cluster,player,game);
		}

	}


	/**
	 * initialize clusters using heuristics
	 * @param clusters
	 * @param game
	 */
	public static void initialiZeClustersV2(List<Integer>[][] clusters, MatrixGame game)
	{

		//double [][] delta = new double[2][clusters[0].length];
		int numberofclusters = clusters[0].length;
		List<Integer>[] unassignedactions = new List[2];
		for(int i=0; i<2; i++)
		{
			unassignedactions[i] = new ArrayList<Integer>();
			for(int action=0; action<game.getNumActions(0); action++)
			{
				unassignedactions[i].add(action+1);
			}
		}
		int actionspercluster = game.getNumActions(0)/numberofclusters;
		/**
		 * at first assign one action to each of the clusters
		 */
		for(int player=0;player<2;player++)
		{
			for(int cluster =0;cluster<numberofclusters;cluster++)
			{
				/**
				 * pic a random action from the unassigned actions
				 */
				int randactionindex = randInt(0, unassignedactions[player].size()-1);
				int tmpaction = unassignedactions[player].get(randactionindex);
				unassignedactions[player].remove(randactionindex);
				clusters[player][cluster].add(tmpaction);
			}
		}
		while(unassignedactions[0].size()!=0 && unassignedactions[1].size()!=0)
		{

			//for(int player=0; player<2;player++)
			{
				for(int cluster=0; cluster<numberofclusters; cluster++)
				{
					for(int player=0; player<2;player++)
					{

						if(clusters[player][cluster].size()<actionspercluster)
						{
							Double [][] deltasforclusterswactions = new Double[unassignedactions[player].size()][2]; // delta if action is  in cluster
							calculateDeltasIfAddedToClustersV3(cluster,deltasforclusterswactions, unassignedactions[player], clusters, 2, numberofclusters, game, player);

							sortArrayAsc(deltasforclusterswactions);
							/**
							 * pick the most promising action and removed it from unassigned actions
							 * include it in cluster
							 */
							int action = deltasforclusterswactions[0][1].intValue();
							clusters[player][cluster].add(action);
							int actionindex = unassignedactions[player].indexOf(action);
							unassignedactions[player].remove(actionindex);
							//	System.out.println("hi");
						}
					}

				}
			}
		}
		//System.out.println("hi");

	}





	/**
	 * hill climbing without checking any repetition 
	 * @param numberofclusters
	 * @param game
	 * @return
	 */
	public static List<Integer>[][] hillClimbingClusteringV4(int numberofclusters, MatrixGame game )
	{
		int numberofplayers = game.getNumPlayers();
		int[] numactions = game.getNumActions();
		int[] clustersize = {numactions[0]/numberofclusters, numactions[1]/numberofclusters};
		double [][] delta = new double[numberofplayers][numberofclusters];
		/**
		 * create an empty cluster object
		 */
		List<Integer>[][] clusters = getEmptyClusters(numberofplayers,numberofclusters);
		
		/**
		 * save the best cluster
		 */
		List<Integer>[][] bestclusters = getEmptyClusters(numberofplayers,numberofclusters);

		/**
		 * initialize cluster with random actions
		 */
		double bestdeltayet = Double.POSITIVE_INFINITY;
		int randomrestart = 5000;
		boolean randomize = false;
		clusters = getEmptyClusters(numberofplayers,numberofclusters);
		//initialiZeClustersV2(clusters, game);

		while(randomrestart>0)
		{
			//clusters = getEmptyClusters(numberofplayers,numberofclusters);
			if(randomize)
			{
				deepCopyCuster(bestclusters, clusters);
			}
			//calculateDeltas(delta, clusters, numberofplayers, numberofclusters, game);
			//double tdelta[] = findMaxDelta(delta);
			
			initialiZeClustersV2(clusters, game,randomize);
			randomize = true;
			int neiborcounter = 0;
			while(true)
			{
				int player = 0;

				calculateDeltas(delta, clusters, numberofplayers, numberofclusters, game);
				double tmpdelta[] = findMaxDelta(delta);
				if(Math.max(tmpdelta[0], tmpdelta[1])<bestdeltayet)
				{
					//bestclusters = getEmptyClusters(numberofplayers, numberofclusters);
					deepCopyCuster(clusters, bestclusters);
					bestdeltayet = Math.max(tmpdelta[0], tmpdelta[1]);
				}
				if(bestdeltayet<5)
				{
					System.out.println("Randomrestart "+randomrestart+" Best delta yet "+ bestdeltayet);
					break;
				}
				
				//for(int neiborcounter = 0;neiborcounter<100;neiborcounter++)

				List<Integer>[][] neighborclusters = getEmptyClusters(2, numberofclusters);
				deepCopyCuster(clusters, neighborclusters);
				createNeighborPartitionV3(game,clusters,neighborclusters);
				player = 1^player;
				boolean changed = checkIfClustersChanged(neighborclusters, clusters);
				
				calculateDeltas(delta, neighborclusters, numberofplayers, numberofclusters, game);
				double tmdelta[] = findMaxDelta(delta);
				
				System.out.println("Randomrestart "+randomrestart+" Best delta yet "+ bestdeltayet + 
						", clusterchnaged : "+changed + ", current delta : "+ Math.max(tmpdelta[0], tmpdelta[1]) + 
								", neighbr delta : "+ Math.max(tmdelta[0], tmdelta[1]));
				if(Math.max(tmdelta[0], tmdelta[1])<Math.max(tmpdelta[0], tmpdelta[1]))
				{
					deepCopyCuster(neighborclusters, clusters);
					neiborcounter=0;

				}
				else if( neiborcounter>500)
				{
					break;
				}
				neiborcounter++;

			}
			if(bestdeltayet<5)
			{
				break;
			}


			randomrestart--;


		}
		return clusters;
	}




	private static void initialiZeClustersV2(List<Integer>[][] clusters,
			MatrixGame game, boolean randomize) {

		if(randomize)
		{

			for(int player=0; player<2; player++)
			{
				player = randInt(0, 1);
				int numofshuffledcluster = 2;//randInt(0,clusters[player].length);
				int timestoswap = 5;//randInt(0,clusters[player][0].size());
				for(int clustercounter = 0; clustercounter<numofshuffledcluster; clustercounter++)
				{
					int cluster1 = randInt(0, clusters[player].length-1);
					int cluster2 = randInt(0, clusters[player].length-1);
					if(cluster1!=cluster2)
					{
						
						for(int actioncounter=0; actioncounter<timestoswap; actioncounter++)
						{
							int action1 = clusters[player][cluster1].get(randInt(0, clusters[player][cluster1].size()-1));
							int action2 = clusters[player][cluster2].get(randInt(0, clusters[player][cluster2].size()-1));
							//int action1 = clusters[player][cluster1].get(actioncounter);
							//int action2 = clusters[player][cluster2].get(actioncounter);

							
							if(action1!=action2)
							{
								clusters[player][cluster1].remove(clusters[player][cluster1].indexOf(action1));
								clusters[player][cluster2].remove(clusters[player][cluster2].indexOf(action2));


								clusters[player][cluster1].add(action2);
								clusters[player][cluster2].add(action1);
							}


						}
					}
				}
			}
		}
		else
		{
			int numberofclusters = clusters[0].length;
			List<Integer>[] unassignedactions = new List[2];
			for(int i=0; i<2; i++)
			{
				unassignedactions[i] = new ArrayList<Integer>();
				for(int action=0; action<game.getNumActions(0); action++)
				{
					unassignedactions[i].add(action+1);
				}
			}
			int actionspercluster = game.getNumActions(0)/numberofclusters;
			/**
			 * at first assign one action to each of the clusters
			 */
			for(int player=0;player<2;player++)
			{
				for(int cluster =0;cluster<numberofclusters;cluster++)
				{
					/**
					 * pic a random action from the unassigned actions
					 */
					int randactionindex = randInt(0, unassignedactions[player].size()-1);
					int tmpaction = unassignedactions[player].get(randactionindex);
					unassignedactions[player].remove(randactionindex);
					clusters[player][cluster].add(tmpaction);
				}
			}
			while(unassignedactions[0].size()!=0 && unassignedactions[1].size()!=0)
			{

				for(int player=0; player<2;player++)
				{
					for(int cluster=0; cluster<numberofclusters; cluster++)
					{
						//for(int player=0; player<2;player++)
						{

							if(clusters[player][cluster].size()<actionspercluster)
							{
								Double [][] deltasforclusterswactions = new Double[unassignedactions[player].size()][2]; // delta if action is  in cluster
								calculateDeltasIfAddedToClustersV3(cluster,deltasforclusterswactions, unassignedactions[player], clusters, 2, numberofclusters, game, player);

								sortArrayAsc(deltasforclusterswactions);
								/**
								 * pick the most promising action and removed it from unassigned actions
								 * include it in cluster
								 */
								int action = deltasforclusterswactions[0][1].intValue();
								clusters[player][cluster].add(action);
								int actionindex = unassignedactions[player].indexOf(action);
								unassignedactions[player].remove(actionindex);
								//	System.out.println("hi");
							}
						}

					}
				}
			}
		}

	}

	public static List<Integer>[][] hillClimbingClusteringV3(int numberofclusters, MatrixGame game )
	{
		int numberofplayers = game.getNumPlayers();
		int[] numactions = game.getNumActions();
		int[] clustersize = {numactions[0]/numberofclusters, numactions[1]/numberofclusters};
		double [][] delta = new double[numberofplayers][numberofclusters];
		/**
		 * create an empty cluster object
		 */
		List<Integer>[][] clusters = getEmptyClusters(numberofplayers,numberofclusters);

		/**
		 * initialize cluster with random actions
		 */
		double bestdeltayet = Double.POSITIVE_INFINITY;
		//List<List<Integer>[][]> historyofclusters = new ArrayList<List<Integer>[][]>() ;
		int randomrestart = 5000;
		while(randomrestart>0)
		{
			//int currentplayer = 0;
			clusters = getEmptyClusters(numberofplayers,numberofclusters);
			initialiZeClustersV2(clusters, game);//(clusters, clustersize, numactions, RAND_ACTION_IN_CLUSTER);
			int playercounter = 0;
			//List<List<Integer>[][]> globalhistoryofclusters = new ArrayList<List<Integer>[][]>() ;
			while(true)
			{
				double tmpdelta[] = new double[2];
				List<Integer>[][] neighborclusters = getEmptyClusters(2, numberofclusters);
				deepCopyCuster(clusters, neighborclusters);
				calculateDeltas(delta, clusters, numberofplayers, numberofclusters, game);
				tmpdelta = findMaxDelta(delta);
				if(Math.max(tmpdelta[0], tmpdelta[1])<bestdeltayet)
				{
					bestdeltayet = Math.max(tmpdelta[0], tmpdelta[1]);
				}
				System.out.println("Randomrestart "+randomrestart+" Best delta yet "+ bestdeltayet);

				for(int currentplayer=0;currentplayer<2;currentplayer++)
				{
					createNeighborPartitionV2(game,clusters,neighborclusters,currentplayer);
					//deepCopyCluster(neighborclusters, clusters, currentplayer);
				}
				//playercounter++;
				calculateDeltas(delta, neighborclusters, numberofplayers, numberofclusters, game);
				//if(playercounter==2)
				//{
				double tmdelta[] = findMaxDelta(delta);
				if(Math.max(tmdelta[0], tmdelta[1])<Math.max(tmpdelta[0], tmpdelta[1]))
				{
					deepCopyCuster(neighborclusters, clusters);

				}
				else
				{
					break;
				}

				//playercounter=0;

				/*boolean repeat1 = checkForRepeatition(game, globalhistoryofclusters, clusters);//checkIfClustersChanged(clusters, oldclusters);
					if( repeat1)
						break;*/

				//globalhistoryofclusters.add(tmpclusters);
				//}




			}
			randomrestart--;
			//globalhistoryofclusters.clear();

		}
		return clusters;
	}





	public static List<Integer>[][] hillClimbingClusteringV2(int numberofclusters, MatrixGame game )
	{
		int numberofplayers = game.getNumPlayers();
		int[] numactions = game.getNumActions();
		int[] clustersize = {numactions[0]/numberofclusters, numactions[1]/numberofclusters};
		double [][] delta = new double[numberofplayers][numberofclusters];
		/**
		 * create an empty cluster object
		 */
		List<Integer>[][] clusters = getEmptyClusters(numberofplayers,numberofclusters);

		/**
		 * initialize cluster with random actions
		 */
		double bestdeltayet = Double.POSITIVE_INFINITY;
		List<List<Integer>[][]> historyofclusters = new ArrayList<List<Integer>[][]>() ;
		int randomrestart = 5000;
		while(randomrestart>0)
		{
			int currentplayer = 0;
			initilizeClusters(clusters, clustersize, numactions, RAND_ACTION_IN_CLUSTER);
			int playercounter = 0;
			List<List<Integer>[][]> globalhistoryofclusters = new ArrayList<List<Integer>[][]>() ;
			while(true)
			{
				while(true)
				{

					calculateDeltas(delta, clusters, numberofplayers, numberofclusters, game);
					double tmpdelta[] = findMaxDelta(delta);
					if(Math.max(tmpdelta[0], tmpdelta[1])<bestdeltayet)
					{
						bestdeltayet = Math.max(tmpdelta[0], tmpdelta[1]);
					}
					System.out.println("Randomrestart "+randomrestart+", currentplayer "+ currentplayer+", Best delta yet "+ bestdeltayet);
					List<Integer>[][] neighborclusters = getEmptyClusters(2, numberofclusters);
					deepCopyCuster(clusters, neighborclusters);
					boolean successful=	createNeighborPartitionV2(game,clusters,neighborclusters,currentplayer);
					boolean changedforplayer = checkIfClustersChangedForAPlayer(neighborclusters, clusters, currentplayer);
					boolean repeat = chekForRepeatitionForAPlayer(game, historyofclusters, neighborclusters, currentplayer);
					if(!successful || !changedforplayer || repeat)
					{
						deepCopyCluster(neighborclusters, clusters, currentplayer);
						historyofclusters.clear();
						break;
					}
					else
					{
						deepCopyCluster(neighborclusters, clusters, currentplayer);
						List<Integer>[][] tmpclusters = getEmptyClusters(2, numberofclusters);
						deepCopyCuster(clusters, tmpclusters);
						historyofclusters.add(tmpclusters);
					}
				}
				currentplayer = 1^currentplayer;
				playercounter++;
				calculateDeltas(delta, clusters, numberofplayers, numberofclusters, game);
				if(playercounter==2)
				{
					playercounter=0;
					boolean repeat1 = checkForRepeatition(game, globalhistoryofclusters, clusters);//checkIfClustersChanged(clusters, oldclusters);
					if( repeat1)
						break;
					List<Integer>[][] tmpclusters = getEmptyClusters(2, numberofclusters);
					deepCopyCuster(clusters, tmpclusters);
					globalhistoryofclusters.add(tmpclusters);
				}



			}
			randomrestart--;
			historyofclusters.clear();
			globalhistoryofclusters.clear();

		}
		return clusters;
	}





	public static List<Integer>[][] hillClimbingClustering(int numberofclusters, MatrixGame game )
	{
		int numberofplayers = game.getNumPlayers();
		int[] numactions = game.getNumActions();
		int[] clustersize = {numactions[0]/numberofclusters, numactions[1]/numberofclusters};
		double [][] delta = new double[numberofplayers][numberofclusters];
		/**
		 * create an empty cluster object
		 */
		List<Integer>[][] clusters = getEmptyClusters(numberofplayers,numberofclusters);
		List<Integer>[][] oldclusters = getEmptyClusters(numberofplayers,numberofclusters);
		/**
		 * initialize cluster with random actions
		 */
		initilizeClusters(clusters,clustersize, numactions, RAND_ACTION_IN_CLUSTER);
		deepCopyCluster(clusters, oldclusters, 0);
		deepCopyCluster(clusters, oldclusters, 1);
		int currentplayer = 0;
		while(true)
		{
			int iteration = 0;
			while(true)
			{
				calculateDeltas(delta, clusters, numberofplayers, numberofclusters, game);


				double mindelta = minDelta(delta[currentplayer], currentplayer);
				double[] deltawithworstcluster = calculateDelta(game, clusters, currentplayer, true);

				List<Integer>[][] neighborclusters = createNeighborPartition(game,clusters,mindelta,(int)deltawithworstcluster[1] ,currentplayer);
				boolean equal = checkIfPartitionsAreEqual(clusters, neighborclusters, currentplayer);
				boolean changed = checkIfClustersChanged(clusters, neighborclusters);
				if(equal || !changed || iteration>100)
				{
					break;
				}
				else
				{
					deepCopyCluster(neighborclusters, clusters, currentplayer);
				}
				iteration++;
			}
			currentplayer = 1^currentplayer;
			boolean b1 = checkIfClustersChanged(clusters, oldclusters);
			//boolean b2 = checkIfPartitionsAreEqual(clusters, oldclusters, 1);
			if(!b1 )
			{
				break;
			}
			else
			{
				deepCopyCluster(clusters, oldclusters, 0);
				deepCopyCluster(clusters, oldclusters, 1);
			}


		}
		calculateDeltas(delta, clusters, numberofplayers, numberofclusters, game);
		return clusters;
	}


	/**
	 * select couple of strategies. Then reassigned them to their nearest cluster. 
	 * @param game
	 * @param currentpartition
	 * @param neighborpartition
	 * @param player
	 * @return
	 */
	private static void createNeighborPartitionV3(
			MatrixGame game,
			List<Integer>[][] currentpartition, List<Integer>[][] neighborpartition) 
	{
		//boolean suc = false;
		for(int player = 0; player<2; player++)
		{
			//suc = false;
			double [][] delta = new double[2][currentpartition[player].length];
			int[] clustersize = {currentpartition[player][0].size(),currentpartition[player][0].size()};
			int numberofactions = currentpartition[player].length*currentpartition[player][0].size();
			/**
			 * pick multiple strategies, then assign them to their nearest cluster. 
			 */
			int lowerlimitstrategy = (int)(0.1*numberofactions);
			int upperlimitstrategy = (int)(numberofactions);
			int numberofstrategiestoswap = randInt(lowerlimitstrategy, upperlimitstrategy); // min 2 strategies needs to be changed
			/**
			 * now pick the strategies
			 */
			ArrayList<Integer> swapcandidates = new ArrayList<Integer>();
			ArrayList<Integer> swappingactions = new ArrayList<Integer>();
			for(int action = 0; action<numberofactions; action++)
			{
				swapcandidates.add(action+1);
			}

			while(swappingactions.size()<numberofstrategiestoswap)
			{
				int actionindex = randInt(0, swapcandidates.size()-1);
				int action = swapcandidates.get(actionindex);
				swappingactions.add(action);
				swapcandidates.remove(actionindex);
			}

			/**
			 * now for the chosen strategies build rank table and assign them. 
			 */


			calculateDeltasForAPlayer(delta,neighborpartition,2,neighborpartition[player].length, game,player);
			double [][] deltasforclusterswactions = new double[neighborpartition[player].length][ numberofstrategiestoswap]; // delta if action is  in cluster
			double [][] deltasforclusterswoactions = new double[neighborpartition[player].length][ numberofstrategiestoswap]; // delta if action is not in cluster
			calculateDeltasIfAddedToClustersV2(swappingactions,delta, deltasforclusterswactions,deltasforclusterswoactions,neighborpartition,2,neighborpartition[player].length,clustersize,game, player);
			Double[][] rankforclusters = findMostPromisingCluster(swappingactions,neighborpartition,deltasforclusterswactions,deltasforclusterswoactions,player);
			//stripActionsFromClusters(neighborpartition,swapcandidates,numberofactions,player);

			swapActionsBetweenCompaitbleClusters(swappingactions,rankforclusters,neighborpartition,deltasforclusterswactions,deltasforclusterswoactions,player);
		}

		//return suc;



	}



	private static void swapActionsBetweenCompaitbleClusters(
			ArrayList<Integer> swappingactions, Double[][] rankforclusters,
			List<Integer>[][] neighborpartition,
			double[][] deltasforclusterswactions,
			double[][] deltasforclusterswoactions, int player) {


		ArrayList<Integer> unpickedindex = new ArrayList<Integer>();
		for(int i=0; i<rankforclusters.length; i++)
		{
			unpickedindex.add(i);
		}

		while(unpickedindex.size()>0)
		//for(int index=0; index<rankforclusters.length; index++)
		{
			int index = unpickedindex.get(randInt(0, unpickedindex.size()-1));
			unpickedindex.remove(unpickedindex.indexOf(index));
			if(rankforclusters[index][0]>0)
			{
				int cluster1 = rankforclusters[index][1].intValue();
				int action1 = rankforclusters[index][2].intValue();
				if(neighborpartition[player][cluster1].contains(action1))
				{
					/**
					 * now choose a different cluster
					 */
					for(int index2=0; index2<rankforclusters.length; index2++)
					{
						if(cluster1 != rankforclusters[index2][1].intValue() &&
								rankforclusters[index2][0].intValue()>=0 &&
								rankforclusters[index2][2].intValue() != action1)
						{
							int cluster2 = rankforclusters[index2][1].intValue();
							int action2 = rankforclusters[index2][2].intValue();
							if(neighborpartition[player][cluster2].contains(action2))
							{

								/**
								 * now check if the cluster and actions are compatible to change
								 */
								boolean compatible = areClustersCompatibleForSwap(swappingactions,cluster1,action1,cluster2,action2,neighborpartition,deltasforclusterswactions,deltasforclusterswoactions,player);
								if(compatible)
								{
									/**
									 * swap the actions and return true
									 */
									if(neighborpartition[player][cluster1].contains(action1) && neighborpartition[player][cluster2].contains(action2))
									{

										//System.out.println("Action " + action1 + " is swapped with "+action2);
										//System.out.println("cluster " + cluster1 + " is swapped with "+cluster2);
										neighborpartition[player][cluster1].remove(neighborpartition[player][cluster1].indexOf(action1));
										neighborpartition[player][cluster2].remove(neighborpartition[player][cluster2].indexOf(action2));


										neighborpartition[player][cluster1].add(action2);
										neighborpartition[player][cluster2].add(action1);
									}

									//return true;
								}

							}
						}
					}

				}
			}
		}
		//return false;


	}

	private static boolean areClustersCompatibleForSwap(
			ArrayList<Integer> swappingactions, int cluster1, int action1,
			int cluster2, int action2, List<Integer>[][] neighborpartition,
			double[][] deltasforclusterswactions,
			double[][] deltasforclusterswoactions, int player) {

		/**
		 * try to check if including action1 in cluster2 makes the delta of cluster2 same or less
		 * and same for action2: check if including action2 in cluster1 makes delta less or same
		 * if both of these condition holds then we can swap them.
		 */
		if(!neighborpartition[player][cluster1].contains(action1) || !neighborpartition[player][cluster2].contains(action2))
		{
			return false;
		}
		int action1index = swappingactions.indexOf(action1);
		int action2index = swappingactions.indexOf(action2);
		//System.out.println(action1index+","+cluster1+","+action2index+","+cluster2);
		//System.out.println(deltasforclusterswactions[cluster1].length+","+deltasforclusterswactions[cluster2].length);
		double diff1_1 = deltasforclusterswactions[cluster2][action1index] - deltasforclusterswoactions[cluster2][action1index];
		double diff1_2 =   deltasforclusterswoactions[cluster2][action2index] - deltasforclusterswactions[cluster2][action2index];
		if((diff1_1+diff1_2)>=0)
			return false;

		double diff2_1 = deltasforclusterswactions[cluster1][action2index] - deltasforclusterswoactions[cluster1][action2index];
		double diff2_2 =   deltasforclusterswoactions[cluster1][action1index] - deltasforclusterswactions[cluster1][action1index];
		if((diff2_1+diff2_2)>=0)
			return false;
		
		//if((diff1_1+diff1_2+diff2_1+diff2_2)<0) // not good
		//	return false;

		return true;


	}

	private static void stripActionsFromClusters(
			List<Integer>[][] neighborpartition, ArrayList<Integer> swapcandidates,
			int numberofactions, int player) {

		for(int cluster = 0; cluster<neighborpartition[player].length; cluster++)
		{
			for(int action : neighborpartition[player][cluster])
			{
				if(swapcandidates.contains(action))
				{
					neighborpartition[player][cluster].remove(neighborpartition[player][cluster].indexOf(action));
				}
			}
		}

	}

	private static Double[][] findMostPromisingCluster(
			ArrayList<Integer> swappingactions,
			List<Integer>[][] neighborpartition,
			double[][] deltasforclusterswactions,
			double[][] deltasforclusterswoactions, int player) {
		//int numberofactions = neighborpartition[0].length * neighborpartition[0][0].size();
		Double[][] rankforcluster = new Double[neighborpartition[player].length*swappingactions.size()][3];
		int rankindex = 0;
		for(int cluster=0; cluster<neighborpartition[player].length; cluster++)
		{
			int actionindex = 0;
			for(int action : swappingactions)
			{
				rankforcluster[rankindex][0] = deltasforclusterswactions[cluster][actionindex] - deltasforclusterswoactions[cluster][actionindex];
				rankforcluster[rankindex][1] = Math.floor(cluster);
				rankforcluster[rankindex][2] = Math.floor(action);
				rankindex++;
				actionindex++;
			}


		}
		sortArrayDesc(rankforcluster);
		return rankforcluster;
	}

	/**
	 * actions are predetermined. Not every actions are chosen for the computation
	 * @param swappingactions
	 * @param delta
	 * @param deltasforclusterswactions
	 * @param deltasforclusterswoactions
	 * @param neighborpartition
	 * @param i
	 * @param length
	 * @param clustersize
	 * @param game
	 * @param player
	 */
	private static void calculateDeltasIfAddedToClustersV2(
			ArrayList<Integer> swappingactions, double[][] delta,
			double[][] deltasforclusterswactions,
			double[][] deltasforclusterswoactions, List<Integer>[][] clusters,
			int numberofplayers, int numberofclusters, int[] clustersize,
			MatrixGame game, int player) {


		for(int cluster=0; cluster<numberofclusters; cluster++)
		{
			int actionindex=0;
			for(int action : swappingactions)
			{
				if(clusters[player][cluster].contains(action))
				{

					deltasforclusterswactions[cluster][actionindex] = delta[player][cluster];

					/**
					 * need to calculate the delta if the action is not in the cluster
					 * and assign that to deltas[player][cluster]
					 */
					List<Integer>[][] tmpcluster = getEmptyClusters(numberofplayers, numberofclusters);
					makeDeepCopy(clusters,tmpcluster);
					int indexofactiontoremove = tmpcluster[player][cluster].indexOf(action);
					tmpcluster[player][cluster].remove(indexofactiontoremove);
					/**
					 * following method returns 0 if the cluster size is 1 or 0
					 */
					deltasforclusterswoactions[cluster][actionindex] = calculateDelta(tmpcluster, cluster, player, game);


				}
				else
				{

					List<Integer>[][] tmpcluster = getEmptyClusters(numberofplayers, numberofclusters);
					makeDeepCopy(clusters,tmpcluster);
					tmpcluster[player][cluster].add(action);
					deltasforclusterswactions[cluster][actionindex] = calculateDelta(tmpcluster, cluster, player, game);
					deltasforclusterswoactions[cluster][actionindex] = delta[player][cluster];

				}
				actionindex++;

			}
		}




	}

	private static boolean createNeighborPartitionV2(
			MatrixGame game,
			List<Integer>[][] currentpartition, List<Integer>[][] neighborpartition , int player) 
	{

		double [][] delta = new double[2][currentpartition[player].length];
		int[] clustersize = {currentpartition[player][0].size(),currentpartition[player][0].size()};
		int numberofactions = currentpartition[player].length*currentpartition[player][0].size();
		//List<Integer>[][] neighborpartition = getEmptyClusters(2, currentpartition[player].length);
		//deepCopyCuster(currentpartition, neighborpartition);

		calculateDeltasForAPlayer(delta,neighborpartition,2,neighborpartition[player].length, game,player);
		double [][] deltasforclusterswactions = new double[neighborpartition[player].length][ numberofactions]; // delta if action is  in cluster
		double [][] deltasforclusterswoactions = new double[neighborpartition[player].length][ numberofactions]; // delta if action is not in cluster
		calculateDeltasIfAddedToClustersV2(delta, deltasforclusterswactions,deltasforclusterswoactions,neighborpartition,2,neighborpartition[player].length,clustersize,game, player);
		Double[][] rankforclusters = findMostPromisingCluster(neighborpartition,deltasforclusterswactions,deltasforclusterswoactions,player);

		//int x = findFurthestAction(game, currentpartition, 0, player);
		//int y = findFurthestAction(game, currentpartition, 1, player);
		//int z = findFurthestAction(game, currentpartition, 2, player);

		boolean suc = swapActionsBetweenCompaitbleClusters(rankforclusters,neighborpartition,deltasforclusterswactions,deltasforclusterswoactions,player);


		return suc;



	}





	/**
	 * 
	 * @param rankforclusters val,cluster and action tuple
	 * @param neighborpartition
	 * @param deltasforclusterswactions
	 * @param deltasforclusterswoactions
	 * @param player
	 * @return
	 */
	private static boolean swapActionsBetweenCompaitbleClusters(
			Double[][] rankforclusters, List<Integer>[][] neighborpartition,
			double[][] deltasforclusterswactions,
			double[][] deltasforclusterswoactions,
			int player) {



		for(int index = 0; index<rankforclusters.length; index++)
		{
			if(rankforclusters[index][0]>0)
			{
				int cluster1 = rankforclusters[index][1].intValue();
				int action1 = rankforclusters[index][2].intValue();
				if(neighborpartition[player][cluster1].contains(action1))
				{
					/**
					 * now choose a different cluster
					 */
					for(int index2=0; index2<rankforclusters.length; index2++)
					{
						if(cluster1 != rankforclusters[index2][1].intValue() &&
								rankforclusters[index2][0].intValue()>=0 &&
								rankforclusters[index2][2].intValue() != action1)
						{
							int cluster2 = rankforclusters[index2][1].intValue();
							int action2 = rankforclusters[index2][2].intValue();
							if(neighborpartition[player][cluster2].contains(action2))
							{

								/**
								 * now check if the cluster and actions are compatible to change
								 */
								boolean compatible = areClustersCompatibleForSwap(cluster1,action1,cluster2,action2,neighborpartition,deltasforclusterswactions,deltasforclusterswoactions,player);
								if(compatible)
								{
									/**
									 * swap the actions and return true
									 */
									neighborpartition[player][cluster1].remove(neighborpartition[player][cluster1].indexOf(action1));
									neighborpartition[player][cluster2].remove(neighborpartition[player][cluster2].indexOf(action2));


									neighborpartition[player][cluster1].add(action2);
									neighborpartition[player][cluster2].add(action1);
									//System.out.println("Action " + action1 + " is swapped with "+action2);
									//System.out.println("cluster " + cluster1 + " is swapped with "+cluster2);
									return true;
								}

							}
						}
					}

				}
			}
		}
		return false;


	}

	private static boolean areClustersCompatibleForSwap(int cluster1,
			int action1, int cluster2, int action2,
			List<Integer>[][] neighborpartition,
			double[][] deltasforclusterswactions,
			double[][] deltasforclusterswoactions, int player) {

		/**
		 * try to check if including action1 in cluster2 makes the delta of cluster2 same or less
		 * and same for action2: check if including action2 in cluster1 makes delta less or same
		 * if both of these condition holds then we can swap them.
		 */
		double diff1_1 = deltasforclusterswactions[cluster2][action1-1] - deltasforclusterswoactions[cluster2][action1-1];
		double diff1_2 =   deltasforclusterswoactions[cluster2][action2-1] - deltasforclusterswactions[cluster2][action2-1];
		if((diff1_1+diff1_2)>0)
			return false;

		double diff2_1 = deltasforclusterswactions[cluster1][action2-1] - deltasforclusterswoactions[cluster1][action2-1];
		double diff2_2 =   deltasforclusterswoactions[cluster1][action1-1] - deltasforclusterswactions[cluster1][action1-1];
		if((diff2_1+diff2_2)>0)
			return false;

		return true;
	}

	private static Double[][] findMostPromisingCluster(
			List<Integer>[][] neighborpartition,
			double[][] deltasforclusterswactions,
			double[][] deltasforclusterswoactions, int player) {


		int numberofactions = neighborpartition[0].length * neighborpartition[0][0].size();
		Double[][] rankforcluster = new Double[numberofactions][3];
		int rankindex = 0;
		for(int cluster=0; cluster<neighborpartition[player].length; cluster++)
		{
			for(int action : neighborpartition[player][cluster])
			{
				rankforcluster[rankindex][0] = deltasforclusterswactions[cluster][action-1] - deltasforclusterswoactions[cluster][action-1];
				rankforcluster[rankindex][1] = Math.floor(cluster);
				rankforcluster[rankindex][2] = Math.floor(action);
				rankindex++;
			}


		}
		sortArrayDesc(rankforcluster);
		return rankforcluster;
	}

	private static void sortArrayDesc(Double[][] ds) {

		Double[] swap = new Double[ds[0].length];

		for (int i = 0; i < ds.length; i++) 
		{
			for (int d = 1; d < ds.length-i; d++) 
			{
				if (ds[d-1][0] < ds[d][0]) /* For descending order use < */
				{
					swap = ds[d];
					ds[d]  = ds[d-1];
					ds[d-1] = swap;
				}
			}
		}

	}


	private static void sortArrayAsc(Double[][] ds) {

		Double[] swap = new Double[ds[0].length];

		for (int i = 0; i < ds.length; i++) 
		{
			for (int d = 1; d < ds.length-i; d++) 
			{
				if (ds[d-1][0] > ds[d][0]) /* For descending order use < */
				{
					swap = ds[d];
					ds[d]  = ds[d-1];
					ds[d-1] = swap;
				}
			}
		}

	}

	private static List<Integer>[][] createNeighborPartition(
			MatrixGame game,
			List<Integer>[][] currentpartition, double currentmindelta, 
			int clustertochange, int player) 
			{

		double [][] delta = new double[2][currentpartition[player].length];
		double newdelta = -1;
		List<Integer>[][] bestneighbor = getEmptyClusters(2, currentpartition[player].length);
		double bestmindelta = Double.POSITIVE_INFINITY; 
		int iteration = 0;
		while(true)
		{

			int opponent = 1^player;
			List<Integer>[][] neighborpartition = getEmptyClusters(2, currentpartition[player].length);
			deepCopyCluster(currentpartition, neighborpartition,player);
			deepCopyCluster(currentpartition, neighborpartition,opponent);
			int cluster1 = clustertochange;
			int cluster2 = -1;
			while(true)
			{
				cluster2 = MakeGameForPartition.randInt(0, currentpartition[player].length-1);
				if(cluster1 != cluster2)
				{
					break;
				}

			}
			//instead of finding the actions in  cluster1 and cluster2 randomly, choose the action
			//which is the farthest from the average of min and max 
			int huristicaction1 = findFurthestAction(game,neighborpartition,cluster1, player);
			int huristicaction2 = findFurthestAction(game,neighborpartition,cluster2, player);




			//	int indx1 = MakeGameForPartition.randInt(0, GameReductionBySubGame.partition[i][cluster1].size()-1);
			//	int indx2 = MakeGameForPartition.randInt(0, GameReductionBySubGame.partition[i][cluster2].size()-1);


			neighborpartition[player][cluster1].remove(neighborpartition[player][cluster1].indexOf(huristicaction1));
			neighborpartition[player][cluster2].remove(neighborpartition[player][cluster2].indexOf(huristicaction2));


			neighborpartition[player][cluster1].add(huristicaction2);
			neighborpartition[player][cluster2].add(huristicaction1);

			calculateDeltasForAPlayer(delta, neighborpartition, 2, neighborpartition[player].length, game, player);
			newdelta = minDelta(delta[player], player);
			if(newdelta < bestmindelta )
			{
				deepCopyCluster(neighborpartition, bestneighbor, 0);
				deepCopyCluster(neighborpartition, bestneighbor, 1);
				bestmindelta = newdelta;

			}
			if(bestmindelta<=currentmindelta && iteration>100)
			{
				return bestneighbor;
			}
			iteration++;
		}



			}





	/**
	 * returns delta with worses cluster
	 * @param game
	 * @param cluster
	 * @param player
	 * @param max
	 * @return
	 */
	private static double[] calculateDelta(Game game, List<Integer>[][] cluster, int player, boolean max)
	{

		//	System.out.println("Staring epsilon calcl**************");
		int[] numactions = game.getNumActions();

		double[] deltas = new double[cluster[player].length]; // there are deltas for each cluster. 
		int opponent =0;
		if(player==0)
			opponent =1;





		/*
		 * for each cluster take the actions and calcualte the delta
		 */
		for(int i=0; i< cluster[player].length; i++) // can be improved, i<cluster.length-1
		{

			double maxdiffplayer =0;

			for(Integer x : cluster[player][i]  ) // x[0] is the action
			{
				for(Integer y: cluster[player][i]) // can be improved , cluster[i+1]
				{
					if(cluster[player][i].indexOf(x)!=cluster[player][i].indexOf(y))// dont want to calculate difference between same actions
					{

						// now iterate over payoffs for action x[0] and y[0]

						for(int z =1; z<= numactions[opponent]; z++)
						{
							if(!cluster[opponent][i].contains(z)) // if  cluster i does not have z
							{



								int[] outcome1 = new int[2];
								int[] outcome2 = new int[2];


								if(player==0)
								{
									outcome1[0] = x;
									outcome1[1] =  z;
									outcome2[0] = y;
									outcome2[1] =  z;

								}
								else if(player==1)
								{
									outcome1[1] = x;
									outcome1[0] =  z;
									outcome2[1] = y;
									outcome2[0] =  z;
								}

								double payoff1= game.getPayoff(outcome1, player);
								double payoff2 = game.getPayoff(outcome2, player);


								double diff =0;
								if((payoff1<0 && payoff2< 0) || (payoff1>=0 && payoff2>=0))
								{
									diff = Math.abs(Math.abs(payoff2) - Math.abs(payoff1));

								}
								else if(payoff1<0 && payoff2>= 0)
								{
									diff = Math.abs(Math.abs(payoff1) + payoff2);
								}
								else if(payoff1>=0 && payoff2< 0)
								{
									diff = Math.abs(Math.abs(payoff2) + payoff1);
								}


								if(diff>maxdiffplayer)
									maxdiffplayer= diff;

							}


						}


					}
				}// inner cluster loop


			} // outer cluster loop

			deltas[i] = maxdiffplayer;
		}



		if(max==true)
		{
			int worstcluster =-1;
			double maximum = Double.NEGATIVE_INFINITY;

			for(int i=0; i< deltas.length; i++)
			{
				if(deltas[i]>maximum)
				{
					worstcluster = i;
					maximum = deltas[i];
				}
			}
			double[] deltawithcluster = {maximum, worstcluster};
			return deltawithcluster;

		}
		else
		{

			int worstcluster =-1;
			double maximum = Double.NEGATIVE_INFINITY;

			for(int i=0; i< deltas.length; i++)
			{
				if(deltas[i]>maximum)
				{
					worstcluster = i;
					maximum = deltas[i];
				}
			}


			double sum = 0.0;

			for(int i=0; i< deltas.length; i++)
			{
				sum+=deltas[i];
			}

			sum= sum/deltas.length;

			double[] deltawithcluster = {sum, worstcluster};
			return deltawithcluster;

			//return deltawithcluster;


		}





		//return deltas;
	}




	private static boolean checkIfPartitionsAreEqual(List<Integer>[][] currentpartition,
			List<Integer>[][] lastneighborpartition, int player) {


		boolean flag = false;
		for(List <Integer> x: currentpartition[player])
		{
			flag = false;
			for(List<Integer> y: lastneighborpartition[player])
			{
				if(checkSImilaritOfTwoCluster(x, y))
				{
					flag = true;
					break;
				}
			}
			if(flag==false)
			{
				return false;
			}
		}
		//}

		return true;
	}

	private static boolean checkSImilaritOfTwoCluster(List<Integer> cluster1, List<Integer> cluster2) {

		boolean flag = false;
		for(Integer x: cluster1)
		{
			flag = false;

			for(Integer y: cluster2)
			{
				if(x==y)
				{
					flag = true;
					break;
				}
			}
			if(flag==false)
			{
				return false;
			}
		}

		return true;
	}





	private static int findFurthestAction(MatrixGame game, List<Integer>[][] partition, int cluster, int player) {

		int opponent =0;
		if(player==0)
		{
			opponent =1;
		}
		int numactionsplayer = game.getNumActions(player);
		int numactionsopponent =  game.getNumActions(opponent);
		//select the cluster to find an action which is far away from center. 
		//int cluster = MakeGameForPartition.randInt(0, GameReductionBySubGame.numberofsubgames-1);
		ArrayList<Double> meanvector = new ArrayList<Double>();
		for(int j=0; j<numactionsopponent; j++)
		{
			if(!partition[opponent][cluster].contains(j+1))
			{

				double minpayoff = (int)Double.POSITIVE_INFINITY;
				double maxpayoff = (int)Double.NEGATIVE_INFINITY;
				for(int x: partition[player][cluster])
				{
					int[] outcome = new int[2];
					if(player==0)
					{
						outcome[0] = x;
						outcome[1] = j+1;
					}
					if(player==1)
					{
						outcome[0] = j+1;
						outcome[1] = x;
					}
					double payoff = game.getPayoff(outcome, player);
					if(payoff>maxpayoff)
					{
						maxpayoff = payoff;
					}
					if(payoff<minpayoff){
						minpayoff = payoff;
					}
				}
				double y = (maxpayoff+minpayoff)/2.0;
				meanvector.add(y);
			}
		}

		int furthestactionsofar = -1;
		double furthestval = Double.NEGATIVE_INFINITY;
		for(int x: partition[player][cluster])
		{
			int index = 0;
			for(int j=0; j<numactionsopponent; j++)
			{

				if(!partition[opponent][cluster].contains(j+1))
				{
					int[] outcome = new int[2];
					outcome[player] = x;
					outcome[opponent] = j+1;
					double payoff = game.getPayoff(outcome, player);
					double diff = Math.abs(meanvector.get(index++)-payoff);
					if(diff>furthestval)
					{
						furthestval = diff;
						furthestactionsofar = x;
					}
				}
			}

		}



		return furthestactionsofar;
	}



	/**
	 * Does kmean clustering for games, for subgame solution concept
	 * Iterates over the clusters
	 * Does clustering for one players until it converges, then for the next player.
	 * And so on...
	 * @param numberofclusters
	 * @param game
	 * @return
	 * @throws Exception
	 */
	public static List<Integer>[][] clusterActionsV2_1(int numberofclusters, MatrixGame game ) throws Exception
	{
		int numberofplayers = game.getNumPlayers();
		int[] numactions = game.getNumActions();
		int[] clustersize = {numactions[0]/numberofclusters, numactions[1]/numberofclusters};
		//double[][][] centroids = new double[numberofplayers][numberofclusters][];
		double [][] delta = new double[numberofplayers][numberofclusters];

		/**
		 * create an empty cluster object
		 */
		List<Integer>[][] clusters = getEmptyClusters(numberofplayers,numberofclusters);
		/**
		 * initialize cluster with random actions
		 */
		initilizeClusters(clusters,clustersize, numactions, RAND_ACTION_IN_CLUSTER);
		/**
		 * cluster object to contain the clustering for previous round
		 */
		List<Integer>[][] oldclusters = getEmptyClusters(numberofplayers,numberofclusters);
		/**
		 * copy cluster to old cluster
		 */
		deepCopyCuster(clusters, oldclusters);

		int iteration = 0;
		int playercounter = 0;
		int player = 0;
		/**
		 * do clustering until it is unchanged for one player
		 * Then move to the next player and so on...
		 */
		boolean clusterchnaged = true;
		double mindeltaamongiterations = Double.POSITIVE_INFINITY;
		double LIMIT = -1;//Double.MIN_VALUE;
		int iterationlimit = 100;
		double ultimatemindelta = Double.POSITIVE_INFINITY;
		List<List<Integer>[][]> historyofclusters = new ArrayList<List<Integer>[][]>() ;
		while(clusterchnaged)
		{

			List<Integer>[][] newclusters = getEmptyClusters(numberofplayers,numberofclusters);
			calculateDeltasForAPlayer(delta,clusters,numberofplayers,numberofclusters, game,player);
			double [][] deltasforclusterswactions = new double[numberofclusters][ numactions[0] ]; // delta if action is  in cluster
			double [][] deltasforclusterswoactions = new double[numberofclusters][ numactions[0] ]; // delta if action is not in cluster
			calculateDeltasIfAddedToClustersV2(delta, deltasforclusterswactions,deltasforclusterswoactions,clusters,numberofplayers,numberofclusters,clustersize,game, player);
			assignActionsToClusterV2(newclusters,delta,deltasforclusterswactions, deltasforclusterswoactions, clustersize, player);
			boolean changed = checkIfClustersChangedForPlayer(newclusters,clusters,player);
			deepCopyCluster(newclusters,clusters,player);
			calculateDeltasForAPlayer(delta,clusters,numberofplayers,numberofclusters, game,player);
			double mindelta = minDelta(delta[player],player);
			if(mindelta<mindeltaamongiterations)
			{
				mindeltaamongiterations = mindelta;
			}
			if(iteration==iterationlimit)
			{
				iterationlimit *=4;
				LIMIT = mindeltaamongiterations;
				mindeltaamongiterations = Double.MAX_VALUE;
			}

			boolean isdeltalelimit = checkIfdeltalelimit(delta[player], LIMIT);

			if(!changed || isdeltalelimit)
			{
				mindeltaamongiterations = Double.MAX_VALUE;
				LIMIT = -1;//Double.MIN_VALUE;
				iteration=0;
				iterationlimit= 100;
				/**
				 * move to next player
				 */
				player = 1^player;
				playercounter++;
				if(playercounter==2)
				{
					/**
					 * check if the cluster changed for both players
					 */

					if(!checkIfClustersChanged(oldclusters,clusters))
					{
						clusterchnaged = false;
					}
					calculateDeltas(delta,clusters,numberofplayers,numberofclusters, game);
					double d1 = minDelta(delta[0],0);
					double d2= minDelta(delta[0],0);
					if(Math.min(d1, d2) < ultimatemindelta)
					{
						//System.out.println("Ultimatemin delta "+ ultimatemindelta);
						ultimatemindelta = Math.min(d1, d2);
					}
					if(d1<10 || d2<10)
					{
						//System.out.println("hmm");
					}
					minDelta(delta[1],1);
					//System.out.println("Deltas : "+ delta[0][0] + ","+delta[0][1]+","+delta[0][2]);
					//System.out.println("Deltas : "+ delta[1][0] + ","+delta[1][1]+","+delta[1][2]);
					//System.out.println("Ultimatemin delta "+ ultimatemindelta);
					double maxdelta[] = findMaxDelta(delta);
					System.out.println(" Maxdelta " + maxdelta[0] + " , "+ maxdelta[1]);
					if(Math.max(maxdelta[0] , maxdelta[1] ) <= 0)
					{
						clusterchnaged = false;
					}
					deepCopyCuster(clusters, oldclusters);
					List<Integer>[][] tmpclusters = getEmptyClusters(numberofplayers,numberofclusters);
					deepCopyCuster(clusters, tmpclusters);
					/**
					 * CHECK whether clusters are repeating
					 * if so, calculate the min max delta.
					 * Find the player for min max delta.
					 * fix the cluster for that player
					 * choose the opponent as next player to do clustering
					 */
					if(historyofclusters.size()==0)
					{
						historyofclusters.add(tmpclusters);
					}
					else
					{
						player = chekForRepeatition(game,historyofclusters,clusters,player);
					}
					//historyofclusters.add(tmpclusters);

					/**
					 * reset player counter
					 */
					playercounter = 0;
				}


			}

			iteration++;
		}
		return clusters;

	}




	private static boolean checkIfdeltalelimit(double[] ds, double limit) {

		for(double x: ds)
		{
			if(x <= limit)
				return true;
		}
		return false;
	}


	private static boolean checkForRepeatition(
			MatrixGame game, List<List<Integer>[][]> historyofclusters,
			List<Integer>[][] clusters) {

		//double minimummaxdelta = Double.POSITIVE_INFINITY;
		//boolean repeatition = false;
		//int repeatedclusterplayer = -1;
		//for(int player = 0; player<2;player++)
		{
			for(List<Integer>[][] clustersforplayers : historyofclusters)
			{

				//for(int cluster=0; cluster<clustersforplayers[0].length; cluster++)
				{
					boolean different1 = checkIfClustersChangedForAPlayer(clustersforplayers, clusters, 0);
					boolean different2 = checkIfClustersChangedForAPlayer(clustersforplayers, clusters, 1);
					if(!different1 && !different2)
					{
						//repeatition = true;
						return true;
					}
				}

			}
		}
		return false;


	}



	/**
	 * CHECK whether clusters are repeating
	 * if so, calculate the min max delta.
	 * Find the player for min max delta.
	 * fix the cluster for that player
	 * choose the opponent as next player to do clustering
	 * @param historyofclusters
	 * @param clusters
	 * @return
	 */
	private static boolean chekForRepeatitionForAPlayer(
			MatrixGame game, List<List<Integer>[][]> historyofclusters,
			List<Integer>[][] clusters, int player) {

		//double minimummaxdelta = Double.POSITIVE_INFINITY;
		//boolean repeatition = false;
		//int repeatedclusterplayer = -1;
		//for(int player = 0; player<2;player++)
		//{
		for(List<Integer>[][] clustersforplayers : historyofclusters)
		{

			//for(int cluster=0; cluster<clustersforplayers[player].length; cluster++)
			{
				boolean different = checkIfClustersChangedForAPlayer(clustersforplayers, clusters, player);
				if(!different)
				{
					//repeatition = true;
					//repeatedclusterplayer = player;
					return true;
				}
			}

		}
		return false;


	}



	/**
	 * CHECK whether clusters are repeating
	 * if so, calculate the min max delta.
	 * Find the player for min max delta.
	 * fix the cluster for that player
	 * choose the opponent as next player to do clustering
	 * @param historyofclusters
	 * @param clusters
	 * @return
	 */
	private static int chekForRepeatition(
			MatrixGame game, List<List<Integer>[][]> historyofclusters,
			List<Integer>[][] clusters, int currentplayer) {

		double minimummaxdelta = Double.POSITIVE_INFINITY;
		boolean repeatition = false;
		int repeatedclusterplayer = -1;
		for(int player = 0; player<2;player++)
		{
			for(List<Integer>[][] clustersforplayers : historyofclusters)
			{

				for(int cluster=0; cluster<clustersforplayers[player].length; cluster++)
				{
					boolean different = checkIfClustersChangedForAPlayer(clustersforplayers, clusters, player);
					if(!different)
					{
						repeatition = true;
						repeatedclusterplayer = player;
						break;
					}
				}
				if(repeatition)
					break;


			}
			if(repeatition)
				break;
		}
		/**
		 * now find the cluster with minmaxdelta
		 * if repeatition is true for repeatedclusterplayer
		 */
		int clusterno = 0;
		int clusterwithminmaxdelta = -1;
		if(repeatition)
		{
			for(List<Integer>[][] clustersforplayers : historyofclusters)
			{
				double [][] delta = new double[2][clustersforplayers[repeatedclusterplayer].length];
				calculateDeltasForAPlayer(delta, clustersforplayers, 2, clustersforplayers[repeatedclusterplayer].length, game, repeatedclusterplayer);
				double tmpdelta = findMaxDelta(delta)[repeatedclusterplayer];
				if(tmpdelta<minimummaxdelta)
				{
					minimummaxdelta = tmpdelta;
					clusterwithminmaxdelta = clusterno; 
				}
				clusterno++;
			}
			List<Integer>[][] clustertouse = historyofclusters.get(clusterwithminmaxdelta);
			deepCopyCluster(clustertouse, clusters, repeatedclusterplayer);
			return 1^repeatedclusterplayer;
		}
		else
		{
			List<Integer>[][] tmpclustersforplayers = getEmptyClusters(2, clusters[0].length);
			deepCopyCluster(clusters, tmpclustersforplayers,0);
			deepCopyCluster(clusters, tmpclustersforplayers,1);
			historyofclusters.add(tmpclustersforplayers);
			return currentplayer;
		}


	}

	/**
	 * Does kmean clustering for games, for subgame solution concept
	 * Iterates over the clusters
	 * @param numberofclusters
	 * @param game
	 * @return
	 * @throws Exception
	 */
	public static List<Integer>[][] clusterActionsV2(int numberofclusters, MatrixGame game ) throws Exception
	{

		int numberofplayers = game.getNumPlayers();
		int[] numactions = game.getNumActions();
		int[] clustersize = {numactions[0]/numberofclusters, numactions[1]/numberofclusters};
		//double[][][] centroids = new double[numberofplayers][numberofclusters][];
		double [][] delta = new double[numberofplayers][numberofclusters];

		/**
		 * create an empty cluster object
		 */
		List<Integer>[][] clusters = getEmptyClusters(numberofplayers,numberofclusters);
		/**
		 * initialize cluster with random actions
		 */
		initilizeClusters(clusters,clustersize, numactions, RAND_ACTION_IN_CLUSTER);
		int iteration = 0;
		while(iteration++ < 50)
		{

			int playercounter = 0;
			List<Integer>[][] newclusters = getEmptyClusters(numberofplayers,numberofclusters);
			for(int player=0; player<2; player++)
			{
				calculateDeltas(delta,clusters,numberofplayers,numberofclusters, game);
				//printDeltas(delta);
				double maxdelta[] = findMaxDelta(delta);
				System.out.println("Iteration "+iteration+" Maxdelta " + maxdelta[0] + " , "+ maxdelta[1]);
				Logger.logit("\nIteration "+iteration+" Maxdelta " + maxdelta[0] + " , "+ maxdelta[1]);
				/**
				 * test
				 */
				//List<Integer>[][] dummyparition = createDummyPartition();
				//calculateDeltas(delta,dummyparition,2,3, game);
				/**
				 * For every action calculate the delta if it is added to a cluster
				 * [#player][#action][#cluster]
				 */
				System.out.println("Cluster before actions reassignment for player "+player);
				Logger.logit("\nCluster before actions reassignment for player "+player+"\n");
				printClusters(clusters,player);
				double [][] deltasforclusterswactions = new double[numberofclusters][ numactions[0] ]; // delta if action is  in cluster
				double [][] deltasforclusterswoactions = new double[numberofclusters][ numactions[0] ]; // delta if action is not in cluster
				//double [][] deltaswithoutactions = new double[numactions[0]][numberofclusters];
				calculateDeltasIfAddedToClustersV2(delta, deltasforclusterswactions,deltasforclusterswoactions,clusters,numberofplayers,numberofclusters,clustersize,game, player);
				//clearCluster(clusters,player);
				/**
				 * create an empty cluster object
				 * after the new object is full
				 * use it as next iteration
				 */

				assignActionsToClusterV2(newclusters,delta,deltasforclusterswactions, deltasforclusterswoactions, clustersize, player);
				calculateDeltas(delta,clusters,numberofplayers,numberofclusters, game);
				printDeltas(delta);
				playercounter++;
				if(playercounter==2)
				{
					/**
					 * deep copy of newcluster to cluster
					 */
					System.out.println("Cluster after actions reassignment for player "+player);
					Logger.logit("\nCluster after actions reassignment for player "+player+"\n");
					printClusters(newclusters, player);
					deepCopyCuster(newclusters,clusters);
					newclusters = getEmptyClusters(numberofplayers,numberofclusters);
				}
				playercounter = playercounter  % 2;


			}
		}
		return clusters;

	}


	/**
	 * tries to cluster actions depending on the distance from the mid of cluster
	 * @param numberofclusters
	 * @param game
	 * @return
	 * @throws Exception
	 */
	public static List<Integer>[][] clusterActionsV3(int numberofclusters, MatrixGame game ) throws Exception
	{
		int numberofplayers = game.getNumPlayers();
		int[] numactions = game.getNumActions();
		int[] clustersize = {numactions[0]/numberofclusters, numactions[1]/numberofclusters};
		double[][][] centroids = new double[numberofplayers][numberofclusters][];
		double [][] delta = new double[numberofplayers][numberofclusters];
		double distances[][] = new double[numactions[0]][numberofclusters]; //assuming that 

		/**
		 * create an empty cluster object
		 */
		List<Integer>[][] clusters = getEmptyClusters(numberofplayers,numberofclusters);
		/**
		 * initialize cluster with random actions
		 */
		initilizeClusters(clusters,clustersize, numactions, RAND_ACTION_IN_CLUSTER);

		createEmptyCentroids(centroids, numactions,clustersize);



		int iteration = 0;
		double bestmaxdelta = Double.POSITIVE_INFINITY;
		while(iteration++ < 500)
		{

			int playercounter = 0;

			List<Integer>[][] newclusters = getEmptyClusters(numberofplayers,numberofclusters);

			calculateCentroids(centroids,clusters,numactions,game);
			for(int player=0; player<2; player++)
			{
				/**
				 * calcualte the distances for every action to every cluster. 
				 */
				calculateDistances(game, clusters, clustersize[player],centroids, distances,numactions[player],player);
				/**
				 * For every action calculate the delta if it is added to a cluster
				 * [#player][#action][#cluster]
				 */
				System.out.println("Cluster before actions reassignment for player "+player);
				Logger.logit("\nCluster before actions reassignment for player "+player+"\n");
				printClusters(clusters,player);
				//double [][] deltasforactions = new double[numactions[0]][numberofclusters];
				//double [][] deltaswithoutactions = new double[numactions[0]][numberofclusters];
				//calculateDeltasIfAddedToClustersV1(delta, deltasforactions,deltaswithoutactions,clusters,numberofplayers,numberofclusters,clustersize,game, player);
				//clearCluster(clusters,player);
				/**
				 * create an empty cluster object
				 * after the new object is full
				 * use it as next iteration
				 */

				assignActionsToClusterV3(newclusters,clustersize[player],distances, numactions[player], player);

				//printDeltas(delta);
				playercounter++;
				if(playercounter==2)
				{
					/**
					 * deep copy of newcluster to cluster
					 */
					System.out.println("Cluster after actions reassignment for player "+player);
					Logger.logit("\nCluster after actions reassignment for player "+player+"\n");
					printClusters(newclusters, player);
					deepCopyCuster(newclusters,clusters);
					newclusters = getEmptyClusters(numberofplayers,numberofclusters);
					calculateDeltas(delta,clusters,numberofplayers,numberofclusters, game);
					double maxdelta[] = findMaxDelta(delta);
					if(Math.max(maxdelta[0], maxdelta[1])<bestmaxdelta)
					{
						bestmaxdelta = Math.max(maxdelta[0], maxdelta[1]);
					}
					System.out.println("Best delta "+bestmaxdelta);

					System.out.println("Iteration "+iteration+" Maxdelta " + maxdelta[0] + " , "+ maxdelta[1]);
					Logger.logit("\nIteration "+iteration+" Maxdelta " + maxdelta[0] + " , "+ maxdelta[1]);
				}
				playercounter = playercounter  % 2;
			}


		}
		return clusters;
	}



	/**
	 * assigns action to cluster depending on the distances
	 * @param newclusters
	 * @param distances
	 * @param i
	 * @param player
	 */
	private static void assignActionsToClusterV3(List<Integer>[][] newclusters, int clustersize,
			double[][] distances, int numberofactions, int player) {

		for(int action=0; action<numberofactions; action++)
		{
			Double[][] distancewithclusterindex = new Double[distances[action].length][2];
			filldistancewithclusterindex(distances,distancewithclusterindex, action);
			sortArray(distancewithclusterindex);
			int clustertoassignto = -1;
			//sortArray(deltawithclusterindex);
			int rank = 0;
			while(rank<distancewithclusterindex.length)
			{
				/**
				 * find the minimum delta
				 */
				clustertoassignto = findClusterWithRankedDistance(distancewithclusterindex, rank);
				if(newclusters[player][clustertoassignto].size()<clustersize)
				{
					newclusters[player][clustertoassignto].add(action+1);
					System.out.println("Player" + player+" action "+(action+1)+ " is asigned to cluster "+ clustertoassignto);
					Logger.logit("\nPlayer" + player+" action "+(action+1)+ " is asigned to cluster "+ clustertoassignto+" with rank "+rank+"\n");
					break;
				}
				else
				{
					clustertoassignto = -1;
				}
				rank++;
			}

		}

	}

	private static Integer findClusterWithRankedDistance(
			Double[][] distancewithclusterindex, int rank) {

		return distancewithclusterindex[rank][1].intValue();
	}

	private static void sortArray(double[][] ds) {

		double[] swap = {0.0,0.0};

		for (int i = 0; i < ds.length; i++) 
		{
			for (int d = 1; d < ds.length-i; d++) 
			{
				if (ds[d-1][0] > ds[d][0]) /* For descending order use < */
				{
					swap = ds[d];
					ds[d]  = ds[d-1];
					ds[d-1] = swap;
				}
			}
		}

	}

	private static void filldistancewithclusterindex(double[][] distances,
			Double[][] distancewithclusterindex, int action) {
		for(int cluster=0; cluster<distances[action].length; cluster++)
		{
			distancewithclusterindex[cluster][0] = distances[action][cluster];
			distancewithclusterindex[cluster][1] = Math.floor(cluster);
		}



	}

	/**
	 * 
	 * @param game
	 * @param clusters
	 * @param clustersize
	 * @param centroids
	 * @param distances
	 * @param numerofactions
	 * @param player
	 */
	private static void calculateDistances(MatrixGame game, List<Integer>[][] clusters, int clustersize,
			double[][][] centroids, double[][] distances, int numerofactions, int player) {


		for(int action=0; action<numerofactions; action++)
		{
			for(int cluster = 0; cluster<clusters[player].length; cluster++)
			{
				double[] distancearray = new double[numerofactions-clustersize];
				int distancearrayindex = 0;
				for(int opponentaction = 0; opponentaction<numerofactions; opponentaction++)
				{
					if(!clusters[1^player][cluster].contains(opponentaction+1)) // if action is not in opponent's cluster
					{
						int[] outcome = new int[2];
						if(player==0)
						{
							outcome[0] = action+1;
							outcome[1] = opponentaction+1;
						}
						else if(player==1)
						{
							outcome[0] = opponentaction+1;
							outcome[1] = action+1; 

						}
						distancearray[distancearrayindex] = Math.abs(centroids[player][cluster][distancearrayindex] - game.getPayoff(outcome, player));
						distancearrayindex++;

					}

				}
				/**
				 * put the distance of an action from the cluster
				 */
				computeDistance(distances, distancearray, action, cluster);
			}
		}


	}

	private static void computeDistance(double[][] distances,
			double[] distancearray, int action, int cluster) {

		double sum = 0;
		for(double x: distancearray)
		{
			sum+= x;
		}
		distances[action][cluster] = sum;


	}

	/**
	 * Assign actions to a cluster
	 * @param newclusters
	 * @param delta
	 * @param deltasforactions
	 * @param deltaswithoutactions
	 * @param clustersize
	 * @param player
	 * @throws Exception
	 */
	private static void assignActionsToClusterV1(List<Integer>[][] newclusters,
			double[][] delta, double[][] deltasforactions, 
			double[][] deltaswithoutactions, 
			int[] clustersize, int player) throws Exception {

		//for(int player=0; player<clusters.length; player++)
		//{

		/**
		 * 1. for every action find the cluster with minimum delta,
		 * Try to to assign it if the cluster is not full.
		 * If full try the next nearest cluster and so on... 
		 * 
		 */
		for(int action=0; action<deltasforactions.length; action++)
		{
			int rank = 0;
			/**
			 * stores the change of delta for action
			 */
			Double[][] deltawithclusterindex = new Double[newclusters[player].length][2];
			Logger.logit("In Distance metric from action "+ (action+1) + " to other clusters");
			fillDeltawithClusterIndex(deltawithclusterindex, deltasforactions[action], deltaswithoutactions[action]);
			/**
			 * iterate untill an assignment is found
			 */

			int clustertoassignto = -1;
			sortArray(deltawithclusterindex);
			while(rank<deltasforactions[action].length)
			{
				/**
				 * find the minimum delta
				 */
				clustertoassignto = findClusterWithRankedDelta(deltawithclusterindex, rank);
				if(newclusters[player][clustertoassignto].size()<clustersize[player])
				{
					newclusters[player][clustertoassignto].add(action+1);
					System.out.println("Player" + player+" action "+(action+1)+ " is asigned to cluster "+ clustertoassignto);
					Logger.logit("\nPlayer" + player+" action "+(action+1)+ " is asigned to cluster "+ clustertoassignto+" with rank "+rank+"\n");
					break;
				}
				else
				{
					clustertoassignto = -1;
				}
				rank++;
			}
			if(clustertoassignto==-1)
			{
				throw new Exception("Problem in assigning action to cluster");
			}

		}


		//	}

	}




	/**
	 * CLuster chooses an action to include to itself
	 * @param newclusters
	 * @param delta
	 * @param deltasforclusterswactions
	 * @param deltasforclusterswoactions
	 * @param clustersize
	 * @param player
	 * @throws Exception 
	 */
	private static void assignActionsToClusterV2(List<Integer>[][] newclusters,
			double[][] delta, double[][] deltasforclusterswactions,
			double[][] deltasforclusterswoactions, int[] clustersize, int player) throws Exception {

		/**
		 * for every cluster try to find the closest action
		 * sort the actions in the ascending order of delta
		 */
		ArrayList<Integer> alreadyassigned = new ArrayList<Integer>();
		//int actioncounter = 1;
		int numberofactions = clustersize[0]*deltasforclusterswoactions.length;
		int cluster = 0;
		while(alreadyassigned.size()<numberofactions)
		{
			Double[][] deltawithaction = new Double[numberofactions-alreadyassigned.size()][2];
			//Logger.logit("In Distance metric, distance from cluster "+ (cluster) + " to other actions");
			fillDeltawithAction(deltawithaction, deltasforclusterswactions[cluster], deltasforclusterswoactions[cluster], alreadyassigned);
			/**
			 * iterate untill an assignment is found
			 */

			int actiontoassign = -1;
			sortArray(deltawithaction);
			int rank=0;
			while(rank<deltasforclusterswactions[cluster].length)
			{
				/**
				 * find the minimum delta
				 */
				actiontoassign = findActionWithRankedDelta(deltawithaction, rank);
				/**
				 * besides size check also if the action has been assigned to a cluster
				 */
				if(newclusters[player][cluster].size()<clustersize[player] && (!alreadyassigned.contains(actiontoassign)))
				{
					newclusters[player][cluster].add(actiontoassign);
					alreadyassigned.add(actiontoassign);
					//System.out.println("Player" + player+" action "+(actiontoassign)+ " is asigned to cluster "+ cluster);
					Logger.logit("\nPlayer" + player+" action "+(actiontoassign)+ " is asigned to cluster "+ cluster+" with rank "+rank+"\n");
					break;
				}
				else
				{
					//System.out.println("Action "+ actiontoassign + " already assigned");
					Logger.logit("\nAction "+ actiontoassign + " already assigned\n");
					actiontoassign = -1;

				}
				rank++;
			}
			if(actiontoassign==-1)
			{
				throw new Exception("Problem in assigning action to cluster");
			}
			cluster++;
			if(cluster==deltasforclusterswoactions.length)
			{
				cluster = cluster % deltasforclusterswoactions.length;
			}

		}


	}

	/**
	 * returns deltas if an action is added to a cluster
	 * @param deltas deltas for clusters
	 * @param deltasforactions deltas if an action is added to a cluster
	 * @param clusters 
	 * @param numberofplayers
	 * @param numberofclusters
	 * @param clustersize
	 * @param game
	 */
	private static void calculateDeltasIfAddedToClustersV1( double[][] deltas,
			double[][] deltasforactions, double[][] deltaswithoutactions, List<Integer>[][] clusters,
			int numberofplayers, int numberofclusters, int[] clustersize, MatrixGame game, int player) 
	{
		//for(int player=0; player<numberofplayers; player++)
		//{
		System.out.println("For player : "+player);
		Logger.logit("\nFor player : "+player+"\n");
		for(int action=0; action<deltasforactions.length; action++)
		{
			for(int cluster=0; cluster<deltasforactions[action].length; cluster++)
			{
				/**
				 * check if action is already in the cluster
				 */
				System.out.println("\n\nFor action "+ (action+1) + ", cluster "+cluster);
				//Logger.logit("\n\nFor action "+ (action+1) + ", cluster "+cluster+"\n");

				if(clusters[player][cluster].contains(action+1))
				{
					System.out.println("Action "+(action+1)+" is in cluster "+ cluster);
					//Logger.logit("Action "+(action+1)+" is in cluster "+ cluster+"\n");
					deltasforactions[action][cluster] = deltas[player][cluster];
					System.out.println("Delta with action : "+ deltas[player][cluster]);
					//Logger.logit("Delta with action : "+ deltas[player][cluster]+"\n");
					/**
					 * need to calculate the delta if the action is not in the cluster
					 * and assign that to deltas[player][cluster]
					 */
					List<Integer>[][] tmpcluster = getEmptyClusters(numberofplayers, numberofclusters);
					makeDeepCopy(clusters,tmpcluster);
					int indexofactiontoremove = tmpcluster[player][cluster].indexOf(action+1);
					tmpcluster[player][cluster].remove(indexofactiontoremove);
					/**
					 * following method returns 0 if the cluster size is 1 or 0
					 */
					deltaswithoutactions[action][cluster] = calculateDelta(tmpcluster, cluster, player, game);
					System.out.println("Delta without action : "+ deltaswithoutactions[action][cluster]);
					//Logger.logit("Delta without action : "+ deltaswithoutactions[action][cluster]+"\n");

				}
				else
				{
					System.out.println("Action "+(action+1)+" is not in cluster "+ cluster);
					//Logger.logit("Action "+(action+1)+" is not in cluster "+ cluster+"\n");
					List<Integer>[][] tmpcluster = getEmptyClusters(numberofplayers, numberofclusters);
					makeDeepCopy(clusters,tmpcluster);
					tmpcluster[player][cluster].add(action+1);
					deltasforactions[action][cluster] = calculateDelta(tmpcluster, cluster, player, game);
					System.out.println("Delta with action : "+ deltasforactions[action][cluster]);
					//	Logger.logit("Delta with action : "+ deltasforactions[action][cluster]+"\n");
					deltaswithoutactions[action][cluster] = deltas[player][cluster];
					System.out.println("Delta without action : "+ deltaswithoutactions[action][cluster]);
					//	Logger.logit("Delta without action : "+ deltaswithoutactions[action][cluster]+"\n");
				}

			}
		}
		//}

	}


	/**
	 * calcualates delta if action is added to the cluster
	 * @param delta
	 * @param deltasforclusterswactions
	 * @param deltasforclusterswoactions
	 * @param clusters
	 * @param numberofplayers
	 * @param numberofclusters
	 * @param clustersize
	 * @param game
	 * @param player
	 */
	private static void calculateDeltasIfAddedToClustersV3(int cluster,
			Double[][] deltasforclusterswactions,
			List<Integer> unassignedactions,List<Integer>[][] clusters,
			int numberofplayers, int numberofclusters,
			MatrixGame game, int player) 
	{

		int actionindex = 0;
		for(int action : unassignedactions)
		{
			List<Integer>[][] tmpcluster = getEmptyClusters(numberofplayers, numberofclusters);
			makeDeepCopy(clusters,tmpcluster);
			tmpcluster[player][cluster].add(action);
			deltasforclusterswactions[actionindex][0] = calculateDelta(tmpcluster, cluster, player, game);
			deltasforclusterswactions[actionindex][1] = Math.floor(action);
			actionindex++;
		}

	}



	/**
	 * 
	 * @param delta
	 * @param deltasforclusterswactions
	 * @param deltasforclusterswoactions
	 * @param clusters
	 * @param numberofplayers
	 * @param numberofclusters
	 * @param clustersize
	 * @param game
	 * @param player
	 */
	private static void calculateDeltasIfAddedToClustersV2(double[][] delta,
			double[][] deltasforclusterswactions,
			double[][] deltasforclusterswoactions, List<Integer>[][] clusters,
			int numberofplayers, int numberofclusters, int[] clustersize,
			MatrixGame game, int player) 
	{
		for(int cluster=0; cluster<numberofclusters; cluster++)
		{
			for(int action=0; action<deltasforclusterswactions[cluster].length; action++)
			{
				if(clusters[player][cluster].contains(action+1))
				{
					//System.out.println("Action "+(action+1)+" is in cluster "+ cluster);
					//Logger.logit("Action "+(action+1)+" is in cluster "+ cluster+"\n");
					deltasforclusterswactions[cluster][action] = delta[player][cluster];
					//System.out.println("Delta with action : "+ delta[player][cluster]);
					//Logger.logit("Delta with action : "+ deltas[player][cluster]+"\n");
					/**
					 * need to calculate the delta if the action is not in the cluster
					 * and assign that to deltas[player][cluster]
					 */
					List<Integer>[][] tmpcluster = getEmptyClusters(numberofplayers, numberofclusters);
					makeDeepCopy(clusters,tmpcluster);
					int indexofactiontoremove = tmpcluster[player][cluster].indexOf(action+1);
					tmpcluster[player][cluster].remove(indexofactiontoremove);
					/**
					 * following method returns 0 if the cluster size is 1 or 0
					 */
					deltasforclusterswoactions[cluster][action] = calculateDelta(tmpcluster, cluster, player, game);
					//System.out.println("Delta without action : "+ deltasforclusterswoactions[cluster][action]);
					//Logger.logit("Delta without action : "+ deltaswithoutactions[action][cluster]+"\n");

				}
				else
				{
					//System.out.println("Action "+(action+1)+" is not in cluster "+ cluster);
					//Logger.logit("Action "+(action+1)+" is not in cluster "+ cluster+"\n");
					List<Integer>[][] tmpcluster = getEmptyClusters(numberofplayers, numberofclusters);
					makeDeepCopy(clusters,tmpcluster);
					tmpcluster[player][cluster].add(action+1);
					deltasforclusterswactions[cluster][action] = calculateDelta(tmpcluster, cluster, player, game);
					//System.out.println("Delta with action : "+ deltasforclusterswactions[cluster][action]);
					//	Logger.logit("Delta with action : "+ deltasforactions[action][cluster]+"\n");
					deltasforclusterswoactions[cluster][action] = delta[player][cluster];
					//System.out.println("Delta without action : "+ deltasforclusterswoactions[cluster][action]);
					//	Logger.logit("Delta without action : "+ deltaswithoutactions[action][cluster]+"\n");
				}

			}
		}



	}

	/**
	 * copt from new cluster to cluster
	 * @param srcclusters source
	 * @param destclusters destination
	 */
	private static void deepCopyCuster(List<Integer>[][] srcclusters,
			List<Integer>[][] destclusters) {
		for(int i=0; i<2; i++)
		{


			for(int j=0; j<srcclusters[i].length; j++)
			{
				destclusters[i][j].clear();
				for(int action: srcclusters[i][j])
				{
					destclusters[i][j].add(action);
				}
			}

		}



	}

	private static void printDeltas(double[][] delta) {

		for(int i=0; i<2; i++)
		{
			Logger.logit("\nPlayer "+ i+" : \n");
			for(int j=0; j<delta[i].length; j++)
			{
				Logger.logit("cluster "+ j + " delta : "+ delta[i][j]+ "\n");
			}
		}

	}

	private static double[] findMaxDelta(double[][] delta) {
		// TODO Auto-generated method stub
		double[] maxdelta = new double[2];
		for(int i=0; i<2;i++)
		{
			maxdelta[i] = delta[i][0];
			for(int j=0; j<delta[i].length; j++)
			{
				if(maxdelta[i]<delta[i][j])
				{
					maxdelta[i] = delta[i][j];
				}

			}
		}
		return maxdelta;


	}

	private static void clearCluster(List<Integer>[][] clusters, int player) {

		for(int j=0; j<clusters[player].length; j++)
		{
			clusters[player][j].clear();
		}


	}


	/**
	 * stores the change of delta in deltawithindex
	 * @param deltawithindex 
	 * @param withaction delta for clusters
	 * @param withoutaction deltas for player
	 */
	private static void fillDeltawithClusterIndex(
			Double[][] deltawithindex, double[] withaction, double[] withoutaction) {

		for(int i=0; i<withaction.length; i++)
		{

			deltawithindex[i][0] = withaction[i]-withoutaction[i];
			deltawithindex[i][1] = Math.floor(i);
			Logger.logit(" \nFrom  "+ deltawithindex[i][1] + " , distance :"+ deltawithindex[i][0] + "\n");

		}

	}



	private static void fillDeltawithAction(
			Double[][] deltawithindex, double[] withaction, double[] withoutaction,
			ArrayList<Integer> alreadyassigned) {

		for(int i=0, j=0; i<withaction.length; i++)
		{
			if(!alreadyassigned.contains(i+1))
			{
				deltawithindex[j][0] = withaction[i]-withoutaction[i];
				deltawithindex[j][1] = Math.floor(i+1);
				Logger.logit(" \nFrom  "+ deltawithindex[j][1] + " , distance :"+ deltawithindex[j][0] + "\n");
				j++;
			}

		}

	}

	/**
	 * 
	 * @param ds
	 * @param rank which cluster to send
	 * @return
	 */
	private static Integer findClusterWithRankedDelta(Double[][] ds, int rank) {

		//sortArray(ds);
		return  ds[rank][1].intValue();
	}

	/**
	 * return action with rank
	 * Choose an action randomly if there are more than one action with same delta
	 * @param ds
	 * @param rank
	 * @return
	 */
	private static Integer findActionWithRankedDelta(Double[][] ds, int rank) {

		//sortArray(ds);
		/**
		 * first detect if there are more than one action with the delta of chosen action
		 */
		double chosendelta =  ds[rank][0];
		int counter = 0;
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		boolean started = false;
		for(int i=0; i<ds.length; i++)
		{
			if(chosendelta==ds[i][0])
			{
				indexes.add(i);
				started = true;
			}
			else if(started)
			{
				break;
			}

		}
		/**
		 * choose an action randomly
		 */
		int index = randInt(0, indexes.size()-1);



		return  ds[indexes.get(index)][1].intValue();
	}

	public static int randInt(int min, int max) {

		// Usually this should be a field rather than a method variable so
		// that it is not re-seeded every call.
		//Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = randint.nextInt((max - min) + 1) + min;

		return randomNum;
	}


	private static void sortArray(Double[][] ds) {

		Double[] swap = {0.0,0.0};

		for (int i = 0; i < ds.length; i++) 
		{
			for (int d = 1; d < ds.length-i; d++) 
			{
				if (ds[d-1][0] > ds[d][0]) /* For descending order use < */
				{
					swap = ds[d];
					ds[d]  = ds[d-1];
					ds[d-1] = swap;
				}
			}
		}

	}



	private static void makeDeepCopy(List<Integer>[][] source,
			List<Integer>[][] destination) 
	{
		for(int i=0; i< 2; i++)
		{

			for(int j =0; j< source[i].length; j++)
			{
				for(int k =0; k<source[i][j].size(); k++)
				{
					int x = source[i][j].get(k);
					destination[i][j].add(x); 
				}
			}
		}

	}

	/**
	 * returns the cluster which are not full
	 * @param clusters
	 * @param clustersize max size of a cluster
	 * @return
	 */
	private static List<Integer>[] giveNonFullClusters(List<Integer>[][] clusters, int[] clustersize) 
	{
		List<Integer>[] nonfullclusters = new List[2];

		for(int player = 0; player<2; player++)
		{
			nonfullclusters[player] = new ArrayList<Integer>();
			for(int cluster=0; cluster< clusters[player].length; cluster++)
			{
				if(clusters[player][cluster].size()<clustersize[player])
				{
					nonfullclusters[player].add(cluster);
				}
			}
		}
		return nonfullclusters;

	}

	private static List<Integer>[][] createDummyPartition() 
	{
		List<Integer>[][] dummypartition = new List[2][];
		for(int i=0; i< dummypartition.length; i++)
		{
			dummypartition[i] = new List[3];
		}
		for(int i=0; i< 2; i++)
		{

			for(int j =0; j< 3; j++)
			{
				dummypartition[i][j] = new ArrayList<Integer>(); 
			}
		}
		//createRandomPartition(numberofcluster, numberofaction, dummypartition);
		/*
		 * create a predefined partition
		 */

		for(int i=0; i<2; i++)
		{

			if(i==0)
			{
				dummypartition[i][0].add(5);
				dummypartition[i][0].add(3);
				dummypartition[i][1].add(4);
				dummypartition[i][1].add(2);
				dummypartition[i][2].add(1);
				dummypartition[i][2].add(6);



			}
			else
			{
				dummypartition[i][0].add(2);
				dummypartition[i][0].add(4);
				dummypartition[i][1].add(5);
				dummypartition[i][1].add(3);
				dummypartition[i][2].add(6);
				dummypartition[i][2].add(1);
			}

		}
		return dummypartition;

	}

	private static void calculateDeltas(double[][] delta, List<Integer>[][] clusters, int numberofplayers,
			int numberofclusters, MatrixGame game) {
		for(int player=0; player<numberofplayers; player++)
		{
			for(int cluster=0; cluster<numberofclusters; cluster++)
			{
				delta[player][cluster] = calculateDelta(clusters, cluster,player,game);
			}
		}

	}

	/**
	 * calculates delta for a particular cluster for a player
	 * @param clusters the cluster object
	 * @param cluster cluster to find delta for
	 * @param player player
	 * @return
	 */
	private static double calculateDelta(List<Integer>[][] clusters,
			int cluster, int player, MatrixGame game) {
		if((clusters[player][cluster].size()==1) || (clusters[player][cluster].size()==0))
		{
			return 0;
		}

		double delta = Double.NEGATIVE_INFINITY;
		int opponent = 1^player;
		int numactionsopponent =  game.getNumActions(opponent);
		for(int opponentaction = 0; opponentaction<numactionsopponent; opponentaction++)
		{
			if(!clusters[opponent][cluster].contains(opponentaction+1))
			{
				double minpayoff = Double.POSITIVE_INFINITY;
				double maxpayoff = Double.NEGATIVE_INFINITY;
				for(int playeraction1: clusters[player][cluster])
				{
					int[] outcome1 = new int[2];
					if(player==0)
					{
						outcome1[0] = playeraction1;
						outcome1[1] = opponentaction+1;
					}
					if(player==1)
					{	
						outcome1[0] = opponentaction+1;
						outcome1[1] = playeraction1;
					}
					double payoff = game.getPayoff(outcome1, player);
					if(payoff>maxpayoff)
					{
						maxpayoff = payoff;
					}
					if(payoff<minpayoff)
					{
						minpayoff = payoff;
					}
				}
				double tmpdelta = maxpayoff-minpayoff;
				if(tmpdelta>delta)
				{
					delta = tmpdelta;
				}
			}
		}
		return delta;

	}

	/**
	 * calculates centroid of every cluster
	 * @param centroids centroids
	 * @param clusters clusters
	 * @param game matrixgame
	 * @throws Exception 
	 */
	private static void calculateCentroids(double[][][] centroids, List<Integer>[][] clusters,
			int[] numaction, MatrixGame game) throws Exception 
	{
		/**
		 * iterate over the players, then clusters, then for each actions of opponent that does not belong to
		 * his cluster, calculate average for actions of the player
		 */
		for(int player=0; player<centroids.length; player++)
		{
			int opponent = 1^player;
			for(int cluster = 0; cluster<clusters[player].length; cluster++)
			{
				if(clusters[player][cluster].size()==0)
				{
					throw new Exception("Cluster size 0 in centroid calculation");
				}
				/**
				 * we need an index to iterate all the dimensions in a centroid
				 */
				int centroidarrayindex = 0;
				for(int opponentaction=0; opponentaction<numaction[opponent]; opponentaction++)
				{
					/**
					 * calculate the average when the actions are not in the cluster for opponent
					 */
					if(!clusters[opponent][cluster].contains(opponentaction+1))
					{
						double average = 0;
						/**
						 * now iterate over all the action in the cluster for player
						 */
						for(Integer action: clusters[player][cluster])
						{
							int[] outcome = new int[2];
							if(player==0)
							{
								outcome[0] = action;
								outcome[1] = opponentaction+1;
							}
							else if(player==1)
							{
								outcome[0] = opponentaction+1;
								outcome[1] = action;

							}
							double payoff = game.getPayoff(outcome, player);
							average += payoff;
						}
						/**
						 * assign the calculated centroid value for one action of oppoennt
						 */
						double centroidvalforanaction = average/clusters[player][cluster].size();
						centroids[player][cluster][centroidarrayindex++] = centroidvalforanaction;

					}
				}



			}

		}

	}

	private static void createEmptyCentroids(double[][][] centroids, int[] numactions, int[] clustersize) {

		for(int player =0; player<centroids.length; player++)
		{
			int opponent = 1^player;
			for(int cluster=0; cluster<centroids[player].length; cluster++)
			{
				centroids[player][cluster] = new double[numactions[opponent]-clustersize[opponent]];
			}

		}

	}

	private static void printClusters(List<Integer>[][] clusters, int player) {

		//int player = 0;
		//for(List<Integer>[] x: clusters )
		//{
		System.out.println("Player : " + player);
		Logger.logit("\nPlayer : " + player+"\n");

		int cluster = 0;
		for(List<Integer> y: clusters[player])
		{
			System.out.print("Cluster "+cluster + " : ");
			Logger.logit("Cluster "+cluster + " : ");
			for(Integer z: y)
			{
				System.out.print(z+ " ");
				Logger.logit(z+ " ");

			}
			System.out.println();
			Logger.logit("\n");
			cluster++;
		}
		player++;

		//}

	}

	/**
	 * initialize the clusters according to the type
	 * @param clusters cluster object for both players
	 * @param clustersize number of actions in each cluster
	 * @param initializationtype type of initialization
	 */
	private static void initilizeClusters(List<Integer>[][] clusters,
			int[] clustersize, int[] numberofactions, int initializationtype) 
	{
		switch(initializationtype)
		{
		case RAND_ACTION_IN_CLUSTER:
			initializeWithRandActions(clusters, clustersize, numberofactions);
			break;
		case ACTION_IN_CLUSTER:
			initActions(clusters);
			break;

		}

	}

	private static void initActions(List<Integer>[][] clusters) {

		for(int i=0; i<2; i++)
		{

			if(i==0)
			{
				clusters[i][0].add(5);
				clusters[i][0].add(3);
				clusters[i][0].add(7);
				clusters[i][1].add(4);
				clusters[i][1].add(2);
				clusters[i][1].add(9);
				clusters[i][2].add(1);
				clusters[i][2].add(6);
				clusters[i][2].add(8);



			}
			else
			{
				clusters[i][0].add(2);
				clusters[i][0].add(4);
				clusters[i][0].add(9);
				clusters[i][1].add(5);
				clusters[i][1].add(3);
				clusters[i][1].add(8);
				clusters[i][2].add(6);
				clusters[i][2].add(1);
				clusters[i][2].add(7);
			}

		}


	}

	/**
	 * initialize the clusters with random actions
	 * @param clusters cluster object for both players
	 * @param clustersize number of actions in each cluster
	 * 
	 */
	private static void initializeWithRandActions(List<Integer>[][] clusters,
			int[] clustersize, int[] numberofactions) {
		//long seed = System.nanoTime();
		for(int player = 0; player<clusters.length; player++)
		{
			/**
			 * create an arraylist containing all the actions
			 */
			ArrayList<Integer> actionstobeassigned = new ArrayList<Integer>();
			for(int i=1; i<= numberofactions[player]; i++)
			{
				actionstobeassigned.add(i);
			}
			/**
			 * shuffle the array list containing the actions
			 */

			Collections.shuffle(actionstobeassigned, new Random());
			int action = 0; // to iterate through the actionstobeassigned list
			/**
			 * assign actions from the actionstobeassigned array list to each cluster serially
			 */
			for(int cluster = 0; cluster<clusters[player].length; cluster++)
			{
				clusters[player][cluster].clear();

				for(int index=0; index<clustersize[player]; index++)
				{
					clusters[player][cluster].add(actionstobeassigned.get(action++));

				}
				/**
				 * check whether cluster size meets size criteria
				 */
				if(clusters[player][cluster].size() != clustersize[player])
				{
					try 
					{
						throw new Exception("CLuster size not maintained in initialization");
					} 
					catch (Exception e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}



	}

	/**
	 * creates an empty cluster set for both players
	 * @param numberofplayers
	 * @param numberofclusters
	 * @return
	 */
	private static List<Integer>[][] getEmptyClusters(int numberofplayers, int numberofclusters) 
	{
		List<Integer>[][] clusters	=	new List[numberofplayers][];
		for(int i=0; i< clusters.length; i++)
		{
			clusters[i] = new List[numberofclusters];
		}
		for(int i=0; i< 2; i++)
		{
			for(int j =0; j< numberofclusters; j++)
			{
				clusters[i][j] = new ArrayList<Integer>(); 
			}
		}
		return clusters;

	}

}

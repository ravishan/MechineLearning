//$Id$
package Examples.TicTacToe;

import java.util.Random;




public class TicTacToe {
	private final int GRID_SIZE = 3;
	private char[][] grid = new char[GRID_SIZE][GRID_SIZE];
	
	Player xplayer;
	Player oplayer;
	char currentPlayer;
	char winner;
	
	boolean isGameFinished; 
	protected Random rand = new Random();
	
	public TicTacToe(Player xplayer,Player oplayer){
		this.xplayer = xplayer;
		this.oplayer = oplayer;
		this.currentPlayer = 'x';
	}
	
	protected void initializePlay(){
		for(int i=0;i<GRID_SIZE;i++){
			for(int j=0;j<GRID_SIZE;j++){
				grid[i][j]=' ';
			}
		}
		isGameFinished = false;
	}
	
	public void playGame(){
		initializePlay();
		char player = rand.nextInt(2)!=0?'X':'O';
		while(!nextMove(player)){
			player = player=='X'?'O':'X';
		}
		//System.out.println("The winner is ----->"+getWinner());
	}
	
	public Player getcurrentPlayer(char whichPlayer){
		if ( (whichPlayer != 'X') && (whichPlayer != 'O') ) {
			whichPlayer = Character.toUpperCase(whichPlayer);
		}
		Player player = (whichPlayer == 'X') ? this.xplayer : this.oplayer;
		return player;
	}
	
	public boolean nextMove(char currentPlayer){
		if(isGameFinished) return true;
		Player player = getcurrentPlayer(currentPlayer);
		int currentMoveIndex = player.chooseNextMove(grid,currentPlayer);
		int row = currentMoveIndex/3;
		int column = currentMoveIndex-(row*3);
		saveMove(row, column, currentPlayer);
		grid[row][column] = currentPlayer;
		Player.printGrid(grid);
		if ( ((winner = getWinnerForMove(grid,row,column)) != ' ')
				|| (player.getFreeCount() == 1) ) {
			isGameFinished = true;
			return true;
		}
		
		if (!anyPossibleWins(grid)) {
				isGameFinished = true;
				return true;
		}
		return false;
	}
	
	public char getWinner(){
		return winner;
	}
	
	private char getWinnerForMove(char[][] grid,int row,int column){
		if((grid[row][0]==grid[row][1])&&(grid[row][1]==grid[row][2])){
			return grid[row][0];
		}else if((grid[0][column]==grid[1][column])&&(grid[1][column]==grid[2][column])){
			return grid[0][column];
		}else if((row==column)||(row==(2-column))){
			if((grid[0][0]==grid[1][1]) && (grid[1][1]==grid[2][2])){
				return grid[0][0];
			}
			if((grid[0][2]==grid[1][1])&&(grid[1][1]==grid[2][0])){
				return grid[0][2];
			}
		}
		return ' ';
	}
	
	public static boolean anyPossibleWins(char[][] grid) {
		int xc, oc, sc;
		for (int r = 0; r < 3; r++) {
			xc = oc = sc = 0;
			for (int c = 0; c < 3; c++) {
				switch (grid[r][c]) {
				case 'X': xc++; break;
				case 'O': oc++; break;
				default: sc++; break;
				}
			}
			if (sc >= 2) return true;
			if ( (sc == 1) && ( (xc == 2) || (oc == 2) ) ) return true;
			if ( (xc == 3) || (oc == 3) ) return true;
		}
		for (int c = 0; c < 3; c++) {
			xc = oc = sc = 0;
			for (int r = 0; r < 3; r++) {
				switch (grid[r][c]) {
				case 'X': xc++; break;
				case 'O': oc++; break;
				default: sc++; break;
				}
			}
			if (sc >= 2) return true;
			if ( (sc == 1) && ( (xc == 2) || (oc == 2) ) ) return true;
			if ( (xc == 3) || (oc == 3) ) return true;
		}
		xc = oc = sc = 0;
		for (int rc = 0; rc < 3; rc++) {
			switch (grid[rc][rc]) {
			case 'X': xc++; break;
			case 'O': oc++; break;
			default: sc++; break;
			}
		}
		if (sc >= 2) return true;
		if ( (sc == 1) && ( (xc == 2) || (oc == 2) ) ) return true;
		if ( (xc == 3) || (oc == 3) ) return true;
		xc = oc = sc = 0;
		for (int rc = 0; rc < 3; rc++) {
			switch (grid[rc][2-rc]) {
			case 'X': xc++; break;
			case 'O': oc++; break;
			default: sc++; break;
			}
		}
		if (sc >= 2) return true;
		if ( (sc == 1) && ( (xc == 2) || (oc == 2) ) ) return true;
		if ( (xc == 3) || (oc == 3) ) return true;
		return false;
	}
	
	private void saveMove(int row,int column,char currentPlayer){
		
	}
	
	public static void main(String args[]){
		MechinePlayer neuralPlayer = new MechinePlayer(18, 36, 9);
		// Do training.
		System.out.println("TRAINING...");

		for (int pass = 1; ; pass++) {
			neuralPlayer.trainTicTacToe();
			int npass = neuralPlayer.numPassedMoves;
			int nfail = neuralPlayer.numFailedMoves;
			System.out.println("train pass="+pass+" npass="+npass+" nfail="+nfail);
			if (nfail == 0) break;
		}

		int numTestPasses = 1000;
		for (int whichOpponent = 1; whichOpponent <= 2; whichOpponent++) {
			Player opponent = (whichOpponent == 1) ? new IdealPlayer() : new IdealPlayer();
			System.out.println("Competing against "+opponent.getClass().getName()+"...");
			TicTacToe tic = new TicTacToe(neuralPlayer, opponent);
			int xWinCount = 0, oWinCount = 0, nWinCount = 0;
			for (int pass = 0; pass < numTestPasses; pass++) {
				System.out.println("THe Game ONE -------->"+pass);
				tic.playGame();
				switch (tic.getWinner()) {
				case 'X':
					xWinCount++;
					break;
				case 'O':
					oWinCount++;
					break;
				default:
					nWinCount++;
					break;
				}
			}
			// Output test result counts.
			System.out.println("X won: "+xWinCount);
			System.out.println("O won: "+oWinCount);
			System.out.println("Nobody won: "+nWinCount);
		
		}
		
		
		
		
//		TicTacToe tic = new TicTacToe(new RandomPlayer(), new RandomPlayer());
//		tic.playGame();
//		System.out.println("The game winner ----->"+tic.winner);
	}
}

/** Back propagation with 2 hidden layer****/	

/*		neuralPlayer.trainTicTacToe();
	 training_loop:
	        for (int i = 0; i < 100000000; i++) {
	            if (i == 5000 || i == 8000 || i == 10000 | i == 12000 | i==20000 | i==25000 | i==27000|i==30000 |i==35000 | i==40000 | i==45000|i==50000| i==55000 |i==67000 |i==70000|i==72000|i==77000)  neuralPlayer.neural.LEARNING_RATE *= 0.75f;
	            float error = neuralPlayer.neural.train();
	            if (i > 0 && i % 500 == 0) {
	                // If the error is too large, slightly randomize weights:
	                if (error > 4)  {
	                	neuralPlayer.neural.randomizeWeights();
	             //      nn.neural.LEARNING_RATE = 0.75f;
	                } else if (error > 1)  {
	                	neuralPlayer.neural.slightlyRandomizeWeights();
	                  //  nn.neural.LEARNING_RATE = 0.75f;
	                } 
	                System.out.println("cycle " + i + " error is " + error);
	            }
	            if (error < 0.2){
	            	System.out.println("ERROR "+error);
	            	break training_loop;
	            }
	        } */
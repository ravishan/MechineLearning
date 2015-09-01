//$Id$
package Examples.TicTacToe;

import NeuralNetworks.BackPropagationNeural_1H;

public class MechinePlayer extends Player{
	public BackPropagationNeural_1H neural ;
	private char[][][] allBoards = null;
	public int[] allBoardsXNextMove = null;
	public int[] allBoardsONextMove = null;
	
	private double[] netExpectedOuts = new double[9];
	private char[][] tmpGrid = new char[3][3];
	private int[] tmpFreeRow = new int[9], tmpFreeCol = new int[9];
	private double[] input =null;
	private double[] output =null;
	
	private static float[] sample = {0.00f,0.00f,0.00f,0.00f,1.00f,0.00f,1.00f,0.00f,0.00f,0.00f,1.00f,0.00f,0.00f,0.00f,0.00f,0.00f,1.00f,0.00f};
	private static char[] charSample = new char[18];
	
	
	public MechinePlayer(int inputsize,int hiddensize,int outputsize){
		neural =  new BackPropagationNeural_1H(inputsize,hiddensize,outputsize);
		input = neural.neuronValue[0];
		output = neural.neuronValue[2];
	}

	@Override
	public int chooseNextMove(char[][] grid, char whichPlayer) {
		loadAllMoves();

		findFreeCells(grid);

		for (int r = 0; r < 3; r++) System.arraycopy(grid[r], 0, tmpGrid[r], 0, 3);
		System.arraycopy(freeRow, 0, tmpFreeRow, 0, freeCount);
		System.arraycopy(freeColumn, 0, tmpFreeCol, 0, freeCount);

		char opponent = (whichPlayer == 'X') ? 'O' : 'X';

		for (int rotPass = 0; rotPass < 4; rotPass++) {
			for (int flipPass = 0; flipPass < 2; flipPass++) {
				boolean found = false;
				char[][] ab;
				for (int i = 0; i < allBoards.length; i++) {
					ab = allBoards[i];
					if ((tmpGrid[0][0] == ab[0][0]) &&
						(tmpGrid[0][1] == ab[0][1]) &&
						(tmpGrid[0][2] == ab[0][2]) &&
						(tmpGrid[1][0] == ab[1][0]) &&
						(tmpGrid[1][1] == ab[1][1]) &&
						(tmpGrid[1][2] == ab[1][2]) &&
						(tmpGrid[2][0] == ab[2][0]) &&
						(tmpGrid[2][1] == ab[2][1]) &&
						(tmpGrid[2][2] == ab[2][2])) {
						found = true;
						break;
					}
				}
				float[] netIns = new float[18];
				if (found) {
					for (int r = 0, idx = 0; r < 3; r++) {
						for (int c = 0; c < 3; c++, idx++) {
							char ch = tmpGrid[r][c];
							input[idx] = (ch == whichPlayer) ? 1.0 : 0.0;
							input[idx+9] = (ch == opponent) ? 1.0 : 0.0;
						}
					}
					neural.forwardPass();
//					 float[] results= neural.recall(netIns);
					
					double maxOut = 0.0;
					int nextMove = -1;
					for (int freeIdx = 0; freeIdx < freeCount; freeIdx++) {
						int pos = (tmpFreeRow[freeIdx]*3)+tmpFreeCol[freeIdx];
						double out = output[pos];
//						System.out.println("position"+pos+" "+out);
						if (out > maxOut) {
							maxOut = out;
							nextMove = pos;
						}
					}
//					System.out.println("The move "+nextMove+" value="+maxOut);
					if (nextMove >= 0) {
						if (flipPass > 0) {
							nextMove = AllBoards.unflipHMove(nextMove);
						}
						for (int rp = 0; rp < rotPass; rp++) {
							nextMove = AllBoards.unrotate90CCWMove(nextMove);
						}
					} else {
						int freeIdx = rand.nextInt(freeCount);
						nextMove = (freeRow[freeIdx]*3)+freeColumn[freeIdx];
					}
					return nextMove;
				}	

				AllBoards.flipHGrid(tmpGrid);
				AllBoards.flipHColInts(tmpFreeCol, freeCount);
			}

			AllBoards.rotate90CCWGrid(tmpGrid);
			AllBoards.rotate90CCWRowColInts(tmpFreeRow, tmpFreeCol, freeCount);
		}

		throw new InternalError("Unexpected grid");
	}
	
	public void loadAllMoves(){
		if (allBoards == null) {
			allBoards = AllBoards.getAllBoards();
			allBoardsXNextMove = new int[allBoards.length];
			allBoardsONextMove = new int[allBoards.length];
			Player idealPlayer = new IdealPlayer();
			idealPlayer.setRandomizeMoves(false);
			for (int i = 0; i < allBoards.length; i++) {
				char[][] grid = allBoards[i];
				int xMoves = 0, oMoves = 0;
				for (int r = 0; r < 3; r++) {
					for (int c = 0; c < 3; c++) {
						switch (grid[r][c]) {
						case 'X': xMoves++; break;
						case 'O': oMoves++; break;
						}
					}
				}
				if (oMoves > xMoves) {
					allBoardsXNextMove[i] = idealPlayer.chooseNextMove(grid, 'X');
					allBoardsONextMove[i] = -1;
				} else if (xMoves > oMoves) {
					allBoardsXNextMove[i] = -1;
					allBoardsONextMove[i] = idealPlayer.chooseNextMove(grid, 'O');
				} else {
					allBoardsXNextMove[i] = idealPlayer.chooseNextMove(grid, 'X');
					allBoardsONextMove[i] = idealPlayer.chooseNextMove(grid, 'O');
				}
			}
		}	
	}
	
	public  void trainTicTacToe() {
		loadAllMoves();
		neural.learningRate =  new double[] { 0.002 };
		numPassedMoves = numFailedMoves = 0;
		for (int i = 0; i < allBoards.length; i++) {
			char[][] grid = allBoards[i];
			int xMove = allBoardsXNextMove[i], oMove = allBoardsONextMove[i];
			if (xMove >= 0) trainMove('X', grid, xMove);
			if (oMove >= 0) trainMove('O', grid, oMove);
		}
	}
	
	public void trainMove(char currentPlayer,char[][] grid,int move){	
		char opponent = (currentPlayer == 'X') ? 'O' : 'X';
		for (int r = 0, idx = 0; r < 3; r++) {
			for (int c = 0; c < 3; c++, idx++) {
				char ch = grid[r][c];
				input[idx] = (ch == currentPlayer) ? 1.0 : 0.0;
				input[idx+9] = (ch == opponent) ? 1.0 : 0.0;
				netExpectedOuts[idx] = 0.0;
			}
		}
		netExpectedOuts[move] = 1.0;
		neural.forwardPass();
		double[] outs = neural.neuronValue[2];
		double max = 0.0;
		int maxIdx = -1;
		for (int i = 0; i < 9; i++) {
			if (outs[i] > max) {
				max = outs[i];
				maxIdx = i;
			}
		}
		if (maxIdx == move) {
			numPassedMoves++;
		} else {
			numFailedMoves++;
			neural.backPropagate(netExpectedOuts);
		}
	}
	
	//?***********TESTING**********************
/*	public static void main(String args[]){
		MechinePlayer nn = new MechinePlayer(18,18,9);

		nn.trainTicTacToe();
		 training_loop:
		        for (int i = 0; i < 100000000; i++) {
		            if (i == 5000 || i == 8000 || i == 10000 | i == 12000 | i==20000 | i==25000 | i==27000|i==30000 |i==35000 | i==40000 | i==45000|i==50000| i==55000 |i==67000 |i==70000|i==72000|i==77000)  nn.neural.LEARNING_RATE *= 0.75f;
		            float error = nn.neural.train();
		            if (i > 0 && i % 500 == 0) {
		                // If the error is too large, slightly randomize weights:
		                if (error > 4)  {
		                    nn.neural.randomizeWeights();
		             //      nn.neural.LEARNING_RATE = 0.75f;
		                } else if (error > 1)  {
		                    nn.neural.slightlyRandomizeWeights();
		                  //  nn.neural.LEARNING_RATE = 0.75f;
		                } 
		                System.out.println("cycle " + i + " error is " + error);
		            }
		            if (error < 0.2){
		            	System.out.println("ERROR "+error);
		            	break training_loop;
		            }
		        } 
        
		 	 	System.out.println("The data ---->"+nn.allBoardsXNextMove[nn.allBoardsXNextMove.length-1]);
		 	 	System.out.println("The data ---->"+nn.allBoardsONextMove[nn.allBoardsONextMove.length-1]);
		        test_recall(nn,sample);
	}
	
	 public static void test_recall(MechinePlayer nn, float[] inputs) {
	        float[] results = nn.neural.recall(inputs);
	        System.out.print("Test case: ");
	        for (int i = 0; i < inputs.length; i++) System.out.print(pp(inputs[i]) + " ");
	        System.out.print(" results: ");
	        for (int i = 0; i < results.length; i++) System.out.print(pp((results[i])) + " ");
	        System.out.println();
	    }

	    public static String pp(float x) {
	        String s = new String("" + x + "00");
	        int index = s.indexOf(".");
	        if (index > -1) s = s.substring(0, index + 3);
	        if (s.startsWith("-") == false) s = " " + s;
	        return s;
	    }*/
	
	
	
}

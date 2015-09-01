//$Id$
package Examples.TicTacToe;

import java.util.Random;

public abstract class Player {
	protected char[][] grid ;
	protected int movesSoFar;int freeCount;int[] freeRow = new int[9];int[] freeColumn = new int[9];
	private int[] tempFreeRow = new int[9],tempFreeColumn = new int[9];
	protected Random rand = new Random();
	protected boolean randomizeMoves = false;
	public void setRandomizeMoves(boolean randomizeMoves) { this.randomizeMoves = randomizeMoves; }
	public int numPassedMoves, numFailedMoves;
	
	public void findFreeCells(char[][] grid){
		this.grid = grid;
		movesSoFar=0;
		freeCount=0;
		for(int r=0;r<3;r++){
			for(int c=0;c<3;c++){
				if(grid[r][c]==' '){
					tempFreeRow[freeCount] = r;
					tempFreeColumn[freeCount] =c;
					freeCount++;
				}
				else{
					movesSoFar++;
				}
			}
		}
		
		for(int index=0;index<freeCount;index++){
			freeRow[index] = tempFreeRow[index];
			freeColumn[index] = tempFreeColumn[index];
		}
		
	}
	
	public abstract int chooseNextMove(char[][] grid, char whichPlayer);
	
	public int isFree(int row,int column){
		for(int i=0;i<9;i++){
			if(freeRow[i]==row && freeColumn[i]==column){
				return i;
			}
		}
		return -1;
	}
	
	public static void printGrid(char[][] grid){
		System.out.println("-------");
		for (int r = 0; r < 3; r++) {
			System.out.print('|');
			System.out.print(grid[r][0]);
			System.out.print('|');
			System.out.print(grid[r][1]);
			System.out.print('|');
			System.out.print(grid[r][2]);
			System.out.println('|');
			if (r < 2) System.out.println("|-----|");
		}
		System.out.println("-------");
	}
	
	public int getFreeCount(){
		return freeCount;
	}
	
	protected int freeCellIdxToRow3Col(int freeCellIdx) {
		return (freeRow[freeCellIdx]*3)+freeColumn[freeCellIdx];
	}
}

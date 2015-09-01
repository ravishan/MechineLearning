//$Id$
package Examples.TicTacToe;

public class RandomPlayer extends Player{
	@Override
	public int chooseNextMove(char[][] grid, char whichPlayer) {
		findFreeCells(grid);
		int nextMoveIndex = rand.nextInt(freeCount);
		return (freeRow[nextMoveIndex]*3)+freeColumn[nextMoveIndex];
	}
}

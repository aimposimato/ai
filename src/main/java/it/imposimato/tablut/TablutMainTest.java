package it.imposimato.tablut;

import java.util.Date;

import aima.core.search.adversarial.AdversarialSearch;
import aima.core.search.adversarial.AlphaBetaSearch;

public class TablutMainTest {

	public static void main(String[] args) {
	
		System.out.println(Character.toLowerCase('A') - 97);
		

		try {
			System.out.println("(Graph Search):");

			int levels = 5;
			long t1 = new Date().getTime();
			long t2;

			System.out.println("ALPHA BETA DEMO\n");

			TablutGame gameW = new TablutGame(TablutState.PLAYER_WHITE);
			TablutGame gameB = new TablutGame(TablutState.PLAYER_WHITE);
			TablutState currState = gameW.getInitialState();
			currState.setLevel(levels);
			AdversarialSearch<TablutState, TablutAction> search = AlphaBetaSearch.createFor(gameW);
			System.out.println(currState);
			TablutGame game = gameW;
			while (!(gameW.isTerminal(currState))) {
				System.out.println(gameW.getPlayer(currState) + "  playing ... ");
				TablutAction action = search.makeDecision(currState);
				currState = gameW.getResult(currState, action);
				currState.setLevel(levels);
				t2 = new Date().getTime();
				System.out.println("time: " + (t2 - t1) + " ms");
				t1 = t2;
				System.out.println(action);
				System.out.println(currState);

				if (game == gameW) {
					game = gameB;
				} else {
					game = gameW;
				}

			}
			System.out.println("ALPHA BETA DEMO done");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

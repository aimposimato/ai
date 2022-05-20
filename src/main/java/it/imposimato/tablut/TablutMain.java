package it.imposimato.tablut;

import java.io.IOException;
import java.util.Date;

import aima.core.search.adversarial.AdversarialSearch;
import aima.core.search.adversarial.AlphaBetaSearch;
import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.State.Pawn;
import it.unibo.ai.didattica.competition.tablut.domain.State.Turn;

public class TablutMain {

	static Turn turn;
	static String player;

	public static void main(String[] args) {

		if (args.length != 5) {
			System.out.println("missing arguments: name colour host port");
			return;
		}
		String name = args[0];
		TablutGame game;
		String colour = args[1].toUpperCase();

		if (colour.equals("WHITE")) {
			game = new TablutGame(TablutState.PLAYER_WHITE);
			turn = Turn.WHITE;
			player = TablutState.PLAYER_WHITE;
		} else if (colour.equals("BLACK")) {
			game = new TablutGame(TablutState.PLAYER_BLACK);
			turn = Turn.BLACK;
			player = TablutState.PLAYER_BLACK;
		} else {
			System.out.println("colour not supported");
			return;
		}
		String host = args[3];

		int port;
		try {
			port = Integer.parseInt(args[4]);
		} catch (NumberFormatException nfe) {
			System.out.println("port not supported");
			return;
		}

		try {

			TablutClient tablutClient = new TablutClient(colour, name, 100, host, port);

			long t1 = new Date().getTime();
			long t2;

			tablutClient.declareName();

			AdversarialSearch<TablutState, TablutAction> search = AlphaBetaSearch.createFor(game);

			TablutState currState;
			do {
				System.out.println("waiting for server");
				tablutClient.read();
						
				currState = getTablutState(tablutClient.getCurrentState());
				if (tablutClient.getCurrentState().getTurn() != turn) {
					continue;
				}
				System.out.println("thinking..");

				System.out.println(currState);

				TablutAction action = search.makeDecision(currState);
				t2 = new Date().getTime();

				tablutClient.write(getClientAction(action));

				System.out.println("time: " + (t2 - t1) + " ms");
				t1 = t2;
				System.out.println(action);
				currState = game.getResult(currState, action);
	//			System.out.println(currState);

			} while (!(game.isTerminal(currState)));
			System.out.println("finish");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Action getClientAction(TablutAction tablutAction) throws IOException {
		return new Action("" + "abcdefghi".charAt(tablutAction.getX1()) + (tablutAction.getY1() + 1),
				"" + "abcdefghi".charAt(tablutAction.getX2()) + (tablutAction.getY2() + 1), turn);
	}

	private static TablutState getTablutState(State currentState) {
		int[][] schema = new int[TablutState.MAX_X][TablutState.MAX_X];
		for (int y = 0; y < TablutState.MAX_Y; y++) {
			for (int x = 0; x < TablutState.MAX_X; x++) {
				schema[x][y] = getSchemaInt(currentState.getPawn(y, x));
			}
		}

		return new TablutState(schema, player, 6);
	}

	private static int getSchemaInt(State.Pawn pawn) {
		if (pawn == Pawn.BLACK) {
			return TablutState.SCHEMA_BLACK;
		}
		if (pawn == Pawn.WHITE) {
			return TablutState.SCHEMA_WHITE;
		}
		if (pawn == Pawn.KING) {
			return TablutState.SCHEMA_KING;
		}

		return TablutState.SCHEMA_EMPTY;

	}

}

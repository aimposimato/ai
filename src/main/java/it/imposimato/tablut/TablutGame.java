package it.imposimato.tablut;

import java.util.ArrayList;
import java.util.List;

import aima.core.search.adversarial.Game;

public class TablutGame implements Game<TablutState, TablutAction, String> {

	private String player;

	public TablutGame(String player) {
		this.player = player;
	}

	@Override
	public String[] getPlayers() {
		return new String[] { TablutState.PLAYER_BLACK, TablutState.PLAYER_WHITE };
	}

	@Override
	public String getPlayer(TablutState state) {
		return state.getPlayer();
	}

	@Override
	public List<TablutAction> getActions(TablutState state) {
		List<TablutAction> result = new ArrayList();
		for (int y = 0; y < TablutState.MAX_Y; y++) {
			for (int x = 0; x < TablutState.MAX_X; x++) {
				int pawn = state.getSchema()[x][y];
				// if (tablutState.getLevel() <2)
				if (((state.getPlayer().equals(TablutState.PLAYER_BLACK)) && (pawn == TablutState.SCHEMA_BLACK))
						|| ((state.getPlayer().equals(TablutState.PLAYER_WHITE))
								&& ((pawn == TablutState.SCHEMA_WHITE) || (pawn == TablutState.SCHEMA_KING)))) {
					// EST Exploration
					for (int i = x + 1; i < TablutState.MAX_X; i++) {
						if (state.isFree(i, y)) {
							TablutAction action = new TablutAction(x, y, i, y);
							result.add(action);
						} else {
							break;
						}
					}

					// WEST Exploration
					for (int i = x - 1; i >= 0; i--) {
						if (state.isFree(i, y)) {
							TablutAction action = new TablutAction(x, y, i, y);
							result.add(action);
						} else {
							break;
						}
					}

					// NORTH Exploration
					for (int i = y + 1; i < TablutState.MAX_Y; i++) {
						if (state.isFree(x, i)) {
							TablutAction action = new TablutAction(x, y, x, i);
							result.add(action);
						} else {
							break;
						}
					}

					// SOUTH Exploration
					for (int i = y - 1; i >= 0; i--) {
						if (state.isFree(x, i)) {
							TablutAction action = new TablutAction(x, y, x, i);
							result.add(action);
						} else {
							break;
						}
					}
				}

			}
		}
		return result;
	}

	@Override
	public TablutState getResult(TablutState state, TablutAction action) {
		TablutAction tablutAction = (TablutAction) action;

		boolean advantage = false;

		int[][] newSchema = new int[TablutState.MAX_X][TablutState.MAX_Y];
		for (int y = 0; y < TablutState.MAX_Y; y++) {
			for (int x = 0; x < TablutState.MAX_X; x++) {
				newSchema[x][y] = state.getSchema()[x][y];
			}
		}
		int pawn = newSchema[tablutAction.getX1()][tablutAction.getY1()];
		newSchema[tablutAction.getX2()][tablutAction.getY2()] = pawn;
		newSchema[tablutAction.getX1()][tablutAction.getY1()] = TablutState.BOARD_FREE;

		if (tablutAction.getX2() + 2 < TablutState.MAX_X
				&& areOpponents(pawn, newSchema[tablutAction.getX2() + 1][tablutAction.getY2()])
				&& (areAllies(pawn, newSchema[tablutAction.getX2() + 2][tablutAction.getY2()])
						|| TablutState.isForbidden(tablutAction.getX2() + 2, tablutAction.getY2()))) { // EAST
			newSchema[tablutAction.getX2() + 1][tablutAction.getY2()] = TablutState.BOARD_FREE;
			advantage = true;
		}
		if (tablutAction.getX2() - 2 >= 0
				&& areOpponents(pawn, newSchema[tablutAction.getX2() - 1][tablutAction.getY2()])
				&& (areAllies(pawn, newSchema[tablutAction.getX2() - 2][tablutAction.getY2()])
						|| TablutState.isForbidden(tablutAction.getX2() - 2, tablutAction.getY2()))) { // WEST
			newSchema[tablutAction.getX2() - 1][tablutAction.getY2()] = TablutState.BOARD_FREE;
			advantage = true;
		}
		if (tablutAction.getY2() + 2 < TablutState.MAX_X
				&& areOpponents(pawn, newSchema[tablutAction.getX2()][tablutAction.getY2() + 1])
				&& (areAllies(pawn, newSchema[tablutAction.getX2()][tablutAction.getY2() + 2])
						|| TablutState.isForbidden(tablutAction.getX2(), tablutAction.getY2() + 2))) { // NORTH
			newSchema[tablutAction.getX2()][tablutAction.getY2() + 1] = TablutState.BOARD_FREE;
			advantage = true;
		}
		if (tablutAction.getY2() - 2 >= 0
				&& areOpponents(pawn, newSchema[tablutAction.getX2()][tablutAction.getY2() - 1])
				&& (areAllies(pawn, newSchema[tablutAction.getX2()][tablutAction.getY2() - 2])
						|| TablutState.isForbidden(tablutAction.getX2(), tablutAction.getY2() - 2))) { // SOUTH
			newSchema[tablutAction.getX2()][tablutAction.getY2() - 1] = TablutState.BOARD_FREE;
			advantage = true;
		}

		return new TablutState(newSchema, invertRole(state.getPlayer()), advantage, state.getLevel() - 1);
	}

	@Override
	public boolean isTerminal(TablutState state) {
		return state.isTerminal();
	}

	@Override
	public double getUtility(TablutState state, String player) {
		return this.player.equals(state.getPlayer()) ? state.getUtility(player) * -1 : state.getUtility(player);
	}

	@Override
	public TablutState getInitialState() {
		int[][] init = { { 0, 0, 0, 1, 1, 1, 0, 0, 0 }, { 0, 0, 0, 0, 1, 0, 0, 0, 0 }, { 0, 0, 0, 0, 2, 0, 0, 0, 0 },
				{ 1, 0, 0, 0, 2, 0, 0, 0, 1 }, { 1, 1, 2, 2, 3, 2, 2, 1, 1 }, { 1, 0, 0, 0, 2, 0, 0, 0, 1 },
				{ 0, 0, 0, 0, 2, 0, 0, 0, 0 }, { 0, 0, 0, 0, 1, 0, 0, 0, 0 }, { 0, 0, 0, 1, 1, 1, 0, 0, 0 } };
		return new TablutState(init, TablutState.PLAYER_WHITE, 5);
	}

	// utility
	private static boolean areOpponents(int s1, int s2) {
		if (s1 == TablutState.SCHEMA_BLACK && (s1 == TablutState.SCHEMA_WHITE)) {
			return true;
		}
		if (s2 == TablutState.SCHEMA_BLACK && (s1 == TablutState.SCHEMA_KING || s1 == TablutState.SCHEMA_WHITE)) {
			return true;
		}
		return false;
	}

	private static boolean areAllies(int s1, int s2) {
		if (s1 == TablutState.SCHEMA_BLACK && s2 == TablutState.SCHEMA_BLACK) {
			return true;
		}
		if ((s1 == TablutState.SCHEMA_KING || s1 == TablutState.SCHEMA_WHITE)
				&& (s2 == TablutState.SCHEMA_KING || s2 == TablutState.SCHEMA_WHITE)) {
			return true;
		}
		return false;
	}

	private static String invertRole(String s) {
		return TablutState.PLAYER_BLACK.equals(s) ? TablutState.PLAYER_WHITE : TablutState.PLAYER_BLACK;
	}

}

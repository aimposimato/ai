package it.imposimato.tablut;

public class TablutState {
	private final int[][] schema;
	private final String player;

	public static final String PLAYER_BLACK = "B";
	public static final String PLAYER_WHITE = "W";

	public static final int SCHEMA_EMPTY = 0;
	public static final int SCHEMA_BLACK = 1;
	public static final int SCHEMA_WHITE = 2;
	public static final int SCHEMA_KING = 3;

	public static final int MAX_X = 9;
	public static final int MAX_Y = 9;
	public static final int[][] BOARD = { { 2, 2, 2, 1, 1, 1, 2, 2, 2 }, { 2, 0, 0, 0, 1, 0, 0, 0, 2 },
			{ 2, 0, 0, 0, 0, 0, 0, 0, 2 }, { 1, 0, 0, 0, 0, 0, 0, 0, 1 }, { 1, 1, 0, 0, 1, 0, 0, 1, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 1 }, { 2, 0, 0, 0, 0, 0, 0, 0, 2 }, { 2, 0, 0, 0, 1, 0, 0, 0, 2 },
			{ 2, 2, 2, 1, 1, 1, 2, 2, 2 } };

	private int level;

	public static final int BOARD_FREE = 0;
	public static final int BOARD_FORBIDDEN = 1;
	public static final int BOARD_TARGET = 2;

	private boolean evaluated = false;
	private double utility = 0;
	private boolean advantage = false;
	private boolean terminal = false;

	public TablutState(int[][] schema, String player, int level) {
		this.schema = schema;
		this.player = player;
		this.level = level;
	}

	public TablutState(int[][] schema, String player, boolean advantage, int level) {
		this.schema = schema;
		this.player = player;
		this.advantage = advantage;
		this.level = level;
	}

	public static boolean isForbidden(int x, int y) {
		return BOARD[x][y] == BOARD_FORBIDDEN;
	}

	public boolean isFree(int x, int y) {
		return BOARD[x][y] != BOARD_FORBIDDEN && schema[x][y] == SCHEMA_EMPTY;
	}

	public int[][] getSchema() {
		return schema;
	}

	public String getPlayer() {
		return player;
	}

	public boolean isTerminal() {
		evaluateState();
		return terminal;
	}

	private void evaluateState() {
		if (evaluated) {
			return;
		}
		evaluated = true;
		if (level == 0) {
			terminal = true;
		}
		if (advantage) {
			utility = PLAYER_WHITE.equals(player) ? 1 : -1;
		}
		for (int y = 0; y < TablutState.MAX_Y; y++) {
			for (int x = 0; x < TablutState.MAX_X; x++) {
				if (schema[x][y] == SCHEMA_KING) {
					if (BOARD[x][y] == BOARD_TARGET) {
						utility = +10;
					} else if ((schema[x + 1][y] == SCHEMA_BLACK || BOARD[x + 1][y] == BOARD_FORBIDDEN)
							&& (schema[x - 1][y] == SCHEMA_BLACK || BOARD[x - 1][y] == BOARD_FORBIDDEN)
							&& (schema[x][y + 1] == SCHEMA_BLACK || BOARD[x][y + 1] == BOARD_FORBIDDEN)
							&& (schema[x][y - 1] == SCHEMA_BLACK || BOARD[x][y - 1] == BOARD_FORBIDDEN)) {
						utility = +10;
					}
					return;
				}
			}
		}

	}

	public double getUtility(String player) {
		evaluateState();
		return utility;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("State: ").append(System.lineSeparator());
		for (int y = TablutState.MAX_Y - 1; y >= 0; y--) {
			sb.append(y).append(" | ");
			for (int x = 0; x < TablutState.MAX_X; x++) {
				sb.append(schema[x][y]).append(" ");
			}
			sb.append(System.lineSeparator());
		}
		sb.append("    -----------------");
		sb.append(System.lineSeparator());
		sb.append("    0 1 2 3 4 5 6 7 8 ");
		sb.append(System.lineSeparator());

		return sb.toString();
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}

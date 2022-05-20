package it.imposimato.tablut;

import aima.core.agent.Action;

public class TablutAction implements Action {

	private final int x1, y1, x2, y2;

	public TablutAction(int x1, int y1, int x2, int y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}

	@Override
	public boolean isNoOp() {
		// TODO Auto-generated method stub
		return false;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	public String toString() {
		return "Action: (" + x1 + ", " + y1 + ") --> (" + +x2 + ", " + y2 + ")";
	}

}

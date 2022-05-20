package it.imposimato.tablut;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;

import com.google.gson.Gson;

import it.unibo.ai.didattica.competition.tablut.domain.Action;
import it.unibo.ai.didattica.competition.tablut.domain.State;
import it.unibo.ai.didattica.competition.tablut.domain.StateTablut;

/**
 * Classe astratta di un client per il gioco Tablut
 * 
 * @author Andrea Piretti
 *
 */
 class TablutClient  {

	private State.Turn player;
	private String name;
	private Socket playerSocket;
	private DataInputStream in;
	private DataOutputStream out;
	private Gson gson;
	private State currentState;
	private int timeout;
	private String serverIp;

	public State.Turn getPlayer() {
		return player;
	}

	public void setPlayer(State.Turn player) {
		this.player = player;
	}

	public State getCurrentState() {
		return currentState;
	}

	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	/**
	 * Creates a new player initializing the sockets and the logger
	 * 
	 * @param player    The role of the player (black or white)
	 * @param name      The name of the player
	 * @param timeout   The timeout that will be taken into account (in seconds)
	 * @param ipAddress The ipAddress of the server
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public TablutClient(String player, String name, int timeout, String ipAddress, int port)
			throws UnknownHostException, IOException {
		serverIp = ipAddress;
		this.timeout = timeout;
		this.gson = new Gson();
		if (player.toLowerCase().equals("white")) {
			this.player = State.Turn.WHITE;
		} else if (player.toLowerCase().equals("black")) {
			this.player = State.Turn.BLACK;
		} else {
			throw new InvalidParameterException("Player role must be BLACK or WHITE");
		}
		playerSocket = new Socket(serverIp, port);
		out = new DataOutputStream(playerSocket.getOutputStream());
		in = new DataInputStream(playerSocket.getInputStream());
		this.name = name;
	}

	/**
	 * Creates a new player initializing the sockets and the logger. The server is
	 * supposed to be communicating on the same machine of this player.
	 * 
	 * @param player  The role of the player (black or white)
	 * @param name    The name of the player
	 * @param timeout The timeout that will be taken into account (in seconds)
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public TablutClient(String player, String name, int timeout) throws UnknownHostException, IOException {
		this(player, name, timeout, "localhost", 8080);
	}

	/**
	 * Creates a new player initializing the sockets and the logger. Timeout is set
	 * to be 60 seconds. The server is supposed to be communicating on the same
	 * machine of this player.
	 * 
	 * @param player The role of the player (black or white)
	 * @param name   The name of the player
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public TablutClient(String player, String name) throws UnknownHostException, IOException {
		this(player, name, 60, "localhost", 8080);
	}

	/**
	 * Creates a new player initializing the sockets and the logger. Timeout is set
	 * to be 60 seconds.
	 * 
	 * @param player    The role of the player (black or white)
	 * @param name      The name of the player
	 * @param ipAddress The ipAddress of the server
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public TablutClient(String player, String name, String ipAddress) throws UnknownHostException, IOException {
		this(player, name, 60, ipAddress, 8080);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Write an action to the server
	 */
	public void write(Action action) throws IOException, ClassNotFoundException {
		writeString(out, this.gson.toJson(action));
	}

	/**
	 * Write the name to the server
	 */
	public void declareName() throws IOException, ClassNotFoundException {
		writeString(out, this.gson.toJson(this.name));
	}

	/**
	 * Read the state from the server
	 */
	public void read() throws ClassNotFoundException, IOException {
		this.currentState = this.gson.fromJson(readString(in), StateTablut.class);
	}

	// UTIL

	private static void writeString(DataOutputStream out, String s) throws IOException {
		// Converti la stringa in un array di byte codificati con UTF-8
		byte[] bytes = s.getBytes(StandardCharsets.UTF_8);

		// Invio la lunghezza dell'array di byte come intero
		out.writeInt(bytes.length);

		// Invio l'array di byte
		out.write(bytes, 0, bytes.length);
	}

	private static String readString(DataInputStream in) throws IOException {
		// Leggo la lunghezza dei byte in ingresso
		int len = in.readInt();

		// Creo un array di bytes che conterra' i dati in ingresso
		byte[] bytes = new byte[len];

		// Leggo TUTTI i bytes
		in.readFully(bytes, 0, len);

		// Converto i bytes in stringa
		return new String(bytes, StandardCharsets.UTF_8);
	}
}

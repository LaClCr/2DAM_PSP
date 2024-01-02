package clases;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe que representa el servidor del sistema de xat. Gestiona les connexions
 * dels clients i inicia els fils de peticio per a cada client connectat.
 */

public class Servidor {

	@SuppressWarnings("unused")
	private static Socket socket;
	public static List<Peticio> llistaPeticions = new ArrayList<Peticio>();

	/**
	 * Metode principal per iniciar el servidor i gestionar les connexions dels
	 * clients. Escolta els clients que es connecten al port 1234 i inicia un fil de
	 * peticio per a cada un. Imprimeix missatges relatius a l'estat del servidor i
	 * les connexions acceptades.
	 *
	 * @param args Arguments de linia de comandes (no utilitzats en aquest cas)
	 * @throws IOException Llança una excepcio si hi ha un error durant la creacio
	 *                     del socket d'escolta.
	 */

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {

		System.err.println("SERVIDOR >>> Arranca el servidor, espera peticio");
		ServerSocket socketEscolta = null;
		try {
			socketEscolta = new ServerSocket(1234);
		} catch (IOException e) {
			System.err.println("SERVIDOR >>> Error");
			return;
		}
		while (true) {
			Socket connexio = socketEscolta.accept();
			System.err.println("SERVIDOR >>> Connexio rebuda --> Llansa fil classe Peticio");
			Peticio p = new Peticio(connexio, llistaPeticions);
			llistaPeticions.add(p);
			Thread fil = new Thread(p);
			fil.start();
		}
	}

}

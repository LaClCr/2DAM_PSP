package clases;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

import utils.TimeStamp;


/**
 * Classe que representa un client del sistema de xat.
 * Implementa la interfície Runnable per a gestionar la lectura dels missatges del servidor.
 */

public class Client implements Runnable {

	private static Socket socket;

	/**
     * Constructor de la classe Client.
     */
	public Client() {

	}
	
	
	/**
     * Mètode principal que inicia la connexió amb el servidor i gestiona l'entrada d'usuari.
     * Inicialitza la connexió, autentica l'usuari, mostra el menú d'opcions i envia missatges al servidor.
     *
     * @param args Arguments de línia de comandes (no utilitzats en aquest cas)
     */
	public static void main(String[] args) {

		try (Scanner scanner = new Scanner(System.in)) {
			try {
				
				// Establir connexió amb el servidor
				InetSocketAddress direccio = new InetSocketAddress("localhost", 1234);
				socket = new Socket();
				socket.connect(direccio);

				boolean credencialsOk = false;

				
				//Bucle d'autenticació de l'usuari
				do {
					System.err.println("Usuari:");
					String usu = scanner.nextLine();

					System.err.println("Contrasenya:");
					String contra = scanner.nextLine();

					// Mana el usuari i la contrasenya introduits a Servidor
					OutputStream os = socket.getOutputStream();
					PrintWriter pw = new PrintWriter(os);
					pw.write(usu + "\n");
					pw.write(contra + "\n");
					pw.flush();

					//Servidor comprova les credencials i torna un missatge
					// Segons el missatge que reba de servidor, sabrem
					// Si les credencials estan be

					InputStream is = socket.getInputStream();
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader bfr = new BufferedReader(isr);
					String resposta = bfr.readLine();
					if (resposta.equals("0")) {
						credencialsOk = false;
						System.err.println( TimeStamp.obtindreTimeStamp() + " - " + "AQUEST CLIENT JA ES TROBA EN LINIA.");
					} else if (resposta.equals("1")) {
						credencialsOk = true;
						System.err.println( TimeStamp.obtindreTimeStamp() + " - " +"CLIENT " + usu + ": CREDENCIALS CORRECTES.");
					} else if (resposta.equals("2")) {
						credencialsOk = false;
						System.err.println( TimeStamp.obtindreTimeStamp() + " - " +"CLIENT " + usu + ": CREDENCIALS INCORRECTES.");
					}

				} while (!credencialsOk);

				
				boolean eixir = false;

				
				// Instanciem un fil de si mateix per a la lectura, i en el main continuarem amb l'escriptura.
				
				Client c = new Client();
				Thread fil = new Thread(c);
				fil.start();

				System.err.println("\n=========MENU D'OPCIONS=========");
				System.err.println("Escriu '?' -> Per a vore els clients actius");
				System.err.println("Escriu '@usuari + missatge' -> Per a enviar un missatge a aquest usuari.");
				System.err.println("Escriu 'missatge' -> Per a enviar un missatge a tots els usuaris actius.");
				System.err.println("Escriu 'exit' -> Per a eixir del programa. \n");

				
				//Bucle per a l'escriptura
				do {
					String opcio = scanner.nextLine();
					// Enviem  la opcio seleccionada
					OutputStream os = socket.getOutputStream();
					PrintWriter pw = new PrintWriter(os);
					pw.write(opcio + "\n");
					pw.flush();

					if (opcio.equalsIgnoreCase("exit")) {
						eixir = true;
						
					}

				} while (!eixir);


			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("SERVIDOR >>> Error.");
			}
		}
	}
	
	
	
	/**
     * Mètode que gestiona la lectura dels missatges dsel servidor i els mostra per pantalla.
     */
	public void run() {
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				System.err.println(line);
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}

	}
	
	
}
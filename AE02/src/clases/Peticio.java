package clases;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import utils.TimeStamp;

/**
 * Classe que implementa la interfície Runnable per a gestionar les peticions dels clients.
 */

public class Peticio implements Runnable {

	private Socket socket;
	public List<Peticio> llistaPeticions = new ArrayList<Peticio>();
	private String nom;

	
	 /**
     * Constructor de la classe Peticio.
     *
     * @param socket             El socket del client
     * @param llistaPeticions    Llista de peticions dels clients connectats
     */
	
	public Peticio(Socket socket, List<Peticio> llistaPeticions) {
		this.socket = socket;
		this.llistaPeticions = llistaPeticions;
	}
	
	
	
	/**
     * Obté el nom de l'usuari.
     *
     * @return El nom de l'usuari
     */

	public String getNom() {
		return nom;
	}

	
	/**
     * Mètode que gestiona les peticions dels clients.
     * Autentica els clients, envia missatges i gestiona la connexió.
     */
	public void run() {

		try {

			// Rep les credencials de client

			boolean credencialsOk = false;

			do {

				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader bfr = new BufferedReader(isr);
				String usuari = bfr.readLine();
				String contrasenya = bfr.readLine();

				// Comprova les credencials i torna un missatge o altre

				credencialsOk = comprovaCredencials(usuari, contrasenya);
				String missatge;
				if (credencialsOk) {

					if (conectat(usuari)) {
						missatge = "0";
						System.err.println(TimeStamp.obtindreTimeStamp()+ " - " +"SERVIDOR >> " + usuari + ": JA ESTA CONECTAT");
						credencialsOk = false;

					} else {
						missatge = "1";
						System.err.println(TimeStamp.obtindreTimeStamp()+ " - " +"SERVIDOR >> " + usuari + ": CREDENCIALS OK");
						this.nom = usuari;

					}
				} else {
					missatge = "2";
					System.err.println(TimeStamp.obtindreTimeStamp()+ " - " +"SERVIDOR >> " + usuari + ": ERROR: CREDENCIALS INCORRECTES");

				}
				OutputStream os = socket.getOutputStream();
				PrintWriter pw = new PrintWriter(os);
				pw.write(missatge + "\n");
				pw.flush();

			} while (!credencialsOk);

			// Una vegada estan les credencials ok, rebrem missatges de Client

			boolean eixir = false;

			do {
				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader bfr = new BufferedReader(isr);
				String opcio = bfr.readLine();
				System.out.println(TimeStamp.obtindreTimeStamp()+ " - " +this.nom + ": " + opcio);

				if (opcio.equals("?")) {

					OutputStream os = socket.getOutputStream();
					PrintWriter pw = new PrintWriter(os);
					pw.write("\nCLIENTS ACTIUS:\n");

					for (Peticio peticio : llistaPeticions) {
						if (peticio.getNom() != null) { // per a que no torne un null cuan s'estiga registrat uno
							pw.write(peticio.nom + "\n");
							pw.flush();
						}
					}

				} else if (opcio.startsWith("@")) {
					String[] parts = opcio.split(" ", 2);
					if (parts.length == 2) {
						String nomUser = parts[0].substring(1);
						String missatge = parts[1];
						nomUser.substring(1);

						// Enviar el missatge al usuari corresponent

						synchronized (llistaPeticions) {
							for (Peticio peticio : llistaPeticions) {
								if (peticio.getNom().equals(nomUser) && !peticio.getNom().equals(nom)) {
									peticio.enviarMensaje(nom + ": " + missatge);
								}
							}
						}

					}
					} else if (opcio.equalsIgnoreCase("exit")) {
						synchronized (llistaPeticions) {
							int indexABorrar = -1;
							for (int i = 0; i < llistaPeticions.size(); i++) {
								if (llistaPeticions.get(i).getNom() != null) {
									if (llistaPeticions.get(i).getNom().equals(nom)) {
										indexABorrar = i;
										break; 
									}
								}

							}
							if (indexABorrar >= 0)
								System.err.println(TimeStamp.obtindreTimeStamp()+ " - " +"SERVIDOR >> " + this.nom + ": S'HA DESCONNECTAT");
								llistaPeticions.remove(indexABorrar);
						}
						eixir = true;

					} else {

						synchronized (llistaPeticions) {
							for (Peticio peticio : llistaPeticions) {
								if (peticio.getNom() != null) {
									if (!peticio.getNom().equals(nom)) {
										peticio.enviarMensaje( nom + ": " +opcio);
									}
								}
							}
						}

					}

				
			} while (!eixir);
			this.socket.close();

		}catch(

	Exception e)
	{
		e.printStackTrace();
		System.err.println(TimeStamp.obtindreTimeStamp()+ " - " +"SERVIDOR >>> Error.");
	}
	}

	/**
     * Envia un missatge al client connectat.
     *
     * @param mensaje   El missatge a enviar
     * @throws IOException Llança una excepció en cas d'error en l'enviament del missatge
     */
	public void enviarMensaje(String mensaje) throws IOException {
		OutputStream os = socket.getOutputStream();
		PrintWriter pw = new PrintWriter(os);
		pw.write(TimeStamp.obtindreTimeStamp()  + mensaje + "\n");
		pw.flush();
	}

	
	/**
     * Comprova si l'usuari està connectat.
     *
     * @param nom   El nom de l'usuari
     * @return      Cert si l'usuari està connectat, sinó fals
     */
	public boolean conectat(String nom) {

		boolean ok = false;

		for (Peticio peticio : llistaPeticions) {
			if (peticio.getNom() != null) {
				if (peticio.getNom().equals(nom))
					ok = true;
			}

		}
		return ok;
	}
	
	/**
     * Verifica les credencials de l'usuari.
     *
     * @param user  Nom d'usuari
     * @param pass  Contrasenya
     * @return      Cert si les credencials són vàlides, sinó fals
     */

	@SuppressWarnings("resource")
	public boolean comprovaCredencials(String user, String pass) {

		File credencials = new File("./src/utils/credencials.txt");
		boolean ok = false;

		try {
			FileReader fr = new FileReader(credencials);
			BufferedReader br = new BufferedReader(fr);

			String line = br.readLine();
			while (line != null) {
				String[] obj = line.split(";");
				if (obj[0].equals(user) && obj[1].equals(pass)) {
					ok = true;
				}
				line = br.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("SERVIDOR >>> Error.");
		}

		return ok;
	}
	

}
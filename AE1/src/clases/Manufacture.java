package clases;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * Classe que representa el procés de fabricació.
 */
public class Manufacture {

	private static final int maxFils = 8;

	/**
     * Metode principal que inicia el proces de fabricacio amb les peces proporcionades.
     * @param args Arguments que contenen informacio sobre les peces a fabricar.
     */
	@SuppressWarnings("unused")
	public static void main(String[] args) {

		List<String> llistaLogs = new ArrayList<String>();
		Manufacture m = new Manufacture();
		List<Thread> fils = new ArrayList<>();

		List<Pesa> peses = new ArrayList<Pesa>();
		for (int i = 0; i < args.length; i++) {
			String[] obj = args[i].split(";");
			int numPeses = Integer.parseInt(obj[1]);
			for (int j = 0; j < numPeses; j++) {
				Pesa pesa = new Pesa(obj[0], llistaLogs);
				peses.add(pesa);
			}
		}

		for (Pesa pesa : peses) {
			Thread t = new Thread(() -> {
				pesa.run();
			});

			System.out.println("\nFil numero " + (fils.size()+1) + ": Manant a fabricar la pesa  " + pesa.getTipoPesa());
			t.start();
			fils.add(t);
			if (fils.size() == maxFils) {
				System.out.println("\nCapacitat maxima de fils. Esperant a que acaben...\n");
				esperarQueAcabenElsFils(fils);
			}
		}

		esperarQueAcabenElsFils(fils);

		System.out.println("\n\nGenerant LOGS");
		generarLog(llistaLogs);

	}

	/**
     * Metode que espera que els fils especificats acaben la seua execucio.
     * @param fils Llista de fils que es vol esperar que acabin.
     */
	private static void esperarQueAcabenElsFils(List<Thread> threads) {
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		threads.clear();
	}

	/**
     * Genera un fitxer de logs amb les dades proporcionades.
     * @param llistaLogs Llista de logs a incloure al fitxer.
     */
	public static void generarLog(List<String> llistaLogs) {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
		String timestamp = now.format(formatter);

		String nomLog = "LOG_" + timestamp;
		File f = new File("./src/logs/" + nomLog + ".txt");

		try {
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);

			for (int i = 0; i < llistaLogs.size(); i++) {
				bw.write(llistaLogs.get(i) + "\n");
			}

			bw.close();
			fw.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}
}

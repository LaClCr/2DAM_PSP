package clases;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


/**
 * Clase que representa una pesa en el sistema de fabricacio.
 * Implementa la interficie Runnable per a executar el proces de fabricacio en un fil.
 */
public class Pesa implements Runnable {

	private String tipoPesa;
	private String log;
	List<String> llistaLogs;


	/**
     * Constructor de la classe Pesa.
     * @param tipoPesa Tipus de la pesa.
     * @param llistaLogs Llista de logs compartida.
     */
	public Pesa(String tipoPesa,List<String> llistaLogs) {
		super();
		this.tipoPesa = tipoPesa;
		this.llistaLogs = llistaLogs;
	}
	

	/**
     * Obte el tipus de la pesa.
     * @return El tipus de la pesa.
     */
	public String getTipoPesa() {
		return tipoPesa;
	}
	

	 /**
     * Metode que simula el proces de fabricacio de la pesa.
     * @param tempsFabricacio Temps de fabricacio en milisegons.
     */
	@SuppressWarnings("unused")
	public static void procesFabricacio(int tempsFabricacio) {
		long tempsInici = System.currentTimeMillis();
		long tempsFinal = tempsInici + tempsFabricacio;
		int iteracions = 0;
		while (System.currentTimeMillis() < tempsFinal) {
			iteracions++;
		}
	}
	
	/**
     * Obte el temps de fabricacio basat en el tipus de pesa.
     * @param tipoPesa Tipus de la pesa.
     * @return Temps de fabricacio en milisegons.
     */
	public static int obtindreTempsFabricacio(String tipoPesa) {
        int temps;

        switch (tipoPesa) {
            case "I":
            	temps = 1000;
                break;
            case "O":
            	temps = 2000;
                break;
            case "T":
            	temps = 3000;
                break;
            case "J":
            case "L":
            	temps = 4000;
                break;
            case "S":
            case "Z":
            	temps = 5000;
                break;
            default:
            	temps = -1; 
                break;
        }

        return temps;
    }
	
	
	/**
     * Metode que s'executa quan es llança el fil.
     * Simula el proces de fabricacio, registra el log i l'afegeix a la llista compartida.
     */
	public void run() {
		

        int temps = obtindreTempsFabricacio(this.tipoPesa);
        procesFabricacio(temps);
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = now.format(formatter);
        
        log = tipoPesa + "_" + timestamp;
        System.out.println("\nLa pesa  " + this.tipoPesa + "  Ha sigut fabricada. ( "+log+" ).");
        
        afegirALogs();

		
	}
	
	
	/**
     * Metode que afegeix a la llista compartida de logs.
     * Es synchronized per a que no puguin accedir mes d'un fil a la vegada.
     */
	private synchronized void afegirALogs() {
		llistaLogs.add(log);
	}
	
	
	
}

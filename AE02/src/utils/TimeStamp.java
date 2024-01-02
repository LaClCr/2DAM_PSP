package utils;
/**
 * Classe que representa una utilitat en concret que es extraure el TimeStamp actual.
 */
public class TimeStamp {


	/**
	 * Mètode estàtic per obtenir una marca de temps amb el format
	 * "ddMMyyyy_HH:mm:ss".
	 *
	 * @return Una cadena que representa la marca de temps en el format especificat.
	 */
	public static String obtindreTimeStamp() {
		java.util.Date data = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("ddMMyyyy_HH:mm:ss");
		return sdf.format(data);
	}


}

package evaluable3.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import evaluable3.Filtre.FiltreExtensio;

@RestController
public class Controller {

	static File directoriPelis = new File("./pelicules");
	static String[] pelis = directoriPelis.list(new FiltreExtensio(".txt"));

	/**
	 * Gestionar les peticions GET per obtenir informació sobre les pel·lícules a
	 * través de l'APIpelis.
	 *
	 * @param strVariable Una cadena de text que pot ser "all" per obtenir
	 *                    informació de totes les pel·lícules o l'ID d'una
	 *                    pel·lícula específica.
	 * @return ResponseEntity amb l'estat de la resposta i les dades de la
	 *         pel·lícula o llista de pel·lícules. Retorna una resposta amb totes
	 *         les pel·lícules i els seus títols si la variable és "all". Retorna
	 *         una resposta amb la informació de la pel·lícula específica si l'ID
	 *         coincideix amb una pel·lícula existent. Retorna un error NOT FOUND si
	 *         l'ID no pertany a cap pel·lícula.
	 */

	@GetMapping("APIpelis/t")
	ResponseEntity<String> tornaInfoPelis(@RequestParam(value = "id") String strVariable) {

		pelis = directoriPelis.list(new FiltreExtensio(".txt"));
		String resposta = "";

		if (strVariable.equals("all")) {
			JSONObject respostaJson = new JSONObject();
			JSONArray titolsArray = new JSONArray();

			for (int i = 0; i < pelis.length; i++) {
				try {
					FileReader fr = new FileReader("./pelicules/" + pelis[i]);
					BufferedReader br = new BufferedReader(fr);

					JSONObject peliculaJson = new JSONObject();

					String id = pelis[i].split("\\.")[0];
					peliculaJson.put("id", id);

					String titol = br.readLine().split(":")[1].trim();
					peliculaJson.put("titol", titol);

					titolsArray.put(peliculaJson);

					fr.close();
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			respostaJson.put("titols", titolsArray);
			resposta = respostaJson.toString();
			System.out.println(resposta);
		} else {
			boolean existeix = false;

			for (int i = 0; i < pelis.length; i++) {

				String id = pelis[i].split("\\.")[0];
				if (id.equals(strVariable)) {
					existeix = true;
				}
			}

			if (!existeix) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Content-Length", "0")
						.body("El recurso no fue encontrado");
			} else {

				try {

					FileReader fr = new FileReader("./pelicules/" + strVariable + ".txt");

					BufferedReader br = new BufferedReader(fr);

					JSONObject peliculaJson = new JSONObject();

					peliculaJson.put("id", strVariable);

					String titol = br.readLine().split(":")[1].trim();
					peliculaJson.put("titol", titol);

					JSONArray ressenyesJson = new JSONArray();
					String linia;

					while ((linia = br.readLine()) != null) {
						String resenya = linia.trim();
						ressenyesJson.put(resenya);
					}

					br.close();
					fr.close();

					peliculaJson.put("ressenyes", ressenyesJson);
					resposta = peliculaJson.toString();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return ResponseEntity.ok(resposta);

	}

	/**
	 * Gestiona les peticions POST per a l'afegiment d'una nova ressenya a una
	 * pel·lícula a través de l'APIpelis.
	 *
	 * @param stringJSON Una cadena JSON que conté les dades de la nova ressenya,
	 *                   incloent l'usuari, la identificació de la pel·lícula (id) i
	 *                   la ressenya.
	 * @return ResponseEntity amb l'estat de la resposta i les capçaleres adequades.
	 *         Retorna una resposta sense contingut si l'usuari està autoritzat i es
	 *         crea amb èxit la nova ressenya. Retorna un error d'autorització si
	 *         l'usuari no està autoritzat per a realitzar aquesta acció. Retorna un
	 *         error forbidden si hi ha problemes en el processament de la petició o
	 *         si no es pot afegir la ressenya al fitxer de la pel·lícula.
	 */

	@PostMapping("APIpelis/novaResenya")
	ResponseEntity<String> postBodyNovaResenya(@RequestBody String stringJSON) {
		JSONObject cosPeticio = new JSONObject(stringJSON);

		try {
			String usuari = (String) cosPeticio.get("usuari");
			String id = (String) cosPeticio.get("id");
			String ressenya = (String) cosPeticio.get("ressenya");

			boolean existeix = false;

			for (int i = 0; i < pelis.length; i++) {

				String idReal = pelis[i].split("\\.")[0];
				if (idReal.equals(id)) {
					existeix = true;
				}
			}

			if (existeix) {

				if (usuariAutoritzat(usuari)) {
					FileWriter fw = new FileWriter("./pelicules/" + id + ".txt", true);
					fw.write("\n" + usuari + ":" + ressenya);
					fw.close();

					return ResponseEntity.noContent().header("Content-Length", "0").build();
				} else {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Content-Length", "0").build();
				}
			}else {
				return ResponseEntity.status(HttpStatus.CONFLICT).header("Content-Length", "0").build();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

	}

	/**
	 * Gestiona les peticions POST per a la creació d'una nova pel·lícula a través
	 * de l'APIpelis.
	 *
	 * @param stringJSON Una cadena JSON que conté les dades de la nova pel·lícula,
	 *                   incloent el títol i l'usuari.
	 * @return ResponseEntity amb l'estat de la resposta i les capçaleres adequades.
	 *         Retorna una resposta sense contingut si l'usuari està autoritzat i es
	 *         crea amb èxit la nova pel·lícula. Retorna un error d'autorització si
	 *         l'usuari no està autoritzat per a realitzar aquesta acció. Retorna un
	 *         error forbidden si hi ha problemes en el processament de la petició o
	 *         si no es pot crear el fitxer de la nova pel·lícula.
	 */

	@PostMapping("APIpelis/novaPeli")
	ResponseEntity<String> postBodyNovaPeli(@RequestBody String stringJSON) {
		JSONObject cosPeticio = new JSONObject(stringJSON);
		try {
			String titol = (String) cosPeticio.get("titol");
			String usuari = (String) cosPeticio.get("usuari");
			if (usuariAutoritzat(usuari)) {
				pelis = directoriPelis.list(new FiltreExtensio(".txt"));
				int id = pelis.length + 1;
				File fitxer = new File("./pelicules/" + id + ".txt");
				if (!fitxer.exists()) {
					if (fitxer.createNewFile()) {
						FileWriter fw = new FileWriter("./pelicules/" + id + ".txt", true);
						fw.write("Titulo:" + titol);
						fw.close();
					}
				}
				return ResponseEntity.noContent().header("Content-Length", "0").build();
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Content-Length", "0").build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

	}

	/**
	 * Maneja les peticions POST per a la creació d'un nou usuari a través de
	 * l'APIpelis.
	 *
	 * @param stringJSON Una cadena JSON que conté les dades del nou usuari.
	 * @return ResponseEntity amb l'estat de la resposta i les capçaleres adequades.
	 *         Retorna una resposta sense contingut si l'usuari és autoritzat i es
	 *         guarda al fitxer d'autoritzats. Retorna un conflicte si l'usuari ja
	 *         està autoritzat. Retorna un error forbidden si hi ha problemes en el
	 *         processament de la petició.
	 */

	@PostMapping("APIpelis/nouUsuari")
	ResponseEntity<String> postBodyNouUsuari(@RequestBody String stringJSON) {
		JSONObject cosPeticio = new JSONObject(stringJSON);
		try {
			String usuari = (String) cosPeticio.get("usuari");
			if (!usuariAutoritzat(usuari)) {
				FileWriter fw = new FileWriter("./autoritzats/autoritzats.txt", true);
				fw.write("\nNom usuari:" + usuari);
				fw.close();
				return ResponseEntity.noContent().header("Content-Length", "0").build();
			} else {
				return ResponseEntity.status(HttpStatus.CONFLICT).header("Content-Length", "0").build();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

	}

	/**
	 * Busca en el fitxer si existeix algun usuari en el nom del parametre donat
	 * 
	 * @param nomUsuari
	 * @return boolean
	 */
	@SuppressWarnings("resource")
	private boolean usuariAutoritzat(String nomUsuari) {

		try {
			FileReader fr = new FileReader("./autoritzats/autoritzats.txt");
			BufferedReader br = new BufferedReader(fr);
			String linia = br.readLine(); // Eliminem la primera línia perquè és el títol
			while ((linia = br.readLine()) != null) {
				if (!linia.isBlank()) {
					String[] descomposat = linia.split(":");
					if (nomUsuari.equals(descomposat[1]))
						return true;
				}

			}

			br.close();
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

}

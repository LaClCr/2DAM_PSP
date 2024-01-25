package evaluable3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

//		GET:
//
//		http://localhost:8080/APIpelis/t?id=all 
//		http://localhost:8080/APIpelis/t?id={id}
//
//		POST:
//
//		http://localhost:8080/APIpelis/novaResenya
//			
//		{
//			 "usuari": "nomUsuari",
//			 "id": "5",
//			 "ressenya": "text de la ressenya"
//			}
//			
//		http://localhost:8080/APIpelis/novaPeli
//			
//		{
//			 "usuari": "nomUsuari",
//			 "titol": "titolPelicula"
//			}
//			
//		http://localhost:8080/APIpelis/nouUsuari
//		{
//			 "usuari": "nomUsuari",
//			}
			
			
	
	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
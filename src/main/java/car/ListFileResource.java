package car;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Ressource REST accessible a l'adresse :
 * 
 * 		http://localhost:8080/ftp/api/listfile
 * 
 * Pour tester l'affichege de la liste de fichiers
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */

@Path("/listfile")
public class ListFileResource {
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Collection<PosixFileAttributes> affiche() throws IOException {
		File myDir = new File("/home/m1/rakotoarivony/testRest");
		Collection<PosixFileAttributes> filesAttr = new LinkedList<PosixFileAttributes>();
		for(File file:myDir.listFiles()){
			PosixFileAttributes attr = Files.readAttributes(
					Paths.get(file.getAbsolutePath()),
					PosixFileAttributes.class);
			filesAttr.add(attr);
		}
		return filesAttr;
	}
}

package car;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Exemple de ressource REST accessible a l'adresse :
 * 
 * 		http://localhost:8080/ftp/api/helloworld
 * 
 * @author Lionel Seinturier <Lionel.Seinturier@univ-lille1.fr>
 */
@Path("/helloworld")
public class HelloWorldResource {

	@GET
	@Produces("text/html")
	public String sayHello() {
		return "<h1>Hello World</h1>";
	}

	 @GET	 
	 @Path("/book/{isbn}")
	 public String getBook( @PathParam("isbn") String isbn ,@QueryParam("title") String title) {
		 return "Book: "+isbn+" title:"+title;		 
	 }
	 
	 @POST
	 @Path("/book/{isbn}")
	 public String getBookPost( @PathParam("isbn") String isbn ,@QueryParam("title") String title) {
		 return "Book: "+isbn+" title:"+title;		 
	 }

	 @GET
	 @Path("{var: .*}/stuff")
	 public String getStuff( @PathParam("var") String stuff ) {
		 return "Stuff: "+stuff;
	 }
}


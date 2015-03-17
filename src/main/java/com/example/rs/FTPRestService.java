package com.example.rs;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import com.example.services.FTPService;

/**
 * sert à executer les requêtes REST avec les verbes HTTP
 * @author Allan Rakotoarivony, Tanguy Maréchal
 * accessible à l'adresse: http://localhost:8080/ftp/api/home
 *
 */
@Path("/home")
public class FTPRestService {
	@Inject private  FTPService ftpService;
	//private  FTPService ftpService = new FTPService(); //pour les tests
	
	@GET 	
	@Produces({MediaType.APPLICATION_JSON})
	public FTPFile[] listRootJSON() {
		System.out.println("FTPRestService.listRootJSON()");
		try {
			return ftpService.listDirectoryJSON();
		} catch (SocketException e) {
			return null;
		}
		catch (IOException e) {
			return null;
		}
	}
	
	/**
	 * list all files in the root directory in HTML format
	 * @return HTML text
	 */
	@GET
	@Produces({MediaType.TEXT_HTML})
	public Response listRoot() {
		System.out.println("FTPRestService.listRoot()");
		try {
			return ftpService.listRootDirectory();
		} catch (SocketException e) {
			
		} catch (IOException e) {
			
		}
		return Response.status(Response.Status.NOT_FOUND).entity("Racine non trouvé").build();
	}
	
	/**
	 * 
	 * @return a formatted HTML which represents the files in this directory
	 */
	@GET
	@Produces({MediaType.TEXT_HTML})
	@Path("/{dirname: .*/}")
	public Response listDirectory( @PathParam("dirname") String dirname){
		try {
			return ftpService.listDirectory(dirname);
		} catch (SocketException e) {
		} catch (IOException e) {
		}
		return Response.status(Response.Status.NOT_FOUND).entity("dossier "+dirname+" non trouvé").build();
	}
	
	
	
	@GET
	@Produces({MediaType.APPLICATION_OCTET_STREAM})
	/**
	 * 
	 * @return a Response which contains the HTTP code answer
	 * formatted HTML which represents the files in this directory
	 */
	@Path("/{dirname: .*}file/{filename: .*}") //il y a un regex là :D
	public Response getFile( @PathParam("dirname") String dirname ,@PathParam("filename") String filename){
		try {
			if(dirname == null)dirname = "";
			if(filename == null)filename = "";
			return ftpService.getFile(dirname+filename);
		} catch (SocketException e) {
		} catch (IOException e) {
		}
		return Response.status(Response.Status.NOT_FOUND).entity("fichier "+filename+" non trouvé").build();
	}
	
	@DELETE
	@Path("/{dirname: .*}file/{filename: .*}")
	public Response deleteFile(@PathParam("dirname") String dirname ,@PathParam("filename") String filename){
		try {
			if(dirname == null)dirname = "";
			if(filename == null)filename = "";
			return ftpService.deleteFile(dirname+filename);
		} catch (IOException e) {
		}
		return Response.status(Response.Status.BAD_GATEWAY).entity("Connexion non disponible").build();
	}
	
	@POST
	@Path("/{dirname: .*/}")
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	@Produces({MediaType.TEXT_HTML})	
	public Response postFile(MultipartFormDataInput input){
		try {
			return ftpService.upload(dirname,link);
		} catch (IOException e) {
			return Response.status(Response.Status.BAD_GATEWAY).entity("Connexion non disponible").build();
		}
	}
	
}

package com.restapi.restService;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;

import com.restapi.exception.DirectoryNotFoundException;
import com.restapi.services.FTPService;

/**
 * sert à executer les requêtes REST avec les verbes HTTP
 * @author Allan Rakotoarivony, Tanguy Maréchal
 * accessible à l'adresse: http://localhost:8080/rest/api/home/
 *
 */
@Path("/home/")
public class FTPRestService {
	@Inject private  FTPService ftpService;
	//private  FTPService ftpService = new FTPService(); //pour les tests
		
	/**
	 * is the entryPoint of the application. It lists all files in the root directory  in HTML format
	 * @return an http response 200 and a formatted HTML which represents the files in the root if everithing goes fine
	 * 			an http response 502 if the connexion to the ftp server failed
	 * 			an http response 404 if the directory can't be found
	 */
	@GET
	@Produces({MediaType.TEXT_HTML})
	public Response listRoot() {
		try {
			return Response.ok(ftpService.listDirectory("/"),MediaType.TEXT_HTML).build();
		} catch (SocketException e) {
			return Response.status(Response.Status.BAD_GATEWAY).entity("Connexion non disponible").build();			
		} catch (IOException e) {
			return Response.status(Response.Status.BAD_GATEWAY).entity("Connexion non disponible").build();			
		} catch (DirectoryNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}
	
	/**
	 * lists all files in the directory specified by dirname in HTML format
	 * @param dirname the name of the directory to be listed
	 * @return an http response 200 and a formatted HTML which represents the files in the directory dirname if everithing goes fine
	 * 			an http response 502 if the connexion to the ftp server failed
	 * 			an http response 404 if the directory can't be found
	 */
	@GET
	@Produces({MediaType.TEXT_HTML})
	@Path("/{dirname: .*/}")
	public Response listDirectory( @PathParam("dirname") String dirname){
		try {
			return Response.ok(ftpService.listDirectory(dirname),MediaType.TEXT_HTML).build();
		} catch (SocketException e) {
			return Response.status(Response.Status.BAD_GATEWAY).entity("Connexion non disponible").build();
		} catch (IOException e) {
			return Response.status(Response.Status.BAD_GATEWAY).entity("Connexion non disponible").build();
		} catch (DirectoryNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}
	
	/**
	 * lists all files in the root directory  in HTML format
	 * @return an http response 200 and a formatted HTML which represents the files in the root if everithing goes fine
	 * 			an http response 502 if the connexion to the ftp server failed
	 * 			an http response 404 if the directory can't be found
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response listRootJSON() {
		System.out.println("FTPRestService.listRoot()");
		try {
			return Response.ok(ftpService.getFiles("/"),MediaType.APPLICATION_JSON).build();
		} catch (SocketException e) {
			return Response.status(Response.Status.BAD_GATEWAY).entity("Connexion non disponible").build();			
		} catch (IOException e) {
			return Response.status(Response.Status.BAD_GATEWAY).entity("Connexion non disponible").build();			
		} catch (DirectoryNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}
	
	/**
	 * lists all files in the directory specified by dirname in HTML format
	 * @param dirname the name of the directory to be listed
	 * @return an http response 200 and a formatted HTML which represents the files in the directory dirname if everithing goes fine
	 * 			an http response 502 if the connexion to the ftp server failed
	 * 			an http response 404 if the directory can't be found
	 */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Path("/{dirname: .*/}")
	public Response listDirectoryJSON( @PathParam("dirname") String dirname){
		try {
			return Response.ok(ftpService.getFiles(dirname),MediaType.APPLICATION_JSON).build();
		} catch (SocketException e) {
			return Response.status(Response.Status.BAD_GATEWAY).entity("Connexion non disponible").build();
		} catch (IOException e) {
			return Response.status(Response.Status.BAD_GATEWAY).entity("Connexion non disponible").build();
		} catch (DirectoryNotFoundException e) {
			return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
		}
	}
	
	
	/**
	 * let the user to retrieve the file specified by dirname/filename
	 * @param dirname the name of directory whre the file is
	 * @param filename the name of the file to be retrieved
	 * @return an http response 200 and the stream of the requested file everything goes well
	 * 			an http response 502 if the connexion to the ftp server failed
	 * 			
	 */
	@GET
	@Produces({MediaType.APPLICATION_OCTET_STREAM})
	@Path("/{dirname: .*}file/{filename: .*}") //il y a un regex là :D
	public Response getFile( @PathParam("dirname") String dirname ,@PathParam("filename") String filename){
		try {
			if(dirname == null)dirname = "";
			if(filename == null)filename = "";
			return ftpService.getFile(dirname+filename);
		} catch (SocketException e) {
			return Response.status(Response.Status.BAD_GATEWAY).entity("Connexion non disponible").build();
		} catch (IOException e) {
			return Response.status(Response.Status.BAD_GATEWAY).entity("Connexion non disponible").build();
		}
	}
	
	/**
	 * delete the file filename in the directory dirname
	 * @param dirname the name of the directory
	 * @param 
	 */
	@DELETE
	@Path("/{dirname: .*}file/{filename: .*}")
	
	public Response deleteFile(@PathParam("dirname") String dirname ,@PathParam("filename") String filename){
		try {
			if(dirname == null)dirname = "";
			if(filename == null)filename = "";
			return ftpService.deleteFile(dirname+filename);
		} catch (IOException e) {
			return Response.status(Response.Status.BAD_GATEWAY).entity("Connexion non disponible").build();
		}
		
	}
	
	@POST
	@Path("/{dirname: .*/}")
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	@Produces({MediaType.TEXT_HTML})	
	public Response postFile(@PathParam("dirname") String dirname,@Multipart("file") MultipartBody file){
		try {
			String filename = file.getRootAttachment().getDataHandler().getName();
			InputStream fileStream = file.getRootAttachment().getDataHandler().getInputStream();
			return ftpService.upload(dirname,fileStream,filename);
		} catch (IOException e) {
			return Response.status(Response.Status.BAD_GATEWAY).entity("Connexion non disponible").build();
		}
	}
	
	@PUT
	@Path("/{dirname: .*/}")
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	@Produces({MediaType.TEXT_HTML})	
	public Response putFile(@PathParam("dirname") String dirname,@Multipart("file") MultipartBody file){
		try {
			String filename = file.getRootAttachment().getDataHandler().getName();
			InputStream fileStream = file.getRootAttachment().getDataHandler().getInputStream();
			return ftpService.upload(dirname,fileStream,filename);
		} catch (IOException e) {
			return Response.status(Response.Status.BAD_GATEWAY).entity("Connexion non disponible").build();
		}
	}
	
}

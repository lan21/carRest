package com.example.rs;

import java.io.IOException;
import java.net.SocketException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.net.ftp.FTPFile;

import com.example.services.FTPService;

/**
 * sert à executer les requêtes REST avec les verbes HTTP
 * @author Allan Rakotoarivony, Tanguy Maréchal
 * accessible à l'adresse: http://localhost:8080/ftp/api/dir
 *
 */
@Path("/dir")
public class FTPRestService {
	@Inject private  FTPService ftpService;
	
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public FTPFile[] listRootJSON() {
		try {
			return ftpService.listDirectoryJSON();
		} catch (SocketException e) {
			return null;
		}
		catch (IOException e) {
			return null;
		}
	}
	
	@GET
	@Produces("text/html")
	public String listRoot() {
		try {
			return ftpService.listDirectory();
		} catch (SocketException e) {
			
		} catch (IOException e) {
			
		}
		return "<h2>Error in getting root</h2>";
	}
	

	@GET
	@Produces("text/html")
	/**
	 * 
	 * @return a formatted HTML which represents the files in this directory
	 */
	@Path("/{dirname: .*}") //il y a un regex là :D
	public String listDirectory( @PathParam("dirname") String dirname){
		try {
			return ftpService.listDirectory(dirname);
		} catch (SocketException e) {
		} catch (IOException e) {
		}
		return "<h2>Cannot access to directory</h2>";
	}
	
}

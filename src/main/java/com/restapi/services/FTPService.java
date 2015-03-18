package com.restapi.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Service;

import com.restapi.exception.DirectoryNotFoundException;

/**
 * is a gateway between the rest interface and the ftp server
 * @author allan rakotoarivony - Tanguy Maréchal
 *
 */
@Service
public class FTPService {
	/**
	 * login used to connect to the ftp server
	 */
	private String username = "allan";
	/**
	 * password used with the login "allan" to connect to the ftp server 
	 */
	private String password = "allan";
	
	private static final String server = "localhost";
	
	private HTMLGenerator htmlGenerator = new HTMLGenerator();
	
	
	/**
	 * connects to an ftp and list all directories on the root of the ftp in JSON
	 * @param dirname 
	 * @return an array of FTPFile which contains all informations about all files present in this directory
	 * @throws SocketException
	 * @throws IOException
	 * @throws DirectoryNotFoundException 
	 */
	public FTPFile[] getFiles(String dirname) throws SocketException, IOException, DirectoryNotFoundException{
		FTPClient client = connectToFTP();
		FTPFile[] files;
		
		boolean directoryFound = true;
		if(dirname != null){
			directoryFound = client.changeWorkingDirectory(dirname);
		}
		if(!directoryFound){
			client.disconnect();
			throw new DirectoryNotFoundException("directory "+dirname+" not found");
		}
		files = client.listFiles();		
		
		client.disconnect();
		return files;
	}
	
		
	/**
	 * List the directory specified by the pathname
	 * @param pathname
	 * @return a formated HTML string representing a listing of files in the directory specified by pahtname
	 * @throws SocketException
	 * @throws IOException
	 * @throws DirectoryNotFoundException 
	 */
	public String listDirectory(String pathname) throws SocketException, IOException, DirectoryNotFoundException{
		FTPFile[] files = getFiles(pathname);
		
		String listFile = htmlGenerator.HTMLPage(files, pathname);	
		
		return listFile;
	}
	
	

	/**
	 * return the requested file as a stream
	 * @param folderName
	 * @return a response corresponding to the 
	 * @throws IOException 
	 */
	public Response getFile(String filename) throws IOException {
		FTPClient client = connectToFTP();
		Response response;
		client.setFileType(FTP.BINARY_FILE_TYPE);
		InputStream fileInputStream = client.retrieveFileStream(filename);
		if(fileInputStream == null){
			System.out.println("not found");
			response = Response.status(Response.Status.NOT_FOUND).entity("file "+filename+" not found").build();
		}else{
			response = Response.ok(fileInputStream, MediaType.APPLICATION_OCTET_STREAM).build();
		}		
		client.disconnect();
		return response;
	}
	
	
	public FTPClient connectToFTP() throws IOException{
		FTPClient client = new FTPClient(); 
		client.connect(server,2121);
		client.login(username,password);
		return client;
	}

	/**
	 * delete a file on the distant FTP
	 * @param filename is the name of the file to delete
	 * @return
	 * @throws IOException 
	 */
	public Response deleteFile(String filename) throws IOException {
		FTPClient client = connectToFTP();
		Response response;
		if(client.deleteFile(filename)){
			response = Response.ok(filename+" has been deleted successfully").build();
		}
		else{
			response = Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Unable to delete file "+filename).build();
		}
		return response;
	}

	/**
	 * upload the file 
	 * @param dirname
	 * @param link
	 * @return
	 * @throws IOException 
	 */
	public Response upload(String dirname, InputStream fileInputStream, String filename) throws IOException {
		FTPClient client = connectToFTP();
		boolean directoryFound = true;
		if(dirname != null){
			directoryFound = client.changeWorkingDirectory(dirname);
		}
		if(!directoryFound){
			client.disconnect();
			return Response.status(Response.Status.NOT_FOUND).entity("directory "+dirname+" not found").build();
		}
		Response response;
		client.setFileType(FTP.BINARY_FILE_TYPE);
		if(client.storeFile(filename, fileInputStream)){
			response = Response.status(Response.Status.CREATED).entity("File "+filename+" created in directory "+dirname).build();
		}
		else{
			response = Response.status(Response.Status.FORBIDDEN).entity("Not allowed to create "+filename+" in directory "+dirname).build();
		}
		return response;
	}
	

}

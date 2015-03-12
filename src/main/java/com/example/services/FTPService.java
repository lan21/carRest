package com.example.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.stereotype.Service;

/**
 * sert à faire le lien entre FTPRestService et le serveur FTP auquel on se connecte
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
	/**
	 * directory separator
	 */
	private static final String DS = "/";
	
	/**
	 * connects to an ftp and list all directories on the root of the ftp in 
	 * @return an array of FTPFile which contains all informations about all files present in this directory
	 * @throws SocketException
	 * @throws IOException
	 */
	public FTPFile[] listDirectoryJSON() throws SocketException, IOException{
		FTPClient client = connectToFTP();
		FTPFile[] files = client.listFiles();
		client.disconnect();
		return files;
	}
	
	/**
	 * list the root directory using a simple HMTL format
	 * @return a formated HTML string representing a listing of files of the root directory
	 * @throws SocketException
	 * @throws IOException
	 */
	public Response listRootDirectory() throws SocketException, IOException{
		return listDirectory("/");
	}
	
	/**
	 * List the directory specified by the pathname
	 * @param pathname
	 * @return a formated HTML string representing a listing of files in the directory specified by pahtname
	 * @throws SocketException
	 * @throws IOException
	 */
	public Response listDirectory(String pathname) throws SocketException, IOException{
		//System.out.println("requested path is "+pathname);
		FTPClient client = connectToFTP();
		FTPFile[] files;
		boolean directoryFound = true;
		if(pathname != null){
			directoryFound = client.changeWorkingDirectory(pathname);
		}
		if(!directoryFound){
			System.out.println("Directory not found");
			client.disconnect();
			return Response.status(Response.Status.NOT_FOUND).entity("directory "+pathname+" not found<br>\n").build();
		}
		files = client.listFiles();
		
		String listFile = "";
		for (FTPFile file : files){
			if(file.isDirectory()){
				listFile = listFile + formatFolderNameHTML(file.getName(),pathname) +"<br/>";
			}
			else{

				listFile = listFile + formatFileNameHTML(file.getName(),pathname) +"<br/>";
			}
		}
		client.disconnect();
		return Response.ok(listFile,MediaType.TEXT_HTML).build();
	}
	
	/**
	 * add a link on folder name so it becomes accessible by navigation
	 * @param folderName
	 * @return
	 */
	private String formatFolderNameHTML(String folderName,String workingDirectoryName){
		System.out.println("pwd:"+workingDirectoryName);
		String intoHTML = "";
		intoHTML += "<a href=\"";
		if(workingDirectoryName.equals("/")){
			intoHTML += "home/";
		}
		else if(!workingDirectoryName.endsWith("/")){
			intoHTML += workingDirectoryName+"/";
		}
		intoHTML += folderName+DS+"\">";
		intoHTML += folderName+DS;
		intoHTML += "</a>";
		return intoHTML;
	}
	
	/**
	 * add a link on a file name so it becomes accessible by navigation
	 * @param fileName
	 * @return
	 */
	private String formatFileNameHTML(String fileName,String workingDirectoryName){
		String intoHTML = "";
		intoHTML += "<a href=\"";
		if(workingDirectoryName.equals("")){
			intoHTML += "home/";
		}
		else if(!workingDirectoryName.endsWith("/")){
			intoHTML += workingDirectoryName+"/";
		}
		intoHTML += "file"+DS;
		intoHTML += fileName+"\">";
		intoHTML += fileName;
		intoHTML += "</a>";
		return intoHTML;
	}

	/**
	 * return the requested file as a stream
	 * @param folderName
	 * @return a response corresponding to the 
	 * @throws IOException 
	 */
	public Response getFile(String filename) throws IOException {
		System.out.println("requested file is "+filename);
		FTPClient client = connectToFTP();
		Response response;
		InputStream is = client.retrieveFileStream(filename);
		//il faut traiter si c'est un dossier
		if(is == null){
			System.out.println("not found");
			response = Response.status(Response.Status.NOT_FOUND).entity("file "+filename+" not found<br>").build();
		}else{
			response = Response.ok(is, MediaType.APPLICATION_OCTET_STREAM).build();
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
	public Response upload(String dirname, InputStream fileInputStream) throws IOException {
		FTPClient client = connectToFTP();
		boolean directoryFound = true;
		if(dirname != null){
			directoryFound = client.changeWorkingDirectory(dirname);
		}
		if(!directoryFound){
			client.disconnect();
			return Response.status(Response.Status.NOT_FOUND).entity("directory "+dirname+" not found<br>\n").build();
		}
		Response response;
		String filename = "fileUploaded";
		if(client.storeFile(filename, fileInputStream)){
			response = Response.status(Response.Status.CREATED).build();
		}
		else{
			response = Response.status(Response.Status.FORBIDDEN).build();
		}
		return response;
	}
	

}

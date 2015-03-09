package com.example.services;

import java.io.IOException;
import java.net.SocketException;

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
	 * connects to an ftp and list all directories on the root of the ftp in 
	 * @return an array of FTPFile which contains all informations about all files present in this directory
	 * @throws SocketException
	 * @throws IOException
	 */
	public FTPFile[] listDirectoryJSON() throws SocketException, IOException{
		FTPClient client = new FTPClient(); //faut-il créer un nouveau client FTP à chaque requête? ou un seul et le mettre en attribut de cette classe
		client.connect(server,2121);
		client.login(username,password);
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
	public String listDirectory() throws SocketException, IOException{
		return listDirectory(null);
	}
	
	/**
	 * List the directory specified by the pathname
	 * @param pathname
	 * @return a formated HTML string representing a listing of files in the directory specified by pahtname
	 * @throws SocketException
	 * @throws IOException
	 */
	public String listDirectory(String pathname) throws SocketException, IOException{
		//System.out.println("requested path is "+pathname);
		FTPClient client = new FTPClient();
		client.connect(server,2121);
		client.login(username,password);
		FTPFile[] files;
		if(pathname != null){
			client.changeWorkingDirectory(pathname);
		}
		files = client.listFiles();
		
		String listFile = "";
		for (FTPFile file : files){
			if(file.isDirectory()){
				listFile = listFile + formatFolderNameHTML(file.getName(),client.printWorkingDirectory()) +"<br/>";
			}
			else{

				listFile = listFile + file.getName() +"<br/>";
			}
		}
		client.disconnect();
		return listFile;
	}
	
	/**
	 * add a link on folder name so it beome accessible by navigation
	 * @param folderName
	 * @return
	 */
	private String formatFolderNameHTML(String folderName,String workingDirectoryName){
		String intoHTML = "";
		intoHTML += "<a href=\"";
		if(!"/".equals(workingDirectoryName)){
			intoHTML += workingDirectoryName.substring(1)+"/";
		}
		else{
			intoHTML += "dir/";
		}
		intoHTML += folderName+"\">";
		intoHTML += folderName;
		intoHTML += "</a>";
		return intoHTML;
	}
	
	//Il faut aussi ajouter une méthode pour foramtter les fichiers en HTML

}

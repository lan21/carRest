package com.restapi.services;

import org.apache.commons.net.ftp.FTPFile;

public class HTMLGenerator {
	/**
	 * directory separator
	 */
	private static final String DS = "/";
	
	/**
	 * home link of the application
	 */
	private static final String HOME = "/rest/api/home/";
	
	
	/**
	 * add a link on folder name so it becomes accessible by navigation
	 * @param folderName
	 * @return
	 */
	private String formatFolderNameHTML(String folderName,String workingDirectoryName){
		System.out.println("pwd:"+workingDirectoryName);
		String intoHTML = "";
		intoHTML += "<a href=\"";
		if("".equals(folderName)){
			intoHTML += HOME + getParentPath(workingDirectoryName)+"\">";
			intoHTML += "..";
		}
		else{
			intoHTML += getAbsolutePath(workingDirectoryName);		
			intoHTML += folderName+DS+"\">";
			intoHTML += folderName+DS;
		}
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
		System.out.println("wdn=" + workingDirectoryName);
		intoHTML += getAbsolutePath(workingDirectoryName);
		intoHTML += "file"+DS;
		intoHTML += fileName+"\">";
		intoHTML += fileName;
		intoHTML += "</a>";
		return intoHTML;
	}
	
	private String formatFiles(FTPFile[] files,String pathname){
		String listFile = "<table>"
				+ "<thead>";
		listFile+= "<tr>"
					+ "<td>Name</td>"
					+ "<td>Size</td>"
					+ "<td>Last modified</td>"
				+  "</tr>"
				+ "</thead>"
				+ "<tbody>";
		listFile += "<td>"+formatFolderNameHTML("",pathname) +"</td>";
		listFile += "<td></td>";
		listFile += "<td></td>";
		for (FTPFile file : files){
			listFile += "<tr>";
			if(file.isDirectory()){
				listFile += "<td>"+formatFolderNameHTML(file.getName(),pathname) +"</td>";
				listFile += "<td></td>";
				listFile += "<td>"+file.getTimestamp().getTime() +"</td>";
			}
			else{
				listFile += "<td>"+ formatFileNameHTML(file.getName(),pathname) +"</td>";
				listFile += "<td>"+file.getSize()+"K</td>";
				listFile += "<td>"+file.getTimestamp().getTime() +"</td>";
			}
			listFile += "</tr>";
		}
		listFile += "</tbody>"
				+ "</table>";
		return listFile;
	}
	
	/**
	 * generate an HTML pages which list the directory pathname given as parameters the files given in parameters an
	 * @param files are the files within the directory
	 * @param pathname is the path of the directory to be listed
	 * @return an HTML formated string representing the directory
	 */
	public String HTMLPage(FTPFile[] files,String pathname){
		String HTML = "";
		HTML+="<html><body>";
		HTML += "<h1>Index of "+pathname+"</h1>";
		HTML += formatFiles(files, pathname);
		HTML += createUploadForm(pathname);
		HTML+="</body></html>";		
		return HTML;
	}
	
	/**
	 * creates a form to upload a file in the pathname directory
	 * @param pathname path of the directory where the uploaded file will be added
	 * @return
	 */
	public String createUploadForm(String pathname) {
		String uploadform="<form action =\"";
		uploadform += getAbsolutePath(pathname);
		uploadform += "\" method=\"post\" enctype=\"multipart/form-data\">";
		uploadform += "<input type=\"file\" name=\"file\"/>";
		uploadform += "<input type=\"submit\" value=\"Upload file\" />";
		uploadform += "</form>";
		return uploadform;
	}

	private String getParentPath(String pathname){
		String parent="";
		String[] pathIntoArray = pathname.split("/");
		for (int i = 0; i < pathIntoArray.length-1; i++) {
			parent = pathIntoArray[i]+"/";			
		}
		return parent;
	}
	
	private String getAbsolutePath(String pathname){
		if("/".equals(pathname)){
			return HOME;
		}
		if(!pathname.endsWith("/")){
			return HOME+pathname+"/";
		}
		return HOME+pathname;
	}
}

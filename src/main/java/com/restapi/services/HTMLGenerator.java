package com.restapi.services;

import org.apache.commons.net.ftp.FTPFile;

public class HTMLGenerator {
	/**
	 * directory separator
	 */
	private static final String DS = "/";
	
	private static final String HOME = "/rest/api/home/";
	
	
	/**
	 * add a link on folder name so it becomes accessible by navigation
	 * @param folderName
	 * @return
	 */
	public String formatFolderNameHTML(String folderName,String workingDirectoryName){
		System.out.println("pwd:"+workingDirectoryName);
		String intoHTML = "";
		intoHTML += "<a href=\"";
		if("".equals(folderName)){
			intoHTML += HOME + getParentPath(workingDirectoryName)+"\">";
			intoHTML += "..";
		}
		else{
			if("/".equals(workingDirectoryName)){
				intoHTML += HOME;
			}
			else if(!workingDirectoryName.endsWith("/")){
				intoHTML += HOME+workingDirectoryName+"/";
			}
			else{
				intoHTML += HOME+workingDirectoryName;
			}			
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
	public String formatFileNameHTML(String fileName,String workingDirectoryName){
		String intoHTML = "";
		intoHTML += "<a href=\"";
		System.out.println("wdn=" + workingDirectoryName);
		if("/".equals(workingDirectoryName)){
			intoHTML += HOME;
		}
		else if(!workingDirectoryName.endsWith("/")){
			intoHTML += HOME+workingDirectoryName+"/";
		}
		else{
			intoHTML += HOME+workingDirectoryName;
		}
		intoHTML += "file"+DS;
		intoHTML += fileName+"\">";
		intoHTML += fileName;
		intoHTML += "</a>";
		return intoHTML;
	}
	
	public String formatFiles(FTPFile[] files,String pathname){
		String listFile = "<table>"
				+ "<thead>";
		listFile+= "<tr>"
					+ "<td>Name</td>"
					+ "<td>Size</td>"
					+ "<td>Last modified</td>"
				+  "</tr>"
				+ "</thead>"
				+ "<tbody>";
		listFile += "<td>"+formatFolderNameHTML("",pathname) +"<td/>";
		listFile += "<td><td/>";
		listFile += "<td><td/>";
		for (FTPFile file : files){
			listFile += "<tr>";
			if(file.isDirectory()){
				listFile += "<td>"+formatFolderNameHTML(file.getName(),pathname) +"<td/>";
				listFile += "<td><td/>";
				listFile += "<td>"+file.getTimestamp().getTime() +"<td/>";
			}
			else{
				listFile += "<td>"+ formatFileNameHTML(file.getName(),pathname) +"<td/>";
				listFile += "<td>"+file.getSize()+"K<td/>";
				listFile += "<td>"+file.getTimestamp().getTime() +"<td/>";
			}
			listFile += "</tr>";
		}
		listFile += "</tbody>"
				+ "</table>";
		return listFile;
	}
	
	public String HTMLPage(FTPFile[] files,String pathname){
		String HTML = "";
		HTML+="<html><body>";
		HTML += formatFiles(files, pathname);
		HTML += createUploadForm(pathname);
		HTML+="</body></html>";		
		return HTML;
	}
	
	public String createUploadForm(String pathname) {
		String uploadform="<form action =\"";
		if("/".equals(pathname)){
			uploadform += HOME;
		}
		else if(!pathname.endsWith("/")){
			uploadform += HOME+pathname+"/";
		}
		else{
			uploadform += HOME+pathname;
		}
		uploadform += "\" method=\"post\" enctype=\"multipart/form-data\">";
		uploadform += "<input type=\"file\" name=\"file\"/>";
		uploadform += "<input type=\"submit\" value=\"Upload file\" />";
		uploadform += "</form>";
		return uploadform;
	}

	public String getParentPath(String pathname){
		String parent="";
		String[] pathIntoArray = pathname.split("/");
		for (int i = 0; i < pathIntoArray.length-1; i++) {
			parent = pathIntoArray[i]+"/";			
		}
		return parent;
	}
}

package com.example.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * is representing all informations about a file : its name, path, filetype(directory or file), the rights on the file, the date of creation, its size
 * @author Allan Rakotoarivony, Tanguy Maréchal
 *
 */
public class FileInfo {
	public String name;
	public String path;
	public String fileType;
	public int chmod;
	public int size;
	//public Date creation;
	
	public FileInfo(File file) throws IOException{
		PosixFileAttributes attr = Files.readAttributes(
				Paths.get(file.getAbsolutePath()),
				PosixFileAttributes.class);
		char dir = '-';
		if (attr.isDirectory())
			dir = 'd';
		if (attr.isSymbolicLink())
			dir = 'l';
		String chmod = PosixFilePermissions.toString(Files.getPosixFilePermissions(file.toPath()));
		String owner = attr.owner().getName();
		String group = attr.group().getName();
		long size = file.length();
		String lastModif = new SimpleDateFormat("MMM dd HH:mm")
				.format(new Date(file.lastModified()));
		String filename = file.getName();

		String fileInfo = String.format("%c%s %s %s %6d %s %s\015\012",dir,chmod,owner,group,size,lastModif,filename);
	}
}

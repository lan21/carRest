package com.restapi.exception;

/**
 * is throwed when the ftpService tries to access to a directory that doesn't exist
 * @author rakotoarivony
 *
 */
public class DirectoryNotFoundException extends Exception {

	public DirectoryNotFoundException(String string) {
		super(string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


}

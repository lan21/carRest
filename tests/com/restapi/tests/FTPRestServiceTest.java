package com.restapi.tests;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

public class FTPRestServiceTest {

	@Test
	public void testListDirectory() throws ClientProtocolException, IOException {
		HttpClient client = HttpClientBuilder.create().build();

		HttpGet request = new HttpGet(
				"http://localhost:8080/rest/api/home/truc/");
		request.addHeader("Accept", MediaType.TEXT_HTML);
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));

		assertEquals("directory " + "truc/" + " not found", rd.readLine());

		HttpGet request2 = new HttpGet(
				"http://localhost:8080/rest/api/home/dossier/test/");
		request2.addHeader("Accept", MediaType.TEXT_HTML);
		HttpResponse response2 = client.execute(request2);

		BufferedReader rd2 = new BufferedReader(new InputStreamReader(response2
				.getEntity().getContent()));
		String dossier = "<html><body><h1>Index of dossier/test/</h1><table><thead><tr><td>Name</td><td>Size</td><td>Last modified</td></tr></thead><tbody><td><a href=\"/rest/api/home/dossier/\">..</a></td><td></td><td></td><tr><td><a href=\"/rest/api/home/dossier/test/file/blai_test.txt\">blai_test.txt</a></td><td>23K</td><td>Thu Feb 19 16:25:00 CET 2015</td></tr><tr><td><a href=\"/rest/api/home/dossier/test/file/filePdfTest.pdf\">filePdfTest.pdf</a></td><td>153868K</td><td>Wed Mar 11 22:48:00 CET 2015</td></tr><tr><td><a href=\"/rest/api/home/dossier/test/file/ban_test.txt\">ban_test.txt</a></td><td>225K</td><td>Wed Mar 18 14:38:00 CET 2015</td></tr></tbody></table><form action =\"/rest/api/home/dossier/test/\" method=\"post\" enctype=\"multipart/form-data\"><input type=\"file\" name=\"file\"/><input type=\"submit\" value=\"Upload file\" /></form></body></html>";
		assertEquals(dossier, rd2.readLine());
	}

	@Test
	public void testGetFile() throws ClientProtocolException, IOException {

		HttpClient client = HttpClientBuilder.create().build();

		HttpGet request = new HttpGet(
				"http://localhost:8080/rest/api/home/dossier/test/file/blai_test.txt");
		HttpResponse response = client.execute(request);
		BufferedReader getBuf = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent()));

	
		BufferedReader refBuf = new BufferedReader(new FileReader(
				"ftpServer/ftpFolder/allan/dossier/test/blai_test.txt"));

		assertEquals(refBuf.readLine(), getBuf.readLine());
		refBuf.close();
	}

	@Test
	public void testPostAndDeleteFile() throws ClientProtocolException,
			IOException {
		/*
		 * idea about how to realize the test: retrieve a file from the
		 * ftpserver with a get (the get method has alreaby been tested right)
		 * send a delete command via the url verify via an assert that the
		 * directory on the ftpserver doesn't contain the file anymore => the
		 * delete command is tested
		 * 
		 * post the file onto the server via the url verify with an assert that
		 * the directory contains the file
		 */
		HttpClient client = HttpClientBuilder.create().build();

		/* post the file */
		HttpPost postRequest = new HttpPost(
				"http://localhost:8080/rest/api/home/dossier/test/");
		File file = new File(
				"/home/rakotoarivony/developpement/CAR/carRest/image_test.png");
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addBinaryBody("upfile", file, ContentType.DEFAULT_BINARY,
				"image_test.png");
		HttpEntity entity = builder.build();
		postRequest.setEntity(entity);
		HttpResponse postResponse = client.execute(postRequest);
		assertEquals("HTTP/1.1 201 Created", postResponse.getStatusLine()
				.toString());

		// delete the file
		HttpDelete deleteRequest = new HttpDelete(
				"http://localhost:8080/rest/api/home/dossier/test/file/image_test.png");
		HttpResponse deleteResponse = client.execute(deleteRequest);
		assertEquals("HTTP/1.1 200 OK", deleteResponse.getStatusLine()
				.toString());

	}

}
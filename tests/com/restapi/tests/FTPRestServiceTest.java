package com.restapi.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import com.restapi.restService.FTPRestService;

public class FTPRestServiceTest {


	@Test
	public void testListRoot() throws ClientProtocolException, IOException {
		String textReturned = "<a href=\"home/dossier/\">dossier/</a><br/><a href=\"file/blai.txt\">blai.txt</a><br/><a href=\"file/Fiche-dinscription_Sport_Co_RNS2015_vf.xls\">Fiche-dinscription_Sport_Co_RNS2015_vf.xls</a><br/>";
		HttpClient client = HttpClientBuilder.create().build();

		//FTPRestService s = new FTPRestService();

		HttpGet request = new HttpGet("http://localhost:8080/rest/api/home/");
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));

		assertEquals(textReturned, rd.readLine());
	}


	@Test
	public void testListDirectory() throws ClientProtocolException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		FTPRestService s = new FTPRestService();
		assertNotNull(s);

		HttpGet request = new HttpGet("http://localhost:8080/rest/api/home/truc/");
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));

		assertEquals("directory "+"truc/"+" not found<br>", rd.readLine());

		HttpGet request2 = new HttpGet("http://localhost:8080/rest/api/home/dossier/");
		HttpResponse response2 = client.execute(request2);
		BufferedReader rd2 = new BufferedReader (new InputStreamReader(response2.getEntity().getContent()));
		String dossier = "<a href=\"file/blai.txt\">blai.txt</a><br/><a href=\"testNew/\">testNew/</a><br/><a href=\"file/sqf.txt\">sqf.txt</a><br/>";
		assertEquals(dossier, rd2.readLine());
	}

	@Test
	public void testGetFile() throws ClientProtocolException, IOException {

		HttpClient client = HttpClientBuilder.create().build();
		//FTPRestService s = new FTPRestService();

		HttpGet request = new HttpGet("http://localhost:8080/rest/api/home/dossier/file/blai.txt");
		HttpResponse response = client.execute(request);
		BufferedReader getBuf = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		//BufferedReader refBuf = new BufferedReader(new FileReader("/home/raynak/Universite/M1S2/CAR/FTP/carftp/ftpFolder/allan/dossier/blai.txt")); // chez tanguy
		BufferedReader refBuf = new BufferedReader(new FileReader("/home/rakotoarivony/developpement/CAR/tp1/ftpFolder/allan/dossier/blai.txt")); // chez allan
		

		assertEquals(refBuf.readLine(), getBuf.readLine());
		refBuf.close();
	}

	@Test
	public void testPostAndDeleteFile() throws ClientProtocolException, IOException {
		/* idea about how to realize the test:
		 * retrieve a file from the ftpserver with a get (the get method has alreaby been tested right)
		 * send a delete command via the url
		 * verify via an assert that the directory on the ftpserver doesn't contain the file anymore 
		 * => the delete command is tested
		 * 
		 * post the file onto the server via the url
		 * verify with an assert that the directory contains the file
		 * */
		HttpClient client = HttpClientBuilder.create().build();
//		FTPRestService s = new FTPRestService();
		/*retrieve the file*/
		HttpGet request = new HttpGet("http://localhost:8080/rest/api/home/dossier/file/TestPostAndDelete.txt");
		HttpResponse response = client.execute(request);
		/*delete the file*/
		HttpDelete deleteRequest = new HttpDelete("http://localhost:8080/rest/api/home/dossier/file/TestPostAndDelete.txt");
		HttpResponse deleteResponse = client.execute(deleteRequest);
		
		/*verify the directory content*/
		HttpGet listAfterDeleteRequest = new HttpGet("http://localhost:8080/rest/api/home/dossier/");
		HttpResponse listAfterDeleteResponse = client.execute(listAfterDeleteRequest);
		BufferedReader rd2 = new BufferedReader (new InputStreamReader(listAfterDeleteResponse.getEntity().getContent()));
		String dossier = "<a href=\"file/blai.txt\">blai.txt</a><br/><a href=\"testNew/\">testNew/</a><br/><a href=\"file/sqf.txt\">sqf.txt</a><br/>";
		assertEquals(dossier, rd2.readLine());
	
		/*post the file*/
		HttpPost postRequest = new HttpPost("http://localhost:8080/rest/api/home/dossier/TestPostAndDelete.txt");
		HttpResponse postResponse = client.execute(postRequest);
		BufferedReader rdpost = new BufferedReader (new InputStreamReader(postResponse.getEntity().getContent()));
		String postDirectory = "<a href=\"file/blai.txt\">blai.txt</a><br/><a href=\"testNew/\">testNew/</a><br/><a href=\"file/sqf.txt\">sqf.txt</a><br/><a href=\"file/TestPostAndDelete.txt\">TestPostAndDelete.txt</a><br/>";
		assertEquals(postDirectory, rdpost.readLine());
	
		
		}


//	private static List<String> fileToLines(String filename) {
//		List<String> lines = new LinkedList<String>();
//		String line = "";
//		try {
//			BufferedReader in = new BufferedReader(new FileReader(filename));
//			while ((line = in.readLine()) != null) {
//				lines.add(line);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return lines;
//	}

}
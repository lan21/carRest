package com.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.Response;

import difflib.DiffUtils;

import org.junit.Before;
import org.junit.Test;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.example.rs.FTPRestService;

public class FTPRestServiceTest {


	@Test
	public void testListRoot() throws ClientProtocolException, IOException {
		String textReturned = "<a href=\"home/dossier/\">dossier/</a><br/><a href=\"file/blai.txt\">blai.txt</a><br/><a href=\"file/Fiche-dinscription_Sport_Co_RNS2015_vf.xls\">Fiche-dinscription_Sport_Co_RNS2015_vf.xls</a><br/>";
		HttpClient client = HttpClientBuilder.create().build();

		FTPRestService s = new FTPRestService();

		HttpGet request = new HttpGet("http://localhost:8080/ftp/api/home/");
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));

		assertEquals(textReturned, rd.readLine());
	}


	@Test
	public void testListDirectory() throws ClientProtocolException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		FTPRestService s = new FTPRestService();
		assertNotNull(s);

		HttpGet request = new HttpGet("http://localhost:8080/ftp/api/home/truc/");
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));

		assertEquals("directory "+"truc/"+" not found<br>", rd.readLine());

		HttpGet request2 = new HttpGet("http://localhost:8080/ftp/api/home/dossier/");
		HttpResponse response2 = client.execute(request2);
		BufferedReader rd2 = new BufferedReader (new InputStreamReader(response2.getEntity().getContent()));
		String dossier = "<a href=\"file/blai.txt\">blai.txt</a><br/><a href=\"testNew/\">testNew/</a><br/><a href=\"file/sqf.txt\">sqf.txt</a><br/>";
		assertEquals(dossier, rd2.readLine());
	}

	@Test
	public void testGetFile() throws ClientProtocolException, IOException {

		HttpClient client = HttpClientBuilder.create().build();
		FTPRestService s = new FTPRestService();

		HttpGet request = new HttpGet("http://localhost:8080/ftp/api/home/dossier/file/blai.txt");
		HttpResponse response = client.execute(request);
		BufferedReader getBuf = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		BufferedReader refBuf = new BufferedReader(new FileReader("/home/raynak/Universite/M1S2/CAR/FTP/carftp/ftpFolder/allan/dossier/blai.txt"));

		assertEquals(refBuf.readLine(), getBuf.readLine());
	}

	@Test
	public void testPostAndDeleteFile() {
		fail("Not yet implemented");
	}


	private static List<String> fileToLines(String filename) {
		List<String> lines = new LinkedList<String>();
		String line = "";
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			while ((line = in.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}

}
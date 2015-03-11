package com.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.example.rs.FTPRestService;

public class FTPRestServiceTest {

	private FTPRestService service;
	@Before
	public void initialize(){
		service	= new FTPRestService();}
	
	

	@Test
	public void testListRoot() {
		fail("Not yet implemented");
	}

	@Test
	public void testListDirectory() {
		System.out.println("youpi");
		System.out.println("entite "+service.listRoot());
		String textReturned = "<a href=\"dossier/\">dossier/</a><br/><a href=\"file/blai.txt\">blai.txt</a><br/><a href=\"file/Fiche-dinscription_Sport_Co_RNS2015_vf.xls\">Fiche-dinscription_Sport_Co_RNS2015_vf.xls</a><br/>";
		assertNotNull(service);
		
		assertEquals(textReturned, service.listDirectory("/").getEntity().toString());
		assertEquals("directory "+"/truc"+" not found<br>\n", service.listDirectory("/truc").getEntity().toString());

	}

	@Test
	public void testGetFile() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteFile() {
		fail("Not yet implemented");
	}

}
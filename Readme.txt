Tp Rest
Allan Rakotoarivony
Tanguy Maréchal
18 mars 2015


*** 1/ Introduction

Cette application a pour but d'accéder au serveur FTP développé lors du premier TP via une passerelle rest accessible par navigateur ou par ligne de commande via le terminal (curl)

L'application implémente les commandes LIST, RETRIEVE, PUT et DELETE

Les url sont les suivantes :
LIST : http://localhost:8080/ftp/api/home/ => liste la racine
       http://localhost:8080/ftp/api/home/dossier/ =>	liste le contenu du dossier

Une fois la liste afficher, un clic sur un fichier lance le download

Un formulaire permet le dépot ou la suppression d'un fichier sur le serveur

*** 2/ Architecture

Classe FTPService 
Classe FTPRestService
Classe Starter
Classe AppConfig
Classe HtmlGenerator

Catch:
try {
	String filename = file.getRootAttachment().getDataHandler().getName();
	InputStream fileStream = file.getRootAttachment().getDataHandler().getInputStream();
	return ftpService.upload(dirname,fileStream,filename);
	} catch (IOException e) {
		return Response.status(Response.Status.BAD_GATEWAY).entity("Connexion non disponible").build();
	}
}

Throw:
SocketException
IOException



******** 3/ Parcours du code (code samples) **************
**********************************************************

Méthode qui permet au service de lister le contenu du répertoire dont le nom est passé dans l'URL.
Elle retourne la réponse du service ftp.

@GET
@Produces({MediaType.TEXT_HTML})
@Path("/{dirname: .*/}")
public Response listDirectory( @PathParam("dirname") String dirname){
	try {
		return ftpService.listDirectory(dirname);
	} catch (SocketException e) {
	} catch (IOException e) {
	}
	return Response.status(Response.Status.NOT_FOUND).entity("dossier "+dirname+" non trouvé").build();
}

******

Méthode qui permet de télécharger le fichier passé en paramètre de l'url
Le type Mime est APPLICATION_OCTET_STREAM : cela permet le téléchargement de tout type de fichier (texte, image,...) en les lisant sous forme d'octets.
Le fichier est téléchargé dans le dossier de l'utilisateur.

@GET
	@Produces({MediaType.APPLICATION_OCTET_STREAM})
	/**
	 * 
	 * @return a Response which contains the HTTP code answer
	 * formatted HTML which represents the files in this directory
	 */
	@Path("/{dirname: .*}file/{filename: .*}") //il y a un regex là :D
	public Response getFile( @PathParam("dirname") String dirname ,@PathParam("filename") String filename){
		try {
			if(dirname == null)dirname = "";
			if(filename == null)filename = "";
			return ftpService.getFile(dirname+filename);
		} catch (SocketException e) {
		} catch (IOException e) {
		}
		return Response.status(Response.Status.NOT_FOUND).entity("fichier "+filename+" non trouvé").build();
	}

******

Méthode en lien avec la méthode précédente.
Retourne la réponse de l'interrogation du serveur ftp par la commande retrieve
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
		client.setFileType(FTP.BINARY_FILE_TYPE);
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
	
*******

Méthode qui permet la suppression d'un fichier sur le serveur ftp

@DELETE
	@Path("/{dirname: .*}file/{filename: .*}")
	public Response deleteFile(@PathParam("dirname") String dirname ,@PathParam("filename") String filename){
		try {
			if(dirname == null)dirname = "";
			if(filename == null)filename = "";
			return ftpService.deleteFile(dirname+filename);
		} catch (IOException e) {
		}
		return Response.status(Response.Status.BAD_GATEWAY).entity("Connexion non disponible").build();
	}


*******

Méthode qui permet le dépot d'un fichier sur le serveur ftp
	@POST
	@Path("/{dirname: .*/}")
	@Consumes({MediaType.MULTIPART_FORM_DATA})
	@Produces({MediaType.TEXT_HTML})	
	public Response postFile(@PathParam("dirname") String dirname,@Multipart("file") MultipartBody file){
		try {
			String filename = file.getRootAttachment().getDataHandler().getName();
			InputStream fileStream = file.getRootAttachment().getDataHandler().getInputStream();
			return ftpService.upload(dirname,fileStream,filename);
		} catch (IOException e) {
			return Response.status(Response.Status.BAD_GATEWAY).entity("Connexion non disponible").build();
		}
	}
	


******************************************************

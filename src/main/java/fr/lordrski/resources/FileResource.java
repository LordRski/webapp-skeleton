/**
 * This file is part of webapp-skeleton.
 *
 * webapp-skeleton is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * webapp-skeleton is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.				 
 * 
 * You should have received a copy of the GNU General Public License
 * along with webapp-skeleton.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Edouard CATTEZ <edouard.cattez@sfr.fr> (La 7 Production)
 */
package fr.lordrski.resources;

import java.io.File;
import java.nio.file.Files;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import fr.lordrski.mvc.Model;
import fr.lordrski.tool.FileTool;
import fr.lordrski.tool.StringTool;
import fr.lordrski.util.Folder;

/**
* Ressource associée aux fichiers
*/
@Singleton
@Path("files")
public class FileResource {
	
	/**
	 * Télécharge un fichier depuis le serveur vers le client
	 * 
	 * @param folder le dossier qui contient le fichier à télécharger
	 * @param filename le nom du fichier à télécharger
	 * @return La réponse avec le fichier dans l'entête ou NOT FOUND
	 */
	@GET
	@Path("download/{folder}/{filename}")
	public Response download(@PathParam("folder") String folder, @PathParam("filename") String filename) {
		java.nio.file.Path path = Folder.EXCHANGE.resolve(folder).resolve(filename);
		if (Files.exists(path)) {
			return Response.ok(path.toFile()).header("Content-Disposition", "attachment; filename=\"" + filename + "\"").build();			
		}
		throw new NotFoundException();
	}
	
	/**
	 * Télécharge un fichier depuis le serveur vers le client
	 * 
	 * @param filename le nom du fichier à télécharger
	 * @return La réponse avec le fichier dans l'entête ou NOT FOUND
	 */
	@GET
	@Path("download/{filename}")
	public Response download(@PathParam("filename") String filename) {
		return download(null, filename);
	}
	
	/**
	 * Télécharge un ou plusieurs fichiers depuis le client vers le serveur
	 * 
	 * @param multiPart L'objet qui contient toutes les informations du formulaire de type multipart
	 * @param folder le dossier de destination
	 * @return La réponse avec la liste des fichiers et leur état succès ou échec de téléchargement
	 */
	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response upload(FormDataMultiPart multiPart, @FormDataParam("folder") String folder) {
		java.nio.file.Path path = Folder.EXCHANGE.resolve(folder);
		Model model = new Model();

		path.toFile().mkdirs();
		multiPart.getFields().values().stream().forEach(
			fields -> fields.stream().filter(field -> !StringTool.isEmpty(field.getFormDataContentDisposition().getFileName())).forEach(
				field -> {
					String filename = field.getFormDataContentDisposition().getFileName();
					boolean result = FileTool.upload(field.getValueAs(File.class), path.resolve(filename));
					model.put(filename, result);
				}
			)
		);
		return Response.ok(model).build();
	}
	
}

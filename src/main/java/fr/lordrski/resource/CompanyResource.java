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
package fr.lordrski.resource;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import fr.lordrski.dao.CompanyDao;
import fr.lordrski.entity.deprecated.Company;
import fr.lordrski.util.sql.DaoProvider;

/**
 * Service associé à l'entité {@link fr.lordrski.entity.deprecated.Company}
 */
@Path("companies")
public class CompanyResource {
	
	@Context
	private UriInfo uriInfo;
	
	private CompanyDao companyDao;

	public CompanyResource() {
		this.companyDao = DaoProvider.getDao(Company.class);
	}

	@POST
	public Response createCompany(Company company) throws SQLException {
		if (companyDao.find(company.getCompanyId()) != null) {
			return Response.status(Response.Status.CONFLICT).build();
		}
		else {
			companyDao.insert(company);
			URI instanceURI = uriInfo.getAbsolutePathBuilder().path(company.getCompanyId()).build();
			return Response.created(instanceURI).build();
		}
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public List<Company> getCompanies() throws SQLException {
		return companyDao.findAll();
	}

	@GET
	@Path("{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Company getCompany(@PathParam("id") String id) throws SQLException {
		Company company = companyDao.find(id);
		if (company == null) {
			throw new NotFoundException();
		}
		return company;
	}

	@DELETE
	@Path("{id}")
	public Response deleteCompany(@PathParam("id") String id) throws SQLException {
		Company company = companyDao.find(id);
		if (company == null) {
			throw new NotFoundException();
		}
		else {
			companyDao.deleteById(id);
			return Response.status(Response.Status.NO_CONTENT).build();
		}
	}

	@PUT
	@Path("{id}")
	public Response updateCompany(@PathParam("id") String id, Company company) throws SQLException {
		if (companyDao.find(id) == null) {
			throw new NotFoundException();
		}
		else {
			companyDao.update(company);
			return Response.status(Response.Status.NO_CONTENT).build();
		}
	}

}

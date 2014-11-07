/************************************************************************
 * Copyright (c) 2014 IoT-Solutions e.U.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ************************************************************************/

package iot.jcypher.samples.domain.people.graph_access;

import iot.jcypher.domain.IDomainAccess;
import iot.jcypher.domain.SyncInfo;
import iot.jcypher.query.result.JcError;
import iot.jcypher.query.result.JcResultException;
import iot.jcypher.samples.domain.people.model.Address;
import iot.jcypher.samples.domain.people.model.Area;
import iot.jcypher.samples.domain.people.model.AreaType;
import iot.jcypher.samples.domain.people.model.Gender;
import iot.jcypher.samples.domain.people.model.Person;
import iot.jcypher.samples.domain.people.util.Util;

import java.util.ArrayList;
import java.util.List;

public class DomainPopulator {

	private Population population;
	
	public DomainPopulator() {
		super();
		this.population = new Population();
	}

	/**
	 * store the object graph represented by a list of root objects to the database
	 * @param domainObjects
	 * @return a list of object ids (i.e. the ids of the nodes in the graph to which the root objects were mapped).
	 */
	public List<Long> populateDomain(List<Object> domainObjects) {
		List<Long> ids = new ArrayList<Long>();
		List<JcError> errors;
		IDomainAccess domainAccess = Config.createDomainAccess();
		
		// initially clear the database
		errors = Config.getDBAccess().clearDatabase();
		if (errors.size() > 0) {
			Util.printErrors(errors);
			throw new JcResultException(errors);
		}
		
		// store the graph of domain objects to graph database
		errors = domainAccess.store(domainObjects);
		if (errors.size() > 0) {
			Util.printErrors(errors);
			throw new JcResultException(errors);
		}
		
		// retrieve the syncInfo for each domain object in the list in order to get each
		// objects id (i.e. the id of the node in the graph to which the object was mapped).
		// we need the ids to later load the objects from the graph database.
		List<SyncInfo> syncInfos = domainAccess.getSyncInfos(domainObjects);
		for (SyncInfo syncInfo : syncInfos) {
			ids.add(syncInfo.getId());
		}
		
		return ids;
	}
	
	/**
	 * create the population,
	 * @return a list of root objects of the created object graph.
	 */
	public List<Object> createDomainPopulationPopulation() {
		List<Object> domainObjects = new ArrayList<Object>();
		
		this.population.createPlaces();
		this.population.createSmithFamily(domainObjects);
		this.population.createBerghammers(domainObjects);
		this.population.createMore(domainObjects);
		
		return domainObjects;
	}
	
	/*****************************************/
	private class Population {
		
		private Area earth;
		private Area northAmerica;
		private Area usa;
		private Area california;
		private Area sanFrancisco;
		private Area europe;
		private Area germany;
		private Area munic;
		private Area newYork;
		private Area newYorkCity;
		private Area austria;
		private Area vienna;
		private Area vienna_17;

		private void createPlaces() {
			earth = new Area(null, "Earth", AreaType.PLANET);
			northAmerica = new Area(null, "North America", AreaType.CONTINENT);
			northAmerica.setPartOf(earth);
			usa = new Area("1", "USA", AreaType.COUNTRY);
			usa.setPartOf(northAmerica);
			california = new Area(null, "California", AreaType.STATE);
			california.setPartOf(usa);
			sanFrancisco = new Area(null, "San Francisco", AreaType.CITY);
			sanFrancisco.setPartOf(california);
			europe = new Area(null, "Europe", AreaType.CONTINENT);
			europe.setPartOf(earth);
			germany = new Area("2", "Germany", AreaType.COUNTRY);
			germany.setPartOf(europe);
			munic = new Area(null, "Munic", AreaType.CITY);
			munic.setPartOf(germany);
			newYork = new Area(null, "New York", AreaType.STATE);
			newYork.setPartOf(usa);
			newYorkCity = new Area(null, "New York City", AreaType.CITY);
			newYorkCity.setPartOf(newYork);
			austria = new Area(null, "Austria", AreaType.COUNTRY);
			austria.setPartOf(europe);
			vienna = new Area("1", "Vienna", AreaType.CITY);
			vienna.setPartOf(austria);
			vienna_17 = new Area("1170", "Hernals", AreaType.URBAN_DISTRICT);
			vienna_17.setPartOf(vienna);
		}
		
		private void createSmithFamily(List<Object> domainObjects) {
			Address smith_address = new Address("Market Street", 20);
			smith_address.setArea(sanFrancisco);
			
			Person john_smith = new Person("John", "Smith", Gender.MALE);
			john_smith.getPointsOfContact().add(smith_address);
			Person caroline_smith = new Person("Caroline", "Smith", Gender.FEMALE);
			caroline_smith.getPointsOfContact().add(smith_address);
			Person angie_smith = new Person("Angelina", "Smith", Gender.FEMALE);
			angie_smith.getPointsOfContact().add(smith_address);
			angie_smith.setMother(caroline_smith);
			angie_smith.setFather(john_smith);
			Person jery_smith = new Person("Jeremy", "Smith", Gender.MALE);
			jery_smith.getPointsOfContact().add(smith_address);
			jery_smith.setMother(caroline_smith);
			jery_smith.setFather(john_smith);
			
			domainObjects.add(john_smith);
			domainObjects.add(caroline_smith);
			domainObjects.add(angie_smith);
			domainObjects.add(jery_smith);
		}

		private void createBerghammers(List<Object> domainObjects) {
			Address berghammer_address = new Address("Hochstrasse", 4);
			berghammer_address.setArea(munic);
			
			Person hans_berghammer = new Person("Hans", "Berghammer", Gender.MALE);
			hans_berghammer.getPointsOfContact().add(berghammer_address);
			Person gerda_berhammer = new Person("Gerda", "Berghammer", Gender.FEMALE);
			gerda_berhammer.getPointsOfContact().add(berghammer_address);
			Person christa_berhammer = new Person("Christa", "Berghammer", Gender.FEMALE);
			christa_berhammer.getPointsOfContact().add(berghammer_address);
			christa_berhammer.setMother(gerda_berhammer);
			christa_berhammer.setFather(hans_berghammer);
			
			domainObjects.add(hans_berghammer);
			domainObjects.add(gerda_berhammer);
			domainObjects.add(christa_berhammer);
		}
		
		private void createMore(List<Object> domainObjects) {
			Address watson_address = new Address("Broadway", 53);
			watson_address.setArea(newYorkCity);
			Person jim_watson = new Person("Jim", "Watson", Gender.MALE);
			jim_watson.getPointsOfContact().add(watson_address);
			
			Address clark_address = new Address("Pearl Street", 124);
			clark_address.setArea(newYorkCity);
			Person sarah_clark = new Person("Sarah", "Clark", Gender.FEMALE);
			sarah_clark.getPointsOfContact().add(clark_address);
			
			Address maier_address = new Address("Lackner Gasse", 12);
			maier_address.setArea(vienna_17);
			Person herbert_maier = new Person("Herbert", "Maier", Gender.MALE);
			herbert_maier.getPointsOfContact().add(maier_address);
			
			domainObjects.add(jim_watson);
			domainObjects.add(sarah_clark);
			domainObjects.add(herbert_maier);
		}
	}
}

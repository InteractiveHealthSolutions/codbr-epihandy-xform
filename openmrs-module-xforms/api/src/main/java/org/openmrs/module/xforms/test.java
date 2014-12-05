package org.openmrs.module.xforms;

import java.util.Date;

import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.User;
import org.openmrs.api.context.Context;

public class test {
public static void main(String[] args) {
	Obs obs = new Obs();
	obs.setConcept(new Concept(971));
	obs.setCreator(new User(1));
	obs.setEncounter(new Encounter(1));
	obs.setPerson(new Person(5));
	obs.setObsDatetime(new Date());
	Context.getObsService().saveObs(obs , null);
}
}

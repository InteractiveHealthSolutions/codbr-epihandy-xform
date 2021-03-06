/**
 * Java Epihandy - Mobile (J2ME) Data Collection tool
 * (c) 2008- Makerere University Faculty of Computing & IT,
 * This program is free software, distributed under the terms of the
 * Apache License, Version 2.0.
 * */


package org.fcitmuk.epihandy;

import java.util.Vector;


public class StudyDefTest {
	
	public StudyDefTest(){
		super();
	}
	
	public static StudyDef getTestStudyDef(boolean includeForms){
		StudyDef study = new StudyDef(FormDefTest.toByte(1),"Study 1", "Study1",null);
		
		if(includeForms){
			study.addForm(FormDefTest.getPatientFormDef());
			study.addForm(FormDefTest.getTBFormDef());
			study.addForm(FormDefTest.getTestFormDef());
		}
		else
			study.setForms(new Vector());
		
		return study;
	}
	
	public static StudyDef getTestStudyDef2(boolean includeForms){
		StudyDef study = new StudyDef(FormDefTest.toByte(2),"Study 2", "Study2",null);
		
		if(includeForms)
			study.addForm(FormDefTest.getTestFormDef2());	
		else
			study.setForms(new Vector());
		
		return study;
	}
}

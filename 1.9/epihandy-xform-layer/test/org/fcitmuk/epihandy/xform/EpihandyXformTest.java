package org.fcitmuk.epihandy.xform;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.fcitmuk.epihandy.FormData;

public class EpihandyXformTest extends TestCase{

	public void testConvertDataXmlToFormData() throws Exception {
		String dataXml = getFileContent(new File("test/resources/SampleForm-Data.xml"));
		String xform = getFileContent(new File("test/resources/SampleForm.xml"));
		FormData formData = EpihandyXform.fromXMLToFormData(xform, dataXml);
		Assert.assertNotNull(formData);
		Assert.assertEquals("1234", formData.getAnswer("/sample_study_sample_form_v1/patient_id"));
		Assert.assertEquals("Mrs", formData.getTextValue("/sample_study_sample_form_v1/title"));
		Assert.assertEquals("Dagmar", formData.getAnswer("/sample_study_sample_form_v1/first_name"));
		Assert.assertEquals("Timler", formData.getAnswer("/sample_study_sample_form_v1/last_name"));
		Assert.assertEquals("Female", formData.getTextValue("/sample_study_sample_form_v1/sex"));
		Assert.assertEquals("12-10-2011", formData.getTextValue("/sample_study_sample_form_v1/birth_date"));
		Assert.assertEquals("60", formData.getTextValue("/sample_study_sample_form_v1/weight_kg"));
		Assert.assertEquals("5", formData.getTextValue("/sample_study_sample_form_v1/height_ft"));
		Assert.assertEquals("AZT", formData.getTextValue("/sample_study_sample_form_v1/arvs"));
		Assert.assertEquals("2", formData.getTextValue("/sample_study_sample_form_v1/number_of_children"));
		Assert.assertEquals("clara,3,Female|ada,0,Female", formData.getTextValue("/sample_study_sample_form_v1/details_of_children"));
		Assert.assertEquals("Africa", formData.getTextValue("/sample_study_sample_form_v1/continent"));
		Assert.assertEquals("Uganda", formData.getTextValue("/sample_study_sample_form_v1/country"));
		Assert.assertEquals("Kampala", formData.getTextValue("/sample_study_sample_form_v1/district"));
		Assert.assertEquals("01:13:55 PM", formData.getTextValue("/sample_study_sample_form_v1/start_time"));
		Assert.assertEquals("01:14:58 PM", formData.getTextValue("/sample_study_sample_form_v1/endtime"));
	}
	
	public static String getFileContent(File file) throws IOException {
		FileReader in=new FileReader(file);
		StringWriter w= new StringWriter();
		char buffer[]=new char[2048];
		int n=0;
		while((n=in.read(buffer))!=-1) {
			w.write(buffer, 0, n);
		}
		w.flush();
		in.close();
		return w.toString();
	}
}

package org.ihs.codbr.constant;

public class WebServerConstants {

	public static final String PARAM_USERNAME = "uname";
	public static final String PARAM_PASSWORD = "pw";
	public static final String PARAM_DOWNLOAD_COHORT = "downloadCohorts";
	public static final String PARAM_SERIALIZER_KEY = "serializerKey";
	public static final String PARAM_DOWNLOAD_PATIENT = "downloadPatients";
	public static final String PARAM_COHORT_ID = "cohortId";
	public static final String PARAM_XFORM_TARGET = "target";
	public static final String PARAM_XFORM_FORM_ID = "formId";
	public static final String PARAM_XFORM_CONTENT_TYPE = "contentType";
	public static final String PARAM_XFORM_EXCLUDE_LAYOUT = "excludeLayout";
	public static final String PARAM_XFORM_BATCH_ENTRY = "batchEntry";

	
	public static final String SUBURL_XFORM_USER_DOWNLOAD = "/moduleServlet/xforms/userDownload?";
	public static final String SUBURL_XFORM_USER_VALIDATOR = "/moduleServlet/xforms/userValidator?";
	public static final String SUBURL_XFORM_COHORT_DOWNLOAD = "/module/xforms/patientDownload.form?";
	public static final String SUBURL_XFORM_PATIENT_DOWNLOAD = "/module/xforms/patientDownload.form?";
	public static final String SUBURL_XFORM_FORM_DOWNLOAD = "/moduleServlet/xforms/xformDownload?";
	public static final String SUBURL_XFORM_FORM_UPLOAD_XML = "/module/xforms/xformDataUpload.form?";
	public static final String SUBURL_XFORM_FORM_UPLOAD_MULTIPART = "/moduleServlet/xforms/xformDataUpload?";


}

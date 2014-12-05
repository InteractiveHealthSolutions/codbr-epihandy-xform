package org.ihs.codbr.forms;

import java.util.Vector;

public class HomeUIListItem {
	public static final Vector HOME_LIST_ITEMS = new Vector();

	//public static final HomeUIListItem COHORTS = new HomeUIListItem ("Cohorts", "", true);
	public static final HomeUIListItem XFORMS_DISPLAY = new HomeUIListItem ("Show Forms", "", true);
	public static final HomeUIListItem XFORMS_DOWNLOAD = new HomeUIListItem ("Download Xforms", "", true);
	public static final HomeUIListItem XFORMS_UPLOAD = new HomeUIListItem ("Upload Collected Forms", "", true);
	//public static final HomeUIListItem PATIENTS = new HomeUIListItem ("Patients", "", true);
	public static final HomeUIListItem PATIENT_SEARCH = new HomeUIListItem ("Patient Search", "", true);
	public static final HomeUIListItem CONNECTION_SETTING = new HomeUIListItem ("Connection Settings", "", true);
	public static final HomeUIListItem FORM_SETTING = new HomeUIListItem ("Form Settings", "", true);
	public static final HomeUIListItem LOG = new HomeUIListItem ("Log", "", true);

	//public static final HomeListItem	MISSING_CXR_RESULT		= new HomeListItem (1, "Pending CXR Result Form", FormType.Form_MISSING_CXR_RESULT, "Patients with pending Xray results", true);
	
	public int INDEX;
	public final String NAME;
	final boolean show;
	final String formDesc;

	public String getFormDesc ()
	{
		return formDesc;
	}

	private HomeUIListItem (String displayName, String formDesc, boolean show)
	{
		this.formDesc = formDesc;
		this.NAME = displayName;
		this.show = show;
		
		HOME_LIST_ITEMS.addElement(this);
	}

	public boolean isShown ()
	{
		return show;
	}

	/*
	 * public void setShown(boolean show) { this.show = show; }
	 */
}

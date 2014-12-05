package org.ihs.codbr.forms;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;

import org.fcitmuk.openmrs.OpenmrsDataStorage;
import org.fcitmuk.openmrs.Patient;
import org.fcitmuk.openmrs.PatientList;
import org.fcitmuk.openmrs.RequestHeader;
import org.ihs.codbr.MainMidlet;
import org.ihs.codbr.constant.UIConstants;
import org.ihs.codbr.constant.WebServerConstants;
import org.ihs.codbr.util.UIUtils;

public class PatientDetailsUIForm extends BaseUIDataView{
	private Command cmdExit;

	public PatientDetailsUIForm(String title, MainMidlet mainMidlet) {
		super(title, mainMidlet);
		cmdExit = new Command ("Back", Command.EXIT, 1);
	}
	
	protected void loadData (int patientId)
	{
		Patient stored = OpenmrsDataStorage.getPatient(patientId);
		if(stored != null){
			append(stored.toString());
			append("Gender : "+stored.getGender());
			append("Birthdate : "+stored.getBirthDate());
			
			
			/*for (int i = 0; i < dataitems.size(); i++) {
				Patient pat = ((Patient)dataitems.getPatient(i));
				append(pat.getName(), null);
			}*/
		}
	}
	
	public void init (Displayable previousDisplayable)
	{
		setPrevDisplayable(previousDisplayable);
		
		deleteAll ();
		loadData (0);

		addCommand (cmdExit);
		setCommandListener (this);
	}
	
	public void commandAction (Command c, Displayable d)
	{
		if (c == cmdExit)
		{
			getMainMidlet().setDisplay(getPrevDisplayable());
		}
	}

	protected void cleanup() {
		// TODO Auto-generated method stub
		
	}
}

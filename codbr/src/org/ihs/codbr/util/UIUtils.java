package org.ihs.codbr.util;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.Ticker;
import javax.microedition.midlet.MIDletStateChangeException;

import org.fcitmuk.util.MenuText;
import org.ihs.codbr.MainMidlet;
import org.ihs.codbr.constant.UIConstants;
import org.ihs.codbr.forms.BaseUIDataView;
import org.ihs.codbr.forms.BaseUIForm;
import org.ihs.codbr.forms.BaseUIList;

public class UIUtils {
	private static final Alert alert = new Alert("ALERT", "alert", null, AlertType.WARNING);
	private static final Alert popup = new Alert("ALERT", "popup", null, AlertType.WARNING);
	private static final Form mGaugeForm = new Form(UIConstants.TITLE_PROGRESS_BAR, new Item[]{new Gauge(UIConstants.TEXT_PROGRESS_BAR, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING)});

	public static void renderAlert (String msg, String imgName, Displayable previousDisplayable, Display display)
	{
		alert.setString(msg);
		alert.setImage(null);
		alert.setTimeout(Alert.FOREVER);

		display.setCurrent(alert, previousDisplayable);
	}
	
	public static void showPopup (String msg, String imgName, Display display)
	{
		popup.setString(msg);
		popup.setImage(null);
		popup.setTimeout (Alert.FOREVER);

		display.setCurrent(popup);
	}
	
	public static void showProgressBar (String msg, Display display)
	{
		Ticker t = new Ticker(msg);
		mGaugeForm.setTicker(t);
		
		display.setCurrent(mGaugeForm);
	}

	public static void showExitConfirmation(final Displayable currentDisplayable, final MainMidlet mainMidlet) {
		Alert alert = new Alert("Alert");
		alert.setString(UIConstants.TEXT_EXIT_CONFIRM);
		alert.addCommand(new Command(MenuText.YES(), Command.OK, 1));
		alert.addCommand(new Command(MenuText.NO(), Command.CANCEL, 1));
		alert.setCommandListener(new CommandListener() {
			public void commandAction(Command c, Displayable d) {
				if (c.getLabel().equals(MenuText.YES())) {
					try {
						mainMidlet.destroyApp(false);
						mainMidlet.notifyDestroyed();
					} catch (MIDletStateChangeException e) {
						e.printStackTrace();
					}
				}
				if (c.getLabel().equals(MenuText.NO())) {
					mainMidlet.setDisplay(currentDisplayable); // TODO
				}
			}
		});
		mainMidlet.setDisplay(alert);
	}
	
	public static void showConfirm(String message, final CommandListener okAction, final CommandListener cancelAction, Display display) {
		final Alert alert = new Alert("Confirm");
		alert.setString(message);
		alert.addCommand(new Command(MenuText.YES(), Command.OK, 1));
		alert.addCommand(new Command(MenuText.NO(), Command.CANCEL, 1));
		alert.setCommandListener(new CommandListener() {
			public void commandAction(Command c, Displayable d) {
				if (c.getLabel().equals(MenuText.YES())) {
					okAction.commandAction(c, d);
				}
				if (c.getLabel().equals(MenuText.NO())) {
					cancelAction.commandAction(c, d);
				}
			}
		});
		
		display.setCurrent(alert);
	}

	/*public void startMainMenu ()
	{
		mainList.init ();
		setDisplay (mainList);
	}*/
	public static void startForm(BaseUIDataView form, Displayable previousDisplayable, Display display)
	{
		form.init (previousDisplayable);
		display.setCurrent(form);
	}
	public static void startForm(BaseUIForm form, Displayable previousDisplayable, Display display)
	{
		form.init (previousDisplayable);
		display.setCurrent(form);
	}
	
	public static void startForm(BaseUIList list, Displayable previousDisplayable, Display display)
	{
		list.init (previousDisplayable);
		display.setCurrent(list);
	}
}

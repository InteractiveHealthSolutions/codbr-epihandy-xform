package org.ihs.logger;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import net.sf.microlog.core.Logger;
import net.sf.microlog.core.LoggerFactory;
import net.sf.microlog.core.format.PatternFormatter;
import net.sf.microlog.midp.appender.FormAppender;

public class Log {
	private static Log _instance;
	public final Form LOGGER_FORM;
	private final FormAppender appender;
	public final Logger LOGGER = LoggerFactory.getLogger("Logger");
	
	private Log(String title) {
		LOGGER_FORM = new Form(title);
		LOGGER_FORM.addCommand(new Command("Exit", Command.EXIT, 0));
		appender = new FormAppender(LOGGER_FORM);
		PatternFormatter formatter = new PatternFormatter();
		formatter.setPattern("[%P]-%m %T");
		appender.setFormatter(formatter );
		LOGGER.addAppender(appender);
	}
	
	public static void configureLogger(String title){
		_instance = new Log(title);
	}
	
	public static Logger LOGGER(){
		if(_instance == null){
			_instance = new Log("Logger Console");
		}
		return _instance.LOGGER;
	}
	
	public static void showLog(final Display display, final Displayable previousDisplayable){
		_instance.LOGGER_FORM.setCommandListener(new CommandListener() {
			public void commandAction(Command c, Displayable d) {
				if(c.getLabel().toLowerCase().compareTo("exit") == 0){
					display.setCurrent(previousDisplayable);
				}
			}
		});
		display.setCurrent(_instance.LOGGER_FORM);
		
	}
}

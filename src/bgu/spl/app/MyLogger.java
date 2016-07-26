package bgu.spl.app;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A singelton for a Log to print messages and save information
 * @author dotan & neta 
 *
 */
public class MyLogger {
	static Logger _logger;
	
	public static class LoggerHolder {
		private static MyLogger instance = new MyLogger();
	}
	
	private MyLogger () {
		_logger = Logger.getLogger("theLogger");
	}
	
	/**
	 * Returns the only one instance of the logger
	 * @return loggerInstance
	 */
	
	public static MyLogger getInstance() {
		return LoggerHolder.instance;
	}
	
	/**
	 * Log the message inserted with the level
	 * @param level
	 * @param msg
	 */
	
    public static void log(Level level, String msg){
    	_logger.log(level, msg);
    }
    
    /**
     * Set the level to the {@value}level given
     * @param level
     */
    public static void setLevel (Level level) {
		_logger.setLevel(level);
    }
    
    /**
     * 
     * @return loggerName
     */
    
    public static String getName () {
    	return _logger.getName();
    }    
}
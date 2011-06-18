package com.limegroup.gnutella.settings;
import java.io.File;

import org.limewire.setting.BooleanSetting;
import org.limewire.setting.FileSetting;
import org.limewire.setting.FloatSetting;
import org.limewire.setting.IntSetting;
import org.limewire.setting.LongSetting;
import org.limewire.setting.StringSetting;
import org.limewire.util.OSUtils;

/**
 * Settings for LimeWire application
 */
public class ApplicationSettings extends LimeProps {
    public static BooleanSetting SEARCH_OPTIONS_COLLAPSED = FACTORY.createBooleanSetting("SEARCH_OPTIONS_COLLAPSED", true);

	private ApplicationSettings() {}
    
    /**
     * The Client ID number
     */
    public static final StringSetting CLIENT_ID = 
        FACTORY.createStringSetting("CLIENT_ID", "");

    /**
     * The average time this user leaves the application running.
     */        
    public static final IntSetting AVERAGE_UPTIME =
        FACTORY.createExpirableIntSetting("AVERAGE_UPTIME", 0);
   
    /**
	 * The total time this user has used the application.
	 */    
    public static final IntSetting TOTAL_UPTIME =
        FACTORY.createIntSetting("TOTAL_UPTIME", 0);
    
    /**
     * The average time this user is connected to the network per session (in seconds).
     */        
    public static final LongSetting AVERAGE_CONNECTION_TIME =
        FACTORY.createExpirableLongSetting("AVERAGE_CONNECTION_TIME", 0L);
    
    /**
     * The total time this user has been connected to the network (in seconds).
     */    
    public static final LongSetting TOTAL_CONNECTION_TIME =
        FACTORY.createLongSetting("TOTAL_CONNECTION_TIME", 0L);
    
    /**
     * The total number of times this user has connected-disconnected from the network.
     */    
    public static final IntSetting TOTAL_CONNECTIONS =
        FACTORY.createIntSetting("TOTAL_CONNECTIONS", 0);
    
    
    /**
     * The total number of times the application  has been run --
	 * used in calculating the average amount of time this user
	 * leaves the application on.
     */    
    public static final IntSetting SESSIONS =
        FACTORY.createIntSetting("SESSIONS", 1);
    
    /**
     * The time that this was last shutdown (system time in milliseconds).
     */
    public static final LongSetting LAST_SHUTDOWN_TIME =
        FACTORY.createLongSetting("LAST_SHUTDOWN_TIME", 0);
    
    /**
     * The fraction of time this is running, a unitless quality.  This is
     * used to identify highly available hosts with big pongs.  This value
     * should only be updated once per session.
     */    
    public static final FloatSetting FRACTIONAL_UPTIME =
        FACTORY.createFloatSetting("FRACTIONAL_UPTIME", 0.0f);
    
    /**
	 * Specifies whether or not the program has been installed, either by
	 * a third-party installer, or by our own.  This is the old value for 
     * legacy InstallShield installers that set the save directory and the
     * connection speed.
	 */
    public static final BooleanSetting INSTALLED =
        FACTORY.createBooleanSetting("INSTALLED", false);
    
    /**
	 * The width that the application should be.
	 */
    public static final IntSetting APP_WIDTH =
        FACTORY.createIntSetting("APP_WIDTH", 1024);
	
    /**
	 * The height that the application should be.
	 */    
    public static final IntSetting APP_HEIGHT =
        FACTORY.createIntSetting("APP_HEIGHT", 600);
    
    /**
	 * A flag for whether or not the application has been run one
	 * time before this.
	 */    
    public static final BooleanSetting RUN_ONCE =
        FACTORY.createBooleanSetting("RUN_ONCE", false);
  
    /**
	 * The x position of the window for the next time the application
	 * is started.
	 */
    public static final IntSetting WINDOW_X =
        (IntSetting)FACTORY.createIntSetting("WINDOW_X", 0).setAlwaysSave(true);
    
    /**
	 * The y position of the window for the next time the application
	 * is started.
	 */
    public static final IntSetting WINDOW_Y =
        (IntSetting)FACTORY.createIntSetting("WINDOW_Y", 0).setAlwaysSave(true);
    
    /** Setting for whether or not LW should start maximized. */
    public static final BooleanSetting MAXIMIZE_WINDOW =
        FACTORY.createBooleanSetting("MAXIMIZE_WINDOW", false);
    
    /**
     * A flag for whether or not to display the system
     * tray icon while the application is visible. 
     */
    public static final BooleanSetting DISPLAY_TRAY_ICON =
        FACTORY.createBooleanSetting("DISPLAY_TRAY_ICON", true);
    
    /**
	 * The language to use for the application.
	 */
    public static final StringSetting LANGUAGE =
        FACTORY.createStringSetting("LANGUAGE", 
            System.getProperty("user.language", ""));
    
    /**
	 * The country to use for the application.
	 */
    public static final StringSetting COUNTRY =
        FACTORY.createStringSetting("COUNTRY", 
            System.getProperty("user.country", ""));
    
    /**
	 * The locale variant to use for the application.
	 */
    public static final StringSetting LOCALE_VARIANT =
        FACTORY.createStringSetting("LOCALE_VARIANT", 
            System.getProperty("user.variant", ""));
   
    /**
	 * Sets whether or not Monitor Tab should be enabled.
	 */    
    public static final BooleanSetting MONITOR_VIEW_ENABLED =
        FACTORY.createBooleanSetting("MONITOR_VIEW_ENABLED", true);
  
    /**
	 * Sets whether or not Connection Tab should be enabled.
	 */
    public static final BooleanSetting CONNECTION_VIEW_ENABLED =
        FACTORY.createBooleanSetting("CONNECTION_VIEW_ENABLED", true);
    
    /**
	 * Sets whether or not Library Tab should be enabled.
	 */
    public static final BooleanSetting LIBRARY_VIEW_ENABLED =
        FACTORY.createBooleanSetting("LIBRARY_VIEW_ENABLED", true);
    
    /** 
     * Sets wheter or not the Android Tab should be enabled
     */
    public static final BooleanSetting ANDROID_VIEW_ENABLED = 
    	FACTORY.createBooleanSetting("ANDROID_VIEW_ENABLED", true);

    /** 
     * Sets wheter or not the Chat Tab should be enabled
     */
    public static final BooleanSetting CHAT_VIEW_ENABLED = 
    	FACTORY.createBooleanSetting("CHAT_VIEW_ENABLED", ChatSettings.CHAT_IRC_ENABLED.getValue());
    
    /**
	 * Sets whether or not Console Tab should be enabled.
	 */    
    public static final BooleanSetting CONSOLE_VIEW_ENABLED =
        FACTORY.createBooleanSetting("CONSOLE_VIEW_ENABLED", false);
    
    /** Whether or not the logging tab is enabled / visible. */
    public static final BooleanSetting LOGGING_VIEW_ENABLED =
        FACTORY.createBooleanSetting("LOGGING_VIEW_ENABLED", false);
    
    /**
     * Sets whether or not SWT Browser Tab should be enabled.
     */
    public static final BooleanSetting SWT_BROWSER_VIEW_ENABLED =
        FACTORY.createBooleanSetting("SWT_BROWSER_VIEW_ENABLED", true);    
    
    /**
	 * Sets the name of the jar file to load on startup, which is read
	 * in from the properties file by RunLime.
	 */
    public static final StringSetting JAR_NAME = 
        FACTORY.createStringSetting("JAR_NAME", "FrostWire.jar");
  
    /**
	 * Sets the classpath for legacy RunLime.jars.
	 */
    public static final StringSetting CLASSPATH = 
        FACTORY.createStringSetting("CLASSPATH", JAR_NAME.getValue());
        
    /**
     * Whether or not we are acting as a peer server.
     */
    public static final BooleanSetting SERVER =
        FACTORY.createBooleanSetting("SERVER", false);
            
    /**
     * Setting for whether or not to create an additional manual GC thread.
     */
    public static final BooleanSetting AUTOMATIC_MANUAL_GC =
        FACTORY.createBooleanSetting("AUTOMATIC_MANUAL_GC", OSUtils.isMacOSX());

    /**
     * the default locale to use if not specified
     * used to set the locale for connections which don't have X_LOCALE_PREF
     * header or pings and pongs that don't advertise locale preferences.
     */
    public static final StringSetting DEFAULT_LOCALE = 
        FACTORY.createStringSetting("DEFAULT_LOCALE", "en");
        

    /**
     * Enable the MagnetClipboardListener on non Windows and Mac OS
     * systems
     */
    public static final BooleanSetting MAGNET_CLIPBOARD_LISTENER
        = FACTORY.createBooleanSetting("MAGNET_CLIPBOARD_LISTENER", 
                !OSUtils.isWindows() && !OSUtils.isAnyMac());
    
    /**
     * Whether LimeWire should handle magnets.
     */
    public static final BooleanSetting HANDLE_MAGNETS = 
    	FACTORY.createBooleanSetting("HANDLE_MAGNETS", true);
    
    /**
     * Whether LimeWire should handle torrents.
     */
    public static final BooleanSetting HANDLE_TORRENTS = 
    	FACTORY.createBooleanSetting("HANDLE_TORRENTS", true);
    			
    /**
     * Whether or not to use 'secure results' to screen search results.
     */
    public static final BooleanSetting USE_SECURE_RESULTS =
        FACTORY.createBooleanSetting("USE_SECURE_RESULTS", false); // FTA: doesn't show 'secure results'
    
    /** The last directory used for opening a file chooser. */
    public static final FileSetting LAST_FILECHOOSER_DIRECTORY =
        FACTORY.createFileSetting("LAST_FILECHOOSER_DIR", new File("")).setAlwaysSave(true);
    
    /** Whether collecting and reporting usage stats is allowed */
    public static final BooleanSetting USAGE_STATS =
        FACTORY.createBooleanSetting("USAGE_STATS", false);
    
    /**
     * Gets the current language setting.
     */
    public static String getLanguage() {
        String lc = LANGUAGE.getValue();
        String cc = COUNTRY.getValue();
        String lv = LOCALE_VARIANT.getValue();
        String lang = lc;
        if(cc != null && !cc.equals(""))
            lang += "_" + cc;
        if(lv != null && !lv.equals(""))
            lang += "_" + lv;
        return lang;
    }
}

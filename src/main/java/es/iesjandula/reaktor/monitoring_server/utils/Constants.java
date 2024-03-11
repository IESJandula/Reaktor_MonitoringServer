package es.iesjandula.reaktor.monitoring_server.utils;

import java.io.File;

public class Constants
{

	/**Valor para encontrar la caroeta donde iran los ficheros de configuracion wifi/capturas/configuracion */
	public static String FILE_FOLDER = "files" + File.separator;
	
	/**Nombre de la carpeta de configuracion*/
	public static final String REAKTOR_CONFIG = "reaktor_config";
	/**Nombre de la carpeta de configuracion con ejecutables */
	public static final String REAKTOR_CONFIG_EXEC = "reaktor_config_exec";
	/**Nombre de la carpeta de configuracion wifi*/
	public static final String REAKTOR_CONFIG_EXEC_CONF_WIFI = REAKTOR_CONFIG_EXEC + File.separator + "confWIFI";
	/**Nombre de la carpeta general*/
	public static final String REAKTOR_CONFIG_EXEC_FILES = REAKTOR_CONFIG_EXEC + File.separator + "files";
	/**Nombre de la carperta de capturas de pantalla */
	public static final String REAKTOR_CONFIG_EXEC_SCREENSHOTS = REAKTOR_CONFIG_EXEC + File.separator + "screenshots";
	/**Nombre de la carpeta de capturas de pantalla en web */
	public static final String REAKTOR_CONFIG_EXEC_WEB_SCREENSHOTS = REAKTOR_CONFIG_EXEC + File.separator + "webScreenshots";
	/**Ubicacion del archivo de configuracion de acciones del proyecto */
	public static final String REAKTOR_CONFIG_EXEC_ACTIONS_CSV = REAKTOR_CONFIG_EXEC + File.separator + "actionsCSV.csv";
	
	/**ACCIONES */
	
	/**Accion para mandar una configuracion wifi*/
	public static final String ACTION_WIFI = "configWifi";
	
	/**Accion para abrir un enlace web*/
	public static final String ACTION_WEB = "openWeb";
	
	/**Accion para mandar uno o varios comandos */
	public static final String ACTION_COMMANDS = "command";
	
	/**Accion para apagar un ordenador */
	public static final String ACTION_SHUTDOWN = "shutdown";
	
	/**Accion para reiniciar un ordenador */
	public static final String ACTION_RESTART = "restart";
	
	/**Accion para bloquear o desbloquear perifericos */
	public static final String ACTION_PERIPHERAL = "postPeripheral";
	
	/**Accion para mandar capturas de pantalla */
	public static final String ACTION_SCREENSHOT = "screenshot";
	
	/**Accion para instalar software */
	public static final String ACTION_INSTALL = "install";
	
	/**Accion para desinstalar software */
	public static final String ACTION_UNINSTALL = "uninstall";
	
	/**Accion para mandar un fichero ejecutable o de configuracion */
	public static final String ACTION_FILE = "file";
	
	/**Informacion del error por cambio de configuracion del fichero actionsCSV.csv*/
	public static final String ERROR_FILE_CFG = "La configuracion de la entidad action ha sido modificada, reestablezca la configuracion del fichero actionsCSV "
			+ "o actualice el nombre de las acciones al fichero actionCSV";
	
	
	


}

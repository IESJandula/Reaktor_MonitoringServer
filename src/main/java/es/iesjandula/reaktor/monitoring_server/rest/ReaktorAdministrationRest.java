package es.iesjandula.reaktor.monitoring_server.rest;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;

import es.iesjandula.reaktor.exceptions.ComputerError;
import es.iesjandula.reaktor.models.Action;
import es.iesjandula.reaktor.models.Motherboard;
import es.iesjandula.reaktor.models.Usb;
import es.iesjandula.reaktor.monitoring_server.repository.IActionRepository;
import es.iesjandula.reaktor.monitoring_server.repository.IMotherboardRepository;
import es.iesjandula.reaktor.monitoring_server.repository.ITaskRepository;
import es.iesjandula.reaktor.monitoring_server.utils.AdminChecker;
import es.iesjandula.reaktor.monitoring_server.utils.AdminUtils;
import es.iesjandula.reaktor.monitoring_server.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Juan Alberto Jurado
 *
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/computers")
@Slf4j
public class ReaktorAdministrationRest
{

	/** Repositorio que se encarga de realizar las operaciones CRUD sobre la entidad Task	 */
	@Autowired
	private ITaskRepository iTaskRepository;

	/**	Repositorio que se encarga de realizar las operaciones CRUD sobre la entidad Motherboard */
	@Autowired
	private IMotherboardRepository iMotherboardRepository;

	/** Repositorio que se encarga de realizar las operaciones CRUD sobre la entidad Action */ 	 
	@Autowired
	private IActionRepository iActionRepository;
	
	/**Clase que almacena metodos para comprobar parametros */
	private AdminChecker checker;
	
	/**Clase que almacena metodos que realizan acciones como añadir o mostrar ordenadores */
	private AdminUtils utils;
	
	/**
	 * Constructor que instancia la clase y que crea las clases AdminChecker y AdminUtils para 
	 * la utilizacion de sus metodos
	 */
	public ReaktorAdministrationRest() 
	{
		this.checker = new AdminChecker();
		this.utils = new AdminUtils();
	}

	/**
	 * Endpoint que se encarga de asignarle a un ordenador por su serial number un fichero
	 * de configuracion wifi usando el nombre del fichero de configuracion
	 * @param serialNumber numero de serie del ordenador
	 * @param wifiFile nombre del fichero de configuracion wifi
	 * @return ok si el ordenador y el nombre del fichero esta bien formado, error si esta mal formado
	 */
	@Operation
	@RequestMapping(method = RequestMethod.POST, value = "/admin/wifiCfg")
	public ResponseEntity<?> postWifiCfg(
			@RequestHeader(required = true) String serialNumber,
			@RequestHeader(required = true) String wifiFileName)
	{
		try
		{
			//Se obtiene un ordenador usando el repositorio y se comprueba mas adelante que exista
			Optional<Motherboard> motherboardId = this.iMotherboardRepository.findById(serialNumber);
			if (motherboardId.isEmpty())
			{
				String error = "El serial number "+serialNumber+" no pertenece a ningun ordenador";
				ComputerError computerError = new ComputerError(404, error);
				return ResponseEntity.status(404).body(computerError.toMap());
			}
			//Si el ordenador existe se obtiene la accion configWifi que contiene la tarea
			//de asignar el fichero de configuracion wifi a un ordenador mas adelante se comprueba si existe la accion
			Optional<Action> actionId = this.iActionRepository.findById(Constants.ACTION_WIFI);

			if (actionId.isPresent())
			{
				this.utils.addTask(motherboardId.get(), actionId.get(), Constants.REAKTOR_CONFIG_EXEC_CONF_WIFI + File.separator + wifiFileName,this.iTaskRepository);
			}
			else
			{
				String error = Constants.ERROR_FILE_CFG;
				ComputerError computerError = new ComputerError(500, error);
				return ResponseEntity.status(500).body(computerError.toMap());
			}
			
			//Devolvemos el estado satisfactorio de la operacion
			return ResponseEntity.ok().build();

		}
		catch (Exception exception)
		{
			String error = "Error de servidor, fallo al abrir la configuracion wifi del ordenador asignado";
			log.error(error, exception);
			ComputerError computerError = new ComputerError(409, error, exception);
			return ResponseEntity.status(409).body(computerError.toMap());
		}

	}

	/**
	 * Endpoint que se encarga de asignarle al ordenador por su serial number un enlace web para
	 * posteriormente abrirlo en el navegador web Chrome
	 * @param serialNumber numero de serie del ordenador
	 * @param webURL url de la web a abrir
	 * @return ok si el ordenador y la web estan bien formados, error si esta mal formado
	 */
	@Operation
	@RequestMapping(method = RequestMethod.POST, value = "/admin/chrome/openWeb")
	public ResponseEntity<?> postOpenWeb(
			@RequestHeader(required = true) String serialNumber,
			@RequestHeader(required = true) String webURL)
	{
		try
		{
			//Se obtiene un ordenador usando el repositorio y se comprueba mas adelante que exista
			Optional<Motherboard> motherboardId = this.iMotherboardRepository.findById(serialNumber);
			if (motherboardId.isEmpty())
			{
				String error = "El serial number "+serialNumber+" no pertenece a ningun ordenador";
				ComputerError computerError = new ComputerError(404, error);
				return ResponseEntity.status(404).body(computerError.toMap());
			}

			Optional<Action> actionId = this.iActionRepository.findById(Constants.ACTION_WEB);

			// Si la accion existe creamos la tarea para abir el enlace web
			if (actionId.isPresent())
			{
				this.utils.addTask(motherboardId.get(), actionId.get(), webURL,this.iTaskRepository);
			}
			else
			{
				String error = Constants.ERROR_FILE_CFG;
				ComputerError computerError = new ComputerError(409, error);
				return ResponseEntity.status(409).body(computerError.toMap());
			}

			//Devolvemos el estado satisfactorio de la operacion
			return ResponseEntity.ok().build();

		}
		catch (Exception exception)
		{
			String error = "Error de servidor, fallo al abrir el enlace web del ordenador asignado";
			log.error(error, exception);
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}

	}

	/**
	 * Endpoint que manda a un ordenador o varios ordenadores una lista de comandos a ejecutar
	 * los ordenadores o el ordenador se identifican por varios parametros en caso de que no se
	 * envie ninguno se le asignara la lista de comandos a todos los ordenadores
	 * 
	 * @param serialNumber numero de serie del ordenador
	 * @param classroom    clase en la que se encuentra
	 * @param trolley      carrito al que pertenece
	 * @param floor        planta en la que se encuentra el ordenador
	 * @param commandLine  linea de comandos a ejecutar sobre el o los ordenadores
	 * @return ok si los ordenadores existen y la linea de comandos esta bien formada, error si fallan
	 */
	@Operation
	@RequestMapping(method = RequestMethod.POST, value = "/admin/commandLine")
	public ResponseEntity<?> postComputerCommandLine(
			@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String classroom, 
			@RequestHeader(required = false) String trolley,
			@RequestHeader(required = false) Integer floor, 
			@RequestHeader(required = true) String commandLine)
	{
		try
		{
			/*
			 * Se instancia un conjunto de ordenadores (vacio al principio) para guardar ordenadores
			 * y evitar que estos se repitan, ya que se usan parametros que permiten la repeticion de
			 * los ordenadores, por ejemplo un ordenador puede ser el mismo si se encuentra en la planta 1
			 * y la clase introducida esta en la planta 1
			 */
			Set<Motherboard> commands = new HashSet<Motherboard>();

			//Se comprueba que los parametros no sean nulos, en caso de que lo sean se envia
			//la linea de comandos a todos los ordenadores
			if ((serialNumber != null) || (classroom != null) || (trolley != null) || (floor != null))
			{
				commands = this.checker.checkAndSend(serialNumber, classroom, trolley, floor, commands,this.iMotherboardRepository);
				
				Optional<Action> actionId = this.iActionRepository.findById(Constants.ACTION_COMMANDS);
				
				if (actionId.isPresent())
				{
					this.utils.addTasks(commands, actionId.get(), commandLine,this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}

				//Devolvemos el estado satisfactorio de la operacion
				return ResponseEntity.ok().build();
			}
			else
			{
				//Mandamos los comandos por todos los ordenadores
				commands = this.utils.addByAll(commands,this.iMotherboardRepository);
				Optional<Action> actionId = this.iActionRepository.findById(Constants.ACTION_COMMANDS);

				if (actionId.isPresent())
				{
					this.utils.addTasks(commands, actionId.get(), commandLine,this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
				log.info("By all Computers");
				//Devolvemos el estado satisfactorio de la operacion
				return ResponseEntity.ok().build();
			}
		}
		catch (Exception exception)
		{
			String error = "Error de servidor, fallo al mandar la linea de comandos a los ordenadores";
			log.error(error,exception);
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}

	/**
	 * Endpoint que manda a un ordenador o varios ordenadores una peticion para que se apaguen
	 * los ordenadores o el ordenador se identifican por varios parametros en caso de que no se
	 * envie ninguno se le asignara la peticion de apagado a todos los ordenadores
	 * 
	 * @param serialNumber numero de serie del ordenador
	 * @param classroom    clase en la que se encuentra
	 * @param trolley      carrito al que pertenece
	 * @param floor        planta en la que se encuentra el ordenador
	 * @return ok si los ordenadores existen, error si fallan
	 */
	@Operation
	@RequestMapping(method = RequestMethod.POST, value = "/admin/shutdown")
	public ResponseEntity<?> putComputerShutdown(
			@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String classroom, 
			@RequestHeader(required = false) String trolley,
			@RequestHeader(required = false) Integer floor)
	{
		try
		{
			/*
			 * Se instancia un conjunto de ordenadores (vacio al principio) para guardar ordenadores
			 * y evitar que estos se repitan, ya que se usan parametros que permiten la repeticion de
			 * los ordenadores, por ejemplo un ordenador puede ser el mismo si se encuentra en la planta 1
			 * y la clase introducida esta en la planta 1
			 */
			Set<Motherboard> shutdownList = new HashSet<Motherboard>();

			//Se comprueba que los parametros no sean nulos, en caso de que lo sean se envia
			//la peticion de apagado a todos los ordenadores
			if ((serialNumber != null) || (classroom != null) || (trolley != null) || (floor != null))
			{
				shutdownList = this.checker.checkAndSend(serialNumber, classroom, trolley, floor, shutdownList,this.iMotherboardRepository);

				Optional<Action> actionId = this.iActionRepository.findById(Constants.ACTION_SHUTDOWN);

				if (actionId.isPresent())
				{
					this.utils.addTasks(shutdownList, actionId.get(), "",this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}

				// Controlamos si se ha seleccionado alguna opción. En caso de que sea vacío, devolver un error
				if(shutdownList.isEmpty()) 
				{
					String error = "No se indicó ningún equipo a través de los filtros";
					log.error(error);
					ComputerError computerError = new ComputerError(400, error, null);
					return ResponseEntity.status(400).body(computerError.toMap());
				}

        // Devolvemos el estado satisfactorio de la operacion
				return ResponseEntity.ok().build();
      }
			else
			{
				String error = "Error no parameters found";
				log.error(error);
				ComputerError computerError = new ComputerError(400, error, null);
				return ResponseEntity.status(400).body(computerError.toMap());
			}
		}
		catch (Exception exception)
		{
			String error = "Error de servidor, fallo al mandar la peticion de apagado a los ordenadores";
			log.error(error,exception);
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}

	/**
	 * Endpoint que manda a un ordenador o varios ordenadores una peticion para que se reinicien
	 * los ordenadores o el ordenador se identifican por varios parametros en caso de que no se
	 * envie ninguno se le asignara la peticion de reinicio a todos los ordenadores
	 * 
	 * @param serialNumber numero de serie del ordenador
	 * @param classroom    clase en la que se encuentra
	 * @param trolley      carrito al que pertenece
	 * @param floor        planta en la que se encuentra el ordenador
	 * @return ok si los ordenadores existen, error si fallan
	 */
	@Operation
	@RequestMapping(method = RequestMethod.POST, value = "/admin/restart")
	public ResponseEntity<?> putComputerRestart(
			@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String classroom, 
			@RequestHeader(required = false) String trolley,
			@RequestHeader(required = false) Integer floor)
	{
		try
		{
			/*
			 * Se instancia un conjunto de ordenadores (vacio al principio) para guardar ordenadores
			 * y evitar que estos se repitan, ya que se usan parametros que permiten la repeticion de
			 * los ordenadores, por ejemplo un ordenador puede ser el mismo si se encuentra en la planta 1
			 * y la clase introducida esta en la planta 1
			 */
			Set<Motherboard> restartList = new HashSet<Motherboard>();

			//Se comprueba que los parametros no sean nulos, en caso de que lo sean se envia
			//la peticion de reinicio a todos los ordenadores
			if ((serialNumber != null) || (classroom != null) || (trolley != null) || (floor != null))
			{
				restartList = this.checker.checkAndSend(serialNumber, classroom, trolley, floor, restartList,this.iMotherboardRepository);

				Optional<Action> actionId = this.iActionRepository.findById(Constants.ACTION_RESTART);

				if (actionId.isPresent())
				{
					this.utils.addTasks(restartList, actionId.get(), "",this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
				
				// Controlamos si se ha seleccionado alguna opción. En caso de que sea vacío, devolver un error
				if(restartList.isEmpty()) 
				{
					String error = "Error no matches found";
					log.error(error);
					ComputerError computerError = new ComputerError(400, error, null);
					return ResponseEntity.status(400).body(computerError.toMap());
				}
				
        // Devolvemos el estado satisfactorio de la operacion
				return ResponseEntity.ok().build();
			}
			else
			{				
				String error = "Error no parameters found";
				log.error(error);
				ComputerError computerError = new ComputerError(400, error, null);
				return ResponseEntity.status(400).body(computerError.toMap());
			}
		}
		catch (Exception exception)
		{
			String error = "Error de servidor, fallo al mandar la peticion de reinicio a los ordenadores";
			log.error(error,exception);
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}

	/**
	 * Metodo que manda una peticion para bloquear o activar dispositivos USB de varios
	 * ordenadores para evitar colar dispositivos sospechosos, los ordenadores se identifican
	 * por varios parametros en caso de que no se envie ninguno se enviara la peticion a todos
	 * los ordenadores
	 * 
	 * IMPORTANTE: Actualmente no se utiliza ya que estamos pendientes de implementarlo de forma correcta
	 * 
	 * @param classroom clase en la que se encuentra el ordenador
	 * @param trolley carrito al que pertenece el ordenador
	 * @param usb usb que posee el ordenador o ordenadores afectados
	 * @return ResponseEntity ok si encuentra los ordenadores, error si fallan
	 */
	@Deprecated
	@Operation
	@RequestMapping(method = RequestMethod.POST, value = "/admin/peripheral", consumes = "application/json")
	public ResponseEntity<?> postPeripheral(
			@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley, 
			@RequestBody(required = true) Usb usb)
	{
		try
		{
			/*
			 * Se instancia un conjunto de ordenadores (vacio al principio) para guardar ordenadores
			 * y evitar que estos se repitan, ya que se usan parametros que permiten la repeticion de
			 * los ordenadores, por ejemplo un ordenador puede ser el mismo si se encuentra en la planta 1
			 * y la clase introducida esta en la planta 1
			 */
			Set<Motherboard> motherboardList = new HashSet<Motherboard>();
			//Se comprueba que los parametros no sean nulos, en caso de que lo sean se envia
			//la peticion de bloqueo o desbloqueo a todos los ordenadores
			if ((classroom != null) || (trolley != null))
			{
				motherboardList = this.checker.checkAndSend(Strings.EMPTY, trolley, classroom, Strings.EMPTY, motherboardList,this.iMotherboardRepository);

				Optional<Action> action = this.iActionRepository.findById(Constants.ACTION_PERIPHERAL);

				if (action.isPresent())
				{
					this.utils.addTasks(motherboardList, action.get(), usb.getId().toString(),this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
				//Devolvemos el estado satisfactorio de la operacion
				return ResponseEntity.ok().build();
			}
			else
			{
				List<Motherboard> list = this.iMotherboardRepository.findAll();
				motherboardList.addAll(list);

				Optional<Action> action = this.iActionRepository.findById(Constants.ACTION_PERIPHERAL);

				if (action.isPresent())
				{
					this.utils.addTasks(motherboardList, action.get(), usb.getId().toString(),this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
        
				log.info("By all Computers");
        
				//Devolvemos el estado satisfactorio de la operacion
				return ResponseEntity.ok().build();
			}
		}
		catch (Exception exception)
		{
			String error = "Error de servidor, fallo al mandar la peticion de bloqueo o desbloqueo de usb";
			log.error(error,exception);
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}

	/**
	 * Endpoint que manda a un ordenador o varios ordenadores una peticion para que sacar una
	 * captura de pantalla los ordenadores o el ordenador se identifican por varios parametros 
	 * en caso de que no se envie ninguno se le asignara la peticion de reinicio a todos los ordenadores
	 *
	 * @param classroom clase en el que se encuentra
	 * @param trolley carrito al que pertenece
	 * @param serialNumber numero de serie del ordenador
	 * @return ok si los ordenadores existen, error si fallan
	 */
	@Operation
	@RequestMapping(method = RequestMethod.POST, value = "/admin/screenshot")
	public ResponseEntity<?> sendScreenshotOrder(
			@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley, 
			@RequestHeader(required = false) String serialNumber)
	{
		try
		{
			/*
			 * Se instancia un conjunto de ordenadores (vacio al principio) para guardar ordenadores
			 * y evitar que estos se repitan, ya que se usan parametros que permiten la repeticion de
			 * los ordenadores, por ejemplo un ordenador puede ser el mismo si se encuentra en la planta 1
			 * y la clase introducida esta en la planta 1
			 */
			Set<Motherboard> screenshotList = new HashSet<Motherboard>();

			//Se comprueba que los parametros no sean nulos, en caso de que lo sean se envia
			//la linea de comandos a todos los ordenadores
			if ((classroom != null) || (trolley != null) || (serialNumber != null))
			{
				screenshotList = this.checker.checkAndSend(serialNumber, classroom, trolley, Strings.EMPTY, screenshotList,this.iMotherboardRepository);
				
				Optional<Action> actionId = this.iActionRepository.findById(Constants.ACTION_SCREENSHOT);

				if (actionId.isPresent())
				{
					this.utils.addTasks(screenshotList, actionId.get(), "",this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
        
				//Devolvemos el estado satisfactorio de la operacion
				return ResponseEntity.ok().build();
			}
			else
			{
				screenshotList = this.utils.addByAll(screenshotList,this.iMotherboardRepository);
				
				Optional<Action> actionId = this.iActionRepository.findById(Constants.ACTION_SCREENSHOT);

				if (actionId.isPresent())
				{
					this.utils.addTasks(screenshotList, actionId.get(), "",this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
				log.info("By all Computers");
				
        // Devolvemos el estado satisfactorio de la operacion
				return ResponseEntity.ok().build();
			}
		}
		catch (Exception exception)
		{
			String error = "Error de servidor, fallo al mandar la peticion de captura a los ordenadores";
			log.error(error,exception);
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}

	/**
	 * Endpoint que envia una peticion para instalar software en uno o varios ordenadores
	 * los ordenadores se identifican por varios parametros en caso de que no se envie
	 * ninguno se envia a todos los ordenadores
	 * 
	 * @param classroom clase en la que se encuentra
	 * @param trolley carrito al que pertenece
	 * @param professor profesor que dirije el ordenador
	 * @param software software disponible para instalar
	 * @return ok si encuentra los ordenadores, error si falla 
	 */
	@Operation
	@RequestMapping(method = RequestMethod.POST, value = "/admin/software")
	public ResponseEntity<?> sendSoftware(
			@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley, 
			@RequestHeader(required = false) String professor,
			@RequestHeader(required = true) String software)
	{
		try
		{
			/*
			 * Se instancia un conjunto de ordenadores (vacio al principio) para guardar ordenadores
			 * y evitar que estos se repitan, ya que se usan parametros que permiten la repeticion de
			 * los ordenadores, por ejemplo un ordenador puede ser el mismo si se encuentra en la planta 1
			 * y la clase introducida esta en la planta 1
			 */
			Set<Motherboard> motherboardSet = new HashSet<Motherboard>();

			//Se comprueba que los parametros no sean nulos, en caso de que lo sean se envia
			//la linea de comandos a todos los ordenadores
			if ((serialNumber != null) || (classroom != null) || (trolley != null) || (professor != null))
			{
				
				motherboardSet = this.checker.checkAndSend(serialNumber, trolley, classroom, professor, motherboardSet,this.iMotherboardRepository);
				
				Optional<Action> action = this.iActionRepository.findById(Constants.ACTION_INSTALL);
				
				if (!action.isEmpty())
				{
					this.utils.addTasks(motherboardSet, action.get(), software,this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
				
				// Controlamos si se ha seleccionado alguna opción. En caso de que sea vacío, devolver un error
				if(motherboardSet.isEmpty()) 
				{
					String error = "Error no matches found";
					log.error(error);
					ComputerError computerError = new ComputerError(400, error, null);
					return ResponseEntity.status(400).body(computerError.toMap());
				}
				
				//Devolvemos el estado satisfactorio de la operacion
				return ResponseEntity.ok().build();
			}
			else
			{
				List<Motherboard> motherboardList = this.iMotherboardRepository.findAll();
				motherboardSet.addAll(motherboardList);
				log.info("By all Computers");
				
				Optional<Action> action = this.iActionRepository.findById(Constants.ACTION_INSTALL);
				
				if (!action.isEmpty())
				{
					this.utils.addTasks(motherboardSet, action.get(), software,this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
				//Devolvemos el estado satisfactorio de la operacion

				return ResponseEntity.ok().build();
			}
		}
		catch (Exception exception)
		{
			String error = "Error de servidor, fallo al mandar la peticion de instalacion de software";
			log.error(error,exception);
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}

	/**
	 * Endpoint que envia una peticion para desinstalar software en uno o varios ordenadores
	 * los ordenadores se identifican por varios parametros en caso de que no se envie
	 * ninguno se envia a todos los ordenadores
	 * 
	 * @param classroom clase en la que se encuentra
	 * @param trolley carrito al que pertenece
	 * @param professor profesor que dirige el ordenador
	 * @param software nombre del software a instalar
	 * @return ok si encuentra los ordenadores, error si falla 
	 */
	@Operation
	@RequestMapping(method = RequestMethod.DELETE, value = "/admin/software")
	public ResponseEntity<?> unistallSoftware(
			@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley, 
			@RequestHeader(required = false) String professor,
			@RequestHeader(required = true) String software)
	{
		try
		{
			/*
			 * Se instancia un conjunto de ordenadores (vacio al principio) para guardar ordenadores
			 * y evitar que estos se repitan, ya que se usan parametros que permiten la repeticion de
			 * los ordenadores, por ejemplo un ordenador puede ser el mismo si se encuentra en la planta 1
			 * y la clase introducida esta en la planta 1
			 */
			Set<Motherboard> motherboardSet = new HashSet<Motherboard>();

			//Se comprueba que los parametros no sean nulos, en caso de que lo sean se envia
			//la linea de comandos a todos los ordenadores
			if ((professor != null) || (serialNumber != null) || (classroom != null) || (trolley != null))
			{
				motherboardSet = this.checker.checkAndSend(serialNumber, trolley, classroom, professor, motherboardSet,this.iMotherboardRepository);

				// BUSCAMOS ACCION
				Optional<Action> action = this.iActionRepository.findById(Constants.ACTION_UNINSTALL);
				
				if (!action.isEmpty())
				{
					this.utils.addTasks(motherboardSet, action.get(), software,this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}

				// --- CONTROL NO MATCHES FOUND ----
				if(motherboardSet.isEmpty()) 
				{
					String error = "Error no matches found";
					log.error(error);
					ComputerError computerError = new ComputerError(400, error, null);
					return ResponseEntity.status(400).body(computerError.toMap());
				}
				
				// Devolvemos el estado satisfactorio de la operacion
				return ResponseEntity.ok().build();
			}
			else
			{
				List<Motherboard> motherboardList = this.iMotherboardRepository.findAll();
				motherboardSet.addAll(motherboardList);
				log.info("By all Computers");
				Optional<Action> action = this.iActionRepository.findById(Constants.ACTION_UNINSTALL);
				if (!action.isEmpty())
				{
					this.utils.addTasks(motherboardSet, action.get(), software,this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
				//Devolvemos el estado satisfactorio de la operacion
				return ResponseEntity.ok().build();
			}
		}
		catch (Exception exception)
		{
			String error = "Error de servidor, fallo al mandar la peticion de desinstalacion de software";
			log.error(error,exception);
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}

	/**
	 * Endpoint que permite que el administrador pueda editar un ordenador identificandolo
	 * mediante su numero de serie, los demás parametros se cambian automaticamente una vez
	 * se identifique el ordenador
	 * 
	 * @param serialNumber numero de serie original del ordenador
	 * @param computerSerialNumber nuevo numero de serie
	 * @param andaluciaId nuevo numero de andalucia del ordenador
	 * @param computerNumber nueva pegatina identificativa del ordenador
	 * @param classroom nueva clase
	 * @param trolley nuevo carrito
	 * @param teacher nuevo profesor
	 * @param floor nueva planta
	 * @param admin opcion de si es administrador o no
	 * @return ok si la operacion ha sido un exito junto a la modificacion del ordenador o error si algun parametro falla
	 */
	@Operation
	@RequestMapping(method = RequestMethod.PUT, value = "/computer/edit")
	public ResponseEntity<?> updateComputer(
			@RequestHeader(required = true) String serialNumber,
			@RequestHeader(required = false) String computerSerialNumber,
			@RequestHeader(required = false) String andaluciaId, 
			@RequestHeader(required = false) String computerNumber,
			@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley, 
			@RequestHeader(required = false) String teacher,           
			@RequestHeader(required = false) Integer floor, 
			@RequestHeader(required = false) Boolean admin)
	{
		try
		{
			/*
			 * Se instancia un conjunto de ordenadores (vacio al principio) para guardar ordenadores
			 * y evitar que estos se repitan, ya que se usan parametros que permiten la repeticion de
			 * los ordenadores, por ejemplo un ordenador puede ser el mismo si se encuentra en la planta 1
			 * y la clase introducida esta en la planta 1
			 */
			Optional<Motherboard> motherboardId = this.iMotherboardRepository.findById(serialNumber);
			//Se comprueba si existe la placa base a editar
			if (motherboardId.isEmpty())
			{
				String error = "El numero de serie "+serialNumber+" introducido no pertenece a ningun ordenador";
				ComputerError computerError = new ComputerError(404, error);
				return ResponseEntity.status(404).body(computerError.toMap());
			}
			//Se comprueba que todos los parametros no esten vacios
			if ((computerSerialNumber == null) && (andaluciaId == null) && (computerNumber == null) 
					&& (classroom == null) && (trolley == null) && (teacher == null) && (floor == null)
					&& (admin == null))
			{
				String error = "No se ha introducido ningun atributo nuevo a editar en el ordenador encontrado";
				ComputerError computerError = new ComputerError(404, error);
				return ResponseEntity.status(404).body(computerError.toMap());
			}

			if (computerSerialNumber != null)
			{
				Optional<Action> action = this.iActionRepository.findById("updateSerialNumber");
				
				if (action.isPresent())
				{
					this.utils.addTask(motherboardId.get(), action.get(), computerSerialNumber,this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
			}
			if (andaluciaId != null)
			{
				Optional<Action> action = this.iActionRepository.findById("updateAndaluciaId");
				
				if (action.isPresent())
				{
					this.utils.addTask(motherboardId.get(), action.get(), andaluciaId,this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
			}
			if (computerNumber != null)
			{
				Optional<Action> action = this.iActionRepository.findById("updateComputerNumber");
				
				if (action.isPresent())
				{
					this.utils.addTask(motherboardId.get(), action.get(), computerNumber,this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
			}
			if (teacher != null)
			{
				Optional<Action> action = this.iActionRepository.findById("updateTeacher");
				
				if (action.isPresent())
				{
					this.utils.addTask(motherboardId.get(), action.get(), teacher,this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
			}
			if (trolley != null)
			{
				Optional<Action> action = this.iActionRepository.findById("updateTrolley");
				
				if (action.isPresent())
				{
					this.utils.addTask(motherboardId.get(), action.get(), trolley,this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
			}
			if (classroom != null)
			{
				Optional<Action> action = this.iActionRepository.findById("updateClassroom");
				
				if (action.isPresent())
				{
					this.utils.addTask(motherboardId.get(), action.get(), classroom,this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
			}
			if (floor != null)
			{
				Optional<Action> action = this.iActionRepository.findById("updateFloor");
				
				if (action.isPresent())
				{
					this.utils.addTask(motherboardId.get(), action.get(), floor.toString(),this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
			}
			if (admin != null)
			{
				Optional<Action> action = this.iActionRepository.findById("updateAdmin");
				
				if (action.isPresent())
				{
					this.utils.addTask(motherboardId.get(), action.get(), admin.toString(),this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
			}
			//Devolvemos el estado satisfactorio de la operacion
			return ResponseEntity.ok().build();
		}
		catch (Exception exception)
		{
			String error = "Error de servidor, fallo al editar los atributos del ordenador "+serialNumber;
			log.error(error,exception);
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}

	/**
	 * Endpoint que manda un fichero de configuracion,ejecutable o general a uno o varios ordenadores
	 * este fichero se guarda en el servidor en una carpeta a la altura de src/main/resources/reaktor_config/files
	 * y se almacena hasta que se descargue en el ordenador solicitado, el ordenador se identifica por varios parametros
	 * si todos los parametros estan vacios el fichero se manda a todos los ordenadores
	 * 
	 * @param serialNumber numero de serie del ordenador
	 * @param classroom clase en la que se encuentra
	 * @param trolley carrito al que pertenece
	 * @param floor planta en la que se encuentra
	 * @param fileName nombre del fichero
	 * @param execFile fichero a mandar para guardar en servidor
	 * @return ok si ha encontrado los ordenadores y si el fichero se ha guardado, mal si falla la busqueda de ordenadores o el guardado del fichero
	 */
	@Operation
	@RequestMapping(method = RequestMethod.POST, value = "/admin/file", consumes = "multipart/form-data")
	public ResponseEntity<?> postComputerExecFile(
			@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String classroom, 
			@RequestHeader(required = false) String trolley,
			@RequestHeader(required = false) Integer floor, 
			@RequestHeader(required = true) String fileName,
			@RequestBody(required = true) MultipartFile execFile)
	{
		try
		{
			/*
			 * Se instancia un conjunto de ordenadores (vacio al principio) para guardar ordenadores
			 * y evitar que estos se repitan, ya que se usan parametros que permiten la repeticion de
			 * los ordenadores, por ejemplo un ordenador puede ser el mismo si se encuentra en la planta 1
			 * y la clase introducida esta en la planta 1
			 */
			Set<Motherboard> fileList = new HashSet<Motherboard>();
			//Se comprueba que los parametros no sean nulos, en caso de que lo sean se envia
			//la linea de comandos a todos los ordenadores
			if ((serialNumber != null) || (classroom != null) || (trolley != null) || (floor != null))
			{

				fileList = this.checker.checkAndSend(serialNumber, classroom, trolley, floor, fileList,this.iMotherboardRepository);

				//Guardamos el fichero en reaktor_config_exec/files
				this.utils.writeText(Constants.REAKTOR_CONFIG_EXEC_FILES + File.separator + fileName, execFile.getBytes());
				
				Optional<Action> actionId = this.iActionRepository.findById(Constants.ACTION_FILE);

				if (actionId.isPresent())
				{
					this.utils.addTasks(fileList, actionId.get(), Constants.REAKTOR_CONFIG_EXEC_FILES + File.separator + fileName,this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
				//Devolvemos el estado satisfactorio de la operacion
				return ResponseEntity.ok().build();
			}
			else
			{
				fileList = this.utils.addByAll(fileList,this.iMotherboardRepository);
				Optional<Action> actionId = this.iActionRepository.findById(Constants.ACTION_FILE);

				if (actionId.isPresent())
				{
					this.utils.addTasks(fileList, actionId.get(), Constants.REAKTOR_CONFIG_EXEC_FILES + File.separator + execFile.getName(),this.iTaskRepository);
				}
				else
				{
					String error = Constants.ERROR_FILE_CFG;
					ComputerError computerError = new ComputerError(500, error);
					return ResponseEntity.status(500).body(computerError.toMap());
				}
				//Devolvemos el estado satisfactorio de la operacion
				return ResponseEntity.ok().build();
			}
		}
		catch (Exception exception)
		{
			String error = "Error de servidor, fallo al mandar el fichero "+fileName;
			log.error(error,exception);
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}

	}

	/**
	 * Metodo que permite controlar las excepciones generadas en springboot para indicar al usuario
	 * errores de insercion de parametros por ejemplo un parametro entero se pasa como string
	 *
	 * @param MethodArgumentTypeMismatchException excepecion generada de springboot
	 * @return informacion de la excepcion generada
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException exception)
	{
		String name = exception.getName();
		String type = exception.getRequiredType().getSimpleName();
		Object value = exception.getValue();
		String message = String.format("'%s' should be a valid '%s' and '%s' isn't", name, type, value);
		log.error(message);
		ComputerError computerError = new ComputerError(404, message, exception);
		return ResponseEntity.status(404).body(computerError.toMap());
	}
}
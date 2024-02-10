package es.iesjandula.reaktor.monitoring_server.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.iesjandula.reaktor.exceptions.ComputerError;
import es.iesjandula.reaktor.models.CommandLine;
import es.iesjandula.reaktor.models.Computer;
import es.iesjandula.reaktor.models.Location;
import es.iesjandula.reaktor.models.MonitorizationLog;
import es.iesjandula.reaktor.models.Peripheral;
import es.iesjandula.reaktor.models.Software;
import es.iesjandula.reaktor.models.Status;
import es.iesjandula.reaktor.models.monitoring.Actions;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Javier Martínez Megías
 *
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/computers")
@Slf4j
public class ReaktorMonitoringRest
{
	/** Attribute computerList */
	private List<Computer> computerList = new ArrayList<>(List.of(
			new Computer("sn123", "and123", "cn123", "windows", "paco", new Location("0.5", 0, "trolley1"),
					new ArrayList<>(), new ArrayList<>(), new CommandLine(), new MonitorizationLog()),
			new Computer("sn1234", "and1234", "cn12344", "windows", "paco", new Location("0.5", 0, "trolley1"),
					new ArrayList<>(), new ArrayList<>(), new CommandLine(), new MonitorizationLog()),
			new Computer("sn123434231423423", "and12355", "cn123455", "windows", "paco",
					new Location("0.7", 0, "trolley2"), new ArrayList<>(), new ArrayList<>(), new CommandLine(),
					new MonitorizationLog()),
			new Computer("sn123556", "and123556", "cn1234556", "windows", "paco", new Location("0.7", 0, "trolley2"),
					new ArrayList<>(), new ArrayList<>(), new CommandLine(), new MonitorizationLog()),
			new Computer("sn123777", "and123777", "cn1234777", "windows", "paco", new Location("0.9", 0, "trolley3"),
					new ArrayList<>(), new ArrayList<>(), new CommandLine(), new MonitorizationLog())

	));
	/** Attribute shutDownComputerList */
	private List<Computer> shutDownComputerList = new ArrayList<>(List.of(
			new Computer("sn123556", "and123556", "cn1234556", "windows", "paco", new Location("0.7", 0, "trolley2"),
					new ArrayList<>(), new ArrayList<>(), new CommandLine(), new MonitorizationLog())));
	/** Attribute restartDownComputerList */
	private List<Computer> restartDownComputerList = new ArrayList<>(List.of(
			new Computer("sn123556", "and123556", "cn1234556", "windows", "paco", new Location("0.7", 0, "trolley2"),
					new ArrayList<>(), new ArrayList<>(), new CommandLine(), new MonitorizationLog())));

	/** Attribute execCommandsComputerList */
	private List<Computer> execCommandsComputerList = new ArrayList<>(List.of(new Computer("sn123556", "and123556",
			"cn1234556", "windows", "paco", new Location("0.7", 0, "trolley2"), new ArrayList<>(), new ArrayList<>(),
			new CommandLine(List.of("mkdir .\\carpeta1David")), new MonitorizationLog())));

	/** Attribute blockDispositiveComputerList */
	private List<Computer> blockDispositiveComputerList = new ArrayList<>(
			List.of(new Computer("sn123556", "and123556", "cn1234556", "windows", "paco",
					new Location("0.7", 0, "trolley2"), new ArrayList<>(List.of(new Peripheral("Raton", 1, true))),
					new ArrayList<>(), new CommandLine(), new MonitorizationLog())));

	/** Attribute openWebComputerList */
	private List<Computer> openWebComputerList = new ArrayList<>(List.of(new Computer("sn123556", "and123556",
			"cn1234556", "windows", "paco", new Location("0.7", 0, "trolley2"), new ArrayList<>(), new ArrayList<>(),
			new CommandLine(List.of("start chrome www.iesjandula.es")), new MonitorizationLog())));

	/** Attribute installAppComputerList */
	private List<Computer> installAppComputerList = new ArrayList<>(List.of(
			new Computer("sn123556", "and123556", "cn1234556", "windows", "paco", new Location("0.7", 0, "trolley2"),
					new ArrayList<>(), new ArrayList<>(List.of(new Software("Notepad++.Notepad++"))),
					new CommandLine(List.of("start chrome")), new MonitorizationLog())));

	/** Attribute uninstallAppComputerList */
	private List<Computer> uninstallAppComputerList = new ArrayList<>(List.of(
			new Computer("sn123556", "and123556", "cn1234556", "windows", "paco", new Location("0.7", 0, "trolley2"),
					new ArrayList<>(), new ArrayList<>(List.of(new Software("Mozilla.Firefox"))),
					new CommandLine(List.of("start chrome")), new MonitorizationLog())));

	/** Attribute configurationFileComputerList */
	private List<Computer> configurationFileComputerList = new ArrayList<>(List.of(new Computer("sn123556", "and123556",
			"cn1234556", "windows", "paco", new Location("0.7", 0, "trolley2"), new ArrayList<>(), new ArrayList<>(),
			new CommandLine(List.of("start chrome")), new MonitorizationLog())));

	/** Attribute updateAndaluciaComputerList */
	private List<Computer> updateAndaluciaComputerList = new ArrayList<>(List.of(new Computer("sn123556", "and123556",
			"cn1234556", "windows", "paco", new Location("0.7", 0, "trolley2"), new ArrayList<>(), new ArrayList<>(),
			new CommandLine(List.of("start chrome")), new MonitorizationLog())));

	/** Attribute updateSerialNumberComputerList */
	private List<Computer> updateSerialNumberComputerList = new ArrayList<>(List.of(new Computer("sn123556",
			"and123556", "cn1234556", "windows", "paco", new Location("0.7", 0, "trolley2"), new ArrayList<>(),
			new ArrayList<>(), new CommandLine(List.of("start chrome")), new MonitorizationLog())));

	/** Attribute updateComputerNumberComputerList */
	private List<Computer> updateComputerNumberComputerList = new ArrayList<>(List.of(new Computer("sn123556",
			"and123556", "cn1234556", "windows", "paco", new Location("0.7", 0, "trolley2"), new ArrayList<>(),
			new ArrayList<>(), new CommandLine(List.of("start chrome")), new MonitorizationLog())));

	/** Attribute screenshotOrderComputerList */
	private List<Computer> screenshotOrderComputerList = new ArrayList<>(List.of(new Computer("sn123556", "and123556",
			"cn1234556", "windows", "paco", new Location("0.7", 0, "trolley2"), new ArrayList<>(), new ArrayList<>(),
			new CommandLine(List.of("start chrome")), new MonitorizationLog())));
	/** Attribute statusComputerList */
	private List<Computer> statusComputerList = new ArrayList<>(List.of(new Computer("sn123556", "and123556",
			"cn1234556", "windows", "paco", new Location("0.7", 0, "trolley2"), new ArrayList<>(), new ArrayList<>(),
			new CommandLine(List.of("start chrome")), new MonitorizationLog())));

	/**
	 * Method sendStatusComputer
	 *
	 * @param serialNumber
	 * @param statusList
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/send/status", consumes = "application/json")
	public ResponseEntity<?> sendStatusComputer(
			@RequestHeader(required = true) String serialNumber,
			@RequestBody(required = true) List<Status> statusList)
	{
		try
		{
			log.info(serialNumber);
			log.info(statusList.toString());

			if (!this.isUsable(serialNumber))
			{
				String error = "Any Paramater Is Empty or Blank";
				ComputerError computerError = new ComputerError(404, error, null);
				return ResponseEntity.status(404).body(computerError.toMap());
			}
			else if (!this.chekIfSerialNumberExistBoolean(serialNumber))
			{
				String error = "The serial number dont exist";
				ComputerError computerError = new ComputerError(404, error, null);
				return ResponseEntity.status(404).body(computerError.toMap());
			}
			else
			{
				return ResponseEntity.ok().body("OK");
			}

		}
		catch (Exception exception)
		{
			log.error(exception.getMessage());
			ComputerError computerError = new ComputerError(500, exception.getMessage(), exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}

	/**
	 * Getting the files , the computer send the serialNumber to identify
	 *
	 * @param serialNumber, the serial number of the computer
	 * @return ResponseEntity
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/get/file")
	public ResponseEntity<?> getAnyFile(@RequestHeader(required = true) String serialNumber)
	{
		try
		{
			if ((serialNumber != null) && this.isUsable(serialNumber))
			{
				// Check if the serial number Exist
				Computer computer = this.chekIfSerialNumberExist(serialNumber);
				if (computer == null)
				{
					String error = "Compuer not found";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}
				return ResponseEntity.ok(computer);
			}
			else
			{
				String error = "Any Paramater Is Empty or Blank";
				ComputerError computerError = new ComputerError(404, error, null);
				return ResponseEntity.status(404).body(computerError.toMap());
			}
		}
		catch (Exception exception)
		{
			log.error(exception.getMessage());
			ComputerError computerError = new ComputerError(500, exception.getMessage(), exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}

	/**
	 * Getting the screenshot order , the compuer send the serialNumber to identify
	 *
	 * @param serialNumber, the serial number of the computer
	 * @return ResponseEntity
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/get/screenshot")
	public ResponseEntity<?> getScreenshotOrder(@RequestHeader(required = true) String serialNumber)
	{
		try
		{
			if ((serialNumber != null) && this.isUsable(serialNumber))
			{
				// Check if the serial number Exist and return a computer
				boolean order = this.chekOrder(serialNumber);
				if (!order)
				{
					String error = "Computer without screenshot order";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}
				else
				{
					this.screenshotOrderComputerList.remove(this.chekIfSerialNumberExist(serialNumber));
					return ResponseEntity.ok("OK");
				}
			}
			else
			{
				String error = "Any Paramater Is Empty or Blank";
				ComputerError computerError = new ComputerError(404, error, null);
				return ResponseEntity.status(404).body(computerError.toMap());
			}
		}
		catch (Exception exception)
		{
			log.error(exception.getMessage());
			ComputerError computerError = new ComputerError(500, exception.getMessage(), exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}

	/**
	 * Getting the files , the computer send the serialNumber to identify
	 *
	 * @param serialNumber, the serial number of the computer
	 * @return ResponseEntity
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/send/screenshot", consumes = "multipart/form-data")
	public ResponseEntity<?> sendScreenshot(@RequestBody(required = true) MultipartFile screenshot)
	{
		try
		{
			if (screenshot != null)
			{
				// Check if the serial number Exist
				return ResponseEntity.ok("OK");
			}
			else
			{
				String error = "The parameter is null";
				ComputerError computerError = new ComputerError(404, error, null);
				return ResponseEntity.status(404).body(computerError.toMap());
			}

		}
		catch (Exception exception)
		{
			log.error(exception.getMessage());
			ComputerError computerError = new ComputerError(500, exception.getMessage(), exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}

	/**
	 * this Method check if the serialNumber is blank or empty
	 *
	 * @param serialNumber, the serial Number of the computer
	 * @return boolean
	 */
	private boolean isUsable(String serialNumber)
	{
		boolean usable = false;
		if (!serialNumber.isBlank() || !serialNumber.isEmpty())
		{
			usable = true;
		}
		return usable;
	}

	/**
	 * this method check if a computer with this serialNumber exist
	 *
	 * @param serialNumber, the serial Number of the computer
	 * @return Computer
	 */
	private Computer chekIfSerialNumberExist(String serialNumber)
	{
		Computer computer = null;
		for (Computer x : this.computerList)
		{
			if (x.getSerialNumber().equals(serialNumber))
			{
				computer = x;
			}
		}
		return computer;
	}

	private boolean chekIfSerialNumberExistBoolean(String serialNumber)
	{
		boolean exist = false;
		for (Computer x : this.computerList)
		{
			if (x.getSerialNumber().equals(serialNumber))
			{
				exist = true;
			}
		}
		return exist;
	}

	/**
	 * this method check the order
	 *
	 * @param serialNumber
	 * @return boolean
	 */
	private boolean chekOrder(String serialNumber)
	{

		for (Computer x : this.screenshotOrderComputerList)
		{
			if (x.getSerialNumber().equals(serialNumber))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Method sendFullComputer that method is used for send periodically computer
	 * Instance
	 *
	 * @param serialNumber     the serial number
	 * @param andaluciaId      the andalucia id
	 * @param computerNumber   the computer number
	 * @param computerInstance the computer object instance
	 * @return ResponseEntity response
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/send/fullInfo", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> sendFullComputer
	(		
			@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String andaluciaId,
			@RequestHeader(required = false) String computerNumber,
			@RequestBody(required = true) Computer computerInstance)
	{
		try
		{
			// --- ONLY ONE PARAMETER BECAUSE ONLY SEND THE STATUS OF ONE COMPUTER AT
			// THE SAME TIME (FOR SCHELUDED TASK ON CLIENT) ---
			if ((serialNumber != null) || (andaluciaId != null) || (computerNumber != null))
			{
				if (serialNumber != null)
				{
					if (this.checkIsBlankEmpty(serialNumber))
					{
						// --- BY SERIAL NUMBER ---
						String error = "serialNumber is Empty or Blank";
						ComputerError computerError = new ComputerError(404, error, null);
						return ResponseEntity.status(404).body(computerError.toMap());
					}
					this.sendFullBySerialNumber(serialNumber, computerInstance);
				}
				else if (andaluciaId != null)
				{
					// --- BY ANDALUCIA ID ---
					if (this.checkIsBlankEmpty(andaluciaId))
					{
						String error = "andaluciaId is Empty or Blank";
						ComputerError computerError = new ComputerError(404, error, null);
						return ResponseEntity.status(404).body(computerError.toMap());
					}
					this.sendFullByAndaluciaId(andaluciaId, computerInstance);
				}
				else if (computerNumber != null)
				{
					// --- BY COMPUTER NUMBER ---
					if (this.checkIsBlankEmpty(computerNumber))
					{
						String error = "computerNumber is Empty or Blank";
						ComputerError computerError = new ComputerError(404, error, null);
						return ResponseEntity.status(404).body(computerError.toMap());
					}
					this.sendFullByComputerNumber(computerNumber, computerInstance);
				}

				// --- RESPONSE WITH OK , BUT TEMPORALY RESPONSE WITH LIST TO SEE THE CHANGES
				// ---
				return ResponseEntity.ok(this.computerList);

			}
			else
			{
				// --- ON THIS CASE ALL PARAMETERS ARE BLANK OR EMPTY ---
				String error = "All Paramaters Empty or Blank";
				ComputerError computerError = new ComputerError(404, error, null);
				return ResponseEntity.status(404).body(computerError.toMap());
			}
		}
		catch (Exception exception)
		{
			String error = "Server Error";
			ComputerError computerError = new ComputerError(404, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}

	/**
	 * Method sendStatusComputer metod to check and send status
	 *
	 * @param serialNumber the serialNumber
	 * @return ResponseEntity
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/get/pendingActions", produces = "application/json")
	public ResponseEntity<?> getPendingActions(@RequestHeader(required = true) String serialNumber)
	{
		try
		{
			Actions actionsToDo = new Actions();
			List<Status> statusList = new ArrayList<>();
			// --- SEARCHING THE COMPUTER ---
			if ((serialNumber == null) || serialNumber.isBlank() || serialNumber.isEmpty())
			{
				String error = "SerialNumber is Null, Empty or Blank";
				ComputerError computerError = new ComputerError(404, error, null);
				return ResponseEntity.status(404).body(computerError.toMap());
			}
			for (Computer computer : this.computerList)
			{
				// --- GETTING THE COMPUTER BY S/N ---
				if (computer.getSerialNumber().equalsIgnoreCase(serialNumber))
				{
					// --- SCANNING TASKS ---
					// --- SHUTDOWN ---
					this.sendStatusComputerShutdown(serialNumber, statusList, actionsToDo);
					// --- RESTART ---
					this.sendStatusComputerRestart(serialNumber, statusList, actionsToDo);
					// --- COMMAND EXECUTE ---
					this.sendStatusComputerCommandExecute(serialNumber, statusList, actionsToDo);
					// --- BLOCK DISPOSITIVE ---
					this.sendStatusComputerBlockDisp(serialNumber, statusList, computer, actionsToDo);
					// --- APERTURA REMOTA DE ENLACE WEB ---
					this.sendStatusComputerOpenWeb(serialNumber, statusList, actionsToDo);

					// --- INSTALACION REMOTA DE APLICACIONES ---
					this.sendStatusComputerInstallApp(serialNumber, statusList, actionsToDo);

					// --- DESISNSTALACION REMOTA DE APP ---
					this.sendStatusComputerUnistallApp(serialNumber, statusList, actionsToDo);

					// --- EJECUCION DE CFG WIFI ---
					this.sendStatusComputerCfgWifi(serialNumber, statusList, actionsToDo);

					// --- ACTUALIZACION DE JUNTA ANDALUCIA ---
					this.sendStatusComputerUpdateAndalucia(serialNumber, statusList, computer, actionsToDo);
					// --- ACTUALIZACION DE NUMERO DE SERIE ---
					this.sendStatusComputerUpdateSn(serialNumber, statusList, computer, actionsToDo);
					// --- ACTUALIZACION DE NUM DE CAJA ---
					this.sendStatusComputerUpdateCn(serialNumber, statusList, computer, actionsToDo);
				}
			}
			return ResponseEntity.ok().body(actionsToDo);
		}
		catch (Exception exception)
		{
			String error = "Server Error";
			ComputerError computerError = new ComputerError(404, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}

	/**
	 * Method sendStatusComputerUpdateCn
	 *
	 * @param serialNumber
	 * @param statusList
	 * @param computer
	 */
	private Actions sendStatusComputerUpdateCn(String serialNumber, List<Status> statusList, Computer computer,
			Actions actionsToDo)
	{
		for (int i = 0; i < this.updateComputerNumberComputerList.size(); i++)
		{
			Computer cmp = this.updateComputerNumberComputerList.get(i);
			if (cmp.getSerialNumber().equalsIgnoreCase(serialNumber))
			{
				// ------------------------- ACTUALIZACION DE NUM DE CAJA LOGIC---------
//				try
//				{
//					//  --- UPDATE FULL LIST WITH THE CMP FROM UPDATE TASKS  ---
//					this.computerList.remove(computer);
//					this.computerList.add(cmp);
//
//					Status status = new Status("Update computer number " + serialNumber, true,null);
//					statusList.add(status);
//					this.updateComputerNumberComputerList.remove(cmp);
//				}
//				catch (Exception exception)
//				{
//					Status status = new Status("Update Andalucia Id " + serialNumber, false,
//							new ComputerError(121, "error on Update computer number", null));
//					statusList.add(status);
//				}
				actionsToDo.setUpdateComputerNumber("cn123456566");
				this.updateComputerNumberComputerList.remove(cmp);
			}
		}
		return actionsToDo;
	}

	/**
	 * Method sendStatusComputerUpdateSn
	 *
	 * @param serialNumber
	 * @param statusList
	 * @param computer
	 */
	private Actions sendStatusComputerUpdateSn(String serialNumber, List<Status> statusList, Computer computer,
			Actions actionsToDo)
	{
		for (int i = 0; i < this.updateSerialNumberComputerList.size(); i++)
		{
			Computer cmp = this.updateSerialNumberComputerList.get(i);
			if (cmp.getSerialNumber().equalsIgnoreCase(serialNumber))
			{
				// ------------------------- ACTUALIZACION DE NUMERO DE SERIE LOGIC---------
//				try
//				{
//					//  --- UPDATE FULL LIST WITH THE CMP FROM UPDATE TASKS  ---
//					this.computerList.remove(computer);
//					this.computerList.add(cmp);
//
//					Status status = new Status("Update serial number " + serialNumber, true,null);
//					statusList.add(status);
//					this.updateSerialNumberComputerList.remove(cmp);
//				}
//				catch (Exception exception)
//				{
//					Status status = new Status("Update serial number " + serialNumber, false,
//							new ComputerError(121, "error on Update serial number", null));
//					statusList.add(status);
//				}
				actionsToDo.setUpdateSerialNumber(serialNumber);
				this.updateSerialNumberComputerList.remove(cmp);
			}
		}
		return actionsToDo;
	}

	/**
	 * Method sendStatusComputerUpdateAndalucia
	 *
	 * @param serialNumber
	 * @param statusList
	 * @param computer
	 */
	private Actions sendStatusComputerUpdateAndalucia(String serialNumber, List<Status> statusList, Computer computer,
			Actions actionsToDo)
	{
		for (int i = 0; i < this.updateAndaluciaComputerList.size(); i++)
		{
			Computer cmp = this.updateAndaluciaComputerList.get(i);
			if (cmp.getSerialNumber().equalsIgnoreCase(serialNumber))
			{
				// ------------------------- ACTUALIZACION DE JUNTA ANDALUCIA LOGIC---------
//				try
//				{
//					//  --- UPDATE FULL LIST WITH THE CMP FROM UPDATE TASKS  ---
//					this.computerList.remove(computer);
//					this.computerList.add(cmp);
//
//					Status status = new Status("Update Andalucia Id " + serialNumber, true,null);
//					statusList.add(status);
//					this.updateAndaluciaComputerList.remove(cmp);
//				}
//				catch (Exception exception)
//				{
//					Status status = new Status("Update Andalucia Id " + serialNumber, false,
//							new ComputerError(121, "error on Update Andalucia Id", null));
//					statusList.add(status);
//				}
				actionsToDo.setUpdateAndaluciaId("andaluciaIdUpdate");
				this.updateAndaluciaComputerList.remove(cmp);
			}
		}
		return actionsToDo;
	}

	/**
	 * Method sendStatusComputerCfgWifi
	 *
	 * @param serialNumber
	 * @param statusList
	 */
	private void sendStatusComputerCfgWifi(String serialNumber, List<Status> statusList, Actions actionsToDo)
	{
		for (int i = 0; i < this.configurationFileComputerList.size(); i++)
		{
			Computer cmp = this.configurationFileComputerList.get(i);
			if (cmp.getSerialNumber().equalsIgnoreCase(serialNumber))
			{
				// ------------------------- WIFI LOGIC--------------------
				actionsToDo.setConfigurationWifi("Wi-Fi-Andared_Corporativo.xml");
				this.configurationFileComputerList.remove(cmp);
			}
		}
	}

	/**
	 * Method sendStatusComputerUnistallApp
	 *
	 * @param serialNumber
	 * @param statusList
	 */
	private void sendStatusComputerUnistallApp(String serialNumber, List<Status> statusList, Actions actionsToDo)
	{
		// --- FOR EACH COMPUTER ON THE UNINSTALL APP COMPUTER LIST ---
		for (int i = 0; i < this.uninstallAppComputerList.size(); i++)
		{
			Computer cmp = this.uninstallAppComputerList.get(i);
			// --- GET THE COMPUTER AND GET THE LIST OF APPS TO UNINSTALL ---
			if (cmp.getSerialNumber().equalsIgnoreCase(serialNumber))
			{
				// ------------------------- Actions to do uninstall
				// AppLOGIC-------------------------------
				List<String> unInstallAppList = new ArrayList<>();

				// --- FOR EACH APP TO UNINSTALL , GET THE NAME/ID AND PUT IN LIST<STRING> FOR
				// ACTIONS OBJECT ---
				for (Software app : cmp.getSoftwareList())
				{

					unInstallAppList.add(app.getApplication());
				}
				// --- SET THE LIST INTO ACTIONS OBJECTS ---
				actionsToDo.setUninstallApps(unInstallAppList);

				// --- REMOVE THE COMPUTER FROM THE UNINSTALL LIST ---
				this.uninstallAppComputerList.remove(cmp);
			}
		}
	}

	/**
	 * Method sendStatusComputerInstallApp
	 *
	 * @param serialNumber
	 * @param statusList
	 */
	private void sendStatusComputerInstallApp(String serialNumber, List<Status> statusList, Actions actionsToDo)
	{
		// --- FOR EACH COMPUTER ON THE ISNTALL APP COMPUTER LIST ---
		for (int i = 0; i < this.installAppComputerList.size(); i++)
		{
			Computer cmp = this.installAppComputerList.get(i);
			// --- GET THE COMPUTER AND GET THE LIST OF APPS TO INSTALL ---
			if (cmp.getSerialNumber().equalsIgnoreCase(serialNumber))
			{
				// ------------------------- Actions to do Install
				// AppLOGIC-------------------------------
				List<String> installAppList = new ArrayList<>();

				// --- FOR EACH APP TO INSTALL , GET THE NAME/ID AND PUT IN LIST<STRING> FOR
				// ACTIONS OBJECT ---
				for (Software app : cmp.getSoftwareList())
				{
					installAppList.add(app.getApplication());
				}
				// --- SET THE LIST INTO ACTIONS OBJECTS ---
				actionsToDo.setInstallApps(installAppList);

				// --- REMOVE THE COMPUTER FROM THE INSTALL LIST ---
				this.installAppComputerList.remove(cmp);
			}
		}
	}

	/**
	 * Method sendStatusComputerOpenWeb
	 *
	 * @param serialNumber
	 * @param statusList
	 */
	private void sendStatusComputerOpenWeb(String serialNumber, List<Status> statusList, Actions actionsToDo)
	{
		for (int i = 0; i < this.openWebComputerList.size(); i++)
		{
			// ------------------------- OPEN WEB -------------------------------
			Computer cmp = this.openWebComputerList.get(i);
			if (cmp.getSerialNumber().equalsIgnoreCase(serialNumber))
			{
				List<String> webCommands = new ArrayList<>();
				for (String command : cmp.getCommandLine().getCommands())
				{
					if (command.contains("start chrome"))
					{
						webCommands.add(command);
					}
				}
				actionsToDo.setOpenWebs(webCommands);
				this.openWebComputerList.remove(cmp);
			}
		}
	}

	/**
	 * Method sendStatusComputerBlockDisp
	 *
	 * @param serialNumber
	 * @param statusList
	 * @param computer
	 */
	private void sendStatusComputerBlockDisp(String serialNumber, List<Status> statusList, Computer computer,
			Actions actionsToDo)
	{
		for (int i = 0; i < this.blockDispositiveComputerList.size(); i++)
		{
			Computer cmp = this.blockDispositiveComputerList.get(i);
			if (cmp.getSerialNumber().equalsIgnoreCase(serialNumber))
			{
				// ------------------------- BLOCK DISP.LOGIC-------------------------------
				List<Peripheral> peripheralList = new ArrayList<>();
				Peripheral peripheral = (Peripheral) cmp.getHardwareList().get(0);
				peripheralList.add(peripheral);
				actionsToDo.setBlockDispositives(peripheralList);
				this.blockDispositiveComputerList.remove(cmp);
			}
		}
	}

	/**
	 * Method sendStatusComputerCommandExecute
	 *
	 * @param serialNumber
	 * @param statusList
	 */
	private void sendStatusComputerCommandExecute(String serialNumber, List<Status> statusList, Actions actionsToDo)
	{
		for (int i = 0; i < this.execCommandsComputerList.size(); i++)
		{
			Computer cmp = this.execCommandsComputerList.get(i);
			if (cmp.getSerialNumber().equalsIgnoreCase(serialNumber))
			{
				// ------------------------- COMMAND EXECUTE
				// LOGIC-------------------------------
				actionsToDo.setCommands(cmp.getCommandLine().getCommands());
				this.execCommandsComputerList.remove(cmp);
			}
		}
	}

	/**
	 * Method sendStatusComputerRestart
	 *
	 * @param serialNumber
	 * @param statusList
	 */
	private void sendStatusComputerRestart(String serialNumber, List<Status> statusList, Actions actionsToDo)
	{
		for (int i = 0; i < this.restartDownComputerList.size(); i++)
		{
			Computer cmp = this.restartDownComputerList.get(i);
			if (cmp.getSerialNumber().equalsIgnoreCase(serialNumber))
			{
				// ------------------------- RESTART LOGIC -------------------------------
				actionsToDo.setRestart(true);
				this.restartDownComputerList.remove(cmp);
			}
		}
	}

	/**
	 * Method sendStatusComputerShutdown
	 *
	 * @param serialNumber
	 * @param statusList
	 */
	private void sendStatusComputerShutdown(String serialNumber, List<Status> statusList, Actions actionsToDo)
	{
		for (int i = 0; i < this.shutDownComputerList.size(); i++)
		{
			Computer cmp = this.shutDownComputerList.get(i);
			if (cmp.getSerialNumber().equalsIgnoreCase(serialNumber))
			{
				// ------------------------- SHUTDOWN LOGIC-------------------------------
				actionsToDo.setShutdown(true);
				this.shutDownComputerList.remove(cmp);
			}

		}
	}

	/**
	 * Method sendFullByComputerNumber
	 *
	 * @param computerNumber
	 * @param computerInstance
	 */
	private void sendFullByComputerNumber(String computerNumber, Computer computerInstance)
	{
		for (int i = 0; i < this.computerList.size(); i++)
		{
			Computer listedComputer = this.computerList.get(i);
			if (listedComputer.getComputerNumber().equalsIgnoreCase(computerNumber))
			{
				this.computerList.set(i, computerInstance);
			}
		}
	}

	/**
	 * Method sendFullByAndaluciaId
	 *
	 * @param andaluciaId
	 * @param computerInstance
	 */
	private void sendFullByAndaluciaId(String andaluciaId, Computer computerInstance)
	{
		for (int i = 0; i < this.computerList.size(); i++)
		{
			Computer listedComputer = this.computerList.get(i);
			if (listedComputer.getAndaluciaID().equalsIgnoreCase(andaluciaId))
			{
				this.computerList.set(i, computerInstance);
			}
		}
	}

	/**
	 * Method sendFullBySerialNumber
	 *
	 * @param serialNumber
	 * @param computerInstance
	 */
	private void sendFullBySerialNumber(String serialNumber, Computer computerInstance)
	{
		for (int i = 0; i < this.computerList.size(); i++)
		{
			Computer listedComputer = this.computerList.get(i);
			if (listedComputer.getSerialNumber().equalsIgnoreCase(serialNumber))
			{
				this.computerList.set(i, computerInstance);
			}
		}
	}

	/**
	 * Method checkNullEmpty
	 *
	 * @param serialNumber
	 * @return
	 */
	private boolean checkIsBlankEmpty(String strigParameter)
	{
		if (strigParameter.isBlank() || strigParameter.isEmpty())
		{
			return true;
		}
		return false;
	}

}

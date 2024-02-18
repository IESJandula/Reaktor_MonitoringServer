package es.iesjandula.reaktor.monitoring_server.rest;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
import es.iesjandula.reaktor.models.Reaktor;
import es.iesjandula.reaktor.models.Task;
import es.iesjandula.reaktor.models.Usb;
import es.iesjandula.reaktor.models.Id.TaskId;
import es.iesjandula.reaktor.monitoring_server.repository.IActionRepository;
import es.iesjandula.reaktor.monitoring_server.repository.IMotherboardRepository;
import es.iesjandula.reaktor.monitoring_server.repository.ITaskRepository;
import es.iesjandula.reaktor.monitoring_server.utils.Constants;
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

	/**
	 * Attribute iTaskRepository
	 */
	@Autowired
	private ITaskRepository iTaskRepository;

	/**
	 * Attribute iMotherboardRepository
	 */
	@Autowired
	private IMotherboardRepository iMotherboardRepository;

	/**
	 * Attribute iActionRepository
	 */
	@Autowired
	private IActionRepository iActionRepository;

	/**
	 * Method postComputerCommandLine
	 * @param serialNumber
	 * @param wifiFile
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/admin/wifiCfg")
	public ResponseEntity<?> postWifiCfg(
			@RequestHeader(required = true) String serialNumber,
			@RequestHeader(required = true) String wifiFileName)
	{
		try
		{
			// --- GETTING THE COMMAND BLOCK ----
			Set<Motherboard> commands = new HashSet<Motherboard>();

			String methodsUsed = "";

			// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
			if (this.checkEmptys(serialNumber, "_", "_", "_"))
			{
				String error = "Any Paramater Is Empty or Blank";
				ComputerError computerError = new ComputerError(404, error, null);
				return ResponseEntity.status(404).body(computerError.toMap());
			}

			if (serialNumber != null)
			{
				// ALL COMMANDS ON SPECIFIC COMPUTER BY serialNumber
				this.addBySerialNumber(serialNumber, commands);
				methodsUsed += "serialNumber,";
			}
			log.info("Parameters Used: " + methodsUsed);

			Optional<Action> actionId = this.iActionRepository.findById("configWifi");

			if (actionId.isPresent())
			{
				this.addTasks(commands, actionId.get(), "./confWIFI/"+wifiFileName);
			}

			// --- RETURN OK RESPONSE ---
			return ResponseEntity.ok().build();
			
		}
		catch (Exception exception)
		{
			String error = "Error on openWeb admin";
			log.error(error,exception);
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}

	}
	/**
	 * Method postComputerCommandLine
	 * @param serialNumber
	 * @param webURL
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/admin/chrome/openWeb")
	public ResponseEntity<?> postOpenWeb(
			@RequestHeader(required = true) String serialNumber,
			@RequestHeader(required = true) String webURL)
	{
		try
		{
			// --- GETTING THE COMMAND BLOCK ----
			Set<Motherboard> commands = new HashSet<Motherboard>();

			String methodsUsed = "";

			// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
			if (this.checkEmptys(serialNumber, "_", "_", "_"))
			{
				String error = "Any Paramater Is Empty or Blank";
				ComputerError computerError = new ComputerError(404, error, null);
				return ResponseEntity.status(404).body(computerError.toMap());
			}

			if (serialNumber != null)
			{
				// ALL COMMANDS ON SPECIFIC COMPUTER BY serialNumber
				this.addBySerialNumber(serialNumber, commands);
				methodsUsed += "serialNumber,";
			}
			log.info("Parameters Used: " + methodsUsed);

			Optional<Action> actionId = this.iActionRepository.findById("openWeb");

			if (actionId.isPresent())
			{
				this.addTasks(commands, actionId.get(), webURL);
			}

			// --- RETURN OK RESPONSE ---
			return ResponseEntity.ok().build();
			
		}
		catch (Exception exception)
		{
			String error = "Error on openWeb admin";
			log.error(error,exception);
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}

	}
	
	
	/**
	 * Method sendInformation to send information of commands to computers
	 *
	 * @param serialNumber the serial number of computer
	 * @param classroom    the classroom
	 * @param trolley      the trolley
	 * @param floor        the floor
	 * @param commandLine  the commnadLine Object
	 * @return ResponseEntity
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/admin/commandLine")
	public ResponseEntity<?> postComputerCommandLine(@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String classroom, @RequestHeader(required = false) String trolley,
			@RequestHeader(required = false) Integer floor, @RequestHeader(required = true) String commandLine)
	{
		try
		{
			// --- GETTING THE COMMAND BLOCK ----
			Set<Motherboard> commands = new HashSet<Motherboard>();

			// --- IF ANY OF THE PARAMETERS IS NOT NULL ---
			if ((serialNumber != null) || (classroom != null) || (trolley != null) || (floor != null))
			{
				String methodsUsed = "";

				// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
				if (this.checkEmptys(serialNumber, "_", classroom, trolley))
				{
					String error = "Any Paramater Is Empty or Blank";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}

				if (serialNumber != null)
				{
					// ALL COMMANDS ON SPECIFIC COMPUTER BY serialNumber
					this.addBySerialNumber(serialNumber, commands);
					methodsUsed += "serialNumber,";
				}
				if (trolley != null)
				{
					// ALL COMMANDS ON SPECIFIC COMPUTER BY trolley
					this.addByTrolley(trolley, commands);
					methodsUsed += "trolley,";
				}
				if (classroom != null)
				{
					// ALL COMMANDS ON SPECIFIC COMPUTER BY classroom
					this.addByClassroom(classroom, commands);
					methodsUsed += "classroom,";
				}
				if (floor != null)
				{
					// ALL COMMANDS ON SPECIFIC COMPUTER BY floor
					this.addByFloor(floor, commands);
					methodsUsed += "floor,";
				}
				log.info("Parameters Used: " + methodsUsed);

				Optional<Action> actionId = this.iActionRepository.findById("command");

				if (actionId.isPresent())
				{
					this.addTasks(commands, actionId.get(), commandLine);
				}

				// --- RETURN OK RESPONSE ---
				return ResponseEntity.ok().build();
			}
			else
			{
				// COMMANDS RUN ON ALL COMPUTERS
				this.addByAll(commands);
				Optional<Action> actionId = this.iActionRepository.findById("command");

				if (actionId.isPresent())
				{
					this.addTasks(commands, actionId.get(), commandLine);
				}
				log.info("By all Computers");
				return ResponseEntity.ok().build();
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
	 * Method shutdownComputers
	 * 
	 * @param serialNumber
	 * @param classroom
	 * @param trolley
	 * @param floor
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/admin/shutdown")
	public ResponseEntity<?> putComputerShutdown(@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String classroom, @RequestHeader(required = false) String trolley,
			@RequestHeader(required = false) Integer floor)
	{
		try
		{
			Set<Motherboard> shutdownList = new HashSet<Motherboard>();

			// --- IF ANY OF THE PARAMETERS IS NOT NULL ---
			if ((serialNumber != null) || (classroom != null) || (trolley != null) || (floor != null))
			{
				String methodsUsed = "";

				// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
				if (this.checkEmptys(serialNumber, "_", classroom, trolley))
				{
					String error = "Any Paramater Is Empty or Blank";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}

				if (serialNumber != null)
				{
					// SHUTDOWN SPECIFIC COMPUTER BY serialNumber
					this.addBySerialNumber(serialNumber, shutdownList);
					methodsUsed += "serialNumber,";
				}
				if (trolley != null)
				{
					// SHUTDOWN SPECIFIC COMPUTER BY trolley
					this.addByTrolley(trolley, shutdownList);
					methodsUsed += "trolley,";
				}
				if (classroom != null)
				{
					// SHUTDOWN SPECIFIC COMPUTER BY classroom
					this.addByClassroom(classroom, shutdownList);
					methodsUsed += "classroom,";
				}
				if (floor != null)
				{
					// SHUTDOWN SPECIFIC COMPUTER BY floor
					this.addByFloor(floor, shutdownList);
					methodsUsed += "floor,";
				}
				log.info("Parameters Used: " + methodsUsed);

				Optional<Action> actionId = this.iActionRepository.findById("shutdown");

				if (actionId.isPresent())
				{
					this.addTasks(shutdownList, actionId.get(), "");
				}
				// --- RETURN OK RESPONSE ---
				return ResponseEntity.ok().build();
			}
			else
			{
				// SHUTDOWN ALL COMPUTERS
				this.addByAll(shutdownList);
				Optional<Action> actionId = this.iActionRepository.findById("shutdown");

				if (actionId.isPresent())
				{
					this.addTasks(shutdownList, actionId.get(), "");
				}
				log.info("By all Computers");
				return ResponseEntity.ok().build();
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
	 * Method shutdownComputers
	 * 
	 * @param serialNumber
	 * @param classroom
	 * @param trolley
	 * @param floor
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/admin/restart")
	public ResponseEntity<?> putComputerRestart(@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String classroom, @RequestHeader(required = false) String trolley,
			@RequestHeader(required = false) Integer floor)
	{
		try
		{
			Set<Motherboard> restartList = new HashSet<Motherboard>();

			// --- IF ANY OF THE PARAMETERS IS NOT NULL ---
			if ((serialNumber != null) || (classroom != null) || (trolley != null) || (floor != null))
			{
				String methodsUsed = "";

				// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
				if (this.checkEmptys(serialNumber, "_", classroom, trolley))
				{
					String error = "Any Paramater Is Empty or Blank";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}

				if (serialNumber != null)
				{
					this.addBySerialNumber(serialNumber, restartList);
					methodsUsed += "serialNumber,";
				}
				if (trolley != null)
				{
					this.addByTrolley(trolley, restartList);
					methodsUsed += "trolley,";
				}
				if (classroom != null)
				{
					this.addByClassroom(classroom, restartList);
					methodsUsed += "classroom,";
				}
				if (floor != null)
				{
					this.addByFloor(floor, restartList);
					methodsUsed += "floor,";
				}
				log.info("Parameters Used: " + methodsUsed);

				Optional<Action> actionId = this.iActionRepository.findById("restart");

				if (actionId.isPresent())
				{
					this.addTasks(restartList, actionId.get(), "");
				}
				// --- RETURN OK RESPONSE ---
				return ResponseEntity.ok().build();
			}
			else
			{
				this.addByAll(restartList);
				Optional<Action> actionId = this.iActionRepository.findById("restart");

				if (actionId.isPresent())
				{
					this.addTasks(restartList, actionId.get(), "");
				}
				log.info("By all Computers");
				return ResponseEntity.ok().build();
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
	 * Method postPeripheral
	 * 
	 * @param classroom
	 * @param trolley
	 * @param hardwareComponent
	 * @return ResponseEntity
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/admin/peripheral", consumes = "application/json")
	public ResponseEntity<?> postPeripheral(@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley, @RequestBody(required = true) Usb usb)
	{
		try
		{

			Set<Motherboard> motherboardList = new HashSet<Motherboard>();
			// --- IF ANY OF THE PARAMETERS IS NOT NULL ---
			if ((classroom != null) || (trolley != null))
			{
				String methodsUsed = "";

				// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
				if (this.checkEmptys("_", "_", classroom, trolley))
				{
					String error = "Any Paramater Is Empty or Blank";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}

				if (trolley != null)
				{
					List<Motherboard> list = this.iMotherboardRepository.findByTrolley(trolley);
					motherboardList.addAll(list);
					methodsUsed += "trolley,";
				}
				if (classroom != null)
				{
					// ON SPECIFIC COMPUTER BY classroom
					List<Motherboard> list = this.iMotherboardRepository.findByClassroom(classroom);
					motherboardList.addAll(list);
					methodsUsed += "classroom,";
				}

				Optional<Action> action = this.iActionRepository.findById("postPeripheral");

				if (action.isPresent())
				{
					this.addTasks(motherboardList, action.get(), usb.getId().toString());
				}

				log.info("Parameters Used: " + methodsUsed);
				// --- RETURN OK RESPONSE ---
				return ResponseEntity.ok().build();
			}
			else
			{
				// ON ALL COMPUTERS
				List<Motherboard> list = this.iMotherboardRepository.findAll();
				motherboardList.addAll(list);

				Optional<Action> action = this.iActionRepository.findById("postPeripheral");

				if (action.isPresent())
				{
					this.addTasks(motherboardList, action.get(), usb.getId().toString());
				}

				log.info("By all Computers");
				return ResponseEntity.ok().build();
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
	 * Method sendScreenshotOrder
	 * 
	 * @param classroom
	 * @param trolley
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/admin/screenshot")
	public ResponseEntity<?> sendScreenshotOrder(@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley, @RequestHeader(required = false) String serialNumber)
	{
		try
		{
			Set<Motherboard> screenshotList = new HashSet<Motherboard>();

			// --- IF ANY OF THE PARAMETERS IS NOT NULL ---
			if ((classroom != null) || (trolley != null))
			{
				String methodsUsed = "";

				// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
				if (this.checkEmptys(serialNumber, "_", classroom, trolley))
				{
					String error = "Any Paramater Is Empty or Blank";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}
				if (serialNumber != null)
				{
					this.addBySerialNumber(serialNumber, screenshotList);
					methodsUsed += "serialNumber,";
				}
				if (trolley != null)
				{
					this.addByTrolley(trolley, screenshotList);
					methodsUsed += "trolley,";
				}
				if (classroom != null)
				{
					this.addByClassroom(classroom, screenshotList);
					methodsUsed += "classroom,";
				}

				log.info("Parameters Used: " + methodsUsed);
				Optional<Action> actionId = this.iActionRepository.findById("screenshot");

				if (actionId.isPresent())
				{
					this.addTasks(screenshotList, actionId.get(), "");
				}
				// --- RETURN OK RESPONSE ---
				return ResponseEntity.ok().build();
			}
			else
			{
				this.addByAll(screenshotList);
				Optional<Action> actionId = this.iActionRepository.findById("screenshot");

				if (actionId.isPresent())
				{
					this.addTasks(screenshotList, actionId.get(), "");
				}
				log.info("By all Computers");
				return ResponseEntity.ok().build();
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
	 * Method sendSoftware
	 * 
	 * @param classroom
	 * @param trolley
	 * @param softwareInstance
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/admin/software")
	public ResponseEntity<?> sendSoftware(
			@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley, 
			@RequestHeader(required = false) String professor,
			@RequestHeader(required = true) String software)
	{
		try
		{
			Set<Motherboard> motherboardSet = new HashSet<Motherboard>();
			// --- IF ANY OF THE PARAMETERS IS NOT NULL ---
			if ((classroom != null) || (trolley != null) || (professor != null))
			{
				String methodsUsed = "";

				// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
				if (this.checkEmptys("_", professor, classroom, trolley))
				{
					String error = "Any Paramater Is Empty or Blank";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}
				if (professor != null)
				{
					List<Motherboard> motherboardList = this.iMotherboardRepository.findByTeacher(professor);
					motherboardSet.addAll(motherboardList);
					methodsUsed += "professor,";
				}
				if (trolley != null)
				{
					// ON SPECIFIC COMPUTER BY trolley
					List<Motherboard> motherboardList = this.iMotherboardRepository.findByTrolley(trolley);
					motherboardSet.addAll(motherboardList);
					methodsUsed += "trolley,";
				}
				if (classroom != null)
				{
					// ON SPECIFIC COMPUTER BY classroom
					List<Motherboard> motherboardList = this.iMotherboardRepository.findByClassroom(classroom);
					motherboardSet.addAll(motherboardList);
					methodsUsed += "classroom,";
				}

				Optional<Action> action = this.iActionRepository.findById("install");
				if (!action.isEmpty())
				{
					this.addTasks(motherboardSet, action.get(), software);
				}

				log.info("Parameters Used: " + methodsUsed);
				// --- RETURN OK RESPONSE ---
				return ResponseEntity.ok().build();
			}
			else
			{
				// ON ALL COMPUTERS
				List<Motherboard> motherboardList = this.iMotherboardRepository.findAll();
				motherboardSet.addAll(motherboardList);
				log.info("By all Computers");
				Optional<Action> action = this.iActionRepository.findById("install");
				if (!action.isEmpty())
				{
					this.addTasks(motherboardSet, action.get(), software);
				}
				return ResponseEntity.ok().build();
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
	 * Method unistallSoftware
	 * 
	 * @param classroom
	 * @param trolley
	 * @param professor
	 * @param softwareInstance
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = "/admin/software")
	public ResponseEntity<?> unistallSoftware(
			@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley, 
			@RequestHeader(required = false) String professor,
			@RequestHeader(required = true) String software)
	{
		try
		{
			Set<Motherboard> motherboardSet = new HashSet<Motherboard>();
			// --- IF ANY OF THE PARAMETERS IS NOT NULL ---
			if ((classroom != null) || (trolley != null))
			{
				String methodsUsed = "";

				// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
				if (this.checkEmptys("_", professor, classroom, trolley))
				{
					String error = "Any Paramater Is Empty or Blank";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}

				if (professor != null)
				{
					// ON SPECIFIC COMPUTER BY professor
					List<Motherboard> motherboardList = this.iMotherboardRepository.findByTeacher(professor);
					motherboardSet.addAll(motherboardList);
					methodsUsed += "professor,";
				}
				if (trolley != null)
				{
					// ON SPECIFIC COMPUTER BY trolley
					List<Motherboard> motherboardList = this.iMotherboardRepository.findByTrolley(trolley);
					motherboardSet.addAll(motherboardList);
					methodsUsed += "trolley,";
				}
				if (classroom != null)
				{
					// ON SPECIFIC COMPUTER BY classroom
					List<Motherboard> motherboardList = this.iMotherboardRepository.findByClassroom(classroom);
					motherboardSet.addAll(motherboardList);
					methodsUsed += "classroom,";
				}
				Optional<Action> action = this.iActionRepository.findById("uninstall");
				if (!action.isEmpty())
				{
					this.addTasks(motherboardSet, action.get(), software);
				}

				log.info("Parameters Used: " + methodsUsed);
				// --- RETURN OK RESPONSE ---
				return ResponseEntity.ok().build();
			}
			else
			{
				// ON ALL COMPUTERS
				List<Motherboard> motherboardList = this.iMotherboardRepository.findAll();
				motherboardSet.addAll(motherboardList);
				log.info("By all Computers");
				Optional<Action> action = this.iActionRepository.findById("uninstall");
				if (!action.isEmpty())
				{
					this.addTasks(motherboardSet, action.get(), software);
				}
				return ResponseEntity.ok().build();
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
	 * Method updateComputer
	 * 
	 * @param serialNumber
	 * @param andaluciaId
	 * @param computerNumber
	 * @param computerInstance
	 * @return ResponseEntity
	 */
	@RequestMapping(method = RequestMethod.PUT, value = "/computer/edit", consumes = "application/json")
	public ResponseEntity<?> updateComputer(@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String andaluciaId, @RequestHeader(required = false) String computerNumber,
			@RequestBody(required = true) Reaktor reaktorInstance)
	{
		try
		{
			// --- IF ANY OF THE PARAMETERS IS NOT NULL ---
			if ((serialNumber != null) || (andaluciaId != null) || (computerNumber != null))
			{
				String methodsUsed = "";

				// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
				if (this.checkEmptyIds(serialNumber, andaluciaId, computerNumber))
				{
					String error = "Any Paramater Is Empty or Blank";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}

				if (serialNumber != null)
				{
					// ON SPECIFIC COMPUTER BY serialNumber
					Motherboard motherboard = this.iMotherboardRepository.findByMotherBoardSerialNumber(serialNumber);

					this.updateMotherboard(reaktorInstance, motherboard);

					methodsUsed += "serialNumber,";
				}
				if (andaluciaId != null)
				{
					// ON SPECIFIC COMPUTER BY trolley
					List<Motherboard> motherboardList = this.iMotherboardRepository.findByAndaluciaId(andaluciaId);

					for (Motherboard motherboard : motherboardList)
					{
						this.updateMotherboard(reaktorInstance, motherboard);
					}

					methodsUsed += "trolley,";
				}
				if (computerNumber != null)
				{
					// ON SPECIFIC COMPUTER BY classroom
					List<Motherboard> motherboardList = this.iMotherboardRepository
							.findByComputerNumber(computerNumber);

					for (Motherboard motherboard : motherboardList)
					{
						this.updateMotherboard(reaktorInstance, motherboard);
					}
					methodsUsed += "classroom,";
				}

				log.info("Parameters Used: " + methodsUsed);
				// --- RETURN OK RESPONSE ---
				return ResponseEntity.ok().build();
			}
			else
			{
				// ALL COMPUTERS
				String error = "No parameters selected";
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
	 * Method to update Motherboard
	 * 
	 * @param reaktorInstance
	 * @param motherboard
	 */
	private void updateMotherboard(Reaktor reaktorInstance, Motherboard motherboard)
	{
		motherboard.setAndaluciaId(reaktorInstance.getMotherboard().getAndaluciaId());
		motherboard.setClassroom(reaktorInstance.getMotherboard().getClassroom());
		motherboard.setComputerNumber(reaktorInstance.getMotherboard().getComputerNumber());
		motherboard.setComputerOn(reaktorInstance.getMotherboard().getComputerOn());
		motherboard.setComputerSerialNumber(reaktorInstance.getMotherboard().getComputerSerialNumber());
		motherboard.setIsAdmin(reaktorInstance.getMotherboard().getIsAdmin());
		motherboard.setLastConnection(reaktorInstance.getMotherboard().getLastConnection());
		motherboard.setLastUpdateComputerOn(reaktorInstance.getMotherboard().getLastUpdateComputerOn());
		motherboard.setMalware(reaktorInstance.getMotherboard().getMalware());
		motherboard.setModel(reaktorInstance.getMotherboard().getModel());
		motherboard.setMotherBoardSerialNumber(reaktorInstance.getMotherboard().getMotherBoardSerialNumber());
		motherboard.setTasks(reaktorInstance.getMotherboard().getTasks());
		motherboard.setTeacher(reaktorInstance.getMotherboard().getTeacher());
		motherboard.setTrolley(reaktorInstance.getMotherboard().getTrolley());

		this.iMotherboardRepository.save(motherboard);
		this.iMotherboardRepository.flush();
	}

	/**
	 * Method checkEmptyIds
	 * 
	 * @param serialNumber
	 * @param computerNumber
	 * @param computerNumber2
	 * @return
	 */
	private boolean checkEmptyIds(String serialNumber, String andaluciaId, String computerNumber)
	{
		if (serialNumber != null)
		{
			if (serialNumber.isBlank() || serialNumber.isEmpty())
			{
				// --- IF IS PARAMETER IS BLANK OR EMPTY ---
				String error = "serialNumber Is Empty or Blank";
				log.error(error);
				return true;
			}
		}
		if (andaluciaId != null)
		{
			if (andaluciaId.isBlank() || andaluciaId.isEmpty())
			{
				// --- IF IS PARAMETER IS BLANK OR EMPTY ---
				String error = "andaluciaId Is Empty or Blank";
				log.error(error);
				return true;
			}
		}
		if (computerNumber != null)
		{
			if (computerNumber.isBlank() || computerNumber.isEmpty())
			{
				// --- IF IS PARAMETER IS BLANK OR EMPTY ---
				String error = "computerNumber Is Empty or Blank";
				log.error(error);
				return true;
			}
		}
		return false;
	}

	/**
	 * Method checkEmptysProfessorClassTrolley
	 * 
	 * @param professor
	 * @param classroom
	 * @param trolley
	 * @return
	 */
	private boolean checkEmptys(String serialNumber, String professor, String classroom, String trolley)
	{
		if (serialNumber != null)
		{
			if (serialNumber.isBlank() || serialNumber.isEmpty())
			{
				// --- IF IS PARAMETER IS BLANK OR EMPTY ---
				String error = "Serial Number Is Empty or Blank";
				log.error(error);
				return true;
			}
		}
		if (professor != null)
		{
			if (professor.isBlank() || professor.isEmpty())
			{
				// --- IF IS PARAMETER IS BLANK OR EMPTY ---
				String error = "Professor Is Empty or Blank";
				log.error(error);
				return true;
			}
		}
		if (trolley != null)
		{
			if (trolley.isBlank() || trolley.isEmpty())
			{
				// --- IF IS PARAMETER IS BLANK OR EMPTY ---
				String error = "Trolley Is Empty or Blank";
				log.error(error);
				return true;
			}
		}
		if (classroom != null)
		{
			if (classroom.isBlank() || classroom.isEmpty())
			{
				// --- IF IS PARAMETER IS BLANK OR EMPTY ---
				String error = "Classroom Is Empty or Blank";
				log.error(error);
				return true;
			}
		}
		return false;
	}

	/**
	 * Method sendInformation to send information of commands to computers
	 *
	 * @param serialNumber the serial number of computer
	 * @param classroom    the classroom
	 * @param trolley      the trolley
	 * @param floor        the floor
	 * @param File         the execFile
	 * @return ResponseEntity
	 */
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
			Set<Motherboard> fileList = new HashSet<Motherboard>();
			if ((serialNumber != null) || (classroom != null) || (trolley != null) || (floor != null))
			{
				// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
				if (this.checkEmptys(serialNumber, "_", classroom, trolley))
				{
					String error = "Any Paramater Is Empty or Blank";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}

				if (serialNumber != null)
				{
					// ALL FILE ON SPECIFIC COMPUTER BY serialNumber
					this.addBySerialNumber(serialNumber, fileList);

				}
				if (trolley != null)
				{
					// ALL FILE ON SPECIFIC COMPUTER BY trolley
					this.addByTrolley(trolley, fileList);

				}
				if (classroom != null)
				{
					// ALL FILE ON SPECIFIC COMPUTER BY classroom
					this.addByClassroom(classroom, fileList);

				}
				if (floor != null)
				{
					// ALL FILE ON SPECIFIC COMPUTER BY floor
					this.addByFloor(floor, fileList);

				}
				// --- RETURN OK RESPONSE ---

				this.writeText(Constants.FILE_FOLDER + fileName, execFile.getBytes());
				Optional<Action> actionId = this.iActionRepository.findById("file");

				if (actionId.isPresent())
				{
					this.addTasks(fileList, actionId.get(), Constants.FILE_FOLDER + fileName);
				}

				return ResponseEntity.ok().build();
			}
			else
			{
				// COMMANDS RUN ON ALL COMPUTERS
				this.addByAll(fileList);
				Optional<Action> actionId = this.iActionRepository.findById("file");

				if (actionId.isPresent())
				{
					this.addTasks(fileList, actionId.get(), Constants.FILE_FOLDER + execFile.getName());
				}
				return ResponseEntity.ok().build();
			}
		}
		catch (Exception exception)
		{
			log.error(exception.getMessage(), exception);
			ComputerError computerError = new ComputerError(500, exception.getMessage(), exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}

	}

	/**
	 * Method writeText
	 * 
	 * @param name
	 * @param content
	 */
	public void writeText(String name, byte[] content)
	{

		FileOutputStream fileOutputStream = null;

		DataOutputStream dataOutputStream = null;

		try
		{
			fileOutputStream = new FileOutputStream(name);

			dataOutputStream = new DataOutputStream(fileOutputStream);

			dataOutputStream.write(content);

			dataOutputStream.flush();

		}
		catch (IOException exception)
		{
			String message = "Error";
			log.error(message, exception);
		}
		finally
		{
			if (dataOutputStream != null)
			{
				try
				{
					dataOutputStream.close();
				}
				catch (IOException exception)
				{
					String message = "Error";
					log.error(message, exception);
				}
			}

			if (fileOutputStream != null)
			{
				try
				{
					fileOutputStream.close();
				}
				catch (IOException exception)
				{
					String message = "Error";
					log.error(message, exception);
				}
			}
		}
	}

	/**
	 * Method handleTypeMismatch method for the spring interal input mismatch
	 * parameter
	 *
	 * @param MethodArgumentTypeMismatchException exception
	 * @return ResponseEntity
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

	/**
	 * Method getComputer
	 * 
	 * @author Adrian
	 * @param classroom
	 * @param trolley
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/computer/admin/screenshot", produces = "application/zip")
	public ResponseEntity<?> getScreenshot(@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley)
	{
		try
		{
			if (classroom.isEmpty() && trolley.isEmpty())
			{
				this.checkParams(classroom, trolley);

				File zipFile = this.getZipFile(classroom, trolley);
			}
			return ResponseEntity.ok().build();
		}
		catch (ComputerError error)
		{
			return ResponseEntity.status(400).body(error.getMessage());
		}
		catch (Exception error)
		{
			return ResponseEntity.status(500).body(error.getMessage());
		}
	}

	/**
	 * Method checkParams
	 * 
	 * @author Adrian
	 * @param classroom
	 * @param trolley
	 * @throws ComputerError
	 */
	private void checkParams(String classroom, String trolley) throws ComputerError
	{
		if (classroom.isEmpty() && trolley.isEmpty())
		{
			throw new ComputerError(400, "Error", null);
		}
	}

	/**
	 * Method getZipFile
	 * 
	 * @author Adrian
	 * @param classroom
	 * @param trolley
	 * @return
	 * @throws Exception
	 */
	private File getZipFile(String classroom, String trolley) throws Exception
	{

		File zipFile = File.createTempFile("screenshots", ".zip");
		try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile)))
		{
			zipOutputStream.putNextEntry(new ZipEntry("screenshot.png"));
			zipOutputStream.write("Contenido de la captura de pantalla".getBytes());
			zipOutputStream.closeEntry();
		}
		return zipFile;
	}

	/**
	 * Method addByAll
	 * 
	 * @param motherboardList
	 */
	private void addByAll(Set<Motherboard> motherboardList)
	{
		List<Motherboard> motherboardId = this.iMotherboardRepository.findAll();
		motherboardList.addAll(motherboardId);
	}

	/**
	 * Method addByClassroom
	 * 
	 * @param classroom
	 * @param motherboardList
	 */
	private void addByClassroom(String classroom, Set<Motherboard> motherboardList)
	{
		List<Motherboard> motherboardId = this.iMotherboardRepository.findByClassroom(classroom);
		motherboardList.addAll(motherboardId);
	}

	/**
	 * Method addByTrolley
	 * 
	 * @param trolley
	 * @param motherboardList
	 */
	private void addByTrolley(String trolley, Set<Motherboard> motherboardList)
	{
		List<Motherboard> motherboardId = this.iMotherboardRepository.findByTrolley(trolley);
		motherboardList.addAll(motherboardId);
	}

	/**
	 * Method addByfloor
	 * 
	 * @param floor
	 * @param motherboardList
	 */
	private void addByFloor(int floor, Set<Motherboard> motherboardList)
	{
		List<Motherboard> motherboardId = this.iMotherboardRepository.findByFloor(floor);
		motherboardList.addAll(motherboardId);
	}

	/**
	 * Method addBySerialNumber
	 * 
	 * @param serialNumber
	 * @param motherboardList
	 */
	private void addBySerialNumber(String serialNumber, Set<Motherboard> motherboardList)
	{
		Optional<Motherboard> motherboardId = this.iMotherboardRepository.findById(serialNumber);
		if (motherboardId.isPresent())
		{
			motherboardList.add(motherboardId.get());
		}
	}

	/**
	 * Method addTasks
	 * 
	 * @param motherboardList
	 * @param action
	 */
	private void addTasks(Set<Motherboard> motherboardList, Action action, String info)
	{
		Date date = new Date();
		for (Motherboard motherboard : motherboardList)
		{
			Task task = new Task();
			TaskId taskId = new TaskId();

			taskId.setActionName(action.getName());
			taskId.setDate(date);
			taskId.setSerialNumber(motherboard.getMotherBoardSerialNumber());

			task.setTaskId(taskId);
			task.setAction(action);
			task.setMotherboard(motherboard);
			task.setInfo(info);
			task.setStatus(Action.STATUS_TODO);

			this.iTaskRepository.saveAndFlush(task);
		}
	}
}
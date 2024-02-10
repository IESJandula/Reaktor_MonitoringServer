package es.iesjandula.reaktor.monitoring_server.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
import es.iesjandula.reaktor.models.CommandLine;
import es.iesjandula.reaktor.models.Computer;
import es.iesjandula.reaktor.models.HardwareComponent;
import es.iesjandula.reaktor.models.Location;
import es.iesjandula.reaktor.models.MonitorizationLog;
import es.iesjandula.reaktor.models.Peripheral;
import es.iesjandula.reaktor.models.Software;
import lombok.extern.slf4j.Slf4j;

/**
 * @author David Martinez
 *
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/computers")
@Slf4j
public class ReaktorAdministrationRest
{
	/** Attribute computerList */
	private List<Computer> computerList = new ArrayList<>(List.of(
			new Computer("sn123", "and123", "cn123", "windows", "paco", new Location("0.5", 0, "trolley1"),
					new ArrayList<>(), new ArrayList<>(), new CommandLine(), new MonitorizationLog()),
			new Computer("sn1234", "and1234", "cn12344", "windows", "paco", new Location("0.5", 0, "trolley1"),
					new ArrayList<>(), new ArrayList<>(), new CommandLine(), new MonitorizationLog()),
			new Computer("sn123", "and12355", "cn123455", "windows", "paco", new Location("0.7", 0, "trolley2"),
					new ArrayList<>(), new ArrayList<>(), new CommandLine(), new MonitorizationLog()),
			new Computer("sn123556", "and123556", "cn1234556", "windows", "paco", new Location("0.7", 0, "trolley2"),
					new ArrayList<>(), new ArrayList<>(), new CommandLine(), new MonitorizationLog()),
			new Computer("sn123777", "and123777", "cn1234777", "windows", "paco", new Location("0.9", 0, "trolley3"),
					new ArrayList<>(), new ArrayList<>(), new CommandLine(), new MonitorizationLog())

	));

	/**
	 * Method sendInformation to send information of commands to computers
	 *
	 * @param serialNumber the serial number of computer
	 * @param classroom    the classroom
	 * @param trolley      the trolley
	 * @param plant        the plant
	 * @param commandLine  the commnadLine Object
	 * @return ResponseEntity
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/admin/commandLine", consumes = "application/json")
	public ResponseEntity<?> postComputerCommandLine(
			@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley,
			@RequestHeader(required = false) Integer plant,
			@RequestBody(required = true) CommandLine commandLine)
	{
		try
		{
			// --- GETTING THE COMMAND BLOCK ----
			List<String> commands = new ArrayList<>(commandLine.getCommands());

			// --- IF ANY OF THE PARAMETERS IS NOT NULL ---
			if ((serialNumber != null) || (classroom != null) || (trolley != null) || (plant != null))
			{
				String methodsUsed = "";

				// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
				if (this.checkBlanksOrEmptys(serialNumber, classroom, trolley))
				{
					String error = "Any Paramater Is Empty or Blank";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}

				if (serialNumber != null)
				{
					// ALL COMMANDS ON SPECIFIC COMPUTER BY serialNumber
					this.commandsToComputerBySerialNumber(serialNumber, commands);
					methodsUsed += "serialNumber,";
				}
				if (trolley != null)
				{
					// ALL COMMANDS ON SPECIFIC COMPUTER BY trolley
					this.commandsToComputerByTrolley(trolley, commands);
					methodsUsed += "trolley,";
				}
				if (classroom != null)
				{
					// ALL COMMANDS ON SPECIFIC COMPUTER BY classroom
					this.commandsToComputerByClassroom(classroom, commands);
					methodsUsed += "classroom,";
				}
				if (plant != null)
				{
					// ALL COMMANDS ON SPECIFIC COMPUTER BY plant
					this.commandsToComputerByPlant(plant, commands);
					methodsUsed += "plant,";
				}
				log.info("Parameters Used: " + methodsUsed);
				// --- RETURN OK RESPONSE ---
				return ResponseEntity.ok(this.computerListToMap());
			}
			else
			{
				// COMMANDS RUN ON ALL COMPUTERS
				this.commandsToAllComputers(commands);
				log.info("By all Computers");
				return ResponseEntity.ok(this.computerListToMap());
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
	 * @param plant
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/admin/shutdown")
	public ResponseEntity<?> putComputerShutdown(
			@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley,
			@RequestHeader(required = false) Integer plant)
	{
		try
		{
			Set<Computer> shutdownComputerListDistint = new HashSet<>();

			// --- IF ANY OF THE PARAMETERS IS NOT NULL ---
			if ((serialNumber != null) || (classroom != null) || (trolley != null) || (plant != null))
			{
				String methodsUsed = "";

				// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
				if (this.checkBlanksOrEmptys(serialNumber, classroom, trolley))
				{
					String error = "Any Paramater Is Empty or Blank";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}

				if (serialNumber != null)
				{
					// SHUTDOWN SPECIFIC COMPUTER BY serialNumber
					this.addBySerialNumber(serialNumber, shutdownComputerListDistint);
					methodsUsed += "serialNumber,";
				}
				if (trolley != null)
				{
					// SHUTDOWN SPECIFIC COMPUTER BY trolley
					this.addByTrolley(trolley, shutdownComputerListDistint);
					methodsUsed += "trolley,";
				}
				if (classroom != null)
				{
					// SHUTDOWN SPECIFIC COMPUTER BY classroom
					this.addByClassroom(classroom, shutdownComputerListDistint);
					methodsUsed += "classroom,";
				}
				if (plant != null)
				{
					// SHUTDOWN SPECIFIC COMPUTER BY plant
					this.addByPlant(plant, shutdownComputerListDistint);
					methodsUsed += "plant,";
				}
				log.info("Parameters Used: " + methodsUsed);
				// --- RETURN OK RESPONSE ---
				return ResponseEntity.ok(this.shutdownComputerListDistintToMap(shutdownComputerListDistint));
			}
			else
			{
				// SHUTDOWN ALL COMPUTERS
				this.addByAll(shutdownComputerListDistint);
				log.info("By all Computers");
				return ResponseEntity.ok(this.shutdownComputerListDistintToMap(shutdownComputerListDistint));
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
	 * @param plant
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/admin/restart")
	public ResponseEntity<?> putComputerRestart(
			@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley,
			@RequestHeader(required = false) Integer plant)
	{
		try
		{
			Set<Computer> restartComputerListDistint = new HashSet<>();

			// --- IF ANY OF THE PARAMETERS IS NOT NULL ---
			if ((serialNumber != null) || (classroom != null) || (trolley != null) || (plant != null))
			{
				String methodsUsed = "";

				// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
				if (this.checkBlanksOrEmptys(serialNumber, classroom, trolley))
				{
					String error = "Any Paramater Is Empty or Blank";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}

				if (serialNumber != null)
				{
					this.addBySerialNumber(serialNumber, restartComputerListDistint);
					methodsUsed += "serialNumber,";
				}
				if (trolley != null)
				{
					this.addByTrolley(trolley, restartComputerListDistint);
					methodsUsed += "trolley,";
				}
				if (classroom != null)
				{
					this.addByClassroom(classroom, restartComputerListDistint);
					methodsUsed += "classroom,";
				}
				if (plant != null)
				{
					this.addByPlant(plant, restartComputerListDistint);
					methodsUsed += "plant,";
				}
				log.info("Parameters Used: " + methodsUsed);
				// --- RETURN OK RESPONSE ---
				return ResponseEntity.ok(this.shutdownComputerListDistintToMap(restartComputerListDistint));
			}
			else
			{
				this.addByAll(restartComputerListDistint);
				log.info("By all Computers");
				return ResponseEntity.ok(this.restartComputerListDistintToMap(restartComputerListDistint));
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
	public ResponseEntity<?> postPeripheral(
			@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley,
			@RequestBody(required = true) List<Peripheral> peripheralInstance)
	{
		try
		{

			// --- IF ANY OF THE PARAMETERS IS NOT NULL ---
			if ((classroom != null) || (trolley != null))
			{
				String methodsUsed = "";

				// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
				if (this.checkBlanksOrEmptyClassRoomTrolley(classroom, trolley))
				{
					String error = "Any Paramater Is Empty or Blank";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}

				if (trolley != null)
				{
					// ON SPECIFIC COMPUTER BY trolley
					for (int i = 0; i < this.computerList.size(); i++)
					{
						Computer computer = this.computerList.get(i);
						this.computerList.remove(computer);
						if (computer.getLocation().getTrolley().equalsIgnoreCase(trolley))
						{
							List<HardwareComponent> hardwareComponentList = this
									.getHardwarePeripheralListEdited(peripheralInstance, computer);
							computer.setHardwareList(hardwareComponentList);
						}
						this.computerList.add(i, computer);
					}
					methodsUsed += "trolley,";
				}
				if (classroom != null)
				{
					// ON SPECIFIC COMPUTER BY classroom
					for (int i = 0; i < this.computerList.size(); i++)
					{
						Computer computer = this.computerList.get(i);
						this.computerList.remove(computer);
						if (computer.getLocation().getClassroom().equalsIgnoreCase(classroom))
						{
							List<HardwareComponent> hardwareComponentList = this
									.getHardwarePeripheralListEdited(peripheralInstance, computer);
							computer.setHardwareList(hardwareComponentList);
						}
						this.computerList.add(i, computer);
					}
					methodsUsed += "classroom,";
				}

				log.info("Parameters Used: " + methodsUsed);
				// --- RETURN OK RESPONSE ---
				return ResponseEntity.ok(this.computerListToMap());
			}
			else
			{
				// ON ALL COMPUTERS
				for (int i = 0; i < this.computerList.size(); i++)
				{
					Computer computer = this.computerList.get(i);
					this.computerList.remove(computer);

					List<HardwareComponent> hardwareComponentList = this
							.getHardwarePeripheralListEdited(peripheralInstance, computer);
					computer.setHardwareList(hardwareComponentList);

					this.computerList.add(i, computer);
				}
				log.info("By all Computers");
				return ResponseEntity.ok(this.computerListToMap());
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
	public ResponseEntity<?> sendScreenshotOrder(
			@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley)
	{
		try
		{
			Set<Computer> sendScreenshotOrderComputerListDistint = new HashSet<>();

			// --- IF ANY OF THE PARAMETERS IS NOT NULL ---
			if ((classroom != null) || (trolley != null))
			{
				String methodsUsed = "";

				// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
				if (this.checkBlanksOrEmptyClassRoomTrolley(classroom, trolley))
				{
					String error = "Any Paramater Is Empty or Blank";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}

				if (trolley != null)
				{
					this.addByTrolley(trolley, sendScreenshotOrderComputerListDistint);
					methodsUsed += "trolley,";
				}
				if (classroom != null)
				{
					this.addByClassroom(classroom, sendScreenshotOrderComputerListDistint);
					methodsUsed += "classroom,";
				}

				log.info("Parameters Used: " + methodsUsed);
				// --- RETURN OK RESPONSE ---
				return ResponseEntity.ok(this.shutdownComputerListDistintToMap(sendScreenshotOrderComputerListDistint));
			}
			else
			{
				this.addByAll(sendScreenshotOrderComputerListDistint);
				log.info("By all Computers");
				return ResponseEntity.ok(this.shutdownComputerListDistintToMap(sendScreenshotOrderComputerListDistint));
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
	@RequestMapping(method = RequestMethod.POST, value = "/admin/software", consumes = "application/json")
	public ResponseEntity<?> sendSoftware(
			@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley,
			@RequestHeader(required = false) String professor,
			@RequestBody(required = true) List<Software> softwareInstance)
	{
		try
		{

			// --- IF ANY OF THE PARAMETERS IS NOT NULL ---
			if ((classroom != null) || (trolley != null))
			{
				String methodsUsed = "";

				// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
				if (this.checkEmptysProfessorClassTrolley(professor, classroom, trolley))
				{
					String error = "Any Paramater Is Empty or Blank";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}
				if (professor != null)
				{
					// ON SPECIFIC COMPUTER BY professor
					for (int i = 0; i < this.computerList.size(); i++)
					{
						Computer computer = this.computerList.get(i);
						this.computerList.remove(computer);
						if (computer.getProfessor().equalsIgnoreCase(professor))
						{
							List<Software> softwareList = this.getSoftwareListEdited(softwareInstance, computer);
							computer.setSoftwareList(softwareList);
						}
						this.computerList.add(i, computer);
					}
					methodsUsed += "professor,";
				}
				if (trolley != null)
				{
					// ON SPECIFIC COMPUTER BY trolley
					for (int i = 0; i < this.computerList.size(); i++)
					{
						Computer computer = this.computerList.get(i);
						this.computerList.remove(computer);
						if (computer.getLocation().getTrolley().equalsIgnoreCase(trolley))
						{
							List<Software> softwareList = this.getSoftwareListEdited(softwareInstance, computer);
							computer.setSoftwareList(softwareList);
						}
						this.computerList.add(i, computer);
					}
					methodsUsed += "trolley,";
				}
				if (classroom != null)
				{
					// ON SPECIFIC COMPUTER BY classroom
					for (int i = 0; i < this.computerList.size(); i++)
					{
						Computer computer = this.computerList.get(i);
						this.computerList.remove(computer);
						if (computer.getLocation().getClassroom().equalsIgnoreCase(classroom))
						{
							List<Software> softwareList = this.getSoftwareListEdited(softwareInstance, computer);
							computer.setSoftwareList(softwareList);
						}
						this.computerList.add(i, computer);
					}
					methodsUsed += "classroom,";
				}

				log.info("Parameters Used: " + methodsUsed);
				// --- RETURN OK RESPONSE ---
				return ResponseEntity.ok(this.computerListToMap());
			}
			else
			{
				// ON ALL COMPUTERS
				for (int i = 0; i < this.computerList.size(); i++)
				{
					Computer computer = this.computerList.get(i);
					this.computerList.remove(computer);

					List<Software> softwareList = this.getSoftwareListEdited(softwareInstance, computer);
					computer.setSoftwareList(softwareList);

					this.computerList.add(i, computer);
				}
				log.info("By all Computers");
				return ResponseEntity.ok(this.computerListToMap());
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
	@RequestMapping(method = RequestMethod.DELETE, value = "/admin/software", consumes = "application/json")
	public ResponseEntity<?> unistallSoftware(
			@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley, 
			@RequestHeader(required = false) String professor,
			@RequestBody(required = true) List<Software> softwareInstance)
	{
		try
		{

			// --- IF ANY OF THE PARAMETERS IS NOT NULL ---
			if ((classroom != null) || (trolley != null))
			{
				String methodsUsed = "";

				// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
				if (this.checkEmptysProfessorClassTrolley(professor, classroom, trolley))
				{
					String error = "Any Paramater Is Empty or Blank";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}

				if (professor != null)
				{
					// ON SPECIFIC COMPUTER BY professor
					for (int i = 0; i < this.computerList.size(); i++)
					{
						Computer computer = this.computerList.get(i);
						this.computerList.remove(computer);
						if (computer.getProfessor().equalsIgnoreCase(professor))
						{
							List<Software> softwareList = new ArrayList<>(computer.getSoftwareList());

							this.removeSoftwareFromList(softwareInstance, softwareList);
							computer.setSoftwareList(softwareList);
						}
						this.computerList.add(i, computer);
					}
					methodsUsed += "professor,";
				}
				if (trolley != null)
				{
					// ON SPECIFIC COMPUTER BY trolley
					for (int i = 0; i < this.computerList.size(); i++)
					{
						Computer computer = this.computerList.get(i);
						this.computerList.remove(computer);
						if (computer.getLocation().getTrolley().equalsIgnoreCase(trolley))
						{
							List<Software> softwareList = new ArrayList<>(computer.getSoftwareList());

							this.removeSoftwareFromList(softwareInstance, softwareList);
							computer.setSoftwareList(softwareList);
						}
						this.computerList.add(i, computer);
					}
					methodsUsed += "trolley,";
				}
				if (classroom != null)
				{
					// ON SPECIFIC COMPUTER BY classroom
					for (int i = 0; i < this.computerList.size(); i++)
					{
						Computer computer = this.computerList.get(i);
						this.computerList.remove(computer);
						if (computer.getLocation().getClassroom().equalsIgnoreCase(classroom))
						{
							List<Software> softwareList = new ArrayList<>(computer.getSoftwareList());

							this.removeSoftwareFromList(softwareInstance, softwareList);
							computer.setSoftwareList(softwareList);
						}
						this.computerList.add(i, computer);
					}
					methodsUsed += "classroom,";
				}

				log.info("Parameters Used: " + methodsUsed);
				// --- RETURN OK RESPONSE ---
				return ResponseEntity.ok(this.computerListToMap());
			}
			else
			{
				// ON ALL COMPUTERS
				for (int i = 0; i < this.computerList.size(); i++)
				{
					Computer computer = this.computerList.get(i);
					this.computerList.remove(computer);

					List<Software> softwareList = new ArrayList<>(computer.getSoftwareList());

					this.removeSoftwareFromList(softwareInstance, softwareList);
					computer.setSoftwareList(softwareList);

					this.computerList.add(i, computer);
				}
				log.info("By all Computers");
				return ResponseEntity.ok(this.computerListToMap());
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
	public ResponseEntity<?> updateComputer(
			@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String andaluciaId,
			@RequestHeader(required = false) String computerNumber,
			@RequestBody(required = true) Computer computerInstance)
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
					for (int i = 0; i < this.computerList.size(); i++)
					{
						Computer computer = this.computerList.get(i);
						if (computer.getSerialNumber().equalsIgnoreCase(serialNumber))
						{
							this.computerList.remove(computer);
						}
					}
					this.computerList.add(computerInstance);

					methodsUsed += "serialNumber,";
				}
				if (andaluciaId != null)
				{
					// ON SPECIFIC COMPUTER BY trolley
					for (int i = 0; i < this.computerList.size(); i++)
					{
						Computer computer = this.computerList.get(i);
						if (computer.getAndaluciaID().equalsIgnoreCase(andaluciaId))
						{
							this.computerList.remove(computer);
						}
					}
					this.computerList.add(computerInstance);
					methodsUsed += "trolley,";
				}
				if (computerNumber != null)
				{
					// ON SPECIFIC COMPUTER BY classroom
					for (int i = 0; i < this.computerList.size(); i++)
					{
						Computer computer = this.computerList.get(i);
						if (computer.getComputerNumber().equalsIgnoreCase(computerNumber))
						{
							this.computerList.remove(computer);
						}
					}
					this.computerList.add(computerInstance);
					methodsUsed += "classroom,";
				}

				log.info("Parameters Used: " + methodsUsed);
				// --- RETURN OK RESPONSE ---
				return ResponseEntity.ok(this.computerListToMap());
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
	 * Method removeSoftwareFromList
	 * 
	 * @param softwareInstance
	 * @param softwareList
	 */
	private void removeSoftwareFromList(List<Software> softwareInstance, List<Software> softwareList)
	{
		for (Software software : softwareInstance)
		{
			if (softwareList.contains(software))
			{
				softwareList.remove(software);
			}
		}
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
	private boolean checkEmptysProfessorClassTrolley(String professor, String classroom, String trolley)
	{
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
	 * Method checkBlanksOrEmptyClassRoomTrolley
	 * 
	 * @param classroom
	 * @param trolley
	 * @return
	 */
	private boolean checkBlanksOrEmptyClassRoomTrolley(String classroom, String trolley)
	{
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
	 * Method checkBlanksOrEmptys
	 *
	 * @param serialNumber
	 * @param classroom
	 * @param trolley
	 * @return boolean
	 */
	private boolean checkBlanksOrEmptys(String serialNumber, String classroom, String trolley)
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
	 * Method commandsToAllComputers send commands to all computers
	 *
	 * @param commands
	 */
	private void commandsToAllComputers(List<String> commands)
	{
		for (int i = 0; i < this.computerList.size(); i++)
		{
			Computer computer = this.computerList.get(i);
			computer.setCommandLine(new CommandLine(commands));
			this.computerList.remove(computer);
			this.computerList.add(i, computer);
		}
	}

	/**
	 * Method commandsToComputerByPlant send commands to computers by plant
	 *
	 * @param plant
	 * @param commands
	 */
	private void commandsToComputerByPlant(Integer plant, List<String> commands)
	{
		for (int i = 0; i < this.computerList.size(); i++)
		{
			Computer computer = this.computerList.get(i);
			if (computer.getLocation().getPlant() == plant)
			{
				computer.setCommandLine(new CommandLine(commands));
				this.computerList.remove(computer);
				this.computerList.add(i, computer);
			}
		}
	}

	/**
	 * Method commandsToComputerByClassroom send commands to computers by classroom
	 *
	 * @param classroom
	 * @param commands
	 */
	private void commandsToComputerByClassroom(String classroom, List<String> commands)
	{
		for (int i = 0; i < this.computerList.size(); i++)
		{
			Computer computer = this.computerList.get(i);
			if (computer.getLocation().getClassroom().equalsIgnoreCase(classroom))
			{
				computer.setCommandLine(new CommandLine(commands));
				this.computerList.remove(computer);
				this.computerList.add(i, computer);
			}
		}
	}

	/**
	 * Method commandsToComputerByTrolley send commands to computers by trolley
	 *
	 * @param trolley
	 * @param commands
	 */
	private void commandsToComputerByTrolley(String trolley, List<String> commands)
	{
		for (int i = 0; i < this.computerList.size(); i++)
		{
			Computer computer = this.computerList.get(i);
			if (computer.getLocation().getTrolley().equalsIgnoreCase(trolley))
			{
				computer.setCommandLine(new CommandLine(commands));
				this.computerList.remove(computer);
				this.computerList.add(i, computer);
			}
		}
	}

	/**
	 * Method commandsToComputerBySerialNumber send commands to computers by
	 * serialNumber
	 *
	 * @param serialNumber
	 * @param commands
	 */
	private void commandsToComputerBySerialNumber(String serialNumber, List<String> commands)
	{
		for (int i = 0; i < this.computerList.size(); i++)
		{
			Computer computer = this.computerList.get(i);
			if (computer.getSerialNumber().equalsIgnoreCase(serialNumber))
			{
				computer.setCommandLine(new CommandLine(commands));
				this.computerList.remove(computer);
				this.computerList.add(i, computer);
			}
		}
	}

	/**
	 * Method addByAll
	 * 
	 * @param computerListDistint
	 */
	private void addByAll(Set<Computer> computerListDistint)
	{
		for (Computer computer : this.computerList)
		{
			computerListDistint.add(computer);
		}
	}

	/**
	 * Method addByPlant
	 * 
	 * @param plant
	 * @param computerListDistint
	 */
	private void addByPlant(Integer plant, Set<Computer> computerListDistint)
	{
		for (Computer computer : this.computerList)
		{
			if (computer.getLocation().getPlant() == plant)
			{
				computerListDistint.add(computer);
			}
		}
	}

	/**
	 * Method addByClassroom
	 * 
	 * @param classroom
	 * @param computerListDistint
	 */
	private void addByClassroom(String classroom, Set<Computer> computerListDistint)
	{
		for (Computer computer : this.computerList)
		{
			if (computer.getLocation().getClassroom().equalsIgnoreCase(classroom))
			{
				computerListDistint.add(computer);
			}
		}
	}

	/**
	 * Method addByTrolley
	 * 
	 * @param trolley
	 * @param computerListDistint
	 */
	private void addByTrolley(String trolley, Set<Computer> computerListDistint)
	{
		for (Computer computer : this.computerList)
		{
			if (computer.getLocation().getTrolley().equalsIgnoreCase(trolley))
			{
				computerListDistint.add(computer);
			}
		}
	}

	/**
	 * Method addBySerialNumber
	 * 
	 * @param serialNumber
	 * @param computerListDistint
	 */
	private void addBySerialNumber(String serialNumber, Set<Computer> computerListDistint)
	{
		for (Computer computer : this.computerList)
		{
			if (computer.getSerialNumber().equalsIgnoreCase(serialNumber))
			{
				computerListDistint.add(computer);
			}
		}
	}

	/**
	 * Method getHardwarePeripheralListEdited
	 * 
	 * @param peripheralInstance
	 * @param computer
	 * @return
	 */
	private List<HardwareComponent> getHardwarePeripheralListEdited(List<Peripheral> peripheralInstance,
			Computer computer)
	{
		List<HardwareComponent> hardwareComponentList = new ArrayList<>(computer.getHardwareList());

		for (Peripheral peripheral : peripheralInstance)
		{
			if (hardwareComponentList.contains(peripheral))
			{
				hardwareComponentList.remove(peripheral);
				hardwareComponentList.add(peripheral);
			}
			else
			{
				hardwareComponentList.add(peripheral);
			}
		}
		return hardwareComponentList;
	}

	/**
	 * Method getSoftwareListEdited
	 * 
	 * @param softwareInstance
	 * @param computer
	 * @return
	 */
	private List<Software> getSoftwareListEdited(List<Software> softwareInstance, Computer computer)
	{
		List<Software> softwareList = new ArrayList<>(computer.getSoftwareList());

		for (Software software : softwareInstance)
		{
			if (softwareList.contains(software))
			{
				softwareList.remove(software);
				softwareList.add(software);
			}
			else
			{
				softwareList.add(software);
			}
		}
		return softwareList;
	}

	/**
	 * Method computerListToMap , method for debug or testing only
	 *
	 * @return Map with list of computers
	 */
	private Map<String, List<Computer>> computerListToMap()
	{
		Map<String, List<Computer>> computerListMap = new HashMap<>();
		computerListMap.put("computers", this.computerList);
		return computerListMap;
	}

	/**
	 * Method shutdownComputerListDistintToMap
	 * 
	 * @param shutdownComputerList
	 * @return
	 */
	private Map<String, List<Computer>> shutdownComputerListDistintToMap(Collection shutdownComputerList)
	{
		Map<String, List<Computer>> computerListMap = new HashMap<>();
		computerListMap.put("computers", new ArrayList<>(shutdownComputerList));
		return computerListMap;
	}

	/**
	 * Method shutdownComputerListDistintToMap
	 * 
	 * @param shutdownComputerList
	 * @return
	 */
	private Map<String, List<Computer>> restartComputerListDistintToMap(Collection shutdownComputerList)
	{
		Map<String, List<Computer>> computerListMap = new HashMap<>();
		computerListMap.put("computers", new ArrayList<>(shutdownComputerList));
		return computerListMap;
	}

	/**
	 * Method sendInformation to send information of commands to computers
	 *
	 * @param serialNumber the serial number of computer
	 * @param classroom    the classroom
	 * @param trolley      the trolley
	 * @param plant        the plant
	 * @param File         the execFile
	 * @return ResponseEntity
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/admin/file", consumes = "multipart/form-data")
	public ResponseEntity<?> postComputerCommandLine(
			@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String classroom, 
			@RequestHeader(required = false) String trolley,
			@RequestHeader(required = false) Integer plant, 
			@RequestBody(required = true) MultipartFile execFile)
	{
		try
		{
			List<Computer> fileComputers = new ArrayList<>();
			if ((serialNumber != null) || (classroom != null) || (trolley != null) || (plant != null))
			{
				// --- CHECKING IF ANY PARAMETER IS BLANK OR EMPTY ---
				if (this.checkBlanksOrEmptys(serialNumber, classroom, trolley))
				{
					String error = "Any Paramater Is Empty or Blank";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}

				if (serialNumber != null)
				{
					// ALL FILE ON SPECIFIC COMPUTER BY serialNumber
					this.fileToComputerBySerialNumber(serialNumber, fileComputers);

				}
				if (trolley != null)
				{
					// ALL FILE ON SPECIFIC COMPUTER BY trolley
					this.fileToComputerByTrolley(trolley, fileComputers);

				}
				if (classroom != null)
				{
					// ALL FILE ON SPECIFIC COMPUTER BY classroom
					this.fileToComputerByClassroom(classroom, fileComputers);

				}
				if (plant != null)
				{
					// ALL FILE ON SPECIFIC COMPUTER BY plant
					this.fileToComputerByPlant(plant, fileComputers);

				}
				// --- RETURN OK RESPONSE ---
				return ResponseEntity.ok(this.computerListToMap(fileComputers));
			}
			else
			{
				// COMMANDS RUN ON ALL COMPUTERS
				this.fileToAllComputers(fileComputers);
				return ResponseEntity.ok(this.computerListToMap(fileComputers));
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
	 * Method fileToAllComputers send to all classroom computers
	 *
	 * @param file
	 */
	private void fileToAllComputers(List<Computer> fileComputers)
	{
		for (int i = 0; i < this.computerList.size(); i++)
		{
			Computer computer = this.computerList.get(i);
			fileComputers.add(computer);
		}
	}

	/**
	 * Method fileToComputerByPlant send file by plant computers
	 *
	 * @param file
	 */
	private void fileToComputerByPlant(Integer plant, List<Computer> fileComputers)
	{
		for (int i = 0; i < this.computerList.size(); i++)
		{
			Computer computer = this.computerList.get(i);
			if (computer.getLocation().getPlant() == plant)
			{
				fileComputers.add(computer);
			}
		}
	}

	/**
	 * Method fileToComputerByClassroom send file by classroom computers
	 *
	 * @param file
	 */
	private void fileToComputerByClassroom(String classroom, List<Computer> fileComputers)
	{
		for (int i = 0; i < this.computerList.size(); i++)
		{
			Computer computer = this.computerList.get(i);
			if (computer.getLocation().getClassroom().equalsIgnoreCase(classroom))
			{
				fileComputers.add(computer);
			}
		}
	}

	/**
	 * Method fileToComputerByTrolley send file by trolley computers
	 *
	 * @param file
	 */
	private void fileToComputerByTrolley(String trolley, List<Computer> fileComputers)
	{
		for (int i = 0; i < this.computerList.size(); i++)
		{
			Computer computer = this.computerList.get(i);
			if (computer.getLocation().getTrolley().equalsIgnoreCase(trolley))
			{
				fileComputers.add(computer);
			}
		}
	}

	/**
	 * Method fileToComputerBySerialNumber send file by serial number computers
	 *
	 * @param file
	 */
	private void fileToComputerBySerialNumber(String serialNumber, List<Computer> fileComputers)
	{
		for (int i = 0; i < this.computerList.size(); i++)
		{
			Computer computer = this.computerList.get(i);
			if (computer.getSerialNumber().equalsIgnoreCase(serialNumber))
			{
				fileComputers.add(computer);
			}
		}
	}

	/**
	 *
	 * @param fileComputers change the list to a map
	 * @return
	 */
	private Map<String, List<Computer>> computerListToMap(List<Computer> fileComputers)
	{
		Map<String, List<Computer>> computerListMap = new HashMap<>();
		computerListMap.put("computers", fileComputers);
		return computerListMap;
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
	public ResponseEntity<?> getComputer(
			@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley)
	{
		try
		{
			if (classroom.isEmpty() && trolley.isEmpty())
			{
				this.checkParams(classroom, trolley);

				File zipFile = this.getZipFile(classroom, trolley);
			}
			return ResponseEntity.ok("OK");
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
}
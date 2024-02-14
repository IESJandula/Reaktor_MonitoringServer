package es.iesjandula.reaktor.monitoring_server.rest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.iesjandula.reaktor.exceptions.ComputerError;
import es.iesjandula.reaktor.models.Action;
import es.iesjandula.reaktor.models.CommandLine;
import es.iesjandula.reaktor.models.Computer;
import es.iesjandula.reaktor.models.Location;
import es.iesjandula.reaktor.models.MonitorizationLog;
import es.iesjandula.reaktor.models.Motherboard;
import es.iesjandula.reaktor.models.Reaktor;
import es.iesjandula.reaktor.models.Status;
import es.iesjandula.reaktor.models.Task;
import es.iesjandula.reaktor.models.DTO.TaskDTO;
import es.iesjandula.reaktor.models.Id.TaskId;
import es.iesjandula.reaktor.monitoring_server.repository.IActionRepository;
import es.iesjandula.reaktor.monitoring_server.repository.IMotherboardRepository;
import es.iesjandula.reaktor.monitoring_server.repository.ITaskRepository;
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
	
	/** Attribute IActionRepository*/
	@Autowired
	private IActionRepository actionRepository;
	
	/** Attribute iMotherboardRepository*/
	@Autowired
	private IMotherboardRepository iMotherboardRepository;
	
	/** Attribute iTaskRepository*/
	@Autowired
	private ITaskRepository iTaskRepository;

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
	@RequestMapping(method = RequestMethod.GET, value = "/get/file", produces = "multipart/form-data")
	public ResponseEntity<?> getAnyFile
	(
		@RequestHeader(required = true) String serialNumber,
		@RequestBody(required = true) TaskDTO taskDTO)
	{
		try
		{
			Optional<Motherboard> motherboard = this.iMotherboardRepository.findById(serialNumber);
			
			if (motherboard.isEmpty())
			{
				String error = "Incorrect Serial Number";
				ComputerError computerError = new ComputerError(401, error, null);
				return ResponseEntity.status(401).body(computerError.toMap());
			}
			
			TaskId taskId = new TaskId(serialNumber, taskDTO.getName(), taskDTO.getDate());
			Optional<Task> task = this.iTaskRepository.findById(taskId);
			
			if (task.isEmpty())
			{
				String error = "Incorrect Task ID";
				ComputerError computerError = new ComputerError(401, error, null);
				return ResponseEntity.status(401).body(computerError.toMap());
			}
			
			
			InputStreamResource outcomeInputStreamResource = new InputStreamResource(new java.io.ByteArrayInputStream(this.readText(taskDTO.getInfo())));
			
			return ResponseEntity.ok().body(outcomeInputStreamResource);
			
		}
		catch (Exception exception)
		{
			log.error(exception.getMessage());
			ComputerError computerError = new ComputerError(500, exception.getMessage(), exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}
	
	/**
	 * Method readText to read
	 * @param name
	 * @return byte[]
	 * @throws ComputerError
	 */
	public byte[] readText(String name) throws ComputerError
    {

        FileInputStream fileInputStream = null;

        DataInputStream dataInputStream = null;

        try
        {
            fileInputStream = new FileInputStream(name);

            dataInputStream = new DataInputStream(fileInputStream);

            return dataInputStream.readAllBytes();
        } 
        catch (IOException exception)
        {
            String message = "Error";
            log.error(message, exception);
            throw new ComputerError(1, message, exception);
        } 
        finally
        {
            if (dataInputStream != null)
            {
                try
                {
                    dataInputStream.close();
                } catch (IOException exception)
                {
                    String message = "Error";
                    log.error(message, exception);
                }
            }

            if (fileInputStream != null)
            {
                try
                {
                    fileInputStream.close();
                } catch (IOException exception)
                {
                    String message = "Error";
                    log.error(message, exception);
                }
            }
        }
    }

	/**
	 * Getting the files , the computer send the serialNumber to identify
	 *
	 * @param serialNumber, the serial number of the computer
	 * @return ResponseEntity
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/send/screenshot", consumes = "multipart/form-data")
	public ResponseEntity<?> sendScreenshot
	(
		@RequestBody(required = true) MultipartFile screenshot,
		@RequestHeader(required = true) String serialNumber,
		@RequestHeader(required = true) Long dateLong)
	{
		try
		{
			Optional<Motherboard> motherboard = this.iMotherboardRepository.findById(serialNumber);
			
			if (motherboard.isEmpty())
			{
				String error = "Incorrect Serial Number";
				ComputerError computerError = new ComputerError(401, error, null);
				return ResponseEntity.status(401).body(computerError.toMap());
			}
			
			Date date = new Date(dateLong);
			
			TaskId taskId = new TaskId(serialNumber, "screenshot", date);
			Optional<Task> task = this.iTaskRepository.findById(taskId);
			
			if (task.isEmpty())
			{
				String error = "Incorrect Task ID";
				ComputerError computerError = new ComputerError(401, error, null);
				return ResponseEntity.status(401).body(computerError.toMap());
			}
			
			String finalDate = date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
			
			this.writeText(".\\screenshots\\" + serialNumber + "\\" + finalDate, screenshot.getBytes());
			return ResponseEntity.ok().build();

		}
		catch (Exception exception)
		{
			log.error(exception.getMessage());
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

		} catch (IOException exception)
		{
			String message = "Error";
			log.error(message, exception);
		} finally
		{
			if (dataOutputStream != null)
			{
				try
				{
					dataOutputStream.close();
				} catch (IOException exception)
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
				} catch (IOException exception)
				{
					String message = "Error";
					log.error(message, exception);
				}
			}
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
	 * Method chekIfSerialNumberExistBoolean
	 * @param serialNumber
	 * @return boolean
	 */
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
	 * Method sendFullComputer that method is used for send periodically computer
	 * Instance
	 *
	 * @param serialNumber     the serial number
	 * @param andaluciaId      the andalucia id
	 * @param computerNumber   the computer number
	 * @param reaktorInstance  the reaktor object instance
	 * @return ResponseEntity response
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/send/fullInfo", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> sendFullComputer
	(		
			@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String andaluciaId,
			@RequestHeader(required = false) String computerNumber,
			@RequestBody(required = true) Reaktor reaktorInstance)
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
					// ON SPECIFIC COMPUTER BY serialNumber
					Motherboard motherboard = this.iMotherboardRepository.findByMotherBoardSerialNumber(serialNumber);

					this.updateMotherboard(reaktorInstance, motherboard);
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
					// ON SPECIFIC COMPUTER BY ANDALUCIA ID
					List<Motherboard> motherboardList = this.iMotherboardRepository.findByAndaluciaId(andaluciaId);

					for(Motherboard motherboard:motherboardList) 
					{
						this.updateMotherboard(reaktorInstance, motherboard);
					}
					
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
					// ON SPECIFIC COMPUTER BY serialNumber
					Motherboard motherboard = this.iMotherboardRepository.findByMotherBoardSerialNumber(computerNumber);

					this.updateMotherboard(reaktorInstance, motherboard);
				}

				// --- RESPONSE WITH OK , 
				// ---
				return ResponseEntity.ok().build();

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
			Optional<Motherboard> motherboard = this.iMotherboardRepository.findById(serialNumber);
			
			if (motherboard.isEmpty())
			{
				String error = "Incorrect Serial Number";
				ComputerError computerError = new ComputerError(401, error, null);
				return ResponseEntity.status(401).body(computerError.toMap());
			}

			List<Task> tasks = this.iTaskRepository.findBySerialNumberAndStatus(serialNumber, Action.STATUS_TODO);
			
			tasks.sort((o1, o2) -> o1.getTaskId().getDate().compareTo(o2.getTaskId().getDate()));
			Task task = tasks.get(0);			
			TaskDTO taskDTO = new TaskDTO(task.getTaskId().getActionName(),task.getAction().getCommandWindows(),task.getAction().getCommandLinux(), task.getInfo(),task.getTaskId().getDate());
			
			return ResponseEntity.ok().body(taskDTO);
		}
		catch (Exception exception)
		{
			String error = "Server Error";
			ComputerError computerError = new ComputerError(404, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
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
	
	/**
	 * Method to update Motherboard
	 * 
	 * @param reaktorInstance
	 * @param motherboard
	 */
	private void updateMotherboard(Reaktor reaktorInstance, Motherboard motherboard)
	{
		
		// --- SE OBTIENE CADA APARTADO NECESARIO PARA GUARDAR EL MOTHERBOARD DESDE EL OBJETO REAKTOR ---
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

		
		// --- GUARDAMOS Y HACEMOS FLUSH ---
		this.iMotherboardRepository.save(motherboard);
		this.iMotherboardRepository.flush();
	}

}

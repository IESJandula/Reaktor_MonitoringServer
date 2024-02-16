package es.iesjandula.reaktor.monitoring_server.rest;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import es.iesjandula.reaktor.models.Motherboard;
import es.iesjandula.reaktor.models.Task;
import es.iesjandula.reaktor.models.Id.TaskId;
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
	@Autowired
	ITaskRepository iTaskRepository;
	IMotherboardRepository iMotherboardRepository;

	/**
	 * Method sendStatusComputer
	 *
	 * @param serialNumber
	 * @param statusList
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/send/status", consumes = "application/json")
	public ResponseEntity<?> sendStatusComputer(@RequestHeader(required = true) String serialNumber,
			@RequestBody(required = true) Task taskStatus)
	{
		try
		{
			log.info(serialNumber);
			log.info(taskStatus.toString());

			if (!this.isUsable(serialNumber))
			{
				String error = "Any Paramater Is Empty or Blank";
				ComputerError computerError = new ComputerError(404, error, null);
				return ResponseEntity.status(404).body(computerError.toMap());
			} else if (taskStatus.getStatus().equals(Action.STATUS_FAILURE))
			{
				iTaskRepository.save(taskStatus);
				String error = "Error al ejecutar el comando";
				ComputerError computerError = new ComputerError(404, error, null);
				return ResponseEntity.status(404).body(computerError.toMap());
			} else
			{
				iTaskRepository.save(taskStatus);
				return ResponseEntity.ok().body("OK");
			}

		} catch (Exception exception)
		{
			iTaskRepository.save(taskStatus);
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
		TaskId task = new TaskId();
		task.setSerialNumber(serialNumber);
		task.setActionName("screenshot");
		List<Task> fileTask = iTaskRepository.findByTaskIdAndStatus(task,
				Action.STATUS_TODO);
		try
		{

			if (fileTask.isEmpty())
			{
				String error = "Este ordenador no tiene tareas file pendientes";
				ComputerError computerError = new ComputerError(404, error, null);
				return ResponseEntity.status(404).body(computerError.toMap());
			} else
			{
				Task tarea = fileTask.get(0);
				File fichero = new File(tarea.getInfo());
				if (!fichero.exists())
				{
					String error = "El fichero no existe";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}
				byte[] fileContent = Files.readAllBytes(fichero.toPath());
				return ResponseEntity.ok().body(fileContent);
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
		TaskId task = new TaskId();
		task.setSerialNumber(serialNumber);
		task.setActionName("screenshot");
		List<Task> screenshotTask = iTaskRepository.findByTaskIdAndStatus(task,
				Action.STATUS_TODO);
		try
		{

			if (screenshotTask.isEmpty())
			{
				String error = "Este ordenador no tiene tareas file pendientes";
				ComputerError computerError = new ComputerError(404, error, null);
				return ResponseEntity.status(404).body(computerError.toMap());
			} else
			{
				Task tarea = screenshotTask.get(0);
				File fichero = new File(tarea.getInfo());
				if (!fichero.exists())
				{
					String error = "El fichero no existe";
					ComputerError computerError = new ComputerError(404, error, null);
					return ResponseEntity.status(404).body(computerError.toMap());
				}
				byte[] fileContent = Files.readAllBytes(fichero.toPath());
				return ResponseEntity.ok().body(fileContent);
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
	public ResponseEntity<?> sendScreenshot(@RequestBody(required = true) MultipartFile screenshot,
	@RequestHeader(required = true) String serialNumber)
	{
		try
		{
			if (screenshot != null)
			{
				Date date = new Date();
				String dateText = date.getYear()+"/"+date.getMonth()+"/"+date.getDate()+"/"+date.getHours()+"/"+date.getMinutes()+"/"+date.getSeconds();
				writeText("."+File.separator+"screenshots"+File.separator+serialNumber+dateText, null);
				return ResponseEntity.ok().body("");				
			} else
			{
				String error = "The parameter is null";
				ComputerError computerError = new ComputerError(404, error, null);
				return ResponseEntity.status(404).body(computerError.toMap());
			}

		} catch (Exception exception)
		{
			log.error(exception.getMessage());
			ComputerError computerError = new ComputerError(500, exception.getMessage(), exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}

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
	 * this method check if a computer with this serialNumber exist
	 *
	 * @param serialNumber, the serial Number of the computer
	 * @return Computer
	 */

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
	public ResponseEntity<?> sendFullComputer(@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String andaluciaId, @RequestHeader(required = false) String computerNumber)

	{
		try
		{
			List<Motherboard> motherboardList = new ArrayList<Motherboard>();
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
					motherboardList.add(this.iMotherboardRepository.findByMotherBoardSerialNumber(serialNumber));
				} else if (andaluciaId != null)
				{
					// --- BY ANDALUCIA ID ---
					if (this.checkIsBlankEmpty(andaluciaId))
					{
						String error = "andaluciaId is Empty or Blank";
						ComputerError computerError = new ComputerError(404, error, null);
						return ResponseEntity.status(404).body(computerError.toMap());
					}
					motherboardList = this.iMotherboardRepository.findByAndaluciaId(andaluciaId);
				} else if (computerNumber != null)
				{
					// --- BY COMPUTER NUMBER ---
					if (this.checkIsBlankEmpty(computerNumber))
					{
						String error = "computerNumber is Empty or Blank";
						ComputerError computerError = new ComputerError(404, error, null);
						return ResponseEntity.status(404).body(computerError.toMap());
					}
					motherboardList = this.iMotherboardRepository.findByComputerNumber(computerNumber);
				}

				// --- RESPONSE WITH OK , BUT TEMPORALY RESPONSE WITH LIST TO SEE THE CHANGES
				// ---
				return ResponseEntity.ok(motherboardList);

			} else
			{
				// --- ON THIS CASE ALL PARAMETERS ARE BLANK OR EMPTY ---
				String error = "All Paramaters Empty or Blank";
				ComputerError computerError = new ComputerError(404, error, null);
				return ResponseEntity.status(404).body(computerError.toMap());
			}
		} catch (Exception exception)
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
			List<Task> taskList = new ArrayList<Task>();

			// --- SEARCHING THE COMPUTER ---
			if ((serialNumber == null) || serialNumber.isBlank() || serialNumber.isEmpty())
			{
				String error = "SerialNumber is Null, Empty or Blank";
				ComputerError computerError = new ComputerError(404, error, null);
				return ResponseEntity.status(404).body(computerError.toMap());
			}
			TaskId task = new TaskId();
			task.setSerialNumber(serialNumber);
			taskList = iTaskRepository.findByTaskIdAndStatus(task, Action.STATUS_TODO);

			taskList.sort((o1, o2) -> o1.getTaskId().getDate().compareTo(o2.getTaskId().getDate()));
			taskList.get(0).setStatus(Action.STATUS_IN_PROGRESS);
			iTaskRepository.save(taskList.get(0));
			return ResponseEntity.ok().body(taskList.get(0));
		} catch (Exception exception)
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

}

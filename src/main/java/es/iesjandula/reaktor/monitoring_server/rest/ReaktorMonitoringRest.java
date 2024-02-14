package es.iesjandula.reaktor.monitoring_server.rest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import es.iesjandula.reaktor.models.Motherboard;
import es.iesjandula.reaktor.models.Reaktor;
import es.iesjandula.reaktor.models.Status;
import es.iesjandula.reaktor.models.Task;
import es.iesjandula.reaktor.models.DTO.TaskDTO;
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
			@RequestBody(required = true) Status status)
	{
		try
		{
			log.info(serialNumber);
			log.info(status.toString());
			//Comprobar motherboard si existe
			
			Optional<Motherboard> motherboard = this.iMotherboardRepository.findById(serialNumber);
			if (motherboard.isEmpty())
			{
				String error = "Incorrect Serial Number";
				ComputerError computerError = new ComputerError(401, error, null);
				return ResponseEntity.status(401).body(computerError.toMap());
			}
			
			//Comprobar si existe la Task por id
			TaskId taskId = new TaskId(serialNumber, status.getTaskDTO().getName(), status.getTaskDTO().getDate());
			Optional<Task> task = this.iTaskRepository.findById(taskId);
			
			if (task.isEmpty())
			{
				String error = "Incorrect Task ID";
				ComputerError computerError = new ComputerError(401, error, null);
				return ResponseEntity.status(401).body(computerError.toMap());
			}
			//Comprobar el estado de la tarea, si es in progress
			if(!task.get().getStatus().equals(Action.STATUS_IN_PROGRESS))
			{
				String error = "Task not in progress";
				ComputerError computerError = new ComputerError(401, error, null);
				return ResponseEntity.status(401).body(computerError.toMap());
			}
			//Comprobar el estado, si esta correcto o a fallado, asigna el estado a la tarea
			if(status.getStatus())
			{
				task.get().setStatus(Action.STATUS_DONE);
			}
			else
			{
				task.get().setStatus(Action.STATUS_FAILURE);
				
				log.error(status.getStatusInfo(), status.getError());
			}
			this.iTaskRepository.saveAndFlush(task.get());
			
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
			// --- SACAMOS EL MOTHERBOARD POR EL SERIAL NUMBER ---
			Optional<Motherboard> motherboard = this.iMotherboardRepository.findById(serialNumber);
			
			// --- SI NO ES EMPTY ---
			if (motherboard.isEmpty())
			{
				String error = "Incorrect Serial Number";
				ComputerError computerError = new ComputerError(401, error, null);
				return ResponseEntity.status(401).body(computerError.toMap());
			}
			
			// --- CREAMOS EL TASK ID ---
			TaskId taskId = new TaskId(serialNumber, taskDTO.getName(), taskDTO.getDate());
			
			// --- BUSCAMOS POR EL TASK ID ---
			Optional<Task> task = this.iTaskRepository.findById(taskId);
			
			// --- SI EL TASKID NO ESTA VACIO ---
			if (task.isEmpty())
			{
				String error = "Incorrect Task ID";
				ComputerError computerError = new ComputerError(401, error, null);
				return ResponseEntity.status(401).body(computerError.toMap());
			}
			
			// --- CREAMOS EL IMPUT STREAM ---
			InputStreamResource outcomeInputStreamResource = new InputStreamResource(new java.io.ByteArrayInputStream(this.readText(taskDTO.getInfo())));
			
			// --- RESPONDEMOS CON EL INPUT STREAM ---
			return ResponseEntity.ok().body(outcomeInputStreamResource);
			
		}
		// --- CAPTURAMOS Y ARROJAMOS ---
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

		// --- CREAMOS FLUJOS ---
        FileInputStream fileInputStream = null;
        DataInputStream dataInputStream = null;

        try
        {
        	// --- INICIALIZAMOS FLUJOS ---
            fileInputStream = new FileInputStream(name);
            dataInputStream = new DataInputStream(fileInputStream);

            // --- RETORNAMOS CON LA LECTURA DE TODOS LOS BYTES ---
            return dataInputStream.readAllBytes();
        } 
        // --- CAPTURAMOS Y ARROJAMOS EXCEPCION ---
        catch (IOException exception)
        {
            String message = "Error";
            log.error(message, exception);
            throw new ComputerError(1, message, exception);
        } 
        finally
        {
        	//  --- CERRRAMOS TODOS LOS FLUJOS EN EL FINALLY ---
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
			// --- OBTENEMOS EL MOTHERBOARD CON UN SERIALNUMBER ---
			Optional<Motherboard> motherboard = this.iMotherboardRepository.findById(serialNumber);
			
			// --- SI EL MOTHERBOARD NO ESTA VACIO ---
			if (motherboard.isEmpty())
			{
				String error = "Incorrect Serial Number";
				ComputerError computerError = new ComputerError(401, error, null);
				return ResponseEntity.status(401).body(computerError.toMap());
			}
			
			// --- CREAMOS EL DATE ---
			Date date = new Date(dateLong);
			
			// --- CREAMOS EL TASK ID ---
			TaskId taskId = new TaskId(serialNumber, "screenshot", date);
			
			// --- BUSCAMOS LA TASK CON EL TASKID ---
			Optional<Task> task = this.iTaskRepository.findById(taskId);
			
			// --- SI EL TASK NO ESTA VACIO ---
			if (task.isEmpty())
			{
				String error = "Incorrect Task ID";
				ComputerError computerError = new ComputerError(401, error, null);
				return ResponseEntity.status(401).body(computerError.toMap());
			}
			
			// --- SACAMOS LA DATE A UN STRING ---
			String finalDate = date.getYear() + "-" + date.getMonth() + "-" + date.getDay();
			
			// --- ESCRIBIMOS ---
			this.writeText(".\\screenshots\\" + serialNumber + "\\" + finalDate, screenshot.getBytes());
			
			// --- RETORNAMOS OK ---
			return ResponseEntity.ok().build();

		}
		// --- CAPTURAMOS Y ARROJAMOS ---
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
		// --- CREAMOS FLUJOS ---
		FileOutputStream fileOutputStream = null;
		DataOutputStream dataOutputStream = null;

		try
		{
			// --- INICIALIZAMOS LOS FLUJOS ---
			fileOutputStream = new FileOutputStream(name);
			dataOutputStream = new DataOutputStream(fileOutputStream);

			// --- ESCRIBIMOS Y HACEMOS FLUSH ---
			dataOutputStream.write(content);
			dataOutputStream.flush();

		}
		// --- CAPTURAMOS Y ARROJAMOS ---
		catch (IOException exception)
		{
			String message = "Error";
			log.error(message, exception);
		} 
		finally
		{
			// --- CERRAMOS TODOS LOS FLUJOS EN EL FINALLY ---
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
			// --- OBTENEMOS EL MOTHERBOARD CON EL SERIALNUMBER ---
			Optional<Motherboard> motherboard = this.iMotherboardRepository.findById(serialNumber);
			
			// --- SI EL MOTHERBOARD NO ES EMPTY ---
			if (motherboard.isEmpty())
			{
				String error = "Incorrect Serial Number";
				ComputerError computerError = new ComputerError(401, error, null);
				return ResponseEntity.status(401).body(computerError.toMap());
			}

			// --- OBTENEMOS LAS TASKS CON EL SERIALNUMBER Y LAS ACCIONES POR HACER ---
			List<Task> tasks = this.iTaskRepository.findBySerialNumberAndStatus(serialNumber, Action.STATUS_TODO);
			
			// --- ORDENAMOS LAS FECHAS ---
			tasks.sort((o1, o2) -> o1.getTaskId().getDate().compareTo(o2.getTaskId().getDate()));
			
			// --- OBTENEMOS LA PRIMERA TASK ---
			Task task = tasks.get(0);		
			
			// --- CREAMOS TASK DTO---
			TaskDTO taskDTO = new TaskDTO(task.getTaskId().getActionName(),task.getAction().getCommandWindows(),task.getAction().getCommandLinux(), task.getInfo(),task.getTaskId().getDate());
			
			// RETORNAMOS
			return ResponseEntity.ok().body(taskDTO);
		}
		// CAPTURAMOS Y ARROJAMOS 
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

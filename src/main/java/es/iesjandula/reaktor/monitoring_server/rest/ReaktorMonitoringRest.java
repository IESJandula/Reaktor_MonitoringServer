package es.iesjandula.reaktor.monitoring_server.rest;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
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
import es.iesjandula.reaktor.models.Motherboard;
import es.iesjandula.reaktor.models.Reaktor;
import es.iesjandula.reaktor.models.Status;
import es.iesjandula.reaktor.models.Task;
import es.iesjandula.reaktor.models.DTO.TaskDTO;
import es.iesjandula.reaktor.models.Id.TaskId;
import es.iesjandula.reaktor.monitoring_server.reaktor_actions.ReaktorActions;
import es.iesjandula.reaktor.monitoring_server.repository.IMotherboardRepository;
import es.iesjandula.reaktor.monitoring_server.repository.ITaskRepository;
import es.iesjandula.reaktor.monitoring_server.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
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

	/**	Repositorio que se encarga de realizar las operaciones CRUD sobre la entidad Motherboard */
	@Autowired
	private IMotherboardRepository iMotherboardRepository;

	/**	Repositorio que se encarga de realizar las operaciones CRUD sobre la entidad Task */
	@Autowired
	private ITaskRepository iTaskRepository;

	/**	Repositorio que se encarga de realizar las operaciones CRUD sobre varios repositorios dentro de la clase ReaktorActions */
	@Autowired
	private ReaktorActions reaktorActions;

	/**
	 * Enpoint que comprueba que la tarea asignada a un ordenador este en progreso (IN_PROGRESS) para cambiar
	 * su estado a realizada (DONE) o fallida (FAILURE), los ordenadores se identifican por su numero de serie
	 * y la tarea se identifica dentro del objeto status que ademas se comprueba que exista en el ordenador encontrado
	 * 
	 * @param serialNumber numero de serie del ordenador
	 * @param status estado de la tarea
	 * @return ok si la tarea ha cambiado de estado, error si no existe la tarea o el ordenador
	 */
	@Operation
	@RequestMapping(method = RequestMethod.POST, value = "/send/status", consumes = "application/json")
	public ResponseEntity<?> sendStatusComputer(@RequestHeader(required = true) String serialNumber,
			@RequestBody(required = true) Status status)
	{
		try
		{
			log.info(serialNumber);
			log.info(status.toString());
			//Se comprueba qie el ordenador exista 
			Optional<Motherboard> motherboard = this.iMotherboardRepository.findById(serialNumber);
			if (motherboard.isEmpty())
			{
				String error = "El numero de serie "+serialNumber+" no pertenece a ningun ordenador";
				ComputerError computerError = new ComputerError(401, error);
				return ResponseEntity.status(401).body(computerError.toMap());
			}

			//Se obtiene un objeto TaskId para identificar a la entidad Task
			TaskId taskId = new TaskId(serialNumber, status.getTaskDTO().getName(), status.getTaskDTO().getDate());
			Optional<Task> task = this.iTaskRepository.findById(taskId);
			//Se comprueba que la tarea exista
			if (task.isEmpty())
			{
				String error = "La tarea en el ordenador con numero de serie "+serialNumber+" \ny con la tarea "+status.getTaskDTO().getName()+" no existe";
				ComputerError computerError = new ComputerError(401, error);
				return ResponseEntity.status(401).body(computerError.toMap());
			}

			//Se comprueba que la tarea este en progreso en caso de que no lo este se envia un error
			if (!task.get().getStatus().equals(Action.STATUS_IN_PROGRESS))
			{
				String error = "La tarea no se encuentra en progreso, compruebe su estado antes de volver a enviarla";
				ComputerError computerError = new ComputerError(401, error);
				return ResponseEntity.status(401).body(computerError.toMap());
			}

			//Se comprueba si la tarea ha finalizado o no si su valor es true su estado cambia a DONE
			//Si no su estado cambia a FAILURE
			if (status.getStatus())
			{
				task.get().setStatus(Action.STATUS_DONE);
			}
			else
			{
				task.get().setStatus(Action.STATUS_FAILURE);

				log.error(status.getStatusInfo(), status.getError());
			}
			
			//Si todo ha ido bien se salva la tarea en la base de datos
			this.iTaskRepository.saveAndFlush(task.get());
			
			//Se devuelve el estado satisfactorio de la operacion
			return ResponseEntity.ok().build();
		}
		catch (Exception exception)
		{
			String error = "Error a la hora de evaluar el estado de la tarea";
			log.error(error,exception);
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}

	/**
	 * Endpoint que lee y devuelve el contenido de una tarea que contenga un fichero, el ordenador
	 * se identifica por su numero de serie y se comprueba que la tarea tenga una ruta hacia algún fichero
	 * 
	 * @param serialNumber numero de serie del ordenador
	 * @param taskDTO tarea con la ruta del fichero a leer
	 * @return contenido del fichero de la tarea o error si el ordenador no existe o la tarea no posee ningún fichero o si el fichero no se puede leer
	 */
	@Operation
	@RequestMapping(method = RequestMethod.POST, value = "/get/file", produces = "multipart/form-data")
	public ResponseEntity<?> getAnyFile(@RequestHeader(required = true) String serialNumber,
			@RequestBody(required = true) TaskDTO taskDTO)
	{
		try
		{
			//Se comprueba que el ordenador exista
			Optional<Motherboard> motherboard = this.iMotherboardRepository.findById(serialNumber);

			//Si no existe mandamos un error
			if (motherboard.isEmpty())
			{
				String error = "El numero de serie "+serialNumber+" no pertenece a ningun ordenador";
				ComputerError computerError = new ComputerError(401, error, null);
				return ResponseEntity.status(401).body(computerError.toMap());
			}

			//Creamos la id de la tarea para buscar la tarea en especifico
			TaskId taskId = new TaskId(serialNumber, taskDTO.getName(), taskDTO.getDate());

			//Buscamos la tarea por su id
			Optional<Task> task = this.iTaskRepository.findById(taskId);

			//Se comprueba que la tarea exista
			if (task.isEmpty())
			{
				String error = "La tarea en el ordenador con numero de serie "+serialNumber+" \ny con la tarea "+taskDTO.getName()+" no existe";
				ComputerError computerError = new ComputerError(401, error, null);
				return ResponseEntity.status(401).body(computerError.toMap());
			}
			
			if(task.get().getInfo()==null || task.get().getInfo().isEmpty())
			{
				String error = "La tarea del ordenador encontrado no contiene ninguna ruta a ningún fichero del servidor";
				ComputerError computerError = new ComputerError(401,error);
				return ResponseEntity.status(401).body(computerError.toMap());
			}
			//Creamos el flujo de entrada para obtener el contenido del fichero
			InputStreamResource outcomeInputStreamResource = new InputStreamResource(
					new ByteArrayInputStream(this.readText(taskDTO.getInfo())));

			//Devolvemos el contenido del fichero
			return ResponseEntity.ok().body(outcomeInputStreamResource);

		}
		catch (Exception exception)
		{
			String error = "Error a la hora de leer el fichero de la tarea especificada";
			log.error(error,exception);
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}

	/**
	 * Endpoint que recibe una captura de pantalla de un ordenador y la guarda en el servidor en la carpeta
	 * src\main\resources\reaktor_config\screenshots, el fichero se identifica con el numero de serie del
	 * ordenador mas la fecha actual
	 * 
	 * @param screenshot fichero de la captura de pantalla
	 * @param serialNumber numero de serie del ordenador
	 * @param dateLong fecha en formato long
	 * @return ok si escribio bien el fichero, error si no encuentra el ordenador o si escribio mal el fichero
	 */
	@Operation
	@RequestMapping(method = RequestMethod.POST, value = "/send/screenshot", consumes = "multipart/form-data")
	public ResponseEntity<?> sendScreenshot(@RequestBody(required = true) MultipartFile screenshot,
			@RequestHeader(required = true) String serialNumber, @RequestHeader(required = true) Long dateLong)
	{
		try
		{
			//Se comprueba que el ordenador exista
			Optional<Motherboard> motherboard = this.iMotherboardRepository.findById(serialNumber);

			//Si no existe mandamos un error
			if (motherboard.isEmpty())
			{
				String error = "El numero de serie "+serialNumber+" no pertenece a ningun ordenador";
				ComputerError computerError = new ComputerError(401, error, null);
				return ResponseEntity.status(401).body(computerError.toMap());
			}

			//Instanciamos la fecha usando el long
			Date date = new Date(dateLong);

			//Creamos la id de la tarea para buscar la tarea en especifico
			TaskId taskId = new TaskId(serialNumber, "screenshot", date);

			//Buscamos la tarea por su id especifico
			Optional<Task> task = this.iTaskRepository.findById(taskId);

			//Si la tarea no existe mandamos un error
			if (task.isEmpty())
			{
				String error = "La tarea en el ordenador con numero de serie "+serialNumber+" \ny con la tarea "+task.get().getAction().getName()+" no existe";
				ComputerError computerError = new ComputerError(401, error, null);
				return ResponseEntity.status(401).body(computerError.toMap());
			}

			//Se monta la ruta del fichero usando el nombre del fichero pasado mas la fecha parseada
			String fileName = Constants.REAKTOR_CONFIG_EXEC_WEB_SCREENSHOTS + File.separator + "screen_" + serialNumber + "_" + date.toString() + ".png";
			File file = new File(fileName);
			String absolutePath = file.getAbsolutePath();

			log.info("ROUTE: " + absolutePath);

			//Se crea un try-catch con recursos para instanciar y cerrar el flujod e salida
			try (FileOutputStream outputStream = new FileOutputStream(file))
			{
				//Escribimos el contenido del fichero en el fichero destino
				outputStream.write(screenshot.getBytes());
				outputStream.close();
			}
			catch (IOException exception)
			{
				String error = "Error escribiendo el contenido del fichero en el fichero destino";
				log.error(error,exception);
				ComputerError computerError = new ComputerError(500,error, exception);
				return ResponseEntity.status(500).body(computerError.toMap());
			}

			log.info("Saving " + fileName + " -> " + file.exists());

			//Devolvemos el estado satisfactorio de la operacion
			return ResponseEntity.ok().build();

		}
		catch (Exception exception)
		{
			String error = "Error al guardar la captura de pantalla en el servidor";
			log.error(error,exception);
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}

	/**
	 * Endpoint que guarda toda la informacion de un ordenador en base de datos y lo escanea 
	 * para comprobar que existen tareas pendientes de realizar
	 * 
	 * @param serialNumber numero de serie del ordenador
	 * @param reaktorInstance instancia completa del ordenador con hardware y tareas
	 * @return informacion sobre el servidor
	 */
	@Operation
	@RequestMapping(method = RequestMethod.POST, value = "/send/fullInfo", consumes = "application/json", produces = "application/json")
	public ResponseEntity<?> sendFullComputer(@RequestHeader(required = false) String serialNumber,
			@RequestBody(required = true) Reaktor reaktorInstance)
	{
		//Obtenemos toda la informacion y la guardamos con reaktor actions
		log.info("Recibiendo toda la informacion de reaktor {}", reaktorInstance);
		this.reaktorActions.saveReaktor(reaktorInstance);
		return ResponseEntity.ok("Iniciando servidor de reaktor");
	}

	/**
	 * Endpoint que obtiene las tareas en por hacer (TO DO) para ponerlas en progreso
	 * (IN_PROGRESS) para que se lleve a cabo la tarea a realizar sobre ese ordenador
	 * el ordenador se identifica por su numero de serie 
	 * 
	 * @param serialNumber Numero de serie del ordenador
	 * @return Informacion de la tarea especifica por hacer o error si no encuentra el ordenador
	 */
	@Operation
	@RequestMapping(method = RequestMethod.GET, value = "/get/pendingActions", produces = "application/json")
	public ResponseEntity<?> getPendingActions(@RequestHeader(required = true) String serialNumber)
	{
		try
		{
			//Se comprueba que el ordenador exista
			Optional<Motherboard> motherboard = this.iMotherboardRepository.findById(serialNumber);

			//Si no existe mandamos un error
			if (motherboard.isEmpty())
			{
				String error = "El numero de serie "+serialNumber+" no pertenece a ningun ordenador";
				ComputerError computerError = new ComputerError(401, error, null);
				return ResponseEntity.status(401).body(computerError.toMap());
			}

			//Obtenemos la lista de tarea que esten por hacer
			List<Task> tasks = this.iTaskRepository.findByTaskIdSerialNumberAndStatus(serialNumber, Action.STATUS_TODO);
			
			//Se comprueba que haya tareas
			if (!tasks.isEmpty())
			{ 
		        //Ordenamos las tareas por fechas
		        tasks.sort((o1, o2) -> o1.getTaskId().getDate().compareTo(o2.getTaskId().getDate()));
		
		        //Cogemos la primera tarea
		        Task task = tasks.get(0);		

		        //Transformamos la tarea a entidad
		        TaskDTO taskDTO = new TaskDTO(task.getTaskId().getActionName(),task.getAction().getCommandWindows(),task.getAction().getCommandLinux(), task.getInfo(),task.getTaskId().getDate());
					
				//Cambiamos el estado de TO DO a IN_PROGRESS
				task.setStatus(Action.STATUS_IN_PROGRESS);
				
				//Actualizamos la tarea
				this.iTaskRepository.saveAndFlush(task);

		        //Devolvemos su informacion
		        return ResponseEntity.ok().body(taskDTO); 
			}
			//Si no hay tareas devilvemos un ok sin mas
			log.error("No hay tareas por hacer");
			return ResponseEntity.ok().build();
		}
		catch (Exception exception)
		{
			String error = "Error al actualizar el estado de la tarea";
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError.toMap());
		}
	}
}

	/**
	 * Metodo que se le pasa el nombre del fichero y devuelve un array de bytes 
	 * representando el contenido del fichero enviado
	 * 
	 * @param name nombre del fichero
	 * @return byte[] contenido del fichero EN BYTES
	 * @throws ComputerError 
	 */
	public byte[] readText(String name) throws ComputerError
	{
	
		//Creamos los flujos de entrada
		FileInputStream fileInputStream = null;
		DataInputStream dataInputStream = null;
	
		try
		{
			//Se inicializan los flujos de entrada
			fileInputStream = new FileInputStream(name);
			dataInputStream = new DataInputStream(fileInputStream);
	
			//Devolvemos el contenido de bytes
			return dataInputStream.readAllBytes();
		}
		catch (IOException exception)
		{
			String message = "Error al leer el contenido del fichero pasado";
			log.error(message, exception);
			throw new ComputerError(500, message, exception);
		}
		finally
		{
			if (dataInputStream != null)
			{
				try
				{
					dataInputStream.close();
				}
				catch (IOException exception)
				{
					String message = "Error al cerrar el flujo de entrada de datos";
					log.error(message, exception);
				}
			}
	
			if (fileInputStream != null)
			{
				try
				{
					fileInputStream.close();
				}
				catch (IOException exception)
				{
					String message = "Error al cerrar el flujo de entrada general";
					log.error(message, exception);
				}
			}
		}
	}
}	
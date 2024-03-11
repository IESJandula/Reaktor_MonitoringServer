package es.iesjandula.reaktor.monitoring_server.utils;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import es.iesjandula.reaktor.exceptions.ComputerError;
import es.iesjandula.reaktor.models.Action;
import es.iesjandula.reaktor.models.Motherboard;
import es.iesjandula.reaktor.models.Task;
import es.iesjandula.reaktor.models.Id.TaskId;
import es.iesjandula.reaktor.monitoring_server.repository.IMotherboardRepository;
import es.iesjandula.reaktor.monitoring_server.repository.ITaskRepository;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AdminUtils 
{
	/**Logger de la clase */
	private static Logger log = LogManager.getLogger();
	/**
	 * Metodo que inserta un ordenador en el conjunto de ordenadores y lo devuelve para actualizarlo en la clase principal
	 * @param classroom clase a buscar en el ordenador o ordenadores
	 * @param motherboardList conjunto de ordenadores
	 * @param iMotherboardRepository repositorio para operaciones CRUD
	 * @return conjunto de ordenadores actualizado
	 */
	public Set<Motherboard> addByClassroom(String classroom, Set<Motherboard> motherboardList, IMotherboardRepository iMotherboardRepository)
	{
		List<Motherboard> motherboardId = iMotherboardRepository.findByClassroom(classroom);
		motherboardList.addAll(motherboardId);
		return motherboardList;
	}

	/**
	 * Metodo que inserta un ordenador en el conjunto de ordenadores y lo devuelve para actualizarlo en la clase principal
	 * @param trolley carrito a buscar en el ordenador o ordenadores
	 * @param motherboardList conjunto de ordenadores
	 * @param iMotherboardRepository repositorio para operaciones CRUD
	 * @return conjunto de ordenadores actualizado
	 */
	public Set<Motherboard> addByTrolley(String trolley, Set<Motherboard> motherboardList,IMotherboardRepository iMotherboardRepository)
	{
		List<Motherboard> motherboardId = iMotherboardRepository.findByTrolley(trolley);
		motherboardList.addAll(motherboardId);
		return motherboardList;
	}

	/**
	 * Metodo que inserta un ordenador en el conjunto de ordenadores y lo devuelve para actualizarlo en la clase principal
	 * @param floor planta a buscar en el ordenador o ordenadores
	 * @param motherboardList conjunto de ordenadores
	 * @param iMotherboardRepository repositorio para operaciones CRUD
	 * @return conjunto de ordenadores actualizado
	 */
	public Set<Motherboard> addByFloor(int floor, Set<Motherboard> motherboardList,IMotherboardRepository iMotherboardRepository)
	{
		List<Motherboard> motherboardId = iMotherboardRepository.findByFloor(floor);
		motherboardList.addAll(motherboardId);
		return motherboardList;
	}

	/**
	 * Metodo que inserta un ordenador en el conjunto de ordenadores y lo devuelve para actualizarlo en la clase principal
	 * @param serialNumber numero de serie a buscar enel ordenador
	 * @param motherboardList conjunto de ordenadores
	 * @param iMotherboardRepository repositorio para operaciones CRUD
	 * @return conjunto de ordenadores actualizado
	 */
	public Set<Motherboard> addBySerialNumber(String serialNumber, Set<Motherboard> motherboardList,IMotherboardRepository iMotherboardRepository)
	{
		Optional<Motherboard> motherboardId = iMotherboardRepository.findById(serialNumber);
		if (motherboardId.isPresent())
		{
			motherboardList.add(motherboardId.get());
		}
		return motherboardList;
	}
	
	/**
	 * Metodo que añade al conjunto de ordenadores todos los ordenadores de la BBDD
	 * @param motherboardList conjunto de ordenadores
	 * @param iMotherboardRepository repositorio que contiene las operaciones CRUD
	 * @return conjunto de ordenadores actualizado
	 */
	public Set<Motherboard> addByAll(Set<Motherboard> motherboardList,IMotherboardRepository iMotherboardRepository)
	{
		List<Motherboard> motherboardId = iMotherboardRepository.findAll();
		motherboardList.addAll(motherboardId);
		return motherboardList;
	}
	
	/**
	 * Meodo que inserta varias tareas en una base de datos
	 * @param motherboardList conjunto de placas base que referencian a las tareas
	 * @param action accion a realizar
	 * @param info informacion adicional de la accion
	 * @param iTaskRepository repositorio que contiene las operaciones CRUD
	 */
	public void addTasks(Set<Motherboard> motherboardList, Action action, String info,ITaskRepository iTaskRepository)
	{
		// SACAMOS LA FECHA ACTUAL
		Date date = new Date();
		
		// --- POR CADA PC EN LA LISTA , CREAREMOS SU TAREA ---
		for (Motherboard motherboard : motherboardList)
		{
			// -- -CREAMOS LA TASK ---
			Task task = new Task();
			// --- CREAMOS LA ID DE LA TASK --
			TaskId taskId = new TaskId();

			// --- PONEMOS EL NOMBRE DE LA ACCION (TASK)---
			taskId.setActionName(action.getName());
			
			// --- PONEMOS LA DATE ---
			taskId.setDate(date);
			
			// --- PONEMOS EL SERIALNUMBER ---
			taskId.setSerialNumber(motherboard.getMotherBoardSerialNumber());

			// ARMAMOS EL TASK CON TODO LO ANTERIOR ---
			task.setTaskId(taskId);
			task.setAction(action);
			task.setMotherboard(motherboard);
			// --- PONEMOS EL INFO NECESARIO
			task.setInfo(info);
			
			// PONEMOS EN ESTADO DE TODO
			task.setStatus(Action.STATUS_TODO);

			// --- GUARDAMOS ---
			iTaskRepository.saveAndFlush(task);
		}
	}
	/**
	 * Metodo que guarda en la base de datos una única tarea
	 * @param motherboard placa base que referencia la tarea
	 * @param action accion a realizar 
	 * @param info informacion adicional de la accion
	 * @param iTaskRepository repositorio que contiene las operaciones CRUD
	 */
	public void addTask(Motherboard motherboard, Action action, String info,ITaskRepository iTaskRepository) 
	{
		
		// SACAMOS LA FECHA ACTUAL
		Date date = new Date();
		
		// -- -CREAMOS LA TASK ---
		Task task = new Task();
		// --- CREAMOS LA ID DE LA TASK --
		TaskId taskId = new TaskId();

		// --- PONEMOS EL NOMBRE DE LA ACCION (TASK)---
		taskId.setActionName(action.getName());
		
		// --- PONEMOS LA DATE ---
		taskId.setDate(date);
		
		// --- PONEMOS EL SERIALNUMBER ---
		taskId.setSerialNumber(motherboard.getMotherBoardSerialNumber());

		// ARMAMOS EL TASK CON TODO LO ANTERIOR ---
		task.setTaskId(taskId);
		task.setAction(action);
		task.setMotherboard(motherboard);
		// --- PONEMOS EL INFO NECESARIO
		task.setInfo(info);
		
		// PONEMOS EN ESTADO DE TODO
		task.setStatus(Action.STATUS_TODO);

		// --- GUARDAMOS ---
		iTaskRepository.saveAndFlush(task);
	}
	
	/**
	 * Metodo que escribe el contenido guardado en bytes en un fichero aparte para guardarlo en el servidor
	 * @param name nombre del fichero
	 * @param content contenido del fichero
	 */
	public void writeText(String name, byte[] content) throws ComputerError
	{
		// DELCARAMOS FLUJOS
		FileOutputStream fileOutputStream = null;
		DataOutputStream dataOutputStream = null;

		try
		{
			// CREAMOS LOS FLUJOS
			fileOutputStream = new FileOutputStream(name);
			dataOutputStream = new DataOutputStream(fileOutputStream);
			
			// GUARDAMOS EL FICHERO
			dataOutputStream.write(content);
			// HACEMOS FLUSH
			dataOutputStream.flush();

		}
		catch (IOException exception)
		{
			String message = "Error trasladando el contenido del fichero";
			log.error(message, exception);
			throw new ComputerError(500,message,exception);
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
					String message = "Error cerrando e flujo de transmision de datos";
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
					String message = "Error cerrando el flujo de salida";
					log.error(message, exception);
				}
			}
		}
	}


}

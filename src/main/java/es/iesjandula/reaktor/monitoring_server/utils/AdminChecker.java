package es.iesjandula.reaktor.monitoring_server.utils;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.util.List;
import java.util.Set;

import org.springframework.web.multipart.MultipartFile;

import es.iesjandula.reaktor.models.Motherboard;
import es.iesjandula.reaktor.models.Usb;
import es.iesjandula.reaktor.monitoring_server.repository.IMotherboardRepository;

/**
 * @author Pablo Ruiz Canovas
 */
public class AdminChecker 
{
	/**Clase admin utils que contiene los metodos de añadir atributos */
	private  AdminUtils utils;
	/**Repositorio que realiza las acciones CRUD sobre la entidad Motherboard */
	private IMotherboardRepository motherboardRepository;
	
	/**
	 * Constructor que crea la clase AdminChecker usando el repositorio de la placa base
	 * e instanciando la clase AdminUtils dentro del constructor para el uso de sus metodos
	 * @param motherboardRepository repositorio para realizar acciones CRUD
	 */
	public AdminChecker(IMotherboardRepository motherboardRepository) 
	{
		this.motherboardRepository = motherboardRepository;
		this.utils = new AdminUtils();
	}

	/**
	 * Metodo que se encarga de comprobar los parametros de los endpoints <br>
	 * <br>
	 * {@link #ReaktorAdministrationRest.postComputerCommandLine(String, String, String, Integer, String)}<br>
	 * <br>
	 * {@link #putComputerShutdown(String, String, String, Integer)}<br>
	 * <br>
	 * {@link #putComputerRestart(String, String, String, Integer)}<br>
	 * <br>
	 * {@link #postComputerExecFile(String, String, String, Integer, String, MultipartFile)}<br>
	 * <br>
	 * {@link #sendScreenshotOrder(String, String, String)}<br>
	 * <br>
	 * para enviar y añadir al conjunto de ordenadores usando sus identificadores
	 * @param serialNumber numero de serie del ordenador
	 * @param classroom clase a la que pertenece
	 * @param trolley carrito al que pertenece
	 * @param floor planta en la que se encuentra
	 * @param set conjunto que guarda los ordenadores
	 */
	public void checkAndSend(String serialNumber,String classroom,String trolley,Integer floor,Set<Motherboard> set)
	{
		String methodsUsed = "";

		if (serialNumber != null)
		{
			//Se envia la peticion por el numero de serie
			utils.addBySerialNumber(serialNumber, set,this.motherboardRepository);
			methodsUsed += "serialNumber,";
		}
		if (trolley != null)
		{
			//Se envia la peticion por carrito
			utils.addByTrolley(trolley, set,this.motherboardRepository);
			methodsUsed += "trolley,";
		}
		if (classroom != null)
		{
			//Se envia la peticion por la clase
			utils.addByClassroom(classroom, set,this.motherboardRepository);
			methodsUsed += "classroom,";
		}
		if (floor != null)
		{
			//Se envia la peticion por la planta
			utils.addByFloor(floor, set,this.motherboardRepository);
			methodsUsed += "floor,";
		}
		//log.info("Parameters Used: " + methodsUsed);
	}
	
	/**
	 * Metodo sobrecargado que se encarga de comprobar los parametros de los endpoints <br>
	 * {@link #postPeripheral(String, String, Usb)}<br>
	 * <br>
	 * {@link #unistallSoftware(String, String, String, String)}<br>
	 * <br>
	 * {@link #sendSoftware(String, String, String, String)}<br>
	 * <br>
	 * @param trolley carrito al que pertenece el ordenador
	 * @param classroom clase enla que se encuentra
	 * @param professor profesor que dirije el ordenador
	 * @param set conjunto que guarda los ordenadores
	 * @see original {@link #checkAndSend(String, String, String, Integer, Set)}
	 */
	public void checkAndSend(String trolley,String classroom,String professor,Set<Motherboard> set)
	{
		String methodsUsed = "";

		if (professor != null)
		{
			//Se envia la peticion por el profesor
			List<Motherboard> motherboardList = this.iMotherboardRepository.findByTeacher(professor);
			set.addAll(motherboardList);
			methodsUsed += "professor,";
		}
		if (trolley != null)
		{
			//Se envia la peticion por el carrito
			List<Motherboard> motherboardList = this.iMotherboardRepository.findByTrolley(trolley);
			set.addAll(motherboardList);
			methodsUsed += "trolley,";
		}
		if (classroom != null)
		{
			//Se envia la peticion por la clase
			List<Motherboard> motherboardList = this.iMotherboardRepository.findByClassroom(classroom);
			set.addAll(motherboardList);
			methodsUsed += "classroom,";
		}
		
		//log.info("Parameters Used: " + methodsUsed);
	}
}

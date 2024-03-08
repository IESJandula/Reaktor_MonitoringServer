package es.iesjandula.reaktor.monitoring_server.rest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.exceptions.ComputerError;
import es.iesjandula.reaktor.models.Motherboard;
import es.iesjandula.reaktor.monitoring_server.repository.IMotherboardRepository;
import es.iesjandula.reaktor.monitoring_server.utils.Constants;
import es.iesjandula.reaktor.monitoring_server.utils.WebUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author David Martinez
 * @author Javier Megias
 * @author Adrian verdejo
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/computers")
@Slf4j
public class ReaktorWebRest
{
	/**	Repositorio que se encarga de realizar las operaciones CRUD sobre la entidad Motherboard */
	@Autowired
	private IMotherboardRepository iMotherboardRepository;
	
	/**Clase que se encarga de operaciones basicas para los endpoints */
	private WebUtils webUtils;
	
	/**
	 * Constructor que instancia la clase WebUtils
	 */
	public ReaktorWebRest()
	{
		this.webUtils = new WebUtils(this.iMotherboardRepository);
	}
	
	/**
	 * Endpoint que filtra y muestra ordenadores por sus atributos
	 * en caso de que no se pase ningun atributo devolvera todos los ordenadores
	 * 
	 * @param serialNumber numero de serie del ordenador
	 * @param andaluciaId numero de andalucia del ordenador
	 * @param computerNumber pegatina identificativa del ordenador
	 * @param classroom clase en la que se encuentra el ordenador
	 * @param trolley carrito al que pertenece el ordenador
	 * @param floor planta en la que se encuentra el ordenador 
	 * @param professor profesor que dirige el ordenador
	 * @return lista de ordenadores encontrados o error si algun parametro esta mal introducido
	 */
	@Operation
	@RequestMapping(method = RequestMethod.POST, value = "/web", produces = "application/json")
	public ResponseEntity<?> getComputersByAny(
			@RequestHeader(required = false) String serialNumber,
			@RequestHeader(required = false) String andaluciaId,
			@RequestHeader(required = false) String computerNumber,
			@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley,
			@RequestHeader(required = false) Integer floor,
			@RequestHeader(required = false) String professor
			)
	{
		try
		{
			//Lista vacia para buscar los ordenadores
			List<Motherboard> list = new ArrayList<>();
			
			//Comprobamos que haya al menos un parametro 
			if(serialNumber != null || andaluciaId!=null || computerNumber!=null || classroom!=null || trolley!=null || floor!=null || professor!=null)
			{
				//Buscamos por numero de serie
				this.webUtils.getBySerialNumber(serialNumber, list);
	
				//Buscamos por andalucia id
				this.webUtils.getByAndaluciaId(andaluciaId, list);
	
				//Buscamos por numero de ordenador
				this.webUtils.getByComputerNumber(computerNumber, list);
	
				//Buscamos por clase
				this.webUtils.getByClassroom(classroom, list);
	
				//Buscamos por carrito
				this.webUtils.getByTrolley(trolley, list);
	
				//Buscamos por planta
				this.webUtils.getByFloor(floor, list);
	
				//Buscamos por profesor
				this.webUtils.getByProfessor(professor, list);
				
				log.info(list.toString());
				
				//Si la lista esta vacia devolvemos un error
				if(list.isEmpty()) 
				{
					String error = "Error no hay conincidencias";
					ComputerError computerError = new ComputerError(400, error);
					return ResponseEntity.status(400).body(computerError.toMap());
				}
				
				//Si la lista tiene datos devolvemos los ordenadores encontrados
				return ResponseEntity.ok(list);
			}
			else
			{
				//Si no se introduce ningun parametro devolvemos todos los ordenadores
				list = this.iMotherboardRepository.findAll();
				return ResponseEntity.ok(list);
			}
		}
		catch(Exception exception)
		{
			String error = "Error al filtrar ordenadores";
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError);
		}

	}

	/**
	 * Endpoint que busca un ordenador y devuelve un zip con sus capturas de pantalla
	 * el ordenador se identifica con varios parametros
	 * @param classroom
	 * @param trolley
	 * @param floor
	 * @param professor
	 * @return Carpeta .zip de capturas de pantalla
	 */
	@Operation
	@RequestMapping(method = RequestMethod.GET, value = "/web/screenshot", produces = "application/zip")
	public ResponseEntity<?> getComputersScreens(
			@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley,
			@RequestHeader(required = false) Integer floor,
			@RequestHeader(required = false) String professor
			)
	{
		try
		{
			//Comando para zipear una carpeta
			String finalZipCommand = "tar -a -c -f Compressed.zip";
			
			//Se añade la clase al comando
			finalZipCommand = this.getToZipCommandByClassroom(classroom, finalZipCommand);

			//Se añade el carrito al comando
			finalZipCommand = this.getToZipCommandByTrolley(trolley, finalZipCommand);

			//Se añade la planta al comando
			finalZipCommand = this.getToZipCommandByfloor(floor, finalZipCommand);

			//Se añade el profesor al comando
			finalZipCommand = this.getToZipCommandByProfessor(professor, finalZipCommand);

			//Se añaden todos los ordenadores
			finalZipCommand = this.getToZipCommandByNullAll(classroom, trolley, floor, professor, finalZipCommand);

			log.info(finalZipCommand);
			
			//Comprobamos que el comando contenga parametros si no los tiene devolvemos un error
			if(!finalZipCommand.equalsIgnoreCase("tar -a -c -f Compressed.zip"))
			{
				
				try
				{
					//Metodo que ejecuta el comando y devuelve la carpeta .zip
					return this.executeFinalZipCommand(finalZipCommand);
				}
				catch (IOException exception)
				{
					String error = "Error En la compresion";
					ComputerError computerError = new ComputerError(500, error, exception);
					return ResponseEntity.status(500).body(computerError);
				}
			}
			else
			{
				String error = "Error no hay capturas que coincidan";
				ComputerError computerError = new ComputerError(400, error, null);
				return ResponseEntity.status(400).body(computerError);
			}

		}
		catch(Exception exception)
		{
			String error = "Error del Servidor";
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError);
		}
	}


	/**
	 * Method executeFinalZipCommand
	 * @param finalZipCommand
	 * @return
	 * @throws IOException
	 */
	private ResponseEntity<?> executeFinalZipCommand(String finalZipCommand) throws IOException
	{
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec("cmd.exe /c " + finalZipCommand);

		File file = new File("Compressed.zip");
		byte[] bytesArray = Files.readAllBytes(file.toPath());

		// --- SETTING THE HEADERS WITH THE NAME OF THE FILE TO DOWLOAD PDF ---
		HttpHeaders responseHeaders = new HttpHeaders();
		//--- SET THE HEADERS ---
		responseHeaders.set("Content-Disposition", "attachment; filename="+file.getName());

		return ResponseEntity.ok().headers(responseHeaders).body(bytesArray);
	}


	/**
	 * Method getToZipCommandByNullAll
	 * @param classroom
	 * @param trolley
	 * @param floor
	 * @param professor
	 * @param finalZipCommand
	 * @return
	 */
	private String getToZipCommandByNullAll(String classroom, String trolley, Integer floor, String professor,
			String finalZipCommand)
	{
		if((classroom==null) && (trolley == null) && (floor == null) && (professor == null) )
		{
			for(Motherboard motherboard : this.iMotherboardRepository.findAll())
			{
				finalZipCommand+= " " + Constants.REAKTOR_CONFIG_EXEC_WEB_SCREENSHOTS + File.separator + motherboard.getMotherBoardSerialNumber()+".png";
			}
		}
		return finalZipCommand;
	}


	/**
	 * Method getToZipCommandByProfessor
	 * @param professor
	 * @param finalZipCommand
	 * @return
	 */
	private String getToZipCommandByProfessor(String professor, String finalZipCommand)
	{
		if((professor!=null) && !professor.isBlank() && !professor.isEmpty())
		{
			for(Motherboard motherboard : this.iMotherboardRepository.findByTeacher(professor))
			{
				finalZipCommand+= " " + Constants.REAKTOR_CONFIG_EXEC_WEB_SCREENSHOTS + File.separator + motherboard.getMotherBoardSerialNumber()+".png";
			}
		}
		return finalZipCommand;
	}


	/**
	 * Method getToZipCommandByfloor
	 * @param floor
	 * @param finalZipCommand
	 * @return
	 */
	private String getToZipCommandByfloor(Integer floor, String finalZipCommand)
	{
		if(floor!=null)
		{
			for(Motherboard motherboard : this.iMotherboardRepository.findByFloor(floor))
			{
				finalZipCommand+= " " + Constants.REAKTOR_CONFIG_EXEC_WEB_SCREENSHOTS + File.separator + motherboard.getMotherBoardSerialNumber()+".png";
			}
		}
		return finalZipCommand;
	}


	/**
	 * Method getToZipCommandByTrolley
	 * @param trolley
	 * @param finalZipCommand
	 * @return
	 */
	private String getToZipCommandByTrolley(String trolley, String finalZipCommand)
	{
		if((trolley!=null) && !trolley.isBlank() && !trolley.isEmpty())
		{
			for(Motherboard motherboard : this.iMotherboardRepository.findByTrolley(trolley))
			{
				finalZipCommand+= " " + Constants.REAKTOR_CONFIG_EXEC_WEB_SCREENSHOTS + File.separator + motherboard.getMotherBoardSerialNumber()+".png";
			}
		}
		return finalZipCommand;
	}


	/**
	 * Method getToZipCommandByClassroom
	 * @param classroom
	 * @param finalZipCommand
	 * @return
	 */
	private String getToZipCommandByClassroom(String classroom, String finalZipCommand)
	{
		if((classroom!=null) && !classroom.isBlank() && !classroom.isEmpty())
		{
			for(Motherboard motherboard : this.iMotherboardRepository.findByClassroom(classroom))
			{
				finalZipCommand+= " " + Constants.REAKTOR_CONFIG_EXEC_WEB_SCREENSHOTS + File.separator + motherboard.getMotherBoardSerialNumber()+".png";
			}
		}
		return finalZipCommand;
	}



}
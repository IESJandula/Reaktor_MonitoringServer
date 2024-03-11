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
		this.webUtils = new WebUtils();
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
				this.webUtils.getBySerialNumber(serialNumber, list,this.iMotherboardRepository);
	
				//Buscamos por andalucia id
				this.webUtils.getByAndaluciaId(andaluciaId, list,this.iMotherboardRepository);
	
				//Buscamos por numero de ordenador
				this.webUtils.getByComputerNumber(computerNumber, list,this.iMotherboardRepository);
	
				//Buscamos por clase
				this.webUtils.getByClassroom(classroom, list,this.iMotherboardRepository);
	
				//Buscamos por carrito
				this.webUtils.getByTrolley(trolley, list,this.iMotherboardRepository);
	
				//Buscamos por planta
				this.webUtils.getByFloor(floor, list,this.iMotherboardRepository);
	
				//Buscamos por profesor
				this.webUtils.getByProfessor(professor, list,this.iMotherboardRepository);
				
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
	 * 
	 * @param classroom clase en la que se encuentra 
	 * @param trolley carrito al que pertenece
	 * @param floor planta en la que se encuentra
	 * @param professor profesor que gestiona el ordenador
	 * @return Carpeta .zip de capturas de pantalla
	 * @see {@link #executeFinalZipCommand(String)} metodo que ejecuta el comando
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
			finalZipCommand = this.webUtils.getToZipCommandByClassroom(classroom, finalZipCommand,this.iMotherboardRepository);

			//Se añade el carrito al comando
			finalZipCommand = this.webUtils.getToZipCommandByTrolley(trolley, finalZipCommand,this.iMotherboardRepository);

			//Se añade la planta al comando
			finalZipCommand = this.webUtils.getToZipCommandByfloor(floor, finalZipCommand,this.iMotherboardRepository);

			//Se añade el profesor al comando
			finalZipCommand = this.webUtils.getToZipCommandByProfessor(professor, finalZipCommand,this.iMotherboardRepository);

			//Se añaden todos los ordenadores
			finalZipCommand = this.webUtils.getToZipCommandByNullAll(classroom, trolley, floor, professor, finalZipCommand,this.iMotherboardRepository);

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
	 * Metodo que ejecuta el comando que comprime la carpeta .zip con las capturas de pantalla
	 * usando los parametros introducidos del endpoint al que pertenece
	 * 
	 * @param finalZipCommand comando para comprimir un archivo
	 * @return la carpeta .zip con el contenido
 	 * @throws IOException
	 */
	private ResponseEntity<?> executeFinalZipCommand(String finalZipCommand) throws IOException
	{
		//Se crea el entorno de ejecucion
		Runtime rt = Runtime.getRuntime();
		//Se llama a la cmd y se pone como argumento el comando para zipear una carpeta
		Process pr = rt.exec("cmd.exe /c " + finalZipCommand);

		File file = new File("Compressed.zip");
		byte[] bytesArray = Files.readAllBytes(file.toPath());

		/*
		 * Colocamos cabeceras para que a la hora de descargar el fichero se identifique
		 * con una extension en este caso .zip
		 */
		HttpHeaders responseHeaders = new HttpHeaders();
		//Le damos valor a las cabeceras
		responseHeaders.set("Content-Disposition", "attachment; filename="+file.getName());
		//Retornamos el valor de las cabeceras junto con la carpeta comprimida
		return ResponseEntity.ok().headers(responseHeaders).body(bytesArray);
	}

}
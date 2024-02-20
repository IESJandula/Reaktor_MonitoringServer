package es.iesjandula.reaktor.monitoring_server.rest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	@Autowired
	private IMotherboardRepository iMotherboardRepository;

	/**
	 * Method getComputersByAny
	 * @param serialNumber
	 * @param andaluciaId
	 * @param computerNumber
	 * @param classroom
	 * @param trolley
	 * @param floor
	 * @param professor
	 * @param hardwareList
	 * @return ResponseEntity
	 */
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
			// --- EMPTY LIST ---
			List<Motherboard> list = new ArrayList<>();

			// --- BY SERIAL NUMBER ---
			this.getBySerialNumber(serialNumber, list);

			// --- BY andaluciaId---
			this.getByAndaluciaId(andaluciaId, list);

			// --- BY computerNumber---
			this.getByComputerNumber(computerNumber, list);

			// --- BY classroom---
			this.getByClassroom(classroom, list);

			// --- BY trolley---
			this.getByTrolley(trolley, list);

			// --- BY floor---
			this.getByFloor(floor, list);

			// --- BY professor---
			this.getByProfessor(professor, list);
			
			log.info(list.toString());
			
			// --- 400 ERROR ---
			if(list.isEmpty()) 
			{
				String error = "Error no hay conincidencias";
				ComputerError computerError = new ComputerError(400, error, null);
				return ResponseEntity.status(400).body(computerError);
			}

			return ResponseEntity.ok(list);
		}
		catch(Exception exception)
		{
			String error = "Error del Servidor";
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError);
		}

	}


	/**
	 * Method getByProfessor
	 * @param professor
	 * @param list
	 */
	private void getByProfessor(String professor, List<Motherboard> list)
	{
		if((professor!=null) && !professor.isBlank() && !professor.isEmpty())
		{
			list.addAll(this.iMotherboardRepository.findByTeacher(professor));
		}
	}


	/**
	 * Method getByFloor
	 * @param floor
	 * @param list
	 */
	private void getByFloor(Integer floor, List<Motherboard> list)
	{
		if(floor!=null)
		{
			list.addAll(this.iMotherboardRepository.findByFloor(floor));
		}
	}


	/**
	 * Method getByTrolley
	 * @param trolley
	 * @param list
	 */
	private void getByTrolley(String trolley, List<Motherboard> list)
	{
		if((trolley!=null) && !trolley.isBlank() && !trolley.isEmpty())
		{
			list.addAll(this.iMotherboardRepository.findByTrolley(trolley));
		}
	}


	/**
	 * Method getByClassroom
	 * @param classroom
	 * @param list
	 */
	private void getByClassroom(String classroom, List<Motherboard> list)
	{
		if((classroom!=null) && !classroom.isBlank() && !classroom.isEmpty())
		{
			list.addAll(this.iMotherboardRepository.findByClassroom(classroom));
		}
	}


	/**
	 * Method getByComputerNumber
	 * @param computerNumber
	 * @param list
	 */
	private void getByComputerNumber(String computerNumber, List<Motherboard> list)
	{
		if((computerNumber!=null) && !computerNumber.isBlank() && !computerNumber.isEmpty())
		{
			list.addAll(this.iMotherboardRepository.findByComputerNumber(computerNumber));
		}
	}


	/**
	 * Method getByAndaluciaId
	 * @param andaluciaId
	 * @param list
	 */
	private void getByAndaluciaId(String andaluciaId, List<Motherboard> list)
	{
		if((andaluciaId!=null) && !andaluciaId.isBlank() && !andaluciaId.isEmpty())
		{
			list.addAll(this.iMotherboardRepository.findByAndaluciaId(andaluciaId));
		}
	}


	/**
	 * Method getBySerialNumber
	 * @param serialNumber
	 * @param list
	 */
	private void getBySerialNumber(String serialNumber, List<Motherboard> list)
	{
		if((serialNumber!=null) && !serialNumber.isBlank() && !serialNumber.isEmpty())
		{
			Optional<Motherboard> mOptional = this.iMotherboardRepository.findById(serialNumber);
			
			if (mOptional.isPresent())
			{
				list.add(mOptional.get());
			}
			
		}
	}


	/**
	 * Method getComputersScreens
	 * @param classroom
	 * @param trolley
	 * @param floor
	 * @param professor
	 * @return ResponseEntity
	 */
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
			// --- THE FINAL COMMAND ZIP ---

			String finalZipCommand = "tar -a -c -f Compressed.zip";
			// --- BY classroom---
			finalZipCommand = this.getToZipCommandByClassroom(classroom, finalZipCommand);

			// --- BY trolley---
			finalZipCommand = this.getToZipCommandByTrolley(trolley, finalZipCommand);

			// --- BY floor---
			finalZipCommand = this.getToZipCommandByfloor(floor, finalZipCommand);

			// --- BY professor---
			finalZipCommand = this.getToZipCommandByProfessor(professor, finalZipCommand);

			// --- BY ALL---
			finalZipCommand = this.getToZipCommandByNullAll(classroom, trolley, floor, professor, finalZipCommand);

			log.info(finalZipCommand);
			
			if(!finalZipCommand.equalsIgnoreCase("tar -a -c -f Compressed.zip"))
			{
				
				try
				{
					// --- METHOD WITH THE EXECUTION PROCESS AND THE RESPONSE ENTITY ---
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
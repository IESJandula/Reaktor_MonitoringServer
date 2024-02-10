package es.iesjandula.reaktor.monitoring_server.rest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.iesjandula.reaktor.exceptions.ComputerError;
import es.iesjandula.reaktor.models.CommandLine;
import es.iesjandula.reaktor.models.Computer;
import es.iesjandula.reaktor.models.HardwareComponent;
import es.iesjandula.reaktor.models.Location;
import es.iesjandula.reaktor.models.MonitorizationLog;
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
	/** Attribute computerList */
	private List<Computer> computerList = new ArrayList<>(List.of(
			new Computer("sn123", "and123", "cn123", "windows", "paco", new Location("0.5", 0, "trolley1"),
					new ArrayList<>(), new ArrayList<>(), new CommandLine(),
					new MonitorizationLog()),
			new Computer("sn1234", "and1234", "cn12344", "windows", "paco", new Location("0.5", 0, "trolley1"),
					new ArrayList<>(), new ArrayList<>(), new CommandLine(),
					new MonitorizationLog()),
			new Computer("sn123434231423423", "and12355", "cn123455", "windows", "paco", new Location("0.7", 0, "trolley2"),
					new ArrayList<>(), new ArrayList<>(), new CommandLine(),
					new MonitorizationLog()),
			new Computer("sn123556", "and123556", "cn1234556", "windows", "paco", new Location("0.7", 0, "trolley2"),
					new ArrayList<>(), new ArrayList<>(), new CommandLine(),
					new MonitorizationLog()),
			new Computer("sn123777", "and123777", "cn1234777", "windows", "paco", new Location("0.9", 0, "trolley3"),
					new ArrayList<>(), new ArrayList<>(), new CommandLine(),
					new MonitorizationLog())

	));


	/**
	 * Method getComputersByAny
	 * @param serialNumber
	 * @param andaluciaId
	 * @param computerNumber
	 * @param classroom
	 * @param trolley
	 * @param plant
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
			@RequestHeader(required = false) Integer plant,
			@RequestHeader(required = false) String professor,
			@RequestBody(required = false) List<HardwareComponent> hardwareList
			)
	{
		try
		{
			// --- EMPTY LIST ---
			List<Computer> computerFilterList = new ArrayList<>();

			// --- BY SERIAL NUMBER ---
			this.getBySerialNumber(serialNumber, computerFilterList);

			// --- BY andaluciaId---
			this.getByAndaluciaId(andaluciaId, computerFilterList);

			// --- BY computerNumber---
			this.getByComputerNumber(computerNumber, computerFilterList);

			// --- BY classroom---
			this.getByClassroom(classroom, computerFilterList);

			// --- BY trolley---
			this.getByTrolley(trolley, computerFilterList);

			// --- BY plant---
			this.getByPlant(plant, computerFilterList);

			// --- BY professor---
			this.getByProfessor(professor, computerFilterList);

			// --- BY hardwareList---
			this.getByHardwareList(hardwareList, computerFilterList);
			
			log.info(computerFilterList.toString());
			
			// --- 400 ERROR ---
			if(computerFilterList.isEmpty()) 
			{
				String error = "Error no hay conincidencias";
				ComputerError computerError = new ComputerError(400, error, null);
				return ResponseEntity.status(400).body(computerError);
			}

			return ResponseEntity.ok(computerFilterList);
		}
		catch(Exception exception)
		{
			String error = "Error del Servidor";
			ComputerError computerError = new ComputerError(500, error, exception);
			return ResponseEntity.status(500).body(computerError);
		}

	}


	/**
	 * Method getByHardwareList
	 * @param hardwareList
	 * @param computerFilterList
	 */
	private void getByHardwareList(List<HardwareComponent> hardwareList, List<Computer> computerFilterList)
	{
		if((hardwareList!=null) && (hardwareList.size()>0))
		{
			for(Computer computer : this.computerList)
			{
				for(HardwareComponent computerComponent : computer.getHardwareList())
				{
					if(hardwareList.contains(computerComponent))
					{
						computerFilterList.add(computer);
					}
				}
			}
		}
	}


	/**
	 * Method getByProfessor
	 * @param professor
	 * @param computerFilterList
	 */
	private void getByProfessor(String professor, List<Computer> computerFilterList)
	{
		if((professor!=null) && !professor.isBlank() && !professor.isEmpty())
		{
			for(Computer computer : this.computerList)
			{
				if(computer.getProfessor().equals(professor))
				{
					computerFilterList.add(computer);
				}
			}
		}
	}


	/**
	 * Method getByPlant
	 * @param plant
	 * @param computerFilterList
	 */
	private void getByPlant(Integer plant, List<Computer> computerFilterList)
	{
		if(plant!=null)
		{
			for(Computer computer : this.computerList)
			{
				if(computer.getLocation().getPlant()==plant)
				{
					computerFilterList.add(computer);
				}
			}
		}
	}


	/**
	 * Method getByTrolley
	 * @param trolley
	 * @param computerFilterList
	 */
	private void getByTrolley(String trolley, List<Computer> computerFilterList)
	{
		if((trolley!=null) && !trolley.isBlank() && !trolley.isEmpty())
		{
			for(Computer computer : this.computerList)
			{
				if(computer.getLocation().getTrolley().equals(trolley))
				{
					computerFilterList.add(computer);
				}
			}
		}
	}


	/**
	 * Method getByClassroom
	 * @param classroom
	 * @param computerFilterList
	 */
	private void getByClassroom(String classroom, List<Computer> computerFilterList)
	{
		if((classroom!=null) && !classroom.isBlank() && !classroom.isEmpty())
		{
			for(Computer computer : this.computerList)
			{
				if(computer.getLocation().getClassroom().equals(classroom))
				{
					computerFilterList.add(computer);
				}
			}
		}
	}


	/**
	 * Method getByComputerNumber
	 * @param computerNumber
	 * @param computerFilterList
	 */
	private void getByComputerNumber(String computerNumber, List<Computer> computerFilterList)
	{
		if((computerNumber!=null) && !computerNumber.isBlank() && !computerNumber.isEmpty())
		{
			for(Computer computer : this.computerList)
			{
				if(computer.getComputerNumber().equals(computerNumber))
				{
					computerFilterList.add(computer);
				}
			}
		}
	}


	/**
	 * Method getByAndaluciaId
	 * @param andaluciaId
	 * @param computerFilterList
	 */
	private void getByAndaluciaId(String andaluciaId, List<Computer> computerFilterList)
	{
		if((andaluciaId!=null) && !andaluciaId.isBlank() && !andaluciaId.isEmpty())
		{
			for(Computer computer : this.computerList)
			{
				if(computer.getAndaluciaID().equals(andaluciaId))
				{
					computerFilterList.add(computer);
				}
			}
		}
	}


	/**
	 * Method getBySerialNumber
	 * @param serialNumber
	 * @param computerFilterList
	 */
	private void getBySerialNumber(String serialNumber, List<Computer> computerFilterList)
	{
		if((serialNumber!=null) && !serialNumber.isBlank() && !serialNumber.isEmpty())
		{
			for(Computer computer : this.computerList)
			{
				if(computer.getSerialNumber().equals(serialNumber))
				{
					computerFilterList.add(computer);
				}
			}
		}
	}


	/**
	 * Method getComputersScreens
	 * @param classroom
	 * @param trolley
	 * @param plant
	 * @param professor
	 * @return ResponseEntity
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/web/screenshot", produces = "application/zip")
	public ResponseEntity<?> getComputersScreens(
			@RequestHeader(required = false) String classroom,
			@RequestHeader(required = false) String trolley,
			@RequestHeader(required = false) Integer plant,
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

			// --- BY plant---
			finalZipCommand = this.getToZipCommandByPlant(plant, finalZipCommand);

			// --- BY professor---
			finalZipCommand = this.getToZipCommandByProfessor(professor, finalZipCommand);

			// --- BY ALL---
			finalZipCommand = this.getToZipCommandByNullAll(classroom, trolley, plant, professor, finalZipCommand);

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
	 * @param plant
	 * @param professor
	 * @param finalZipCommand
	 * @return
	 */
	private String getToZipCommandByNullAll(String classroom, String trolley, Integer plant, String professor,
			String finalZipCommand)
	{
		if((classroom==null) && (trolley == null) && (plant == null) && (professor == null) )
		{
			for(Computer computer : this.computerList)
			{

				finalZipCommand+=" webScreenshots/"+computer.getSerialNumber()+".png";

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
			for(Computer computer : this.computerList)
			{
				if(computer.getProfessor().equals(professor))
				{
					finalZipCommand+=" webScreenshots/"+computer.getSerialNumber()+".png";
				}
			}
		}
		return finalZipCommand;
	}


	/**
	 * Method getToZipCommandByPlant
	 * @param plant
	 * @param finalZipCommand
	 * @return
	 */
	private String getToZipCommandByPlant(Integer plant, String finalZipCommand)
	{
		if(plant!=null)
		{
			for(Computer computer : this.computerList)
			{
				if(computer.getLocation().getPlant()==plant)
				{
					finalZipCommand+=" webScreenshots/"+computer.getSerialNumber()+".png";
				}
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
			for(Computer computer : this.computerList)
			{
				if(computer.getLocation().getTrolley().equals(trolley))
				{
					finalZipCommand+=" webScreenshots/"+computer.getSerialNumber()+".png";
				}
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
			for(Computer computer : this.computerList)
			{
				if(computer.getLocation().getClassroom().equals(classroom))
				{
					finalZipCommand+=" webScreenshots/"+computer.getSerialNumber()+".png";
				}
			}
		}
		return finalZipCommand;
	}



}
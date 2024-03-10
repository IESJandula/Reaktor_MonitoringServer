package es.iesjandula.reaktor.monitoring_server.utils;

import java.io.File;
import java.util.List;
import java.util.Optional;

import es.iesjandula.reaktor.models.Motherboard;
import es.iesjandula.reaktor.monitoring_server.repository.IMotherboardRepository;
import lombok.NoArgsConstructor;

/**
 * @author Pablo Ruiz Canovas
 */
@NoArgsConstructor
public class WebUtils 
{

	/**
	 * Metodo que filtra un ordenador por profesor
	 * @param professor profesor que dirige el ordenador
	 * @param list lista de ordenadores
	 * @return lista de ordenadores actualizada
	 */
	public List<Motherboard> getByProfessor(String professor, List<Motherboard> list,IMotherboardRepository iMotherboardRepository)
	{
		if((professor!=null) && !professor.isBlank() && !professor.isEmpty())
		{
			list.addAll(iMotherboardRepository.findByTeacher(professor));
		}
		return list;
	}


	/**
	 * Metodo que filtra un ordenador por planta
	 * @param floor planta en la que se encuentra el ordenador
	 * @param list lista de ordenadores
	 * @return lista de ordenadores actualizada
	 */
	public List<Motherboard> getByFloor(Integer floor, List<Motherboard> list,IMotherboardRepository iMotherboardRepository)
	{
		if(floor!=null)
		{
			list.addAll(iMotherboardRepository.findByFloor(floor));
		}
		return list;
	}


	/**
	 * Metodo que filtra un ordenador por un carrito
	 * @param trolley carrito al que pertenece el ordenador
	 * @param list lista de ordenadores
	 * @return lista de ordenadores actualizada
	 */
	public List<Motherboard> getByTrolley(String trolley, List<Motherboard> list,IMotherboardRepository iMotherboardRepository)
	{
		if((trolley!=null) && !trolley.isBlank() && !trolley.isEmpty())
		{
			list.addAll(iMotherboardRepository.findByTrolley(trolley));
		}
		return list;
	}


	/**
	 * Metodo que filtra un ordenador por la clase
	 * @param classroom clase en la que se encuentra el ordenador
	 * @param list lista de ordenadores
	 * @return lista de ordenadores actualizada
	 */
	public List<Motherboard> getByClassroom(String classroom, List<Motherboard> list,IMotherboardRepository iMotherboardRepository)
	{
		if((classroom!=null) && !classroom.isBlank() && !classroom.isEmpty())
		{
			list.addAll(iMotherboardRepository.findByClassroom(classroom));
		}
		return list;
	}


	/**
	 * Metodo que filtra un ordenador por su pegatina identificativa
	 * @param computerNumber pegatina identificativa del ordenador
	 * @param list lista de ordenadores
	 * @return lista de ordenadores actualizada
	 */
	public List<Motherboard> getByComputerNumber(String computerNumber, List<Motherboard> list,IMotherboardRepository iMotherboardRepository)
	{
		if((computerNumber!=null) && !computerNumber.isBlank() && !computerNumber.isEmpty())
		{
			list.addAll(iMotherboardRepository.findByComputerNumber(computerNumber));
		}
		return list;
	}


	/**
	 * Metodo que filtra un ordenador por su numero de andalucia 
	 * @param andaluciaId numero de andalucia del ordenador
	 * @param list lista de ordenadores
	 * @return lista de ordenadores actualizada
	 */
	public List<Motherboard> getByAndaluciaId(String andaluciaId, List<Motherboard> list,IMotherboardRepository iMotherboardRepository)
	{
		if((andaluciaId!=null) && !andaluciaId.isBlank() && !andaluciaId.isEmpty())
		{
			list.addAll(iMotherboardRepository.findByAndaluciaId(andaluciaId));
		}
		return list;
	}


	/**
	 * Metodo que filtra un ordenador por su numero de serie
	 * @param serialNumber numero de serie del ordenador 
	 * @param list lista de ordenadores
	 * @return lista de ordenadores actualizada
	 */
	public List<Motherboard> getBySerialNumber(String serialNumber, List<Motherboard> list,IMotherboardRepository iMotherboardRepository)
	{
		if((serialNumber!=null) && !serialNumber.isBlank() && !serialNumber.isEmpty())
		{
			Optional<Motherboard> mOptional = iMotherboardRepository.findById(serialNumber);
			
			if (mOptional.isPresent())
			{
				list.add(mOptional.get());
			}
		}
		return list;
	}
	

	/**
	 * Metodo que completa el comando para zipear una carpeta para todos los ordenadores
	 * @param classroom clase para añadir al comando
	 * @param trolley carrito para añadir al comando
	 * @param floor planta para añadir el comando
	 * @param professor profesor para añadir el comando
	 * @param finalZipCommand comando para zipear la carpeta
	 * @return comando para ziperar la carpeta actualizado
	 */
	public String getToZipCommandByNullAll(String classroom, String trolley, Integer floor, String professor,
			String finalZipCommand,IMotherboardRepository iMotherboardRepository)
	{
		if((classroom==null) && (trolley == null) && (floor == null) && (professor == null) )
		{
			for(Motherboard motherboard : iMotherboardRepository.findAll())
			{
				finalZipCommand+= " " + Constants.REAKTOR_CONFIG_EXEC_WEB_SCREENSHOTS + File.separator + motherboard.getMotherBoardSerialNumber()+".png";
			}
		}
		return finalZipCommand;
	}


	/**
	 * Metodo que añade un profesor al comando para zipear la carpeta de capturas
	 * @param professor profesor para añadir al comando 
	 * @param finalZipCommand comando para zipear la carpeta
	 * @return comando para zipear la carpeta actualizado
	 */
	public String getToZipCommandByProfessor(String professor, String finalZipCommand,IMotherboardRepository iMotherboardRepository)
	{
		if((professor!=null) && !professor.isBlank() && !professor.isEmpty())
		{
			for(Motherboard motherboard : iMotherboardRepository.findByTeacher(professor))
			{
				finalZipCommand+= " " + Constants.REAKTOR_CONFIG_EXEC_WEB_SCREENSHOTS + File.separator + motherboard.getMotherBoardSerialNumber()+".png";
			}
		}
		return finalZipCommand;
	}


	/**
	 * Metodo que añade una planta al comando para zipear la carpeta de capturas
	 * @param floor planta para añadir al comando
	 * @param finalZipCommand comando para zipear la carpeta
	 * @return comando para zipear la carpeta actualizado
	 */
	public String getToZipCommandByfloor(Integer floor, String finalZipCommand,IMotherboardRepository iMotherboardRepository)
	{
		if(floor!=null)
		{
			for(Motherboard motherboard : iMotherboardRepository.findByFloor(floor))
			{
				finalZipCommand+= " " + Constants.REAKTOR_CONFIG_EXEC_WEB_SCREENSHOTS + File.separator + motherboard.getMotherBoardSerialNumber()+".png";
			}
		}
		return finalZipCommand;
	}


	/**
	 * Metodo que añade un carrito al comando para zipear la carpeta de capturas
	 * @param trolley carrito para añadir al comando
	 * @param finalZipCommand comando para zipear la carpeta
	 * @return comando para zipear la carpeta actualizado
	 */
	public String getToZipCommandByTrolley(String trolley, String finalZipCommand,IMotherboardRepository iMotherboardRepository)
	{
		if((trolley!=null) && !trolley.isBlank() && !trolley.isEmpty())
		{
			for(Motherboard motherboard : iMotherboardRepository.findByTrolley(trolley))
			{
				finalZipCommand+= " " + Constants.REAKTOR_CONFIG_EXEC_WEB_SCREENSHOTS + File.separator + motherboard.getMotherBoardSerialNumber()+".png";
			}
		}
		return finalZipCommand;
	}


	/**
	 * Metodo que añade una clase al comando para zipear la carpeta de capturas
	 * @param classroom clase para añadir al comando
	 * @param finalZipCommand comando para zipear la carpeta
	 * @return comando para zipear la carpeta actualizado
	 */
	public String getToZipCommandByClassroom(String classroom, String finalZipCommand,IMotherboardRepository iMotherboardRepository)
	{
		if((classroom!=null) && !classroom.isBlank() && !classroom.isEmpty())
		{
			for(Motherboard motherboard : iMotherboardRepository.findByClassroom(classroom))
			{
				finalZipCommand+= " " + Constants.REAKTOR_CONFIG_EXEC_WEB_SCREENSHOTS + File.separator + motherboard.getMotherBoardSerialNumber()+".png";
			}
		}
		return finalZipCommand;
	}

}

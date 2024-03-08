package es.iesjandula.reaktor.monitoring_server.utils;

import java.util.List;
import java.util.Optional;

import es.iesjandula.reaktor.models.Motherboard;
import es.iesjandula.reaktor.monitoring_server.repository.IMotherboardRepository;

/**
 * @author Pablo Ruiz Canovas
 */
public class WebUtils 
{
	/**	Repositorio que se encarga de realizar las operaciones CRUD sobre la entidad Motherboard */
	private IMotherboardRepository iMotherboardRepository;
	
	/**
	 * Constructor que instancia la clase usando el repositorio que realiza operaciones CRUD
	 * @param iMotherboardRepository
	 */
	public WebUtils(IMotherboardRepository iMotherboardRepository) 
	{
		this.iMotherboardRepository = iMotherboardRepository;
	}


	/**
	 * Metodo que filtra un ordenador por profesor
	 * @param professor profesor que dirige el ordenador
	 * @param list lista de ordenadores
	 * @return lista de ordenadores actualizada
	 */
	public List<Motherboard> getByProfessor(String professor, List<Motherboard> list)
	{
		if((professor!=null) && !professor.isBlank() && !professor.isEmpty())
		{
			list.addAll(this.iMotherboardRepository.findByTeacher(professor));
		}
		return list;
	}


	/**
	 * Metodo que filtra un ordenador por planta
	 * @param floor planta en la que se encuentra el ordenador
	 * @param list lista de ordenadores
	 * @return lista de ordenadores actualizada
	 */
	public List<Motherboard> getByFloor(Integer floor, List<Motherboard> list)
	{
		if(floor!=null)
		{
			list.addAll(this.iMotherboardRepository.findByFloor(floor));
		}
		return list;
	}


	/**
	 * Metodo que filtra un ordenador por un carrito
	 * @param trolley carrito al que pertenece el ordenador
	 * @param list lista de ordenadores
	 * @return lista de ordenadores actualizada
	 */
	public List<Motherboard> getByTrolley(String trolley, List<Motherboard> list)
	{
		if((trolley!=null) && !trolley.isBlank() && !trolley.isEmpty())
		{
			list.addAll(this.iMotherboardRepository.findByTrolley(trolley));
		}
		return list;
	}


	/**
	 * Metodo que filtra un ordenador por la clase
	 * @param classroom clase en la que se encuentra el ordenador
	 * @param list lista de ordenadores
	 * @return lista de ordenadores actualizada
	 */
	public List<Motherboard> getByClassroom(String classroom, List<Motherboard> list)
	{
		if((classroom!=null) && !classroom.isBlank() && !classroom.isEmpty())
		{
			list.addAll(this.iMotherboardRepository.findByClassroom(classroom));
		}
		return list;
	}


	/**
	 * Metodo que filtra un ordenador por su pegatina identificativa
	 * @param computerNumber pegatina identificativa del ordenador
	 * @param list lista de ordenadores
	 * @return lista de ordenadores actualizada
	 */
	public List<Motherboard> getByComputerNumber(String computerNumber, List<Motherboard> list)
	{
		if((computerNumber!=null) && !computerNumber.isBlank() && !computerNumber.isEmpty())
		{
			list.addAll(this.iMotherboardRepository.findByComputerNumber(computerNumber));
		}
		return list;
	}


	/**
	 * Metodo que filtra un ordenador por su numero de andalucia 
	 * @param andaluciaId numero de andalucia del ordenador
	 * @param list lista de ordenadores
	 * @return lista de ordenadores actualizada
	 */
	public List<Motherboard> getByAndaluciaId(String andaluciaId, List<Motherboard> list)
	{
		if((andaluciaId!=null) && !andaluciaId.isBlank() && !andaluciaId.isEmpty())
		{
			list.addAll(this.iMotherboardRepository.findByAndaluciaId(andaluciaId));
		}
		return list;
	}


	/**
	 * Metodo que filtra un ordenador por su numero de serie
	 * @param serialNumber numero de serie del ordenador 
	 * @param list lista de ordenadores
	 * @return lista de ordenadores actualizada
	 */
	public List<Motherboard> getBySerialNumber(String serialNumber, List<Motherboard> list)
	{
		if((serialNumber!=null) && !serialNumber.isBlank() && !serialNumber.isEmpty())
		{
			Optional<Motherboard> mOptional = this.iMotherboardRepository.findById(serialNumber);
			
			if (mOptional.isPresent())
			{
				list.add(mOptional.get());
			}
		}
		return list;
	}
}

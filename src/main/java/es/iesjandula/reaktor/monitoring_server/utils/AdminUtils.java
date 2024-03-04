package es.iesjandula.reaktor.monitoring_server.utils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import es.iesjandula.reaktor.models.Motherboard;
import es.iesjandula.reaktor.monitoring_server.repository.IMotherboardRepository;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AdminUtils 
{
	/**
	 * Method addByClassroom
	 * 
	 * @param classroom
	 * @param motherboardList
	 */
	public void addByClassroom(String classroom, Set<Motherboard> motherboardList, IMotherboardRepository iMotherboardRepository)
	{
		List<Motherboard> motherboardId = iMotherboardRepository.findByClassroom(classroom);
		motherboardList.addAll(motherboardId);
	}

	/**
	 * Method addByTrolley
	 * 
	 * @param trolley
	 * @param motherboardList
	 */
	public void addByTrolley(String trolley, Set<Motherboard> motherboardList,IMotherboardRepository iMotherboardRepository)
	{
		List<Motherboard> motherboardId = iMotherboardRepository.findByTrolley(trolley);
		motherboardList.addAll(motherboardId);
	}

	/**
	 * Method addByfloor
	 * 
	 * @param floor
	 * @param motherboardList
	 */
	public void addByFloor(int floor, Set<Motherboard> motherboardList,IMotherboardRepository iMotherboardRepository)
	{
		List<Motherboard> motherboardId = iMotherboardRepository.findByFloor(floor);
		motherboardList.addAll(motherboardId);
	}

	/**
	 * Method addBySerialNumber
	 * 
	 * @param serialNumber
	 * @param motherboardList
	 */
	public void addBySerialNumber(String serialNumber, Set<Motherboard> motherboardList,IMotherboardRepository iMotherboardRepository)
	{
		Optional<Motherboard> motherboardId = iMotherboardRepository.findById(serialNumber);
		if (motherboardId.isPresent())
		{
			motherboardList.add(motherboardId.get());
		}
	}

}

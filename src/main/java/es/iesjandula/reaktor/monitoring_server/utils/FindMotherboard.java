package es.iesjandula.reaktor.monitoring_server.utils;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import es.iesjandula.reaktor.models.Computer;
import es.iesjandula.reaktor.models.jpa.MotherBoard;
import es.iesjandula.reaktor.monitoring_server.repository.IMotherBoardJPARepository;

public class FindMotherboard 
{
	/**Lista de oredenadores */
	private List<Computer> computers;
	
	
	@Autowired
	private IMotherBoardJPARepository motherBoardRepo;
	
	/**
	 * Constructor que instancia la lista de ordenadores para la busqueda de placas base
	 * @param computers
	 */
	public FindMotherboard(List<Computer> computers)
	{
		this.computers = computers;
	}
	/**
	 * Metodo que busca una placa base por un serial number pasado por parametro
	 * @param serialNumber
	 * @return placa base encontrada por su serial number
	 */
	public MotherBoard findBySerialNumber(String serialNumber)
	{
		List<MotherBoard> motherBoards = this.motherBoardRepo.findAll();
		MotherBoard board = null;
		int index = 0;
		boolean getOut = false;
		
		while(!getOut)
		{
			//Obtenemos la placa
			board = motherBoards.get(index);
			if(board.getSerialNumber().equals(serialNumber))
			{
				getOut = true;
			}
			index++;
			//Si pasa el indice de la lista y no ha encontrado nada devolvemos null
			if(index>motherBoards.size())
			{
				getOut = true;
				board = null;
			}
		}
		
		return board;
		
	}
	/**
	 * Metodo que busca una o varias placas bases que se encuentran en una clase
	 * @param classroom
	 * @return 
	 */
	public List<MotherBoard> findByClassroom(String classroom)
	{
		List<MotherBoard> motherBoards = this.motherBoardRepo.findAll();
		List<MotherBoard> otherList = new LinkedList<MotherBoard>();
		
		for(Computer c:this.computers)
		{
			String serialNumber = c.getSerialNumber();
			for(MotherBoard m:motherBoards)
			{
				if(m.getSerialNumber().equals(serialNumber))
				{
					otherList.add(m);
				}
			}
		}
		
		return otherList;
	}
}

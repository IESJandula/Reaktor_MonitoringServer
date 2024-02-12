package es.iesjandula.reaktor.monitoring_server.utils;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import es.iesjandula.reaktor.models.jpa.Action;
import es.iesjandula.reaktor.models.jpa.MotherBoard;
import es.iesjandula.reaktor.models.jpa.Status;
import es.iesjandula.reaktor.models.jpa.Task;
import es.iesjandula.reaktor.models.jpa.TaskId;
import es.iesjandula.reaktor.monitoring_server.repository.ITaskRepository;

public class FillTask 
{
	@Autowired
	private ITaskRepository taskRepo;
	/**Lista de placas base */
	private List<MotherBoard> motherboards;
	/**Placa base */
	private MotherBoard board;
	
	public FillTask(MotherBoard board)
	{
		this.board = board;
	}
	
	public FillTask(List<MotherBoard> motherBoards)
	{
		this.motherboards = motherBoards;
	}
	
	public List<Task> doOnAll(Action action)
	{
		List<Task> tasks = new LinkedList<Task>();
		
		for(MotherBoard m:this.motherboards)
		{
			Date date = new Date();
			TaskId id = new TaskId(action.getName(),date,"",m.getSerialNumber());
			tasks.add(new Task(id,Status.TO_DO,m,action));
		}
		
		return tasks;
	}
	
	
}

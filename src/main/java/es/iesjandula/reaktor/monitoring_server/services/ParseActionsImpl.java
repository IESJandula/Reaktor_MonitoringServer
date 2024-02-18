package es.iesjandula.reaktor.monitoring_server.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.iesjandula.reaktor.exceptions.ComputerError;
import es.iesjandula.reaktor.models.Action;
import es.iesjandula.reaktor.monitoring_server.interfaces.IParseActions;
import es.iesjandula.reaktor.monitoring_server.repository.IActionRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ParseActionsImpl implements IParseActions
{
	@Autowired
	private IActionRepository actionRepository;
	
	@Override
	public void parseFile(String pathFile) throws ComputerError
	{
		
		Scanner scanner = null;
		
		try
		{
			scanner = new Scanner(new File(pathFile));
			scanner.nextLine();
			while (scanner.hasNext())
			{
				String[] string = scanner.nextLine().split(",");
				if (string.length >2)
				{
					this.actionRepository.saveAndFlush(new Action(string[0], string[1], string[2]));
				} else if (string.length >1) {
					this.actionRepository.saveAndFlush(new Action(string[0], string[1], ""));
				}else {
					this.actionRepository.saveAndFlush(new Action(string[0], "", ""));
				}
				
			}
			
		} 
		catch (FileNotFoundException exception)
		{
			log.error(exception.getMessage());
			throw new ComputerError(500, exception.getMessage(), exception);
		}
		
	}

}

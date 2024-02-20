package es.iesjandula.reaktor.monitoring_server.interfaces;

import org.springframework.stereotype.Repository;

import es.iesjandula.reaktor.exceptions.ComputerError;

@Repository
public interface IParseActions
{

	void parseFile(String pathFile) throws ComputerError;
	
}

package es.iesjandula.reaktor.monitoring_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.models.jpa.MotherBoard;

public interface IMotherBoardJPARepository extends JpaRepository<MotherBoard, String> 
{
	
}

package es.iesjandula.reaktor.monitoring_server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.models.Motherboard;

public interface IMotherboardRepository extends JpaRepository<Motherboard, String>
{
    Motherboard findByMotherBoardSerialNumber(String motherBoardSerialNumber);

    List<Motherboard> findByTrolley(String trolley);
    
    List<Motherboard> findByClassroom(String classroom);
    
    List<Motherboard> findByTeacher(String teacher);
    
    List<Motherboard> findByAndaluciaId(String andaluciaId);
    
    List<Motherboard> findByComputerNumber(String computerNumber);
}

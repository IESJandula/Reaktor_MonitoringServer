package es.iesjandula.reaktor.monitoring_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.models.Motherboard;

public interface IMotherboardRepository extends JpaRepository<Motherboard, String>
{
    Motherboard findByMotherBoardSerialNumber(String motherBoardSerialNumber);


}

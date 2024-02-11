package es.iesjandula.reaktor.monitoring_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.models.jpa.USB;

public interface IUSBRepository extends JpaRepository<USB, Long>
{

}

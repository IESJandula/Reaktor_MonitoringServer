package es.iesjandula.reaktor.monitoring_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.models.Ram;
import es.iesjandula.reaktor.models.Id.RamId;

public interface IRamRepository extends JpaRepository <Ram, RamId>
{
}

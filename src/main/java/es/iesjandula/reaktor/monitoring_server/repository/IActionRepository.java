package es.iesjandula.reaktor.monitoring_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.models.Action;

public interface IActionRepository extends JpaRepository<Action, String>
{

}

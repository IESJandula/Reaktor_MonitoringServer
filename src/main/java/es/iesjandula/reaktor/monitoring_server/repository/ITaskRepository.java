package es.iesjandula.reaktor.monitoring_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.models.Task;
import es.iesjandula.reaktor.models.Id.TaskId;

public interface ITaskRepository extends JpaRepository<Task, TaskId>
{

}

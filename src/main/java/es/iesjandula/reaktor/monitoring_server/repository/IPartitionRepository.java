package es.iesjandula.reaktor.monitoring_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.models.Partition;
import es.iesjandula.reaktor.models.Id.PartitionId;

public interface IPartitionRepository extends JpaRepository<Partition, PartitionId>
{
}

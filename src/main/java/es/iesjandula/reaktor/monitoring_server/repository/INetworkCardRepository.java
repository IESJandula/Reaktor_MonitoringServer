package es.iesjandula.reaktor.monitoring_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.models.NetworkCard;
import es.iesjandula.reaktor.models.Id.NetworkCardId;

public interface INetworkCardRepository extends JpaRepository<NetworkCard, NetworkCardId>
{
}

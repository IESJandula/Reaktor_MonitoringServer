package es.iesjandula.reaktor.monitoring_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.models.InternetConnectionNetworkCard;
import es.iesjandula.reaktor.models.Id.InternetConnectionNetworkCardId;

public interface IinternetConnectionNetworkCardRepository extends JpaRepository<InternetConnectionNetworkCard, InternetConnectionNetworkCardId>
{
}

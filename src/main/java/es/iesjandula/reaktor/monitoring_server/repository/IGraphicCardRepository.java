package es.iesjandula.reaktor.monitoring_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.models.GraphicCard;
import es.iesjandula.reaktor.models.Id.GraphicCardId;

public interface IGraphicCardRepository extends JpaRepository<GraphicCard, GraphicCardId>
{
}

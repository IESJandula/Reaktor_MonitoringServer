package es.iesjandula.reaktor.monitoring_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.iesjandula.reaktor.models.Usb;

public interface IUsbRepository extends JpaRepository<Usb, Long>
{

}
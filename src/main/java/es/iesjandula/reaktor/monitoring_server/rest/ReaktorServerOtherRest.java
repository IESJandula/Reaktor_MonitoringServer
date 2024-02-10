package es.iesjandula.reaktor.monitoring_server.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author David Martinez
 *
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/computers")
public class ReaktorServerOtherRest
{
	@RequestMapping(method = RequestMethod.GET, value = "/admin/commandLine")
	public ResponseEntity<?> sendInformation(@RequestHeader(required=false) String cookies )
	{
		return ResponseEntity.ok("Reaktor Server is running");
	}

}

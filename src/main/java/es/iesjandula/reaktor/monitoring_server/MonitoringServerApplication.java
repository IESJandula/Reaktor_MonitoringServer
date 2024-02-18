package es.iesjandula.reaktor.monitoring_server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import es.iesjandula.reaktor.monitoring_server.interfaces.IParseActions;
import jakarta.transaction.Transactional;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = "es.iesjandula.reaktor.models")
public class MonitoringServerApplication implements CommandLineRunner
{
	
	@Autowired
	private IParseActions iParseActions;
	
    public static void main(String[] args)
    {
        SpringApplication.run(MonitoringServerApplication.class, args);
    }

	@Override
	@Transactional
	public void run(String... args) throws Exception
	{

		this.iParseActions.parseFile(".\\src\\main\\resources\\actionsCSV.csv");
		
	}
}

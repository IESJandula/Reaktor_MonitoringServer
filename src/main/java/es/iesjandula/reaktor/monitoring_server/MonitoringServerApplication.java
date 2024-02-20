package es.iesjandula.reaktor.monitoring_server;

import java.io.File;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import es.iesjandula.reaktor.monitoring_server.interfaces.IParseActions;
import es.iesjandula.reaktor.monitoring_server.utils.Constants;
import es.iesjandula.reaktor.monitoring_server.utils.ReaktorMonitoringServerException;
import es.iesjandula.reaktor.monitoring_server.utils.resources_handler.ResourcesHandler;
import es.iesjandula.reaktor.monitoring_server.utils.resources_handler.ResourcesHandlerFile;
import es.iesjandula.reaktor.monitoring_server.utils.resources_handler.ResourcesHandlerJar;
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
		this.copyTemplatesFolderToDestinationFolder();
		this.iParseActions.parseFile(Constants.REAKTOR_CONFIG_EXEC_ACTIONS_CSV);
		
	}
	
	/**
	 * Este método se encarga de copiar toda la estructura de carpetas a un lugar común
	 * ya sea en el entorno de desarrollo o ejecutando JAR
	 * @throws ReaktorMonitoringServerException con una excepción
	 */
	public void copyTemplatesFolderToDestinationFolder() throws ReaktorMonitoringServerException
	{
		// Esta es la carpeta con las subcarpetas y configuraciones
	    ResourcesHandler reaktorConfigFolder = this.getResourcesHandler(Constants.REAKTOR_CONFIG);
	    if (reaktorConfigFolder != null)
	    {
	      // Nombre de la carpeta destino
	      File destinationFolder = new File(Constants.REAKTOR_CONFIG_EXEC);
	      
	      // Copiamos las plantillas (origen) al destino
	      reaktorConfigFolder.copyToDirectory(destinationFolder);
	    } 
	  }
	
	/**
	 * 
	 * @param resourceFilePath con la carpeta origen que tiene las plantillas
	 * @return el manejador que crea la estructura
	 */
	private ResourcesHandler getResourcesHandler(String resourceFilePath)
	{
		ResourcesHandler outcome = null;

		URL baseDirSubfolderUrl = Thread.currentThread().getContextClassLoader().getResource(resourceFilePath);
		if (baseDirSubfolderUrl != null)
		{
			if (baseDirSubfolderUrl.getProtocol().equalsIgnoreCase("file"))
			{
				outcome = new ResourcesHandlerFile(baseDirSubfolderUrl);
			}
			else
			{
				outcome = new ResourcesHandlerJar(baseDirSubfolderUrl);
			}
		}
		
		return outcome;
	}
}

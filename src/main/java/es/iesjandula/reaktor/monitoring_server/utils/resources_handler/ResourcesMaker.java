package es.iesjandula.reaktor.monitoring_server.utils.resources_handler;

import java.io.File;
import java.net.URL;

import es.iesjandula.reaktor.monitoring_server.utils.ReaktorMonitoringServerException;

public class ResourcesMaker
{
	public void checkStaticFilesToCopyThem() throws ReaktorMonitoringServerException
	{
	    ResourcesHandler reaktorConfigFolder = this.getResourcesHandler("reaktor_config");
	    if (reaktorConfigFolder != null)
	    {
	      // Get the current directory
	      File destinationFolder = new File("");
	      
	      // Copy to this directory
	      reaktorConfigFolder.copyToDirectory(destinationFolder);
	    } 
	  }
	
	

	
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

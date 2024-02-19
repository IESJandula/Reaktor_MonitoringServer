package es.iesjandula.reaktor.monitoring_server;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import es.iesjandula.reaktor.monitoring_server.interfaces.IParseActions;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = "es.iesjandula.reaktor.models")
@Slf4j
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
		this.checkServerStructure();
		this.iParseActions.parseFile(".\\src\\main\\resources\\actionsCSV.csv");
		
	}
    /**
     * Method checkClientStructure
     */
    public void checkServerStructure() 
    {
    	File fileFolder = new File("."+File.separator+"files");
    	log.info(fileFolder.getAbsolutePath()+"-> EXIST fileFolder:"+fileFolder.exists());
    	if(!fileFolder.exists()) 
    	{
    		fileFolder.mkdir();
    	}
    	
    	File wifiFolder = new File("."+File.separator+"confWIFI");
    	log.info(wifiFolder.getAbsolutePath()+"-> EXIST wifiFolder :"+wifiFolder.exists());
    	if(!wifiFolder.exists()) 
    	{
    		wifiFolder.mkdir();
    	}
    	
    	File screenshotsFolder = new File("."+File.separator+"screenshots");
    	log.info(screenshotsFolder.getAbsolutePath()+"-> EXIST screenshotsFolder :"+screenshotsFolder.exists());
    	if(!screenshotsFolder.exists()) 
    	{
    		screenshotsFolder.mkdir();
    	}
    	
    	File webScreenshotsFolder = new File("."+File.separator+"webScreenshots");
    	log.info(webScreenshotsFolder.getAbsolutePath()+"-> EXIST webScreenshotsFolder :"+webScreenshotsFolder.exists());
    	if(!webScreenshotsFolder.exists()) 
    	{
    		webScreenshotsFolder.mkdir();
    	}
    }
}

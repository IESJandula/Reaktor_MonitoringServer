package es.iesjandula.reaktor.monitoring_server.utils.resources_handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.iesjandula.reaktor.monitoring_server.utils.ReaktorMonitoringServerException;

/**
 * Resources Handler
 */
public abstract class ResourcesHandler
{
	/** Logger of the class */
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourcesHandler.class);

	/** Attribute - Resource Folder URL */
	private final URL resourceFolderUrl;

	/**
	 * @param resourceFolderUrl resource folder URL
	 */
	protected ResourcesHandler(URL resourceFolderUrl)
	{
		this.resourceFolderUrl = resourceFolderUrl;
	}

	/**
	 * @return resource folder URL
	 */
	public URL getResourceFolderUrl()
	{
		return this.resourceFolderUrl;
	}

	/**
	 * @param paramFile parameter file
	 * @throws ReaktorMonitoringServerException with an occurred exception
	 */
	public abstract void copyToDirectory(File paramFile) throws ReaktorMonitoringServerException;

	/**
	 * @param destDir destination directory
	 * @param subfolderFile subfolder file
	 */
	protected void createDirectoryIfNotExists(File destDir, String subfolderFile)
	{
		File directory = new File(destDir, subfolderFile);
		boolean directoryCreated = this.createDirectoryIfNotExists(directory);
		if (directoryCreated)
		{
			LOGGER.info("Directory created [{}]", subfolderFile);
		}
	}

	/**
	 * @param directory director
	 * @return true if the directory was created
	 */
	private boolean createDirectoryIfNotExists(File directory)
	{
		boolean outcome = !directory.exists();
		if (outcome)
		{
			outcome = directory.mkdirs();
		}
		return outcome;
	}

	protected String getFileSubPathWithoutExtraInfoFromFullPath(String subfolderFile)
	{
		int latestFileSeparator = subfolderFile.lastIndexOf(getSeparator());
		String subfolders = subfolderFile.substring(0, latestFileSeparator + 1);
		String fileName = subfolderFile.substring(latestFileSeparator + 1);
		
		LOGGER.info("Info about the file that it is checking: [subfolders: {}, fileName: {}]", subfolders, fileName);
		
		return getFinalFilePath(subfolders, fileName);
	}

	private String getFinalFilePath(String subfolders, String fileName)
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(subfolders);
		stringBuilder.append(fileName);
		
		return stringBuilder.toString();
	}

	protected abstract String getSeparator();

	protected void createFile(File destDir, InputStream inputStream, String filePathWithoutExtraInfo) throws ReaktorMonitoringServerException
	{
		this.createDirectoryIfNotExists(destDir);
		
		File file = new File(destDir, filePathWithoutExtraInfo);
		LOGGER.info("Created file [{}]", filePathWithoutExtraInfo);
		
		FileOutputStream fileOutputStream = null;
		try
		{
			fileOutputStream = new FileOutputStream(file);
			byte[] bytes = new byte[1024];
			int read = inputStream.read(bytes);
			while (read != -1)
			{
				fileOutputStream.write(bytes, 0, read);
				read = inputStream.read(bytes);
			}
		}
		catch (IOException ioException)
		{
			String errorString = "IOException while creating the file: " + filePathWithoutExtraInfo;
			
			LOGGER.error(errorString, ioException);
			throw new ReaktorMonitoringServerException(errorString, ioException);
		}
		finally
		{
			this.closeStream(fileOutputStream);
		}
	}

	private void closeStream(FileOutputStream fileOutputStream) throws ReaktorMonitoringServerException
	{
		if (fileOutputStream != null)
		{
			try
			{
				fileOutputStream.close();
			}
			catch (IOException ioException)
			{
				String errorString = "IOException while closing the file output stream";
				
				LOGGER.error(errorString, ioException);
				throw new ReaktorMonitoringServerException(errorString, ioException);
			}
		}
	}

	protected void closeStream(InputStream inputStream) throws ReaktorMonitoringServerException
	{
		if (inputStream != null)
		{
			try
			{
				inputStream.close();
			}
			catch (IOException ioException)
			{
				String errorString = "IOException while closing the input stream";
				
				LOGGER.error(errorString, ioException);
				throw new ReaktorMonitoringServerException(errorString, ioException);
			}
		}
	}
}

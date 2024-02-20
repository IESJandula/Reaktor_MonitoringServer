package es.iesjandula.reaktor.monitoring_server.utils;

/**
 * Reaktor Monitoring Server Exception
 */
public class ReaktorMonitoringServerException extends Exception
{
	/** Serial Version UID */
	private static final long serialVersionUID = 2730570172165453453L;

	/**
	 * @param errorString with the error string
	 * @param exception with the exception
	 */
	public ReaktorMonitoringServerException(String errorString, Exception exception)
	{
		super(errorString, exception);
	}
}

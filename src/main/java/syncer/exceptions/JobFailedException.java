package syncer.exceptions;

public class JobFailedException extends RuntimeException {
	
	private static final long serialVersionUID = 7593612811361987824L;

	public JobFailedException() {}

    public JobFailedException(String message)
    {
       super(message);
    }

}

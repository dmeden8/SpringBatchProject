package syncer.exceptions;

public class AfterRollbackJobFailingException extends Exception {

	private static final long serialVersionUID = 2610332103503515151L;

	public AfterRollbackJobFailingException(String message) {
        super(message);
    }
	
}

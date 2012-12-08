package zip;

public class ZipCheckException extends RuntimeException {

	private State state;

	public ZipCheckException() {
		super();
	}

	public ZipCheckException(String message, Throwable cause) {
		super(message, cause);
	}

	public ZipCheckException(String message) {
		super(message);
	}

	public ZipCheckException(String message, State state, Throwable cause) {
		super(message, cause);
		this.state = state;
	}

	public ZipCheckException(String message, State state) {
		super(message);
		this.state = state;
	}

	public ZipCheckException(Throwable cause) {
		super(cause);
	}

	public ZipCheckException(State state) {
		super();
		this.state = state;
	}

	public State getState() {
		return state;
	}

	@Override
	public String getMessage() {

		return super.getMessage() + "  " + state;
	}

}

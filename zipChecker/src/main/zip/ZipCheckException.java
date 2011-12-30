package zip;

public class ZipCheckException extends RuntimeException {

	private State state;

	public ZipCheckException() {
		super();
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public ZipCheckException(String message, Throwable cause) {
		super(message, cause);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public ZipCheckException(String message) {
		super(message);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public ZipCheckException(String message, State state, Throwable cause) {
		super(message, cause);
		this.state = state;
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public ZipCheckException(String message, State state) {
		super(message);
		this.state = state;
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public ZipCheckException(Throwable cause) {
		super(cause);
		// TODO 自動生成されたコンストラクター・スタブ
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

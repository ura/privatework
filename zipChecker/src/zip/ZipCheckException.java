package zip;


public class ZipCheckException extends RuntimeException {

	private State state;

	public ZipCheckException() {
		super();
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
	}

	public ZipCheckException(String message, Throwable cause) {
		super(message, cause);
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
	}

	public ZipCheckException(String message) {
		super(message);
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
	}

	public ZipCheckException(String message, State state,Throwable cause) {
		super(message, cause);
		this.state = state;
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
	}

	public ZipCheckException(String message,State state) {
		super(message);
		this.state = state;
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
	}


	public ZipCheckException(Throwable cause) {
		super(cause);
		// TODO �����������ꂽ�R���X�g���N�^�[�E�X�^�u
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

		return super.getMessage()+"  "+state;
	}



}

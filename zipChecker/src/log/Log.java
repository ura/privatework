package log;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Log {

	/**
	 * ����������}�[�J�B���ۂ̃t�@�C���ړ��A�폜�̂Ƃ��Ɏg�p����B
	 */
	public static final Marker OP = MarkerFactory.getMarker("OP");

	/**
	 * �W�v�p�̃}�[�J
	 */
	public static final Marker SUMMARY = MarkerFactory.getMarker("SUMMARY");

	/**
	 * ����̖������}�[�J
	 */
	public static final Marker INFO = MarkerFactory.getMarker("INFO");

}

package log;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Log {

	/**
	 * 操作を示すマーカ。実際のファイル移動、削除のときに使用する。
	 */
	public static final Marker OP = MarkerFactory.getMarker("OP");

	/**
	 * 集計用のマーカ
	 */
	public static final Marker SUMMARY = MarkerFactory.getMarker("SUMMARY");

	/**
	 * 操作の無い情報マーカ
	 */
	public static final Marker INFO = MarkerFactory.getMarker("INFO");

	/**
	 * 操作の無い情報マーカ
	 */
	public static final Marker STATIC = MarkerFactory.getMarker("STATIC");

}

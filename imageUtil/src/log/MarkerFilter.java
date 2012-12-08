package log;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;

public class MarkerFilter extends AbstractMatcherFilter {

	@Override
	public FilterReply decide(Object eventObject) {

		if (!isStarted())
			return FilterReply.NEUTRAL;
		LoggingEvent event = (LoggingEvent) eventObject;

		if (event.getMarker() != null && event.getMarker().contains(marker)) {
			return onMatch;
		} else {
			return onMismatch;
		}
	}

	public String marker;

	public void setMarker(String marker) {
		this.marker = marker;
	}

}

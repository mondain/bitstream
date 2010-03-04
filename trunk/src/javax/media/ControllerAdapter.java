package javax.media;

import javax.media.format.FormatChangeEvent;

/**
 * 
 * @author Ken Larson
 * 
 */
public class ControllerAdapter implements ControllerListener,
		java.util.EventListener {

	public ControllerAdapter() {
	}

	public void cachingControl(CachingControlEvent e) {
	}

	public void controllerError(ControllerErrorEvent e) {
	}

	public void dataLostError(DataLostErrorEvent e) {
	}

	public void dataStarved(DataStarvedEvent e) {
	}

	public void internalError(InternalErrorEvent e) {
	}

	public void resourceUnavailable(ResourceUnavailableEvent e) {
	}

	public void durationUpdate(DurationUpdateEvent e) {
	}

	public void mediaTimeSet(MediaTimeSetEvent e) {
	}

	public void rateChange(RateChangeEvent e) {
	}

	public void stopTimeChange(StopTimeChangeEvent e) {
	}

	public void transition(TransitionEvent e) {
	}

	public void prefetchComplete(PrefetchCompleteEvent e) {
	}

	public void realizeComplete(RealizeCompleteEvent e) {
	}

	public void start(StartEvent e) {
	}

	public void stop(StopEvent e) {
	}

	public void deallocate(DeallocateEvent e) {
	}

	public void endOfMedia(EndOfMediaEvent e) {
	}

	public void restarting(RestartingEvent e) {
	}

	public void stopAtTime(StopAtTimeEvent e) {
	}

	public void stopByRequest(StopByRequestEvent e) {
	}

	public void audioDeviceUnavailable(AudioDeviceUnavailableEvent e) {
	}

	public void configureComplete(ConfigureCompleteEvent e) {
	}

	public void controllerClosed(ControllerClosedEvent e) {
	}

	public void sizeChange(SizeChangeEvent e) {
	}

	public void connectionError(ConnectionErrorEvent e) {
	}

	public void formatChange(FormatChangeEvent e) {
	}

	public void replaceURL(Object e) {
		// comments from a subclass:
		// Replaces the URL that is invoked when a hot link to a new URL is
		// triggered.
		throw new UnsupportedOperationException(); // TODO
	}

	public void showDocument(Object e) {
		// comments from a subclass:
		// Displays the html document which is invoked when a hotlink to a html
		// is triggered in a mvr file
		// see? http://comers.citadel.edu/tutorials/hmedia/hmanimation.html for
		// MVR info
		throw new UnsupportedOperationException(); // TODO
	}

	public void controllerUpdate(ControllerEvent e) {
		if (e instanceof AudioDeviceUnavailableEvent) {
			audioDeviceUnavailable((AudioDeviceUnavailableEvent) e);
		} else if (e instanceof CachingControlEvent) {
			cachingControl((CachingControlEvent) e);
		} else if (e instanceof ControllerClosedEvent) {
			controllerClosed((ControllerClosedEvent) e);

			if (e instanceof ControllerErrorEvent) {
				controllerError((ControllerErrorEvent) e);

				if (e instanceof ConnectionErrorEvent) {
					connectionError((ConnectionErrorEvent) e);
				}
				if (e instanceof InternalErrorEvent) {
					internalError((InternalErrorEvent) e);
				}
				if (e instanceof ResourceUnavailableEvent) {
					resourceUnavailable((ResourceUnavailableEvent) e);
				}
			} else if (e instanceof DataLostErrorEvent) {
				dataLostError((DataLostErrorEvent) e);
			}
		} else if (e instanceof DurationUpdateEvent) {
			durationUpdate((DurationUpdateEvent) e);
		} else if (e instanceof FormatChangeEvent) {
			formatChange((FormatChangeEvent) e);

			if (e instanceof SizeChangeEvent) {
				sizeChange((SizeChangeEvent) e);
			}
		} else if (e instanceof MediaTimeSetEvent) {
			mediaTimeSet((MediaTimeSetEvent) e);
		} else if (e instanceof RateChangeEvent) {
			rateChange((RateChangeEvent) e);
		} else if (e instanceof StopTimeChangeEvent) {
			stopTimeChange((StopTimeChangeEvent) e);
		} else if (e instanceof TransitionEvent) {
			transition((TransitionEvent) e);

			if (e instanceof ConfigureCompleteEvent) {
				configureComplete((ConfigureCompleteEvent) e);
			} else if (e instanceof PrefetchCompleteEvent) {
				prefetchComplete((PrefetchCompleteEvent) e);
			} else if (e instanceof RealizeCompleteEvent) {
				realizeComplete((RealizeCompleteEvent) e);
			} else if (e instanceof StartEvent) {
				start((StartEvent) e);
			} else if (e instanceof StopEvent) {
				stop((StopEvent) e);

				if (e instanceof DataStarvedEvent) {
					dataStarved((DataStarvedEvent) e);
				} else if (e instanceof DeallocateEvent) {
					deallocate((DeallocateEvent) e);
				} else if (e instanceof EndOfMediaEvent) {
					endOfMedia((EndOfMediaEvent) e);
				} else if (e instanceof RestartingEvent) {
					restarting((RestartingEvent) e);
				} else if (e instanceof StopAtTimeEvent) {
					stopAtTime((StopAtTimeEvent) e);
				} else if (e instanceof StopByRequestEvent) {
					stopByRequest((StopByRequestEvent) e);
				}

			}
		}

	}
}

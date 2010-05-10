package javax.media;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.protocol.DataSource;
import javax.media.protocol.SourceCloneable;
import javax.media.protocol.URLDataSource;

import net.sf.fmj.utility.LoggerSingleton;

/**
 * 
 * @author Ken Larson
 * 
 */
public final class Manager {
	public static final String FMJ_TAG = "FMJ"; // Do not remove - this is used
												// by ClasspathChecker to
												// distinguish this version from
												// JMF's.

	private static final Logger logger = LoggerSingleton.logger;

	public static final int MAX_SECURITY = 1;

	public static final int CACHING = 2;

	public static final int LIGHTWEIGHT_RENDERER = 3;

	public static final int PLUGIN_PLAYER = 4;

	public static final String UNKNOWN_CONTENT_NAME = "unknown";

	private static TimeBase systemTimeBase = new SystemTimeBase();

	private static final Map hints = new HashMap();
	static {
		hints.put(new Integer(MAX_SECURITY), Boolean.FALSE);
		hints.put(new Integer(CACHING), Boolean.TRUE);
		hints.put(new Integer(LIGHTWEIGHT_RENDERER), Boolean.FALSE);
		hints.put(new Integer(PLUGIN_PLAYER), Boolean.FALSE);

	}

	// one thing that is (was) silly about this connect and retry loop, is that
	// in the case of something like RTP, if
	// there is a timeout connecting to the source, it will then go on and try
	// others. This would be in
	// the case of an IOException thrown by dataSource.connect, for example.
	// What makes this strange is that the end result is a
	// "No player exception", when it should really rethrow
	// the IOException. Let's try this.
	public static final boolean RETHROW_IO_EXCEPTIONS = true;

	public static String getVersion() {
		// try to load fmj.build.properties as a resource, look for build=.
		// this will succeed only for release builds, not when running from
		// CVS. This allows us to see what build a user is running.
		try {
			final Properties p = new Properties();
			p.load(Manager.class.getResourceAsStream("/"
					+ "fmj.build.properties"));
			final String s = p.getProperty("build");
			if (s != null && !s.equals(""))
				return "FMJ " + s.trim();
		} catch (Exception e) { // ignore, fall through...
		}

		return "FMJ non-release x.x"; // this is just a generic reponse when
										// running from CVS or any other source.

		// return "2.1.1e"; // for compatibility reasons. Eventually, we should
		// // perhaps have our own version numbering.
	}

	public static Player createPlayer(java.net.URL sourceURL)
			throws java.io.IOException, NoPlayerException {
		return createPlayer(new MediaLocator(sourceURL), new RandomAccessFile(sourceURL.toString(),"r"));
	}

	public static Player createPlayer(MediaLocator sourceLocator, RandomAccessFile raf)
			throws java.io.IOException, NoPlayerException {

		final String protocol = sourceLocator.getProtocol();
		final Vector dataSourceList = getDataSourceList(protocol);
		for (int i = 0; i < dataSourceList.size(); ++i) {
			String dataSourceClassName = (String) dataSourceList.get(i);
			try {
				final Class dataSourceClass = Class
						.forName(dataSourceClassName);
				/*
				 * final DataSource dataSource = (DataSource) dataSourceClass.newInstance();
				 * 
				 * send the raf to the datasource
				 */
				System.out.println("here4");
				if(raf == null) {
					final DataSource dataSource = (DataSource) dataSourceClass.newInstance();
					dataSource.setLocator(sourceLocator);
					dataSource.connect();
					return createPlayer(dataSource);
				}
				else {
					final net.sf.fmj.media.protocol.file.DataSource dataSource = new net.sf.fmj.media.protocol.file.DataSource();
					dataSource.setLocator(sourceLocator);
					dataSource.connect(raf);
					return createPlayer(dataSource);
				}
				//
				
				

				// TODO: JMF seems to disconnect data sources in this method,
				// based on this stack trace:
				// java.lang.NullPointerException
				// at
				// com.sun.media.protocol.rtp.DataSource.disconnect(DataSource.java:207)
				// at javax.media.Manager.createPlayer(Manager.java:425)
				// at
				// net.sf.fmj.ui.application.ContainerPlayer.createNewPlayer(ContainerPlayer.java:357)
			} catch (NoPlayerException e) { // no need to log, will be logged by
											// call to createPlayer.
				continue;
			} catch (ClassNotFoundException e) {
				logger.finer("createPlayer: " + e); // no need for call stack
				continue;
			} catch (IOException e) {
				logger.log(Level.FINE, "" + e, e);
				if (RETHROW_IO_EXCEPTIONS)
					throw e;
				else
					continue;
			} catch (NoClassDefFoundError e) {
				logger.log(Level.FINE, "" + e, e);
				continue;
			} catch (Exception e) {
				logger.log(Level.FINE, "" + e, e);
				continue;
			}

		}

		// if none found, try URLDataSource:
		final URL url;
		try {
			url = sourceLocator.getURL();
		} catch (Exception e) {
			logger.log(Level.WARNING, "" + e, e);
			throw new NoPlayerException();
		}
		final URLDataSource dataSource = new URLDataSource(url);
		dataSource.connect(); // TODO: there is a problem because we connect to
								// the datasource here, but
		// the following call may try twice or more to use the datasource with a
		// player, once
		// for the right content type, and multiple times for unknown. The first
		// attempt (for example) may actually
		// read data, in which case the second one will be missing data when it
		// reads.
		// really, the datasource needs to be recreated.
		// The workaround for now is that URLDataSource (and others) allows
		// repeated connect() calls.
		return createPlayer(dataSource);

	}

	private static Player createPlayer(DataSource source, String contentType)
			throws java.io.IOException, NoPlayerException {
		final Vector handlerClassList = getHandlerClassList(contentType);
		for (int i = 0; i < handlerClassList.size(); ++i) {
			final String handlerClassName = (String) handlerClassList.get(i);

			try {
				final Class handlerClass = Class.forName(handlerClassName);
				if (!Player.class.isAssignableFrom(handlerClass)
						&& !MediaProxy.class.isAssignableFrom(handlerClass))
					continue; // skip any classes that will not be matched
								// below.
				/*
				 * made changes here
				 */
				final MediaHandler handler = (MediaHandler) handlerClass.newInstance();
				handler.setSource(source);
				if (handler instanceof Player) {
					return (Player) handler;
				} else if (handler instanceof MediaProxy) {
					final MediaProxy mediaProxy = (MediaProxy) handler;
					return createPlayer(mediaProxy.getDataSource());
				}
			} catch (ClassNotFoundException e) {
				logger.finer("createPlayer: " + e); // no need for call stack
				continue;
			} catch (IncompatibleSourceException e) {
				logger.finer("createPlayer(" + source + ", " + contentType
						+ "): " + e); // no need for call stack
				continue;
			} catch (IOException e) {
				logger.log(Level.FINE, "" + e, e);
				if (RETHROW_IO_EXCEPTIONS)
					throw e;
				else
					continue;
			} catch (NoPlayerException e) { // no need to log, will be logged by
											// call to createPlayer.
				continue;
			} catch (NoClassDefFoundError e) {
				logger.log(Level.FINE, "" + e, e);
				continue;
			} catch (Exception e) {
				logger.log(Level.FINE, "" + e, e);
				continue;
			}
		}
		throw new NoPlayerException("No player found for "
				+ source.getLocator());
	}

	public static Player createPlayer(DataSource source)
			throws java.io.IOException, NoPlayerException {

		try {
			return createPlayer(source, source.getContentType());
		} catch (NoPlayerException e) { // no need to log, will be logged by
										// call to createProcessor.
		} catch (IOException e) {
			logger.log(Level.FINE, "" + e, e);
			if (RETHROW_IO_EXCEPTIONS)
				throw e;
		} catch (Exception e) {
			logger.log(Level.FINER, "" + e, e);

		}
		// TODO: this is dangerous to re-use the same source for another player.
		// this may actually cause it to re-use this source multiple times.
		System.out.println("=============================> sending unknown contentType");
		return createPlayer(source, "unknown");

	}

	public static Player createRealizedPlayer(java.net.URL sourceURL)
			throws java.io.IOException, NoPlayerException,
			CannotRealizeException {
		final Player player = createPlayer(sourceURL);
		blockingRealize(player);
		return player;
	}

	public static Player createRealizedPlayer(MediaLocator ml)
			throws java.io.IOException, NoPlayerException,
			CannotRealizeException {
		final Player player = createPlayer(ml, new RandomAccessFile(ml.toExternalForm(), "r"));
		blockingRealize(player);
		return player;
	}

	public static Player createRealizedPlayer(DataSource source)
			throws java.io.IOException, NoPlayerException,
			CannotRealizeException {
		final Player player = createPlayer(source);
		blockingRealize(player);
		return player;
	}

	public static Processor createProcessor(java.net.URL sourceURL)
			throws java.io.IOException, NoProcessorException {
		return createProcessor(new MediaLocator(sourceURL));
	}

	public static Processor createProcessor(MediaLocator sourceLocator)
			throws java.io.IOException, NoProcessorException {
		final String protocol = sourceLocator.getProtocol();
		final Vector dataSourceList = getDataSourceList(protocol);
		for (int i = 0; i < dataSourceList.size(); ++i) {
			String dataSourceClassName = (String) dataSourceList.get(i);
			try {
				final Class dataSourceClass = Class
						.forName(dataSourceClassName);
				final DataSource dataSource = (DataSource) dataSourceClass
						.newInstance();
				dataSource.setLocator(sourceLocator);
				dataSource.connect();
				return createProcessor(dataSource);

			} catch (ClassNotFoundException e) {
				logger.finer("createProcessor: " + e); // no need for call stack
				continue;
			} catch (IOException e) {
				logger.log(Level.FINE, "" + e, e);
				if (RETHROW_IO_EXCEPTIONS)
					throw e;
				else
					continue;
			} catch (NoProcessorException e) {
				continue; // no need to log, will be logged by call to
							// createProcessor.
			} catch (NoClassDefFoundError e) {
				logger.log(Level.FINE, "" + e, e);
				continue;
			} catch (Exception e) {
				logger.log(Level.FINE, "" + e, e);
				continue;
			}

		}

		// if none found, try URLDataSource:
		final URL url;
		try {
			url = sourceLocator.getURL();
		} catch (Exception e) {
			logger.log(Level.WARNING, "" + e, e);
			throw new NoProcessorException();
		}
		final URLDataSource dataSource = new URLDataSource(url);
		dataSource.connect();
		return createProcessor(dataSource);
	}

	private static Processor createProcessor(DataSource source,
			String contentType) throws java.io.IOException,
			NoProcessorException {
		final Vector handlerClassList = getProcessorClassList(contentType);
		for (int i = 0; i < handlerClassList.size(); ++i) {
			final String handlerClassName = (String) handlerClassList.get(i);

			try {
				final Class handlerClass = Class.forName(handlerClassName);
				if (!Processor.class.isAssignableFrom(handlerClass)
						&& !MediaProxy.class.isAssignableFrom(handlerClass))
					continue; // skip any classes that will not be matched
								// below.
				final MediaHandler handler = (MediaHandler) handlerClass
						.newInstance();
				handler.setSource(source);
				if (handler instanceof Processor) {
					return (Processor) handler;
				} else if (handler instanceof MediaProxy) {
					final MediaProxy mediaProxy = (MediaProxy) handler;
					return createProcessor(mediaProxy.getDataSource());
				}
			} catch (ClassNotFoundException e) {
				logger.finer("createProcessor: " + e); // no need for call stack
				continue;
			} catch (IncompatibleSourceException e) {
				logger.finer("createProcessor(" + source + ", " + contentType
						+ "): " + e); // no need for call stack
				continue;
			} catch (NoProcessorException e) {
				continue; // no need to log, will be logged by call to
							// createProcessor.
			} catch (IOException e) {
				logger.log(Level.FINE, "" + e, e);
				if (RETHROW_IO_EXCEPTIONS)
					throw e;
				else
					continue;
			} catch (NoClassDefFoundError e) {
				logger.log(Level.FINE, "" + e, e);
				continue;
			} catch (Exception e) {
				logger.log(Level.FINE, "" + e, e);
				continue;
			}
		}
		throw new NoProcessorException();
	}

	public static Processor createProcessor(DataSource source)
			throws java.io.IOException, NoProcessorException {
		try {
			return createProcessor(source, source.getContentType());
		} catch (IOException e) {
			logger.log(Level.FINE, "" + e, e);
			if (RETHROW_IO_EXCEPTIONS)
				throw e;

		} catch (NoProcessorException e) { // no need to log, will be logged by
											// call to createProcessor.
		} catch (Exception e) {
			logger.log(Level.FINE, "" + e, e);
		}
		return createProcessor(source, "unknown");

	}

	public static Processor createRealizedProcessor(ProcessorModel model)
			throws java.io.IOException, NoProcessorException,
			CannotRealizeException {
		final Processor processor;
		if (model.getInputDataSource() != null)
			processor = createProcessor(model.getInputDataSource());
		else
			processor = createProcessor(model.getInputLocator());

		processor.setContentDescriptor(model.getContentDescriptor()); // TODO:
																		// return
																		// value?
		// TODO: what to do with model.getFormats

		// TODO: configure?

		blockingRealize(processor);

		return processor;
	}

	public static DataSource createDataSource(java.net.URL sourceURL)
			throws java.io.IOException, NoDataSourceException {
		return createDataSource(new MediaLocator(sourceURL));
	}

	// this method has a fundamental flaw (carried over from JMF): the
	// DataSource may not be
	// accepted by the Handler. So createPlayer(createDataSource(MediaLocator))
	// is not equivalent to
	// createPlayer(MediaLocator)
	public static DataSource createDataSource(MediaLocator sourceLocator)
			throws java.io.IOException, NoDataSourceException {
		final String protocol = sourceLocator.getProtocol();
		final Vector dataSourceList = getDataSourceList(protocol);
		for (int i = 0; i < dataSourceList.size(); ++i) {
			String dataSourceClassName = (String) dataSourceList.get(i);
			try {
				final Class dataSourceClass = Class
						.forName(dataSourceClassName);
				final DataSource dataSource = (DataSource) dataSourceClass
						.newInstance();
				dataSource.setLocator(sourceLocator);
				dataSource.connect();
				return dataSource;

			} catch (ClassNotFoundException e) {
				logger.finer("createDataSource: " + e); // no need for call
														// stack
				continue;
			} catch (IOException e) {
				logger.log(Level.FINE, "" + e, e);
				if (RETHROW_IO_EXCEPTIONS)
					throw e;
				else
					continue;
			} catch (NoClassDefFoundError e) {
				logger.log(Level.FINE, "" + e, e);
				continue;
			} catch (Exception e) {
				logger.log(Level.FINE, "" + e, e);
				continue;
			}

		}

		// if none found, try URLDataSource:
		final URL url;
		try {
			url = sourceLocator.getURL();
		} catch (Exception e) {
			logger.log(Level.WARNING, "" + e, e);
			throw new NoDataSourceException();
		}
		final URLDataSource dataSource = new URLDataSource(url);
		dataSource.connect();
		return dataSource;
	}

	public static DataSource createMergingDataSource(DataSource[] sources)
			throws IncompatibleSourceException {
		// JMF does not return source[0] if sources.length == 1.
		throw new UnsupportedOperationException(); // TODO
	}

	public static DataSource createCloneableDataSource(DataSource source) {
		if (source instanceof SourceCloneable)
			return source;
		throw new UnsupportedOperationException(); // TODO
	}

	public static TimeBase getSystemTimeBase() {
		return systemTimeBase;
	}

	// does not handle proxies.
	private static DataSink createDataSink(DataSource datasource,
			String protocol) throws NoDataSinkException {
		final Vector handlerClassList = getDataSinkClassList(protocol);

		for (int i = 0; i < handlerClassList.size(); ++i) {
			final String handlerClassName = (String) handlerClassList.get(i);

			try {
				final Class handlerClass = Class.forName(handlerClassName);
				if (!DataSink.class.isAssignableFrom(handlerClass))
					continue; // skip any classes that will not be matched
								// below.

				final MediaHandler handler = (MediaHandler) handlerClass
						.newInstance();
				handler.setSource(datasource);
				if (handler instanceof DataSink) {
					return (DataSink) handler;
				}

			} catch (ClassNotFoundException e) {
				logger.finer("createDataSink: " + e); // no need for call stack
				continue;
			} catch (IncompatibleSourceException e) {
				logger.finer("createDataSink(" + datasource + ", " + protocol
						+ "): " + e); // no need for call stack
				continue;
			} catch (NoClassDefFoundError e) {
				logger.log(Level.FINE, "" + e, e);
				continue;
			} catch (Exception e) {
				logger.log(Level.FINE, "" + e, e);
				continue;
			}
		}
		throw new NoDataSinkException();
	}

	public static DataSink createDataSink(DataSource datasource,
			MediaLocator destLocator) throws NoDataSinkException {
		final String protocol = destLocator.getProtocol();

		final Vector handlerClassList = getDataSinkClassList(protocol);

		for (int i = 0; i < handlerClassList.size(); ++i) {
			final String handlerClassName = (String) handlerClassList.get(i);

			try {
				final Class handlerClass = Class.forName(handlerClassName);
				if (!DataSink.class.isAssignableFrom(handlerClass)
						&& !DataSinkProxy.class.isAssignableFrom(handlerClass))
					continue; // skip any classes that will not be matched
								// below.

				final MediaHandler handler = (MediaHandler) handlerClass
						.newInstance();
				handler.setSource(datasource);
				if (handler instanceof DataSink) {
					DataSink dataSink = (DataSink) handler;
					dataSink.setOutputLocator(destLocator);
					return dataSink;
				} else if (handler instanceof DataSinkProxy) {
					// If the MediaHandler is a DataSinkProxy, obtain the
					// content type of the proxy using the getContentType()
					// method.
					// Now obtain a list of MediaHandlers that support the
					// protocol of the Medialocator and the content type
					// returned by the proxy
					// i.e. look for
					// content-prefix.media.datasink.protocol.content-type.Handler
					final DataSinkProxy mediaProxy = (DataSinkProxy) handler;

					final Vector handlerClassList2 = getDataSinkClassList(protocol
							+ "."
							+ toPackageFriendly(mediaProxy
									.getContentType(destLocator)));

					for (int j = 0; j < handlerClassList2.size(); ++j) {
						final String handlerClassName2 = (String) handlerClassList2
								.get(j);

						try {
							final Class handlerClass2 = Class
									.forName(handlerClassName2);
							if (!DataSink.class.isAssignableFrom(handlerClass2))
								continue; // skip any classes that will not be
											// matched below.
							final MediaHandler handler2 = (MediaHandler) handlerClass2
									.newInstance();
							handler2.setSource(mediaProxy.getDataSource());
							if (handler2 instanceof DataSink) {
								DataSink dataSink = (DataSink) handler2;
								dataSink.setOutputLocator(destLocator);
								return (DataSink) handler2;
							}

						} catch (ClassNotFoundException e) {
							logger.finer("createDataSink: " + e); // no need for
																	// call
																	// stack
							continue;
						} catch (IncompatibleSourceException e) {
							logger.finer("createDataSink(" + datasource + ", "
									+ destLocator + "), proxy="
									+ mediaProxy.getDataSource() + ": " + e); // no
																				// need
																				// for
																				// call
																				// stack
							continue;
						} catch (NoClassDefFoundError e) {
							logger.log(Level.FINE, "" + e, e);
							continue;
						} catch (Exception e) {
							logger.log(Level.FINE, "" + e, e);
							continue;
						}
					}

				}
			} catch (ClassNotFoundException e) {
				logger.finer("createDataSink: " + e); // no need for call stack
				continue;
			} catch (IncompatibleSourceException e) {
				logger.finer("createDataSink(" + datasource + ", "
						+ destLocator + "): " + e); // no need for call stack
				continue;
			} catch (NoClassDefFoundError e) {
				logger.log(Level.FINE, "" + e, e);
				continue;
			} catch (Exception e) {
				logger.log(Level.FINE, "" + e, e);
				continue;
			}
		}

		throw new NoDataSinkException();

	}

	private static void blockingRealize(Controller controller)
			throws CannotRealizeException {
		try {
			new BlockingRealizer(controller).realize();
		} catch (InterruptedException e) {
			throw new CannotRealizeException("Interrupted");
		}
	}

	private static class BlockingRealizer implements ControllerListener {
		private final Controller controller;

		private volatile boolean realized = false;
		private volatile boolean busy = true;
		private volatile String cannotRealizeExceptionMessage;

		public void realize() throws CannotRealizeException,
				InterruptedException {
			controller.addControllerListener(this);
			controller.realize();
			while (!busy) {
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {
					controller.removeControllerListener(this);
					throw e;
				}
			}
			controller.removeControllerListener(this);

			if (!realized)
				throw new CannotRealizeException(cannotRealizeExceptionMessage);
		}

		public synchronized void controllerUpdate(ControllerEvent event) {
			if (event instanceof RealizeCompleteEvent) {
				realized = true;
				busy = false;
				notify();
			} else if (event instanceof StopEvent
					|| event instanceof ControllerClosedEvent) {
				if (event instanceof StopEvent) {
					cannotRealizeExceptionMessage = "Cannot realize: received StopEvent: "
							+ event;
					logger.info(cannotRealizeExceptionMessage);
				} else // if (event instanceof ControllerClosedEvent)
				{
					cannotRealizeExceptionMessage = "Cannot realize: received ControllerClosedEvent: "
							+ event
							+ "; message: "
							+ ((ControllerClosedEvent) event).getMessage();
					logger.info(cannotRealizeExceptionMessage);
				}

				realized = false;
				busy = false;
				notify();
			}
		}

		public BlockingRealizer(Controller controller) {
			super();
			this.controller = controller;
		}
	}

	public static String getCacheDirectory() {
		return System.getProperty("java.io.tmpdir");
	}

	public static void setHint(int hint, Object value) {
		hints.put(new Integer(hint), value);
	}

	public static Object getHint(int hint) {
		return hints.get(new Integer(hint));
	}

	public static Vector getDataSourceList(String protocolName) {
		return getClassList(protocolName, PackageManager
				.getProtocolPrefixList(), "protocol", "DataSource");

	}

	private static char toPackageFriendly(char c) {
		if (c >= 'a' && c <= 'z')
			return c;
		else if (c >= 'A' && c <= 'Z')
			return c;
		else if (c >= '0' && c <= '9')
			return c;
		else if (c == '.')
			return c;
		else if (c == '/')
			return '.';
		else
			return '_';
	}

	private static String toPackageFriendly(String contentName) {
		final StringBuffer b = new StringBuffer();
		for (int i = 0; i < contentName.length(); ++i) {
			final char c = contentName.charAt(i);
			b.append(toPackageFriendly(c));
		}
		return b.toString();
	}

	public static Vector getClassList(String contentName, Vector packages,
			String component2, String className) {

		final Vector result = new Vector();
		result.add("media." + component2 + "." + contentName + "." + className);

		for (int i = 0; i < packages.size(); ++i) {
			result.add(((String) packages.get(i)) + ".media." + component2
					+ "." + contentName + "." + className);
		}

		return result;
	}

	public static Vector getDataSinkClassList(String contentName) {
		return getClassList(toPackageFriendly(contentName), PackageManager
				.getContentPrefixList(), "datasink", "Handler");

	}

	public static Vector getHandlerClassList(String contentName) {
		return getClassList(toPackageFriendly(contentName), PackageManager
				.getContentPrefixList(), "content", "Handler");

	}

	public static Vector getProcessorClassList(String contentName) {
		return getClassList(toPackageFriendly(contentName), PackageManager
				.getContentPrefixList(), "processor", "Handler");
	}
}

package net.sf.fmj.utility;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.media.CaptureDeviceInfo;
import javax.media.MediaLocator;
import javax.media.PlugInManager;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * 
 * @author Ken Larson
 * @author Warren Bloomer
 * 
 */
public class RegistryIO {
	private static final Logger logger = LoggerSingleton.logger;

	private final RegistryContents registryContents;

	/** version of the registry file format */
	private static final String version = "0.1";

	public RegistryIO(RegistryContents registryContents) {
		super();
		this.registryContents = registryContents;
	}

	/* ---------------------- XML operations --------------------- */

	private static final String ELEMENT_REGISTRY = "registry";
	private static final String ATTR_VERSION = "version";

	private static final String ELEMENT_PROTO_PREFIX = "protocol-prefixes";
	private static final String ELEMENT_CONTENT_PREFIX = "content-prefixes";
	private static final String ELEMENT_PLUGINS = "plugins";
	private static final String ELEMENT_MIMETYPES = "mime-types";
	private static final String ELEMENT_MIMETYPE = "type";
	private static final String ELEMENT_CAPTURE_DEVICES = "capture-devices";

	private static final String ELEMENT_CODECS = "codecs";
	private static final String ELEMENT_DEMUXES = "demuxes";
	private static final String ELEMENT_MUXES = "muxes";
	private static final String ELEMENT_EFFECTS = "effects";
	private static final String ELEMENT_RENDERERS = "renderers";

	private static final String ELEMENT_PREFIX = "prefix";
	private static final String ELEMENT_CLASS = "class";

	private static final String ELEMENT_DEVICE = "device";
	private static final String ELEMENT_DEVICE_NAME = "name";
	private static final String ELEMENT_DEVICE_LOCATOR = "locator";
	private static final String ELEMENT_DEVICE_FORMAT = "format";
	private static final String ELEMENT_DEVICE_FORMAT_CLASS = "class";
	private static final String ELEMENT_DEVICE_FORMAT_DESCRIPTION = "description";
	private static final String ELEMENT_DEVICE_FORMAT_SERIALIZED = "serialized";

	/**
	 * Load the Registry data from a Reader/
	 */
	public void load(Reader reader) throws IOException {

		// read the registry
		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			Document document = saxBuilder.build(reader);

			// read all the data from the document and place into data
			// structures.
			loadDocument(document);
		} catch (JDOMException e) {
			// problem parsing XML.
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * Write the registry data to the Writer.
	 * 
	 * @param writer
	 *            destination for the registry data.
	 * @throws IOException
	 */
	public void write(Writer writer) throws IOException {
		// build document from registry data structures
		Document document = buildDocument();

		XMLOutputter xmlOutputter = new XMLOutputter();
		xmlOutputter.setFormat(Format.getPrettyFormat());

		xmlOutputter.output(document, writer);
	}

	private void loadDocument(Document document) throws IOException {

		Element rootElement = document.getRootElement();
		String versionString = rootElement.getAttributeValue(ATTR_VERSION);

		// TODO use version String
		logger.info("FMJ registry document version " + versionString);

		Element pluginsElement = rootElement.getChild(ELEMENT_PLUGINS);
		loadPlugins(pluginsElement);

		Element contentPrefixesElement = rootElement
				.getChild(ELEMENT_CONTENT_PREFIX);
		loadContentPrefixes(contentPrefixesElement);

		Element protocolPrefixesElement = rootElement
				.getChild(ELEMENT_PROTO_PREFIX);
		loadProtocolPrefixes(protocolPrefixesElement);

		Element mimetypesElement = rootElement.getChild(ELEMENT_MIMETYPES);
		loadMimeTypes(mimetypesElement);

		// load capture devices
		Element captureDevicesElement = rootElement
				.getChild(ELEMENT_CAPTURE_DEVICES);
		try {
			loadCaptureDevices(captureDevicesElement);
		} catch (ClassNotFoundException e) {
			throw new IOException(e.getMessage());
		}

	}

	private void loadPlugins(Element element) {

		Element codecsElement = element.getChild(ELEMENT_CODECS);
		loadPlugins(codecsElement, PlugInManager.CODEC);

		Element effectsElement = element.getChild(ELEMENT_EFFECTS);
		loadPlugins(effectsElement, PlugInManager.EFFECT);

		Element renderersElement = element.getChild(ELEMENT_RENDERERS);
		loadPlugins(renderersElement, PlugInManager.RENDERER);

		Element muxesElement = element.getChild(ELEMENT_MUXES);
		loadPlugins(muxesElement, PlugInManager.MULTIPLEXER);

		Element demuxesElement = element.getChild(ELEMENT_DEMUXES);
		loadPlugins(demuxesElement, PlugInManager.DEMULTIPLEXER);
	}

	/**
	 * 
	 * @param type
	 */
	private void loadPlugins(Element element, int type) {
		if (element == null) {
			return;
		}

		Vector vector = registryContents.plugins[type - 1];
		List pluginElements = element.getChildren(ELEMENT_CLASS);
		for (int i = 0; i < pluginElements.size(); i++) {
			Element pluginElement = (Element) pluginElements.get(i);
			String classname = pluginElement.getTextTrim();
			vector.add(classname);
		}
	}

	private void loadProtocolPrefixes(Element element) {
		registryContents.protocolPrefixList.clear();
		List list = element.getChildren(ELEMENT_PREFIX);
		for (int i = 0; i < list.size(); i++) {
			Element prefixElement = (Element) list.get(i);
			registryContents.protocolPrefixList
					.add(prefixElement.getTextTrim());
		}
	}

	private void loadContentPrefixes(Element element) {
		registryContents.contentPrefixList.clear();
		List list = element.getChildren(ELEMENT_PREFIX);
		for (int i = 0; i < list.size(); i++) {
			Element prefixElement = (Element) list.get(i);
			registryContents.contentPrefixList.add(prefixElement.getTextTrim());
		}
	}

	/**
	 * Builds a Document from the registry data structures.
	 * 
	 * @return the Document.
	 * @throws IOException
	 */
	private Document buildDocument() throws IOException {
		Document document = new Document();

		Element rootElement = new Element(ELEMENT_REGISTRY);
		rootElement.setAttribute(ATTR_VERSION, version);

		document.setRootElement(rootElement);

		rootElement.addContent(getPluginsElement());
		rootElement.addContent(getContentElement());
		rootElement.addContent(getProtocolElement());
		rootElement.addContent(getMimeElement());
		rootElement.addContent(getCaptureDeviceElement());

		return document;
	}

	private Element getPluginsElement() {
		Element pluginElement = new Element(ELEMENT_PLUGINS);

		pluginElement.addContent(getCodecElement());
		pluginElement.addContent(getDemuxElement());
		pluginElement.addContent(getEffectElement());
		pluginElement.addContent(getMuxElement());
		pluginElement.addContent(getRendererElement());

		return pluginElement;
	}

	private Element getCodecElement() {
		return getPluginElement(PlugInManager.CODEC, ELEMENT_CODECS);
	}

	private Element getDemuxElement() {
		return getPluginElement(PlugInManager.DEMULTIPLEXER, ELEMENT_DEMUXES);
	}

	private Element getEffectElement() {
		return getPluginElement(PlugInManager.EFFECT, ELEMENT_EFFECTS);
	}

	private Element getMuxElement() {
		return getPluginElement(PlugInManager.MULTIPLEXER, ELEMENT_MUXES);
	}

	private Element getRendererElement() {
		return getPluginElement(PlugInManager.RENDERER, ELEMENT_RENDERERS);
	}

	private Element getPluginElement(int pluginType, String typeName) {
		Element pluginsElement = new Element(typeName);
		Vector plugins = registryContents.plugins[pluginType - 1];

		if (plugins != null) {
			Iterator pluginIter = plugins.iterator();
			while (pluginIter.hasNext()) {
				String classname = (String) pluginIter.next();
				Element pluginElement = new Element(ELEMENT_CLASS);
				pluginElement.setText(classname);

				pluginsElement.addContent(pluginElement);
			}
		}

		return pluginsElement;
	}

	private Element getContentElement() {
		Element contentElement = new Element(ELEMENT_CONTENT_PREFIX);

		Iterator prefixIter = registryContents.contentPrefixList.iterator();
		while (prefixIter.hasNext()) {
			String prefix = (String) prefixIter.next();
			Element prefixElement = new Element(ELEMENT_PREFIX);
			prefixElement.setText(prefix);
			contentElement.addContent(prefixElement);
		}

		return contentElement;
	}

	private Element getProtocolElement() {
		Element protocolElement = new Element(ELEMENT_PROTO_PREFIX);
		Iterator prefixIter = registryContents.protocolPrefixList.iterator();
		while (prefixIter.hasNext()) {
			String prefix = (String) prefixIter.next();
			Element prefixElement = new Element(ELEMENT_PREFIX);
			prefixElement.setText(prefix);
			protocolElement.addContent(prefixElement);
		}
		return protocolElement;
	}

	/* -------------- MIME methods -------------- */

	private void loadMimeTypes(Element element) {
		registryContents.mimeTable.clear();

		final List list = element.getChildren(ELEMENT_MIMETYPE);
		for (int i = 0; i < list.size(); i++) {
			final Element typeElement = (Element) list.get(i);
			String type = typeElement.getAttributeValue("value");
			String defaultExtension = typeElement
					.getAttributeValue("default-ext");

			final List list2 = typeElement.getChildren("ext");
			for (int j = 0; j < list2.size(); j++) {
				final Element extElement = (Element) list2.get(j);
				String ext = extElement.getText();

				registryContents.mimeTable.addMimeType(ext, type);
			}
			registryContents.mimeTable.addMimeType(defaultExtension, type);
		}
	}

	private Element getMimeElement() {
		Element mimeElement = new Element(ELEMENT_MIMETYPES);

		final Iterator typesIterator = registryContents.mimeTable
				.getMimeTypes().iterator();
		while (typesIterator.hasNext()) {
			final String type = (String) typesIterator.next();
			final List extensions = registryContents.mimeTable
					.getExtensions(type);

			final Element typeElement = new Element(ELEMENT_MIMETYPE);
			typeElement.setAttribute("value", type);
			typeElement.setAttribute("default-ext", registryContents.mimeTable
					.getDefaultExtension(type));
			mimeElement.addContent(typeElement);

			for (int i = 0; i < extensions.size(); ++i) {
				String ext = (String) extensions.get(i);
				final Element extElement = new Element("ext");
				extElement.setText(ext);
				typeElement.addContent(extElement);
			}

		}

		return mimeElement;
	}

	/* -------------- Capture Device methods -------------- */

	private void loadCaptureDevices(Element element) throws IOException,
			ClassNotFoundException {
		registryContents.captureDeviceInfoList.clear();
		final List list = element.getChildren(ELEMENT_DEVICE);
		for (int i = 0; i < list.size(); i++) {
			final Element deviceElement = (Element) list.get(i);
			final Element deviceNameElement = deviceElement
					.getChild(ELEMENT_DEVICE_NAME);
			final Element deviceLocatorElement = deviceElement
					.getChild(ELEMENT_DEVICE_LOCATOR);
			final List formatElementsList = deviceElement
					.getChildren(ELEMENT_DEVICE_FORMAT);
			final javax.media.Format[] formats = new javax.media.Format[formatElementsList
					.size()];
			for (int j = 0; j < formatElementsList.size(); ++j) {
				final Element formatElement = (Element) formatElementsList
						.get(j);
				final Element serializedElement = formatElement
						.getChild(ELEMENT_DEVICE_FORMAT_SERIALIZED);
				formats[j] = SerializationUtils.deserialize(serializedElement
						.getTextTrim());
			}

			final CaptureDeviceInfo info = new CaptureDeviceInfo(
					deviceNameElement.getTextTrim(), new MediaLocator(
							deviceLocatorElement.getTextTrim()), formats);
			registryContents.captureDeviceInfoList.add(info);

		}
	}

	private Element getCaptureDeviceElement() throws IOException {
		final Element captureDeviceElement = new Element(
				ELEMENT_CAPTURE_DEVICES);
		final Iterator<CaptureDeviceInfo> iter = registryContents.captureDeviceInfoList
				.iterator();
		while (iter.hasNext()) {
			final CaptureDeviceInfo info = iter.next();

			if (info.getLocator() == null)
				continue; // should never be null, only seems to be null due to
							// some unit tests.

			final Element deviceElement = new Element(ELEMENT_DEVICE);
			{
				final Element deviceNameElement = new Element(
						ELEMENT_DEVICE_NAME);
				deviceNameElement.setText(info.getName());
				deviceElement.addContent(deviceNameElement);
			}

			{
				final Element e = new Element(ELEMENT_DEVICE_LOCATOR);
				e.setText(info.getLocator().toExternalForm());
				deviceElement.addContent(e);
			}
			{
				final javax.media.Format[] formats = info.getFormats();
				for (int i = 0; i < formats.length; ++i) {
					final Element formatElement = new Element(
							ELEMENT_DEVICE_FORMAT);

					{
						final Element e2 = new Element(
								ELEMENT_DEVICE_FORMAT_CLASS); // for XML
																// readability
																// only
						e2.setText(formats[i].getClass().getName());
						formatElement.addContent(e2);
					}

					{
						final Element e2 = new Element(
								ELEMENT_DEVICE_FORMAT_DESCRIPTION); // for XML
																	// readability
																	// only
						e2.setText(formats[i].toString());
						formatElement.addContent(e2);
					}
					// TODO: perhaps "known" formats like RGBFormat could be
					// serialized much more nicely.
					// we have to use serialization because that is the only way
					// to support JMF-compatible subclasses
					// that are not in JMF.
					{
						final Element e2 = new Element(
								ELEMENT_DEVICE_FORMAT_SERIALIZED);
						e2.setText(SerializationUtils.serialize(formats[i]));
						formatElement.addContent(e2);
					}

					deviceElement.addContent(formatElement);
				}
			}

			captureDeviceElement.addContent(deviceElement);
		}
		return captureDeviceElement;
	}

}

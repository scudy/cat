package com.dianping.cat;

import java.io.File;
import java.text.MessageFormat;
import java.util.Date;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.unidal.helper.Files;
import org.unidal.helper.Properties;
import org.unidal.initialization.DefaultModuleContext;
import org.unidal.initialization.Module;
import org.unidal.initialization.ModuleContext;
import org.unidal.initialization.ModuleInitializer;
import org.unidal.lookup.ContainerLoader;

import com.dianping.cat.configuration.EnvironmentHelper;
import com.dianping.cat.configuration.client.entity.ClientConfig;
import com.dianping.cat.configuration.client.entity.Server;
import com.dianping.cat.configuration.client.transform.DefaultSaxParser;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.ForkedTransaction;
import com.dianping.cat.message.Heartbeat;
import com.dianping.cat.message.MessageProducer;
import com.dianping.cat.message.TaggedTransaction;
import com.dianping.cat.message.Trace;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.internal.NullMessage;
import com.dianping.cat.message.internal.NullMessageManager;
import com.dianping.cat.message.internal.NullMessageProducer;
import com.dianping.cat.message.spi.MessageManager;
import com.dianping.cat.message.spi.MessageTree;

/**
 * This is the main entry point to the system.
 */
public class Cat {

	private MessageProducer m_producer;

	private MessageManager m_manager;

	private PlexusContainer m_container;

	private static int m_errorCount;

	private static Cat s_instance = new Cat();

	private static volatile boolean s_init = false;

	public static final String CLENT_CONFIG = "cat-client-config";

	public static final String UNKNOWN = "unknown";

	private static void checkAndInitialize() {
		try {
			if (!s_init) {
				initialize();
			}
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	/**
	 * @return create next cat message id
	 */
	public static String createMessageId() {
		try {
			return Cat.getProducer().createMessageId();
		} catch (Exception e) {
			errorHandler(e);
			return NullMessageProducer.NULL_MESSAGE_PRODUCER.createMessageId();
		}
	}

	/**
	 * destory cat if stop jvm
	 */
	public static void destroy() {
		try {
			s_instance.m_container.dispose();
			s_instance = new Cat();
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	private static void errorHandler(Exception e) {
		if (m_errorCount++ % 100 == 0 || m_errorCount <= 3) {
			e.printStackTrace();
		}
	}

	/**
	 * @return current cat config path , default value is /data/appdatas/cat/
	 */
	public static String getCatHome() {
		String catHome = Properties.forString().fromEnv().fromSystem().getProperty("CAT_HOME", "/data/appdatas/cat/");

		return catHome;
	}

	/**
	 * @return current cat message tree id
	 */
	public static String getCurrentMessageId() {
		try {
			MessageTree tree = Cat.getManager().getThreadLocalMessageTree();

			if (tree != null) {
				String messageId = tree.getMessageId();

				if (messageId == null) {
					messageId = Cat.createMessageId();
					tree.setMessageId(messageId);
				}
				return messageId;
			} else {
				return null;
			}
		} catch (Exception e) {
			errorHandler(e);
			return NullMessageProducer.NULL_MESSAGE_PRODUCER.createMessageId();
		}
	}

	/**
	 * @return cat instance
	 */
	public static Cat getInstance() {
		return s_instance;
	}

	/**
	 * @return message manager
	 */
	public static MessageManager getManager() {
		try {
			checkAndInitialize();
			MessageManager manager = s_instance.m_manager;

			if (manager != null) {
				return manager;
			} else {
				return NullMessageManager.NULL_MESSAGE_MANAGER;
			}
		} catch (Exception e) {
			errorHandler(e);
			return NullMessageManager.NULL_MESSAGE_MANAGER;
		}
	}

	/**
	 * @return message producer
	 */
	public static MessageProducer getProducer() {
		try {
			checkAndInitialize();

			MessageProducer producer = s_instance.m_producer;

			if (producer != null) {
				return producer;
			} else {
				return NullMessageProducer.NULL_MESSAGE_PRODUCER;
			}
		} catch (Exception e) {
			errorHandler(e);
			return NullMessageProducer.NULL_MESSAGE_PRODUCER;
		}
	}

	/**
	 * this should be called during application initialization time
	 */
	public static void initialize() {
		try {
			if (!s_init) {
				synchronized (s_instance) {
					if (!s_init) {
						PlexusContainer container = ContainerLoader.getDefaultContainer();
						ModuleContext ctx = new DefaultModuleContext(container);
						Module module = ctx.lookup(Module.class, CatClientModule.ID);

						if (!module.isInitialized()) {
							ModuleInitializer initializer = ctx.lookup(ModuleInitializer.class);

							initializer.execute(ctx, module);
						}
						log("INFO", "Cat is lazy initialized!");
						s_init = true;
					}
				}
			}
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	/**
	 * this should be called during application initialization time
	 */
	public static void initialize(ClientConfig config) {
		try {
			if (!s_init) {
				synchronized (s_instance) {
					if (!s_init) {
						System.setProperty(Cat.CLENT_CONFIG, config.toString());

						PlexusContainer container = ContainerLoader.getDefaultContainer();
						ModuleContext ctx = new DefaultModuleContext(container);
						Module module = ctx.lookup(Module.class, CatClientModule.ID);

						if (!module.isInitialized()) {
							ModuleInitializer initializer = ctx.lookup(ModuleInitializer.class);

							initializer.execute(ctx, module);
						}
						log("INFO", "Cat is lazy initialized!");
						s_init = true;
					}
				}
			}
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	/**
	 * @deprecated
	 * @param configFile
	 */
	public static void initialize(File configFile) {

	}

	/**
	 * this should be called during application initialization time
	 */
	public static void initialize(PlexusContainer container, File configFile) {
		try {
			String config = Files.forIO().readFrom(configFile, "utf-8");
			System.setProperty(CLENT_CONFIG, config);
		} catch (Exception e) {
			// ingnore
		}

		ModuleContext ctx = new DefaultModuleContext(container);
		Module module = ctx.lookup(Module.class, CatClientModule.ID);

		if (!module.isInitialized()) {
			ModuleInitializer initializer = ctx.lookup(ModuleInitializer.class);

			initializer.execute(ctx, module);
		}
	}

	/**
	 * this should be called during application initialization time
	 * 
	 * @param servers
	 *           cat server ip, with default tcp port 2280, default http port 8080
	 */
	public static void initialize(String... servers) {
		try {
			ClientConfig config = new ClientConfig();

			for (String server : servers) {
				config.addServer(new Server(server));
			}

			config.setDomain(EnvironmentHelper.loadAppNameByProperty(UNKNOWN));

			initialize(config);
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	/**
	 * this should be called during application initialization time
	 * 
	 * @param domain
	 */
	public static void initializeByDomain(String domain) {
		try {
			String path = Cat.getCatHome() + "client.xml";
			File configFile = new File(path);
			String xml = Files.forIO().readFrom(configFile, "utf-8");
			ClientConfig config = DefaultSaxParser.parse(xml);

			config.setDomain(EnvironmentHelper.loadAppNameByProperty(domain));
			initialize(config);
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	/**
	 * this should be called during application initialization time
	 * 
	 * @param domain
	 * @param port
	 * @param httpPort
	 * @param servers
	 *           cat server ip list
	 */
	public static void initializeByDomain(String domain, int port, int httpPort, String... servers) {
		try {
			ClientConfig config = new ClientConfig();

			config.setDomain(EnvironmentHelper.loadAppNameByProperty(domain));

			for (String server : servers) {
				Server serverObj = new Server(server);
				serverObj.setHttpPort(httpPort);
				serverObj.setPort(port);
				config.addServer(serverObj);
			}

			initialize(config);
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	/**
	 * this should be called during application initialization time
	 * 
	 * @param domain
	 * @param servers
	 *           cat server ip list
	 */
	public static void initializeByDomain(String domain, String... servers) {
		try {
			initializeByDomain(domain, 2280, 80, servers);
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	public static boolean isInitialized() {
		return s_init;
	}

	static void log(String severity, String message) {
		MessageFormat format = new MessageFormat("[{0,date,MM-dd HH:mm:ss.sss}] [{1}] [{2}] {3}");

		System.out.println(format.format(new Object[] { new Date(), severity, "cat", message }));
	}

	/**
	 * log error to cat
	 * 
	 * @param message
	 * @param cause
	 */
	public static void logError(String message, Throwable cause) {
		try {
			Cat.getProducer().logError(message, cause);
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	/**
	 * log error to cat
	 * 
	 * @param cause
	 */
	public static void logError(Throwable cause) {
		try {
			Cat.getProducer().logError(cause);
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	/**
	 * Log an event in one shot with SUCCESS status.
	 * 
	 * @param type
	 *           event type
	 * @param name
	 *           event name
	 */
	public static void logEvent(String type, String name) {
		try {
			Cat.getProducer().logEvent(type, name);
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	/**
	 * Log an event in one shot.
	 * 
	 * @param type
	 *           event type
	 * @param name
	 *           event name
	 * @param status
	 *           "0" means success, otherwise means error code
	 * @param nameValuePairs
	 *           name value pairs in the format of "a=1&b=2&..."
	 */
	public static void logEvent(String type, String name, String status, String nameValuePairs) {
		try {
			Cat.getProducer().logEvent(type, name, status, nameValuePairs);
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	/**
	 * Log a heartbeat in one shot.
	 * 
	 * @param type
	 *           heartbeat type
	 * @param name
	 *           heartbeat name
	 * @param status
	 *           "0" means success, otherwise means error code
	 * @param nameValuePairs
	 *           name value pairs in the format of "a=1&b=2&..."
	 */
	public static void logHeartbeat(String type, String name, String status, String nameValuePairs) {
		try {
			Cat.getProducer().logHeartbeat(type, name, status, nameValuePairs);
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	/**
	 * @deprecated
	 * @param name
	 * @param keyValues
	 */
	public static void logMetric(String name, Object... keyValues) {
	}

	/**
	 * Increase the counter specified by <code>name</code> by one.
	 * 
	 * @param name
	 *           the name of the metric default count value is 1
	 */
	public static void logMetricForCount(String name) {
		logMetricInternal(name, "C", "1");
	}

	/**
	 * Increase the counter specified by <code>name</code> by one.
	 * 
	 * @param name
	 *           the name of the metric
	 */
	public static void logMetricForCount(String name, int quantity) {
		logMetricInternal(name, "C", String.valueOf(quantity));
	}

	/**
	 * Increase the metric specified by <code>name</code> by <code>durationInMillis</code>.
	 * 
	 * @param name
	 *           the name of the metric
	 * @param durationInMillis
	 *           duration in milli-second added to the metric
	 */
	public static void logMetricForDuration(String name, long durationInMillis) {
		logMetricInternal(name, "T", String.valueOf(durationInMillis));
	}

	/**
	 * Increase the sum specified by <code>name</code> by <code>value</code> only for one item.
	 * 
	 * @param name
	 *           the name of the metric
	 * @param value
	 *           the value added to the metric
	 */
	public static void logMetricForSum(String name, double value) {
		logMetricInternal(name, "S", String.format("%.2f", value));
	}

	/**
	 * Increase the metric specified by <code>name</code> by <code>sum</code> for multiple items.
	 * 
	 * @param name
	 *           the name of the metric
	 * @param sum
	 *           the sum value added to the metric
	 * @param quantity
	 *           the quantity to be accumulated
	 */
	public static void logMetricForSum(String name, double sum, int quantity) {
		logMetricInternal(name, "S,C", String.format("%s,%.2f", quantity, sum));
	}

	private static void logMetricInternal(String name, String status, String keyValuePairs) {
		try {
			Cat.getProducer().logMetric(name, status, keyValuePairs);
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	/**
	 * logRemoteCallClient is used in rpc client
	 * 
	 * @param ctx
	 *           ctx is rpc context ,such as duboo context , please use rpc context implement Context
	 * @param domain
	 *           domain is default, if use default config, the performance of server storage is bad。
	 * 
	 */
	public static void logRemoteCallClient(Context ctx) {
		logRemoteCallClient(ctx, "default");
	}

	/**
	 * logRemoteCallClient is used in rpc client
	 * 
	 * @param ctx
	 *           ctx is rpc context ,such as duboo context , please use rpc context implement Context
	 * @param domain
	 *           domain is project name of rpc server name
	 */
	public static void logRemoteCallClient(Context ctx, String domain) {
		try {
			MessageTree tree = Cat.getManager().getThreadLocalMessageTree();
			String messageId = tree.getMessageId();

			if (messageId == null) {
				messageId = Cat.createMessageId();
				tree.setMessageId(messageId);
			}

			String childId = Cat.getProducer().createRpcServerId(domain);
			Cat.logEvent(CatConstants.TYPE_REMOTE_CALL, "", Event.SUCCESS, childId);

			String root = tree.getRootMessageId();

			if (root == null) {
				root = messageId;
			}

			ctx.addProperty(Context.ROOT, root);
			ctx.addProperty(Context.PARENT, messageId);
			ctx.addProperty(Context.CHILD, childId);
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	/**
	 * used in rpc server，use clild id as server message tree id.
	 * 
	 * @param ctx
	 *           ctx is rpc context ,such as duboo context , please use rpc context implement Context
	 */
	public static void logRemoteCallServer(Context ctx) {
		try {
			MessageTree tree = Cat.getManager().getThreadLocalMessageTree();
			String childId = ctx.getProperty(Context.CHILD);
			String rootId = ctx.getProperty(Context.ROOT);
			String parentId = ctx.getProperty(Context.PARENT);

			if (parentId != null) {
				tree.setParentMessageId(parentId);
			}
			if (rootId != null) {
				tree.setRootMessageId(rootId);
			}
			if (childId != null) {
				tree.setMessageId(childId);
			}
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	/**
	 * Log an trace in one shot with SUCCESS status.
	 * 
	 * @param type
	 *           trace type
	 * @param name
	 *           trace name
	 */
	public static void logTrace(String type, String name) {
		try {
			Cat.getProducer().logTrace(type, name);
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	/**
	 * Log an trace in one shot.
	 * 
	 * @param type
	 *           trace type
	 * @param name
	 *           trace name
	 * @param status
	 *           "0" means success, otherwise means error code
	 * @param nameValuePairs
	 *           name value pairs in the format of "a=1&b=2&..."
	 */
	public static void logTrace(String type, String name, String status, String nameValuePairs) {
		try {
			Cat.getProducer().logTrace(type, name, status, nameValuePairs);
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	public static <T> T lookup(Class<T> role) throws ComponentLookupException {
		return lookup(role, null);
	}

	public static <T> T lookup(Class<T> role, String hint) throws ComponentLookupException {
		return s_instance.m_container.lookup(role, hint);
	}

	/**
	 * Create a new event with given type and name.
	 * 
	 * @param type
	 *           event type
	 * @param name
	 *           event name
	 */
	public static Event newEvent(String type, String name) {
		try {
			return Cat.getProducer().newEvent(type, name);
		} catch (Exception e) {
			errorHandler(e);
			return NullMessage.EVENT;
		}
	}

	/**
	 * Create a forked transaction for child thread.
	 * 
	 * @param type
	 *           transaction type
	 * @param name
	 *           transaction name
	 * @return forked transaction
	 */
	public static ForkedTransaction newForkedTransaction(String type, String name) {
		try {
			return Cat.getProducer().newForkedTransaction(type, name);
		} catch (Exception e) {
			errorHandler(e);
			return NullMessage.TRANSACTION;
		}
	}

	/**
	 * Create a new heartbeat with given type and name.
	 * 
	 * @param type
	 *           heartbeat type
	 * @param name
	 *           heartbeat name
	 */
	public static Heartbeat newHeartbeat(String type, String name) {
		try {
			return Cat.getProducer().newHeartbeat(type, name);
		} catch (Exception e) {
			errorHandler(e);
			return NullMessage.HEARTBEAT;
		}
	}

	/**
	 * Create a tagged transaction for another process or thread.
	 * 
	 * @param type
	 *           transaction type
	 * @param name
	 *           transaction name
	 * @param tag
	 *           tag applied to the transaction
	 * @return tagged transaction
	 */
	public static TaggedTransaction newTaggedTransaction(String type, String name, String tag) {
		try {
			return Cat.getProducer().newTaggedTransaction(type, name, tag);
		} catch (Exception e) {
			errorHandler(e);
			return NullMessage.TRANSACTION;
		}
	}

	/**
	 * Create a new trace with given type and name.
	 * 
	 * @param type
	 *           trace type
	 * @param name
	 *           trace name
	 */
	public static Trace newTrace(String type, String name) {
		try {
			return Cat.getProducer().newTrace(type, name);
		} catch (Exception e) {
			errorHandler(e);
			return NullMessage.TRACE;
		}
	}

	/**
	 * Create a new transaction with given type and name.
	 * 
	 * @param type
	 *           transaction type
	 * @param name
	 *           transaction name
	 */
	public static Transaction newTransaction(String type, String name) {
		try {
			return Cat.getProducer().newTransaction(type, name);
		} catch (Exception e) {
			errorHandler(e);
			return NullMessage.TRANSACTION;
		}
	}

	/**
	 * @deprecated this should be called when a thread ends to clean some thread local data
	 */
	public static void reset() {
	}

	/**
	 * @deprecated this should be called when a thread starts to create some thread local data
	 */
	public static void setup(String sessionToken) {
		try {
			Cat.getManager().setup();
		} catch (Exception e) {
			errorHandler(e);
		}
	}

	private Cat() {
	}

	void setContainer(PlexusContainer container) {
		try {
			m_container = container;
			m_manager = container.lookup(MessageManager.class);
			m_producer = container.lookup(MessageProducer.class);
		} catch (ComponentLookupException e) {
			throw new RuntimeException("Unable to get instance of MessageManager, "
			      + "please make sure the environment was setup correctly!", e);
		}
	}

	public static interface Context {

		public final String ROOT = "_catRootMessageId";

		public final String PARENT = "_catParentMessageId";

		public final String CHILD = "_catChildMessageId";

		public void addProperty(String key, String value);

		public String getProperty(String key);
	}

}

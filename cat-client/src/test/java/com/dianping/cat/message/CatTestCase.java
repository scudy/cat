package com.dianping.cat.message;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.junit.After;
import org.junit.Before;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.cat.Cat;
import com.dianping.cat.configuration.client.entity.ClientConfig;
import com.dianping.cat.configuration.client.entity.Server;

public abstract class CatTestCase extends ComponentTestCase {
	
	protected ClientConfig getConfigurationFile() {
		if (isCatServerAlive()) {
			try {
				ClientConfig config = new ClientConfig();
				
				config.setDomain("cat");
				config.addServer(new Server("localhost").setPort(2280));

				return config;
			} catch (Exception e) {
				return null;
			}
		}

		return null;
	}

	protected boolean isCatServerAlive() {
		// detect if a CAT server listens on localhost:2280
		try {
			SocketChannel channel = SocketChannel.open(new InetSocketAddress("localhost", 2280));

			channel.close();
			return true;
		} catch (Exception e) {
			// ignore it
		}

		return false;
	}

	@Before
	public void setup() throws Exception {
		Cat.initialize(getConfigurationFile());
	}

	@After
	public void teardown() throws Exception {
	}
}
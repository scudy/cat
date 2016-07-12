package com.dianping.cat;

import org.junit.Before;
import org.junit.Test;

import com.dianping.cat.message.Transaction;

public class CatLazyInitTest {
	
	@Before
	public void setUp(){
		System.setProperty("devMode", "true");
	}

	@Test
	public void test() throws InterruptedException {
		for (int i = 0; i < 100; i++) {
			Transaction t = Cat.newTransaction("type", "name");

			t.complete();
		}

		Thread.sleep(100000);
	}
	
	
	@Test
	public void test2() throws InterruptedException {
		Cat.initializeByDomain("cat-test", "10.66.13.115");
		
		for (int i = 0; i < 100; i++) {
			Transaction t = Cat.newTransaction("type", "name");

			t.complete();
		}
		Thread.sleep(100000);
	}


}

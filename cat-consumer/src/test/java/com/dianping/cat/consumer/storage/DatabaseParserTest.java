package com.dianping.cat.consumer.storage;

import junit.framework.Assert;

import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.cat.consumer.DatabaseParser;
import com.dianping.cat.consumer.DatabaseParser.Database;

public class DatabaseParserTest extends ComponentTestCase {

	@Test
	public void testOracle() {
		DatabaseParser parser = lookup(DatabaseParser.class);
		Database database = parser.parseDatabase("jdbc:oracle:thin:@172.20.70.36:1521:gbst");

		System.err.println(database);
		Assert.assertEquals("172.20.70.36", database.getIp());
		Assert.assertEquals("gbst", database.getName());
	}

	@Test
	public void testOracle2() {
		DatabaseParser parser = lookup(DatabaseParser.class);
		Database database = parser.parseDatabase("jdbc:oracle:thin:username/password@//x.x.x.1:1522/ABCD");

		System.err.println(database);
		Assert.assertEquals("x.x.x.1", database.getIp());
		Assert.assertEquals("ABCD", database.getName());
	}

	@Test
	public void testOracle3() {
		DatabaseParser parser = lookup(DatabaseParser.class);
		Database database = parser
		      .parseDatabase("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=x.x.x.1)(PORT=1521))(ADDRESS=(PROTOCOL=TCP)(HOST=x.x.x.2)(PORT=1521)))(LOAD_BALANCE=yes)(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=xxrac)))");

		System.err.println(database);
		Assert.assertEquals("x.x.x.1", database.getIp());
		Assert.assertEquals("xxrac", database.getName());
	}

	@Test
	public void testOracle4() {
		DatabaseParser parser = lookup(DatabaseParser.class);
		Database database = parser
		      .parseDatabase("url=jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=ON)(FAILOVER=ON)(ADDRESS_LIST=(ADDRESS=(PROTOCOL=TCP)(HOST=10.86.25.28)(PORT=1521)))(CONNECT_DATA=(SERVER=DEDICATED)(SERVICE_NAME=cbst)(FAILOVER_MODE=(TYPE=SELECT)(METHOD=BASIC))))");

		System.err.println(database);
		Assert.assertEquals("10.86.25.28", database.getIp());
		Assert.assertEquals("cbst", database.getName());
	}

	@Test
	public void testMysql() {
		DatabaseParser parser = lookup(DatabaseParser.class);
		Database database = parser.parseDatabase("jdbc:mysql://localhost:3306/mydb");

		Assert.assertEquals("localhost", database.getIp());
		Assert.assertEquals("mydb", database.getName());
	}

}

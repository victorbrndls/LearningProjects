package com.harystolho.tda.client;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.harystolho.tda.client.processor.DatabaseQueryProcessor;

public class ConnectionIT {

	private static Connection conn;

	@BeforeAll
	public static void init() throws UnknownHostException {
		conn = new Connection(DatabaseQueryProcessor.create(InetAddress.getLocalHost(), 4455));
	}

	@Test
	public void test() {
		conn.beginTransaction();
	}
}
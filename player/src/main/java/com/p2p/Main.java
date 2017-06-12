package com.p2p;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.shell.Bootstrap;

public class Main {
	@SuppressWarnings("unused")
	private static final Logger log = LogManager.getLogger(Main.class.getName());

	public static void main(String[] args) throws IOException {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
		Bootstrap bs = new Bootstrap();
		bs.getJLineShellComponent().setApplicationContext(context);
		bs.getJLineShellComponent().start();
		context.registerShutdownHook();
	}

}

package com.p2p.net;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.p2p.util.Util;

import lombok.Getter;
import lombok.Setter;

@Component
public class Hub {
	private static final Logger log = LogManager.getLogger(Hub.class.getName());

	public static final int PLAYER_PORT = 2222;
	public static final int HUB_PORT = 1111;
	public static final int MAX_PLAYERS = 10;
	public static final String HEADER = "hub";
	public static final String SEPARATOR = "!!";

	@Autowired
	private Util util;

	@Autowired
	private HubHandler handler;

	@Getter
	@Setter
	private IoAcceptor acceptor;

	@Getter
	@Setter
	private InetAddress host;

	@Getter
	@Setter
	private String fileName;
	
	@Getter
	@Setter
	private List<IoSession> connected = new ArrayList<>();

	// only one hub as acceptor per app
	public String start(String networkInterface) throws SocketException {
		this.host = util.getIp(networkInterface);
		if (this.host == null)
			return "network interface not found";
		if (this.acceptor == null || !this.acceptor.isActive()) {
			try {
				this.acceptor = new NioSocketAcceptor();
				this.acceptor.getFilterChain().addLast("codec",
						new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
				this.acceptor.setHandler(this.handler);
				// TODO
				this.acceptor.getSessionConfig().setReadBufferSize(2048);
				this.acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 1000 * 60 * 60);
				this.acceptor.bind(new InetSocketAddress(HUB_PORT));
				log.info("hub started at " + this.host.toString() + ":" + HUB_PORT);
				return "hub started with success";
			} catch (IOException e) {
				return "hub cannot be started at port " + HUB_PORT + ", already used ?";
			}
		} else
			return "cannot start more than 1 hub per app";
	}

	public String stop() {
		if (this.acceptor != null) {
			this.acceptor.dispose();
			this.acceptor = null;
			log.info("hub at " + this.host.toString() + ":" + HUB_PORT + " stopped");
			return "hub stopped with success";
		} else
			return "hub is already stopped";
	}

	public String status() {
		if (this.acceptor == null || !this.acceptor.isActive())
			return "hub stopped";
		return "hub started at " + this.host.toString() + ":" + HUB_PORT;
	}

	public String libr(String name) {
		if (this.acceptor == null || !this.acceptor.isActive())
			return "start hub before";
		File input = new File(System.getProperty("user.home") + File.separator + name);
		if (!input.exists())
			return "no file named " + name + " in home directory";
		try {
			File output = util.librarify(input, this.host.toString(), HUB_PORT);
			if (output.exists()) {
				this.fileName = name;
				return name + " librarified with success: " + output.getAbsolutePath();
			}
			return "librarification failed";
		} catch (IOException e) {
			return "librarification failed";
		}
	}

	public String list() {
		return this.handler.listPlayers();
	}

}

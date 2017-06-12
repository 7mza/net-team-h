package com.p2p.net;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.UnresolvedAddressException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.p2p.util.Util;

import lombok.Getter;
import lombok.Setter;

@Component
public class Player extends IoHandlerAdapter {
	private static final Logger log = LogManager.getLogger(Player.class.getName());

	public static final int PLAYER_PORT = 2222;
	public static final int HUB_PORT = 1111;
	public static final int MAX_PLAYERS = 10;
	public static final String HEADER = "player";
	public static final String SEPARATOR = "!!";

	@Autowired
	private Util util;

	@Autowired
	private PlayerHandler handler;

	/*
	 * this as server
	 */
	@Getter
	@Setter
	private InetAddress host;
	@Getter
	@Setter
	private IoAcceptor acceptor;

	/*
	 * file handling
	 */
	@Getter
	@Setter
	private String fileName;
	@Getter
	@Setter
	private List<Boolean> downloadedBooks;
	@Getter
	@Setter
	private List<byte[]> fileContent;
	@Getter
	@Setter
	private List<String> sha1Codes;
	@Getter
	@Setter
	private int bookSize;

	/*
	 * communication with hub
	 */
	@Getter
	@Setter
	private IoConnector hubConnector;
	@Getter
	@Setter
	private IoSession hubSession;
	@Getter
	@Setter
	private Host hub;

	/*
	 * communication with players
	 */
	@Getter
	@Setter
	private List<IoConnector> playersConnectors;
	@Getter
	@Setter
	private List<IoSession> playersSessions;
	@Getter
	@Setter
	private List<Host> players;

	public String connectToHub(String ip) {
		if (this.acceptor == null || !this.acceptor.isActive())
			return "must start player before";
		if (this.hubSession != null)
			return "player already connected to hub";
		try {
			this.hubConnector = new NioSocketConnector();
			this.hubConnector.getFilterChain().addLast("codec",
					new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
			// hubConnector.getFilterChain().addLast("codec2", new
			// ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
			this.hubConnector.setHandler(this.handler);
			ConnectFuture future = hubConnector.connect(new InetSocketAddress(ip, HUB_PORT));
			future.awaitUninterruptibly();
			this.hubSession = future.getSession();
			// this.session.getCloseFuture().awaitUninterruptibly();
			log.info("player succesfully connected to hub at " + ip + ":" + HUB_PORT);
			return "player connected to hub with success";
		} catch (UnresolvedAddressException e) {
			return "player cannot connect to hub, check address ?";
		}
	}

	public String disconnect() {
		/*
		 * if (this.hubConnector != null) { this.hubSession.write("bye");
		 * log.info("player at " + this.host + ":" + this.port +
		 * " disconnected from hub at "); this.hubSession = null;
		 * this.hubConnector = null; return "player disconnected with success";
		 * } else return "player is not yet connected";
		 */
		if (this.hubSession == null)
			return "player is not yet connected";
		this.hubSession.write("bye");
		this.playersConnectors = null;
		this.hubConnector = null;
		this.hubSession = null;
		return "player disconnected with success";
	}

	// only one player as server per app
	public String start(String networkInterface) throws SocketException {
		this.host = util.getIp(networkInterface);
		if (this.host == null)
			return "network interface not found";
		if (this.acceptor == null || !this.acceptor.isActive()) {
			try {
				this.acceptor = new NioSocketAcceptor();
				// this.server.getFilterChain().addLast("logger", new
				// LoggingFilter());
				TextLineCodecFactory codec = new TextLineCodecFactory(Charset.forName("UTF-8"));
				codec.setDecoderMaxLineLength(6000);
				this.acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(codec));
				this.acceptor.setHandler(this.handler);
				// TODO
				this.acceptor.getSessionConfig().setReadBufferSize(2048);
				this.acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 1000 * 60 * 60);
				this.acceptor.bind(new InetSocketAddress(PLAYER_PORT));
				log.info("player started at " + this.host + ":" + PLAYER_PORT);
				return "player started with success";
			} catch (IOException e) {
				return "player cannot be started at port " + PLAYER_PORT + ", already used ?";
			}
		} else
			return "cannot start more than 1 player per app";
	}

	// TODO stop as server + client
	public String stop() {
		if (this.acceptor != null) {
			this.acceptor.dispose();
			this.acceptor = null;
			this.disconnect();
			log.info("player at " + this.host + ":" + PLAYER_PORT + " stopped");
			return "player stopped with success";
		} else
			return "player is already stopped";
	}

	// TODO status as server + client
	public String status() {
		if (this.acceptor == null || !this.acceptor.isActive())
			return "player stopped";
		return "player started";
	}

	public void update() {
		this.hubSession.write("update_me");
	}

	private List<Host> extractHosts(String content) {
		List<Host> hosts = new ArrayList<>();
		String[] tokens = content.split("/");
		for (String token : tokens) {
			if (!token.isEmpty())
				hosts.add(new Host(token.split("\\:")[0]));
		}
		return hosts;
	}

	public String libr(String librName) throws IOException {
		File libr = new File(System.getProperty("user.home") + File.separator + librName + "." + "libr");
		List<String> lines = FileUtils.readLines(libr, "UTF-8");
		this.hub = this.extractHosts(lines.get(0)).get(0);
		// TODO
		// this.start("wlan0");
		// this.connectToHub(this.hub.getIp());
		// TODO
		this.fileName = lines.get(1);
		int fileSize = Integer.valueOf(lines.get(2));
		File file = new File(System.getProperty("user.home") + File.separator + "copy" + "." + fileName);
		byte[] dummyContent = new byte[fileSize];
		FileUtils.writeByteArrayToFile(file, dummyContent);
		this.bookSize = Integer.valueOf(lines.get(3));
		this.downloadedBooks = new ArrayList<>();
		for (int i = 0; i < Integer.valueOf(lines.get(4)); i++) {
			downloadedBooks.add(i, false);
		}
		System.out.println(downloadedBooks.size());
		this.fileContent = new ArrayList<>();
		for (int i = 0; i < Integer.valueOf(lines.get(4)); i++) {
			this.fileContent.add(i, null);
		}
		System.out.println(fileContent.size());
		this.sha1Codes = FileUtils.readLines(libr);
		sha1Codes.remove(0);
		sha1Codes.remove(0);
		sha1Codes.remove(0);
		sha1Codes.remove(0);
		sha1Codes.remove(0);
		System.out.println(sha1Codes.get(0));
		return "libr file processed with success";
	}

	public String download() {
		Map<Host, IoSession> map = new HashMap<>();
		try {
			while (true) {
				if (isDownloaded() == true)
					break;
				int next = this.getNext();
				this.downloadedBooks.set(next, true);
				int index = (int) (Math.random() * this.players.size());
				Host host = this.players.get(index);
				IoSession session;
				if (map.containsKey(host))
					session = map.get(host);
				else {
					IoConnector connector = new NioSocketConnector();
					TextLineCodecFactory codec = new TextLineCodecFactory(Charset.forName("UTF-8"));
					codec.setDecoderMaxLineLength(6000);
					connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(codec));
					connector.setHandler(this.handler);
					ConnectFuture future = connector.connect(new InetSocketAddress(host.getIp(), PLAYER_PORT));
					future.awaitUninterruptibly();
					session = future.getSession();
					map.put(host, session);
				}
				session.write("player!!gimme_book!!" + next);
				// this.session.getCloseFuture().awaitUninterruptibly();
			}
			return "Download success";
		} catch (UnresolvedAddressException e1) {
			return "There is a probleme";
		}
	}

	private boolean isDownloaded() {
		for (Boolean b : this.downloadedBooks) {
			if (b == false)
				return false;
		}
		return true;
	}

	private int getNext() {
		int cpt = 0;
		loop: for (Boolean b : this.downloadedBooks) {
			if (b == true)
				cpt++;
			else
				break loop;
		}
		return cpt;
	}

	public String check() {
		double cpt = 0.0;
		double total = (double) this.downloadedBooks.size();
		System.out.println(total);
		if (total == 0)
			return String.valueOf("0% is downloaded");
		for (Boolean b : this.downloadedBooks) {
			if (b == true)
				cpt++;
		}
		System.out.println(cpt);
		double percentage = (cpt / total) * 100;
		return String.valueOf(percentage + "% is downloaded");
	}

	public String write() {
		try {
			File file = new File(System.getProperty("user.home") + File.separator + "copy" + "." + fileName);
			for (byte[] b : this.fileContent)
				FileUtils.writeByteArrayToFile(file, b, true);
		} catch (IOException e) {
			return "can't write file to drive";
		}
		return "file written successfully";
	}

	public String download(int book) {
		try {
			Host host = this.players.get(0);
			IoConnector connector = new NioSocketConnector();
			TextLineCodecFactory codec = new TextLineCodecFactory(Charset.forName("UTF-8"));
			codec.setDecoderMaxLineLength(6000);
			connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(codec));
			connector.setHandler(this.handler);
			ConnectFuture future = connector.connect(new InetSocketAddress(host.getIp(), PLAYER_PORT));
			future.awaitUninterruptibly();
			IoSession session = future.getSession();
			session.write("player!!gimme_book!!" + book);
			return "Download success";
		} catch (UnresolvedAddressException e1) {
			return "There is a probleme";
		}
	}

}

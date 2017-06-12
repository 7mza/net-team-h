package com.p2p.net;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.p2p.shell.PlayerCommands;
import com.p2p.util.Util;

@Component
public class PlayerHandler extends IoHandlerAdapter {
	private static final Logger log = LogManager.getLogger(PlayerHandler.class.getName());

	@Autowired
	private Player player;

	@Autowired
	private PlayerCommands pcmd;

	@Autowired
	private Util util;

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		cause.printStackTrace();
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		// log.info(message);
		String[] tokens = message.toString().trim().toLowerCase().split(Player.SEPARATOR);
		if (tokens.length < 3)
			return;
		String header = tokens[0];
		String msg = tokens[1];
		String content = tokens[2];
		if (header.equals("hub")) {
			switch (msg) {
			case "info":
				System.out.println(content);
				pcmd.print(content);
				break;
			case "take_that":
				this.player.setPlayers(this.extractHosts(content));
				System.out.println(this.player.getPlayers().size());
				break;
			case "take_care":
				System.out.println(content);
				pcmd.print(content);
				break;
			case "welcome":
				System.out.println(content);
				pcmd.print(content);
				break;
			}
		} else if (header.equals("player")) {
			switch (msg) {
			case "gimme_book":
				int book = Integer.valueOf(content);
				File file = new File(System.getProperty("user.home") + File.separator + this.player.getFileName());
				byte[] bytes = this.readBook(file, book);
				// log.info(sha);
				// log.info("+++" + Base64.encodeBase64String(bytes).length());
				// log.info(Base64.encodeBase64String(bytes));
				// System.out.println(this.prepareMessage(Base64.encodeBase64String(bytes)
				// + "%" + book, "take_book"));
				session.write(this.prepareMessage(Base64.encodeBase64URLSafeString(bytes) + "%" + book, "take_book"));
				break;
			case "take_book":
				String[] tokenss = content.split("%");
				String msgg = tokenss[0];
				byte[] bytess = Base64.decodeBase64(msgg);
				System.out.println(msgg);
				String sha1 = this.util.byteArrayToHexString(bytess);
				System.out.println(sha1);
				// TODO
				int bookk = Integer.valueOf(tokenss[1]);
				if (this.player.getSha1Codes().get(bookk).equalsIgnoreCase(sha1)) {
					this.player.getDownloadedBooks().set(bookk, true);
					this.player.getFileContent().set(bookk, bytess);
				} else {
					log.info("Book no " + bookk + " corrupted !");
					this.player.getDownloadedBooks().set(bookk, false);
				}
				break;
			}
		}
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {
		// TODO
		// session.closeOnFlush();
	}

	@Override
	public void sessionOpened(IoSession session) {
		// log.info("someone connected");
	}

	@Override
	public void sessionClosed(IoSession session) {
		// log.info("someone disconnected");
	}

	public String update() {
		// TODO Auto-generated method stub
		return null;
	}

	public String prepareMessage(Object obj, String type) {
		return Player.HEADER + Player.SEPARATOR + type + Player.SEPARATOR + obj;
	}

	private List<Host> extractHosts(String content) {
		List<Host> hosts = new ArrayList<>();
		String[] tokens = content.split("/");
		for (String token : tokens) {
			if (!token.isEmpty()) {
				System.out.println(this.player.getHost().getHostAddress() + "-" + token);
				if (!this.player.getHost().getHostAddress().trim().equalsIgnoreCase(token.trim()))
					hosts.add(new Host(token));
			}
		}
		return hosts;
	}

	public byte[] readBook(File file, int book) throws IOException {
		byte[] bytes = FileUtils.readFileToByteArray(file);
		byte[] slice = Arrays.copyOfRange(bytes, Util.BOOK_SIZE * book, Util.BOOK_SIZE * book + Util.BOOK_SIZE);
		return slice;
	}

}

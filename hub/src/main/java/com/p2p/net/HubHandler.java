package com.p2p.net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.p2p.shell.HubCommands;

@Component
public class HubHandler extends IoHandlerAdapter {
	private static final Logger log = LogManager.getLogger(HubHandler.class.getName());

	@Autowired
	private Hub hub;

	@Autowired
	private HubCommands hcmd;

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		cause.printStackTrace();
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		String str = message.toString().trim().toLowerCase();
		String msg;
		switch (str) {
		case "bye":
			msg = "Hope to see you soon :)";
			session.write(this.prepareMessage("take_care", msg));
			session.closeOnFlush();
			break;
		case "update_me":
			// List<SocketAddress> players = this.getListOfPlayers();
			session.write(this.prepareMessage("take_that", this.extractPlayers()));
		default:
			break;
		}
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) {
	}

	@Override
	public void sessionOpened(IoSession session) {
		this.hub.getConnected().add(session);
		String msg;
		if (this.hub.getConnected().size() > Hub.MAX_PLAYERS) {
			msg = "Sorry more than " + Hub.MAX_PLAYERS + " players are connected, try later";
			session.write(this.prepareMessage("info", msg));
			session.closeOnFlush();
			this.hub.getConnected().remove(session);
			return;
		}
		msg = "Welcome :) this hub is sharing " + this.hub.getFileName() + ", " + this.hub.getConnected().size()
				+ " players are connected";
		session.write(this.prepareMessage("info", msg));
		this.informPlayersOfNew(session);
	}

	@Override
	public void sessionClosed(IoSession session) {
		boolean removed = this.hub.getConnected().remove(session);
		this.informPlayersOfQuit(session);
		log.info("Player at " + session.getRemoteAddress() + " disconnected");
		log.info("\tRemoved from list: " + removed);
	}

	private void informPlayersOfNew(IoSession session) {
		String msg = "New player at " + session.getRemoteAddress() + " joined" + "\n" + this.hub.getConnected().size()
				+ " players are connected";
		for (IoSession tmp : this.hub.getConnected()) {
			if (tmp != session)
				tmp.write(this.prepareMessage("info", msg));
		}
	}

	private void informPlayersOfQuit(IoSession session) {
		String msg = "Player at " + session.getRemoteAddress() + " left" + "\n" + this.hub.getConnected().size()
				+ " players are connected";
		for (IoSession tmp : this.hub.getConnected()) {
			tmp.write(this.prepareMessage("info", msg));
		}
	}

	private String prepareMessage(String type, Object obj) {
		return Hub.HEADER + Hub.SEPARATOR + type + Hub.SEPARATOR + obj;
	}

	public String listPlayers() {
		int i = 1;
		String tmp = this.hub.getConnected().size() + " are connected" + "\n";
		for (IoSession session : this.hub.getConnected()) {
			tmp = tmp + i + " : " + session.getRemoteAddress().toString() + "\n";
			i++;
		}
		return tmp;
	}

	private String extractPlayers() {
		String msg = "";
		for (IoSession session : this.hub.getConnected()) {
			String tmp = session.getRemoteAddress().toString();
			String t = this.extractIp(tmp);
			System.out.println("----" + t);
			if (t.equals("127.0.0.1"))
				t = this.extractIp(this.hub.getHost().toString());
			msg = msg + t + "/";
			/*
			 * String[] tokens = tmp.split("/"); String t =
			 * tokens[1].split("\\:")[0]; System.out.println(t); if
			 * (t.equals("127.0.0.1")) tmp =
			 * this.hub.getHost().getHostAddress().toString(); msg = msg + tmp;
			 */
		}
		return msg;
	}

	private String extractIp(String tmp) {
		String[] tokens = tmp.split("/");
		return tokens[1].split("\\:")[0];
	}

}
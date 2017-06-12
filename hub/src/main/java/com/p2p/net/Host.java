package com.p2p.net;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Host {

	@Getter
	@Setter

	private String ip;
	@Getter
	@Setter
	private int port;

	public Host(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

}
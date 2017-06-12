package com.p2p.shell;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultHistoryFileNameProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PlayerLogFile extends DefaultHistoryFileNameProvider {

	public String getHistoryFileName() {
		return "player.log";
	}

	@Override
	public String getProviderName() {
		return "player shell log file";
	}

}

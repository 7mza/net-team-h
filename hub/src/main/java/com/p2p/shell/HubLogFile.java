package com.p2p.shell;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultHistoryFileNameProvider;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HubLogFile extends DefaultHistoryFileNameProvider {

	public String getHistoryFileName() {
		return "hub.log";
	}

	@Override
	public String getProviderName() {
		return "hub shell log file";
	}

}

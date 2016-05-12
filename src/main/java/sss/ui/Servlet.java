package sss.ui;


import com.vaadin.annotations.Push;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;
import sss.ui.reactor.ReactorActorSystem$;

import javax.servlet.annotation.WebServlet;

@SuppressWarnings("serial")
@WebServlet(value = "/*", asyncSupported = true)
@VaadinServletConfiguration(productionMode = false, ui = ConsoleUI.class, widgetset = "NobuWidgetSet")
@Push
public class Servlet extends VaadinServlet {
	
	@Override
	public void destroy() {		
		ReactorActorSystem$.MODULE$.blockingTerminate();
		log("Terminated actor system!");
		super.destroy();
	}
}


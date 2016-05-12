package sss.ui

import com.vaadin.annotations.{Push, Theme, Title}
import com.vaadin.server.VaadinRequest
import com.vaadin.ui._
import org.vaadin.notifique.Notifique

@Theme("sss-console")
@Title("sss console")
@SuppressWarnings(Array("serial"))
@Push
class ConsoleUI extends UI {

  protected def init(request: VaadinRequest) {
    val stack = new Notifique(false);
    getPage.setTitle("sss console")
    val baseLayout = new HorizontalLayout
    baseLayout.setMargin(false)
    baseLayout.setSizeFull
    baseLayout.addComponent(stack)
    setContent(baseLayout)
    val msg = new HorizontalLayout
    msg.addComponent(new Label("What?"))
    msg.addComponent(new Button("Send!"))

    stack.add(
      null,
      msg,
      Notifique.Styles.MESSAGE, true);
    stack.add(
      null,
      "Welcome! This is a demo application for the <a href=\"http://vaadin.com/addon/notifique\">Notifique</a> add-on for Vaadin.",
      true, Notifique.Styles.BROWSER_CHROME, true);

  }

}

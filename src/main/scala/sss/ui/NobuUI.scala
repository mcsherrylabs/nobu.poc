package sss.ui

import akka.actor.{ActorRef, Props}
import com.vaadin.annotations.{Push, Theme, Title}
import com.vaadin.server.VaadinRequest
import com.vaadin.ui._

import scala.concurrent.duration._
import sss.ui.MessageRoll.{Clear, NewMessage}
import sss.ui.reactor.{ComponentEvent, Register, UIEventActor, UIReactor}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
@Theme("sss-console")
@Title("sss console")
@SuppressWarnings(Array("serial"))
@Push
class NobuUI extends UI {


  protected def init(request: VaadinRequest) {

    val uiReactor = UIReactor(this)

    getPage.setTitle("NOBU")
    val tabSheet = new TabSheet()
    tabSheet.setSizeFull()

    val mr = new MessageRoll(uiReactor)
    tabSheet.addTab(mr, "Msgs")
    tabSheet.addTab(TestTab(uiReactor, mr.actorRef), "Test")
    setContent(tabSheet)

  }

}


object TestTab {

  def apply(uiReactor: UIReactor, messageRollRef: ActorRef): Component = {
    val result = new HorizontalLayout
    val msgTxt = new TextArea()

    msgTxt.setRows(5)
    msgTxt.setWordwrap(true)

    val btn = new Button("Send", uiReactor)
    val repeatBtn = new Button("Send Lots", uiReactor)
    val loadBtn = new Button("Send Fast", uiReactor)
    val clearBtn = new Button("Clear", uiReactor)

    result.addComponents(repeatBtn, btn, loadBtn, msgTxt, clearBtn)

    messageRollRef ! NewMessage("One to be going on with...")

    object BtnReactor extends UIEventActor {
      override def react(reactor: ActorRef, broadcaster: ActorRef, ui: UI): BtnReactor.Receive = {
        case ComponentEvent(`clearBtn`, _) =>  messageRollRef ! Clear
        case ComponentEvent(`loadBtn`, _) =>
          (0 to 110).foreach(i => messageRollRef ! NewMessage(s" $i ${msgTxt.getValue}"))

        case ComponentEvent(`btn`, _) => messageRollRef ! NewMessage(msgTxt.getValue)
        case ComponentEvent(`repeatBtn`, _) => {
          messageRollRef ! NewMessage(msgTxt.getValue)
          context.system.scheduler.scheduleOnce(2 seconds, messageRollRef , NewMessage(msgTxt.getValue))
          context.system.scheduler.scheduleOnce(4 seconds, messageRollRef , NewMessage(msgTxt.getValue))
          context.system.scheduler.scheduleOnce(6 seconds, messageRollRef , NewMessage(msgTxt.getValue))
          context.system.scheduler.scheduleOnce(8 seconds, messageRollRef , NewMessage(msgTxt.getValue))
        }
      }
    }

    uiReactor.actorOf(Props(BtnReactor), btn, repeatBtn, loadBtn, clearBtn)

    result
  }
}


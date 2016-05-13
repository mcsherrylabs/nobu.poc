package sss.ui

import akka.actor.{ActorRef, Props}
import com.vaadin.server.ExternalResource
import com.vaadin.shared.ui.label.ContentMode
import com.vaadin.ui._
import com.vaadin.ui.themes.ValoTheme
import org.vaadin.notifique.Notifique
import org.vaadin.teemu.VaadinIcons
import sss.ui.reactor.{ComponentEvent, Event, ListenTo, Register, UIEventActor, UIReactor}

import scala.util.{Failure, Try}

/**
  * Created by alan on 5/13/16.
  */


object MessageRoll {

  val category = "sss.ui.msgroll"
  case class NewMessage(msg: String) extends Event {  val category = MessageRoll.category }
  case object Clear
}

class MessageRoll(uiReactor: UIReactor) extends HorizontalLayout {

  import MessageRoll._

  private val stack = new Notifique(false)
  setMargin(false)
  setSizeFull
  stack.setVisibleCount(100)

  addComponent(stack)

  object RollReactor extends UIEventActor {

    def createCloseButton: Button = {
      val b: Button = new Button
      b.setStyleName(ValoTheme.BUTTON_LINK)
      b.addStyleName(ValoTheme.BUTTON_TINY)
      b.setIcon(VaadinIcons.CLOSE)

      b
    }

    def createControlBar(components: Component*): Component = {
      val controlBar = new HorizontalLayout()
      controlBar.setSizeFull
      controlBar.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT)
      controlBar.addComponents(components : _*)
      new Panel(controlBar)
    }

    def createMessageFrame: AbstractOrderedLayout = {
      val msgContent = new VerticalLayout()
      msgContent.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
      msgContent
    }

    def createImageHolder(img: String): Component = {
      val resource = new ExternalResource(img)
      val image = new Image(null,resource)
      image.setStyleName("msg-roll-img")
      image
    }

    def createTextHolder(msg:String): Component = {
      val label = new Label(msg, ContentMode.TEXT)
      label.setEnabled(false)
      label
    }

    override def react(reactor: ActorRef, broadcaster: ActorRef, ui: UI): Receive = {

      case Clear => push(stack.clear())

      case ComponentEvent(btn : Button, _) => Try {
        push(btn.getData.asInstanceOf[Notifique#Message].hide)
      } match {
        case Failure(e) => println(e)
        case _ => println("ok")
      }

      case NewMessage(msg) =>

        val frame = createMessageFrame
        val btn = createCloseButton
        btn.addClickListener(uiReactor)
        self ! ListenTo(btn)
        val controlPanel = createControlBar(btn)
        frame.addComponent(controlPanel)
        frame.setComponentAlignment(controlPanel, Alignment.MIDDLE_RIGHT)
        //frame.addComponent(createImageHolder("https://i.ytimg.com/vi/tntOCGkgt98/maxresdefault.jpg"))
        frame.addComponent(createTextHolder(msg))
        push {
          val msg = stack.add(null, frame, Notifique.Styles.BROWSER_CHROME, false)
          btn.setData(msg)
        }


    }
  }

  val actorRef = uiReactor actorOf (Props(RollReactor))

  actorRef ! Register(category)

}

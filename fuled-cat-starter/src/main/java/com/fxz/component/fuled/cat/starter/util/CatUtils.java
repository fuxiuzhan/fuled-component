package com.fxz.component.fuled.cat.starter.util;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.internal.AbstractMessage;
import com.dianping.cat.message.internal.NullMessage;
import org.springframework.web.context.request.RequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * @author fxz
 */
public class CatUtils {
    public static void createMessageTree() {
        CatPropertyContext context = new CatPropertyContext();
        Cat.logRemoteCallClient(context, Cat.getManager().getDomain());
        CatTraceCarrier.Context contextCarrier = new CatTraceCarrier.Context();
        contextCarrier.setParentTrace(context.getProperty(Cat.Context.PARENT));
        contextCarrier.setRootTrace(context.getProperty(Cat.Context.ROOT));
        contextCarrier.setChildTrace(context.getProperty(Cat.Context.CHILD));
        contextCarrier.setAppName(Cat.getManager().getDomain());
        CatTraceCarrier.setContext(contextCarrier);
    }

    public static void createConsumerCross(Transaction transaction, String callApp, String callServer, String callPort) {
        Event crossOwnerEvent = Cat.newEvent("RemoteCall.ownerIp", getLocalHost());
        Event crossAppEvent = Cat.newEvent("RemoteCall.providerApp", callApp);
        Event crossServerEvent = Cat.newEvent("RemoteCall.providerServer", callServer);
        Event crossPortEvent = Cat.newEvent("RemoteCall.providerPort", callPort);
        serviceCrossBuilder(transaction, crossOwnerEvent, crossAppEvent, crossServerEvent, crossPortEvent);
    }

    public static void createProviderCross(HttpServletRequest request, Transaction t) {
        Event crossOwnerEvent = Cat.newEvent("ServiceProvider.ownerIp", getLocalHost());
        Event crossAppEvent = Cat.newEvent("ServiceProvider.clientApp", request.getHeader("application.name"));
        Event crossServerEvent = Cat.newEvent("ServiceProvider.clientIp", request.getRemoteAddr());
        serviceCrossBuilder(t, crossOwnerEvent, crossAppEvent, crossServerEvent);
    }

    private static void serviceCrossBuilder(Transaction transaction, Event... events) {
        for (int i = 0; i < events.length; ++i) {
            Event event = events[i];
            event.setStatus("0");
            completeEvent(event);
            transaction.addChild(event);
        }
    }

    public static void completeEvent(Event event) {
        if (event != NullMessage.EVENT) {
            AbstractMessage message = (AbstractMessage) event;
            message.setCompleted(true);
        }
    }

    public static String getLocalHost() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException var1) {
            return "";
        }
    }

    public static String getRootId(RequestAttributes requestAttributes) {
        if (!RequestAttributesUtil.isRequestActive(requestAttributes)) {
            return "";
        } else {
            Object rootObject = requestAttributes.getAttribute(Cat.Context.ROOT, 0);
            return Objects.isNull(rootObject) ? "" : rootObject.toString();
        }
    }

    public static String getChildId(RequestAttributes requestAttributes) {
        if (!RequestAttributesUtil.isRequestActive(requestAttributes)) {
            return "";
        } else {
            Object childObject = requestAttributes.getAttribute(Cat.Context.CHILD, 0);
            return Objects.isNull(childObject) ? "" : childObject.toString();
        }
    }

    public static String getParentId(RequestAttributes requestAttributes) {
        if (!RequestAttributesUtil.isRequestActive(requestAttributes)) {
            return "";
        } else {
            Object parentObject = requestAttributes.getAttribute(Cat.Context.PARENT, 0);
            return Objects.isNull(parentObject) ? "" : parentObject.toString();
        }
    }
}

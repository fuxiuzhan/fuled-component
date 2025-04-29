package com.fxz.component.fuled.cat.starter.component.threadpool;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.spi.MessageTree;
import com.fxz.component.fuled.cat.starter.util.CatPropertyContext;

import java.util.concurrent.Callable;

/**
 *
 */
public class CallableTracedWrapper implements Callable {

    private Callable callable;

    private CatPropertyContext context;

    private Transaction transaction;

    private String threadPoolName;

    public CallableTracedWrapper(Callable callable, String threadPoolName) {
        this.callable = callable;
        this.threadPoolName = threadPoolName;
        Transaction t = Cat.newTransaction("CrossThreadPool", threadPoolName);
        context = new CatPropertyContext();
        Cat.logRemoteCallClient(context, Cat.getManager().getDomain());
        t.setStatus(Transaction.SUCCESS);
        t.complete();
    }

    @Override
    public Object call() throws Exception {
        try {
            Cat.getManager().setup();
            MessageTree tree = Cat.getManager().getThreadLocalMessageTree();
            String childId = context.getProperty(Cat.Context.CHILD);
            String rootId = context.getProperty(Cat.Context.ROOT);
            String parentId = context.getProperty(Cat.Context.PARENT);
            if (parentId != null) {
                tree.setParentMessageId(parentId);
            }
            if (rootId != null) {
                tree.setRootMessageId(rootId);
            }
            if (childId != null) {
                tree.setMessageId(childId);
            }
            transaction = Cat.newTransaction("CallableExecute", threadPoolName);
            Cat.logEvent("CallableBeforeExecute", Thread.currentThread().getName());
            Object call = callable.call();
            Cat.logEvent("CallableAfterExecute", Thread.currentThread().getName());
            transaction.setStatus(Transaction.SUCCESS);
            transaction.complete();
            return call;
        } catch (Exception e) {
            Cat.logEvent("CallableOnException", Thread.currentThread().getName());
            transaction.setStatus(e);
            transaction.complete();
            throw e;
        }
    }
}

package com.fxz.component.fuled.cat.starter.component.threadpool;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.spi.MessageTree;
import com.fxz.component.fuled.cat.starter.util.CatPropertyContext;

public class RunnableTracedWrapper implements Runnable {

    private Runnable runnable;
    private CatPropertyContext context;
    private Transaction transaction;
    private String threadPoolName;

    public RunnableTracedWrapper(Runnable runnable, String threadPoolName) {
        this.runnable = runnable;
        this.threadPoolName = threadPoolName;
        Transaction t = Cat.newTransaction("CrossThreadPool", threadPoolName);
        context = new CatPropertyContext();
        Cat.logRemoteCallClient(context, Cat.getManager().getDomain());
        t.setStatus(Transaction.SUCCESS);
        t.complete();
    }

    @Override
    public void run() {
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
            transaction = Cat.newTransaction("RunnableExecute", threadPoolName);
            Cat.logEvent("RunnableBeforeExecute", Thread.currentThread().getName());
            runnable.run();
            Cat.logEvent("RunnableAfterExecute", Thread.currentThread().getName());
            transaction.setStatus(Transaction.SUCCESS);
            transaction.complete();
        } catch (Exception e) {
            Cat.logEvent("RunnableOnException", Thread.currentThread().getName());
            transaction.setStatus(e);
            transaction.complete();
            throw e;
        }
    }
}

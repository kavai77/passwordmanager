package net.himadri.passwordmanager.service;

import com.google.appengine.api.datastore.ReadPolicy;
import com.google.appengine.api.datastore.Transaction;
import com.googlecode.objectify.*;
import com.googlecode.objectify.cmd.Deferred;
import com.googlecode.objectify.cmd.Deleter;
import com.googlecode.objectify.cmd.Loader;
import com.googlecode.objectify.cmd.Saver;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by himadri on 2016. 10. 31..
 */
class ObjectifyDelegate implements Objectify {
    @Override
    public Loader load() {
        return ofy().load();
    }

    @Override
    public Saver save() {
        return ofy().save();
    }

    @Override
    public Deleter delete() {
        return ofy().delete();
    }

    @Override
    public Deferred defer() {
        return ofy().defer();
    }

    @Override
    public ObjectifyFactory factory() {
        return ofy().factory();
    }

    @Override
    public Objectify consistency(ReadPolicy.Consistency consistency) {
        return ofy().consistency(consistency);
    }

    @Override
    public Objectify deadline(Double aDouble) {
        return ofy().deadline(aDouble);
    }

    @Override
    public Objectify cache(boolean b) {
        return ofy().cache(b);
    }

    @Override
    public Transaction getTransaction() {
        return ofy().getTransaction();
    }

    @Override
    public Objectify transactionless() {
        return ofy().transactionless();
    }

    @Override
    public <R> R transact(Work<R> work) {
        return ofy().transact(work);
    }

    @Override
    public void transact(Runnable runnable) {
        ofy().transact(runnable);
    }

    @Override
    public <R> R transactNew(Work<R> work) {
        return ofy().transactNew(work);
    }

    @Override
    public <R> R transactNew(int i, Work<R> work) {
        return ofy().transactNew(i, work);
    }

    @Override
    public <R> R execute(TxnType txnType, Work<R> work) {
        return ofy().execute(txnType, work);
    }

    @Override
    public void flush() {
        ofy().flush();
    }

    @Override
    public void clear() {
        ofy().clear();
    }

    @Override
    public boolean isLoaded(Key<?> key) {
        return ofy().isLoaded(key);
    }
}

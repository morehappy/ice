// **********************************************************************
//
// Copyright (c) 2003-2008 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

import Demo.*;

class SessionI implements _SessionOperations, _Glacier2SessionOperations
{
    synchronized public LibraryPrx
    getLibrary(Ice.Current c)
    {
        if(_destroyed)
        {
            throw new Ice.ObjectNotExistException();
        }
        return _library;
    }

    synchronized public void
    refresh(Ice.Current c)
    {
        if(_destroyed)
        {
            throw new Ice.ObjectNotExistException();
        }
        _timestamp = System.currentTimeMillis();
    }

    synchronized public void
    destroy(Ice.Current c)
    {
        if(_destroyed)
        {
            throw new Ice.ObjectNotExistException();
        }

        _destroyed = true;
        _logger.trace("Session", "session " + c.adapter.getCommunicator().identityToString(c.id) +
                      " is now destroyed.");

        java.util.Iterator<QueryProxyPair> p = _queries.iterator();
        while(p.hasNext())
        {
            try
            {
                p.next().proxy.destroy();
            }
            catch(Ice.ObjectNotExistException e)
            {
                // Ignore, it could have already been destroyed.
            }
        }

        // This method is never called on shutdown of the server.
        c.adapter.remove(_library.ice_getIdentity());
        c.adapter.remove(c.id);
    }

    // Called on application shutdown.
    synchronized public void
    shutdown()
    {
        if(!_destroyed)
        {
            _destroyed = true;

            // Shutdown each of the associated query objects.
            java.util.Iterator<QueryProxyPair> p = _queries.iterator();
            while(p.hasNext())
            {
                p.next().impl.shutdown();
            }
        }
    }

    synchronized public long
    timestamp()
    {
        if(_destroyed)
        {
            throw new Ice.ObjectNotExistException();
        }
        return _timestamp;
    }

    synchronized public void
    add(BookQueryResultPrx proxy, BookQueryResultI impl)
    {
        // If the session has been destroyed, then destroy the book
        // result, and raise an ObjectNotExistException.
        if(_destroyed)
        {
            proxy.destroy();
            throw new Ice.ObjectNotExistException();
        }
        _queries.add(new QueryProxyPair(proxy, impl));
    }

    synchronized public void
    reapQueries()
    {
        if(_destroyed)
        {
            throw new Ice.ObjectNotExistException();
        }

        java.util.Iterator<QueryProxyPair> p = _queries.iterator();
        while(p.hasNext())
        {
            QueryProxyPair pair = p.next();
            try
            {
                pair.proxy.ice_ping();
            }
            catch(Ice.ObjectNotExistException e)
            {
                p.remove();
            }
        }
    }

    SessionI(Ice.Logger logger, Ice.ObjectAdapter adapter)
    {
        _logger = logger;
        _timestamp = System.currentTimeMillis();
        _library = LibraryPrxHelper.uncheckedCast(adapter.addWithUUID(new DispatchInterceptorI(new LibraryI(this))));
    }

    static class QueryProxyPair
    {
        QueryProxyPair(BookQueryResultPrx p, BookQueryResultI i)
        {
            proxy = p;
            impl = i;
        }

        BookQueryResultPrx proxy;
        BookQueryResultI impl;
    }

    private java.util.List<QueryProxyPair> _queries = new java.util.LinkedList<QueryProxyPair>();
    private Ice.Logger _logger;
    private boolean _destroyed = false; // true if destroy() was called, false otherwise.
    private long _timestamp; // The last time the session was refreshed.
    private LibraryPrx _library;
}

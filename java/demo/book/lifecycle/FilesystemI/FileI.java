// **********************************************************************
//
// Copyright (c) 2003-2009 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

package FilesystemI;

import Ice.*;
import Filesystem.*;
import FilesystemI.*;

public class FileI extends _FileDisp implements NodeI, _FileOperations
{
    public synchronized String
    name(Current c)
    {
        if(_destroyed)
        {
            throw new ObjectNotExistException();
        }
        return _name;
    }

    public Identity
    id()
    {
        return _id;
    }

    public synchronized String[]
    read(Current c)
    {
        if(_destroyed)
        {
            throw new ObjectNotExistException();
        }

        return _lines;
    }

    public synchronized void
    write(String[] text, Current c)
    {
        if(_destroyed)
        {
            throw new ObjectNotExistException();
        }

        _lines = (String[])text.clone();
    }

    public synchronized void
    destroy(Current c)
    {
        if(_destroyed)
        {
            throw new ObjectNotExistException();
        }
        _destroyed = true;

        c.adapter.remove(id());
        _parent.removeEntry(_name);
    }

    public FileI(String name, DirectoryI parent)
    {
        _name = name;
        _parent = parent;
        _destroyed = false;
        _id = new Identity();
        _id.name = Util.generateUUID();
    }

    private String _name;
    private DirectoryI _parent;
    private boolean _destroyed;
    private Identity _id;
    private String[] _lines;
}

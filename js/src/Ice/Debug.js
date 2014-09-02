// **********************************************************************
//
// Copyright (c) 2003-2014 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

var Ice = require("../Ice/ModuleRegistry").Ice;
Ice.Debug =
{
    assert: function(b, msg)
    {
        if(!b)
        {
            console.log(msg === undefined ? "assertion failed" : msg);
            console.log(Error().stack);
            process.exit(1);
        }
    }
};
module.exports.Ice = Ice;

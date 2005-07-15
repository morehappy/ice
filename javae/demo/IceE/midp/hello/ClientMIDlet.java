// **********************************************************************
//
// Copyright (c) 2003-2005 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

import Demo.*;

public class ClientMIDlet
    extends javax.microedition.midlet.MIDlet
    implements javax.microedition.lcdui.CommandListener
{
    private javax.microedition.lcdui.Form _form;
    private javax.microedition.lcdui.Display _display;

    private Ice.Communicator _communicator;
    private Demo.HelloPrx _helloPrx;

    private static final int CMD_PRIORITY = 1;

    private static final javax.microedition.lcdui.Command CMD_EXIT =
        new javax.microedition.lcdui.Command("Exit", javax.microedition.lcdui.Command.EXIT, CMD_PRIORITY);

    private static final javax.microedition.lcdui.Command CMD_HELLO =
        new javax.microedition.lcdui.Command("Hello", javax.microedition.lcdui.Command.ITEM, CMD_PRIORITY);

    protected void
    startApp()
    {
	java.io.InputStream is = getClass().getResourceAsStream("config");
	Ice.Properties properties = Ice.Util.createProperties();
	properties.load(is);
	_communicator = Ice.Util.initializeWithProperties(new String[0], properties);

	if(_display == null)
	{
	    _display = javax.microedition.lcdui.Display.getDisplay(this);
	    _form = new javax.microedition.lcdui.Form("Ice - Hello World");
	    _form.addCommand(CMD_EXIT);
	    _form.addCommand(CMD_HELLO);
	    _form.setCommandListener(this);
	}
	_display.setCurrent(_form);
    }

    protected void
    pauseApp()
    {
	if(_communicator != null)
	{
	    try
	    {
		_communicator.destroy();
		_communicator = null;
	    }
	    catch(Exception ex)
	    {
	    }
	}
    }

    protected void
    destroyApp(boolean unconditional)
    {
	if(_communicator != null)
	{
	    try
	    {
		_communicator.destroy();
		_communicator = null;
	    }
	    catch(Exception ex)
	    {
	    }
	}
    }

    public void
    commandAction(javax.microedition.lcdui.Command cmd, javax.microedition.lcdui.Displayable source)
    {
	if(source == _form)
	{
	    if(cmd == CMD_EXIT)
	    {
		handleExitCmd();
	    }
	    else if(cmd == CMD_HELLO)
	    {
		handleHelloCmd();
	    }
	}
    }

    public void
    handleHelloCmd()
    {
	if(_helloPrx == null)
	{
	    Ice.Properties properties = _communicator.getProperties();
	    String proxy = properties.getProperty("Hello.Proxy");
	    if(proxy == null || proxy.length() == 0)
	    {
		//
		// TODO: Display an error message and back out.
		//
	    }
	    Ice.ObjectPrx base = _communicator.stringToProxy(proxy);
	    _helloPrx = HelloPrxHelper.checkedCast(base);
	}

	_helloPrx.sayHello();
    }

    public void
    handleExitCmd()
    {
	destroyApp(true);
	notifyDestroyed();
    }

    public javax.microedition.lcdui.Form
    getForm()
    {
	return _form;
    }
}

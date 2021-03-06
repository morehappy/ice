// **********************************************************************
//
// Copyright (c) 2003-2016 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

#include <IceBT/PluginI.h>
#include <IceBT/EndpointI.h>
#include <IceBT/Engine.h>
#include <IceBT/Instance.h>
#include <IceBT/Util.h>

#include <Ice/LocalException.h>
#include <Ice/ProtocolPluginFacade.h>
#include <Ice/ProtocolInstance.h>

using namespace std;
using namespace Ice;
using namespace IceBT;

void
IceBT::BluetoothException::ice_print(ostream& out) const
{
    Exception::ice_print(out);
    out << ":\nbluetooth exception: `" << reason << "'";
}

//
// Plug-in factory function.
//
extern "C"
{

ICE_BT_API Ice::Plugin*
createIceBT(const CommunicatorPtr& communicator, const string& /*name*/, const StringSeq& /*args*/)
{
    return new PluginI(communicator);
}

}

namespace Ice
{

ICE_BT_API void
registerIceBT(bool loadOnInitialize)
{
    Ice::registerPluginFactory("IceBT", createIceBT, loadOnInitialize);
}

}

//
// Plugin implementation.
//
IceBT::PluginI::PluginI(const Ice::CommunicatorPtr& com) :
    _engine(new Engine(com))
{
    IceInternal::ProtocolPluginFacadePtr pluginFacade = IceInternal::getProtocolPluginFacade(com);

    //
    // Register the endpoint factory. We have to do this now, rather
    // than in initialize, because the communicator may need to
    // interpret proxies before the plug-in is fully initialized.
    //
    pluginFacade->addEndpointFactory(new EndpointFactoryI(new Instance(_engine, BTEndpointType, "bt")));

    IceInternal::EndpointFactoryPtr sslFactory = pluginFacade->getEndpointFactory(SSLEndpointType);
    if(sslFactory)
    {
        InstancePtr instance = new Instance(_engine, BTSEndpointType, "bts");
        pluginFacade->addEndpointFactory(sslFactory->clone(instance, new EndpointFactoryI(instance)));
    }
}

void
IceBT::PluginI::initialize()
{
    _engine->initialize();
}

void
IceBT::PluginI::destroy()
{
    _engine->destroy();
}

void
#ifdef ICE_CPP11_MAPPING
IceBT::PluginI::startDiscovery(const string& address, function<void(const string&, const PropertyMap&)> cb)
#else
IceBT::PluginI::startDiscovery(const string& address, const DiscoveryCallbackPtr& cb)
#endif
{
    _engine->startDiscovery(address, cb);
}

void
IceBT::PluginI::stopDiscovery(const string& address)
{
    _engine->stopDiscovery(address);
}

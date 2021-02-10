package com.csdk.socket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Create LuckMerlin
 * Date 10:26 2020/12/22
 * TODO
 */
class ManagerHolder {
    private volatile Map<ConnectionInfo, IConnectionManager> mConnectionManagerMap;
    private volatile Map<Integer, IServerManagerPrivate> mServerManagerMap;

    public static ManagerHolder getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private ManagerHolder() {
        this.mConnectionManagerMap = new HashMap();
        this.mServerManagerMap = new HashMap();
        this.mConnectionManagerMap.clear();
    }

    public IServerManager getServer(int localPort) {
        IServerManagerPrivate manager = (IServerManagerPrivate)this.mServerManagerMap.get(localPort);
        if (manager == null) {
            manager = (IServerManagerPrivate)SPIUtils.load(IServerManager.class);
            if (manager == null) {
                String err = "Oksocket.Server() load error. Server plug-in are required! For details link to https://github.com/xuuhaoo/OkSocket";
                SLog.e(err);
                throw new IllegalStateException(err);
            } else {
                synchronized(this.mServerManagerMap) {
                    this.mServerManagerMap.put(localPort, manager);
                }

                manager.initServerPrivate(localPort);
                return manager;
            }
        } else {
            return manager;
        }
    }

    public IConnectionManager getConnection(ConnectionInfo info) {
        IConnectionManager manager = (IConnectionManager)this.mConnectionManagerMap.get(info);
        return manager == null ? this.getConnection(info, OkSocketOptions.getDefault()) : this.getConnection(info, manager.getOption());
    }

    public IConnectionManager getConnection(ConnectionInfo info, OkSocketOptions okOptions) {
        IConnectionManager manager = (IConnectionManager)this.mConnectionManagerMap.get(info);
        if (manager != null) {
            if (!okOptions.isConnectionHolden()) {
                synchronized(this.mConnectionManagerMap) {
                    this.mConnectionManagerMap.remove(info);
                }

                return this.createNewManagerAndCache(info, okOptions);
            } else {
                manager.option(okOptions);
                return manager;
            }
        } else {
            return this.createNewManagerAndCache(info, okOptions);
        }
    }

    private IConnectionManager createNewManagerAndCache(ConnectionInfo info, OkSocketOptions okOptions) {
        AbsConnectionManager manager = new ConnectionManagerImpl(info);
        manager.option(okOptions);
        manager.setOnConnectionSwitchListener(new IConnectionSwitchListener() {
            public void onSwitchConnectionInfo(IConnectionManager manager, ConnectionInfo oldInfo, ConnectionInfo newInfo) {
                synchronized(ManagerHolder.this.mConnectionManagerMap) {
                    ManagerHolder.this.mConnectionManagerMap.remove(oldInfo);
                    ManagerHolder.this.mConnectionManagerMap.put(newInfo, manager);
                }
            }
        });
        synchronized(this.mConnectionManagerMap) {
            this.mConnectionManagerMap.put(info, manager);
            return manager;
        }
    }

    protected List<IConnectionManager> getList() {
        List<IConnectionManager> list = new ArrayList();
        Map<ConnectionInfo, IConnectionManager> map = new HashMap(this.mConnectionManagerMap);
        Iterator it = map.keySet().iterator();

        while(it.hasNext()) {
            ConnectionInfo info = (ConnectionInfo)it.next();
            IConnectionManager manager = (IConnectionManager)map.get(info);
            if (!manager.getOption().isConnectionHolden()) {
                it.remove();
            } else {
                list.add(manager);
            }
        }

        return list;
    }

    private static class InstanceHolder {
        private static final ManagerHolder INSTANCE = new ManagerHolder();

        private InstanceHolder() {
        }
    }
}

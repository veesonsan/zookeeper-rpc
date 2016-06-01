package com.iot.chinamobile.rpc.thrift;

import java.net.InetSocketAddress;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.TServiceClientFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.iot.chinamobile.rpc.thrift.zookeeper.ThriftServerAddressProvider;

/**
 * 连接池,thrift-client for spring
 */
public class ThriftClientPoolFactory extends BasePoolableObjectFactory<TServiceClient> {

	private final ThriftServerAddressProvider serverAddressProvider;
	private final TServiceClientFactory<TServiceClient> clientFactory;
	private PoolOperationCallBack callback;

	protected ThriftClientPoolFactory(ThriftServerAddressProvider addressProvider, TServiceClientFactory<TServiceClient> clientFactory) throws Exception {
		this.serverAddressProvider = addressProvider;
		this.clientFactory = clientFactory;
	}

	protected ThriftClientPoolFactory(ThriftServerAddressProvider addressProvider, TServiceClientFactory<TServiceClient> clientFactory,
			PoolOperationCallBack callback) throws Exception {
		this.serverAddressProvider = addressProvider;
		this.clientFactory = clientFactory;
		this.callback = callback;
	}

	static interface PoolOperationCallBack {
		// 销毁client之前执行
		void destroy(TServiceClient client);

		// 创建成功时执行
		void make(TServiceClient client);
	}

	public void destroyObject(TServiceClient client) throws Exception {
		if (callback != null) {
			try {
				callback.destroy(client);
			} catch (Exception e) {
				//
			}
		}
		TTransport pin = client.getInputProtocol().getTransport();
		pin.close();
	}

	public boolean validateObject(TServiceClient client) {
		TTransport pin = client.getInputProtocol().getTransport();
		return pin.isOpen();
	}

	@Override
	public TServiceClient makeObject() throws Exception {
		InetSocketAddress address = serverAddressProvider.selector();
		if (address == null) {
			throw new ThriftException("no service available");
		}
		TSocket tsocket = new TSocket(address.getHostName(), address.getPort());
		TTransport transport = new TFramedTransport(tsocket);
		TProtocol protocol = new TBinaryProtocol(transport);
		TServiceClient client = this.clientFactory.getClient(protocol);
		transport.open();
		if (callback != null) {
			try {
				callback.make(client);
			} catch (Exception e) {
				//
			}
		}
		return client;
	}

}

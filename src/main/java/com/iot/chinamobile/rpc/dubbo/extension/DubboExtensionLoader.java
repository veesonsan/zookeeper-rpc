package com.iot.chinamobile.rpc.dubbo.extension;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.ProxyFactory;
import com.iot.chinamobile.rpc.dubbo.client.DubboClientFilter;
import com.iot.chinamobile.rpc.dubbo.provider.DubboServiceFactory;

public class DubboExtensionLoader {

	@SuppressWarnings({ "unchecked", "rawtypes", "deprecation" })
	public static void loadExtension() {
		ExtensionLoader providerExtensionLoader = ExtensionLoader.getExtensionLoader(ProxyFactory.class);
		providerExtensionLoader.replaceExtension(DubboServiceFactory.EXTENSION_NAME, DubboServiceFactory.class);
		
		ExtensionLoader consumerExtensionLoader = ExtensionLoader.getExtensionLoader(Filter.class);
		consumerExtensionLoader.replaceExtension(DubboClientFilter.EXTENSION_NAME, DubboClientFilter.class);
	}
	
}

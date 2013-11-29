package org.osgi.demo.mysimpleservice.impl;

import org.osgi.demo.mysimpleservice.IMySimpleService;

public class MySimpleServiceImpl implements IMySimpleService {

	@Override
	public void sayHello() {
		System.out.println("Hello!");
	}
}

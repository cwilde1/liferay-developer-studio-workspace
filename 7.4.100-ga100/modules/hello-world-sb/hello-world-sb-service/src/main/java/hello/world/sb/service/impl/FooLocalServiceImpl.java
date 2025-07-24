/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package hello.world.sb.service.impl;

import com.liferay.portal.aop.AopService;

import hello.world.sb.service.base.FooLocalServiceBaseImpl;

import org.osgi.service.component.annotations.Component;
import hello.world.sb.model.Foo;
import hello.world.sb.service.FooLocalService;
import hello.world.sb.service.persistence.FooPersistence;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = "model.class.name=hello.world.sb.model.Foo",
	service = AopService.class
)
public class FooLocalServiceImpl extends FooLocalServiceBaseImpl {


	public Foo addFoo(String name) {
	     long fooId = counterLocalService.increment();
	     Foo foo = createFoo(fooId);
	     //foo.setName(name);
	     return updateFoo(foo);
	 }

	public Foo getFoo(long fooId) {
	     return fooPersistence.fetchByPrimaryKey(fooId);
	 }

}
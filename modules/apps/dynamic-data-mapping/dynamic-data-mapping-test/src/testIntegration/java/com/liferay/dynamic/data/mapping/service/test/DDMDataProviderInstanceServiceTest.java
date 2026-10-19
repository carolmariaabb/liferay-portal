/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.model.DDMDataProviderInstance;
import com.liferay.dynamic.data.mapping.model.DDMForm;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceLocalService;
import com.liferay.dynamic.data.mapping.service.DDMDataProviderInstanceService;
import com.liferay.dynamic.data.mapping.storage.DDMFormValues;
import com.liferay.dynamic.data.mapping.test.util.DDMFormTestUtil;
import com.liferay.dynamic.data.mapping.test.util.DDMFormValuesTestUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Carolina Barbosa
 */
@RunWith(Arquillian.class)
public class DDMDataProviderInstanceServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_ddmDataProviderInstance = _addDDMDataProviderInstance();

		Role guestRole = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(), RoleConstants.GUEST);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(),
			DDMDataProviderInstance.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(_ddmDataProviderInstance.getDataProviderInstanceId()),
			guestRole.getRoleId(), new String[] {ActionKeys.VIEW});

		_user = UserTestUtil.addUser();

		_originalName = PrincipalThreadLocal.getName();
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
	}

	@After
	public void tearDown() {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		PrincipalThreadLocal.setName(_originalName);
	}

	@Test
	public void testGettersKeepCredentialsForUserWithUpdatePermission()
		throws Exception {

		_assertCredentials(
			_ddmDataProviderInstanceService.getDataProviderInstance(
				_ddmDataProviderInstance.getDataProviderInstanceId()));
		_assertCredentials(
			_ddmDataProviderInstanceService.fetchDataProviderInstance(
				_ddmDataProviderInstance.getDataProviderInstanceId()));
	}

	@Test
	public void testGettersRemoveCredentialsForUserWithViewPermission()
		throws Exception {

		_setUser(_user);

		long dataProviderInstanceId =
			_ddmDataProviderInstance.getDataProviderInstanceId();

		_assertNoCredentials(
			_ddmDataProviderInstanceService.getDataProviderInstance(
				dataProviderInstanceId));
		_assertNoCredentials(
			_ddmDataProviderInstanceService.getDataProviderInstanceByUuid(
				_ddmDataProviderInstance.getUuid()));
		_assertNoCredentials(
			_ddmDataProviderInstanceService.fetchDataProviderInstance(
				dataProviderInstanceId));
		_assertNoCredentials(
			_ddmDataProviderInstanceService.fetchDataProviderInstanceByUuid(
				_ddmDataProviderInstance.getUuid()));

		long[] groupIds = {_group.getGroupId()};

		List<DDMDataProviderInstance> ddmDataProviderInstances =
			_ddmDataProviderInstanceService.getDataProviderInstances(
				TestPropsValues.getCompanyId(), groupIds, -1, -1);

		Assert.assertEquals(
			ddmDataProviderInstances.toString(), 1,
			ddmDataProviderInstances.size());

		_assertNoCredentials(ddmDataProviderInstances.get(0));

		ddmDataProviderInstances = _ddmDataProviderInstanceService.search(
			TestPropsValues.getCompanyId(), groupIds, null, null, false, -1, -1,
			null);

		Assert.assertEquals(
			ddmDataProviderInstances.toString(), 1,
			ddmDataProviderInstances.size());

		_assertNoCredentials(ddmDataProviderInstances.get(0));

		ddmDataProviderInstances = _ddmDataProviderInstanceService.search(
			TestPropsValues.getCompanyId(), groupIds, null, -1, -1, null);

		Assert.assertEquals(
			ddmDataProviderInstances.toString(), 1,
			ddmDataProviderInstances.size());

		_assertNoCredentials(ddmDataProviderInstances.get(0));
	}

	@Test
	public void testRemovingCredentialsDoesNotAffectStoredDefinition()
		throws Exception {

		_setUser(_user);

		_assertNoCredentials(
			_ddmDataProviderInstanceService.getDataProviderInstance(
				_ddmDataProviderInstance.getDataProviderInstanceId()));

		_assertCredentials(
			_ddmDataProviderInstanceLocalService.getDataProviderInstance(
				_ddmDataProviderInstance.getDataProviderInstanceId()));
	}

	private DDMDataProviderInstance _addDDMDataProviderInstance()
		throws Exception {

		Map<Locale, String> nameMap = HashMapBuilder.put(
			LocaleUtil.getSiteDefault(), StringUtil.randomString()
		).build();

		DDMForm ddmForm = DDMFormTestUtil.createDDMForm(
			"url", "username", "password");

		DDMFormValues ddmFormValues = DDMFormValuesTestUtil.createDDMFormValues(
			ddmForm);

		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"url", "https://example.com/rest"));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"username", _USERNAME));
		ddmFormValues.addDDMFormFieldValue(
			DDMFormValuesTestUtil.createUnlocalizedDDMFormFieldValue(
				"password", _PASSWORD));

		return _ddmDataProviderInstanceLocalService.addDataProviderInstance(
			TestPropsValues.getUserId(), _group.getGroupId(), nameMap, nameMap,
			ddmFormValues, "rest",
			ServiceContextTestUtil.getServiceContext(
				_group.getGroupId(), TestPropsValues.getUserId()));
	}

	private void _assertCredentials(
		DDMDataProviderInstance ddmDataProviderInstance) {

		String definition = ddmDataProviderInstance.getDefinition();

		Assert.assertTrue(definition, definition.contains(_USERNAME));
		Assert.assertTrue(definition, definition.contains(_PASSWORD));
	}

	private void _assertNoCredentials(
		DDMDataProviderInstance ddmDataProviderInstance) {

		String definition = ddmDataProviderInstance.getDefinition();

		Assert.assertFalse(definition, definition.contains(_USERNAME));
		Assert.assertFalse(definition, definition.contains(_PASSWORD));
		Assert.assertTrue(
			definition, definition.contains("https://example.com/rest"));
	}

	private void _setUser(User user) throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(user));

		PrincipalThreadLocal.setName(user.getUserId());
	}

	private static final String _PASSWORD = "REST-PASSWORD-SECRET";

	private static final String _USERNAME = "REST-USER";

	private DDMDataProviderInstance _ddmDataProviderInstance;

	@Inject
	private DDMDataProviderInstanceLocalService
		_ddmDataProviderInstanceLocalService;

	@Inject
	private DDMDataProviderInstanceService _ddmDataProviderInstanceService;

	@DeleteAfterTestRun
	private Group _group;

	private String _originalName;
	private PermissionChecker _originalPermissionChecker;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@DeleteAfterTestRun
	private User _user;

}

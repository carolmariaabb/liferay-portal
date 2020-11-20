/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.dynamic.data.mapping.form.field.type.internal.document.library;

import com.liferay.document.library.kernel.service.DLAppService;
import com.liferay.dynamic.data.mapping.constants.DDMPortletKeys;
import com.liferay.dynamic.data.mapping.form.field.type.BaseDDMFormFieldTypeSettingsTestCase;
import com.liferay.dynamic.data.mapping.model.DDMFormField;
import com.liferay.dynamic.data.mapping.render.DDMFormFieldRenderingContext;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.model.Repository;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactory;
import com.liferay.portal.kernel.portlet.RequestBackedPortletURLFactoryUtil;
import com.liferay.portal.kernel.portletfilerepository.PortletFileRepository;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletURL;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Html;
import com.liferay.portal.kernel.util.Props;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.util.HtmlImpl;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.hamcrest.CoreMatchers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.api.mockito.expectation.PowerMockitoStubber;
import org.powermock.api.support.membermodification.MemberMatcher;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Pedro Queiroz
 */
@PrepareForTest(RequestBackedPortletURLFactoryUtil.class)
@RunWith(PowerMockRunner.class)
public class DocumentLibraryDDMFormFieldTemplateContextContributorTest
	extends BaseDDMFormFieldTypeSettingsTestCase {

	public HttpServletRequest createHttpServletRequest() {
		MockHttpServletRequest httpServletRequest =
			new MockHttpServletRequest();

		httpServletRequest.setParameter(
			"formInstanceId", String.valueOf(_FORM_INSTANCE_ID));

		return httpServletRequest;
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		setUpDLAppService();
		setUpFileEntry();
		setUpJSONFactory();
		setUpHtml();
		setUpParamUtil();
		setUpPortletFileRepository();
		setUpRequestBackedPortletURLFactoryUtil();
		setUpUserLocalService();
	}

	@Test
	public void testGetParametersShouldContainAllowGuestUsers() {
		DDMFormField ddmFormField = new DDMFormField(
			"field", "document_library");

		ddmFormField.setProperty("allowGuestUsers", true);

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setHttpServletRequest(
			createHttpServletRequest());

		DocumentLibraryDDMFormFieldTemplateContextContributor spy = createSpy();

		Map<String, Object> parameters = spy.getParameters(
			ddmFormField, ddmFormFieldRenderingContext);

		Assert.assertEquals(true, parameters.get("allowGuestUsers"));
	}

	@Test
	public void testGetParametersShouldContainFileEntryURL() {
		DDMFormField ddmFormField = new DDMFormField("field", "numeric");

		DocumentLibraryDDMFormFieldTemplateContextContributor spy = createSpy();

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setHttpServletRequest(
			createHttpServletRequest());
		ddmFormFieldRenderingContext.setProperty("groupId", _GROUP_ID);
		ddmFormFieldRenderingContext.setReadOnly(true);
		ddmFormFieldRenderingContext.setValue(
			"{\"uuid\": \"0000-1111\", \"title\": \"File Title\"}");

		Map<String, Object> parameters = spy.getParameters(
			ddmFormField, ddmFormFieldRenderingContext);

		Assert.assertTrue(parameters.containsKey("fileEntryURL"));
	}

	@Test
	public void testGetParametersShouldContainItemSelectorAuthToken() {
		DDMFormField ddmFormField = new DDMFormField("field", "numeric");

		DocumentLibraryDDMFormFieldTemplateContextContributor spy = createSpy();

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setHttpServletRequest(
			createHttpServletRequest());
		ddmFormFieldRenderingContext.setValue(
			"{\"uuid\": \"0000-1111\", \"title\": \"Title\"}");

		Map<String, Object> parameters = spy.getParameters(
			ddmFormField, ddmFormFieldRenderingContext);

		Assert.assertEquals("token", parameters.get("itemSelectorAuthToken"));
	}

	@Test
	public void testGetParametersShouldContainUploadURL() {
		DDMFormField ddmFormField = new DDMFormField(
			"field", "document_library");

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setHttpServletRequest(
			createHttpServletRequest());
		ddmFormFieldRenderingContext.setProperty("groupId", _GROUP_ID);

		DocumentLibraryDDMFormFieldTemplateContextContributor spy = createSpy();

		Map<String, Object> parameters = spy.getParameters(
			ddmFormField, ddmFormFieldRenderingContext);

		String uploadURL = String.valueOf(parameters.get("uploadURL"));

		Assert.assertThat(
			uploadURL,
			CoreMatchers.containsString(
				"param_javax.portlet.action=/dynamic_data_mapping_form" +
					"/upload_file_entry"));
		Assert.assertThat(
			uploadURL,
			CoreMatchers.containsString("param_folderId=" + _FOLDER_ID));
		Assert.assertThat(
			uploadURL,
			CoreMatchers.containsString(
				"param_formInstanceId=" + _FORM_INSTANCE_ID));
		Assert.assertThat(
			uploadURL,
			CoreMatchers.containsString("param_groupId=" + _GROUP_ID));
	}

	@Test
	public void testGetParametersShouldUseFileEntryTitle() {
		DDMFormField ddmFormField = new DDMFormField("field", "numeric");

		DocumentLibraryDDMFormFieldTemplateContextContributor spy = createSpy();

		DDMFormFieldRenderingContext ddmFormFieldRenderingContext =
			new DDMFormFieldRenderingContext();

		ddmFormFieldRenderingContext.setHttpServletRequest(
			createHttpServletRequest());
		ddmFormFieldRenderingContext.setReadOnly(true);
		ddmFormFieldRenderingContext.setValue(
			"{\"uuid\": \"0000-1111\", \"title\": \"Old Title\"}");

		Map<String, Object> parameters = spy.getParameters(
			ddmFormField, ddmFormFieldRenderingContext);

		Assert.assertEquals("New Title", parameters.get("fileEntryTitle"));
	}

	protected DocumentLibraryDDMFormFieldTemplateContextContributor
		createSpy() {

		DocumentLibraryDDMFormFieldTemplateContextContributor spy =
			PowerMockito.spy(
				_documentLibraryDDMFormFieldTemplateContextContributor);

		PowerMockitoStubber stubber = PowerMockito.doReturn(_resourceBundle);

		stubber.when(
			spy
		).getResourceBundle(
			Matchers.any(Locale.class)
		);

		stubber = PowerMockito.doReturn("token");

		stubber.when(
			spy
		).getItemSelectorAuthToken(
			Matchers.any(HttpServletRequest.class)
		);

		stubber = PowerMockito.doReturn(mockThemeDisplay());

		stubber.when(
			spy
		).getThemeDisplay(
			Matchers.any(HttpServletRequest.class)
		);

		return spy;
	}

	protected Folder mockFolder() {
		Folder folder = mock(Folder.class);

		PowerMockito.when(
			folder.getFolderId()
		).thenReturn(
			_FOLDER_ID
		);

		return folder;
	}

	protected Repository mockRepository() {
		Repository repository = mock(Repository.class);

		PowerMockito.when(
			repository.getRepositoryId()
		).thenReturn(
			0L
		);

		return repository;
	}

	protected RequestBackedPortletURLFactory
		mockRequestBackedPortletURLFactory() {

		RequestBackedPortletURLFactory requestBackedPortletURLFactory = mock(
			RequestBackedPortletURLFactory.class);

		when(
			requestBackedPortletURLFactory.createActionURL(
				DDMPortletKeys.DYNAMIC_DATA_MAPPING_FORM)
		).thenReturn(
			new MockLiferayPortletURL()
		);

		return requestBackedPortletURLFactory;
	}

	protected ThemeDisplay mockThemeDisplay() {
		ThemeDisplay themeDisplay = mock(ThemeDisplay.class);

		when(
			themeDisplay.getCompanyId()
		).thenReturn(
			0L
		);

		when(
			themeDisplay.getPathContext()
		).thenReturn(
			"/my/path/context/"
		);

		when(
			themeDisplay.getPathThemeImages()
		).thenReturn(
			"/my/theme/images/"
		);

		User user = mockUser();

		when(
			themeDisplay.getUser()
		).thenReturn(
			user
		);

		when(
			themeDisplay.isSignedIn()
		).thenReturn(
			Boolean.FALSE
		);

		return themeDisplay;
	}

	protected User mockUser() {
		User user = mock(User.class);

		when(
			user.getUserId()
		).thenReturn(
			0L
		);

		return user;
	}

	protected void setUpDLAppService() throws Exception {
		MemberMatcher.field(
			DocumentLibraryDDMFormFieldTemplateContextContributor.class,
			"dlAppService"
		).set(
			_documentLibraryDDMFormFieldTemplateContextContributor,
			_dlAppService
		);

		PowerMockito.when(
			_dlAppService.getFileEntryByUuidAndGroupId(
				Matchers.anyString(), Matchers.anyLong())
		).thenReturn(
			_fileEntry
		);
	}

	protected void setUpFileEntry() {
		_fileEntry.setUuid("0000-1111");
		_fileEntry.setGroupId(_GROUP_ID);

		PowerMockito.when(
			_fileEntry.getTitle()
		).thenReturn(
			"New Title"
		);
	}

	protected void setUpHtml() throws Exception {
		MemberMatcher.field(
			DocumentLibraryDDMFormFieldTemplateContextContributor.class, "html"
		).set(
			_documentLibraryDDMFormFieldTemplateContextContributor, _html
		);
	}

	protected void setUpJSONFactory() throws Exception {
		MemberMatcher.field(
			DocumentLibraryDDMFormFieldTemplateContextContributor.class,
			"jsonFactory"
		).set(
			_documentLibraryDDMFormFieldTemplateContextContributor, _jsonFactory
		);
	}

	protected void setUpParamUtil() {
		PropsUtil.setProps(Mockito.mock(Props.class));
	}

	protected void setUpPortletFileRepository() throws Exception {
		MemberMatcher.field(
			DocumentLibraryDDMFormFieldTemplateContextContributor.class,
			"_portletFileRepository"
		).set(
			_documentLibraryDDMFormFieldTemplateContextContributor,
			_portletFileRepository
		);

		Repository repository = mockRepository();

		PowerMockito.when(
			_portletFileRepository.fetchPortletRepository(
				Matchers.anyLong(), Matchers.anyString())
		).thenReturn(
			repository
		);

		Folder folder = mockFolder();

		PowerMockito.when(
			_portletFileRepository.getPortletFolder(
				Matchers.anyLong(), Matchers.anyLong(), Matchers.anyString())
		).thenReturn(
			folder
		);
	}

	protected void setUpRequestBackedPortletURLFactoryUtil() {
		PowerMockito.mockStatic(RequestBackedPortletURLFactoryUtil.class);

		RequestBackedPortletURLFactory requestBackedPortletURLFactory =
			mockRequestBackedPortletURLFactory();

		PowerMockito.when(
			RequestBackedPortletURLFactoryUtil.create(
				Matchers.any(HttpServletRequest.class))
		).thenReturn(
			requestBackedPortletURLFactory
		);
	}

	protected void setUpUserLocalService() throws Exception {
		MemberMatcher.field(
			DocumentLibraryDDMFormFieldTemplateContextContributor.class,
			"_userLocalService"
		).set(
			_documentLibraryDDMFormFieldTemplateContextContributor,
			_userLocalService
		);

		User user = mockUser();

		PowerMockito.when(
			_userLocalService.getUserByScreenName(
				Matchers.anyLong(), Matchers.anyString())
		).thenReturn(
			user
		);
	}

	private static final long _FOLDER_ID = RandomTestUtil.randomLong();

	private static final long _FORM_INSTANCE_ID = RandomTestUtil.randomLong();

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	@Mock
	private DLAppService _dlAppService;

	private final DocumentLibraryDDMFormFieldTemplateContextContributor
		_documentLibraryDDMFormFieldTemplateContextContributor =
			new DocumentLibraryDDMFormFieldTemplateContextContributor();

	@Mock
	private FileEntry _fileEntry;

	private final Html _html = new HtmlImpl();
	private final JSONFactory _jsonFactory = new JSONFactoryImpl();

	@Mock
	private PortletFileRepository _portletFileRepository;

	@Mock
	private ResourceBundle _resourceBundle;

	@Mock
	private UserLocalService _userLocalService;

}
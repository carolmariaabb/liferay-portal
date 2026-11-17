/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.internal.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.rest.test.util.ObjectRelationshipTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.test.util.ObjectDefinitionTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.io.Serializable;

import java.util.List;

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
@FeatureFlags(featureFlags = @FeatureFlag("LPD-108620"))
@RunWith(Arquillian.class)
public class ObjectEntryOneToOneResourceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_parentObjectDefinition = _publishObjectDefinition();
		_childObjectDefinition = _publishObjectDefinition();

		_objectRelationship = ObjectRelationshipTestUtil.addObjectRelationship(
			_parentObjectDefinition, _childObjectDefinition,
			TestPropsValues.getUserId(),
			ObjectRelationshipConstants.TYPE_ONE_TO_ONE);

		ObjectField relationshipObjectField =
			_objectFieldLocalService.getObjectField(
				_objectRelationship.getObjectFieldId2());

		_relationshipObjectFieldName = relationshipObjectField.getName();
	}

	@After
	public void tearDown() throws Exception {
		_objectRelationshipLocalService.deleteObjectRelationship(
			_objectRelationship);

		_objectDefinitionLocalService.deleteObjectDefinition(
			_childObjectDefinition);
		_objectDefinitionLocalService.deleteObjectDefinition(
			_parentObjectDefinition);
	}

	@Test
	public void testFilterByRelationshipField() throws Exception {
		ObjectEntry parentObjectEntry = _addObjectEntry(
			_parentObjectDefinition);

		ObjectEntry childObjectEntry = _addChildObjectEntry(parentObjectEntry);

		_addChildObjectEntry(_addObjectEntry(_parentObjectDefinition));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_childObjectDefinition.getRESTContextPath(), "?filter=",
				_relationshipObjectFieldName, "%20eq%20'",
				parentObjectEntry.getObjectEntryId(), "'"),
			Http.Method.GET);

		Assert.assertEquals(1, jsonObject.getInt("totalCount"));

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			childObjectEntry.getObjectEntryId(), itemJSONObject.getLong("id"));
	}

	@Test
	public void testFilterByRelatedObjectEntryField() throws Exception {
		String value = RandomTestUtil.randomString();

		ObjectEntry parentObjectEntry = ObjectEntryTestUtil.addObjectEntry(
			_parentObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				_FIELD_NAME, value
			).build());

		ObjectEntry childObjectEntry = _addChildObjectEntry(parentObjectEntry);

		_addChildObjectEntry(_addObjectEntry(_parentObjectDefinition));

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_childObjectDefinition.getRESTContextPath(), "?filter=",
				_objectRelationship.getName(), StringPool.SLASH, _FIELD_NAME,
				"%20eq%20'", value, "'"),
			Http.Method.GET);

		Assert.assertEquals(1, jsonObject.getInt("totalCount"));

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			childObjectEntry.getObjectEntryId(), itemJSONObject.getLong("id"));
	}

	@Test
	public void testGetChildObjectEntry() throws Exception {
		ObjectEntry parentObjectEntry = _addObjectEntry(
			_parentObjectDefinition);

		ObjectEntry childObjectEntry = _addChildObjectEntry(parentObjectEntry);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_childObjectDefinition.getRESTContextPath(), StringPool.SLASH,
				childObjectEntry.getObjectEntryId()),
			Http.Method.GET);

		Assert.assertEquals(
			parentObjectEntry.getObjectEntryId(),
			jsonObject.getLong(_relationshipObjectFieldName));
	}

	@Test
	public void testGetParentObjectEntryWithNestedField() throws Exception {
		ObjectEntry parentObjectEntry = _addObjectEntry(
			_parentObjectDefinition);

		ObjectEntry childObjectEntry = _addChildObjectEntry(parentObjectEntry);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null,
			StringBundler.concat(
				_parentObjectDefinition.getRESTContextPath(), StringPool.SLASH,
				parentObjectEntry.getObjectEntryId(), "?nestedFields=",
				_objectRelationship.getName()),
			Http.Method.GET);

		JSONObject nestedJSONObject = jsonObject.getJSONObject(
			_objectRelationship.getName());

		Assert.assertNotNull(jsonObject.toString(), nestedJSONObject);
		Assert.assertEquals(
			childObjectEntry.getObjectEntryId(),
			nestedJSONObject.getLong("id"));
	}

	@Test
	public void testGetRelatedObjectEntries() throws Exception {
		ObjectEntry parentObjectEntry = _addObjectEntry(
			_parentObjectDefinition);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getRelatedEndpoint(parentObjectEntry), Http.Method.GET);

		Assert.assertEquals(0, jsonObject.getInt("totalCount"));

		ObjectEntry childObjectEntry = _addChildObjectEntry(parentObjectEntry);

		jsonObject = HTTPTestUtil.invokeToJSONObject(
			null, _getRelatedEndpoint(parentObjectEntry), Http.Method.GET);

		Assert.assertEquals(1, jsonObject.getInt("totalCount"));

		JSONArray itemsJSONArray = jsonObject.getJSONArray("items");

		JSONObject itemJSONObject = itemsJSONArray.getJSONObject(0);

		Assert.assertEquals(
			childObjectEntry.getObjectEntryId(), itemJSONObject.getLong("id"));
	}

	@Test
	public void testPostChildObjectEntryLinkedToAnAlreadyLinkedParent()
		throws Exception {

		ObjectEntry parentObjectEntry = _addObjectEntry(
			_parentObjectDefinition);

		_addChildObjectEntry(parentObjectEntry);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				_FIELD_NAME, RandomTestUtil.randomString()
			).put(
				_relationshipObjectFieldName,
				parentObjectEntry.getObjectEntryId()
			).toString(),
			_childObjectDefinition.getRESTContextPath(), Http.Method.POST);

		Assert.assertEquals("BAD_REQUEST", jsonObject.getString("status"));
	}

	@Test
	public void testPostChildObjectEntryLinkedToTheParent() throws Exception {
		ObjectEntry parentObjectEntry = _addObjectEntry(
			_parentObjectDefinition);

		JSONObject jsonObject = HTTPTestUtil.invokeToJSONObject(
			JSONUtil.put(
				_FIELD_NAME, RandomTestUtil.randomString()
			).put(
				_relationshipObjectFieldName,
				parentObjectEntry.getObjectEntryId()
			).toString(),
			_childObjectDefinition.getRESTContextPath(), Http.Method.POST);

		Assert.assertTrue(jsonObject.has("id"));
		Assert.assertEquals(
			parentObjectEntry.getObjectEntryId(),
			jsonObject.getLong(_relationshipObjectFieldName));
	}

	private ObjectEntry _addChildObjectEntry(ObjectEntry parentObjectEntry)
		throws Exception {

		return ObjectEntryTestUtil.addObjectEntry(
			_childObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				_FIELD_NAME, RandomTestUtil.randomString()
			).put(
				_relationshipObjectFieldName,
				parentObjectEntry.getObjectEntryId()
			).build());
	}

	private ObjectEntry _addObjectEntry(ObjectDefinition objectDefinition)
		throws Exception {

		return ObjectEntryTestUtil.addObjectEntry(
			objectDefinition,
			HashMapBuilder.<String, Serializable>put(
				_FIELD_NAME, RandomTestUtil.randomString()
			).build());
	}

	private String _getRelatedEndpoint(ObjectEntry parentObjectEntry) {
		return StringBundler.concat(
			_parentObjectDefinition.getRESTContextPath(), StringPool.SLASH,
			parentObjectEntry.getObjectEntryId(), StringPool.SLASH,
			_objectRelationship.getName());
	}

	private ObjectDefinition _publishObjectDefinition() throws Exception {
		return ObjectDefinitionTestUtil.publishObjectDefinition(
			List.of(
				new TextObjectFieldBuilder(
				).indexed(
					true
				).indexedAsKeyword(
					true
				).labelMap(
					RandomTestUtil.randomLocaleStringMap()
				).name(
					_FIELD_NAME
				).build()),
			false);
	}

	private static final String _FIELD_NAME = "textField";

	private ObjectDefinition _childObjectDefinition;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectFieldLocalService _objectFieldLocalService;

	private ObjectRelationship _objectRelationship;

	@Inject
	private ObjectRelationshipLocalService _objectRelationshipLocalService;

	private ObjectDefinition _parentObjectDefinition;
	private String _relationshipObjectFieldName;

}
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.related.models;

import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.exception.RequiredObjectRelationshipException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.InlineSQLHelper;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Carolina Barbosa
 */
public class SystemObject1to1ObjectRelatedModelsProviderImpl
	<T extends BaseModel<T>>
		extends BaseSystemObjectObjectRelatedModelsProviderImpl<T> {

	public SystemObject1to1ObjectRelatedModelsProviderImpl(
		InlineSQLHelper inlineSQLHelper, ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		SystemObjectDefinitionManager systemObjectDefinitionManager,
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry) {

		super(
			inlineSQLHelper, objectDefinition, objectDefinitionLocalService,
			objectEntryLocalService, objectFieldLocalService,
			objectRelationshipLocalService, systemObjectDefinitionManager,
			systemObjectDefinitionManagerRegistry);
	}

	@Override
	public void deleteRelatedModel(
			long userId, long groupId, long objectRelationshipId,
			long primaryKey, String deletionType)
		throws PortalException {

		ObjectRelationship objectRelationship =
			objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		List<T> relatedModels = getRelatedModels(
			groupId, objectRelationshipId, null, false, primaryKey, null, 0, 1,
			null);

		if (relatedModels.isEmpty()) {
			return;
		}

		BaseModel<T> baseModel = relatedModels.get(0);

		SystemObjectDefinitionManager systemObjectDefinitionManager =
			systemObjectDefinitionManagerRegistry.
				getSystemObjectDefinitionManager(objectDefinition.getName());

		if (Objects.equals(
				deletionType,
				ObjectRelationshipConstants.DELETION_TYPE_CASCADE)) {

			systemObjectDefinitionManager.deleteBaseModel(baseModel);
		}
		else if (Objects.equals(
					deletionType,
					ObjectRelationshipConstants.DELETION_TYPE_DISASSOCIATE)) {

			ObjectField objectField = objectFieldLocalService.getObjectField(
				objectRelationship.getObjectFieldId2());

			Map<String, Object> modelAttributes =
				baseModel.getModelAttributes();

			Column<?, Long> primaryKeyColumn =
				systemObjectDefinitionManager.getPrimaryKeyColumn();

			objectEntryLocalService.insertIntoOrUpdateExtensionTable(
				userId, objectRelationship.getObjectDefinitionId2(),
				GetterUtil.getLong(
					modelAttributes.get(primaryKeyColumn.getName())),
				HashMapBuilder.<String, Serializable>put(
					objectField.getName(), 0
				).build());
		}
		else if (Objects.equals(
					deletionType,
					ObjectRelationshipConstants.DELETION_TYPE_PREVENT)) {

			throw new RequiredObjectRelationshipException(objectRelationship);
		}
	}

	@Override
	public String getObjectRelationshipType() {
		return ObjectRelationshipConstants.TYPE_ONE_TO_ONE;
	}

	@Override
	public List<T> getRelatedModels(
			long groupId, long objectRelationshipId, Predicate predicate,
			boolean preferApproved, long primaryKey, String search, int start,
			int end, Sort[] sorts)
		throws PortalException {

		if (start > 0) {
			return Collections.emptyList();
		}

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(
					systemObjectDefinitionManager.getModelClassName());

		return persistedModelLocalService.dslQuery(
			getGroupByStep(
				getDynamicObjectDefinitionTable(),
				DSLQueryFactoryUtil.selectDistinct(table), groupId,
				objectRelationshipId, primaryKey, search
			).orderBy(
				systemObjectDefinitionManager.getPrimaryKeyColumn(
				).ascending()
			).limit(
				0, 1
			));
	}

}
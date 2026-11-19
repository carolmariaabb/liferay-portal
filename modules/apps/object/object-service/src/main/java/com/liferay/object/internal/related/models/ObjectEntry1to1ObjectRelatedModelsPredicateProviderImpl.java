/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.related.models;

import com.liferay.object.constants.ObjectRelationshipConstants;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionLocalizationTableFactory;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTable;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.portal.kernel.exception.PortalException;

/**
 * @author Carolina Barbosa
 */
public class ObjectEntry1to1ObjectRelatedModelsPredicateProviderImpl
	extends BaseObjectEntryObjectRelatedModelsPredicateProviderImpl {

	public ObjectEntry1to1ObjectRelatedModelsPredicateProviderImpl(
		ObjectDefinition objectDefinition,
		ObjectFieldLocalService objectFieldLocalService) {

		super(objectDefinition, objectFieldLocalService);
	}

	@Override
	public String getObjectRelationshipType() {
		return ObjectRelationshipConstants.TYPE_ONE_TO_ONE;
	}

	@Override
	public Predicate getPredicate(
			Long[] groupIds, ObjectRelationship objectRelationship,
			Predicate predicate, ObjectDefinition relatedObjectDefinition)
		throws PortalException {

		ObjectDefinition objectDefinition1 = getObjectDefinition1(
			objectRelationship);

		DynamicObjectDefinitionTable
			objectDefinition1DynamicObjectDefinitionTable =
				getDynamicObjectDefinitionTable(objectDefinition1);

		ObjectDefinition objectDefinition2 = getObjectDefinition2(
			objectRelationship);

		DynamicObjectDefinitionTable
			objectDefinition2DynamicObjectDefinitionTable =
				getDynamicObjectDefinitionTable(objectDefinition2);
		DynamicObjectDefinitionTable
			objectDefinition2ExtensionDynamicObjectDefinitionTable =
				getExtensionDynamicObjectDefinitionTable(objectDefinition2);

		Column<DynamicObjectDefinitionTable, ?> objectRelationshipColumn =
			getObjectRelationshipColumn(
				objectDefinition2DynamicObjectDefinitionTable,
				objectDefinition2ExtensionDynamicObjectDefinitionTable,
				objectDefinition1, objectRelationship);

		if (objectDefinition.getObjectDefinitionId() ==
				objectRelationship.getObjectDefinitionId1()) {

			return getInPredicate(
				objectDefinition1DynamicObjectDefinitionTable.
					getPrimaryKeyColumn(),
				DynamicObjectDefinitionLocalizationTableFactory.create(
					objectDefinition2, objectFieldLocalService),
				objectDefinition2DynamicObjectDefinitionTable,
				objectDefinition2ExtensionDynamicObjectDefinitionTable,
				DSLQueryFactoryUtil.select(objectRelationshipColumn), groupIds,
				objectDefinition2, predicate);
		}

		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			objectRelationshipColumn.getTable();

		Column<?, ?> objectDefinition2PKObjectFieldColumn =
			objectDefinition2DynamicObjectDefinitionTable.getPrimaryKeyColumn();

		return objectDefinition2PKObjectFieldColumn.in(
			DSLQueryFactoryUtil.select(
				dynamicObjectDefinitionTable.getPrimaryKeyColumn()
			).from(
				dynamicObjectDefinitionTable
			).where(
				getInPredicate(
					objectRelationshipColumn,
					DynamicObjectDefinitionLocalizationTableFactory.create(
						objectDefinition1, objectFieldLocalService),
					objectDefinition1DynamicObjectDefinitionTable,
					getExtensionDynamicObjectDefinitionTable(objectDefinition1),
					DSLQueryFactoryUtil.select(
						objectDefinition1DynamicObjectDefinitionTable.
							getPrimaryKeyColumn()),
					groupIds, objectDefinition1, predicate)
			));
	}

}
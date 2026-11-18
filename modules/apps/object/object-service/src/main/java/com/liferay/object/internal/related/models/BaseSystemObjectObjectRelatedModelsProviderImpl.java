/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.related.models;

import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.internal.entry.util.ObjectEntrySearchUtil;
import com.liferay.object.internal.security.permission.util.ObjectEntryPermissionUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.model.ObjectRelationship;
import com.liferay.object.petra.sql.dsl.DynamicObjectDefinitionTable;
import com.liferay.object.related.models.ObjectRelatedModelsProvider;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.object.service.ObjectFieldLocalService;
import com.liferay.object.service.ObjectRelationshipLocalService;
import com.liferay.object.system.SystemObjectDefinitionManager;
import com.liferay.object.system.SystemObjectDefinitionManagerRegistry;
import com.liferay.petra.sql.dsl.Column;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.Table;
import com.liferay.petra.sql.dsl.expression.Expression;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.sql.dsl.query.DSLQuery;
import com.liferay.petra.sql.dsl.query.FromStep;
import com.liferay.petra.sql.dsl.query.GroupByStep;
import com.liferay.petra.sql.dsl.query.JoinStep;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.security.permission.InlineSQLHelper;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;

import java.io.Serializable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Carolina Barbosa
 */
public abstract class BaseSystemObjectObjectRelatedModelsProviderImpl
	<T extends BaseModel<T>>
		implements ObjectRelatedModelsProvider<T> {

	public BaseSystemObjectObjectRelatedModelsProviderImpl(
		InlineSQLHelper inlineSQLHelper, ObjectDefinition objectDefinition,
		ObjectDefinitionLocalService objectDefinitionLocalService,
		ObjectEntryLocalService objectEntryLocalService,
		ObjectFieldLocalService objectFieldLocalService,
		ObjectRelationshipLocalService objectRelationshipLocalService,
		SystemObjectDefinitionManager systemObjectDefinitionManager,
		SystemObjectDefinitionManagerRegistry
			systemObjectDefinitionManagerRegistry) {

		this.inlineSQLHelper = inlineSQLHelper;
		this.objectDefinition = objectDefinition;
		this.objectDefinitionLocalService = objectDefinitionLocalService;
		this.objectEntryLocalService = objectEntryLocalService;
		this.objectFieldLocalService = objectFieldLocalService;
		this.objectRelationshipLocalService = objectRelationshipLocalService;
		this.systemObjectDefinitionManager = systemObjectDefinitionManager;
		this.systemObjectDefinitionManagerRegistry =
			systemObjectDefinitionManagerRegistry;

		localizationTable =
			systemObjectDefinitionManager.getLocalizationTable();
		table = systemObjectDefinitionManager.getTable();
	}

	@Override
	public void disassociateRelatedModels(
			long userId, long objectRelationshipId, long primaryKey1,
			long primaryKey2)
		throws PortalException {

		ObjectRelationship objectRelationship =
			objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2());
		Map<String, Serializable> values =
			HashMapBuilder.<String, Serializable>put(
				() -> {
					ObjectField objectField =
						objectFieldLocalService.getObjectField(
							objectRelationship.getObjectFieldId2());

					return objectField.getName();
				},
				0
			).build();

		if (objectDefinition.isUnmodifiableSystemObject()) {
			objectEntryLocalService.insertIntoOrUpdateExtensionTable(
				userId, objectDefinition.getObjectDefinitionId(), primaryKey2,
				values);

			return;
		}

		ObjectEntry objectEntry = objectEntryLocalService.getObjectEntry(
			primaryKey2);

		objectEntryLocalService.partialUpdateObjectEntry(
			userId, primaryKey2, objectEntry.getObjectEntryFolderId(), values,
			new ServiceContext());
	}

	@Override
	public T fetchRelatedModel(
			long groupId, long objectRelationshipId, long primaryKey)
		throws PortalException {

		ObjectRelationship objectRelationship =
			objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		ObjectDefinition relatedObjectDefinition =
			objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2());

		if (relatedObjectDefinition.isUnmodifiableSystemObject()) {
			throw new UnsupportedOperationException();
		}

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(
					systemObjectDefinitionManager.getModelClassName());

		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			getDynamicObjectDefinitionTable(
				relatedObjectDefinition.getObjectDefinitionId());

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId1());

		Column<DynamicObjectDefinitionTable, Long> column =
			(Column<DynamicObjectDefinitionTable, Long>)
				dynamicObjectDefinitionTable.getColumn(
					StringBundler.concat(
						"r_", objectRelationship.getName(), "_",
						objectDefinition.getPKObjectFieldName()));

		if (column == null) {
			dynamicObjectDefinitionTable =
				getExtensionDynamicObjectDefinitionTable(
					relatedObjectDefinition.getObjectDefinitionId());
		}

		FromStep fromStep = DSLQueryFactoryUtil.selectDistinct(table);
		ObjectField objectField = objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());
		Column<DynamicObjectDefinitionTable, Long> primaryKeyColumn =
			dynamicObjectDefinitionTable.getPrimaryKeyColumn();

		List<T> relatedModels = persistedModelLocalService.dslQuery(
			fromStep.from(
				table
			).innerJoinON(
				dynamicObjectDefinitionTable,
				systemObjectDefinitionManager.getPrimaryKeyColumn(
				).eq(
					(Expression<Long>)dynamicObjectDefinitionTable.getColumn(
						objectField.getDBColumnName())
				)
			).where(
				primaryKeyColumn.eq(
					primaryKey
				).and(
					table.getColumn(
						"companyId"
					).eq(
						groupId
					)
				)
			));

		if (relatedModels.isEmpty()) {
			return null;
		}

		return relatedModels.get(0);
	}

	@Override
	public String getClassName() {
		return systemObjectDefinitionManager.getModelClassName();
	}

	@Override
	public long getCompanyId() {
		return objectDefinition.getCompanyId();
	}

	@Override
	public int getRelatedModelsCount(
			long groupId, long objectRelationshipId, Predicate predicate,
			long primaryKey, String search)
		throws PortalException {

		DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
			getDynamicObjectDefinitionTable();

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(
					systemObjectDefinitionManager.getModelClassName());

		return persistedModelLocalService.dslQueryCount(
			getGroupByStep(
				dynamicObjectDefinitionTable,
				DSLQueryFactoryUtil.countDistinct(
					dynamicObjectDefinitionTable.getPrimaryKeyColumn()),
				groupId, objectRelationshipId, primaryKey, search));
	}

	@Override
	public List<T> getUnrelatedModels(
			long companyId, long groupId, ObjectDefinition objectDefinition,
			long objectEntryId, long objectRelationshipId, String search,
			int start, int end)
		throws PortalException {

		DSLQuery dslQuery = getUnrelatedModelsGroupByStep(
			companyId, DSLQueryFactoryUtil.select(table), groupId,
			objectDefinition, objectRelationshipId
		).orderBy(
			systemObjectDefinitionManager.getPrimaryKeyColumn(
			).ascending()
		).limit(
			start, end
		);

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(objectDefinition.getClassName());

		return persistedModelLocalService.dslQuery(dslQuery);
	}

	@Override
	public int getUnrelatedModelsCount(
			long companyId, long groupId, ObjectDefinition objectDefinition,
			long objectEntryId, long objectRelationshipId, String search)
		throws PortalException {

		DSLQuery dslQuery = getUnrelatedModelsGroupByStep(
			companyId, DSLQueryFactoryUtil.count(), groupId, objectDefinition,
			objectRelationshipId);

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(objectDefinition.getClassName());

		return persistedModelLocalService.dslQueryCount(dslQuery);
	}

	protected DynamicObjectDefinitionTable getDynamicObjectDefinitionTable()
		throws PortalException {

		// TODO Cache this across the cluster with proper invalidation when the
		// object definition or its object fields are updated

		return new DynamicObjectDefinitionTable(
			objectDefinition,
			objectFieldLocalService.getObjectFields(
				objectDefinition.getObjectDefinitionId(),
				objectDefinition.getExtensionDBTableName()),
			objectDefinition.getExtensionDBTableName());
	}

	protected DynamicObjectDefinitionTable getDynamicObjectDefinitionTable(
			long objectDefinitionId)
		throws PortalException {

		// TODO Cache this across the cluster with proper invalidation when the
		// object definition or its object fields are updated

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.getObjectDefinition(
				objectDefinitionId);

		return new DynamicObjectDefinitionTable(
			objectDefinition,
			objectFieldLocalService.getObjectFields(
				objectDefinitionId, objectDefinition.getDBTableName()),
			objectDefinition.getDBTableName());
	}

	protected DynamicObjectDefinitionTable
			getExtensionDynamicObjectDefinitionTable(long objectDefinitionId)
		throws PortalException {

		// TODO Cache this across the cluster with proper invalidation when the
		// object definition or its object fields are updated

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.getObjectDefinition(
				objectDefinitionId);

		return new DynamicObjectDefinitionTable(
			objectDefinition,
			objectFieldLocalService.getObjectFields(
				objectDefinitionId, objectDefinition.getExtensionDBTableName()),
			objectDefinition.getExtensionDBTableName());
	}

	protected GroupByStep getGroupByStep(
			DynamicObjectDefinitionTable dynamicObjectDefinitionTable,
			FromStep fromStep, long groupId, long objectRelationshipId,
			long primaryKey, String search)
		throws PortalException {

		Column<?, Long> primaryKeyColumn = null;

		ObjectRelationship objectRelationship =
			objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		ObjectDefinition objectDefinition1 =
			objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId1());
		ObjectDefinition objectDefinition2 =
			objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId2());

		ObjectField objectField = objectFieldLocalService.getObjectField(
			objectRelationship.getObjectFieldId2());

		if (Objects.equals(objectField.getDBTableName(), table)) {
			primaryKeyColumn = (Column<?, Long>)table.getColumn(
				objectField.getDBColumnName());
		}
		else {
			primaryKeyColumn =
				(Column<DynamicObjectDefinitionTable, Long>)
					dynamicObjectDefinitionTable.getColumn(
						objectField.getDBColumnName());
		}

		JoinStep joinStep = fromStep.from(
			table
		).innerJoinON(
			dynamicObjectDefinitionTable,
			dynamicObjectDefinitionTable.getPrimaryKeyColumn(
			).eq(
				systemObjectDefinitionManager.getPrimaryKeyColumn()
			)
		);

		if (localizationTable != null) {
			joinStep = joinStep.leftJoinOn(
				localizationTable,
				dynamicObjectDefinitionTable.getPrimaryKeyColumn(
				).eq(
					localizationTable.getColumn(
						dynamicObjectDefinitionTable.getPrimaryKeyColumnName())
				).and(
					localizationTable.getColumn(
						"languageId"
					).eq(
						ObjectEntrySearchUtil.getLanguageId()
					)
				));
		}

		return joinStep.where(
			primaryKeyColumn.eq(
				primaryKey
			).and(
				() -> {
					Column<?, Long> groupIdColumn = table.getColumn("groupId");

					if ((groupIdColumn == null) ||
						Objects.equals(
							ObjectDefinitionConstants.SCOPE_COMPANY,
							objectDefinition1.getScope()) ||
						Objects.equals(
							ObjectDefinitionConstants.SCOPE_COMPANY,
							objectDefinition2.getScope())) {

						return null;
					}

					return groupIdColumn.eq(groupId);
				}
			).and(
				() -> {
					Column<?, Long> companyIdColumn = table.getColumn(
						"companyId");

					if (companyIdColumn == null) {
						return null;
					}

					return companyIdColumn.eq(objectField.getCompanyId());
				}
			).and(
				ObjectEntryPermissionUtil.getPermissionWherePredicate(
					dynamicObjectDefinitionTable, groupId, inlineSQLHelper)
			).and(
				() -> {
					ObjectField titleObjectField =
						objectFieldLocalService.getObjectField(
							objectDefinition2.getTitleObjectFieldId());

					if (titleObjectField.isLocalized()) {
						return ObjectEntrySearchUtil.getRelatedModelsPredicate(
							objectDefinition2, objectFieldLocalService, search,
							localizationTable);
					}

					return ObjectEntrySearchUtil.getRelatedModelsPredicate(
						objectDefinition2, objectFieldLocalService, search,
						dynamicObjectDefinitionTable);
				}
			));
	}

	protected GroupByStep getUnrelatedModelsGroupByStep(
			long companyId, FromStep fromStep, long groupId,
			ObjectDefinition objectDefinition, long objectRelationshipId)
		throws PortalException {

		Column<?, Long> companyIdColumn = (Column<?, Long>)table.getColumn(
			"companyId");

		ObjectRelationship objectRelationship =
			objectRelationshipLocalService.getObjectRelationship(
				objectRelationshipId);

		ObjectDefinition objectDefinition1 =
			objectDefinitionLocalService.getObjectDefinition(
				objectRelationship.getObjectDefinitionId1());

		return fromStep.from(
			table
		).where(
			companyIdColumn.eq(
				companyId
			).and(
				() -> {
					Column<?, Long> groupIdColumn = table.getColumn("groupId");

					if ((groupIdColumn == null) ||
						Objects.equals(
							ObjectDefinitionConstants.SCOPE_COMPANY,
							objectDefinition1.getScope())) {

						return null;
					}

					ObjectDefinition objectDefinition2 =
						objectDefinitionLocalService.getObjectDefinition(
							objectRelationship.getObjectDefinitionId2());

					if (Objects.equals(
							ObjectDefinitionConstants.SCOPE_COMPANY,
							objectDefinition2.getScope())) {

						return null;
					}

					return groupIdColumn.eq(groupId);
				}
			).and(
				() -> {
					Column<?, Long> primaryKeyColumn = table.getColumn(
						objectDefinition.getPKObjectFieldDBColumnName());

					DynamicObjectDefinitionTable dynamicObjectDefinitionTable =
						getDynamicObjectDefinitionTable();
					ObjectField objectField =
						objectFieldLocalService.getObjectField(
							objectRelationship.getObjectFieldId2());

					Column<DynamicObjectDefinitionTable, Long>
						foreignKeyColumn =
							(Column<DynamicObjectDefinitionTable, Long>)
								dynamicObjectDefinitionTable.getColumn(
									objectField.getDBColumnName());

					return primaryKeyColumn.notIn(
						DSLQueryFactoryUtil.select(
							dynamicObjectDefinitionTable.getPrimaryKeyColumn()
						).from(
							dynamicObjectDefinitionTable
						).where(
							foreignKeyColumn.neq(0L)
						));
				}
			)
		);
	}

	protected final InlineSQLHelper inlineSQLHelper;
	protected final Table localizationTable;
	protected final ObjectDefinition objectDefinition;
	protected final ObjectDefinitionLocalService objectDefinitionLocalService;
	protected final ObjectEntryLocalService objectEntryLocalService;
	protected final ObjectFieldLocalService objectFieldLocalService;
	protected final ObjectRelationshipLocalService
		objectRelationshipLocalService;
	protected final SystemObjectDefinitionManager systemObjectDefinitionManager;
	protected final SystemObjectDefinitionManagerRegistry
		systemObjectDefinitionManagerRegistry;
	protected final Table table;

}
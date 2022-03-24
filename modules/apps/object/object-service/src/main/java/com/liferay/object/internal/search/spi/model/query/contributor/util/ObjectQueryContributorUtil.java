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

package com.liferay.object.internal.search.spi.model.query.contributor.util;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.BooleanQuery;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.ParseException;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.generic.MatchQuery;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Carolina Barbosa
 */
public class ObjectQueryContributorUtil {

	public static void addMatchQuery(
		BooleanQuery booleanQuery, String fieldName, boolean localized,
		SearchContext searchContext) {

		String fieldValue = GetterUtil.getString(
			searchContext.getAttribute(fieldName));

		if (Validator.isNull(fieldValue)) {
			return;
		}

		if (localized) {
			fieldName = Field.getLocalizedName(
				searchContext.getLanguageId(), fieldName);
		}

		try {
			booleanQuery.add(
				_getMatchQuery(fieldName, fieldValue),
				BooleanClauseOccur.SHOULD);
		}
		catch (ParseException parseException) {
			throw new SystemException(parseException);
		}
	}

	private static MatchQuery _getMatchQuery(
		String fieldName, String fieldValue) {

		MatchQuery matchQuery = new MatchQuery(fieldName, fieldValue);

		matchQuery.setType(MatchQuery.Type.PHRASE_PREFIX);

		return matchQuery;
	}

}
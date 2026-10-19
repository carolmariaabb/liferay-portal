/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.entry.preview;

import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectEntryVersion;

/**
 * @author Carolina Barbosa
 */
public interface ObjectEntryPreviewVersionProvider {

	public ObjectEntryVersion fetchPreviewObjectEntryVersion(
		ObjectEntry objectEntry);

}
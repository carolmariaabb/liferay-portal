/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

function escapeHTML(text: string): string {
	return text
		.replace(/&/g, '&amp;')
		.replace(/</g, '&lt;')
		.replace(/>/g, '&gt;');
}

function normalizeInlineHeaders(md: string): string {
	return md
		.replace(/([^\n])\s*(#{1,6})\s+/g, '$1\n\n$2 ')
		.replace(/\s*(#{1,6})\s+([^\n]+)/g, '\n\n$1 $2\n\n');
}

function renderHeaders(md: string): string {
	return md
		.replace(/^### (.*)$/gm, '<h3>$1</h3>')
		.replace(/^## (.*)$/gm, '<h2>$1</h2>')
		.replace(/^# (.*)$/gm, '<h1>$1</h1>');
}

function renderInlineFormatting(md: string): string {
	return md
		.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
		.replace(/\*(.*?)\*/g, '<em>$1</em>')
		.replace(/`([^`]+)`/g, '<code>$1</code>');
}

function renderLists(md: string): string {
	return md.replace(/(^|\n)- (.*)(\n- .*)*/g, (match: string) => {
		const items = match
			.trim()
			.split('\n')
			.map((line: string) => '<li>' + line.replace(/^- /, '') + '</li>')
			.join('');

		return '<ul>' + items + '</ul>';
	});
}

function wrapParagraphs(md: string): string {
	return md
		.split(/\n{2,}/)
		.map((block: string) => {
			if (block.match(/^<(blockquote|h\d|ol|p|ul)/)) {
				return block;
			}

			return '<p>' + block.replace(/\n/g, '<br />') + '</p>';
		})
		.join('');
}

export function renderMarkdown(text: string): string {
	let md = normalizeInlineHeaders(text);

	md = escapeHTML(md);
	md = renderHeaders(md);
	md = renderInlineFormatting(md);
	md = renderLists(md);
	md = wrapParagraphs(md);

	return md;
}

export function getInitials(name: string): string {
	if (!name) {
		return '';
	}

	return name
		.split(' ', 3)
		.reduce((acc: string, val: string) => acc + val.substring(0, 1), '')
		.toUpperCase();
}

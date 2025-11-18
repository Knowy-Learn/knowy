/** @type {import('prettier').Config & import('prettier-plugin-tailwindcss').PluginOptions} */
export default {
	plugins: ["prettier-plugin-astro", "prettier-plugin-tailwindcss"],
	useTabs: true,
	singleQuote: false,
	trailingComma: "none",
	semi: false,
	printWidth: 120,
	pluginSearchDirs: false
}

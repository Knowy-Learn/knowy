import { defineConfig } from "astro/config"
import tailwindcss from "@tailwindcss/vite"

export default defineConfig({
	outDir: '../target/classes/public',
	vite: {
		plugins: [tailwindcss()],
	}
})

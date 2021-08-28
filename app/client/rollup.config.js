import svelte from 'rollup-plugin-svelte';
import smelte from 'smelte/rollup-plugin-smelte';
import resolve from '@rollup/plugin-node-resolve';
import commonjs from '@rollup/plugin-commonjs';
import livereload from 'rollup-plugin-livereload';
import { terser } from 'rollup-plugin-terser';
import { config } from 'dotenv';

const production = !process.env.ROLLUP_WATCH;

function serve() {
	let server;
	
	function toExit() {
		if (server) server.kill(0);
	}

	return {
		writeBundle() {
			if (server) return;
			server = require('child_process').spawn('npm', ['run', 'start', '--', '--dev'], {
				stdio: ['ignore', 'inherit', 'inherit'],
				shell: true
			});

			process.on('SIGTERM', toExit);
			process.on('exit', toExit);
		}
	};
}

export default {
	input: 'src/main/svelte/app.js',
	output: {
		sourcemap: true,
		format: 'iife',
		name: 'client',
		file: 'public/build/bundle.js'
	},
	plugins: [
		config(),
		
		svelte({
			compilerOptions: {
				dev: !production
			}
		}),

		smelte({
			output: "public/build/smelte.css", 
			purge: false /*production*/,
			postcss: [],
			whitelist: [],
			whitelistPatterns: [],
			tailwind: { 
				colors: { 
					primary: "#b027b0",
					secondary: "#009688",
					error: "#f44336",
					success: "#4caf50",
					alert: "#ff9800",
					blue: "#2196f3",
					dark: "#212121" 
			  	},
				darkMode: false,
			}
		}),

		resolve({
			browser: true,
			dedupe: ['svelte']
		}),

		commonjs(),

		// In dev mode, call `npm run start` once
		// the bundle has been generated
		!production && serve(),

		// Watch the `public` directory and refresh the
		// browser on changes when not in production
		!production && livereload('public'),

		// If we're building for production (npm run build
		// instead of npm run dev), minify
		production && terser()
	],
	watch: {
		clearScreen: false
	}
};

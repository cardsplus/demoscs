import preprocess from 'svelte-preprocess';
import node from '@sveltejs/adapter-node';

const config = {
	preprocess: [
		preprocess({
			postcss: true
		}),
	],
	kit: {
		adapter: node(),
		// hydrate the <div id="svelte"> element in src/app.html
		target: '#svelte'
	}
};

export default config;

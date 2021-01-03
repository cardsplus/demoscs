<script>
	import { onMount } from 'svelte';
	import { Button, Snackbar } from "smelte";
	import { fetchHtml } from './utils/rest.js';
	
	let alertSnackbarDialog = false;
	let alertSnackbarText = 'ok';
		
	let homeUrl = BACKEND_URL;
	let htmlUrl = '/version.html';
	let htmlText = 'loading ..';
	onMount(async () => {
		fetchHtml(htmlUrl)
		.then(res => res.text())
		.then(html => {
			htmlText = html;
		})
		.catch(err => {
			alertSnackbarText = err;
			alertSnackbarDialog = true;
		});
	});
</script>

<Snackbar bind:value={alertSnackbarDialog} bottom left color="alert" timeout={2000}>
	<div>{alertSnackbarText}</div>
</Snackbar>

<h1>Home</h1>
<div class="flex flex-col space-y-2">
	<h2>Version</h2>
	<div class="text-2xl">
		{@html htmlText}	
	</div>	
	<h2>Backend</h2>
	<div class="text-2xl underline text-blue-600">
		<a href="{homeUrl}/api" target="_blank">
			{homeUrl}
		</a>
	</div>
	<h2>Hilfe</h2>
	<div class="text-2xl underline text-blue-600">
		<a href="https://developer.mozilla.org/docs/Web/JavaScript" target="_blank">
			Javascript
		</a>
		<br/>
		<a href="https://svelte.dev/docs" target="_blank">
			Svelte API
		</a>
		<br/>
		<a href="https://svelte.dev/repl" target="_blank">
			Svelte REPL
		</a>
		<br/>
		<a href="https://smeltejs.com/components" target="_blank">
			Smelte
		</a>
		<br/>
		<a href="https://tailwindcss.com/docs" target="_blank">
			Tailwind CSS
		</a>
		<br/>		
	</div>
</div>

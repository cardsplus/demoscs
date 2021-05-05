<script>
	import { onMount } from 'svelte';
	import { Chip, Snackbar } from "smelte";
	import { fetchDoc } from './utils/rest.js';
	import { createTestSet, removeTestSet } from './utils/data.js';

	let alertSnackbarDialog = false;
	let alertSnackbarText = 'ok';
		
	let homeUrl = BACKEND_URL;
	let versionUrl = '/version';
	let versionHtml = 'loading ..';
	onMount(async () => {
		fetchDoc(versionUrl,'text/html')
		.then(res => res.text())
		.then(html => {
			versionHtml = html;
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

<h1>Info</h1>
<div class="flex flex-col space-y-2">
	<div class="text-2xl">
		Version {@html versionHtml}	
	</div>	
	<div class="text-2xl">
		<a class="underline text-blue-600" href="{homeUrl}/api" target="_blank">{homeUrl}</a>
	</div>
	<div>
		<Chip on:click={createTestSet} icon="create">
			Testdatensätze anlegen
		</Chip>
	</div>
	<div>
		<Chip on:click={removeTestSet} icon="delete">
			Testdatensätze löschen
		</Chip>
	</div>
</div>

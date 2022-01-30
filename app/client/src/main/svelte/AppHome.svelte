<script>
	import { onMount } from 'svelte';
	import Chip from './components/Chip';
	import { toast } from './components/Toast';
	import { apiExplorerUrl } from './utils/rest.js';
	import { fetchDoc } from './utils/rest.js';
	import { createTestSet } from './utils/test.js';
	import { removeTestSet } from './utils/test.js';

	let apiExplorer = apiExplorerUrl();
		
	let versionUrl = '/version';
	let versionHtml = 'loading ..';
	onMount(async () => {
		fetchDoc(versionUrl,'text/html')
		.then(res => res.text())
		.then(html => {
			versionHtml = html;
		})
		.catch(err => {
			console.log(err);
			toast.push(err.toString());
		});
	});
</script>

<h1>Info</h1>
<div class="flex flex-col ml-2 mr-2 space-y-2">
	<fieldset class="p-4 border-2 space-y-2">
		<legend class="text-xs">APP-Version</legend>
		<div class="text-2xl">
			{#if versionHtml}
			{@html versionHtml}
			{:else}
			-
			{/if}
		</div>	
	</fieldset>
	<fieldset class="p-4 border-2 space-y-2">
		<legend class="text-xs">API-Explorer</legend>
		<div class="text-2xl">
			<a class="underline text-blue-600" href="{apiExplorer}" target="_blank">{apiExplorer}</a>
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
	</fieldset>
</div>

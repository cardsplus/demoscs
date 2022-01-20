<script>
	import { onMount } from 'svelte';
	import Chip from './components/Chip';
	import { toast } from './components/Toast';
	import { SERVER_URL } from './utils/rest.js';
	import { fetchDoc } from './utils/rest.js';
	import { createTestSet } from './utils/test.js';
	import { removeTestSet } from './utils/test.js';
		
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
<div class="flex flex-col space-y-2">
	<div class="text-2xl">
		Version {@html versionHtml}	
	</div>	
	<div class="text-2xl">
		<a class="underline text-blue-600" href="{SERVER_URL}/api" target="_blank">{SERVER_URL}</a>
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

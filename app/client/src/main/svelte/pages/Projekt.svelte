<script>
	import { onMount } from 'svelte';
	import { toast } from '../components/Toast';
	import { loadAllValue } from '../utils/rest.js';
	import { updatePatch } from '../utils/rest.js';
	import Icon from '../components/Icon';
	import Checkbox from '../components/Checkbox';
	import TextField from '../components/TextField';
	import ProjektEditor from './ProjektEditor.svelte';

	let allProjekt = [];
	let projektId = undefined;
	function onProjektClicked(projekt) {
		projektId = projekt.id;
	}

	let projektEditorCreate = false;
	function projektEditorCreateClicked() {
		projektEditorCreate = true;
	}	 
	let projektEditorUpdate = false;
	function projektEditorUpdateClicked(projekt) {
		projektId = projekt.id;
		projektEditorUpdate = true;
	}
	$: projektEditorDisabled = projektEditorCreate || projektEditorUpdate;

	let allSpracheItem = [];

	let allNutzerItem = [];

    onMount(async () => {
        try {
			allNutzerItem = await loadAllValue('/api/nutzer/search/findAllItem');
            console.log(['onMount', allNutzerItem]);
			allSpracheItem = await loadAllValue('/api/enum/sprache');
            console.log(['onMount', allSpracheItem]);
        } catch(err) {
			console.log(['onMount', err]);
			toast.push(err.toString());
        };
		reloadAllProjekt();
	});

	let filterPrefix = '';
	function filterProjekt(prefix,allValue) {
		if (!filterPrefix) return allValue;
		return allValue.filter(e => {
			for (const s of e.name.split(" ")) {
				if (s.toLowerCase().startsWith(prefix.toLowerCase())) {
					return true;
				}
			}
            return false;
		})
	}
	$: allProjektFiltered = filterProjekt(filterPrefix, allProjekt);

	function reloadAllProjekt() {
		loadAllValue('/api/projekt/search/findAllByOrderByNameAsc')
		.then(json => {
			console.log(['reloadAllNutzer', json]);
			allProjekt = json;
		})
		.catch(err => {
			console.log(['reloadAllNutzer', err]);
			toast.push(err.toString());
		});
	};

	function updateProjekt(projekt) {
		updatePatch('/api/projekt/' + projekt.id, projekt)
		.then(() => {
			reloadAllProjekt();
		})
		.catch(err => {
			console.log(['updateProjekt', err]);
			toast.push(err.toString());
		});
	};
</script>

<h1>Projekt</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
	<div class="flex-grow">
		<h4 title="Filter für die Projekt, nicht case-sensitiv">
			Aktueller Filter
		</h4>
		<TextField 
			bind:value={filterPrefix}
			label="Filter" 
			placeholder="Bitte Filterkriterien eingeben"
			disabled={projektEditorDisabled}/>
		<h4 title="Liste der Projekt, ggfs. gefiltert, jedes Element editierbar">
			Aktuelle Projekte <small>({allProjektFiltered.length})</small>
		</h4>
		<table class="table-fixed">
			<thead class="justify-between">
				<tr class="bg-gray-100">
					<th class="px-2 py-3 border-b-2 border-gray-300 w-16">
						<span class="text-gray-600 text-sm">Aktiv</span>
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/3">
						<span class="text-gray-600">Name</span>
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 text-left w-full">
						<span class="text-gray-600">Team</span>
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 w-16">
						<Icon 
							on:click={() => projektEditorCreateClicked()}
							disabled={projektEditorDisabled}
							name="edit"
                            outlined/>
					</th>
				</tr>
			</thead>
			<tbody>
				{#if projektEditorCreate}
				<tr>
					<td>
					</td>
					<td	colspan="2">
						<ProjektEditor
							bind:visible={projektEditorCreate} 
							on:create={e => reloadAllProjekt()}
							{allSpracheItem}
							{allNutzerItem}/>
					<td>
				</tr>
				{/if}
				{#each allProjektFiltered as projekt, i}
				<tr on:click={e => onProjektClicked(projekt)}
					title={projekt.id}
					class:ring={projektId === projekt.id}>
					<td class="px-2 py-3">
						<Checkbox 
							bind:checked={projekt.aktiv}
							on:change={() => updateProjekt(projekt)} />
					</td>
					<td  class="px-2 py-3 text-left">
						<span>{projekt.name}</span>
					</td>
					<td  class="px-2 py-3 text-left">
						<div class="flex flex-col">
							{#each projekt.allMitgliedItem as nutzerItem}
							<div class="text-sm underline text-blue-600">
								<a href={'/nutzer/' + nutzerItem.value}>{nutzerItem.text}</a>
							</div>
							{:else}
							<div class="text-sm underline text-blue-600">
								<a href={'/nutzer/'}>Keine Mitglieder</a>
							</div>
							{/each}
						</div>
					</td>
					<td class="px-2 py-3">
						<Icon 
							on:click={() => projektEditorUpdateClicked(projekt)}
							disabled={projektEditorDisabled}
							name="edit"
                            outlined/>
					</td>
				</tr>
				{#if projektEditorUpdate && projektId === projekt.id}
				<tr>
					<td>
					</td>
					<td	colspan="2">
						<ProjektEditor
							bind:visible={projektEditorUpdate} 
							on:update={e => reloadAllProjekt()}
							on:remove={e => reloadAllProjekt()}
							{projekt}
							{allSpracheItem}
							{allNutzerItem}/>
					<td>
				</tr>
				{/if}
				{:else}
				<tr>
					<td class="px-2 py-3" colspan="5">
						Keine Projekte
					</td>
				</tr>
				{/each}
			</tbody>
		</table>
	</div>
</div>

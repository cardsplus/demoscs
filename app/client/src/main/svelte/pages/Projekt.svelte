<script>
	import { onMount } from 'svelte';
	import Icon from '../components/Icon';
	import Checkbox from '../components/Checkbox';
	import TextField from '../components/TextField';
	import { toast } from '../components/Toast';
	import { loadAllValue } from '../utils/rest.js';
	import { createValue } from '../utils/rest.js';
	import { updatePatch } from '../utils/rest.js';
	import { removeValue } from '../utils/rest.js';
	import { toNutzerItem } from './nutzer.js';
	import ProjektEditor from './ProjektEditor.svelte';

	let allProjektValue = [];
	let projektIndexOf = undefined;
	let projektSelected = undefined;
	function onProjektClicked(index) {
		projektIndexOf = index;
		projektSelected = allProjektValueFiltered[index];
		aufgabeSelected = undefined;
        reloadAufgabe(projektSelected);
	}

	let projektEditorCreate = false;
	function projektEditorCreateClicked() {
		projektEditorCreate = true;
	}	 
	let projektEditorUpdate = false;
	let projektEditorUpdateId = undefined;
	function projektEditorUpdateClicked(id) {
		projektEditorUpdateId = id;
		projektEditorUpdate = true;
	}
	$: projektEditorDisabled = projektEditorCreate || projektEditorUpdate;

	let allNutzerItem = [];

    onMount(async () => {
		await loadAllValue('/api/projekt/search/findAllByOrderByNameAsc')
        .then(json => {
			console.log(json);
			filterPrefix = '';
            allProjektValue = json;
        })
        .catch(err => {
            console.log(err);
			toast.push(err.toString());
        });
		await loadAllValue('/api/nutzer/search/findAllByOrderByMailAsc')
        .then(json => {
			console.log(json);
            allNutzerItem = json.map(toNutzerItem);
         })
        .catch(err => {
            console.log(err);
			toast.push(err.toString());
        });
	});

	let filterPrefix = '';
	function filterProjekt(prefix,allValue) {
		projektIndexOf = undefined;
		projektSelected = undefined;
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
	$: allProjektValueFiltered = filterProjekt(filterPrefix,allProjektValue);
 
	function createProjekt(projekt) {
		createValue('/api/projekt', projekt)
		.then(() => {
			return loadAllValue('/api/projekt/search/findAllByOrderByNameAsc');
		})
		.then(json => {
			console.log(json);
			allProjektValue = json;
		})
		.catch(err => {
            console.log(err);
			toast.push(err.toString());
		});
	};

	function updateProjekt(projekt) {
		updatePatch('/api/projekt/' + projekt.id, projekt)
		.then(() => {
			return loadAllValue('/api/projekt/search/findAllByOrderByNameAsc');
		})
		.then(json => {
			console.log(json);
			allProjektValue = json;
		})
		.catch(err => {
            console.log(err);
			toast.push(err.toString());
		});
	};

	function removeProjekt(projekt) {
		if (!confirm("Projekt '" + projekt.name + "' wirklich löschen?")) return;
		removeValue('/api/projekt/' + projekt.id)
		.then(() => {
			return loadAllValue('/api/projekt/search/findAllByOrderByNameAsc');
		})
		.then(json => {
			console.log(json);
			allProjektValue = json;
		})
		.catch(err => {
            console.log(err);
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
		<TextField bind:value={filterPrefix}
			label="Filter" 
			placeholder="Bitte Filterkriterien eingeben"/>
		<h4 title="Liste der Projekt, ggfs. gefiltert, jedes Element editierbar">
			Aktuelle Projekte <small>({allProjektValueFiltered.length})</small>
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
						<Icon on:click={() => projektEditorCreateClicked()}
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
					<td	colspan="3">
						<ProjektEditor
							bind:visible={projektEditorCreate} 
							on:create={e => createProjekt(e.detail)}
							{allNutzerItem}/>
					<td>
				</tr>
				{/if}
				{#each allProjektValueFiltered as projekt, i}
				<tr on:click={e => onProjektClicked(i)}
					title={projekt.id}
					class:ring={projektIndexOf === i}>
					<td class="px-2 py-3">
						<Checkbox bind:checked={projekt.aktiv}
							on:change={() => updateProjekt(projekt)} />
					</td>
					<td  class="px-2 py-3 text-left">
						<span>{projekt.name}</span>
					</td>
					<td  class="px-2 py-3 text-left">
						<div class="flex flex-wrap gap-1">
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
						<Icon on:click={() => projektEditorUpdateClicked(projekt.id)}
							disabled={projektEditorDisabled}
							name="edit"
                            outlined/>
					</td>
				</tr>
				{#if projektEditorUpdate && projektEditorUpdateId === projekt.id}
				<tr>
					<td>
					</td>
					<td	colspan="3">
						<ProjektEditor
							bind:visible={projektEditorUpdate} 
							on:update={e => updateProjekt(e.detail)}
							on:remove={e => removeProjekt(e.detail)}
							{projekt}
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

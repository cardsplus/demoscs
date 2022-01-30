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
	import NutzerEditor from './NutzerEditor.svelte';

	let allNutzerValue = [];
	let nutzerIndexOf = undefined;
	function onNutzerClicked(index) {
		nutzerIndexOf = index;
	}

	let nutzerEditorCreate = false;
	function nutzerEditorCreateClicked() {
		nutzerEditorCreate = true;
	}    
	let nutzerEditorUpdate = false;
	let nutzerEditorUpdateId = undefined;
	function nutzerEditorUpdateClicked(id) {
		nutzerEditorUpdateId = id;
		nutzerEditorUpdate = true;
	}
	$: nutzerEditorDisabled = nutzerEditorCreate || nutzerEditorUpdate;

	let allSpracheItem = [];

    onMount(async () => {
        try {
            allNutzerValue = await loadAllValue('/api/nutzer/search/findAllByOrderByMailAsc');
            console.log(['onMount', allNutzerValue]);
			allSpracheItem = await loadAllValue('/api/enum/sprache');
            console.log(['onMount', allSpracheItem]);
        } catch(err) {
			console.log(['onMount', err]);
			toast.push(err.toString());
        };
	});

	let filterPrefix = '';
	function filterNutzer(prefix,allValue) {
		nutzerIndexOf = undefined;
		if (!filterPrefix) return allValue;
		return allValue.filter(e => {
			for (const s of e.name.split(" ")) {
				if (s.toLowerCase().startsWith(prefix.toLowerCase())) {
					return true;
				}
			}
            if (e.mail.toLowerCase().startsWith(prefix.toLowerCase())) {
                return true;
            }
            return false;
		})
	}
	$: allNutzerValueFiltered = filterNutzer(filterPrefix,allNutzerValue);
 
	function createNutzer(nutzer) {
		createValue('/api/nutzer', nutzer)
		.then(() => {
			return loadAllValue('/api/nutzer/search/findAllByOrderByMailAsc');
		})
		.then(json => {
			console.log(json);
			allNutzerValue = json;
		})
		.catch(err => {
			console.log(err);
			toast.push(err.toString());
		});
	};

	function updateNutzer(nutzer) {
		updatePatch('/api/nutzer/' + nutzer.id, nutzer)
		.then(() => {
			return loadAllValue('/api/nutzer/search/findAllByOrderByMailAsc');
		})
		.then(json => {
			console.log(json);
			allNutzerValue = json;
		})
		.catch(err => {
			console.log(err);
			toast.push(err.toString());
		});
	};

	function removeNutzer(nutzer) {
		if (!confirm("Nutzer '" + nutzer.name + "' wirklich löschen?")) return;
		removeValue('/api/nutzer/' + nutzer.id)
		.then(() => {
			return loadAllValue('/api/nutzer/search/findAllByOrderByMailAsc');
		})
		.then(json => {
			console.log(json);
			allNutzerValue = json;
		})
		.catch(err => {
			console.log(err);
			toast.push(err.toString());
		});
	};
</script>

<h1>Nutzer</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
	<div class="flex-grow">
		<h4 title="Filter für die Nutzer, nicht case-sensitiv">
			Aktueller Filter
		</h4>
		<TextField bind:value={filterPrefix}
			label="Filter" 
			placeholder="Bitte Filterkriterien eingeben"/>
		<h4 title="Liste der Nutzer, ggfs. gefiltert, jedes Element editierbar">
			Aktuelle Nutzer <small>({allNutzerValueFiltered.length})</small>
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
					<th class="px-2 py-3 border-b-2 border-gray-300 text-left w-2/3">
						<span class="text-gray-600">E-Mail</span>
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 w-16">
						<Icon on:click={() => nutzerEditorCreateClicked()}
							disabled={nutzerEditorDisabled}
							name="edit"
                            outlined/>
					</th>
				</tr>
			</thead>
			<tbody>
				{#if nutzerEditorCreate}
				<tr>
					<td>
					</td>
					<td colspan="2">
						<NutzerEditor
							bind:visible={nutzerEditorCreate} 
							on:create={e => createNutzer(e.detail)}
							{allSpracheItem}/>
					<td>
				</tr>
				{/if}
				{#each allNutzerValueFiltered as nutzer, i}
				<tr on:click={e => onNutzerClicked(i)}
					title={nutzer.id}
					class:ring={nutzerIndexOf === i}>
					<td class="px-2 py-3">
						<Checkbox bind:checked={nutzer.aktiv}
							on:change={() => updateNutzer(nutzer)} />
					</td>
					<td class="px-2 py-3 text-left">
						<span>{nutzer.name}</span>
					</td>
					<td class="px-2 py-3 text-left">
						<div class="text-sm underline text-blue-600">
							<a href={'/nutzer/' + nutzer.id}>{nutzer.mail}</a>
						</div>
					</td>
					<td class="px-2 py-3">
						<Icon on:click={() => nutzerEditorUpdateClicked(nutzer.id)}
							disabled={nutzerEditorDisabled}
							name="edit"
                            outlined/>
					</td>
				</tr>
				{#if nutzerEditorUpdate && nutzerEditorUpdateId === nutzer.id}
				<tr>
					<td>
					</td>
					<td	colspan="2">
						<NutzerEditor
							bind:visible={nutzerEditorUpdate} 
							on:update={e => updateNutzer(e.detail)}
							on:remove={e => removeNutzer(e.detail)}
							{nutzer}
							{allSpracheItem}/>
					<td>
				</tr>
				{/if}
				{:else}
				<tr>
					<td class="px-2 py-3" colspan="4">
						Keine Nutzer
					</td>
				</tr>
				{/each}
			</tbody>
		</table>
	</div>
</div>

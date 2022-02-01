<script>
	import { onMount } from 'svelte';
	import Icon from '../components/Icon';
	import TextField from '../components/TextField';
	import { toast } from '../components/Toast';
	import { loadAllValue } from '../utils/rest.js';
	import { createValue } from '../utils/rest.js';
	import { updatePatch } from '../utils/rest.js';
	import { removeValue } from '../utils/rest.js';
	import VetEditor from './VetEditor.svelte';

	let allVet = [];
	let vetIndexOf = undefined;
	function onVetClicked(index) {
		vetIndexOf = index;
	}

	let vetEditorCreate = false;
	let vetEditorUpdate = false;
	let vetEditorUpdateId = undefined;
	$: vetEditorDisabled = vetEditorCreate || vetEditorUpdate;
	function vetEditorCreateClicked() {
		vetEditorCreate = true;
	}    
	function vetEditorUpdateClicked(id) {
		vetEditorUpdateId = id;
		vetEditorUpdate = true;
	}

    onMount(async () => {
        try {
            allVet = await loadAllValue('/api/vet/search/findAllByOrderByNameAsc');
            console.log(['onMount', allVet]);
        } catch(err) {
			console.log(['onMount', err]);
			toast.push(err.toString());
        };
	});

	let filterPrefix = '';
	$: allVetFiltered = filterVet(filterPrefix,allVet);
	function filterVet(prefix,allValue) {
		vetIndexOf = undefined;
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
 
	function createVet(vet) {
		createValue('/api/vet', vet)
		.then(() => {
			return loadAllValue('/api/vet/search/findAllByOrderByNameAsc');
		})
		.then(json => {
			console.log(json);
			allVet = json;
		})
		.catch(err => {
			console.log(err);
			toast.push(err.toString());
		});
	};

	function updateVet(vet) {
		updatePatch('/api/vet/' + vet.id, vet)
		.then(() => {
			return loadAllValue('/api/vet/search/findAllByOrderByNameAsc');
		})
		.then(json => {
			console.log(json);
			allVet = json;
		})
		.catch(err => {
			console.log(err);
			toast.push(err.toString());
		});
	};

	function removeVet(vet) {
		if (!confirm("Really delete '" + vet.name + "'?")) return;
		removeValue('/api/vet/' + vet.id)
		.then(() => {
			return loadAllValue('/api/vet/search/findAllByOrderByNameAsc');
		})
		.then(json => {
			console.log(json);
			allVet = json;
		})
		.catch(err => {
			console.log(err);
			toast.push(err.toString());
		});
	};
</script>

<h1>Vet <span class="text-sm">({allVetFiltered.length})</span></h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
	<div class="flex-grow">
		<TextField bind:value={filterPrefix}
			label="Filter" 
			placeholder="Insert a criteria"/>
		<table class="table-fixed">
			<thead class="justify-between">
				<tr class="bg-gray-100">
					<th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/3">
						<span class="text-gray-600">Name</span>
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 text-left w-full">
						<span class="text-gray-600">Skills</span>
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 w-16">
						<Icon on:click={() => vetEditorCreateClicked()}
							disabled={vetEditorDisabled}
							name="edit"
                            outlined/>
					</th>
				</tr>
			</thead>
			<tbody>
				{#if vetEditorCreate}
				<tr>
					<td colspan="2">
						<VetEditor
							bind:visible={vetEditorCreate} 
							on:create={e => createVet(e.detail)}/>
					<td>
				</tr>
				{/if}
				{#each allVetFiltered as vet, i}
				<tr on:click={e => onVetClicked(i)}
					title={vet.id}
					class:ring={vetIndexOf === i}>
					<td class="px-2 py-3 text-left">
						<div class="text-sm underline text-blue-600">
							<a href={'/vet/' + vet.id}>{vet.name}</a>
						</div>
					</td>
					<td class="px-2 py-3 text-left">
					</td>
					<td class="px-2 py-3">
						<Icon on:click={() => vetEditorUpdateClicked(vet.id)}
							disabled={vetEditorDisabled}
							name="edit"
                            outlined/>
					</td>
				</tr>
				{#if vetEditorUpdate && vetEditorUpdateId === vet.id}
				<tr>
					<td	colspan="2">
						<VetEditor
							bind:visible={vetEditorUpdate} 
							on:update={e => updateVet(e.detail)}
							on:remove={e => removeVet(e.detail)}
							{vet}/>
					<td>
				</tr>
				{/if}
				{:else}
				<tr>
					<td class="px-2 py-3" colspan="3">
						No vets
					</td>
				</tr>
				{/each}
			</tbody>
		</table>
	</div>
</div>

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
	import VisitViewer from './VisitViewer.svelte';

	let allVet = [];
	let vetIndexOf = undefined;
	let vetId = undefined;
	function onVetClicked(index) {
		vetIndexOf = index;		
		reloadAllVisit(allVet[index]);
	}

	let vetEditorCreate = false;
	let vetEditorUpdate = false;
	$: vetEditorDisabled = vetEditorCreate || vetEditorUpdate;
	function vetEditorCreateClicked() {
		vetEditorCreate = true;
		visitViewerCreate = false;
	}    
	function vetEditorUpdateClicked(vet) {
		vetId = vet.id;
		vetEditorUpdate = true;
		visitViewerCreate = false;
	}
	
	let allVisit = [];

	let visitViewerCreate = false;
	function visitViewerCreateClicked(vet) {
		vetId = vet.id;
		visitViewerCreate = !visitViewerCreate;
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
			console.log(['createVet', json]);
			allVet = json;
		})
		.catch(err => {
			console.log(['createVet', err]);
			toast.push(err.toString());
		});
	};

	function updateVet(vet) {
		updatePatch('/api/vet/' + vet.id, vet)
		.then(() => {
			return loadAllValue('/api/vet/search/findAllByOrderByNameAsc');
		})
		.then(json => {
			console.log(['updateVet', json]);
			allVet = json;
		})
		.catch(err => {
			console.log(['updateVet', err]);
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
			console.log(['removeVet', json]);
			allVet = json;
		})
		.catch(err => {
			console.log(['removeVet', err]);
			toast.push(err.toString());
		});
	};

	function reloadAllVisit(vet) {
		loadAllValue('/api/visit/search/findAllByVet?vetId=' + vet.id)
		.then(json => {
			console.log(['reloadAllVisit', json]);
			allVisit = json;
		})
		.catch(err => {
			console.log(['reloadAllVisit', err]);
			toast.push(err.toString());
		});
	}
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
					<th class="px-2 py-3 border-b-2 border-gray-300 text-left w-2/3">
						<span class="text-gray-600">Skills</span>
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 w-16">
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
					<td colspan="3">
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
						{#each vet.allSkill as skill}
						<div class="flex flex-col">
							<span>{skill}</span>
						</div>
						{:else}
						<span>No special skills</span>
						{/each}
					</td>
					<td class="px-2 py-3">
						<Icon on:click={() => visitViewerCreateClicked(vet)}
							title="Show all visits"
							disabled={vetEditorDisabled}
							name="list"
                            outlined/>
					</td>
					<td class="px-2 py-3">
						<Icon on:click={() => vetEditorUpdateClicked(vet)}
							title="Edit vet details"
							disabled={vetEditorDisabled}
							name="edit"
                            outlined/>
					</td>
				</tr>
				{#if visitViewerCreate && vetId === vet.id}
				<tr>
					<td class="px-4" colspan="6">
						<VisitViewer
							bind:visible={visitViewerCreate} 
							{allVisit}/>
					<td>
				</tr>
				{/if}
				{#if vetEditorUpdate && vetId === vet.id}
				<tr>
					<td	colspan="3">
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

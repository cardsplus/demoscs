<script>
	import { onMount } from 'svelte';
	import Icon from '../components/Icon';
	import TextArea from '../components/TextArea';
	import TextField from '../components/TextField';
	import { toast } from '../components/Toast';
	import { loadAllValue } from '../utils/rest.js';
	import { createValue } from '../utils/rest.js';
	import { updatePatch } from '../utils/rest.js';
	import { removeValue } from '../utils/rest.js';
	import OwnerEditor from './OwnerEditor.svelte';
	import VisitEditor from './VisitEditor.svelte';
	import VisitViewer from './VisitViewer.svelte';

	let allOwner = [];
	let ownerIndexOf = undefined;
	let ownerId = undefined;
	function onOwnerClicked(index) {
		ownerIndexOf = index;
		reloadAllVisit(allOwner[index]);
	}

	let ownerEditorCreate = false;
	let ownerEditorUpdate = false;
	$: ownerEditorDisabled = ownerEditorCreate || ownerEditorUpdate;
	function ownerEditorCreateClicked() {
		ownerEditorCreate = true;
	}    
	function ownerEditorUpdateClicked(owner) {
		ownerId = owner.id;
		ownerEditorUpdate = true;
	}
	
	let allVisit = [];

	let visitEditorCreate = false;
	function visitEditorCreateClicked(owner) {
		ownerId = owner.id;
		visitEditorCreate = true;
	}

	let visitViewerCreate = false;
	function visitViewerCreateClicked(owner) {
		ownerId = owner.id;
		visitViewerCreate = true;
	}

	let allVetItem = [];

    onMount(async () => {
        try {
            allOwner = await loadAllValue('/api/owner/search/findAllByOrderByNameAsc');
            console.log(['onMount', allOwner]);
            allVetItem = await loadAllValue('/api/vet/search/findAllItem');
            console.log(['onMount', allVetItem]);
        } catch(err) {
			console.log(['onMount', err]);
			toast.push(err.toString());
        };
	});

	let filterPrefix = '';
	$: allOwnerFiltered = filterOwner(filterPrefix,allOwner);
	function filterOwner(prefix,allValue) {
		ownerIndexOf = undefined;
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
 
	function createOwner(owner) {
		createValue('/api/owner', owner)
		.then(() => {
			return loadAllValue('/api/owner/search/findAllByOrderByNameAsc');
		})
		.then(json => {
			console.log(json);
			allOwner = json;
		})
		.catch(err => {
			console.log(err);
			toast.push(err.toString());
		});
	};

	function updateOwner(owner) {
		updatePatch('/api/owner/' + owner.id, owner)
		.then(() => {
			return loadAllValue('/api/owner/search/findAllByOrderByNameAsc');
		})
		.then(json => {
			console.log(json);
			allOwner = json;
		})
		.catch(err => {
			console.log(err);
			toast.push(err.toString());
		});
	};

	function removeOwner(owner) {
		if (!confirm("Really delete '" + owner.name + "'?")) return;
		removeValue('/api/owner/' + owner.id)
		.then(() => {
			return loadAllValue('/api/owner/search/findAllByOrderByNameAsc');
		})
		.then(json => {
			console.log(json);
			allOwner = json;
		})
		.catch(err => {
			console.log(err);
			toast.push(err.toString());
		});
	};

	function reloadAllVisit(owner) {
		loadAllValue('/api/visit/search/findAllByOwner?ownerId=' + owner.id)
		.then(json => {
			console.log(json);
			allVisit = json;
		})
		.catch(err => {
			console.log(err);
			toast.push(err.toString());
		});
	}
 
 function createVisit(visit) {
	 createValue('/api/visit', visit)
	 .then(() => {
		 return loadAllValue('/api/owner/search/findAllByOrderByNameAsc');
	 })
	 .then(json => {
		 console.log(json);
		 allOwner = json;
	 })
	 .catch(err => {
		 console.log(err);
		 toast.push(err.toString());
	 });
 };
</script>

<h1>Owner <span class="text-sm">({allOwnerFiltered.length})</span></h1>
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
						<span class="text-gray-600">Pets</span>
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 w-16">
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 w-16">
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 w-16">
						<Icon on:click={() => ownerEditorCreateClicked()}
							disabled={ownerEditorDisabled}
							name="edit"
                            outlined/>
					</th>
				</tr>
			</thead>
			<tbody>
				{#if ownerEditorCreate}
				<tr>
					<td colspan="2">
						<OwnerEditor
							bind:visible={ownerEditorCreate} 
							on:create={e => createOwner(e.detail)}/>
					<td>
				</tr>
				{/if}
				{#each allOwnerFiltered as owner, i}
				<tr on:click={e => onOwnerClicked(i)}
					title={owner.id}
					class:ring={ownerIndexOf === i}>
					<td class="px-2 py-3 text-left">
						<div class="text-sm underline text-blue-600">
							<a href={'/owner/' + owner.id}>{owner.name}</a>
						</div>
					</td>
					<td class="px-2 py-3 text-left">						
						<div class="flex flex-col">
							{#each owner.allPetItem as petItem}
							<span>{petItem.text}</span>
							{:else}
							<span>No pets</span>
							{/each}
						</div>
					</td>
					<td class="px-2 py-3">
						<Icon on:click={() => visitViewerCreateClicked(owner)}
							title="Show all visits"
							name="list"
                            outlined/>
					</td>
					<td class="px-2 py-3">
						<Icon on:click={() => visitEditorCreateClicked(owner)}
							title="Add a new visit"
							disabled={visitEditorCreate}
							name="event"
                            outlined/>
					</td>
					<td class="px-2 py-3">
						<Icon on:click={() => ownerEditorUpdateClicked(owner)}
							title="Edit owner"
							disabled={ownerEditorDisabled}
							name="edit"
                            outlined/>
					</td>
				</tr>
				{#if visitViewerCreate && ownerId === owner.id}
				<tr>
					<td class="px-4" colspan="5">
						<VisitViewer
							bind:visible={visitViewerCreate} 
							{allVisit}/>
					<td>
				</tr>
				{/if}
				{#if visitEditorCreate && ownerId === owner.id}
				<tr>
					<td class="px-4" colspan="5">
						<VisitEditor
							bind:visible={visitEditorCreate} 
							on:create={e => createVisit(e.detail)}
							allPetItem={owner.allPetItem}
							allVetItem={allVetItem}/>
					<td>
				</tr>
				{/if}
				{#if ownerEditorUpdate && ownerId === owner.id}
				<tr>
					<td class="px-4" colspan="5">
						<OwnerEditor
							bind:visible={ownerEditorUpdate} 
							on:update={e => updateOwner(e.detail)}
							on:remove={e => removeOwner(e.detail)}
							{owner}/>
					<td>
				</tr>
				{/if}
				{:else}
				<tr>
					<td class="px-2 py-3" colspan="5">
						No owners
					</td>
				</tr>
				{/each}
			</tbody>
		</table>
	</div>
</div>

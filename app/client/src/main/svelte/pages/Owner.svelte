<script>
	import { onMount } from 'svelte';
	import Icon from '../components/Icon';
	import TextField from '../components/TextField';
	import { toast } from '../components/Toast';
	import { loadAllValue } from '../utils/rest.js';
	import { createValue } from '../utils/rest.js';
	import { updatePatch } from '../utils/rest.js';
	import { removeValue } from '../utils/rest.js';
	import OwnerEditor from './OwnerEditor.svelte';

	let allOwnerValue = [];
	let ownerIndexOf = undefined;
	function onOwnerClicked(index) {
		ownerIndexOf = index;
	}

	let ownerEditorCreate = false;
	function ownerEditorCreateClicked() {
		ownerEditorCreate = true;
	}    
	let ownerEditorUpdate = false;
	let ownerEditorUpdateId = undefined;
	$: ownerEditorDisabled = ownerEditorCreate || ownerEditorUpdate;
	function ownerEditorUpdateClicked(id) {
		ownerEditorUpdateId = id;
		ownerEditorUpdate = true;
	}

    onMount(async () => {
        try {
            allOwnerValue = await loadAllValue('/api/owner/search/findAllByOrderByNameAsc');
            console.log(['onMount', allOwnerValue]);
        } catch(err) {
			console.log(['onMount', err]);
			toast.push(err.toString());
        };
	});

	let filterPrefix = '';
	$: allOwnerValueFiltered = filterOwner(filterPrefix,allOwnerValue);
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
			allOwnerValue = json;
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
			allOwnerValue = json;
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
			allOwnerValue = json;
		})
		.catch(err => {
			console.log(err);
			toast.push(err.toString());
		});
	};
</script>

<h1>Owner <span class="text-sm">({allOwnerValueFiltered.length})</span></h1>
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
				{#each allOwnerValueFiltered as owner, i}
				<tr on:click={e => onOwnerClicked(i)}
					title={owner.id}
					class:ring={ownerIndexOf === i}>
					<td class="px-2 py-3 text-left">
						<div class="text-sm underline text-blue-600">
							<a href={'/owner/' + owner.id}>{owner.name}</a>
						</div>
					</td>
					<td class="px-2 py-3 text-left">
					</td>
					<td class="px-2 py-3">
						<Icon on:click={() => ownerEditorUpdateClicked(owner.id)}
							disabled={ownerEditorDisabled}
							name="edit"
                            outlined/>
					</td>
				</tr>
				{#if ownerEditorUpdate && ownerEditorUpdateId === owner.id}
				<tr>
					<td	colspan="2">
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
					<td class="px-2 py-3" colspan="3">
						No owners
					</td>
				</tr>
				{/each}
			</tbody>
		</table>
	</div>
</div>

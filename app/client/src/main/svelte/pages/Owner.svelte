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

	let allOwner = [];
	let ownerIndexOf = undefined;
	function onOwnerClicked(index) {
		ownerIndexOf = index;
		reloadAllVisit(allOwner[index]);
	}

	let ownerEditorCreate = false;
	let ownerEditorUpdate = false;
	let ownerEditorUpdateId = undefined;
	$: ownerEditorDisabled = ownerEditorCreate || ownerEditorUpdate;
	function ownerEditorCreateClicked() {
		ownerEditorCreate = true;
	}    
	function ownerEditorUpdateClicked(id) {
		ownerEditorUpdateId = id;
		ownerEditorUpdate = true;
	}

	let allVisitByDate = new Map();
	let visitIndexOf = undefined;
	function onVisitClicked(index) {
		visitIndexOf = index;
	}

    onMount(async () => {
        try {
            allOwner = await loadAllValue('/api/owner/search/findAllByOrderByNameAsc');
            console.log(['onMount', allOwner]);
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
			allVisitByDate = new Map();
			json.forEach(e => {
				let k = '"' + e.petItem.text + '" on ' + e.date;		
				let v = allVisitByDate.get(k);
				if (v) {
					allVisitByDate.set(k, [...v, e]);
				} else {
					allVisitByDate.set(k, [e]);
				}
			});
		})
		.catch(err => {
			console.log(err);
			toast.push(err.toString());
		});
	}
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
	
	{#each [...allVisitByDate] as [titel, allVisit]}
	<details>
		<summary>{titel}</summary>
		<div class="ml-4">
		{#each allVisit as visit}
		<TextField value={visit.vetItem.text}
			label="Veterinarian" 
			disabled/>
		<TextArea value={visit.text}
			label="Diagnosis" 
			disabled/>
		{/each}
		</div>
	</details>
	{/each}
</div>

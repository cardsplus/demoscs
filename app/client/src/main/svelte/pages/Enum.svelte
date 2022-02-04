<script>
	import { onMount } from 'svelte';
	import Icon from '../components/Icon';
	import TextField from '../components/TextField';
	import { toast } from '../components/Toast';
	import { loadAllValue } from '../utils/rest.js';
	import { createValue } from '../utils/rest.js';
	import { updateValue } from '../utils/rest.js';
	import { removeValue } from '../utils/rest.js';
	import EnumEditor from './EnumEditor.svelte';

	export let art;

	let allItem = [];
	let itemIndexOf = undefined;
	let itemCode = undefined;
	function onItemClicked(index) {
		itemIndexOf = index;
	}

	let itemEditorCreate = false;
	let itemEditorUpdate = false;
	$: itemEditorDisabled = itemEditorCreate || itemEditorUpdate;
	function itemEditorCreateClicked() {
		itemEditorCreate = true;
	}
	function itemEditorUpdateClicked(item) {
		itemEditorUpdate = true;
		itemCode = item.code;
	}

    onMount(async () => {
        try {
            allItem = await loadAllValue('/api/enum/' + art);
            console.log(['onMount', allItem]);
        } catch(err) {
			console.log(['onMount', err]);
			toast.push(err.toString());
        };
	});

	let filterPrefix = '';
	$: allItemFiltered = filterEnum(filterPrefix, allItem);
	function filterEnum(prefix,allValue) {
		itemIndexOf = undefined;
		if (!filterPrefix) return allValue;
		return allValue.filter(e => {
			if (e.name.toLowerCase().startsWith(prefix.toLowerCase())) {
				return true;
			}
			return false;
		})
	}

	function createItem(item) {
		createValue('/api/enum/' + art, item)
		.then(() => {
			return loadAllValue('/api/enum/' + art);
		})
		.then(json => {
			console.log(['createItem', json]);
			allItem = json;
		})
		.catch(err => {
			console.log(['createItem', err]);
			toast.push(err.toString());
		});
	};

	function updateItem(item) {
		updateValue('/api/enum/' + art + '/' + item.code, item)
		.then(() => {
			return loadAllValue('/api/enum/' + art);
		})
		.then(json => {
			console.log(['updateItem', json]);
			allItem = json;
		})
		.catch(err => {
			console.log(['updateItem', err]);
			toast.push(err.toString());
		});
	};

	function removeItem(item) {
		if (!confirm("Really delete '" +  item.name + "'?")) return;
		removeValue('/api/enum/' + art + '/' + item.code)
		.then(() => {
			return loadAllValue('/api/enum/' + art);
		})
		.then(json => {
			console.log(['removeItem', json]);
			allItem = json;
		})
		.catch(err => {
			console.log(['removeItem', err]);
			toast.push(err.toString());
		});
	};
</script>

<h1>{art.toUpperCase()} <span class="text-sm">({allItemFiltered.length})</span></h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
	<div class="flex-grow">
		<TextField bind:value={filterPrefix}
			label="Filter"
			placeholder="Insert a criteria"/>
		<table class="table-fixed">
			<thead class="justify-between">
				<tr class="bg-gray-100">
					<th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/4">
						<span class="text-gray-600">Code</span>
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/4">
						<span class="text-gray-600">Name</span>
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/2">
						<span class="text-gray-600">Text</span>
					</th>
					<th class="px-2 py-3 border-b-2 border-gray-300 w-16">
						<Icon on:click={() => itemEditorCreateClicked()}
							disabled={itemEditorDisabled}
							name="edit"
                            outlined/>
					</th>
				</tr>
			</thead>
			<tbody>
				{#if itemEditorCreate}
				<tr>
					<td	class="px-4" colspan="4">
						<EnumEditor
							bind:visible={itemEditorCreate}
							on:create={e => createItem(e.detail)}
							code={allItem.length}/>
					<td>
				</tr>
				{/if}
				{#each allItemFiltered as item, i}
				<tr on:click={e => onItemClicked(i)}
					title={item.id}
					class:ring={itemIndexOf === i}>
					<td class="px-2 py-3 text-left">
						<span>{item.code}</span>
					</td>
					<td class="px-2 py-3 text-left">
						<span>{item.name}</span>
					</td>
					<td class="px-2 py-3 text-left">
						<span>{item.text}</span>
					</td>
					<td class="px-2 py-3">
						<Icon on:click={() => itemEditorUpdateClicked(item)}
							disabled={itemEditorDisabled}
							name="edit"
                            outlined/>
					</td>
				</tr>
				{#if itemEditorUpdate && itemCode === item.code}
				<tr>
					<td class="px-4" colspan="4">
						<EnumEditor
							bind:visible={itemEditorUpdate}
							on:update={e => updateItem(e.detail)}
							on:remove={e => removeItem(e.detail)}
							code={item.code}
							{item}/>
					<td>
				</tr>
				{/if}
				{:else}
				<tr>
					<td class="px-2 py-3" colspan="4">
						No items
					</td>
				</tr>
				{/each}
			</tbody>
		</table>
	</div>
</div>

<script>
	import { onMount } from 'svelte';
	import Icon from '../components/Icon';
	import TextField from '../components/TextField';
	import { toast } from '../components/Toast';
	import { loadAllValue } from '../utils/rest.js';
	import { createValue } from '../utils/rest.js';
	import { updatePatch } from '../utils/rest.js';
	import { removeValue } from '../utils/rest.js';
	import EnumEditor from './EnumEditor.svelte';

	export let art;

	let allItem = [];
	let itemIndexOf = undefined;
	function onItemClicked(index) {
		itemIndexOf = index;
	}

	let itemEditorCreate = false;
	function itemEditorCreateClicked() {
		itemEditorCreate = true;
	}
	let itemEditorUpdate = false;
	let itemEditorUpdateCode = undefined;
	function itemEditorUpdateClicked(code) {
		itemEditorUpdateCode = code;
		itemEditorUpdate = true;
	}
	$: itemEditorDisabled = itemEditorCreate || itemEditorUpdate;

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
	$: allItemFiltered = filterEnum(filterPrefix, allItem);

	function createItem(item) {
		createValue('/api/enum/' + art, item)
		.then(() => {
			return loadAllValue('/api/enum/' + art);
		})
		.then(json => {
			console.log(json);
			allItem = json;
		})
		.catch(err => {
			console.log(err);
			toast.push(err.toString());
		});
	};

	function updateItem(item) {
		updatePatch('/api/enum/' + art + '/' + item.id, item)
		.then(() => {
			return loadAllValue('/api/enum/' + art);
		})
		.then(json => {
			console.log(json);
			allItem = json;
		})
		.catch(err => {
			console.log(err);
			toast.push(err.toString());
		});
	};

	function removeItem(item) {
		if (!confirm("Enum '" + item.name + "' wirklich löschen?")) return;
		removeValue('/api/enum/' + art + '/' + item.id)
		.then(() => {
			return loadAllValue('/api/enum/' + art);
		})
		.then(json => {
			console.log(json);
			allItem = json;
		})
		.catch(err => {
			console.log(err);
			toast.push(err.toString());
		});
	};
</script>

<h1>{art.toUpperCase()}</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
	<div class="flex-grow">
		<h4 title="Filter für die Werte, nicht case-sensitiv">
			Aktueller Filter
		</h4>
		<TextField bind:value={filterPrefix}
			label="Filter"
			placeholder="Bitte Filterkriterien eingeben"/>
		<h4 title="Liste der Werte, ggfs. gefiltert, jedes Element editierbar">
			Aktuelle Werte <small>({allItemFiltered.length})</small>
		</h4>
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
					<td	colspan="3">
						<EnumEditor
							bind:visible={itemEditorCreate}
							on:create={e => createItem(e.detail)}/>
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
						<Icon on:click={() => itemEditorUpdateClicked(item.code)}
							disabled={itemEditorDisabled}
							name="edit"
                            outlined/>
					</td>
				</tr>
				{#if itemEditorUpdate && itemEditorUpdateCode === item.code}
				<tr>
					<td colspan="3">
						<EnumEditor
							bind:visible={itemEditorUpdate}
							on:update={e => updateItem(e.detail)}
							on:remove={e => removeItem(e.detail)}
							{item}/>
					<td>
				</tr>
				{/if}
				{:else}
				<tr>
					<td class="px-2 py-3" colspan="4">
						Keine Werte
					</td>
				</tr>
				{/each}
			</tbody>
		</table>
	</div>
</div>

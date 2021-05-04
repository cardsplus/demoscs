<script>
   	import { onMount } from 'svelte';
  	import { Button, Checkbox, Dialog, Snackbar, TextField } from "smelte";
    import { loadAllValue, createValue, updateValue, removeValue } from '../utils/rest.js';

	let alertSnackbarDialog = false;
	let alertSnackbarText = 'ok';

	const projektUrl = '/api/projekt';
	let allProjekt = [];
    onMount(async () => {
		loadAllValue(projektUrl + "?sort=name")
        .then(res => res.json())
        .then(json => {
			console.log(json);
			filterPrefix = '';
            allProjekt = json.content;
			projektIndexOf = 0;
        })
        .catch(err => {
			alertSnackbarText = err;
			alertSnackbarDialog = true;
        });
	});
	
	let projektName = '';
	let projektAktiv = true;
	let projektSprache = "DE";	
	let projektSpracheDialog = false;
	let projektIndexOf = undefined;

	// refresh after change in filter criteria
	let filterPrefix = '';
	$: allProjektFiltered = filterPrefix
		? allProjekt.filter(projekt => {
            if (`${projekt.name}`.toLowerCase().startsWith(filterPrefix.toLowerCase())) {
                return true;
            }
            return false;
		})
		: allProjekt;

	// refresh after change of unique mail address
	$: projektSelected = onChangedValue(allProjekt.find(projekt => projekt.name == projektName));
	function onChangedValue(projekt) {
		if (projekt) {
			projektName = projekt.name;
			projektAktiv = projekt.aktiv;
			projektSprache = projekt.sprache;
			projektIndexOf = allProjektFiltered.indexOf(projekt);
		} else {			
			projektIndexOf = undefined;
		}
		return projekt;
	}

	// refresh after change of projektIndexOf list item
	$: projektSelected = onChangedIndex(allProjektFiltered[projektIndexOf]);
	function onChangedIndex(projekt) {
		if (projekt) {
			projektName = projekt.name;
			projektAktiv = projekt.aktiv;
			projektSprache = projekt.sprache;
		} else {
			// don't reset unique key value
			projektAktiv = true;
			projektSprache = "DE";
		}
		return projekt;
	}

	$: disableCreate = !projektName || projektIndexOf !== undefined;
	function create() {
		createValue(projektUrl, {
			name: projektName, 
			aktiv: projektAktiv,
			sprache: projektSprache
		})
        .then(res => res.json())
        .then(json => {
			console.log(json);
			filterPrefix = '';
			allProjekt = [...allProjekt, json];
			projektIndexOf = allProjekt.length - 1;
        })
        .catch(err => {
			alertSnackbarText = err;
			alertSnackbarDialog = true;
        });
	}

	$: disableUpdate = !projektName || projektIndexOf === undefined;
	function update() {
		updateValue(projektUrl + '/' + projektSelected.id, {
			name: projektName, 
			aktiv: projektAktiv,
			sprache: projektSprache
		})
        .then(res => res.json())
        .then(json => {
			console.log(json);
			const i = allProjekt.indexOf(projektSelected);
			allProjekt = [...allProjekt.slice(0, i), json, ...allProjekt.slice(i + 1)];
        })
        .catch(err => {
			alertSnackbarText = err;
			alertSnackbarDialog = true;
        });
	}
	function remove() {
		if (!confirm("Projekt '" + projektName + "' wirklich löschen?")) return;
		removeValue(projektUrl + '/' + projektSelected.id)
        .then(res => {
			filterPrefix = '';
			const i = allProjekt.indexOf(projektSelected);
			allProjekt = [...allProjekt.slice(0, i), ...allProjekt.slice(i + 1)];
			projektIndexOf = 0;
        })
        .catch(err => {
			alertSnackbarText = err;
			alertSnackbarDialog = true;
		});
	}
</script>

<Snackbar bind:value={alertSnackbarDialog} bottom left color="alert" timeout={2000}>
	<div>{alertSnackbarText}</div>
</Snackbar>

<h1>Projekt</h1>
<div class="flex flex-col ml-2 mr-2 space-y-2 sm:flex-row sm:space-x-2 sm:space-y-0">
	<div class="flex-grow">
		<h2 title="Filter für die gespeicherte Projekte">Aktueller Filter</h2>
		<TextField bind:value={filterPrefix}
			label="Filter" 
			placeholder="Bitte Filterkriterien eingeben"/>
		<h2 title="Liste der gespeicherte Projekte, filterbar, editierbar">Aktuelle Liste</h2>
		<table class="table-fixed">
			<thead class="justify-between">
				<tr class="bg-gray-100">
					<th class="px-6 py-3 border-b-2 border-gray-300 text-center w-1">
						<span class="text-gray-600">#</span>
					</th>
					<th class="px-6 py-3 border-b-2 border-gray-300 text-left w-full">
						<span class="text-gray-600">Name</span>
					</th>
				</tr>
			</thead>
			<tbody>
				{#each allProjektFiltered as projekt, i}
				<tr  on:click|preventDefault={e => projektIndexOf = i}
					title={projekt.id}
					class:bg-gray-50={i === projektIndexOf}>
					<td class="px-6 py-3 border-b-2 border-gray-300 text-center w-1">
						<span>{i + 1}</span>
					</td>
					<td class="px-6 py-3 border-b-2 border-gray-300 text-left w-full">
						<span>{projekt.name}</span>
					</td>
				</tr>
				{:else}
				<tr>
					<td class="px-6 py-3 text-center w-1" colspan="2">
						Keine Projekte
					</td>
				</tr>				
				{/each}
			</tbody>
		</table>
	</div>	
	<div class="space-y-2">
		{#if projektSelected}
		<h2>Gewähltes Projekt</h2>
		{:else}
		<h2>Neues Projekt</h2>
		{/if}
		<TextField bind:value={projektName}
			label="Name" 
			placeholder="Bitte den vollen Namen eingeben"/>
		<Checkbox bind:checked={projektAktiv}
			label="Bitte Haken setzen, wenn das Projekt aktiv ist?"/>
		<Button on:click={() => projektSpracheDialog=true}
			disabled={!projektName}
			outlined block>
			Sprache <small>({projektSprache})</small>
		</Button>
		<div class="py-2">
			<hr class="my-2"/>
		</div>
		<div class="buttons">
			<Button on:click={create} disabled={disableCreate}>
				Anlegen
			</Button>
			<Button on:click={update} disabled={disableUpdate}>
				Ändern
			</Button>
			<Button on:click={remove} disabled={disableUpdate}>
				Löschen
			</Button>
		</div>
	</div>
</div>

<Dialog bind:value={projektSpracheDialog} classes="z-50 bg-white p-4">
	<h5 slot="title">
		Projektsprache
	</h5>
	<div class="flex flex-col space-y-2">
		<label>
			<input type=radio bind:group={projektSprache} value="DE"/>
			Deutsch (DE)
		</label>
		<label>
			<input type=radio bind:group={projektSprache} value="EN"/>
			Englisch (EN)
		</label>
		<label>
			<input type=radio bind:group={projektSprache} value="IT"/>
			Italienisch (IT)
		</label>
	</div>
	<div slot="actions">
		<Button text on:click={() => projektSpracheDialog=false}>
			Ok
		</Button>
	</div>
</Dialog>

<style>
	h2 {
		font-size: 1.2em;
  		font-weight: bold;
		margin-top: 0;
		margin-bottom: 0.2em;
		text-transform :uppercase;
	}
	hr {
		border-width: 0.1em;
	}
</style>

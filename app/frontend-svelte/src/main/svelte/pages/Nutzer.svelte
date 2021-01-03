<script>
   	import { onMount } from 'svelte';
	import { Button, Checkbox, Dialog, Snackbar, TextField } from "smelte";
    import { loadAllValue, createValue, updateValue, removeValue } from '../utils/rest.js';

	let alertSnackbarDialog = false;
	let alertSnackbarText = 'ok';

	const nutzerUrl = '/api/nutzer';
	let allNutzer = [];
    onMount(async () => {
		loadAllValue(nutzerUrl + "?sort=name")
        .then(res => res.json())
        .then(json => {
			console.log(json);
			filterPrefix = '';
            allNutzer = json.content;
			nutzerIndexOf = 0;
        })
        .catch(err => {
			alertSnackbarText = err;
			alertSnackbarDialog = true;
        });
	});

	let nutzerMail = '';
	let nutzerAktiv = true;
	let nutzerName = '';
	let nutzerAllRolle = [];
	let nutzerAllRolleDialog = false;
	let nutzerIndexOf = undefined;

	// refresh after change in filter criteria
	let filterPrefix = '';
	$: allNutzerFiltered = filterPrefix
		? allNutzer.filter(nutzer => {
            if (`${nutzer.name}`.toLowerCase().startsWith(filterPrefix.toLowerCase())) {
                return true;
            }
            if (`${nutzer.mail}`.toLowerCase().startsWith(filterPrefix.toLowerCase())) {
                return true;
            }
            return false;
		})
		: allNutzer;

	// refresh after change of unique mail address
	$: nutzerSelected = onChangedValue(allNutzer.find(nutzer => nutzer.mail == nutzerMail));
	function onChangedValue(nutzer) {
		if (nutzer) {
			nutzerMail = nutzer.mail;
			nutzerAktiv = nutzer.aktiv;
			nutzerName = nutzer.name;
			nutzerAllRolle = nutzer.allRolle;
			nutzerIndexOf = allNutzerFiltered.indexOf(nutzer);
		} else {			
			nutzerIndexOf = undefined;
		}
		return nutzer;
	}

	// refresh after change of nutzerIndexOf list item
	$: nutzerSelected = onChangedIndex(allNutzerFiltered[nutzerIndexOf]);
	function onChangedIndex(nutzer) {
		if (nutzer) {
			nutzerMail = nutzer.mail;
			nutzerAktiv = nutzer.aktiv;
			nutzerName = nutzer.name;
			nutzerAllRolle = nutzer.allRolle;
		} else {
			// don't reset unique key value
			nutzerAktiv = true;
			nutzerName = '';
			nutzerAllRolle = ["BESUCHER"];
		}
		return nutzer;
	}

	$: disableCreate = !nutzerMail || !nutzerName || nutzerIndexOf !== undefined;
	function create() {
		createValue(nutzerUrl, {
			mail: nutzerMail,
			aktiv: nutzerAktiv,
			name: nutzerName, 
			allRolle: nutzerAllRolle
		})
        .then(res => res.json())
        .then(json => {
			console.log(json);
			filterPrefix = '';
			allNutzer = [...allNutzer, json];
			nutzerIndexOf = allNutzer.length - 1;
        })
        .catch(err => {
			alertSnackbarText = err;
			alertSnackbarDialog = true;
        });
	}

	$: disableUpdate = !nutzerMail || !nutzerName || nutzerIndexOf === undefined;
	function update() {
		updateValue(nutzerUrl + '/' + nutzerSelected.dataId, {
			mail: nutzerMail,
			aktiv: nutzerAktiv,
			name: nutzerName, 
			allRolle: nutzerAllRolle
        })
        .then(res => res.json())
        .then(json => {
			console.log(json);
			const i = allNutzer.indexOf(nutzerSelected);
			allNutzer = [...allNutzer.slice(0, i), json, ...allNutzer.slice(i + 1)];
        })
        .catch(err => {
			alertSnackbarText = err;
			alertSnackbarDialog = true;
        });
	}
	function remove() {
		if (!confirm("Nutzer '" + nutzerMail + "' wirklich löschen?")) return;
		removeValue(nutzerUrl + '/' + nutzerSelected.dataId)
        .then(res => {
			filterPrefix = '';
			const i = allNutzer.indexOf(nutzerSelected);
			allNutzer = [...allNutzer.slice(0, i), ...allNutzer.slice(i + 1)];
			nutzerIndexOf = 0;
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

<h1>Nutzer</h1>
<div class="flex flex-col ml-2 mr-2 space-y-2 sm:flex-row sm:space-x-2 sm:space-y-0">
	<div class="flex-grow">
		<h2 title="Filter für die gespeicherten Nutzer">
			Aktueller Filter
		</h2>
		<TextField bind:value={filterPrefix}
			label="Filter" 
			placeholder="Bitte Filterkriterien eingeben"/>
		<h2 title="List der gespeicherten Nutzer, filterbar, editierbar">
			Aktuelle Liste <small>({allNutzerFiltered.length})</small>
		</h2>
		<table class="table-fixed">
			<thead class="justify-between">
				<tr class="bg-gray-100">
					<th class="px-6 py-3 border-b-2 border-gray-300 text-center w-1">
						<span class="text-gray-600">#</span>
					</th>
					<th class="px-6 py-3 border-b-2 border-gray-300 text-left w-1/3">
						<span class="text-gray-600">Name</span>
					</th>
					<th class="px-6 py-3 border-b-2 border-gray-300 text-left w-full">
						<span class="text-gray-600">E-Mail</span>
					</th>
				</tr>
			</thead>
			<tbody>
				{#each allNutzerFiltered as nutzer, i}
				<tr class:bg-gray-50={i === nutzerIndexOf}>
					<td title={nutzer.dataId}
						class="px-6 py-3 border-b-2 border-gray-300 text-center w-1">
						<span>{i + 1}</span>
					</td>
					<td title={nutzer.dataId}
						class="px-6 py-3 border-b-2 border-gray-300 text-left w-1/3">
						<span>
							<a href on:click|preventDefault={e => nutzerIndexOf = i}>{nutzer.name}</a>							
						</span>
					</td>
					<td title={nutzer.dataId}
						class="px-6 py-3 border-b-2 border-gray-300 text-left w-full text-sm">
						<span>
							<a href on:click|preventDefault={e => nutzerIndexOf = i}>{nutzer.mail}</a>							
						</span>
					</td>
				</tr>
				{:else}
				<tr>
					<td colspan="3"
						class="px-6 py-3 text-center w-1">
						Keine Nutzer
					</td>
				</tr>
				{/each}
			</tbody>
		</table>
	</div>	
	<div class="space-y-2">
		{#if nutzerSelected}
		<h2>Gewählter Nutzer</h2>
		{:else}
		<h2>Neuer Nutzer</h2>
		{/if}
		<TextField bind:value={nutzerMail}
			label="Mail" 
			placeholder="Bitte eine E-Mail-Adresse eingeben"/>
		<TextField bind:value={nutzerName}
			label="Name"
			placeholder="Bitte den vollen Namen eingeben"/>
		<Checkbox bind:checked={nutzerAktiv}
			label="Bitte Haken setzen, wenn der Nutzer aktiv ist?"/>
		<Button on:click={() => nutzerAllRolleDialog=true} outlined block>
			Rollen <small>({nutzerAllRolle.length})</small>
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

<Dialog bind:value={nutzerAllRolleDialog} classes="z-50 bg-white p-4">
	<h5 slot="title">
		Rollen
	</h5>
	<div class="flex flex-col space-y-2">
		<label>
			<input type=checkbox bind:group={nutzerAllRolle} value="BESUCHER"/>
			Besucher
		</label>
		<label>
			<input type=checkbox bind:group={nutzerAllRolle} value="BEARBEITER"/>
			Bearbeiter
		</label>
		<label>
			<input type=checkbox bind:group={nutzerAllRolle} value="VERWALTER"/>
			Verwalter
		</label>
		<label>
			<input type=checkbox bind:group={nutzerAllRolle} value="ADMINISTRATOR"/>
			Administrator
		</label>
	</div>
	<div slot="actions">
		<Button text on:click={() => nutzerAllRolleDialog=false}>
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

<script>
    import { onMount } from 'svelte';
    import { Button, Checkbox, Select, Snackbar, TextField } from "smelte";
    import { loadAllValue, createValue, updateValue, removeValue } from '../utils/rest.js';

	let alertSnackbarDialog = false;
	let alertSnackbarText = 'ok';

    const projektUrl = '/api/projekt';
    let allProjektItem = [];
    let projektSelected = undefined;
    onMount(async () => {
		loadAllValue(projektUrl + "?sort=name")
        .then(res => res.json())
        .then(json => {
			console.log(json);
            allProjektItem = json.content.map(e => {
                return {
                    value: e,
                    text: e.name
                };
            });
            if (allProjektItem.length) {
                projektSelected = allProjektItem[0].value;
            }            
        })
        .catch(err => {
			alertSnackbarText = err;
			alertSnackbarDialog = true;
        });
    });

    $: if (projektSelected) reload();

    function reload() {
        loadAllValue(projektUrl + "/" + projektSelected.dataId + "/allAufgabe")
        .then(res => res.json())
        .then(json => {
			console.log(json);
            allAufgabe = json.content;
        })
        .catch(err => {
			alertSnackbarText = err;
			alertSnackbarDialog = true;
        });
    }
    
    const aufgabeUrl = '/api/aufgabe';
    let aufgabeText = "";
    let allAufgabe = [];
 
    function create() {
        if (!aufgabeText) return;
		createValue(aufgabeUrl, {
            text: aufgabeText,
            projekt: projektUrl + "/" + projektSelected.dataId
		})
        .then(res => res.json())
        .then(json => {
			console.log(json);
			allAufgabe = [...allAufgabe, json];
            aufgabeText = "";
        })
        .catch(err => {
			alertSnackbarText = err;
			alertSnackbarDialog = true;
        });
    };

    function update(aufgabe) {
        updateValue(aufgabeUrl + '/' + aufgabe.dataId, aufgabe)
        .then(res => res.json())
        .then(json => {
            console.log(json);
			const i = allAufgabe.indexOf(aufgabe);
			allAufgabe = [...allAufgabe.slice(0, i), json, ...allAufgabe.slice(i + 1)];
        })
        .catch(err => {
            alertSnackbarText = err;
            alertSnackbarDialog = true;
        });
    };

    function remove(aufgabe) {
		if (!confirm("Aufgabe '" + aufgabe.text + "' wirklich löschen?")) return;
		removeValue(aufgabeUrl + '/' + aufgabe.dataId)
        .then(res => {
			const i = allAufgabe.indexOf(aufgabe);
			allAufgabe = [...allAufgabe.slice(0, i), ...allAufgabe.slice(i + 1)];
        })
        .catch(err => {
			alertSnackbarText = err;
			alertSnackbarDialog = true;
		});
    };
</script>

<Snackbar bind:value={alertSnackbarDialog} bottom left color="alert" timeout={2000}>
	<div>{alertSnackbarText}</div>
</Snackbar>

<h1>Aufgabe</h1>
{#if projektSelected}
<div class="flex flex-col space-y-2">
    <div class="flex flex-row space-x-2">
        <div class="flex-grow">
            <form on:submit|preventDefault={() => create()}>
                <TextField bind:value={aufgabeText}
                    label="Text"
                    placeholder="Bitte hier eine neue Aufgabe eingeben" />
            </form>
        </div>
        <div>
            <Select bind:value={projektSelected} items={allProjektItem} 
                label="Projekt"
                placeholder="Bitte hier das Projekt wählen" />
        </div>
    </div>
    <ul>
        {#each allAufgabe as aufgabe}
        <li>
            <div class="flex flex-row">
                <div class="flex-grow">
                    <Checkbox on:change={() => update(aufgabe)} bind:checked={aufgabe.aktiv}
                        label={aufgabe.text} />
                </div>
                <div class="flex-none">
                    <Button on:click={() => remove(aufgabe)}
                        icon="delete" text light flat />
                </div>
            </div>
        </li>
        {/each}
    </ul>
</div>
{:else}
<span class="px-6 py-3 text-center w-1">
    Keine Projekte
</span>
{/if}

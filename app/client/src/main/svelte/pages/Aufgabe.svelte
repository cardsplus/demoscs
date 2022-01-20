<script>
    import { onMount } from 'svelte';
	import Checkbox from '../components/Checkbox';
	import Icon from '../components/Icon';
	import Select from '../components/Select';
	import TextField from '../components/TextField';
	import { toast } from '../components/Toast';
    import { loadAllValue } from '../utils/rest.js';
    import { createValue } from '../utils/rest.js';
    import { updateValue } from '../utils/rest.js';
    import { removeValue } from '../utils/rest.js';

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
			console.log(err);
			toast.push(err.toString());
        });
    });

    $: if (projektSelected) reload();

    function reload() {
        loadAllValue(projektUrl + "/" + projektSelected.id + "/allAufgabe")
        .then(res => res.json())
        .then(json => {
			console.log(json);
            allAufgabe = json.content;
        })
        .catch(err => {
			console.log(err);
			toast.push(err.toString());
        });
    }
    
    const aufgabeUrl = '/api/aufgabe';
    let aufgabeText = "";
    let allAufgabe = [];
 
    function create() {
        if (!aufgabeText) return;
		createValue(aufgabeUrl, {
            text: aufgabeText,
            projekt: projektUrl + "/" + projektSelected.id
		})
        .then(res => res.json())
        .then(json => {
			console.log(json);
			allAufgabe = [...allAufgabe, json];
            aufgabeText = "";
        })
        .catch(err => {
			console.log(err);
			toast.push(err.toString());
        });
    };

    function update(aufgabe) {
        updateValue(aufgabeUrl + '/' + aufgabe.id, aufgabe)
        .then(res => res.json())
        .then(json => {
            console.log(json);
			const i = allAufgabe.indexOf(aufgabe);
			allAufgabe = [...allAufgabe.slice(0, i), json, ...allAufgabe.slice(i + 1)];
        })
        .catch(err => {
			console.log(err);
			toast.push(err.toString());
        });
    };

    function remove(aufgabe) {
		if (!confirm("Aufgabe '" + aufgabe.text + "' wirklich löschen?")) return;
		removeValue(aufgabeUrl + '/' + aufgabe.id)
        .then(res => {
			const i = allAufgabe.indexOf(aufgabe);
			allAufgabe = [...allAufgabe.slice(0, i), ...allAufgabe.slice(i + 1)];
        })
        .catch(err => {
			console.log(err);
			toast.push(err.toString());
		});
    };
</script>

<h1>Aufgabe</h1>
{#if projektSelected}
<div class="flex flex-col">
    <div class="flex flex-row">
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
            <div class="flex flex-row py-1">
                <div class="flex-grow">
                    <Checkbox on:change={() => update(aufgabe)} bind:checked={aufgabe.aktiv}
                        label={aufgabe.text} />
                </div>
                <div class="flex-none">
                    <Icon on:click={() => remove(aufgabe)}
                        name="delete" outlined />
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

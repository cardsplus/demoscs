<script>
    import { onMount } from 'svelte';
	import Checkbox from '../components/Checkbox';
	import Icon from '../components/Icon';
	import Select from '../components/Select';
	import TextField from '../components/TextField';
	import { toast } from '../components/Toast';
    import { loadAllValue } from '../utils/rest.js';
    import { createValue } from '../utils/rest.js';
    import { updatePatch } from '../utils/rest.js';
    import { removeValue } from '../utils/rest.js';

    let allProjektItem = [];    
    let allAufgabeValue = [];
    let aufgabeText = undefined;
    let aufgabeProjektId = null;

    onMount(async () => {
        try {
            allProjektItem = await loadAllValue('/api/projekt/search/findAllItem');
            console.log(['onMount', allProjektItem]);
        } catch(err) {
			console.log(['onMount', err]);
			toast.push(err.toString());
        };
    });

    $: if (aufgabeProjektId) reload();
    function reload() {
        loadAllValue('/api/aufgabe/search/findAllByProjekt?projektId=' + aufgabeProjektId)
        .then(json => {
			console.log(json);
            allAufgabeValue = json;
        })
        .catch(err => {
			console.log(err);
			toast.push(err.toString());
        });
    }
 
    function createAufgabe() {
        if (!aufgabeText) return;
		createValue('/api/aufgabe', {
            text: aufgabeText,
            projekt: '/api/aufgabe/' + aufgabeProjektId
		})
		.then(() => {
			return loadAllValue('/api/aufgabe/search/findAllByProjekt?projektId=' + aufgabeProjektId);
		})        
        .then(json => {
			console.log(json);
            allAufgabeValue = json;
        })
        .catch(err => {
			console.log(err);
			toast.push(err.toString());
        });
    };

    function updateAufgabe(aufgabe) {
        updatePatch('/api/aufgabe/' + aufgabe.id, aufgabe)
		.then(() => {
			return loadAllValue('/api/aufgabe/search/findAllByProjekt?projektId=' + aufgabeProjektId);
		})        
        .then(json => {
			console.log(json);
            allAufgabeValue = json;
        })
        .catch(err => {
			console.log(err);
			toast.push(err.toString());
        });
    };

    function removeAufgabe(aufgabe) {
		if (!confirm("Aufgabe '" + aufgabe.text + "' wirklich löschen?")) return;
		removeValue('/api/aufgabe/' + aufgabe.id)
		.then(() => {
			return loadAllValue('/api/aufgabe/search/findAllByProjekt?projektId=' + aufgabeProjektId);
		})        
        .then(json => {
			console.log(json);
            allAufgabeValue = json;
        })
        .catch(err => {
			console.log(err);
			toast.push(err.toString());
        });
    };
</script>

<h1>Aufgabe</h1>
<div class="flex flex-col ml-2 mr-2">
    <div class="flex flex-row gap-1">
        <div class="w-2/3">
            <form on:submit|preventDefault={() => createAufgabe()}>
                <TextField 
                    bind:value={aufgabeText}
                    label="Text"
                    placeholder="Bitte hier eine neue Aufgabe eingeben"
                    disabled={!aufgabeProjektId}
                    required/>
            </form>
        </div>
        <div class="w-1/3">
            <Select 
                bind:value={aufgabeProjektId} 
                items={allProjektItem} 
                label="Projekt"
                placeholder="Bitte hier das Projekt wählen"/>
        </div>
    </div>
    <ul>
        {#each allAufgabeValue as aufgabe}
        <li>
            <div class="flex flex-row py-1">
                <div class="flex-grow">
                    <Checkbox 
                        bind:checked={aufgabe.aktiv}
                        on:change={() => updateAufgabe(aufgabe)} 
                        label={aufgabe.text}
                        title={aufgabe.id} />
                </div>
                <div class="flex-none">
                    <Icon 
                        on:click={() => removeAufgabe(aufgabe)}
                        disabled={aufgabe.aktiv}
                        name="delete"
                        outlined />
                </div>
            </div>
        </li>
        {/each}
    </ul>
</div>

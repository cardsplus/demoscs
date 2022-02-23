<script>
    import { createEventDispatcher } from 'svelte';
	import { toast } from '../components/Toast';
	import { createValue } from '../utils/rest.js';
	import { updatePatch } from '../utils/rest.js';
	import { removeValue } from '../utils/rest.js';
	import Button from '../components/Button';
	import TextField from '../components/TextField';
	import Toggle from '../components/Toggle';
    
    export let visible = false;
    export let nutzer = undefined;
    export let allSpracheItem;

    let showUpdate;
    let showRemove;
    let newNutzer = {
        name: undefined,
        mail: undefined,
        allSprache: [],
        aktiv: true
    }

    $: if (nutzer) onChangeNutzer()
    function onChangeNutzer() {
        showUpdate = true;
        showRemove = !nutzer.aktiv;
        newNutzer = {
            id: nutzer.id,
            name: nutzer.name,
            mail: nutzer.mail,
            allSprache: nutzer.allSprache,
            aktiv: nutzer.aktiv
        }
        console.log(['onChangeNutzer', newNutzer]);
    }

    $: disabled = !newNutzer.name || !newNutzer.mail || !newNutzer.allSprache.length;

    const dispatch = createEventDispatcher();
    function onCreateNutzer() {
        createValue('/api/nutzer', newNutzer)
        .then(json => {
            console.log(['onCreateNutzer', newNutzer, json]);
            visible = false;
            dispatch('create', newNutzer);
        })
        .catch(err => {
            console.log(['onCreateNutzer', newNutzer, err]);
            toast.push(err.toString());
        });;
    }
    function onUpdateNutzer() {
        updatePatch('/api/nutzer' + '/' + newNutzer.id, newNutzer)
        .then(json => {
            console.log(['onUpdateNutzer', newNutzer, json]);
            visible = false;
            dispatch('update', newNutzer);
        })
        .catch(err => {
            console.log(['onUpdateNutzer', newNutzer, err]);
            toast.push(err.toString());
        });;
    }
    function onRemoveNutzer() {
        const text = newNutzer.name;
        const hint = text.length > 20 ? text.substring(0, 20) + '...' : text;
        if (!confirm("Nutzer '" + hint + "' wirklich löschen?")) return;
        removeValue('/api/nutzer' + '/' + newNutzer.id)
        .then(() => {
            console.log(['onRemoveNutzer', newNutzer]);
            visible = false;
            dispatch('remove', newNutzer);
        })
        .catch(err => {
            console.log(['onRemoveNutzer', newNutzer, err]);
            toast.push(err.toString());
        });;
    }
    function onCancel() {
        visible = false;
    }
</script>

<div class="flex flex-col">
    <div class="w-full">
        <TextField 
            bind:value={newNutzer.name} 
            label="Name"		
            placeholder="Bitte den Namen eingeben"/>
    </div>
    <div class="w-full">
        <TextField 
            bind:value={newNutzer.mail}
            label="E-Mail"		
            placeholder="Bitte die E-Mail-Adresse eingeben"/>
    </div>
    <div class="w-full">
        <Toggle 
            bind:allValue={newNutzer.allSprache}
            allItem={allSpracheItem} 
            label="Sprachen"
            placeholder="Bitte Sprachen wählen"/>
    </div>
</div>

<div class="py-4">
    {#if showUpdate}
    <Button on:click={() => onUpdateNutzer()} {disabled}>
        Ok
    </Button>
    {:else}
    <Button on:click={() => onCreateNutzer()} {disabled}>
        Ok
    </Button>
    {/if}
    {#if showRemove}
    <Button on:click={() => onRemoveNutzer()}>
        Löschen
    </Button>
    {/if}
    <Button on:click={() => onCancel()}>
        Abbrechen
    </Button>
</div>

<details tabindex="-1">
    <summary>JSON</summary>
    <pre>{JSON.stringify(newNutzer, null, 2)}</pre>
</details>

<script>
    import { createEventDispatcher } from 'svelte';
	import Button from '../components/Button';
	import Icon from '../components/Icon';
	import Select from '../components/Select';
	import TextField from '../components/TextField';
    
    export let visible = false;
    export let projekt = undefined;
    export let allNutzerItem;
    export let allSpracheItem;

    let showUpdate;
    let showRemove;
    let newProjekt = {
        name: undefined,
        sprache: undefined,
        besitzerItem: {
            value: null
        },
        allMitgliedItem: [],
        aktiv: true
    }
    let newMitgliedId = null;

    $: disabled = !newProjekt.name || !newProjekt.sprache || !newProjekt.besitzerItem.value;
    $: if (projekt) onChange()
    function onChange() {
        showUpdate = true;
        showRemove = !projekt.aktiv;
        newProjekt = {
            id: projekt.id,
            name: projekt.name,
            sprache: projekt.sprache,
            besitzerItem: projekt.besitzerItem,
            allMitgliedItem: projekt.allMitgliedItem,
            aktiv: true
        }
        newMitgliedId = null;
        console.log(newProjekt);
    }

    const dispatch = createEventDispatcher();
    function onCreate() {
        visible = false;
        newProjekt.besitzer = '/api/nutzer/' + newProjekt.besitzerItem.value;
        console.log(['create', newProjekt]);
        dispatch('create', newProjekt);
    }
    function onUpdate() {
        visible = false;
        newProjekt.besitzer = '/api/nutzer/'  + newProjekt.besitzerItem.value;
        console.log(['update', newProjekt]);
        dispatch('update', newProjekt);
    }
    function onRemove() {
        visible = false;
        console.log(['remove', newProjekt]);
        dispatch('remove', newProjekt);
    }
    function onCancel() {
        visible = false;
    }
    function onInsertMitglied(id) {
        console.log(['onInsertMitglied',id]);
        newProjekt.allMitgliedItem = newProjekt.allMitgliedItem.concat(allNutzerItem.filter(e => e.value == id));
        newProjekt.allMitglied = newProjekt.allMitgliedItem.map(e => '/api/nutzer/' + e.value);
        newMitgliedId = null;
        console.log(['onInsertMitglied',newProjekt]);
    }
    function onRemoveMitglied(id) {
        console.log(['onRemoveMitglied',id]);
        newProjekt.allMitgliedItem = newProjekt.allMitgliedItem.filter(e => e.value != id);
        newProjekt.allMitglied = newProjekt.allMitgliedItem.map(e => '/api/nutzer/' + e.value);
        newMitgliedId = null;
        console.log(['onRemoveMitglied',newProjekt]);
    }
</script>

<div class="flex flex-col">
    <div class="w-full">
        <TextField 
            bind:value={newProjekt.name} 
            label="Name"		
            placeholder="Bitte den Namen eingeben"/>
    </div>
    <div class="w-40">
        <Select 
            bind:value={newProjekt.sprache} 
            items={allSpracheItem} 
            label="Sprache"
            placeholder="Bitte hier die Projektsprache wählen" />
    </div>
    <div class="w-full">
        <Select 
            bind:value={newProjekt.besitzerItem.value}
            items={allNutzerItem} 
            label="Besitzer"
            placeholder="Bitte hier eine Person wählen" />
    </div>
    <div class="w-full">
        {#each newProjekt.allMitgliedItem as mitgliedItem, i}
        <div class="flex flex-row gap-1 items-baseline">
            <div class="flex-grow">
                <TextField bind:value={mitgliedItem.text}
                    title={mitgliedItem.value}
                    label={(i+1) + '. Mitglied'}
                    disabled/>
            </div>
            <Icon on:click={() => onRemoveMitglied(mitgliedItem.value)}
                name="delete"
                outlined/>
        </div>
        {/each}
        <div class="flex flex-row gap-1 items-baseline">
            <div class="flex-grow">
                <Select bind:value={newMitgliedId}
                    items={allNutzerItem}
                    label={'Neues Mitglied'}
                    placeholder="Bitte eine Person wählen"/>
            </div>
            <Icon on:click={() => onInsertMitglied(newMitgliedId)}
                disabled={!newMitgliedId}
                name="add"
                outlined/>
        </div>
    </div>
</div>

<div class="py-4">
    {#if showUpdate}
    <Button on:click={() => onUpdate()} {disabled}>
        Ok
    </Button>
    {:else}
    <Button on:click={() => onCreate()} {disabled}>
        Ok
    </Button>
    {/if}
    {#if showRemove}
    <Button on:click={() => onRemove()}>
        Löschen
    </Button>
    {/if}
    <Button on:click={() => onCancel()}>
        Abbrechen
    </Button>
</div>

<details>
    <summary>JSON</summary>
    <pre>{JSON.stringify(newProjekt, null, 2)}</pre>
</details>

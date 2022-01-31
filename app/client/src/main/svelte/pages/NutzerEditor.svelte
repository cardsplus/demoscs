<script>
    import { createEventDispatcher } from 'svelte';
	import Button from '../components/Button';
	import Dialog from '../components/Dialog';
	import Groupbox from '../components/Groupbox';
	import TextField from '../components/TextField';
    
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

	let allSpracheDialog = false;
    function onAllSpracheDialogOk() {
        allSpracheDialog = false;
    }

    $: disabled = !newNutzer.name || !newNutzer.mail || !newNutzer.allSprache;
    $: if (nutzer) onChange()
    function onChange() {
        showUpdate = true;
        showRemove = !nutzer.aktiv;
        newNutzer = {
            id: nutzer.id,
            name: nutzer.name,
            mail: nutzer.mail,
            allSprache: nutzer.allSprache,
            aktiv: nutzer.aktiv
        }
        console.log(['onChange', newNutzer]);
    }

    const dispatch = createEventDispatcher();
    function onCreate() {
        visible = false;
        console.log(['create', newNutzer]);
        dispatch('create', newNutzer);
    }
    function onUpdate() {
        visible = false;
        console.log(['update', newNutzer]);
        dispatch('update', newNutzer);
    }
    function onRemove() {
        visible = false;
        console.log(['remove', newNutzer]);
        dispatch('remove', newNutzer);
    }
    function onCancel() {
        visible = false;
    }
</script>

<div class="flex flex-col">
    <div class="w-full">
        <TextField bind:value={newNutzer.name} 
            label="Name"		
            placeholder="Bitte den Namen eingeben"/>
    </div>
    <div class="w-full">
        <TextField bind:value={newNutzer.mail}
            label="E-Mail"		
            placeholder="Bitte die E-Mail-Adresse eingeben"/>
    </div>
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-2 py-2">
        <Button on:click={() => allSpracheDialog=true}
            disabeled={!newNutzer.name}
            outlined block>
            Sprachen
            <small>({newNutzer.allSprache.length})</small>
        </Button>
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
    <pre>{JSON.stringify(newNutzer, null, 2)}</pre>
</details>

<Dialog bind:value={allSpracheDialog}>
	<h5 slot="title">
		{newNutzer.name}s Sprachen
	</h5>
    <div class="flex flex-col gap-1">
        <Groupbox 
            bind:group={newNutzer.allSprache} 
            items={allSpracheItem}/>
    </div>
    <div slot="actions">
		<Button text on:click={onAllSpracheDialogOk}>Ok</Button>
	</div>
</Dialog>

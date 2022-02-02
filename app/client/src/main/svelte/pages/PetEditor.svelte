<script>
    import { createEventDispatcher } from 'svelte';
	import Button from '../components/Button';
	import TextField from '../components/TextField';
    
    export let visible = false;
    export let pet = undefined;
    export let ownerId;

    let showUpdate;
    let showRemove;
    let newPet = {
        name: undefined
    }

    $: disabled = !newPet.name;
    $: if (pet) onChange()
    function onChange() {
        showUpdate = true;
        showRemove = !pet.aktiv;
        newPet = {
            id: pet.id,
            name: pet.name
        }
        console.log(['onChange', newPet]);
    }

    const dispatch = createEventDispatcher();
    function onCreate() {
        visible = false;
        newPet.owner = '/api/owner/' + ownerId;
        console.log(['create', newPet]);
        dispatch('create', newPet);
    }
    function onUpdate() {
        visible = false;
        newPet.owner = '/api/owner/' + ownerId;
        console.log(['update', newPet]);
        dispatch('update', newPet);
    }
    function onRemove() {
        visible = false;
        console.log(['remove', newPet]);
        dispatch('remove', newPet);
    }
    function onCancel() {
        visible = false;
    }
</script>

<div class="flex flex-col">
    <div class="w-full">
        <TextField bind:value={newPet.name} 
            label="Name"		
            placeholder="Insert a name"/>
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
    <pre>{JSON.stringify(newPet, null, 2)}</pre>
</details>

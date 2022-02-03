<script>
    import { createEventDispatcher } from 'svelte';
	import Button from '../components/Button';
	import TextField from '../components/TextField';
    
    export let visible = false;
    export let owner = undefined;

    let showUpdate;
    let showRemove;
    let newOwner = {
        name: undefined,
        address: undefined
    }

    $: disabled = !newOwner.name;
    $: if (owner) onChange()
    function onChange() {
        showUpdate = true;
        showRemove = true;
        newOwner = {
            id: owner.id,
            name: owner.name,
            address: owner.address
        }
        console.log(['onChange', newOwner]);
    }

    const dispatch = createEventDispatcher();
    function onCreate() {
        visible = false;
        console.log(['create', newOwner]);
        dispatch('create', newOwner);
    }
    function onUpdate() {
        visible = false;
        console.log(['update', newOwner]);
        dispatch('update', newOwner);
    }
    function onRemove() {
        visible = false;
        console.log(['remove', newOwner]);
        dispatch('remove', newOwner);
    }
    function onCancel() {
        visible = false;
    }
</script>

<div class="flex flex-col">
    <div class="w-full">
        <TextField bind:value={newOwner.name} 
            label="Name"		
            placeholder="Insert a name"/>
    </div>
    <div class="w-full">
        <TextField bind:value={newOwner.address} 
            label="Address"		
            placeholder="Insert an address"/>
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
    <pre>{JSON.stringify(newOwner, null, 2)}</pre>
</details>

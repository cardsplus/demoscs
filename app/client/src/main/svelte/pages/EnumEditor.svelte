<script>
    import { createEventDispatcher } from 'svelte';
    import Button from '../components/Button';
    import TextField from '../components/TextField';
    
    export let visible = false;
    export let item = undefined;
    export let code;

    let showUpdate;
    let showRemove;
    let newItem = {
        code: code,
        name: undefined,
        text: undefined
    }

    $: disabled = !newItem.name || !newItem.text;
    $: if (item) onChange()
    function onChange() {
        showUpdate = true;
        showRemove = false;
        newItem = {
            code: code,
            name: item.name,
            text: item.text
        }
        console.log(['onChange', newItem]);
    }

    const dispatch = createEventDispatcher();
    function onCreate() {
        visible = false;
        console.log(['create', newItem]);
        dispatch('create', newItem);
    }
    function onUpdate() {
        visible = false;
        console.log(['update', newItem]);
        dispatch('update', newItem);
    }
    function onRemove() {
        visible = false;
        console.log(['remove', newItem]);
        dispatch('remove', newItem);
    }
    function onCancel() {
        visible = false;
    }
</script>

<div class="flex flex-col lg:flex-row gap-1 items-baseline">
    <div class="w-full lg:w-1/4">
        {#if showUpdate}
        <TextField bind:value={newItem.code}
            type=number
            label="Code"		
            disabled/>
        {:else}
        <TextField bind:value={newItem.code} 
            type=number
            label="Code"		
            placeholder="Insert a unique code"/>
        {/if}
    </div>
    <div class="w-full lg:w-1/4">
        <TextField bind:value={newItem.name} 
            label="Name"		
            placeholder="Insert a unique name"/>
    </div>
    <div class="w-full lg:w-1/2">
        <TextField bind:value={newItem.text} 
            label="Text"		
            placeholder="Insert a text"/>
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
    <pre>{JSON.stringify(newItem, null, 2)}</pre>
</details>

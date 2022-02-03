<script>
    import { createEventDispatcher } from 'svelte';
	import Button from '../components/Button';
	import TextField from '../components/TextField';
    
    export let visible = false;
    export let vet = undefined;

    let showUpdate;
    let showRemove;
    let newVet = {
        name: undefined,
        allSkill: []
    }

    $: disabled = !newVet.name;
    $: if (vet) onChange()
    function onChange() {
        showUpdate = true;
        showRemove = true;
        newVet = {
            id: vet.id,
            name: vet.name,
            allSkill: vet.allSkill
        }
        console.log(['onChange', newVet]);
    }

    const dispatch = createEventDispatcher();
    function onCreate() {
        visible = false;
        console.log(['create', newVet]);
        dispatch('create', newVet);
    }
    function onUpdate() {
        visible = false;
        console.log(['update', newVet]);
        dispatch('update', newVet);
    }
    function onRemove() {
        visible = false;
        console.log(['remove', newVet]);
        dispatch('remove', newVet);
    }
    function onCancel() {
        visible = false;
    }
</script>

<div class="flex flex-col">
    <div class="w-full">
        <TextField bind:value={newVet.name} 
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
    <pre>{JSON.stringify(newVet, null, 2)}</pre>
</details>

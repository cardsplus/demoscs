<script>
    import { createEventDispatcher } from 'svelte';
	import Button from '../components/Button';
	import Select from '../components/Select';
	import TextArea from '../components/TextArea';
	import TextField from '../components/TextField';
    
    export let visible = false;
    export let visit = undefined;
    export let allPetItem;
    export let allVetItem;

    let showUpdate;
    let showRemove;
    let newVisit = {
        date: null,
        text: undefined,
        petItem: {
            value: null
        },
        vetItem: {
            value: null
        }
    }
    let newPetId = null;
    let newVetId = null;

    $: disabled = !newVisit.date || !newPetId || !newVetId;
    $: if (visit) onChange()
    function onChange() {
        showUpdate = true;
        showRemove = true;
        newVisit = {
            id: visit.id,
            text: visit.name
        }
        newPetId = null;
        newVetId = null;
        console.log(['onChange', newVisit]);
    }

    const dispatch = createEventDispatcher();
    function onCreate() {
        visible = false;
        newVisit.pet = '/api/pet/' + newPetId; 
        newVisit.vet = '/api/vet/' + newVetId; 
        console.log(['create', newVisit]);
        dispatch('create', newVisit);
    }
    function onUpdate() {
        visible = false;
        newVisit.pet = '/api/pet/' + newPetId; 
        newVisit.vet = '/api/vet/' + newVetId; 
        console.log(['update', newVisit]);
        dispatch('update', newVisit);
    }
    function onRemove() {
        visible = false;
        console.log(['remove', newVisit]);
        dispatch('remove', newVisit);
    }
    function onCancel() {
        visible = false;
    }
</script>

<div class="flex flex-col">
    <form class="w-full">
        <div class="flex flex-col lg:flex-row gap-1">
            <div class="w-full lg:w-2/5">
                <Select 
                    bind:value={newPetId} 
                    bind:valueItem={newVisit.petItem}
                    required
                    allItem={allPetItem} 
                    label="Pet"
                    placeholder="Insert pet"/>
            </div>
            <div class="w-full lg:w-2/5">
                <Select 
                    bind:value={newVetId} 
                    bind:valueItem={newVisit.vetItem}            
                    required
                    allItem={allVetItem} 
                    label="Veterinarian"
                    placeholder="Insert veterinarian"/>
            </div>
            <div class="w-48 lg:w-1/5">
                <TextField 
                    bind:value={newVisit.date}
                    required
                    type="date"
                    label="Date"
                    placeholder="Insert date"/>
            </div>                
        </div>
        <div class="w-full">
            <TextArea 
                bind:value={newVisit.text}
                required
                label="Diagnosis"
                placeholder="Insert diagnosis"/>
        </div>
    </form>
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
    <pre>{JSON.stringify(newVisit, null, 2)}</pre>
</details>

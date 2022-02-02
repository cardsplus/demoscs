<script>
	import { onMount } from 'svelte';
	import Icon from '../components/Icon';
	import Select from '../components/Select';
	import { toast } from '../components/Toast';
	import { loadAllValue } from '../utils/rest.js';
	import { createValue } from '../utils/rest.js';
	import { updatePatch } from '../utils/rest.js';
	import { removeValue } from '../utils/rest.js';
    import { storedOwnerId } from '../store/owner.js';
	import PetEditor from './PetEditor.svelte';

    let allPet = []; 
    let petOwnerId = null; 
	let petIndexOf = undefined;
	function onPetClicked(index) {
		petIndexOf = index;
	}

	let petEditorCreate = false;
	let petEditorCreateId = undefined;
	let petEditorUpdate = false;
	let petEditorUpdateId = undefined;
	$: petEditorDisabled = petEditorCreate || petEditorUpdate;
	function petEditorCreateClicked(id) {
		petEditorCreateId = id;
		petEditorCreate = true;
	}    
	function petEditorUpdateClicked(id) {
		petEditorUpdateId = id;
		petEditorUpdate = true;
	}

    let allOwnerItem = [];

    onMount(async () => {
        try {
			allOwnerItem = await loadAllValue('/api/owner/search/findAllItem');
            console.log(['onMount', allOwnerItem]);
        } catch(err) {
			console.log(['onMount', err]);
			toast.push(err.toString());
        };
        petOwnerId = $storedOwnerId;
    });

    $: petOwnerId, reloadAllPet();
    function reloadAllPet() {
        if (petOwnerId) {
            $storedOwnerId = petOwnerId;
            loadAllValue('/api/pet/search/findAllByOwner?ownerId=' + petOwnerId)
            .then(json => {
                console.log(json);
                allPet = json;
            })
            .catch(err => {
                console.log(err);
                toast.push(err.toString());
            });
        }
    }
    
    function createPet(pet) {
        createValue('/api/pet', pet)
        .then(() => {
            reloadAllPet();
        })
        .catch(err => {
            console.log(err);
            toast.push(err.toString());
        });
    };

    function updatePet(pet) {
        updatePatch('/api/pet/' + pet.id, pet)
        .then(() => {
            reloadAllPet();
        })
        .catch(err => {
            console.log(err);
            toast.push(err.toString());
        });
    };

    function removePet(pet) {
        if (!confirm("Really delete '" + pet.name + "'?")) return;
        removeValue('/api/pet/' + pet.id)
        .then(() => {
            return reloadAllPet();
        })
        .catch(err => {
            console.log(err);
            toast.push(err.toString());
        });
    };
</script>

<h1>Pet</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
    <div class="flex flex-row gap-1">
        <div class="w-full lg:w-96">
            <Select 
                bind:value={petOwnerId} 
                items={allOwnerItem} 
                label="Owner"
                placeholder="Choose owner"/>
        </div>
    </div>
    <table class="table-fixed">
        <thead class="justify-between">
            <tr class="bg-gray-100">
                <th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/2">
                    <span class="text-gray-600">Name</span>
                </th>
                <th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/4">
                    <span class="text-gray-600">Species</span>
                </th>
                <th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/4">
                    <span class="text-gray-600">Birth</span>
                </th>
                <th class="px-2 py-3 border-b-2 border-gray-300 w-16">
                    <Icon on:click={() => petEditorCreateClicked()}
                        disabled={petEditorDisabled}
                        name="edit"
                        outlined/>
                </th>
            </tr>
        </thead>
        <tbody>
            {#if petEditorCreate}
            <tr>
                <td colspan="4">
                    <PetEditor
                        bind:visible={petEditorCreate} 
                        on:create={e => createPet(e.detail)}
                        ownerId={petOwnerId}/>
                <td>
            </tr>
            {/if}
            {#each allPet as pet, i}
            <tr on:click={e => onPetClicked(i)}
                title={pet.id}
                class:ring={petIndexOf === (i)}>
                <td class="px-2 py-3 text-left">
                    <div class="text-sm underline text-blue-600">
                        <a href={'/pet/' + pet.id}>{pet.name}</a>
                    </div>
                </td>
                <td class="px-2 py-3 text-left">
                    <span>tbd</span>
                </td>
                <td class="px-2 py-3 text-left">
                    <span>tbd</span>
                </td>
                <td class="px-2 py-3">
                    <Icon on:click={() => petEditorUpdateClicked(pet.id)}
                        disabled={petEditorDisabled}
                        name="edit"
                        outlined/>
                </td>
            </tr>
            {#if petEditorUpdate && petEditorUpdateId === pet.id}
            <tr>
                <td	colspan="4">
                    <PetEditor
                        bind:visible={petEditorUpdate} 
                        on:update={e => updatePet(e.detail)}
                        on:remove={e => removePet(e.detail)}
                        ownerId={pet.ownerItem.value}
                        {pet}/>
                <td>
            </tr>
            {/if}
            {:else}
            <tr>
                <td class="px-2 py-3" colspan="4">
                    No pets
                </td>
            </tr>
            {/each}
        </tbody>
    </table>
</div>

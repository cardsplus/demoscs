<script>
    import { onMount } from "svelte";
	import Button from '../components/Button';
	import { toast } from '../components/Toast';
    import { loadOneValue } from "../utils/rest.js";

    export let id;

    let nutzer = {
        name: undefined,
        mail: undefined,
        allSprache: []
    };

    onMount(async () => {
        await loadOneValue('/api/nutzer/' + id)
        .then((json) => {
            console.log(json);
            nutzer = json;
        })
        .catch((err) => {
            console.log(err);
			toast.push(err.toString());
        });
    });
</script>

<h1 class:line-through={!nutzer.aktiv}>{nutzer.name}</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
    <fieldset class="p-4 border-2 space-y-2">
        <legend class="text-xs">E-Mail</legend>
        <div class="flex flex-row gap-1 items-center">
            <div class="text-xl underline text-blue-600">
                <a href={"mailto:" + nutzer.mail}>{nutzer.mail}</a>
            </div>
        </fieldset>
    <fieldset class="p-4 border-2 space-y-2">
        <legend class="text-xs">Sprachen</legend>
        <div class="flex flex-wrap gap-1">
            {#each nutzer.allSprache as sprache}
            <Button disabled>{sprache}</Button>
            {:else}
            <Button disabled>Keine Sprachen</Button>
            {/each}
        </div>
    </fieldset>
</div>

<script>
	import TextArea from '../components/TextArea';
	import TextField from '../components/TextField';
	import { mapify } from '../utils/list.js';

    export let allVisit;

    let allVisitByDate = new Map();
    $: allVisitByDate = mapify(allVisit, visitKey);
    function visitKey(e) {
        return e.petItem.text + ' on ' + e.date;
    }
</script>

{#each [...allVisitByDate] as [k, l]}
<details>
    <summary>{k}</summary>
    <div class="ml-4">
    {#each l as e}
    <TextField value={e.vetItem.text}
        label="Veterinarian" 
        disabled/>
    <TextArea value={e.text}
        label="Diagnosis" 
        disabled/>
    {/each}
    </div>
</details>
{/each}

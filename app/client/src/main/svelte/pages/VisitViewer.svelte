<script>
	import TextArea from '../components/TextArea';
	import TextField from '../components/TextField';

    export let allVisit;

    let allVisitByDate = new Map();
    $: allVisitByDate = process(allVisit);
    function process(l) {
        console.log(['process', l]);
        let m = new Map();
        l.forEach(e => {
            let k = e.petItem.text + ' on ' + e.date;		
            let v = m.get(k);
            if (v) {
                m.set(k, [...v, e]);
            } else {
                m.set(k, [e]);
            }
        });
        console.log(['process', m]);
        return m;
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

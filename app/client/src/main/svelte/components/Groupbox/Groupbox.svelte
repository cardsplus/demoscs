<script>
	import Option from './Option.svelte';
    import filterProps from "../filterProps.js";
    const props = filterProps([
      'checked',
      'disabled',
      'title'
    ], $$props);
    export let disabled = false;
    export let group;
    export let items = [];
    export let title = undefined;

    $: itemsProcessed = items.map(processItem);
    function processItem(e) {
        if (typeof e !== "object") {
            return {
                value: e, 
                text: e 
            };
        } else {
            return {
                value: e.value,
                text: e.text
            }
        }
    }
</script>

{#each itemsProcessed as item}
<Option
    {...props}
    {disabled}
    {title}
    bind:group
    value={item.value}
    label={item.text}/>
{/each}
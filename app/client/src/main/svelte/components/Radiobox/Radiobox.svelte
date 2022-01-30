<script>
    import filterProps from "../filterProps.js";
    const props = filterProps([
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
  
  <div class="relative w-auto h-auto flex flex-col">
    {#each itemsProcessed as item}
    <label {title} class="inline-flex items-center p-2 cursor-pointer">
      <input
        {...props}
        {title}
        class="disabled:opacity-50 border-2 border-primary-500 text-primary-500"
        type=radio 
        {disabled} 
        bind:group
        value={item.value}
        on:change/>
      <div class="pl-2 cursor-pointer text-gray-700">
        {item.text}
      </div>
    </label>
    {/each}
  </div>
  
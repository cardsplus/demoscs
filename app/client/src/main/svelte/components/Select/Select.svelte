<script>
  import filterProps from "../filterProps.js";
  const props = filterProps([
    'disabled',
    'items',
    'label',
    'value'
  ], $$props);
  export let disabled = false;
  export let items = [];
  export let label;
  export let value;
  let focused;
  
  $: itemsProcessed = items.map(item => typeof item !== "object"
     ? ({ value: item, text: item })
     : item);
</script>

<div
  class="mt-2 mb-6 relative"
>
  <span
    class="pb-2 px-4 pt-2 text-xs absolute left-0 top-0"
    class:text-gray-600={!focused}
    class:text-primary-600={focused}
    >
      {label}
  </span>
  <select
    {...props}
    {disabled}
    class="disabled:opacity-50 w-full pb-2 px-4 pt-6 text-black bg-gray-100"
    class:border-0={!focused}
    aria-label={label}
    bind:value
    on:change
    on:input
    on:keydown
    on:keypress
    on:keyup
    on:click
    on:focus={() => focused = true}
    on:focus
    on:blur={() => focused = false}
    on:blur
  >
    {#each itemsProcessed as item}
    <option value={item.value}>
      {item.text}
    </option>
    {/each}  
  </select>
  <div class="w-full bg-gray-600 absolute left-0 bottom-0">
    <div 
      class="mx-auto w-0" 
      style="height: 1px; transition: width 0.2s ease 0s;"
    />
  </div>
</div>

<!-- 
  https://svelte.dev/tutorial/checkbox-inputs
-->

<script>
  export let checked;
  export let disabled = false;
  export let label = undefined;
</script>

<div class="relative w-auto h-auto">
  <label class="inline-flex items-center p-2 cursor-pointer">
    <input
      class="disabled:opacity-50 border-2 border-primary-500 text-primary-500"
      type=checkbox 
      {disabled} 
      bind:checked 
      on:change/>
    {#if label}
    <div class="pl-2 cursor-pointer text-gray-700">
      {label}
    </div>
    {/if}
  </label>
</div>

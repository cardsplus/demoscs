<!--
  https://svelte.dev/tutorial/group-inputs
-->

<script>
  export let disabled = false;
  export let group;
  export let label = undefined;
  export let value;

	function onChange({ target }) {
		if (target.checked) {
			group = [...group, value]
		} else {
			group = group.filter((e) => e !== value);
		}
	}
</script>

<div class="relative w-auto h-auto">
  <label class="inline-flex items-center p-2 cursor-pointer">
    <input
      class="disabled:opacity-50 border-2 border-primary-500 text-primary-500"
      type=checkbox
      {disabled} 
      checked={group.includes(value)}
      on:change={onChange}
      on:change/>
    {#if label}
    <div class="pl-2 cursor-pointer text-gray-700">
      {label}
    </div>
    {/if}
  </label>
</div>

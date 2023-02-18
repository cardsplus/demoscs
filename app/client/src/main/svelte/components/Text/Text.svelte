<script>
  import filterProps from "../filterProps.js";
  const props = filterProps(["label", "title", "value"], $$props);
  export let label = undefined;
  export let title = undefined;
  export let value;
</script>

<div class="mt-1 relative">
  {#if label}
    <span class="pb-2 px-4 pt-2 text-xs absolute left-0 top-0">
      {label}
    </span>
  {/if}
  <p
    {...props}
    {title}
    class="disabled:opacity-50 w-full pb-2 px-4 text-black bg-gray-100 border-0"
    class:pt-6={label}
    aria-label={label}
  >
    {#if value}
      {value}
    {:else}
      &nbsp;
    {/if}
  </p>
  {#if label}
    <div class="w-full bg-label-600 absolute left-0 bottom-0">
      <div
        class="mx-auto w-0"
        style="height: 1px; transition: width 0.2s ease 0s;"
      />
    </div>
  {/if}
</div>

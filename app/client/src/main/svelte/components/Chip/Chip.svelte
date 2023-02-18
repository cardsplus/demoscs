<script>
  import filterProps from "../filterProps.js";
  const props = filterProps(["disabled", "icon", "outlined", "title"], $$props);
  export let disabled = false;
  export let icon;
  export let outlined = false;
  export let title = undefined;
</script>

<button
  {...props}
  {title}
  {disabled}
  class:disabled
  class="text-sm text-white rounded-full py-2 px-4 disabled:opacity-50 hover:opacity-90 focus:ring bg-primary-500 overflow-hidden"
  class:outlined
  on:click
  on:mouseover
  on:focus
  on:blur
>
  <div class="flex flex-row content-center justify-start items-center gap-1">
    <i
      {title}
      aria-hidden="true"
      class="material-icons icon text-xl select-none duration-200 ease-in"
      disabled
    >
      {icon}
    </i>
    <slot />
  </div>
</button>

<style lang="postcss">
  button.disabled {
    user-select: none;
    pointer-events: none;
    cursor: default;
  }
  button.outlined {
    @apply border-2 border-solid border-primary-500 bg-transparent text-primary-500 font-bold;
  }
</style>

<script>
  import { printerUrl } from "../../utils/url.js";
  import filterProps from "../filterProps.js";
  const props = filterProps(
    ["disabled", "name", "outlined", "title", "path"],
    $$props
  );
  export let disabled = false;
  export let name;
  export let outlined = false;
  export let title;
  export let path;
  let openIn;
  function onClick() {
    const url = printerUrl(path instanceof Function ? path() : path);
    openIn.href = url;
    openIn.click();
    openIn.href = undefined;
  }
</script>

<button
  {...props}
  {title}
  {disabled}
  class:disabled
  class="text-xl text-white w-12 h-12 rounded-full p-2 disabled:opacity-50 hover:opacity-90 focus:ring bg-primary-500"
  class:outlined
  on:click={onClick}
  on:click
  on:mouseover
  on:focus
  on:blur
>
  <div class="flex justify-center items-center">
    <i
      {title}
      aria-hidden="true"
      class="material-icons icon text-xl select-none"
      disabled
    >
      {name}
    </i>
  </div>
</button>
<!-- svelte-ignore a11y-missing-attribute -->
<a class="hidden" target="_blank" bind:this={openIn}>&nbsp;</a>

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

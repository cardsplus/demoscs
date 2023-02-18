<script>
  import { zip } from "../../utils/rest.js";
  import { toast } from "../Toast";
  import filterProps from "../filterProps.js";
  const props = filterProps(
    ["disabled", "name", "outlined", "title", "job"],
    $$props
  );
  export let disabled = false;
  export let name;
  export let outlined = false;
  export let title;
  export let job;
  let saveAs;
  function onClick() {
    const toastId = toast.push(title);
    const json = job instanceof Function ? job() : job;
    zip(json)
      .then((blob) => {
        console.log(["zip", json]);
        toast.pop(toastId);
        saveAs.href = URL.createObjectURL(blob);
        saveAs.download = json.archive;
        saveAs.click();
        saveAs.href = URL.revokeObjectURL(saveAs.href);
        saveAs.download = undefined;
      })
      .catch((err) => {
        console.log(["zip", json, err]);
        toast.pop(toastId);
        toast.push(err.toString());
      });
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
<a class="hidden" bind:this={saveAs}>&nbsp;</a>

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

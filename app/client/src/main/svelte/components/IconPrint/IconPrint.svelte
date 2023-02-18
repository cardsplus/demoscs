<script>
  import { print } from "../../utils/rest.js";
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
  function onClick() {
    const toastId = toast.push(title);
    const json = job instanceof Function ? job() : job;
    print(json)
      .then((log) => {
        console.log(["print", json, log]);
        toast.pop(toastId);
      })
      .catch((err) => {
        console.log(["print", json, err]);
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

<script>
  import { createEventDispatcher } from "svelte";
  const dispatch = createEventDispatcher();
  import filterProps from "../filterProps.js";
  const props = filterProps(["disabled", "label", "title", "value"], $$props);
  export let disabled = false;
  export let label = undefined;
  export let title = undefined;
  export let value;
  let focused;
  let element;
  export function focus() {
    element.focus();
  }
  $: valueInternal = value;
  function onBlur() {
    value = valueInternal;
    dispatch("change", value);
  }
  function onKey(e) {
    // change on enter
    if (e.key === "Enter") {
      e.stopPropagation();
      onBlur();
      return;
    }
    // clean on escape
    if (e.key === "Escape") {
      e.stopPropagation();
      valueInternal = undefined;
      return;
    }
  }
</script>

<div class="mt-1 relative">
  {#if label}
    <span
      class="pb-2 px-4 pt-2 text-xs absolute left-0 top-0"
      class:text-label-600={!focused}
      class:text-primary-500={focused}
    >
      {label}
    </span>
  {/if}
  <input
    bind:this={element}
    type="text"
    {...props}
    {title}
    {disabled}
    class="disabled:opacity-50 w-full pb-2 px-4 text-black bg-gray-100"
    class:pt-6={label}
    class:border-0={!focused}
    aria-label={label}
    bind:value={valueInternal}
    on:input
    on:keydown={onKey}
    on:keypress
    on:keyup
    on:keyup
    on:click
    on:focus={() => (focused = true)}
    on:focus
    on:blur={() => (focused = false)}
    on:blur={onBlur}
    on:blur
  />
  {#if label}
    <div class="w-full bg-label-600 absolute left-0 bottom-0">
      <div
        class="mx-auto w-0"
        style="height: 1px; transition: width 0.2s ease 0s;"
      />
    </div>
  {/if}
</div>

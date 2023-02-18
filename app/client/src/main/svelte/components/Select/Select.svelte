<script>
  import { createEventDispatcher } from "svelte";
  const dispatch = createEventDispatcher();
  import filterProps from "../filterProps.js";
  const props = filterProps(
    ["allItem", "disabled", "label", "nullable", "title", "value", "valueItem"],
    $$props
  );
  export let allItem;
  export let disabled = false;
  export let label = undefined;
  export let nullable = false;
  export let title = undefined;
  export let value;
  export let valueItem = undefined;
  let focused;
  let element;
  export function focus() {
    element.focus();
  }

  $: valueProcessed = processValue(value);
  function processValue(e) {
    if (typeof e !== "object") {
      return e;
    } else {
      return e ? JSON.stringify(e) : null;
    }
  }

  $: allItemProcessed = collect(allItem, valueItem).map(processItem);
  function collect(a, i) {
    // An already selected value must be in the collection
    // to be displayed and may always be selected again.
    if (i && i.value && i.text) {
      if (!a.find((e) => e.value === i.value)) {
        return [i, ...a];
      }
    }
    return a;
  }
  function processItem(e) {
    if (typeof e !== "object") {
      return {
        value: e,
        text: e,
      };
    } else {
      return {
        value: processValue(e.value),
        text: e.text,
      };
    }
  }

  function onChange({ target }) {
    let i = target.selectedIndex - skip();
    let e = allItem[i];
    if (e) {
      valueItem = e;
      if (typeof e !== "object") {
        value = e;
      } else {
        value = e.value;
      }
    } else {
      valueItem = {};
      value = undefined;
    }
    dispatch("change", valueItem);
  }

  function skip() {
    return nullable ? 1 : 0;
  }
</script>

<div class="mt-1 relative">
  {#if label}
    <span
      {title}
      class="pb-2 px-4 pt-2 text-xs absolute left-0 top-0"
      class:text-label-600={!focused}
      class:text-primary-500={focused}
    >
      {label}
    </span>
  {/if}
  <select
    bind:this={element}
    {...props}
    {title}
    {disabled}
    class="disabled:opacity-50 w-full pb-2 px-4 text-black bg-gray-100"
    class:pt-6={label}
    class:border-0={!focused}
    aria-label={label}
    value={valueProcessed}
    on:change={onChange}
    on:input
    on:keydown
    on:keypress
    on:keyup
    on:click
    on:focus={() => (focused = true)}
    on:focus
    on:blur={() => (focused = false)}
    on:blur
  >
    {#if nullable}
      <option value={null}>&nbsp;</option>
    {/if}
    {#each allItemProcessed as item}
      <option value={item.value}>
        {item.text}
      </option>
    {/each}
  </select>
  {#if label}
    <div class="w-full bg-label-600 absolute left-0 bottom-0">
      <div
        class="mx-auto w-0"
        style="height: 1px; transition: width 0.2s ease 0s;"
      />
    </div>
  {/if}
</div>

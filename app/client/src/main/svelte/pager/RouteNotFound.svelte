<script>
  import { register, activeRoute } from "./Router.svelte";
  export let path = "*";
  export let component = null;
  register({ path, component });
</script>

{#if $activeRoute.path === path}
  <svelte:component this={component} />
  <slot />
{/if}

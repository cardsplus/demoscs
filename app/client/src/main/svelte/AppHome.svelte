<script>
  import { onMount } from "svelte";
  import { toast } from "./components/Toast";
  import { apiExplorerUrl } from "./utils/rest.js";
  import { fetchDoc } from "./utils/rest.js";

  let apiExplorer = apiExplorerUrl();

  let versionUrl = "/version";
  let versionHtml = "loading ..";
  onMount(async () => {
    fetchDoc(versionUrl, "text/html")
      .then((res) => res.text())
      .then((html) => {
        versionHtml = html;
      })
      .catch((err) => {
        console.log(err);
        toast.push(err.toString());
      });
  });
</script>

<h1>Info</h1>
<div class="flex flex-col ml-2 mr-2 space-y-2">
  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">APP-Version</legend>
    <div class="text-2xl">
      {#if versionHtml}
        {@html versionHtml}
      {:else}
        <p>Nicht verfügbar</p>
      {/if}
    </div>
  </fieldset>
  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">API-Explorer</legend>
    <div class="text-2xl">
      <a
        class="underline text-blue-600"
        href={apiExplorer}
        target="_blank"
        rel="noreferrer"
      >
        {apiExplorer}
      </a>
    </div>
  </fieldset>
</div>

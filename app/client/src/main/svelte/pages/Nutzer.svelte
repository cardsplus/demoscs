<script>
  import { onMount } from "svelte";
  import { toast } from "../components/Toast";
  import { loadAllValue } from "../utils/rest.js";
  import { updatePatch } from "../utils/rest.js";
  import Icon from "../components/Icon";
  import Checkbox from "../components/Checkbox";
  import TextField from "../components/TextField";
  import NutzerEditor from "./NutzerEditor.svelte";

  let allNutzer = [];
  let nutzerId = undefined;
  function onNutzerClicked(nutzer) {
    nutzerId = nutzer.id;
  }

  let nutzerEditorCreate = false;
  function nutzerEditorCreateClicked() {
    nutzerEditorCreate = true;
  }
  let nutzerEditorUpdate = false;
  function nutzerEditorUpdateClicked(nutzer) {
    nutzerEditorUpdate = true;
    nutzerId = nutzer.id;
  }
  $: nutzerEditorDisabled = nutzerEditorCreate || nutzerEditorUpdate;

  let allSpracheItem = [];

  onMount(async () => {
    try {
      allSpracheItem = await loadAllValue("/api/enum/sprache");
      console.log(["onMount", allSpracheItem]);
    } catch (err) {
      console.log(["onMount", err]);
      toast.push(err.toString());
    }
    reloadAllNutzer();
  });

  let filterPrefix = "";
  function filterNutzer(prefix, allValue) {
    if (!filterPrefix) return allValue;
    return allValue.filter((e) => {
      for (const s of e.name.split(" ")) {
        if (s.toLowerCase().startsWith(prefix.toLowerCase())) {
          return true;
        }
      }
      if (e.mail.toLowerCase().startsWith(prefix.toLowerCase())) {
        return true;
      }
      return false;
    });
  }
  $: allNutzerFiltered = filterNutzer(filterPrefix, allNutzer);

  function reloadAllNutzer() {
    loadAllValue("/api/nutzer/search/findAllByOrderByMailAsc")
      .then((json) => {
        console.log(["reloadAllNutzer", json]);
        allNutzer = json;
      })
      .catch((err) => {
        console.log(["reloadAllNutzer", err]);
        toast.push(err.toString());
      });
  }

  function updateNutzer(nutzer) {
    updatePatch("/api/nutzer/" + nutzer.id, nutzer)
      .then(() => {
        reloadAllNutzer();
      })
      .catch((err) => {
        console.log(["updateNutzer", err]);
        toast.push(err.toString());
      });
  }
</script>

<h1>Nutzer</h1>
<div class="flex flex-col gap-1 ml-2 mr-2">
  <div class="flex-grow">
    <h4 title="Filter für die Nutzer, nicht case-sensitiv">Aktueller Filter</h4>
    <TextField
      bind:value={filterPrefix}
      label="Filter"
      placeholder="Bitte Filterkriterien eingeben"
      disabled={nutzerEditorDisabled}
    />
    <h4 title="Liste der Nutzer, ggfs. gefiltert, jedes Element editierbar">
      Aktuelle Nutzer <small>({allNutzerFiltered.length})</small>
    </h4>
    <table class="table-fixed">
      <thead class="justify-between">
        <tr class="bg-gray-100">
          <th class="px-2 py-3 border-b-2 border-gray-300 w-16">
            <span class="text-gray-600 text-sm">Aktiv</span>
          </th>
          <th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/4">
            <span class="text-gray-600">Name</span>
          </th>
          <th class="px-2 py-3 border-b-2 border-gray-300 text-left w-2/4">
            <span class="text-gray-600">E-Mail</span>
          </th>
          <th class="px-2 py-3 border-b-2 border-gray-300 text-left w-1/4">
            <span class="text-gray-600">Sprachen</span>
          </th>
          <th class="px-2 py-3 border-b-2 border-gray-300 w-16">
            <Icon
              on:click={() => nutzerEditorCreateClicked()}
              disabled={nutzerEditorDisabled}
              name="add"
              outlined
            />
          </th>
        </tr>
      </thead>
      <tbody>
        {#if nutzerEditorCreate}
          <tr>
            <td class="px-4" colspan="5">
              <NutzerEditor
                bind:visible={nutzerEditorCreate}
                on:create={(e) => reloadAllNutzer()}
                {allSpracheItem}
              />
            </td>
          </tr>
        {/if}
        {#each allNutzerFiltered as nutzer, i}
          <tr
            on:click={(e) => onNutzerClicked(nutzer)}
            title={nutzer.id}
            class:ring={nutzerId === nutzer.id}
          >
            <td class="px-2 py-3">
              <Checkbox
                bind:checked={nutzer.aktiv}
                on:change={() => updateNutzer(nutzer)}
              />
            </td>
            <td class="px-2 py-3 text-left">
              <span>{nutzer.name}</span>
            </td>
            <td class="px-2 py-3 text-left">
              <div class="text-sm underline text-blue-600">
                <a href={"/nutzer/" + nutzer.id}>{nutzer.mail}</a>
              </div>
            </td>
            <td class="px-2 py-3 text-left">
              <div class="flex flex-row flex-wrap gap-1">
                {#each nutzer.allSprache as sprache}
                  <span class="p-1 text-xs text-white bg-primary-500 rounded">
                    {sprache}
                  </span>
                {/each}
              </div>
            </td>
            <td class="px-2 py-3">
              <Icon
                on:click={() => nutzerEditorUpdateClicked(nutzer)}
                disabled={nutzerEditorDisabled}
                name="edit"
                outlined
              />
            </td>
          </tr>
          {#if nutzerEditorUpdate && nutzerId === nutzer.id}
            <tr>
              <td class="px-4" colspan="5">
                <NutzerEditor
                  bind:visible={nutzerEditorUpdate}
                  on:update={(e) => reloadAllNutzer()}
                  on:remove={(e) => reloadAllNutzer()}
                  {nutzer}
                  {allSpracheItem}
                />
              </td>
            </tr>
          {/if}
        {:else}
          <tr>
            <td class="px-2 py-3" colspan="5">Keine Nutzer</td>
          </tr>
        {/each}
      </tbody>
    </table>
  </div>
</div>

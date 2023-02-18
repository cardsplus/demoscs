<script>
  import { onMount } from "svelte";
  import { toast } from "../components/Toast";
  import { loadAllValue } from "../utils/rest.js";
  import { createValue } from "../utils/rest.js";
  import { updatePatch } from "../utils/rest.js";
  import { removeValue } from "../utils/rest.js";
  import { storedProjektId } from "../stores/projekt.js";
  import Checkbox from "../components/Checkbox";
  import Icon from "../components/Icon";
  import Select from "../components/Select";
  import TextArea from "../components/TextArea";
  import TextField from "../components/TextField";

  let allAufgabe = [];
  let aufgabeText = undefined;
  let aufgabeProjektId = null;
  let aufgabeId = undefined;
  function onAufgabeClicked(aufgabe) {
    aufgabeId = aufgabe.id;
  }

  let allProjektItem = [];

  onMount(async () => {
    try {
      aufgabeProjektId = null;
      allProjektItem = await loadAllValue("/api/projekt/search/findAllItem");
      console.log(["onMount", allProjektItem]);
      aufgabeProjektId = $storedProjektId;
    } catch (err) {
      console.log(["onMount", err]);
      toast.push(err.toString());
    }
  });

  $: aufgabeProjektId, reloadAllAufgabe();
  function reloadAllAufgabe() {
    allAufgabe = [];
    if (!aufgabeProjektId) return;
    $storedProjektId = aufgabeProjektId;
    loadAllValue(
      "/api/aufgabe/search/findAllByProjekt?projektId=" + aufgabeProjektId
    )
      .then((json) => {
        console.log(["reloadAllAufgabe", json]);
        allAufgabe = json;
      })
      .catch((err) => {
        console.log(["reloadAllAufgabe", err]);
        toast.push(err.toString());
      });
  }

  function createAufgabe() {
    if (!aufgabeProjektId) return;
    if (!aufgabeText) return;
    let aufgabe = {
      text: aufgabeText,
      projekt: "/api/aufgabe/" + aufgabeProjektId,
    };
    createValue("/api/aufgabe", aufgabe)
      .then((json) => {
        console.log(["createAufgabe", aufgabe, json]);
        reloadAllAufgabe();
        aufgabeText = "";
      })
      .catch((err) => {
        console.log(["createAufgabe", aufgabe, err]);
        toast.push(err.toString());
      });
  }

  function updateAufgabe(aufgabe) {
    updatePatch("/api/aufgabe/" + aufgabe.id, aufgabe)
      .then((json) => {
        console.log(["updateAufgabe", aufgabe, json]);
        reloadAllAufgabe();
      })
      .catch((err) => {
        console.log(["updateAufgabe", aufgabe, err]);
        toast.push(err.toString());
      });
  }

  function removeAufgabe(aufgabe) {
    const text = aufgabe.text;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Aufgabe '" + hint + "' wirklich löschen?")) return;
    removeValue("/api/aufgabe/" + aufgabe.id)
      .then(() => {
        console.log(["removeAufgabe", aufgabe]);
        reloadAllAufgabe();
      })
      .catch((err) => {
        console.log(["removeAufgabe", aufgabe, err]);
        toast.push(err.toString());
      });
  }
</script>

<h1>Aufgabe</h1>
<div class="flex flex-col ml-2 mr-2">
  <div class="flex flex-row gap-1">
    <div class="w-2/3">
      <form on:submit|preventDefault={() => createAufgabe()}>
        <TextField
          bind:value={aufgabeText}
          required
          label="Aufgabe"
          placeholder="Bitte eine neue Aufgabe eingeben"
          disabled={!aufgabeProjektId}
        />
      </form>
    </div>
    <div class="w-1/3">
      <Select
        bind:value={aufgabeProjektId}
        allItem={allProjektItem}
        nullable="true"
        label="Projekt"
        placeholder="Bitte ein Projekt wählen"
      />
    </div>
  </div>
  <ul>
    {#each allAufgabe as aufgabe}
      <li
        on:click={(e) => onAufgabeClicked(aufgabe)}
        title={aufgabe.id}
        class:ring={aufgabeId === aufgabe.id}
      >
        <div class="flex flex-row items-start gap-1 pt-2">
          <div class="flex-none">
            <Checkbox
              bind:checked={aufgabe.aktiv}
              on:change={() => updateAufgabe(aufgabe)}
              title={aufgabe.id}
            />
          </div>
          <div class="flex-grow">
            <TextArea
              class="w-full border-0"
              bind:value={aufgabe.text}
              on:change={() => updateAufgabe(aufgabe)}
              title={aufgabe.id}
              rows={Math.max(2, aufgabe.text.split("\n").length)}
            />
          </div>
          <div class="flex-none px-2 pt-2">
            <Icon
              on:click={() => removeAufgabe(aufgabe)}
              disabled={aufgabe.aktiv}
              name="delete"
              outlined
            />
          </div>
        </div>
      </li>
    {:else}
      <span class="px-6 py-3"> Keine Aufgaben </span>
    {/each}
  </ul>
</div>

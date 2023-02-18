<script>
  import { createEventDispatcher } from "svelte";
  import { toast } from "../components/Toast";
  import { createValue } from "../utils/rest.js";
  import { updatePatch } from "../utils/rest.js";
  import { removeValue } from "../utils/rest.js";
  import Button from "../components/Button";
  import Icon from "../components/Icon";
  import Select from "../components/Select";
  import TextField from "../components/TextField";

  export let visible = false;
  export let projekt = undefined;
  export let allSpracheItem;
  export let allNutzerItem;

  let showUpdate;
  let showRemove;
  let newProjekt = {
    name: "",
    sprache: null,
    besitzerItem: {
      value: null,
      text: "",
    },
    allMitgliedItem: [],
    aktiv: true,
  };
  let newMitgliedId = null;

  $: if (projekt) onChangeProjekt();
  function onChangeProjekt() {
    showUpdate = true;
    showRemove = !projekt.aktiv;
    newProjekt = {
      id: projekt.id,
      name: projekt.name,
      sprache: projekt.sprache,
      besitzerItem: {
        value: projekt.besitzerItem.value,
        text: projekt.besitzerItem.text,
      },
      allMitgliedItem: projekt.allMitgliedItem.map((e) => ({
        value: e.value,
        text: e.text,
      })),
      aktiv: true,
    };
    newMitgliedId = null;
    console.log(["onChangeProjekt", newProjekt]);
  }

  $: disabled =
    !newProjekt.name || !newProjekt.sprache || !newProjekt.besitzerItem;

  const dispatch = createEventDispatcher();
  function onCreateProjekt() {
    newProjekt.besitzer = "/api/nutzer/" + newProjekt.besitzerItem.value;
    newProjekt.allMitglied = newProjekt.allMitgliedItem.map(
      (e) => "/api/nutzer/" + e.value
    );
    createValue("/api/projekt", newProjekt)
      .then((json) => {
        console.log(["onCreateProjekt", newProjekt, json]);
        visible = false;
        dispatch("create", newProjekt);
      })
      .catch((err) => {
        console.log(["onCreateProjekt", newProjekt, err]);
        toast.push(err.toString());
      });
  }
  function onUpdateProjekt() {
    newProjekt.besitzer = "/api/nutzer/" + newProjekt.besitzerItem.value;
    newProjekt.allMitglied = newProjekt.allMitgliedItem.map(
      (e) => "/api/nutzer/" + e.value
    );
    updatePatch("/api/projekt" + "/" + newProjekt.id, newProjekt)
      .then((json) => {
        console.log(["onUpdateProjekt", newProjekt, json]);
        visible = false;
        dispatch("update", newProjekt);
      })
      .catch((err) => {
        console.log(["onUpdateProjekt", newProjekt, err]);
        toast.push(err.toString());
      });
  }
  function onRemoveProjekt() {
    const text = newProjekt.name;
    const hint = text.length > 20 ? text.substring(0, 20) + "..." : text;
    if (!confirm("Projekt '" + hint + "' wirklich löschen?")) return;
    removeValue("/api/projekt" + "/" + newProjekt.id)
      .then(() => {
        console.log(["onRemoveProjekt", newProjekt]);
        visible = false;
        dispatch("remove", newProjekt);
      })
      .catch((err) => {
        console.log(["onRemoveProjekt", newProjekt, err]);
        toast.push(err.toString());
      });
  }
  function onCancel() {
    visible = false;
  }

  function onInsertMitglied(id) {
    newProjekt.allMitgliedItem = newProjekt.allMitgliedItem.concat(
      allNutzerItem.filter((e) => e.value == id)
    );
    newMitgliedId = null;
    console.log(["onInsertMitglied", newProjekt]);
  }
  function onRemoveMitglied(id) {
    newProjekt.allMitgliedItem = newProjekt.allMitgliedItem.filter(
      (e) => e.value != id
    );
    newMitgliedId = null;
    console.log(["onRemoveMitglied", newProjekt]);
  }
</script>

<div class="flex flex-col">
  <div class="w-full">
    <TextField
      bind:value={newProjekt.name}
      label="Name"
      placeholder="Bitte den Namen eingeben"
    />
  </div>
  <div class="w-40">
    <Select
      bind:value={newProjekt.sprache}
      allItem={allSpracheItem}
      label="Sprache"
      placeholder="Bitte hier die Projektsprache wählen"
    />
  </div>
  <div class="w-full">
    <Select
      bind:value={newProjekt.besitzerItem.value}
      allItem={allNutzerItem}
      label="Besitzer"
      placeholder="Bitte hier eine Person wählen"
    />
  </div>
  <div class="w-full">
    {#each newProjekt.allMitgliedItem as mitgliedItem, i}
      <div class="flex flex-row gap-1 items-baseline">
        <div class="flex-grow">
          <TextField
            bind:value={mitgliedItem.text}
            title={mitgliedItem.value}
            label={i + 1 + ". Mitglied"}
            disabled
          />
        </div>
        <Icon
          on:click={() => onRemoveMitglied(mitgliedItem.value)}
          name="delete"
          outlined
        />
      </div>
    {/each}
    <div class="flex flex-row gap-1 items-baseline">
      <div class="flex-grow">
        <Select
          bind:value={newMitgliedId}
          allItem={allNutzerItem}
          label={"Neues Mitglied"}
          placeholder="Bitte eine Person wählen"
        />
      </div>
      <Icon
        on:click={() => onInsertMitglied(newMitgliedId)}
        disabled={!newMitgliedId}
        name="add"
        outlined
      />
    </div>
  </div>
</div>

<div class="py-4">
  {#if showUpdate}
    <Button on:click={() => onUpdateProjekt()} {disabled}>Ok</Button>
  {:else}
    <Button on:click={() => onCreateProjekt()} {disabled}>Ok</Button>
  {/if}
  {#if showRemove}
    <Button on:click={() => onRemoveProjekt()}>Löschen</Button>
  {/if}
  <Button on:click={() => onCancel()}>Abbrechen</Button>
</div>

<details tabindex="-1">
  <summary>JSON</summary>
  <pre>{JSON.stringify(newProjekt, null, 2)}</pre>
</details>

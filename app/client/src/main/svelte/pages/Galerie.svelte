<script>
  import Button from "../components/Button";
  import Checkbox from "../components/Checkbox";
  import Chip from "../components/Chip";
  import Dialog from "../components/Dialog";
  import Goto from "../components/Goto";
  import Groupbox from "../components/Groupbox";
  import Icon from "../components/Icon";
  import Radiobox from "../components/Radiobox";
  import Select from "../components/Select";
  import TextArea from "../components/TextArea";
  import TextField from "../components/TextField";
  import Toggle from "../components/Toggle";
  import { toast } from "../components/Toast";

  const allQuelleItem = [
    "JIRA",
    "GITHUB",
    "GITLAB",
    "BITBUCKET",
    "CONFLUENCE",
    "MAIL",
    "CHAT",
    "SKYPE",
    "DISCORD",
    "SLACK",
    "TEAMS",
  ];

  const DE = { name: "DE", code: 1 };
  const EN = { name: "EN", code: 2 };
  const IT = { name: "IT", code: 3 };
  const allSpracheItem = [
    {
      icon: "bolt",
      value: DE,
      text: "Deutsch",
    },
    {
      icon: "cloud",
      value: EN,
      text: "Englisch",
    },
    {
      icon: "sunny",
      value: IT,
      text: "Italienisch",
    },
  ];

  let buttonAction = undefined;

  let gotoAction = undefined;

  let iconAction = undefined;

  let chipAction = undefined;

  let checkboxValue = {
    status: undefined,
  };

  let groupboxValue = {
    allQuelle: ["JIRA"],
    allSprache: [EN],
  };

  let radioboxValue = {
    quelle: "JIRA",
    sprache: EN,
  };

  let dialogVisible = false;

  let selectValue = {
    quelle: "JIRA",
    sprache: EN,
  };

  let tagsValue = {
    allQuelle: ["JIRA"],
    allSprache: [EN],
  };

  let textAreaValue = {
    text: [
      "Lorem ipsum dolor sit amet.",
      "sed diam voluptua.",
      "Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.",
      "Sed diam voluptua.",
      "At vero eos et accusam.",
      "Stet clita kasd gubergren.",
    ].join("\n"),
  };

  let textFieldValue = {
    text: undefined,
    date: undefined,
    time: undefined,
    number: undefined,
  };

  function onButtonSuccess() {
    dialogVisible = false;
  }

  function onButtonFailure() {
    try {
      dialogVisible = false;
      throw new Error(textAreaValue.text);
    } catch (err) {
      toast.push(err.toString());
    }
  }
</script>

<Dialog bind:value={dialogVisible}>
  <h5 slot="title">{textFieldValue.name}</h5>
  <div class="w-96">
    <span>{textAreaValue.text}</span>
  </div>
  <div slot="actions">
    <Button on:click={onButtonSuccess}>Quit</Button>
    <Button on:click={onButtonFailure}>Fail</Button>
  </div>
</Dialog>

<h1>Galerie</h1>
<div class="flex flex-col ml-2 mr-2 space-y-2">
  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">Dialog</legend>
    <div class="flex flex-row gap-1 items-center">
      <Button title="Dialog aufrufen." on:click={() => (dialogVisible = true)}
        >Dialog</Button
      >
      <Icon title="Dialog aufrufen." on:click={() => (dialogVisible = true)} />
      <Chip title="Dialog aufrufen." on:click={() => (dialogVisible = true)}
        >Dialog</Chip
      >
      <Checkbox
        title="Dialog aufrufen."
        bind:checked={dialogVisible}
        label="Dialog"
      />
    </div>
  </fieldset>

  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">Button</legend>
    <div class="flex flex-col gap-1">
      <div
        class="grid grid-cols-2 md:grid-cols-8 justify-center items-center justify-items-left gap-1"
      >
        <Button title="Klick mich" on:click={() => (buttonAction = "Default")}
          >Default</Button
        >
        <Button
          title="Klick mich"
          on:click={() => (buttonAction = "Disabled")}
          disabled>Default</Button
        >
      </div>
      <div
        class="grid grid-cols-2 md:grid-cols-8 justify-center items-center justify-items-left gap-1"
      >
        <Button
          title="Klick mich"
          on:click={() => (buttonAction = "Outlined Default")}
          outlined>Default</Button
        >
        <Button
          title="Klick mich"
          on:click={() => (buttonAction = "Outlined Disabled")}
          outlined
          disabled>Default</Button
        >
      </div>
    </div>
    <pre class="max-w-0" class:line-through={!buttonAction}>{buttonAction}</pre>
  </fieldset>

  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">Goto</legend>
    <div class="flex flex-col gap-1">
      <div
        class="grid grid-cols-2 md:grid-cols-8 justify-center items-center justify-items-left gap-1"
      >
        <Goto
          title="Klick mich"
          on:click={() => (gotoAction = "Home")}
          page="/"
          target="_blank">Home</Goto
        >
        <Goto
          title="Klick mich"
          on:click={() => (gotoAction = "Home")}
          page="/"
          target="_blank"
          disabled>Home</Goto
        >
      </div>
      <div
        class="grid grid-cols-2 md:grid-cols-8 justify-center items-center justify-items-left gap-1"
      >
        <Goto
          title="Klick mich"
          on:click={() => (gotoAction = "Outlined Home")}
          outlined
          page="/"
          target="_blank">Home</Goto
        >
        <Goto
          title="Klick mich"
          on:click={() => (gotoAction = "Outlined Home")}
          outlined
          page="/"
          target="_blank"
          disabled>Home</Goto
        >
      </div>
    </div>
    <pre class="max-w-0" class:line-through={!gotoAction}>{gotoAction}</pre>
  </fieldset>

  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">Icon</legend>
    <div class="flex flex-col gap-1">
      <div
        class="grid grid-cols-4 md:grid-cols-12 justify-center items-center justify-items-center gap-1"
      >
        <Icon
          title="Klick mich"
          on:click={() => (iconAction = "Default Icon")}
        />
        <Icon
          title="Klick mich"
          on:click={() => (iconAction = "Default Icon")}
          disabled
        />
        {#each ["Build", "Edit", "Delete", "Link", "Lock", "Login", "Logout", "Print", "Schedule", "Search"] as name}
          <Icon
            title="Klick mich"
            on:click={() => (iconAction = name + " Icon")}
            name={name.toLowerCase()}
          />
        {/each}
      </div>
      <div
        class="grid grid-cols-4 md:grid-cols-12 justify-center items-center justify-items-center gap-1"
      >
        <Icon
          title="Klick mich"
          on:click={() => (iconAction = "Outlined Default Icon")}
          outlined
        />
        <Icon
          title="Klick mich"
          on:click={() => (iconAction = "Outlined Default Icon")}
          outlined
          disabled
        />
        {#each ["Build", "Edit", "Delete", "Link", "Lock", "Login", "Logout", "Print", "Schedule", "Search"] as name}
          <Icon
            title="Klick mich"
            on:click={() => (iconAction = "Outlined " + name + " Icon")}
            name={name.toLowerCase()}
            outlined
          />
        {/each}
      </div>
    </div>
    <pre class="max-w-0" class:line-through={!iconAction}>{iconAction}</pre>
  </fieldset>

  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">Chip</legend>
    <div class="flex flex-col gap-1">
      <div
        class="grid grid-cols-2 md:grid-cols-8 justify-center items-center justify-items-left gap-1"
      >
        <Chip title="Klick mich" on:click={() => (chipAction = "Chip")}
          >Chip</Chip
        >
        <Chip title="Klick mich" on:click={() => (chipAction = "Chip")} disabled
          >Chip</Chip
        >
      </div>
    </div>
    <div class="flex flex-col gap-1">
      <div
        class="grid grid-cols-2 md:grid-cols-8 justify-center items-center justify-items-left gap-1"
      >
        <Chip
          title="Klick mich"
          on:click={() => (chipAction = "Outlined Chip")}
          outlined>Chip</Chip
        >
        <Chip
          title="Klick mich"
          on:click={() => (chipAction = "Outlined Chip")}
          outlined
          disabled>Chip</Chip
        >
      </div>
    </div>
    <pre class="max-w-0" class:line-through={!chipAction}>{chipAction}</pre>
  </fieldset>

  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">Checkbox</legend>
    <div class="flex flex-col md:flex-row gap-1">
      <div class="w-full md:w-1/2 border-2">
        <Checkbox bind:checked={checkboxValue.status} title="Status wählen" />
        <Checkbox
          bind:checked={checkboxValue.status}
          title="Status wählen"
          label="Status"
        />
      </div>
    </div>
    <div class="flex flex-col md:flex-row gap-1">
      <div class="w-full md:w-1/2 border-2">
        <Checkbox
          bind:checked={checkboxValue.status}
          title="Status wählen"
          disabled
        />
        <Checkbox
          bind:checked={checkboxValue.status}
          title="Status wählen"
          label="Status"
          disabled
        />
      </div>
    </div>
    <details>
      <summary>JSON</summary>
      <pre class="max-w-0">{JSON.stringify(checkboxValue, null, 2)}</pre>
    </details>
  </fieldset>

  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">Groupbox</legend>
    <div class="flex flex-col md:flex-row gap-1">
      <div class="w-full md:w-1/2 border-2">
        <Groupbox
          bind:group={groupboxValue.allQuelle}
          allItem={allQuelleItem}
          title="Quelle wählen"
        />
      </div>
      <div class="w-full md:w-1/2 border-2">
        <Groupbox
          bind:group={groupboxValue.allSprache}
          allItem={allSpracheItem}
          title="Sprache wählen"
        />
      </div>
    </div>
    <div class="flex flex-col md:flex-row gap-1">
      <div class="w-full md:w-1/2 border-2">
        <Groupbox
          bind:group={groupboxValue.allQuelle}
          allItem={allQuelleItem}
          disabled
        />
      </div>
      <div class="w-full md:w-1/2 border-2">
        <Groupbox
          bind:group={groupboxValue.allSprache}
          allItem={allSpracheItem}
          disabled
        />
      </div>
    </div>
    <details>
      <summary>JSON</summary>
      <pre class="max-w-0">{JSON.stringify(groupboxValue, null, 2)}</pre>
    </details>
  </fieldset>

  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">Radiobox</legend>
    <div class="flex flex-col md:flex-row gap-1">
      <div class="w-full md:w-1/2 border-2">
        <Radiobox
          bind:group={radioboxValue.quelle}
          allItem={allQuelleItem}
          title="Quelle wählen"
        />
      </div>
      <div class="w-full md:w-1/2 border-2">
        <Radiobox
          bind:group={radioboxValue.sprache}
          allItem={allSpracheItem}
          title="Sprache wählen"
        />
      </div>
    </div>
    <div class="flex flex-col md:flex-row gap-1">
      <div class="w-full md:w-1/2 border-2">
        <Radiobox
          bind:group={radioboxValue.quelle}
          allItem={allQuelleItem}
          disabled
        />
      </div>
      <div class="w-full md:w-1/2 border-2">
        <Radiobox
          bind:group={radioboxValue.sprache}
          allItem={allSpracheItem}
          disabled
        />
      </div>
    </div>
    <details>
      <summary>JSON</summary>
      <pre class="max-w-0">{JSON.stringify(radioboxValue, null, 2)}</pre>
    </details>
  </fieldset>

  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">Select</legend>
    <div class="flex flex-col md:flex-row gap-1 items-baseline">
      <div class="w-full md:w-1/2">
        <Select
          bind:value={selectValue.quelle}
          allItem={allQuelleItem}
          title="Wähle mich"
          label="Quelle"
          placeholder="Bitte hier eine Quelle wählen"
        />
      </div>
      <div class="w-full md:w-1/2">
        <Select
          bind:value={selectValue.sprache}
          bind:valueItem={selectValue.spracheItem}
          allItem={allSpracheItem}
          title="Wähle mich"
          label="Sprache"
          placeholder="Bitte hier eine Sprache wählen"
        />
      </div>
    </div>
    <div class="flex flex-col md:flex-row gap-1 items-baseline">
      <div class="w-full md:w-1/2">
        <Select
          bind:value={selectValue.quelle}
          allItem={allQuelleItem}
          disabled
        />
      </div>
      <div class="w-full md:w-1/2">
        <Select
          bind:value={selectValue.sprache}
          bind:valueItem={selectValue.spracheItem}
          allItem={allSpracheItem}
          disabled
        />
      </div>
    </div>
    <details>
      <summary>JSON</summary>
      <pre class="max-w-0">{JSON.stringify(selectValue, null, 2)}</pre>
    </details>
  </fieldset>

  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">Toggle</legend>
    <div class="flex flex-col md:flex-row gap-1 items-baseline">
      <div class="w-full md:w-1/2">
        <Toggle
          bind:allValue={tagsValue.allQuelle}
          allItem={allQuelleItem}
          title="Wähle mich"
          label="Quelle"
          placeholder="Bitte hier Quellen wählen"
        />
      </div>
      <div class="w-full md:w-1/2">
        <Toggle
          bind:allValue={tagsValue.allSprache}
          allItem={allSpracheItem}
          title="Wähle mich"
          label="Sprache"
          placeholder="Bitte hier Sprachen wählen"
        />
      </div>
    </div>
    <div class="flex flex-col md:flex-row gap-1 items-baseline">
      <div class="w-full md:w-1/2">
        <Toggle
          bind:allValue={tagsValue.allQuelle}
          allItem={allQuelleItem}
          disabled
        />
      </div>
      <div class="w-full md:w-1/2">
        <Toggle
          bind:allValue={tagsValue.allSprache}
          allItem={allSpracheItem}
          disabled
        />
      </div>
    </div>
    <details>
      <summary>JSON</summary>
      <pre class="max-w-0">{JSON.stringify(tagsValue, null, 2)}</pre>
    </details>
  </fieldset>

  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">TextArea</legend>
    <div class="flex flex-col md:flex-row gap-1 items-baseline">
      <div class="w-full md:w-1/2">
        <TextArea
          bind:value={textAreaValue.text}
          rows="5"
          label="Text"
          placeholder="Bitte einen Text eingeben"
        />
      </div>
    </div>
    <div class="flex flex-col md:flex-row gap-1 items-baseline">
      <div class="w-full md:w-1/2">
        <TextArea bind:value={textAreaValue.text} rows="5" disabled />
      </div>
    </div>
    <details>
      <summary>JSON</summary>
      <pre class="max-w-0">{JSON.stringify(textAreaValue, null, 2)}</pre>
    </details>
  </fieldset>

  <fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">TextField</legend>
    <div class="flex flex-col md:flex-row gap-1 items-baseline">
      <div class="w-full md:w-1/2">
        <div class="flex flex-col">
          <TextField
            bind:value={textFieldValue.text}
            type="text"
            label="Text"
            placeholder="Bitte einen Text eingeben"
          />
          <TextField
            bind:value={textFieldValue.text}
            type="password"
            label="Text"
            placeholder="Bitte einen Text eingeben"
          />
        </div>
      </div>
      <div class="w-48 md:w-1/6">
        <TextField
          bind:value={textFieldValue.date}
          type="date"
          label="Datum"
          placeholder="Bitte ein Datum eingeben"
        />
      </div>
      <div class="w-48 md:w-1/6">
        <TextField
          bind:value={textFieldValue.time}
          type="time"
          label="Zeit"
          placeholder="Bitte eine Zeit eingeben"
        />
      </div>
      <div class="w-48 md:w-1/6">
        <TextField
          bind:value={textFieldValue.number}
          type="number"
          step="1"
          min="0"
          max="720"
          label="Zahl"
          placeholder="Bitte eine Zahl eingeben"
        />
      </div>
    </div>
    <div class="flex flex-col md:flex-row gap-1 items-baseline">
      <div class="w-full md:w-1/2">
        <TextField bind:value={textFieldValue.text} disabled />
      </div>
      <div class="w-48 md:w-1/6">
        <TextField bind:value={textFieldValue.date} type="date" disabled />
      </div>
      <div class="w-48 md:w-1/6">
        <TextField bind:value={textFieldValue.time} type="time" disabled />
      </div>
      <div class="w-48 md:w-1/6">
        <TextField
          bind:value={textFieldValue.number}
          type="number"
          step="1"
          min="0"
          max="720"
          disabled
        />
      </div>
    </div>
    <details>
      <summary>JSON</summary>
      <pre class="max-w-0">{JSON.stringify(textFieldValue, null, 2)}</pre>
    </details>
  </fieldset>
</div>

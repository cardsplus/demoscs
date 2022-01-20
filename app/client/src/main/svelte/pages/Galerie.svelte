<script>
	import Button from '../components/Button';
	import Checkbox from '../components/Checkbox';
	import CheckboxGroup from '../components/CheckboxGroup';
	import Chip from '../components/Chip';
	import Dialog from '../components/Dialog';
	import Goto from '../components/Goto';
	import Icon from '../components/Icon';
	import Select from '../components/Select';
	import TextArea from '../components/TextArea';
	import TextField from '../components/TextField';
	import { toast } from '../components/Toast';

    let allSpracheItem = [{
        icon: 'bolt',
        value: 'DE',
        text: 'Deutsch'
    },
    {
        icon: 'cloud',
        value: 'EN',
        text: 'Englisch'
    },
    {
        icon: 'sunny',
        value: 'IT',
        text: 'Italienisch'
    }];

    let buttonAction = undefined;

    let gotoAction = undefined;

    let iconAction = undefined;

    let chipAction = undefined;

    let checkboxValue = {
        status: undefined,
        allSprache: ['EN']
    };

    let dialogVisible = false;

    let selectValue = {
        sprache: 'EN'
    }

    let textAreaValue = {
        text: [
            'Lorem ipsum dolor sit amet.',
            'sed diam voluptua.',
            'Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.',
            'Sed diam voluptua.',
            'At vero eos et accusam.',
            'Stet clita kasd gubergren.'
        ].join('\n')
    }

    let textFieldValue = {
        text: undefined,
        date: undefined,
        time: undefined,
        number: undefined
    }

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
		<legend class="text-xs">Button</legend>
        <div class="flex flex-col gap-1">
            <div class="grid grid-cols-2 md:grid-cols-8 justify-center items-center justify-items-left gap-1">
                <Button on:click={() => buttonAction = 'Default'}>Default</Button>
                <Button on:click={() => buttonAction = 'Disabled'} disabled>Default</Button>
            </div>
            <div class="grid grid-cols-2 md:grid-cols-8 justify-center items-center justify-items-left gap-1">
                <Button on:click={() => buttonAction = 'Outlined Default'} outlined>Default</Button>
                <Button on:click={() => buttonAction = 'Outlined Disabled'} outlined disabled>Default</Button>
            </div>
        </div>
        <pre class="max-w-0" class:line-through={!buttonAction}>{buttonAction}</pre>
	</fieldset>

	<fieldset class="p-4 border-2 space-y-2">
		<legend class="text-xs">Goto</legend>
        <div class="flex flex-col gap-1">
            <div class="grid grid-cols-2 md:grid-cols-8 justify-center items-center justify-items-left gap-1">
                <Goto on:click={() => gotoAction = 'Home'} page="/" target="_blank">Home</Goto>
                <Goto on:click={() => gotoAction = 'Home'} page="/" target="_blank" disabled>Home</Goto>
            </div>
            <div class="grid grid-cols-2 md:grid-cols-8 justify-center items-center justify-items-left gap-1">
                <Goto on:click={() => gotoAction = 'Outlined Home'} outlined page="/" target="_blank">Home</Goto>
                <Goto on:click={() => gotoAction = 'Outlined Home'} outlined page="/" target="_blank" disabled>Home</Goto>
            </div>
        </div>
        <pre class="max-w-0" class:line-through={!gotoAction}>{gotoAction}</pre>
	</fieldset>
    
	<fieldset class="p-4 border-2 space-y-2">
		<legend class="text-xs">Icon</legend>
        <div class="flex flex-col gap-1">
            <div class="grid grid-cols-4 md:grid-cols-12 justify-center items-center justify-items-center gap-1">
                <Icon on:click={() => iconAction = "Default Icon"}/>
                <Icon on:click={() => iconAction = "Default Icon"} disabled/>
                {#each ['Build', 'Edit', 'Delete', 'Link', 'Lock', 'Login', 'Logout', 'Print', 'Schedule', 'Search'] as name}
                <Icon on:click={() => iconAction = name + " Icon"} name={name.toLowerCase()}/>
                {/each}
            </div>
            <div class="grid grid-cols-4 md:grid-cols-12 justify-center items-center justify-items-center gap-1">
                <Icon on:click={() => iconAction = "Outlined Default Icon"} outlined/>
                <Icon on:click={() => iconAction = "Outlined Default Icon"} outlined disabled/>
                {#each ['Build', 'Edit', 'Delete', 'Link', 'Lock', 'Login', 'Logout', 'Print', 'Schedule', 'Search'] as name}
                <Icon on:click={() => iconAction = "Outlined " + name + " Icon"} name={name.toLowerCase()} outlined/>
                {/each}
            </div>
        </div>
        <pre class="max-w-0" class:line-through={!iconAction}>{iconAction}</pre>
	</fieldset>
    
	<fieldset class="p-4 border-2 space-y-2">
		<legend class="text-xs">Chip</legend>
        <div class="flex flex-col gap-1">
            <div class="grid grid-cols-2 md:grid-cols-8 justify-center items-center justify-items-left gap-1">
                <Chip on:click={() => chipAction = 'Chip'}>Chip</Chip>
                <Chip on:click={() => chipAction = 'Chip'} disabled>Chip</Chip>
            </div>
        </div>
        <div class="flex flex-col gap-1">
            <div class="grid grid-cols-2 md:grid-cols-8 justify-center items-center justify-items-left gap-1">
                <Chip on:click={() => chipAction = 'Outlined Chip'} outlined>Chip</Chip>
                <Chip on:click={() => chipAction = 'Outlined Chip'} outlined disabled>Chip</Chip>
            </div>
        </div>
        <pre class="max-w-0" class:line-through={!chipAction}>{chipAction}</pre>
	</fieldset>

	<fieldset class="p-4 border-2 space-y-2">
		<legend class="text-xs">Checkbox</legend>
        <div class="flex flex-col md:flex-row gap-1">
            <div class="w-full md:w-1/3 border-2">
                <Checkbox 
                    bind:checked={checkboxValue.status}/>
                <Checkbox 
                    bind:checked={checkboxValue.status} 
                    label="Status"/>
                <Checkbox 
                    bind:checked={checkboxValue.status} 
                    label="Status"
                    disabled/>
            </div>
            <div class="w-full md:w-1/3 border-2">
                {#each allSpracheItem as item}
                <CheckboxGroup 
                    bind:group={checkboxValue.allSprache} 
                    value={item.value}
                    label={item.text}/>
                {/each}
            </div>
            <div class="w-full md:w-1/3 border-2">
                {#each allSpracheItem as item}
                <CheckboxGroup 
                    bind:group={checkboxValue.allSprache} 
                    value={item.value}
                    label={item.text}
                    disabled/>
                {/each}
            </div>
        </div>
        <pre class="max-w-0">{JSON.stringify(checkboxValue)}</pre>
	</fieldset>
    
	<fieldset class="p-4 border-2 space-y-2">
    <legend class="text-xs">Dialog</legend>
        <div class="flex flex-row gap-1 items-center">
            <Button title="Dialog aufrufen." on:click={() => dialogVisible = true}>Dialog</Button>
            <Icon title="Dialog aufrufen." on:click={() => dialogVisible = true}/>
            <Chip title="Dialog aufrufen." on:click={() => dialogVisible = true}>Dialog</Chip>
            <Checkbox title="Dialog aufrufen." bind:checked={dialogVisible}/>
        </div>
	</fieldset>

	<fieldset class="p-4 border-2 space-y-2">
		<legend class="text-xs">Select</legend>
        <div class="flex flex-col md:flex-row gap-1 items-baseline">
            <div class="w-full md:w-1/2">
                <Select bind:value={selectValue.sprache}
                    items={allSpracheItem} 
                    label="Sprache"
                    placeholder="Bitte hier eine Sprache wählen"/>
            </div>
        </div>
        <div class="flex flex-col md:flex-row gap-1 items-baseline">
            <div class="w-full md:w-1/2">
                <Select bind:value={selectValue.sprache}
                    items={allSpracheItem} 
                    label="Sprache"
                    disabled/>
            </div>
        </div>
        <pre class="max-w-0">{JSON.stringify(selectValue)}</pre>
	</fieldset>

	<fieldset class="p-4 border-2 space-y-2">
		<legend class="text-xs">TextArea</legend>
        <div class="flex flex-col md:flex-row gap-1 items-baseline">
            <div class="w-full md:w-1/2">
                <TextArea bind:value={textAreaValue.text}
                    rows=5
                    label="Text"
                    placeholder="Bitte einen Text eingeben"/>
            </div>
        </div>
        <div class="flex flex-col md:flex-row gap-1 items-baseline">
            <div class="w-full md:w-1/2">
                <TextArea bind:value={textAreaValue.text}
                    rows=5
                    label="Text"
                    disabled/>
            </div>
        </div>
        <pre class="max-w-0">{JSON.stringify(textAreaValue)}</pre>
	</fieldset>

	<fieldset class="p-4 border-2 space-y-2">
		<legend class="text-xs">TextField</legend>
        <div class="flex flex-col md:flex-row gap-1 items-baseline">
            <div class="w-full md:w-1/2">
                <div class="flex flex-col">
                    <TextField bind:value={textFieldValue.text}
                        type=text
                        label="Text"		
                        placeholder="Bitte einen Text eingeben"/>
                    <TextField bind:value={textFieldValue.text}
                        type=password
                        label="Text"		
                        placeholder="Bitte einen Text eingeben"/>
                </div>
            </div>
            <div class="w-48 md:w-1/6">
                <TextField bind:value={textFieldValue.date}
                    type=date
                    label="Datum"		
                    placeholder="Bitte ein Datum eingeben"/>
            </div>
            <div class="w-48 md:w-1/6">
                <TextField bind:value={textFieldValue.time}
                    type=time
                    label="Zeit"		
                    placeholder="Bitte eine Zeit eingeben"/>
            </div>
            <div class="w-48 md:w-1/6">
                <TextField bind:value={textFieldValue.number}
                    type=number
                    step=1
                    min=0
                    max=720
                    label="Zahl"		
                    placeholder="Bitte eine Zahl eingeben"/>
            </div>
        </div>
        <div class="flex flex-col md:flex-row gap-1 items-baseline">
            <div class="w-full md:w-1/2">
                <TextField bind:value={textFieldValue.text} 
                    label="Name"		
                    disabled/>
            </div>
            <div class="w-48 md:w-1/6">
                <TextField bind:value={textFieldValue.date}
                    type=date
                    label="Datum"		
                    disabled/>
            </div>
            <div class="w-48 md:w-1/6">
                <TextField bind:value={textFieldValue.time}
                    type=time
                    label="Zeit"		
                    disabled/>
            </div>
            <div class="w-48 md:w-1/6">
                <TextField bind:value={textFieldValue.number}
                    type=number
                    step=1
                    min=0
                    max=720
                    label="Zahl"				
                    disabled/>
            </div>
        </div>
        <pre class="max-w-0">{JSON.stringify(textFieldValue)}</pre>
	</fieldset>
</div>

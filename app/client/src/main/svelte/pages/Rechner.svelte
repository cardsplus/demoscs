<script>
	import Button from '../components/Button';
	let total = 0;
	let console = "";
	let state = null;
	function resolveState(){
		switch(state){
			case "add":
				total += parseFloat(console);
				console = "0";
				break;
			case "substract":
				total -= parseFloat(console);
				console = "0";
				break;
			case "multiply":
				total *= parseFloat(console);
				console = "0";
				break;
			case "divide":
				total /= parseFloat(console);
				console = "0";
				break;
			default:
				total = parseFloat(console);
				console = "0";
				break;
		}
	}
	function setOperation(operation){
		resolveState();
		state = operation;
	}
	function setValue(value){
		if(console.toString() == "0" || state == "equal"){
			console = "";
		}
		if(state == "equal"){
			state = null;
		}
		if(value == "C"){
			total = 0;
			state = null;
			console = "";
		} else {
			console = console + value;
		}
	}
	function equal(){
		resolveState();
		console = total;
		state = "equal";
	}
</script>

<h1>Rechner</h1>
<h2>Einfach</h2>
<div class="w-96 ml-2 mr-2 gap-1">
	<pre class="text-2xl text-right">
		{console ? console : "_"}
	</pre>
	<div class="grid grid-cols-4 gap-1">
		<Button outlined on:click={()=>{setOperation('add');}}>
			+
		</Button>
		<Button outlined on:click={()=>{setOperation('substract');}}>
			-
		</Button>
		<Button outlined on:click={()=>{setOperation('multiply');}}>
			&times;
		</Button>
		<Button outlined on:click={()=>{setOperation('divide');}}>
			&divide;
		</Button>
		<Button on:click={()=>{setValue(7);}}>
			7
		</Button>
		<Button on:click={()=>{setValue(8);}}>
			8
		</Button>
		<Button on:click={()=>{setValue(9);}}>
			9
		</Button>
		<Button on:click={()=>{setValue(4);}}>
			4
		</Button>
		<Button on:click={()=>{setValue(5);}}>
			5
		</Button>
		<Button on:click={()=>{setValue(6);}}>
			6
		</Button>
		<Button on:click={()=>{setValue(1);}}>
			1
		</Button>
		<Button on:click={()=>{setValue(2);}}>
			2
		</Button>
		<Button on:click={()=>{setValue(3);}}>
			3
		</Button>
		<Button on:click={()=>{setValue(0);}}>
			0
		</Button>
		<Button on:click={()=>{setValue('.');}}>
			.
		</Button>
		<Button outlined on:click={()=>{setValue('C');}}>
			C
		</Button>
		<div class="calculator-eq ">
			<Button outlined on:click={equal}>
				=
			</Button>
		</div>
	</div>
</div>

<style>
	.calculator-eq {
		display: grid;
		grid-area: 2 / 4 / 6 / 5;
	}
</style>

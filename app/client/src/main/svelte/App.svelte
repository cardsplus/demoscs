<script>
	import "smelte/src/tailwind.css";
	import AppHelp from './AppHelp.svelte';
	import AppInfo from './AppInfo.svelte';
    import AppLogo from './AppLogo.svelte'
	import AppMenu from './AppMenu.svelte'
	import Router from './pager/Router.svelte';
  	import Route from './pager/Route.svelte';
  	import RouteNotFound from './pager/RouteNotFound.svelte';	
	import Nutzer from './pages/Nutzer.svelte';
	import Projekt from './pages/Projekt.svelte';
	import Aufgabe from './pages/Aufgabe.svelte';
	import Rechner from './pages/Rechner.svelte';
	import Uhrzeit from './pages/Uhrzeit.svelte';
	let open = false;
</script>

<style>
	aside {
		left: -100%;
		transition: left 0.3s ease-in-out;
		z-index: 99;
	}
	.open {
		left: 0
	}
</style>

<header class="flex justify-between bg-gray-200 p-2 border-b-2 z-50 sticky top-0">
	<nav class="flex text-lg text-gray-600 space-x-2 items-left">
        <AppMenu bind:open={open}/>
        <AppLogo bind:open={open}/>
	</nav>
	<nav class="flex text-lg text-gray-600 space-x-2 items-right">
		<a on:click={() => open = false} href="/help">?</a>
	</nav>
</header>

<aside class="absolute w-full h-full bg-gray-200 border-r-2 shadow-lg" class:open>
	<nav class="p-12 text-xl">
		<div class="w-1/2 rounded px-6">
			<div class="border-l-4 border-red-400 -ml-6 pl-6 flex items-center justify-between my-4">
				<div class="font-semibold text-gray-800 text-xl">Anwendung</div>
			</div>
			<div class="flex items-center my-4">
				<div class="flex-1 pl-2">
					<div class="text-gray-600 font-thin">
						<a on:click={() => open = false} href="/nutzer">Nutzer</a>
					</div>
				</div>
			</div>
			<div class="flex items-center justify-between my-4">
				<div class="flex-1 pl-2">
					<div class="text-gray-600 font-thin">
						<a on:click={() => open = false} href="/projekt">Projekt</a>
					</div>
				</div>
			</div>
			<div class="flex items-center justify-between my-4">
				<div class="flex-1 pl-2">
					<div class="text-gray-600 font-thin">
						<a on:click={() => open = false} href="/aufgabe">Aufgabe</a>
					</div>
				</div>
			</div>
		</div>
		<div class="w-1/2 rounded px-6">
			<div class="border-l-4 border-red-400 -ml-6 pl-6 flex items-center justify-between my-4">
				<div class="font-semibold text-gray-800 text-xl">Experimente</div>
			</div>
			<div class="flex items-center justify-between my-4">
				<div class="flex-1 pl-2">
					<div class="text-gray-600 font-thin">
						<a on:click={() => open = false} href="/uhrzeit">Uhrzeit</a>
					</div>
				</div>
			</div>
			<div class="flex items-center justify-between my-4">
				<div class="flex-1 pl-2">
					<div class="text-gray-600 font-thin">
						<a on:click={() => open = false} href="/rechner">Rechner</a>
					</div>
				</div>
			</div>
		</div>
	</nav>
</aside>

<Router>
	<Route path="/" component="{AppInfo}" />
	<Route path="/help" component="{AppHelp}" />
	<Route path="/nutzer" component="{Nutzer}" />
	<Route path="/projekt" component="{Projekt}" />
	<Route path="/aufgabe" component="{Aufgabe}" />
	<Route path="/uhrzeit" component="{Uhrzeit}" />
	<Route path="/rechner" component="{Rechner}" />
	<RouteNotFound>
		<h1>Ups!</h1>
	</RouteNotFound>
</Router>

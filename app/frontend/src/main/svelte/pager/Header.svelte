<script>
    import HeaderLogo from './HeaderLogo.svelte'    
	import HeaderMenu from './HeaderMenu.svelte'
	export let sidebar = false
</script>

<header class="flex justify-between bg-gray-200 p-2 items-center text-gray-600 border-b-2 z-50 sticky top-0">
	<nav class="flex">
        <HeaderMenu bind:open={sidebar}/>
		<HeaderLogo/>	
	</nav>
</header>

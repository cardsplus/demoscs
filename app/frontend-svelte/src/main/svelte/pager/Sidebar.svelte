<script>
	export let open = false
</script>

<aside class="absolute w-full h-full bg-gray-200 border-r-2 shadow-lg" class:open>
	<nav class="p-12 text-xl">
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
		</div>
	</nav>
</aside>

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
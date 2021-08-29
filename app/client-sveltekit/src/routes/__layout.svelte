<script>
	import 'smelte/src/tailwind.css';
	import '../app.css';
</script>

<header>
	<div class="corner">
		<a sveltekit:prefetch href="/">Home</a>
	</div>

	<nav>
		<ul>
			<li><a sveltekit:prefetch href="/">Home</a></li>
		</ul>
	</nav>

	<div class="corner">
		<a sveltekit:prefetch href="/">Home</a>
	</div>
</header>

<main>
	<slot />
</main>

<footer>
	<a sveltekit:prefetch href="/">Home</a>
</footer>

<style>
	header {
		font-weight: bold;
		display: flex;
		justify-content: space-between;
	}

	.corner {
		width: 3em;
		height: 3em;
	}

	.corner a {
		display: flex;
		align-items: center;
		justify-content: center;
		width: 100%;
		height: 100%;
	}

	nav {
		display: flex;
		justify-content: center;
		--background: rgba(255, 255, 255, 0.7);
	}

	main {
		flex: 1;
		display: flex;
		flex-direction: column;
		padding: 1rem;
		width: 100%;
		max-width: 1024px;
		margin: 0 auto;
		box-sizing: border-box;
	}

	footer {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		padding: 40px;
	}

	footer a {
		font-weight: bold;
	}

	@media (min-width: 480px) {
		footer {
			padding: 40px 0;
		}
	}
</style>

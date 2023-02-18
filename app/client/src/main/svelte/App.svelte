<script>
  import "./index.css";
  import Menu from "./components/Menu";
  import Toast from "./components/Toast";
  import Router from "./pager/Router.svelte";
  import Route from "./pager/Route.svelte";
  import RouteNotFound from "./pager/RouteNotFound.svelte";
  import AppHelp from "./AppHelp.svelte";
  import AppHome from "./AppHome.svelte";
  import AppLogo from "./AppLogo.svelte";
  import AppMenu from "./AppMenu.svelte";
  import Aufgabe from "./pages/Aufgabe.svelte";
  import Enum from "./pages/Enum.svelte";
  import Nutzer from "./pages/Nutzer.svelte";
  import NutzerViewer from "./pages/NutzerViewer.svelte";
  import Projekt from "./pages/Projekt.svelte";
  import Galerie from "./pages/Galerie.svelte";
  import Uhrzeit from "./pages/Uhrzeit.svelte";
  import Rechner from "./pages/Rechner.svelte";
  let menuVisible = false;
</script>

<article class="flex flex-col h-screen">
  <header class="flex justify-between bg-gray-200 p-2 h-12">
    <nav class="flex flex-row text-lg text-gray-600 gap-1">
      <AppMenu bind:open={menuVisible} />
      <AppLogo bind:open={menuVisible} />
    </nav>
    <nav class="flex flex-row text-lg text-gray-600 gap-1">
      <a on:click={() => (menuVisible = false)} href="/help">?</a>
    </nav>
  </header>
  <main class="flex-1 overflow-y-auto">
    <Toast />
    <Menu bind:show={menuVisible}>
      <div class="flex flex-col p-2 text-gray-600 gap-1">
        <span class="text-lg text-gray-900 capitalize">Anwendung</span>
        <div class="flex flex-col p-4 text-gray-600 gap-1">
          <a on:click={() => (menuVisible = false)} href="/nutzer">Nutzer</a>
          <a on:click={() => (menuVisible = false)} href="/projekt">Projekt</a>
          <a on:click={() => (menuVisible = false)} href="/aufgabe">Aufgabe</a>
        </div>
      </div>
      <div class="flex flex-col p-2 text-gray-600 gap-1">
        <span class="text-lg text-gray-900">Aufzählungen</span>
        <div class="flex flex-col p-4 text-gray-600 gap-1">
          <a on:click={() => (menuVisible = false)} href="/enum/sprache"
            >Sprache</a
          >
        </div>
      </div>
      <div class="flex flex-col p-2 text-gray-600 gap-1">
        <span class="text-lg text-gray-900">Experimente</span>
        <div class="flex flex-col p-4 text-gray-600 gap-1">
          <a on:click={() => (menuVisible = false)} href="/galerie">Galerie</a>
          <a on:click={() => (menuVisible = false)} href="/uhrzeit">Uhrzeit</a>
          <a on:click={() => (menuVisible = false)} href="/rechner">Rechner</a>
        </div>
      </div>
    </Menu>
    <Router>
      <Route path="/" component={AppHome} />
      <Route path="/home" component={AppHome} />
      <Route path="/help" component={AppHelp} />
      <Route path="/nutzer" component={Nutzer} />
      <Route path="/nutzer/:id" component={NutzerViewer} />
      <Route path="/projekt" component={Projekt} />
      <Route path="/aufgabe" component={Aufgabe} />
      <Route path="/enum/sprache" component={Enum} art="sprache" />
      <Route path="/galerie" component={Galerie} />
      <Route path="/uhrzeit" component={Uhrzeit} />
      <Route path="/rechner" component={Rechner} />
      <RouteNotFound>
        <h1>Ups!</h1>
      </RouteNotFound>
    </Router>
  </main>
  <footer class="flex justify-center bg-gray-200 p-2 h-10">
    <nav class="flex flex-row text-sm text-gray-600 gap-1">
      <a on:click={() => (menuVisible = false)} href="/help">Impressum</a>
    </nav>
  </footer>
</article>

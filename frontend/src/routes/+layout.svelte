<script>
    import UserPill from "../components/UserPill.svelte";
    import "../app.postcss";
    import {Navbar, NavBrand, NavHamburger, NavLi, NavUl} from "flowbite-svelte";
    import {page} from "$app/stores";

    export const prerender = true;
    export const trailingSlash = "always";
</script>

<svelte:head>
    <title>PeerRequest</title>
</svelte:head>

<Navbar color="form" let:hidden let:toggle>
    <NavHamburger class1="w-full md:flex md:w-auto md:order-1" on:click={toggle}/>
    <NavBrand href="/">
        <img alt="PeerRequest Logo" class="mr-3 h-6 sm:h-12" src="/img/logo.png"/>
        <span class="self-center whitespace-nowrap text-xl font-semibold dark:text-white">PeerRequest</span>
    </NavBrand>
    <UserPill/>
    <NavUl class="mx-auto" {hidden}>
        <NavLi active={$page.url.pathname === "/"} class="mt-1.5" href="/">Home</NavLi>
        <NavLi active={$page.url.pathname.startsWith("/categories")} class="mt-1.5" href="/categories">Conferences
        </NavLi>
    </NavUl>
</Navbar>

<slot/>

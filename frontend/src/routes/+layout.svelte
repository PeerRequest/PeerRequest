<script>
    export const prerender = true;
    export const trailingSlash = "always";

    import "../app.postcss";
    import {
        Avatar,
        Button,
        Dropdown,
        DropdownItem,
        Navbar,
        NavBrand,
        NavHamburger,
        NavLi,
        NavUl
    } from "flowbite-svelte";
    import {page} from "$app/stores";
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
    <div class="flex items-center lg:order-2">
        <Button class="!p-1 lg:!pr-2" color="primary" id="avatar_with_name" pill>
            <Avatar class="lg:mr-2" src="/img/avatar-4.jpg"/>
            <span class="hidden md:inline">Kaori Chihiro</span>
        </Button>
        <Dropdown inline triggeredBy="#avatar_with_name">
            <div class="px-4 py-2" slot="header">
                <span class="block text-sm text-gray-900 dark:text-white"> Kaori Chihiro </span>
                <span class="block truncate text-sm font-medium"> kaori@peerrequest.com </span>
            </div>
            <DropdownItem>My Papers</DropdownItem>

            <a href="/my-reviews">
                <DropdownItem>
                    My Reviews
                </DropdownItem>
            </a>

            <DropdownItem slot="footer">Sign out</DropdownItem>
        </Dropdown>
    </div>
    <NavUl class="mx-auto" {hidden}>
        <NavLi active={$page.url.pathname === "/"} class="mt-1.5" href="/">Home</NavLi>
        <NavLi active={$page.url.pathname.startsWith("/categories")} class="mt-1.5" href="/categories">Conferences
        </NavLi>
    </NavUl>
</Navbar>

<slot/>
